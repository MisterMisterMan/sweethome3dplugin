package com.eteks.sweethome3d.plugin.wallarea.model;

import java.awt.*;
import java.awt.geom.Area;

public class IntersectionDetector {

    public boolean isIntersecting(Shape shapeA, Shape shapeB) {
        Area areaA = new Area(shapeA);
        areaA.intersect(new Area(shapeB));
        return !areaA.isEmpty();
    }



}
