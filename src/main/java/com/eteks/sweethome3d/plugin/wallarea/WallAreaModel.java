package com.eteks.sweethome3d.plugin.wallarea;

import com.eteks.sweethome3d.model.Room;

public class WallAreaModel {

    private float[][] points;
    private String name;
    private float heigth;

    public WallAreaModel(Room room) {
        this.points = room.getPoints();
        this.name = room.getName();
    }

    public String getName() {
        return name;
    }

    public float getPerimeter() {
        float totalDistance = 0.0f;
        float[] previousPoint = points[0];
        float[] nextPoint = null;
        for (int i=1; i<points.length;i++) {
            nextPoint=points[i];
            totalDistance += distance(previousPoint,nextPoint);
            previousPoint=points[i];
        }
        totalDistance += distance(previousPoint,nextPoint);
        return totalDistance;
    }

    private double distance(float[] previousPoint, float[] nextPoint) {
        float deltaX = nextPoint[0]-previousPoint[0];
        float deltaY = nextPoint[1]-previousPoint[1];
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
}
