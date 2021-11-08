package com.michaelwhite.transport4.tacorecipeapi.dao;

import com.google.common.collect.Lists;
import com.michaelwhite.transport4.tacorecipeapi.model.Ingredient;
import com.michaelwhite.transport4.tacorecipeapi.model.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class IngredientRepository  { // extends CrudRepository<Ingredient, Long> {

    private static final Logger logger = LoggerFactory.getLogger(IngredientRepository.class);

    private Connection conn;
    private Statement  statement;
    private int recipe_id;

    public IngredientRepository() {

        try {
            Class.forName("rg.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection("jdbc:derby:memory:derby_db;create=false", "sa", "");
        }catch(ClassNotFoundException cnfe ) {
            logger.debug(cnfe.getMessage());
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }

    }

    public IngredientRepository(int recipe_id) {
        IngredientRepository ingredientRepository = new IngredientRepository();
        this.recipe_id = recipe_id;
    }


    public Ingredient findById(int id) {

        Ingredient an_ingredient = null;
        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT i FROM Ingredient i where i.id=" + id );
            while(resultSet.next()) {
                an_ingredient = new Ingredient((int)resultSet.getInt("id"),
                        resultSet.getInt("recipe_id")     ,
                                                          resultSet.getString("name"),
                                                         resultSet.getString("image"),
                                                        resultSet.getString("consistency"),
                                                        resultSet.getString("nameClean"),
                                                        resultSet.getString("original"),
                                                        resultSet.getString("originalString"),
                                                        resultSet.getFloat("amount"),
                                                        resultSet.getString("unit"),
                                                        (ArrayList) resultSet.getArray("meta"),
                                                        (ArrayList) resultSet.getArray("metaInformation"), (ArrayList)  resultSet.getArray("measures"));

            }
            return an_ingredient;
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return an_ingredient;
    }

    public List<Ingredient> findByRecipeId(int recipeId) {
        Ingredient an_ingredient;
        List<Ingredient> ingredientList = null;
        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT i FROM Ingredient i where i.recipe_id =" + recipeId );
            while(resultSet.next()) {
                an_ingredient = new Ingredient((int)resultSet.getInt("id"),
                        resultSet.getInt("recipe_id")     ,
                        resultSet.getString("name"),
                        resultSet.getString("image"),
                        resultSet.getString("consistency"),
                        resultSet.getString("nameClean"),
                        resultSet.getString("original"),
                        resultSet.getString("originalString"),
                        resultSet.getFloat("amount"),
                        resultSet.getString("unit"),
                        (ArrayList) resultSet.getArray("meta"),
                        (ArrayList) resultSet.getArray("metaInformation"), (ArrayList)  resultSet.getArray("measures"));

                ingredientList.add(an_ingredient);
            }
            return ingredientList;
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return ingredientList;
    }

    public List<Ingredient> findAll() {

        Ingredient an_ingredient = null;
        List<Ingredient> ingredientList = null;
        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT i FROM Ingredient i");
            while(resultSet.next()) {
                an_ingredient = new Ingredient((int)resultSet.getInt("id"),
                        resultSet.getInt("recipe_id")     ,
                        resultSet.getString("name"),
                        resultSet.getString("image"),
                        resultSet.getString("consistency"),
                        resultSet.getString("nameClean"),
                        resultSet.getString("original"),
                        resultSet.getString("originalString"),
                        resultSet.getFloat("amount"),
                        resultSet.getString("unit"),
                        (ArrayList) resultSet.getArray("meta"),
                        (ArrayList) resultSet.getArray("metaInformation"), (ArrayList)  resultSet.getArray("measures"));

                ingredientList.add(an_ingredient);
            }
            return ingredientList;
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return ingredientList;
    }

    public Iterable<Ingredient> findAllById(Iterable<Integer> ints) {
        Set<Ingredient> ingredientSet = new HashSet<Ingredient>();
        for(int id : ints) {
            Ingredient found_ingredient= findById(id);
            ingredientSet.add(found_ingredient);
        }
        return ingredientSet;
    }

    public Ingredient save(Ingredient ingredient) {

        try{
            ResultSet resultSet = conn.createStatement().executeQuery("insert into Ingredient ('id','recipe_id','name', 'image','consistency','nameClean','original', 'originalString', 'amount', 'unit', 'meta', 'metaInformation', 'measures') values(" + ingredient.getId() +"," + ingredient.getRecipe_id()
                                                    + ", '"  + ingredient.getName() + "','" + ingredient.getImage() + "','" + ingredient.getConsistency() +
                                                     "', '" + ingredient.getNameClean() + "','" + ingredient.getOriginal() + "', '" + ingredient.getOriginalString() +
                                                     "', " + ingredient.getAmount() + ", '" + ingredient.getUnit() + "'," + ingredient.getMeta() + "," + ingredient.getMetaInformation() +
                                                     "," + ingredient.getMeasures());

            while(resultSet.next()) {
                ResultSet recipeResultSet = conn.createStatement().executeQuery("SELECT i FROM Recipe i where i.id=" + (int) resultSet.getInt("result_id"));

                while(recipeResultSet.next()) {
                    Recipe a_recipe = new Recipe(recipeResultSet.getInt("id"),
                            recipeResultSet.getString("name"),
                            recipeResultSet.getString("description"),
                            recipeResultSet.getString("instructions"),
                            Lists.newArrayList(ingredient));
                }
            }
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
        return ingredient;
    }



    public void delete(Ingredient ingredient) {
        try {
            ResultSet resultSet = conn.createStatement().executeQuery("delete from Ingredient i where i.id=" + ingredient.getId() + " and i.recipe_id=" + ingredient.getRecipe_id());
            while (resultSet.next()) {
                logger.debug("deleting ingredient with id=" + ingredient.getId());
            }
        } catch(SQLException sqlException) {
            logger.debug(sqlException.getMessage());
        }
    }

    public void deleteAllById(Iterable<? extends Integer> ints) {

        List<Ingredient> ingredientList = new ArrayList<>();
        for(int id : ints) {
            Ingredient ingredient = findById(id);
            if(ingredient != null) {
                ingredientList.add(ingredient);
            }
        }

        deleteAll(ingredientList);
    }

    public void deleteAll(Iterable<? extends Ingredient> ingredients) {
        for(Ingredient ingredient : ingredients) {
            delete(ingredient);
        }
    }

    public void deleteAll() {
        List<Ingredient> ingredientList  = findAll();
        if(ingredientList.size() > 0) {
            deleteAll(ingredientList);
        }
    }

    //   @CacheEvict(value = "recipes",allEntries = true)
    public void deleteById(Integer id) {
        deleteAllById(List.of(id));
    }


    public List<Ingredient> getIngredientsWithRecipeId(int recipe_id) {
        return null;
    }


}
