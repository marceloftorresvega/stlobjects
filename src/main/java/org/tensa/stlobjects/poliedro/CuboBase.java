/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;
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
 * @author lorenzo
 */
public class CuboBase implements IndexadoTriangular, PuntosAgrupados {

    private double ancho;
    private ArrayList<DoubleVector3DImpl> pointList;
    private ArrayList<ArrayList<Integer>> triangleIndexList;
    protected ArrayList<StarIndex> starIndexList;
    protected HashSet<Indice> lineIndexList;

    public CuboBase(double ancho) {
        this.ancho = ancho;
        this.init();
        this.indexar();
    }

    @Override
    public ArrayList<DoubleVector3DImpl> getPointList() {
        return pointList;
    }

    @Override
    public void init() {
        pointList = (ArrayList) IntStream.range(0, 15)
                .filter(idx -> idx != 9)
                .boxed()
                .map(idx -> {
                    double x = ancho / 2.0 * (idx % 5 == 4 ? 0.0: 1.0) * (((idx / 5) != 1) ? ((double) (idx % 5 / 2) * 2.0 - 1.0) : ((double) (idx % 3 / 2) * 2.0 - 1.0) * ((idx % 5 / 2 == 1)? 0.0:1.0) );
                    double y = ancho / 2.0 * (idx % 5 == 4 ? 0.0: 1.0) * (((idx / 5) != 1) ? ((double) (idx % 5 % 2) * 2.0 - 1.0) : ((double) (idx % 3 % 2) * 2.0 - 1.0) * ((idx % 5 / 2 == 0)? 0.0:1.0) );
                    double z = ancho / 2.0 * ((double) (idx / 5) - 1.0);
                    
//                    System.out.print( x + ", ");
//                    System.out.print( y + ", ");
//                    System.out.println(z +".");
                    return new DoubleVector3DImpl(x, y, z);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ArrayList<StarIndex> getStarIndexList() {
        return starIndexList;
    }

    @Override
    public ArrayList<ArrayList<Integer>> getTriangleIndexList() {
        return triangleIndexList;
    }

    @Override
    public void indexar() {

        int n;
        DoubleVector3DImpl[] points;

        points = (DoubleVector3DImpl[]) pointList.toArray(new DoubleVector3DImpl[1]);
        n = points.length;
        
        double sqr2 = Math.sqrt(2);

        triangleIndexList = new ArrayList<>();
        starIndexList = new ArrayList<>();
        lineIndexList = new HashSet<>();

        DoubleMatriz distanceMatrix = new DoubleMatriz(new Dominio(n, n));

        distanceMatrix.getDominio().stream()
                .forEach(index -> {
                    distanceMatrix.put(index, points[index.getFila() - 1].distance(points[index.getColumna() - 1]));
                });
        HashSet<Indice> agregados = new HashSet();

        IntStream.rangeClosed(1, n)
//                .filter(index -> !agregados.contains(index))
                .forEach((j) -> {
                    final List<Indice> cupos = IntStream.rangeClosed(1, n)
                    .filter(i -> i != j)
                    .mapToObj(i -> new Indice(i, j))
//                    .filter(index2 -> !agregados.contains(index2))
                    .filter(index2 -> distanceMatrix.get(index2) <= ancho )
                    .sorted((Indice index2a, Indice index2b) -> {
                        Double d1 = distanceMatrix.get(index2a);
                        Double d2 = distanceMatrix.get(index2b);
                        return Double.compare(d1, d2);

                    }).collect(
//                            Collectors.collectingAndThen(
//                                    Collectors.toList(),
//                                    l -> l.size()>8?l.subList(0, 8):l));
//                            Collectors.collectingAndThen(
//                                    Collectors.toList(),
//                                    l -> l.size()>6?l.subList(0, 6):l));
                    Collectors.toList());

                    double total = cupos.stream()
//                            .peek(i -> System.out.print(i.getFila() + ", "+ i.getColumna() + " - "))
                        .mapToDouble(distanceMatrix::get)
//                            .peek(d -> System.out.println(d))
                        .sum();
//                    System.out.println(total +" " + 8.5 * ancho);
//                    System.out.println( "total " + total );
//                    
//                    System.out.println(
//                            Math.abs(total - 8 *  sqr2 * ancho ) < 0.0000000000001 ? " cuz extendida" :
//                            Math.abs(total - 6 *  sqr2 * ancho ) < 0.0000000000001 ? " cruz acotada a 6" :
////                            Math.abs(total - 4 * sqr2 * ancho ) < 0.0000000000001  ? " cruz acotada a 4" :
//                            Math.abs(total - ( 1+ 4 * sqr2) * ancho ) < 0.0000000000001  ? " cruz acotada a 4" :
//                            Math.abs(total - 3 * ( 1+ sqr2 / 2 ) * ancho ) < 0.0000000000001 ? " maximo esquina " :
//                            Math.abs(total - 2 * ( 1+ sqr2 ) * ancho ) < 0.0000000000001 ? " recuadro interno ": 
//                            Math.abs(total - 2 * sqr2 * ancho ) < 0.0000000000001 ? " cruz": "desconocido");
//                    System.out.println(total / 6 );
//                    System.out.println(cupos.size());
                    
//                    System.out.println("disminuir tamaño?");
//                    if( (cupos.size() == 6 && (total - 3 * ( 1+ sqr2/2) * ancho < 0.0000000000001 ) ){**
//                    if( (cupos.size() == 6 && (total >= 3 * ( 1+ sqr2/2) * ancho &&  total <  4 * sqr2 * ancho) ){**
//                    if( (cupos.size() == 6 && (total > 6 *  sqr2 * ancho ||  total <  3 * ( 1+ sqr2/2) * ancho )) ){
//                    System.out.println("SI.");
////                        cupos.removeAll( cupos.subList(4, 5));
//                        cupos.subList(4, 6).clear();
////                        cupos.clear();
//                    }
//                    System.out.println(cupos.size());
                    
//                    System.out.println("disminuir tamaño?");
                    if( cupos.size() == 9 && Math.abs(total - ( 1 + 4 * sqr2) * ancho) < 0.0000000000001 ){
//                    System.out.println("SI.");
//                        cupos.clear();
                        cupos.removeIf( idx -> {
                                        DoubleVector3DImpl point = points[idx.getFila() - 1];
                                        return point.getX() == 0.0 || point.getY() == 0.0 || point.getZ() == 0.0;
                                        } );
                        
                    }
                    if( cupos.size() == 6){
//                    System.out.println("SI.");
                        cupos.clear();
                    }
//                    System.out.println(cupos.size());
                    
//                    System.out.println("remover agregados...");
                    cupos.removeAll(agregados);
//                    System.out.println(cupos.size());
                    
                    StarIndex current;
                    starIndexList.add( current = new StarIndex(j -1, cupos.size()));
                    
                    cupos.stream()
                            .filter( index2 -> !agregados.contains(index2))
                            .map(index2 -> index2.getFila() -1 )
                            .collect( Collectors.collectingAndThen(Collectors.toList(), current::addAll));
                    
                    cupos.stream()
                            .filter( index2 -> !agregados.contains(index2))
                            .collect(Collectors.collectingAndThen(Collectors.toList(), lineIndexList::addAll));
                    
                    cupos.forEach( index2 ->{
//                        agregados.add(index2);
                        Indice transpuesto = new Indice(index2.getColumna(), index2.getFila());
                        agregados.add(transpuesto);
                    });
                    
                    HashSet<Integer> usado = new HashSet<>();
                    
                    cupos.stream()
                            .map(Indice::getFila)
                            .peek(usado::add)
                            .flatMap(i -> {
                                return cupos.stream()
                                        .map(Indice::getFila)
                                        .filter(k -> ! usado.contains(k))
                                        .collect(Collectors.groupingBy(k -> (Double)distanceMatrix.get( new Indice(i, k))))
                                        .entrySet().stream()
                                        .min( (e1, e2 ) -> Double.compare(e1.getKey(), e2.getKey()))
                                        .map( e -> e.getValue())
                                        .map(List::stream)
                                        .orElse(Stream.empty())
//                                        .peek(usado::add)
                                        .map(k -> {
                                            ArrayList<Integer> triangle = new ArrayList<>();
                                            triangle.add(j - 1);
                                            triangle.add(i - 1);
                                            triangle.add(k - 1);
                                            triangle.add(j - 1);
//                                            System.out.print( j + ", ");
//                                            System.out.print( i + ", ");
//                                            System.out.println( k + ".");
                                            return triangle;
                                        });
                            }).collect(Collectors.collectingAndThen(
                                    Collectors.toList(), 
                                    triangleIndexList::addAll));
                    
                });

    }

    @Override
    public HashSet<Indice> getLineIndexList() {
        return lineIndexList;
    }

}
