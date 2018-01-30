package com.eteks.sweethome3d.plugin.wallarea.resource;

public enum WallAreaString {
    CALC_WALL_AREA_SELECTED("general feature name for menu keys - selected walls"),
    CALC_WALL_AREA("general feature name for menu keys - all walls"),
    NO_WALLS_IN_MODEL("no rooms avaiable for wall area calculation")
    ;

    private final String description;

    WallAreaString(String description) {
        this.description = description;
    }
}
