package com.michaelwhite.transport4.tacorecipeapi.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL */

@Getter
@Setter
@Entity
public class Ingredient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Column (name="id")
    private int id;


//    @ManyToOne
//    @JoinColumn(name="id", table="recipe", insertable=false, updatable=false)
    private int recipe_id;

   // @Column(name="name",  length=50, nullable=false, unique=false)
    private String name;

    //@Column(name="image", length=50, nullable=false, unique=false)
    private String image ;

   // @Column(name="consistency", length=50, nullable=false, unique=false)
    private String consistency;

   // @Column(name="nameClean", length=50, nullable=false, unique=false)
    private String nameClean;

   // @Column(name="original", length=50, nullable=false, unique=false)
    private String original;

  //  @Column(name="originalString", length=50, nullable=false, unique=false)
    private String originalString;

  //  @Column(name="amount", length=10, nullable=false, unique=false)
    private float amount;

  //  @Column(name="unit", length=6, nullable=false, unique=false)
    private String unit;


  // @Column(name="meta", length=10, nullable=false, unique=false)
  @ElementCollection
  private List<String> meta;
   // public List<String> getMeta() {
   //     return meta;
   // }
   // public void setMeta(List<String> meta) {
      // String jsonString = JSONArray.toJSONString(meta);
     //  this.meta = meta;
   // }

    @ElementCollection
    private List<String> metaInformation;

    //public List<String> getMetaInformation() {
    //    return metaInformation;
    //}

    //public void setMetaInformation(ArrayList<String> metaInformation) {
    //    this.metaInformation = metaInformation;
   // }

  //  @Column(name="measures", length=10, nullable=false, unique=false)
  @ElementCollection
  private List<String> measures;
   // public List<String> getMeasures() {
       // return JSONArray.toJSONString(measures);
     //   return measures;
   // }

    //public void setMeasures(List<String> measures) {
     //   this.measures = measures;
    //}

    public Ingredient(int id, int recipe_id, String name, String image, String consistency, String nameClean, String original, String originalString, Float amount, String unit, ArrayList<String> meta,ArrayList<String> metaInformation, ArrayList<String> measures ) {

        this.id = id;
        this.recipe_id = recipe_id;
        this.name = name;
        this.image = image;
        this.consistency =consistency;
        this.nameClean = nameClean;
        this.original = original;
        this.originalString = originalString;
        this.amount = amount;
        this.unit = unit;
        this.meta = meta;
        this.metaInformation = metaInformation;
        this.measures= measures;
    }

    public Ingredient() {

    }



}
