package com.company;

//10. Определить класс Четырехугольник на плоскости, вершины
//которого имеют тип Точка. Определить площадь и периметр
//четырехугольника. Создать массив/список/множество объектов и
//подсчитать количество четырехугольников разного типа (квадрат,
//прямоугольник, ромб, произвольный). Определить для каждой группы
//наибольший и наименьший по площади (периметру) объект.

import com.company.model.Vertex;
import com.company.shapes.Quadrilateral;
import com.company.shapes.QuadrilateralType;
import com.company.shapes.Shape;

import java.util.*;

public class Main {

    private List<Shape> quadrilaterals;

    private void fillQuadrilateralsList() {
        quadrilaterals = new ArrayList<>();
        quadrilaterals.add(new Quadrilateral(new Vertex(1, 1), new Vertex(1, 4), new Vertex(7, 4), new Vertex(7, 1)));
        quadrilaterals.add(new Quadrilateral(new Vertex(1, 1), new Vertex(2, 1), new Vertex(2, 2), new Vertex(1, 2)));
        quadrilaterals.add(new Quadrilateral(new Vertex(3, 4), new Vertex(5, 7), new Vertex(1, 1), new Vertex(1, 2)));
        quadrilaterals.add(new Quadrilateral(new Vertex(0, 0), new Vertex(0, 5), new Vertex(5, 5), new Vertex(5, 0)));
        quadrilaterals.add(new Quadrilateral(new Vertex(1, 1), new Vertex(2, 4), new Vertex(5, 4), new Vertex(4, 1)));
        quadrilaterals.add(new Quadrilateral(new Vertex(0, 0), new Vertex(1, 5), new Vertex(4, 7), new Vertex(4, 2)));
        quadrilaterals.add(new Quadrilateral(new Vertex(0, 0), new Vertex(1, 5), new Vertex(4, 7), new Vertex(4, 2)));
        quadrilaterals.add(new Quadrilateral(new Vertex(4, 2), new Vertex(2, 6), new Vertex(6, 8), new Vertex(8, 4)));
        quadrilaterals.add(new Quadrilateral(new Vertex(4, 2), new Vertex(2, 6), new Vertex(6, 8), new Vertex(8, 4)));
        quadrilaterals.add(new Quadrilateral(new Vertex(-1, 0), new Vertex(0, 1), new Vertex(1, 0), new Vertex(0, -1)));
        quadrilaterals.add(new Quadrilateral(new Vertex(-3, 0), new Vertex(0, 3), new Vertex(3, 0), new Vertex(0, -3)));
        quadrilaterals.add(new Quadrilateral(new Vertex(0, 0), new Vertex(10, 3), new Vertex(15, 0), new Vertex(8, -3)));
        quadrilaterals.add(new Quadrilateral(new Vertex(0, 0), new Vertex(0, 0), new Vertex(15, 0), new Vertex(8, -3)));
        quadrilaterals.add(new Quadrilateral(new Vertex(1, 2), new Vertex(2, 4), new Vertex(5, 4), new Vertex(4, 1)));
        quadrilaterals.add(new Quadrilateral(new Vertex(5, 0), new Vertex(0, 0), new Vertex(0, 20), new Vertex(5, 20)));

    }

    private Map<QuadrilateralType, List<Shape>> splitByType() {
        List<Shape> quadrilaterals = this.quadrilaterals;

        Map<QuadrilateralType, List<Shape>> groups = new HashMap<>();

        for (Shape shape : quadrilaterals) {
            if (!groups.containsKey(shape.getType())) {
                List<Shape> group = new ArrayList<>();
                group.add(shape);
                groups.put(shape.getType(), group);
            } else {
                groups.get(shape.getType()).add(shape);
            }
        }
        return groups;
    }

    private void showQuadrilateralsInfo(){
        fillQuadrilateralsList();
        for (Map.Entry<QuadrilateralType, List<Shape>> shapes : splitByType().entrySet()) {

            double maxS = 0;
            double minS = Double.MAX_VALUE;
            double maxP = 0;
            double minP = Double.MAX_VALUE;
            for (Shape shape: shapes.getValue()){
                double shapeS = shape.getSquare();
                double shapeP = shape.getPerimeter();
                if(maxS < shapeS)
                    maxS = shapeS;
                if(minS > shapeS)
                    minS = shapeS;
                if(maxP < shapeP)
                    maxP = shapeP;
                if(minP > shapeP)
                    minP = shapeP;
            }

            System.out.printf("Type: %s %nCount: %d %nmaxSquare: %.3f %nminSquare: %.3f %nmaxPerimeter: %.3f %nminPerimeter: %.3f %n", shapes.getKey(), shapes.getValue().size(), maxS,
                    minS, maxP, minP);
            System.out.println(shapes.getValue() + "\n");

        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.showQuadrilateralsInfo();
    }
}
