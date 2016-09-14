package com.moffatt.xander.familymap.model;

import android.graphics.Color;

/**
 * Settings for lines.
 * Created by Xander on 7/28/2016.
 */
public class LineSetting extends Setting {
    private int color;
    private boolean isDrawn;
    private Setting.LINE_TYPE type;

    public LineSetting() {
        color = 0;
        isDrawn = false;
        type = null;
    }

    public LineSetting(int color, boolean isDrawn, Setting.LINE_TYPE type) {
        this.color = color;
        this.isDrawn = isDrawn;
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isDrawn() {
        return isDrawn;
    }

    public void setDrawn(boolean drawn) {
        isDrawn = drawn;
    }

    public Setting.LINE_TYPE getType() {
        return type;
    }

    public void setType(Setting.LINE_TYPE type) {
        this.type = type;
    }
}
