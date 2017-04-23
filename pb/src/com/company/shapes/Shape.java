package com.company.shapes;

import com.company.model.Vertex;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sails on 23.04.2017.
 */
public abstract class Shape {

    protected List<Vertex> vertices;

    public Shape(Vertex ... vertices){
        this.vertices = Arrays.asList(vertices);
    }

    protected double getLengthBtwVertexes(Vertex v1, Vertex v2) {
        return Math.sqrt(Math.pow(v1.getX() - v2.getX(), 2) + Math.pow(v1.getY() - v2.getY(), 2));
    }

    public abstract double getPerimeter();

    public abstract double getSquare();

    public abstract QuadrilateralType getType();

    @Override
    public String toString() {
        return "Shape{" +
                "vertices=" + vertices +
                '}';
    }
}
