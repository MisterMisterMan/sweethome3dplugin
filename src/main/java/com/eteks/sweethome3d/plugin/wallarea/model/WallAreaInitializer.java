package com.eteks.sweethome3d.plugin.wallarea.model;

import com.eteks.sweethome3d.model.*;
import com.eteks.sweethome3d.plugin.wallarea.WallArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WallAreaInitializer {

    private final PrivateVisibilityAccessor accessor = new PrivateVisibilityAccessor();
    private final IntersectionDetector detector = new IntersectionDetector();

    public List<WallArea> createWallAreaOfSelectables(Collection<Selectable> selectables, Collection<DoorOrWindow> furnitureList) {
        List<Wall> walls = new ArrayList<Wall>();
        for (Selectable selectable : selectables) {
            if (selectable instanceof Wall) walls.add((Wall) selectable);
        }
        return createWallArea(walls, furnitureList);
    }


    public List<WallArea> createWallArea(Collection<Wall> walls, Collection<DoorOrWindow> furnitureList) {
        List<WallArea> wallAreas = new ArrayList<WallArea>();
        for (Wall wall : walls) {
            Shape shape = accessor.invoke(wall, "getShape", false);
            double sizeOfFurniture = calcFurnitureOverlay(shape, furnitureList);
            double size;
            if (!wall.isTrapezoidal()) {
                size = (wall.getHeight() * wall.getLength());
            } else {
                float minHeight = Math.min(wall.getHeight(), wall.getHeightAtEnd());
                float maxHeight = Math.max(wall.getHeight(), wall.getHeightAtEnd());
                size = ((maxHeight + minHeight) * 0.5 * wall.getLength());
            }
            if (wall.getLeftSideColor() != null) {
                wallAreas.add(new WallAreaImpl(shape, size, sizeOfFurniture, wall.getLeftSideColor()));
            }
            if (wall.getRightSideColor() != null) {
                wallAreas.add(new WallAreaImpl(shape, size, sizeOfFurniture, wall.getRightSideColor()));
            }
        }
        return wallAreas;
    }

    private double calcFurnitureOverlay(Shape shape, Collection<DoorOrWindow> furnitureList) {
        double sizeOfFurniture = 0;
        for (DoorOrWindow furniture : furnitureList) {
            if (furniture instanceof HomePieceOfFurniture && detector.isIntersecting(shape,
                    (Shape) accessor.invoke(furniture, "getShape"))) {
                sizeOfFurniture += (furniture.getHeight() * furniture.getWidth());
            }
        }
        return sizeOfFurniture;
    }

    private class WallAreaImpl implements WallArea {

        WallAreaImpl(Shape shape, double size, double sizeOfFurniture, int color) {
            this.shape = shape;
            this.size = size;
            this.sizeOfFurniture = sizeOfFurniture;
            this.color = color;
        }

        private final Shape shape;
        private final double size;
        private final double sizeOfFurniture;
        private final int color;

        @Override
        public double getSurface() {
            return size - sizeOfFurniture;
        }

        @Override
        public Shape getShape() {
            return shape;
        }

        @Override
        public Integer getColor() {
            return color;
        }
    }

}
