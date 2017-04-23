package com.company.shapes;

import com.company.model.Vertex;

/**
 * Created by sails on 21.04.2017.
 */
public class Quadrilateral extends Shape {

    private double side1;
    private double side2;
    private double side3;
    private double side4;
    private double diag1;
    private double diag2;

    public Quadrilateral(Vertex vertex1, Vertex vertex2, Vertex vertex3, Vertex vertex4) {
        super(vertex1, vertex2, vertex3, vertex4);

        this.side1 = getLengthBtwVertexes(vertex1, vertex2);
        this.side2 = getLengthBtwVertexes(vertex2, vertex3);
        this.side3 = getLengthBtwVertexes(vertex3, vertex4);
        this.side4 = getLengthBtwVertexes(vertex4, vertex1);
        this.diag1 = getLengthBtwVertexes(vertex1, vertex3);
        this.diag2 = getLengthBtwVertexes(vertex2, vertex4);
    }


    @Override
    public double getPerimeter() {
        return side1 + side2 + side3 + side4;
    }

    @Override
    public double getSquare() {
        double square = 0;
        for (int i = 0; i < vertices.size() - 1; i++) {

            Vertex p1 = vertices.get(i);
            Vertex p2 = vertices.get(i + 1);
            square += ((p1.getX() - p2.getX()) * (p1.getY() + p2.getY())) / 2;
        }

        Vertex p1 = vertices.get(vertices.size() - 1);
        Vertex p2 = vertices.get(0);
        square += ((p1.getX() - p2.getX()) * (p1.getY() + p2.getY())) / 2;

        return Math.abs(square);
    }

    @Override
    public QuadrilateralType getType() {

        if (hasIdenticalVertexes())
            return QuadrilateralType.NOT_QUADRILATERAL;


        if (side1 == side2 && side1 == side3 && side1 == side4 && diag1 == diag2)
            return QuadrilateralType.SQUARE;

        if (side1 == side3 && side2 == side4 && diag1 == diag2)
            return QuadrilateralType.RECTANGLE;

        if (side1 == side2 && side1 == side3 && side1 == side4)
            return QuadrilateralType.RHOMBUS;

        if (side1 == side3 && side2 == side4)
            return QuadrilateralType.PARALLELOGRAM;

        return QuadrilateralType.ARBITRARY;
    }

    private boolean hasIdenticalVertexes() {
        for (int i = 0; i < vertices.size(); i++)
            for (int j = 0; j < vertices.size(); j++) {
                if (i == j) continue;
                if (vertices.get(i).equals(vertices.get(j)))
                    return true;
            }
        return false;
    }
}
