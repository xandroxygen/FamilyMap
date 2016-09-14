package com.moffatt.xander.familymap.model;

/**
 * Setting for map type.
 * Created by Xander on 7/28/2016.
 */
public class MapSetting extends Setting {
    private Setting.MAP_TYPE type;

    public MapSetting() {
        type = null;
    }

    public MapSetting(Setting.MAP_TYPE type) {
        this.type = type;
    }

    public Setting.MAP_TYPE getType() {
        return type;
    }

    public void setType(Setting.MAP_TYPE type) {
        this.type = type;
    }
}
