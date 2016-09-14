package com.moffatt.xander.familymap.model;

/**
 * Model for setting. Sets tings.
 * Super class for LineSetting and MapTypeSetting
 * Created by Xander on 7/28/2016.
 */
public class Setting {
    public enum LINE_TYPE { LIFE_STORY, FAMILY_TREE, SPOUSE };
    public enum MAP_TYPE { NORMAL, HYBRID, SATELLITE, TERRAIN };
    public Setting() {}
}
