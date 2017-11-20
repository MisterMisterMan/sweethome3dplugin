package com.eteks.sweethome3d.plugin.wallarea;

import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;

import javax.swing.*;

import static com.eteks.sweethome3d.plugin.wallarea.WallAreaStringProvider.APPLICATION_PLUGIN;


public class WallAreaPlugin extends Plugin {


    private WallAreaStringProvider wallAreaStringProvider = null;

    @Override
    public PluginAction[] getActions() {
        return new PluginAction[]{new PluginAction(
                APPLICATION_PLUGIN,
                WallAreaString.CALC_WALL_AREA.name(), getPluginClassLoader(), true) {
            @Override
            public void execute() {
                showWallAreaCalculationWindow();
            }
        }
        };
    }

    private void showWallAreaCalculationWindow() {
        JOptionPane.showMessageDialog(null, createDisplayLabel());
    }

    private String createDisplayLabel() {
        String label = !getHome().getRooms().isEmpty() ? "" :
                getWallAreaStringProvider().get(WallAreaString.NO_ROOMS_IN_MODEL);
        for (Room room : getHome().getRooms()) {
            WallAreaModel model = new WallAreaModel(room);
            String name = model.getName() != null ? model.getName() :
                    getWallAreaStringProvider().get(WallAreaString.ROOM_WITHOUT_NAME);
            label += "" + name + " : "
                    + model.getPerimeter()
                    + "\n";
        }
        return label;
    }

    public WallAreaStringProvider getWallAreaStringProvider() {
        if (wallAreaStringProvider == null) {
            wallAreaStringProvider = new WallAreaStringProvider(getPluginClassLoader());
        }
        return wallAreaStringProvider;
    }
}
