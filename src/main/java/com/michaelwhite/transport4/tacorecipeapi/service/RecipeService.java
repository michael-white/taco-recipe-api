package com.michaelwhite.transport4.tacorecipeapi.service;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.michaelwhite.transport4.tacorecipeapi.TacoRecipeApiApplication;
import com.michaelwhite.transport4.tacorecipeapi.dao.IngredientRepository;
import com.michaelwhite.transport4.tacorecipeapi.dao.TacoRecipeRepository;
import com.michaelwhite.transport4.tacorecipeapi.model.Ingredient;
import com.michaelwhite.transport4.tacorecipeapi.model.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
//import org.springframework.web.util.UriBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.*;

@Service
@PropertySource({
        "classpath:application.properties"
})
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    protected RestTemplate restTemplate;
    protected RequestEntity requestEntity;
    protected ResponseEntity<Recipe> responseEntity;
    protected ResponseEntity<Ingredient> responseIngredientEntity;
    private Object JSONParser;

    public RecipeService(RestTemplateBuilder restTemplateBuilder) {

        this.restTemplate =restTemplateBuilder.build();
    }

    @Value("${SPOONACULAR_ENDPOINT}")
    private String spoonacularUrl;


    @Value("${SPOONACULAR_API_KEY}")
    private String spoonacularApiKey;

    @Value("${CREATE_RECIPE_CARD}")
    private String spoonacularRecipeCardUrl;


    @Autowired
    private TacoRecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    // lookup in local db
    //    if found , return db version
    // if not found in local db. make api call
    //     then save in local db
    // return null;

    private ResponseEntity<Recipe> make_spoonacular_find_by_id(int id) {
        String url =  "https://api.spoonacular.com/recipes/"+id+"/information";
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("apiKey", spoonacularApiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                 .queryParam("apiKey", spoonacularApiKey);

       ResponseEntity<Recipe> recipeResponseEntity = restTemplate.exchange(builder.buildAndExpand(urlParams).toUri() , HttpMethod.GET,
                requestEntity, Recipe.class);
       if( recipeResponseEntity.getStatusCode() == HttpStatus.OK) {
           Recipe insert_recipe = recipeResponseEntity.getBody();
           recipeRepository.save(insert_recipe);
       }
       return recipeResponseEntity;
    }


    private ResponseEntity<List<Ingredient>> make_spoonacular_get_ingredients(int id) {


        String url = "https://api.spoonacular.com/recipes/" +id+ "/ingredientWidget.json";
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("apiKey", spoonacularApiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("apiKey", spoonacularApiKey);
        ResponseEntity<String> ingredientResponseEntity = restTemplate.exchange(builder.buildAndExpand(urlParams).toUri() , HttpMethod.GET,
                requestEntity,String.class);
        if( ingredientResponseEntity.getStatusCode() == HttpStatus.OK) {
            String ingredientJson = ingredientResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Ingredient> map = mapper.readValue(ingredientJson, Map.class);
                List<Ingredient> ingredientList = new ArrayList<Ingredient>(map.values());

                for(Ingredient ingredient: ingredientList) {
                    ingredientRepository.save(ingredient);
                }

                return new ResponseEntity(ingredientList,HttpStatus.OK);

            } catch(Exception e) {
                logger.debug(e.getMessage());
            }

        }
        return new ResponseEntity("",HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<String> make_spoonacular_post_image(Recipe recipe, List<Ingredient> ingredients)  throws IOException,  MalformedURLException {

        StringBuilder stringBuilder;
       for(Ingredient ingredient : ingredients) {
           String urlString = spoonacularRecipeCardUrl;
           Map<String, String> urlParams = new HashMap<>();
           urlParams.put("apiKey", spoonacularApiKey);
           urlParams.put("title", recipe.getName());
           urlParams.put("ingredients", ingredients.toString());
           urlParams.put("instructions", recipe.getInstructions());


           String imageResultAsJsonStr =
                   restTemplate.postForObject(urlString, requestEntity, String.class, urlParams);


           ObjectMapper mapper = new ObjectMapper();

           Map<String, Ingredient> map = mapper.readValue(imageResultAsJsonStr, Map.class);
           List<Ingredient> ingredientList = new ArrayList<Ingredient>(map.values());

           JsonNode root = mapper.readTree(imageResultAsJsonStr);
           for(JsonNode jsoneNode:  root) {
               String imagerUrl = jsoneNode.get("url").toString();
               ingredient.setImage(imagerUrl);
           }
       }

        return new ResponseEntity<String>("", HttpStatus.OK);

    }


    private ResponseEntity<Recipe> make_spoonacular_save_recipe(Recipe recipe, List<Ingredient> ingredientList) {

        ResponseEntity<List<Ingredient>> ingredientsList = make_spoonacular_get_ingredients(recipe.getId());
        ResponseEntity<Recipe> responseEntity = make_spoonacular_find_by_id(recipe.getId());

        saveRecipe(recipe, ingredientList);

        return responseEntity;

    }

    // @Cacheable(value="recipes")
    public Recipe findById(int id)  throws SQLException {
        if(recipeRepository.existsById(id)) {
           Recipe recipe = recipeRepository.findById(id);
           List<Ingredient> ingredientList = ingredientRepository.getIngredientsWithRecipeId(id);
           recipe.setIngredients(ingredientList);
           return recipe;
        } else {
            responseEntity = make_spoonacular_find_by_id(id);
            make_spoonacular_get_ingredients(id);
            return responseEntity.getBody();
        }
    }


    // lookup in local db
    //      if found update db
    //          update in api
    // lookup not in local db
    //      save in local  db
    //      save in api
 //   @CacheEvict(value = "recipes",allEntries = true)


    public int saveRecipe(Recipe recipe, List<Ingredient> ingredients) {

        int recipe_id = (recipe != null) ? recipe.getId() : -1;
        Recipe a_recipe= recipeRepository.findById(recipe_id);

        try {
            if (a_recipe == null) {
                if(recipe != null) {
                    recipe.setIngredients(ingredients);
                    updateRecipe(recipe);
                } else {
                    make_spoonacular_save_recipe(recipe, ingredients);
                }
                return recipe.getId();
            } else {
                a_recipe.setName(recipe.getName());
                a_recipe.setDescription(recipe.getDescription());
                a_recipe.setInstructions(recipe.getInstructions());
                List<Ingredient> ingredientList = ingredientRepository.getIngredientsWithRecipeId(recipe.getId());

                a_recipe.setIngredients(ingredientList);
                updateRecipe(a_recipe);
                return a_recipe.getId();
            }
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return -1;
    }

    // look for recipe in localdb
    //    if found, update local db,  call update on api db
    // if not found in local db
    //     create in local db, then call save on api db
  //  @CacheEvict(value = "recipes",allEntries = true)
    public Recipe updateRecipe(Recipe recipe) throws SQLException{
        Recipe a_recipe= recipeRepository.findById(recipe.getId());
        List<Ingredient> ingredientList = ingredientRepository.getIngredientsWithRecipeId(recipe.getId());

        if(a_recipe != null) {
            if(ingredientList.size() > 0) {
                a_recipe.setIngredients(ingredientList);
            }
            return a_recipe;
        } else {
            recipe.setIngredients(ingredientList);
            saveRecipe(recipe,ingredientList);
            return recipe;
        }
    }
    // call delete in api to delete
    //   if success
    //         delete from local db
    //   else if recipe not exist in api,   call delete on local db
  //  @CacheEvict(value = "recipes",allEntries = true)
    public long deleteRecipe(Recipe recipe) {
        return 1;
    }



}
