package com.olmo.d3stats.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.olmo.d3stats.utils.Stats;

import java.io.Serializable;
import java.util.ArrayList;

@DatabaseTable(tableName = "items")
public class Item {
    public enum Estadisticas implements Serializable {
        MIN_DAMAGE, MAX_DAMAGE, VEL_BASE, ARMOR, STRENGTH, DESTREZA, INTELIGENCIA,
        VITALIDAD, RESISTENCIAS, VIDA, PROB_CRITICO, DANO_CRITICO, VEL_ATAQUE,
        R_FISICA, R_FRIO, R_FUEGO, R_RAYOS, R_VENENO, R_ARCANA, REG_VIDA, VIDA_GOLPE, ROBO_VIDA, H_MAGICO, H_ORO,
        BONUS_ARCANO, BONUS_VENENO, BONUS_RAYOS, BONUS_SAGRADO, BONUS_FRIO, BONUS_FUEGO, BONUS_ELITE
    }

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, foreign = true)
    private Hero hero;
    @DatabaseField
    private String name;
    @DatabaseField
    private int type;
    @DatabaseField
    private String typeName;
    @DatabaseField
    private String color;
    @DatabaseField
    private String icon;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public float[] stats = new float[Stats.NUM_STATS];

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public Gem gems[] = new Gem[3];

    public Item(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
