package com.michaelwhite.transport4.tacorecipeapi.dao;

import com.google.common.collect.Lists;
import com.michaelwhite.transport4.tacorecipeapi.TacoRecipeApiApplication;
import com.michaelwhite.transport4.tacorecipeapi.model.Ingredient;
import com.michaelwhite.transport4.tacorecipeapi.model.Recipe;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class TacoRecipeRepository {


    private static final Logger logger = LoggerFactory.getLogger(TacoRecipeRepository.class);

    private Connection conn;
    private Statement  statement;

    @Autowired
    private IngredientRepository ingredientRepository;


    public TacoRecipeRepository() {

        try {
            Class.forName("rg.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection("jdbc:derby:memory:derby_db;create=false", "sa", "");
        }catch(ClassNotFoundException cnfe ) {
            logger.debug(cnfe.getMessage());
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }

    }

    public  Iterable<Recipe> saveAll(Iterable<Recipe> entities)  {
        List<Recipe> result = new ArrayList<>();

        for (Recipe entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    public Recipe findById(int id)  {
        Recipe recipe = null;
        if( id < 0) {
            return recipe;
        }

        try {
           ResultSet resultSet = conn.createStatement().executeQuery("SELECT r FROM Recipe r WHERE r.id = " + id);
           if (resultSet != null) {
               recipe = (Recipe) resultSet.getObject(0);
               List<Ingredient> ingredientList = (List<Ingredient>) conn.createStatement().executeQuery("SELECT i FROM Ingredient i where i.recipe_id=" + recipe.getId());
               if (ingredientList != null)
                   recipe.setIngredients(ingredientList);
               else recipe.setIngredients(new ArrayList<>());
           }
           return recipe;
       } catch(SQLException sqlException) {
           logger.debug(sqlException.getMessage());
       }
        return recipe;
    }


    public boolean existsById(int anInt)  {

        Recipe recipe = findById(anInt);
        if(recipe!=null) {
            return true;
        } else {
            return false;
        }
    }

    public List<Recipe> findAll()  {
        List<Recipe> recipeList = new ArrayList<>();

        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT r FROM Recipe r");
            while(resultSet.next()) {

                List<Ingredient> ingredientList = (List<Ingredient>) conn.createStatement().executeQuery("SELECT i FROM Ingredient i where i.recipe_id=" + (int) resultSet.getInt("id"));
                Recipe recipe = new Recipe(resultSet.getInt("id"),
                                            resultSet.getString("name"),
                                            resultSet.getString("description"),
                                            resultSet.getString("instructions"),
                                            (ingredientList != null ? ingredientList : new ArrayList<>()))
                        ;
                recipeList.add(recipe);
            }

        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return recipeList;
    }

    public Iterable<Recipe> findAllById(Iterable<Integer> ints) {
       Set<Recipe> recipeSet= new HashSet<Recipe>();
        for(int id : ints) {
            Recipe found_recipe = findById(id);
            recipeSet.add(found_recipe);
        }
        return recipeSet;
    }

    public Recipe save(Recipe recipe)  {
        Recipe a_recipe = null;
        try{
            ResultSet resultSet = conn.createStatement().executeQuery("insert into Recipe ('description','instructions','name') values('" + recipe.getDescription() +"','" + recipe.getInstructions() + "', '"  + recipe.getName() + "'");

            while(resultSet.next()) {
                List<Ingredient> ingredientList = (List<Ingredient>) conn.createStatement().executeQuery("SELECT i FROM Ingredient i where i.recipe_id=" + (int) resultSet.getInt("id"));
                a_recipe = new Recipe(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("instructions"),
                        (ingredientList != null ? ingredientList : new ArrayList<>()));
            }
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return a_recipe;
    }

    public void delete(Recipe recipe)  {

        try {
            ResultSet resultSet = conn.createStatement().executeQuery("delete from Recipe r where r.id=" + recipe.getId());
            while (resultSet.next()) {
                ResultSet resultIngredientSet = conn.createStatement().executeQuery("delete from Ingredient i where i.recipe_id=" + recipe.getId());
                while (resultIngredientSet.next()) {

                }
            }
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
    }

    public void deleteAllById(Iterable<? extends Integer> ints) throws SQLException {

        List<Recipe> recipeList = new ArrayList<>();
        for(int id : ints) {
            Recipe recipe = findById(id);
            if(recipe != null) {
                recipeList.add(recipe);
            }
        }

        deleteAll(recipeList);

    }

    public void deleteAll(Iterable<? extends Recipe> entities) throws SQLException{
        for(Recipe recipe : entities) {
             delete(recipe);
        }
    }

    public void deleteAll() throws SQLException{
        List<Recipe> allRecipes = findAll();
        if(allRecipes.size() > 0) {
            deleteAll(allRecipes);
        }
    }

    public void deleteById(Integer id) throws SQLException {
        deleteAllById(List.of(id));
    }

}
