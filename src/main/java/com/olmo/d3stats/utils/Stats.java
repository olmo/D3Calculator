package com.olmo.d3stats.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Stats {
    public static final int NUM_STATS = 31;

    public static final int MIN_DAMAGE = 0;
    public static final int MAX_DAMAGE = 1;
    public static final int VEL_BASE = 2;
    public static final int ARMOR = 3;
    public static final int STRENGTH = 4;
    public static final int DESTREZA = 5;
    public static final int INTELIGENCIA = 6;
    public static final int VITALIDAD = 7;
    public static final int RESISTENCIAS = 8;
    public static final int VIDA = 9;
    public static final int PROB_CRITICO = 10;
    public static final int DANO_CRITICO = 11;
    public static final int VEL_ATAQUE = 12;
    public static final int R_FISICA = 13;
    public static final int R_FRIO = 14;
    public static final int R_FUEGO = 15;
    public static final int R_RAYOS = 16;
    public static final int R_VENENO = 17;
    public static final int R_ARCANA = 18;
    public static final int REG_VIDA = 19;
    public static final int VIDA_GOLPE = 20;
    public static final int ROBO_VIDA = 21;
    public static final int H_MAGICO = 22;
    public static final int H_ORO = 23;
    public static final int BONUS_ARCANO = 24;
    public static final int BONUS_VENENO = 25;
    public static final int BONUS_RAYOS = 26;
    public static final int BONUS_SAGRADO = 27;
    public static final int BONUS_FRIO = 28;
    public static final int BONUS_FUEGO = 29;
    public static final int BONUS_ELITE = 30;


    public static final Map<String, Integer> properties;

    static{
        Map<String, Integer> pMap = new HashMap<String, Integer>();
        //propiedades.put("Damage", -2);
        pMap.put("Damage_Min#Physical",0);
        pMap.put("Damage_Bonus_Min#Physical",0);
        pMap.put("Damage_Delta#Physical",1);

        pMap.put("Damage_Weapon_Min#Poison",0);
        pMap.put("Damage_Weapon_Min#Arcane",0);
        pMap.put("Damage_Weapon_Min#Cold",0);
        pMap.put("Damage_Weapon_Min#Fire",0);
        pMap.put("Damage_Weapon_Min#Holy",0);
        pMap.put("Damage_Weapon_Min#Lightning",0);
        pMap.put("Damage_Weapon_Delta#Poison",-1);
        pMap.put("Damage_Weapon_Delta#Arcane",-1);
        pMap.put("Damage_Weapon_Delta#Cold",-1);
        pMap.put("Damage_Weapon_Delta#Fire",-1);
        pMap.put("Damage_Weapon_Delta#Holy",-1);
        pMap.put("Damage_Weapon_Delta#Lightning",-1);

        pMap.put("Armor_Bonus_Item",3);
        pMap.put("Armor_Item",3);
        pMap.put("Strength_Item",4);
        pMap.put("Strength",4);
        pMap.put("Dexterity_Item",5);
        pMap.put("Dexterity",5);
        pMap.put("Intelligence_Item",6);
        pMap.put("Intelligence",6);
        pMap.put("Vitality_Item",7);
        pMap.put("Vitality",7);
        pMap.put("Resistance_All",8);
        pMap.put("Hitpoints_Max_Percent_Bonus_Item",9);
        pMap.put("Crit_Percent_Bonus_Capped",10);
        pMap.put("Crit_Damage_Percent",11);
        pMap.put("Attacks_Per_Second_Percent",12);
        pMap.put("Resistance#Physical",13);
        pMap.put("Resistance#Cold",14);
        pMap.put("Resistance#Fire",15);
        pMap.put("Resistance#Lightning",16);
        pMap.put("Resistance#Poison",17);
        pMap.put("Resistance#Arcane",18);
        pMap.put("Hitpoints_Regen_Per_Second",19);
        pMap.put("Hitpoints_On_Hit",20);
        pMap.put("Steal_Health_Percent",21);
        pMap.put("Magic_Find",22);
        pMap.put("Gold_Find",23);

        pMap.put("Damage_Dealt_Percent_Bonus#Arcane",24);
        pMap.put("Damage_Dealt_Percent_Bonus#Poison",25);
        pMap.put("Damage_Dealt_Percent_Bonus#Lightning",26);
        pMap.put("Damage_Dealt_Percent_Bonus#Holy",27);
        pMap.put("Damage_Dealt_Percent_Bonus#Cold",28);
        pMap.put("Damage_Dealt_Percent_Bonus#Fire",29);
        pMap.put("Damage_Percent_Bonus_Vs_Elites",30);
        /*pMap.put("Damage_Percent_Bonus_Vs_Monster_Type",31);

        pMap.put("Damage_Percent_Reduction_From_Elites",32);
        pMap.put("Damage_Percent_Reduction_From_Melee",33);
        pMap.put("Damage_Percent_Reduction_From_Ranged",34);
        pMap.put("Damage_Percent_Reduction_From_Type",35);
        pMap.put("Damage_Percent_Reduction_Turns_Into_Heal",36);

        pMap.put("Block_Amount_Item_Delta",37);
        pMap.put("Block_Amount_Item_Min",38);
        pMap.put("Block_Chance_Bonus_Item",39);
        pMap.put("Block_Chance_Item",40);
        pMap.put("CrowdControl_Reduction",41);
        pMap.put("Experience_Bonus",42);
        pMap.put("Experience_Bonus_Percent",43);

        pMap.put("Gold_PickUp_Radius",43);
        pMap.put("Health_Globe_Bonus_Chance",43);
        pMap.put("Health_Globe_Bonus_Health",43);
        pMap.put("Hitpoints_Granted",43);
        pMap.put("Hitpoints_Granted_Duration",43);
        pMap.put("Hitpoints_Max_Percent_Bonus_Item",43);
        pMap.put("Hitpoints_On_Hit",43);
        pMap.put("Hitpoints_On_Kill",43);
        pMap.put("Hitpoints_Percent",43);
        pMap.put("Hitpoints_Regen_Per_Second",43);
        pMap.put("Movement_Scalar",43);
        pMap.put("Power_Cooldown_Reduction",43);
        pMap.put("Gold_PickUp_Radius",43);
        pMap.put("Gold_PickUp_Radius",43);
        pMap.put("Gold_PickUp_Radius",43);


        */

        properties = Collections.unmodifiableMap(pMap);
    }


}
