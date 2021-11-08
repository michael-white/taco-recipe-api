package com.michaelwhite.transport4.tacorecipeapi.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.Long;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;


/** @noinspection ALL */
@Data
@Entity
//@Table(name="recipe")
//@SecondaryTable(name="ingredient",  foreignKey = @ForeignKey(name=""), pkJoinColumns = @PrimaryKeyJoinColumn(name="recipe_id"))
@Component
public class Recipe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", table = "recipe",  nullable=false, unique=true)
    int id;

   // @Column(name="name", table = "recipe",  length=50, nullable=false, unique=false)
    String name;

   // @Column(name="description", table = "recipe",  length=255, nullable=false, unique=false)
    String description;

//    @Column(name="instructions", table = "recipe",  length=255, nullable=false, unique=false)
    String instructions;

//    @JoinColumn(name = "ingredient_id", nullable = false)
//   // @ElementCollection
//    @CollectionTable(name="ingredient")
//    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "ingredient")
//    Long ingredient_id;


  // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  // @JoinColumn(name = "recipe_id", table="ingredient")
  @ElementCollection
   List<Ingredient> ingredients;
    List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }


    public Recipe(int id, String name, String description, String instructions, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public Recipe () {

    }


}
