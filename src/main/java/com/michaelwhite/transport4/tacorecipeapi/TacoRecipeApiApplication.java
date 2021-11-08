package com.michaelwhite.transport4.tacorecipeapi;

import com.michaelwhite.transport4.tacorecipeapi.model.Ingredient;
import com.michaelwhite.transport4.tacorecipeapi.model.Recipe;
import com.michaelwhite.transport4.tacorecipeapi.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;

@SpringBootApplication
@RestController
@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class TacoRecipeApiApplication  {

    @Autowired
    private RecipeService recipeService;

   // @Bean(name = "dataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        dataSource.setUrl("jdbc:derby:memory:local");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.DERBY).addScript("data-derby.sql").build();
        return db;

    }

    private static final Logger logger = LoggerFactory.getLogger(TacoRecipeApiApplication.class);

    @GetMapping(value = "/get-recipe/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable int id) throws SQLException  {
        Recipe recipe = new Recipe();

        if ((recipe = recipeService.findById(id)) != null) {
            return new ResponseEntity<>(recipe, HttpStatus.OK);
        }

        return new ResponseEntity<>(recipe, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(value= "/save-recipe")
    public ResponseEntity<Recipe> saveRecipe(Recipe recipe) throws SQLException {

        long recipe_id = recipeService.saveRecipe(recipe, new ArrayList< Ingredient >());

        return null;
    }



    public static void main(String[] args) {
        SpringApplication.run(TacoRecipeApiApplication.class, args);
    }

    /*Creating Taco recipes
Updating Taco recipes
Deleting Taco recipes
Retrieving a list of Taco recipes
Retrieve list of Taco sauces from Spoonacular.com Ingredient API
Post Taco Recipe to Spoonacular.com API and return Recipe Card image

     */




}
