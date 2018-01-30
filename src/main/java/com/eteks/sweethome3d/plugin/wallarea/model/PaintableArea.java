package com.eteks.sweethome3d.plugin.wallarea.model;

import com.eteks.sweethome3d.plugin.wallarea.WallArea;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaintableArea {

    Map<Color, Double> areas = new HashMap<Color, Double>();

    public PaintableArea() {
    }

    public void add(List<WallArea> wallAreas) {
        for (WallArea wallArea : wallAreas) {
//            int r = wallArea.getColor() % 256;
//            int g = (wallArea.getColor() / 256) % 256;
//            int b = (wallArea.getColor() / 256 / 256) % 256;
//            int a = (wallArea.getColor() / 256 / 256 / 256);
            Color color = new Color(wallArea.getColor());
            if (areas.containsKey(color)) {
                areas.put(color, (areas.get(color) + wallArea.getSurface()));
            } else {
                areas.put(color, wallArea.getSurface());
            }
        }
    }

    public Map<Color, Double> getAreas() {
        return areas;
    }
}
