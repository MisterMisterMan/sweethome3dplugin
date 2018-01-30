package com.eteks.sweethome3d.plugin.wallarea;

import com.eteks.sweethome3d.model.DoorOrWindow;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.plugin.Plugin;
import com.eteks.sweethome3d.plugin.PluginAction;
import com.eteks.sweethome3d.plugin.wallarea.model.PaintableArea;
import com.eteks.sweethome3d.plugin.wallarea.resource.WallAreaString;
import com.eteks.sweethome3d.plugin.wallarea.resource.WallAreaStringProvider;
import com.eteks.sweethome3d.plugin.wallarea.model.WallAreaInitializer;


import javax.swing.JOptionPane;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.eteks.sweethome3d.plugin.wallarea.resource.WallAreaStringProvider.APPLICATION_PLUGIN;


public class WallAreaPlugin extends Plugin {

    private final WallAreaInitializer initializer = new WallAreaInitializer();
    private WallAreaStringProvider wallAreaStringProvider = null;


    @Override
    public PluginAction[] getActions() {
        return new PluginAction[]{
                new MyPluginAction(
                        APPLICATION_PLUGIN,
                        WallAreaString.CALC_WALL_AREA.name(), getPluginClassLoader(), true, Mode.ALL) {
                }
        };
    }

    private class MyPluginAction extends PluginAction {

        private final Mode mode;

        MyPluginAction(String resourceBaseName,
                       String actionPrefix,
                       ClassLoader pluginClassLoader,
                       boolean enabled,
                       Mode mode) {
            super(resourceBaseName, actionPrefix, pluginClassLoader, enabled);
            this.mode = mode;
        }

        @Override
        public void execute() {
            showWallAreaCalculationWindow(mode);
        }


        private void showWallAreaCalculationWindow(Mode mode) {
            List<WallArea> wallAreas = createWallAreas(mode);
            PaintableArea paintableArea = new PaintableArea();
            paintableArea.add(wallAreas);
            String name;
            try {
                name = createDisplayLabel(paintableArea);
            } catch (Throwable t) {
                name = t.getMessage();
            }
            JOptionPane.showMessageDialog(null, name);
        }

        private List<WallArea> createWallAreas(Mode mode) {
            List<DoorOrWindow> furnitureList = new ArrayList<DoorOrWindow>();
            for (HomePieceOfFurniture piece : getHome().getFurniture()) {
                if (piece instanceof DoorOrWindow) {
                    furnitureList.add((DoorOrWindow) piece);
                }
            }
            switch (mode) {
                case SELECTION:
                    return initializer.createWallAreaOfSelectables(getHome().getSelectedItems(), furnitureList);
                case ALL:
                default:
                    return initializer.createWallArea(getHome().getWalls(), furnitureList);
            }
        }

        private String createDisplayLabel(PaintableArea paintableArea) {
            if (paintableArea.getAreas().isEmpty())
                return getWallAreaStringProvider().get(WallAreaString.NO_WALLS_IN_MODEL);

            StringBuilder builder = new StringBuilder();
            Map<Color, Double> areas = paintableArea.getAreas();
            for (Color color : areas.keySet()) {
                builder.append("R: " + color.getRed() +
                        " G: " + color.getGreen() +
                        " B: " + color.getBlue() +
                        " A: " + color.getAlpha() +
                        "\t=>\t" + areas.get(color) + "\n");
            }
            return builder.toString();
        }

        // Generated Getter / Setter
        WallAreaStringProvider getWallAreaStringProvider() {
            if (wallAreaStringProvider == null) {
                wallAreaStringProvider = new WallAreaStringProvider(getPluginClassLoader());
            }
            return wallAreaStringProvider;
        }
    }

    private enum Mode {
        SELECTION, ALL;
    }
}
