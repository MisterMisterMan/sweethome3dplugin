package com.eteks.sweethome3d.plugin.wallarea;

public enum WallAreaString {
    CALC_WALL_AREA("general feature name for menu keys"),
    ROOM_WITHOUT_NAME("name for room where no name is given - better then display null"),
    NO_ROOMS_IN_MODEL("no rooms avaiable for wall area calculation")
    ;

    private final String description;

    WallAreaString(String description) {
        this.description = description;
    }
}
