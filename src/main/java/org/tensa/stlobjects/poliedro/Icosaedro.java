package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.tensa.tensada.matrix.Dominio;
import org.tensa.tensada.matrix.DoubleMatriz;
import org.tensa.tensada.matrix.Indice;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author mtorres
 */
public class Icosaedro implements Radial, IndexadoTriangular, PuntosAgrupados {

    protected ArrayList<DoubleVector3DImpl> pointList;
    protected ArrayList<ArrayList<Integer>> triangleIndexList;
    protected ArrayList<StarIndex> starIndexList;
    protected HashSet<Indice> lineIndexList;
    protected Integer radius;

    public Icosaedro(Integer radius) {
        this.radius = radius;
        this.init();
        this.indexar();
    }

    @Override
    public void init() {

        pointList = new ArrayList<>();

        double x;
        double y;

        double a = radius * 4 / Math.sqrt((10 + 2 * Math.sqrt(5)));
        double r = a / 2 / Math.sin(java.lang.Math.PI / 5);
        double h = Math.sqrt(a * a - r * r);

        for (int i = 0; i < 5; i++) {
            x = java.lang.Math.cos(i * java.lang.Math.PI / 2.5) * r;
            y = java.lang.Math.sin(i * java.lang.Math.PI / 2.5) * r;
            pointList.add(new DoubleVector3DImpl(x, y, radius - h));
            x = java.lang.Math.cos((i + 0.5) * java.lang.Math.PI / 2.5) * r;
            y = java.lang.Math.sin((i + 0.5) * java.lang.Math.PI / 2.5) * r;
            pointList.add(new DoubleVector3DImpl(x, y, h - radius));
        }
        pointList.add(new DoubleVector3DImpl(0, 0, radius));
        pointList.add(new DoubleVector3DImpl(0, 0, -radius));
    }

    @Override
    public void indexar() {
        int n;
        DoubleVector3DImpl[] points;

        points = (DoubleVector3DImpl[]) pointList.toArray(new DoubleVector3DImpl[1]);
        n = points.length;

        final int hexaSize = 7;
        final int baseSize = 5;
        final int pentaSize = 6;

        HashSet<Indice> alineado = new HashSet<>();

        triangleIndexList = new ArrayList<>();
        starIndexList = new ArrayList<>();
        lineIndexList = new HashSet<>();

        DoubleMatriz distanceMatrix = new DoubleMatriz(new Dominio(n, n));

        distanceMatrix.getDominio().stream()
                .forEach(index -> {
                    distanceMatrix.put(index, points[index.getFila() - 1].distanceSq(points[index.getColumna() - 1]));
                });

        IntStream.rangeClosed(1, n).boxed()
                .forEach((Integer j) -> {
                    double[] orden = IntStream.rangeClosed(1, n)
                            .mapToObj(i -> new Indice(i, j))
                            .mapToDouble(distanceMatrix::get)
                            .sorted()
                            .toArray();

                    double media = Arrays.stream(orden, 1, baseSize).average().getAsDouble();
                    double contraMedia = Arrays.stream(orden, 1, pentaSize).average().getAsDouble();

                    int cupoLimite = (Math.abs(media - contraMedia) > 1 ? hexaSize : pentaSize);

                    HashSet<Integer> probado;
                    probado = new HashSet<>(hexaSize);

                    StarIndex starIdx;
                    starIndexList.add(starIdx = new StarIndex(j - 1, hexaSize));
                    IntStream.range(1, cupoLimite)
                            .forEach(cupo -> {
                                probado.addAll(
                                        IntStream.range(0, n).boxed()
                                                .filter(i -> !probado.contains(i))
                                                .filter(i -> orden[cupo] == distanceMatrix.get(new Indice(i + 1, j)))
                                                .collect(Collectors.toSet())
                                );

                            });

                    probado.stream()
                            .filter(i -> !alineado.contains(new Indice(j, i + 1)))
                            .forEach(i -> {
                                alineado.add(new Indice(i + 1, j));
                                starIdx.add(i);
                                lineIndexList.add(new Indice(i + 1, j));
                            });

                    HashSet<Integer> agregado = new HashSet<>();
                    probado.stream()
                            .flatMap((i) -> {
                                agregado.add(i);

                                Stream<ArrayList<Integer>> triangulos = probado.stream()
                                        .filter(i1 -> !agregado.contains(i1))
                                        .collect(Collectors.groupingBy((i1) -> {
                                            return distanceMatrix.get(new Indice(i + 1, i1 + 1));
                                        }, Collectors.toList()))
                                        .entrySet().stream()
                                        .min((e, e1) -> {

                                            return (int) (e.getKey() - e1.getKey());
                                        })
                                        .map(e -> e.getValue())
                                        .map(List::stream)
                                        .orElse(Stream.empty())
                                        .map(i1 -> {
                                            ArrayList<Integer> triangle = new ArrayList();

                                            triangle.add(j - 1);
                                            triangle.add(i);
                                            triangle.add(i1);
                                            triangle.add(j - 1);

                                            agregado.add(i1);
                                            return triangle;
                                        });

                                return triangulos;
                            })
                            .forEach(triangle -> triangleIndexList.add(triangle));

                });
    }

    @Override
    public ArrayList<DoubleVector3DImpl> getPointList() {
        return pointList;
    }

    @Override
    public ArrayList<ArrayList<Integer>> getTriangleIndexList() {
        return triangleIndexList;
    }

    @Override
    public ArrayList<StarIndex> getStarIndexList() {
        return starIndexList;
    }

    @Override
    public Integer getRadius() {
        return radius;
    }

    @Override
    public HashSet<Indice> getLineIndexList() {
        return lineIndexList;
    }

}
