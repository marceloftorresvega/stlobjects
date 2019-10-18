package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;
import java.util.HashSet;
import org.tensa.tensada.matrix.Dominio;
import org.tensa.tensada.matrix.DoubleMatriz;
import org.tensa.tensada.matrix.Indice;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author marcelo
 */
public class CuboSimple  implements IndexadoTriangular, PuntosAgrupados {

    private double ancho;
    private ArrayList<DoubleVector3DImpl> pointList;
    private ArrayList<ArrayList<Integer>> triangleIndexList;
    protected ArrayList<StarIndex> starIndexList;
    protected HashSet<Indice> lineIndexList;

    public CuboSimple(double ancho) {
        this.ancho = ancho;
        init();
        indexar();
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
        triangleIndexList = new ArrayList<>();
        lineIndexList = new HashSet<>();
        starIndexList = new ArrayList<>();
        
        lineIndexing();
        starIndexing();
        triangleIndexing();
        
        
    }
    
    private void starIndexing(){
        HashSet<Integer> starPoints = new HashSet<>();
        DoubleMatriz distanceMatriz = new DoubleMatriz(new Dominio(8, 8));
        double distanciaMaxima = Math.sqrt(3* ancho * ancho);
        
        for(int i =1; i<=8; i++){
            for(int j =1; j<=8; j++){
                if(i==j) continue;
                double distancia = pointList.get(i-1).distance(pointList.get(j-1));
                distanceMatriz.put(new Indice(i, j), distancia);
            }
        }
        
        for(int i = 1; i<=8; i++){
            StarIndex star = new StarIndex(i-1, 6);
            starPoints.add(i);
            for(int j =1; j<=8; j++){
                if(i==j) continue;
                
                if(starPoints.contains(j)){
                    continue;
                }
                Double distancia = distanceMatriz.get(new Indice(i, j));
                if(distanciaMaxima > distancia && distancia > 0){
                   star.add(j-1);
                }
            }
            starIndexList.add(star);
        }
    }
    
    private void lineIndexing(){
        Indice backLine;
        
        for(int i =0; i<3; i++){
            Indice line = new Indice(i, i + 1 );
            lineIndexList.add(line);
        }
        backLine = new Indice( 3, 0);
        lineIndexList.add(backLine);
        
        for(int i =4; i<7; i++){
            Indice line = new Indice(i, i + 1 );
            lineIndexList.add(line);
        }
        backLine = new Indice( 7, 4);
        lineIndexList.add(backLine);
        
        for(int i =0; i<4; i++){
            Indice line = new Indice(i, i + 4 );
            lineIndexList.add(line);
        }
        
        for(int i =0; i<3; i++){
            Indice line = new Indice(i, i + 5 );
            lineIndexList.add(line);
        }
        backLine = new Indice( 3, 4);
        lineIndexList.add(backLine);
        
        backLine = new Indice( 0, 2);
        lineIndexList.add(backLine);
        
        backLine = new Indice( 4, 6);
        lineIndexList.add(backLine);
        
        
    }
    
    private void triangleIndexing(){
        
        ArrayList<Integer> triangulo;
        
//        triangulo nulo
        triangulo = new ArrayList<>();
        triangulo.add(Integer.SIZE);
        
        for(int i = 0; i < 5 ; i++){
            int intermedio = i % 2;
            int cara = i % 4;
            
            if( intermedio == 0){
                triangulo.add(cara);
                triangulo.add( triangulo.get(0)); // cierra los triangulos, caso borde se libera
                triangulo = new ArrayList<>();
                triangleIndexList.add(triangulo);
            }
            
            triangulo.add(cara);
        }
        
//        triangulo nulo
        triangulo = new ArrayList<>();
        triangulo.add(Integer.SIZE);
        
        for(int i = 0; i < 5 ; i++){
            int intermedio = i % 2;
            int cara = i % 4 + 4;
            
            if( intermedio == 0){
                triangulo.add(cara);
                triangulo.add( triangulo.get(0)); // cierra los triangulos, caso borde se libera
                triangulo = new ArrayList<>();
                triangleIndexList.add(triangulo);
            }
            
            triangulo.add(cara);
        }
        
        for(int i = 0; i < 4 ; i++){
            
            triangulo = new ArrayList<>();
            triangulo.add(i);
            triangulo.add((i + 1) % 4 );
            triangulo.add(i + 4 );
            triangulo.add(i);
            triangleIndexList.add(triangulo);
        }
        
        for(int i = 4; i < 8 ; i++){
            
            triangulo = new ArrayList<>();
            triangulo.add(i);
            triangulo.add((i - 3) % 4 );
            triangulo.add((i + 1) % 4 + 4 );
            triangulo.add(i);
            triangleIndexList.add(triangulo);
        }
    }

    @Override
    public ArrayList<DoubleVector3DImpl> getPointList() {
        return pointList;
    }

    @Override
    public void init() {
        pointList = new ArrayList<>();
        
        for(int i = 0; i <2 ; i++){
                double alto = i==0? ancho /2: - ancho/2;
            for(double j=0; j < 4; j++){
                double fondo = ancho/2 * Math.sin( ( 2 * j + 1 ) * Math.PI / 4);
                double largo = ancho/2 * Math.cos( ( 2 * j + 1 ) * Math.PI / 4);
                DoubleVector3DImpl point = new DoubleVector3DImpl(largo, fondo, alto);
                pointList.add(point);
            }
        }
        
    }

    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

    @Override
    public HashSet<Indice> getLineIndexList() {
        return lineIndexList;
    }
    
}
