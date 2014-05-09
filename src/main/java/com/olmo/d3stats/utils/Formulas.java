package com.olmo.d3stats.utils;


import com.olmo.d3stats.models.Hero;
import com.olmo.d3stats.models.Item;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Formulas {

    public static float[] sumStats(Hero hero, Item it, boolean usePasives, float extraGems[]){
        float sumStats[] = new float[Stats.NUM_STATS];
        int hp = 0;
        float vel_ataque_real = 0;

        sumStats[Stats.PROB_CRITICO] += hero.getParagonCriticPercent();
        sumStats[Stats.DANO_CRITICO] += hero.getParagonCriticDamage();
        sumStats[Stats.VEL_ATAQUE] += hero.getParagonAttackSpeed();

        for(int i=0; i<extraGems.length; i++)
            sumStats[i] += extraGems[i];

        Object[] itemsO = hero.items.toArray();
        Item[] items = Arrays.asList(itemsO).toArray(new Item[itemsO.length]);

        if(it!=null)
            items[it.getType()] = it;

        for(Item item: items) {

            for(int i=4; i<Stats.NUM_STATS; i++){
                if(i!=12 || (i==12 && item.getType() != 11 && !(item.getType() == 12 && item.stats[2] > 0)))
                    sumStats[i] += item.stats[i];
            }

            for (int i = 0; i < 3; i++){
                if (item.gems[i] != null) {
                    sumStats[item.gems[i].getType()] += item.gems[i].getValue();
                }
            }

        }

        sumStats[Stats.VITALIDAD] += 9 + (hero.getLevel()-1)*2;
        sumStats[Stats.VEL_ATAQUE] /= 100f;
        sumStats[Stats.VIDA] /= 100f;
        sumStats[Stats.VIDA] += 1;

        //Velocidad de ataque
        if(usePasives && hero.getClase().equals("barbarian") && hero.getPasivas()[3] &&
                (items[11].getTypeName().equals("Polearm") ||
                        items[11].getTypeName().equals("Spear")) )
            sumStats[Stats.VEL_ATAQUE] += 0.08;


        if(items[12].stats[2]>0){
            DecimalFormat dec = new DecimalFormat();
            dec.setMaximumFractionDigits(2);

            float v1 = (sumStats[Stats.VEL_ATAQUE]+1.15f)*(items[11].stats[2]);
            float v2 = (sumStats[Stats.VEL_ATAQUE]+1.15f)*(items[12].stats[2]);

            vel_ataque_real = Float.parseFloat(dec.format((v1 + v2) / 2).replace(',', '.'));
            sumStats[Stats.VEL_ATAQUE] = (sumStats[Stats.VEL_ATAQUE]+1.15f)*(items[11].stats[2]);
        }
        else{
            sumStats[Stats.VEL_ATAQUE] = (1+sumStats[Stats.VEL_ATAQUE])*(items[11].stats[2]);
            vel_ataque_real = sumStats[Stats.VEL_ATAQUE];
        }

        //Prob. y da�o cr�tico
        sumStats[Stats.PROB_CRITICO] = (5+sumStats[Stats.PROB_CRITICO])/100;
        sumStats[Stats.DANO_CRITICO] = (50+sumStats[Stats.DANO_CRITICO])/100;

        int mindi=0, maxdi=0;
        float ditem = 0;

        for(Item item: hero.items){
            if(item.getType()!=11 && item.getType()!=12){
                mindi += item.stats[0];
                maxdi += item.stats[1];
                ditem += (item.stats[0]+item.stats[1])/2;
            }
        }
        if(items[12].stats[2]==0){
            mindi += items[12].stats[0];
            maxdi += items[12].stats[1];
            ditem += (items[12].stats[0]+items[12].stats[1])/2;
        }

        //float ditem = (mindi+maxdi)/2;
        int bonusId = 0;
        float bonusMayor = 0;

        for(int i=Stats.BONUS_ARCANO; i<=Stats.BONUS_FUEGO; i++) {
            if (sumStats[i] > bonusMayor){
                bonusMayor = sumStats[i];
                bonusId = i;
            }
        }

        //float bonus = b_arcano+b_veneno+b_rayos+b_sagrado+b_frio+b_fuego;
        //bonus = 0;

        float darma=0;
        if(items[12].stats[2]>0)
            darma = ((items[11].stats[0]+items[11].stats[1])/2 +
                    (items[12].stats[0]+items[12].stats[1])/2)/2;
        else
            darma = (items[11].stats[0]+items[11].stats[1])/2;

        //ditem=0;


        float dps = 0;


        if(hero.getClase().equals("barbarian")){
            sumStats[Stats.STRENGTH] += 10 + (hero.getLevel()-1)*3 + hero.getParagonMainStat();
            sumStats[Stats.DESTREZA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.INTELIGENCIA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.ARMOR] += sumStats[Item.Estadisticas.STRENGTH.ordinal()];

            sumStats[Stats.RESISTENCIAS] += sumStats[Stats.INTELIGENCIA]*0.1;

            if(usePasives && hero.getPasivas()[2])
                sumStats[Stats.ARMOR] += sumStats[Stats.VITALIDAD] * 0.5;

            float mul_armor = 1;
            if(usePasives && hero.getPasivas()[10])
                mul_armor += 0.25;

            sumStats[Stats.ARMOR] *= mul_armor;

            //Vida
            if(hero.getLevel()<35)
                hp += 36+4*hero.getLevel()+10*sumStats[Stats.VITALIDAD];
            else
                hp += 36+4*hero.getLevel()+(hero.getLevel()-25)*sumStats[Stats.VITALIDAD];
            hp *= sumStats[Stats.VIDA];

            if(usePasives && hero.getPasivas()[3] &&
                    (items[11].getTypeName().equals("Mace") ||
                            items[11].getTypeName().equals("Two-Handed Mace")||
                            items[11].getTypeName().equals("Axe")||
                            items[11].getTypeName().equals("Two-Handed Axe") ) )
                sumStats[Stats.PROB_CRITICO] += 0.05;

            dps = calcularDPS((int)sumStats[Stats.STRENGTH], ditem, darma, vel_ataque_real, sumStats[Stats.PROB_CRITICO], sumStats[Stats.DANO_CRITICO]);

            if(usePasives && hero.getPasivas()[3] &&
                    (items[11].getTypeName().equals("Sword") ||
                            items[11].getTypeName().equals("Dagger")||
                            items[11].getTypeName().equals("Two-Handed Sword")) )
                dps *= (1+0.15);
        }
        else if(hero.getClase().equals("crusader")){
            sumStats[Stats.STRENGTH] += 10 + (hero.getLevel()-1)*3 + hero.getParagonMainStat();
            sumStats[Stats.DESTREZA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.INTELIGENCIA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.ARMOR] += sumStats[Item.Estadisticas.STRENGTH.ordinal()];

            sumStats[Stats.RESISTENCIAS] += sumStats[Stats.INTELIGENCIA]*0.1;


            //Vida
            if(hero.getLevel()<35)
                hp += 36+4*hero.getLevel()+10*sumStats[Stats.VITALIDAD];
            else
                hp += 36+4*hero.getLevel()+(hero.getLevel()-25)*sumStats[Stats.VITALIDAD];
            hp *= sumStats[Stats.VIDA];

            dps = calcularDPS((int)sumStats[Stats.STRENGTH], ditem, darma, vel_ataque_real, sumStats[Stats.PROB_CRITICO], sumStats[Stats.DANO_CRITICO]);
        }
        else if(hero.getClase().equals("demonhunter")){
            sumStats[Stats.STRENGTH] += 8 + (hero.getLevel()-1);
            sumStats[Stats.DESTREZA] += 10 + (hero.getLevel()-1)*3 + hero.getParagonMainStat();
            sumStats[Stats.INTELIGENCIA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.ARMOR] += sumStats[Stats.STRENGTH];

            sumStats[Stats.RESISTENCIAS] += sumStats[Stats.INTELIGENCIA]*0.1;
            //Vida
            if(hero.getLevel()<35)
                hp += 36+4*hero.getLevel()+10*sumStats[Stats.VITALIDAD];
            else
                hp += 36+4*hero.getLevel()+(hero.getLevel()-25)*sumStats[Stats.VITALIDAD];
            hp *= sumStats[Stats.VIDA];

            if(usePasives && hero.getPasivas()[8] && items[11].getTypeName().equals("Crossbow"))
                sumStats[Stats.DANO_CRITICO]+=0.5;
            if(usePasives && hero.getPasivas()[8] && items[11].getTypeName().equals("Hand Crossbow"))
                sumStats[Stats.PROB_CRITICO]+=0.05;

            if(usePasives && hero.getPasivas()[13])
                sumStats[Stats.PROB_CRITICO]=1;

            dps = calcularDPS((int)sumStats[Stats.DESTREZA], ditem, darma, vel_ataque_real, sumStats[Stats.PROB_CRITICO], sumStats[Stats.DANO_CRITICO]);
            if(usePasives && hero.getPasivas()[8] && items[11].getTypeName().equals("Bow"))
                dps *= (1+0.08);

            if(usePasives && hero.getPasivas()[3])
                dps *= (1+0.2);
        }
        else if(hero.getClase().equals("monk")){
            sumStats[Stats.STRENGTH] += 8 + (hero.getLevel()-1);
            sumStats[Stats.DESTREZA] += 10 + (hero.getLevel()-1)*3;
            sumStats[Stats.INTELIGENCIA] += 8 + (hero.getLevel()-1) + hero.getParagonMainStat();
            sumStats[Stats.ARMOR] += sumStats[Stats.STRENGTH];

            sumStats[Stats.RESISTENCIAS] += sumStats[Stats.INTELIGENCIA]*0.1;
            //Vida
            if(hero.getLevel()<35)
                hp += 36+4*hero.getLevel()+10*sumStats[Stats.VITALIDAD];
            else
                hp += 36+4*hero.getLevel()+(hero.getLevel()-25)*sumStats[Stats.VITALIDAD];
            hp *= sumStats[Stats.VIDA];

            if(usePasives && hero.getPasivas()[5])
                sumStats[Stats.ARMOR] += sumStats[Stats.DESTREZA];

            dps = calcularDPS((int)sumStats[Stats.DESTREZA], ditem, darma, vel_ataque_real, sumStats[Stats.PROB_CRITICO], sumStats[Stats.DANO_CRITICO]);
        }
        else if(hero.getClase().equals("witchdoctor")){
            sumStats[Stats.STRENGTH] += 8 + (hero.getLevel()-1);
            sumStats[Stats.DESTREZA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.INTELIGENCIA] += 10 + (hero.getLevel()-1)*3 + hero.getParagonMainStat();
            sumStats[Stats.ARMOR] += sumStats[Stats.STRENGTH];

            sumStats[Stats.RESISTENCIAS] += sumStats[Stats.INTELIGENCIA]*0.1;
            //Vida
            if(hero.getLevel()<35)
                hp += 36+4*hero.getLevel()+10*sumStats[Stats.VITALIDAD];
            else
                hp += 36+4*hero.getLevel()+(hero.getLevel()-25)*sumStats[Stats.VITALIDAD];
            hp *= sumStats[Stats.VIDA];

            dps = calcularDPS((int)sumStats[Stats.INTELIGENCIA], ditem, darma, vel_ataque_real, sumStats[Stats.PROB_CRITICO], sumStats[Stats.DANO_CRITICO]);
            if(usePasives && hero.getPasivas()[7])
                dps *= (1+0.2);
        }
        else if(hero.getClase().equals("wizard")){
            sumStats[Stats.STRENGTH] += 8 + (hero.getLevel()-1);
            sumStats[Stats.DESTREZA] += 8 + (hero.getLevel()-1);
            sumStats[Stats.INTELIGENCIA] += 10 + (hero.getLevel()-1)*3 + hero.getParagonMainStat();
            sumStats[Stats.ARMOR] += sumStats[Stats.STRENGTH];

            sumStats[Stats.RESISTENCIAS] += sumStats[Stats.INTELIGENCIA]*0.1;
            //Vida
            if(hero.getLevel()<35)
                hp += 36+4*hero.getLevel()+10*sumStats[Stats.VITALIDAD];
            else
                hp += 36+4*hero.getLevel()+(hero.getLevel()-25)*sumStats[Stats.VITALIDAD];
            hp *= sumStats[Stats.VIDA];

            if(usePasives && hero.getPasivas()[3]){
                sumStats[Stats.ARMOR] *= (1-0.1);
                sumStats[Stats.RESISTENCIAS] *= (1-0.1);
            }

            dps = calcularDPS((int)sumStats[Stats.INTELIGENCIA], ditem, darma, vel_ataque_real, sumStats[Stats.PROB_CRITICO], sumStats[Stats.DANO_CRITICO]);

            if(usePasives && hero.getPasivas()[3])
                dps *= (1+0.15);
        }

        float elementalDPS = dps + dps*sumStats[bonusId]/100;
        float eliteDPS = elementalDPS + elementalDPS*sumStats[Stats.BONUS_ELITE]/100;

        sumStats[Stats.R_FISICA] += sumStats[Stats.RESISTENCIAS];
        sumStats[Stats.R_FRIO] += sumStats[Stats.RESISTENCIAS];
        sumStats[Stats.R_FUEGO] += sumStats[Stats.RESISTENCIAS];
        sumStats[Stats.R_RAYOS] += sumStats[Stats.RESISTENCIAS];
        sumStats[Stats.R_VENENO] += sumStats[Stats.RESISTENCIAS];
        sumStats[Stats.R_ARCANA] += sumStats[Stats.RESISTENCIAS];


        return new float[] {sumStats[Stats.STRENGTH], sumStats[Stats.DESTREZA], sumStats[Stats.INTELIGENCIA], sumStats[Stats.VITALIDAD], sumStats[Stats.ARMOR], hp, dps, elementalDPS, eliteDPS, sumStats[Stats.PROB_CRITICO]*100, sumStats[Stats.DANO_CRITICO]*100, sumStats[Stats.VEL_ATAQUE], sumStats[Stats.REG_VIDA], sumStats[Stats.VIDA_GOLPE], sumStats[Stats.ROBO_VIDA], sumStats[Stats.H_MAGICO], sumStats[Stats.H_ORO], sumStats[Stats.R_FISICA], sumStats[Stats.R_FRIO], sumStats[Stats.R_FUEGO], sumStats[Stats.R_RAYOS], sumStats[Stats.R_VENENO], sumStats[Stats.R_ARCANA]};
    }

    public static float calculateDPS(Hero hero, Item it, int type, boolean usePasives, float extraGems[]){
        float stats[] = sumStats(hero, it, usePasives, extraGems);

        if(type==0)
            return stats[6];
        else if(type==1)
            return stats[7];
        else if(type==2)
            return stats[8];

        return stats[6];
    }

    private static float calcularDPS(int principal, float ditem, float darma, float velocidad, float pcritico, float dcritico){
        return (ditem+darma)*velocidad*(1+pcritico*dcritico)*(1+0.01f*principal);
        //return ((ditem+darma)*velocidad*(1-pcritico)+(ditem+darma)*velocidad*pcritico*(1+dcritico))*(1+0.01f*principal);
    }
}
