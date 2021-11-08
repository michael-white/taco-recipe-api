package com.michaelwhite.transport4.tacorecipeapi.dao;

import com.michaelwhite.transport4.tacorecipeapi.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {

//    @Override
//    @Cacheable(value="recipes")
//    public Optional<Recipe> findById(Long id);id

//    @Override
//    @Cacheable(value="recipes")
//    public Optional findById(Long id);

//    @Override
//    public boolean existsById(Long aLong);

    List<Recipe> findAll();


    //@Override
    Recipe save(Recipe recipe);


//    @Override
//    @CacheEvict(value = "recipes",allEntries = true)
//    public void update(Recipe updatedRecipe);

    //@Override
    void delete(Recipe recipe);
//
//
//
//    @Override
//    @CacheEvict(value = "recipes",allEntries = true)
//    public void deleteAll();
//
//    @Override
//    @CacheEvict(value = "recipes",allEntries = true)
//    public void deleteById(Long id);


}
