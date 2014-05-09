package com.olmo.d3stats.models;

import java.io.Serializable;

public class Gem implements Serializable{
    private int type;
    private float value;
    private String icon;

    public Gem(int type, float value, String icon){
        this.type = type;
        this.value = value;
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
