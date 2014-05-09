package com.olmo.d3stats.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "heroes")
public class Hero {

    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String server;
    @DatabaseField
    private String profile;
    @DatabaseField
    private String clase;
    @DatabaseField
    private int gender;
    @DatabaseField
    private int level;
    @DatabaseField
    private int paragonLevel;
    @DatabaseField
    private boolean hardcore;
    @DatabaseField
    private boolean dead;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private int pasives[] = new int[4];
    @ForeignCollectionField(eager = true, orderColumnName="type")
    public ForeignCollection<Item> items;
    @DatabaseField
    private float paragonMainStat;
    @DatabaseField
    private float paragonCriticDamage;
    @DatabaseField
    private float paragonCriticPercent;
    @DatabaseField
    private float paragonAttackSpeed;
    @DatabaseField
    private int position;

    public Hero(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getParagonLevel() {
        return paragonLevel;
    }

    public void setParagonLevel(int paragonLevel) {
        this.paragonLevel = paragonLevel;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getPasive(int num) {
            return pasives[num];
    }

    public void setPasive(int num, int pasive) {
        this.pasives[num] = pasive;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean[] getPasivas(){
        boolean [] pasivas = new boolean[19];

        for(int i=0; i<19; i++)
            pasivas[i] = false;

        pasivas[pasives[0]] = true;
        pasivas[pasives[1]] = true;
        pasivas[pasives[2]] = true;
        pasivas[pasives[3]] = true;

        return pasivas;
    }

    public float getParagonAttackSpeed() {
        return paragonAttackSpeed;
    }

    public void setParagonAttackSpeed(float paragonAttackSpeed) {
        this.paragonAttackSpeed = paragonAttackSpeed;
    }

    public float getParagonMainStat() {
        return paragonMainStat;
    }

    public void setParagonMainStat(float paragonMainStat) {
        this.paragonMainStat = paragonMainStat;
    }

    public float getParagonCriticDamage() {
        return paragonCriticDamage;
    }

    public void setParagonCriticDamage(float paragonCriticDamage) {
        this.paragonCriticDamage = paragonCriticDamage;
    }

    public float getParagonCriticPercent() {
        return paragonCriticPercent;
    }

    public void setParagonCriticPercent(float paragonCriticPercent) {
        this.paragonCriticPercent = paragonCriticPercent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
