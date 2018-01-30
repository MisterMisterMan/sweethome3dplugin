package com.eteks.sweethome3d.plugin.wallarea.resource;

import java.util.Locale;
import java.util.ResourceBundle;

public class WallAreaStringProvider {

    public static final String APPLICATION_PLUGIN = "ApplicationPlugin";
    private final ResourceBundle resource;

    public WallAreaStringProvider(ClassLoader classLoader) {
        resource = ResourceBundle.getBundle(
                APPLICATION_PLUGIN,
                Locale.getDefault(), classLoader);
    }

    public String get(WallAreaString wallAreaString) {
        return resource.getString(wallAreaString.name());
    }

}
