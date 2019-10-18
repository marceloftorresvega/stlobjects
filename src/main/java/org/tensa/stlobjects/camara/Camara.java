package org.tensa.stlobjects.camara;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.IntStream;
import org.tensa.stlobjects.poliedro.IndexadoTriangular;
import org.tensa.stlobjects.poliedro.PuntosAgrupados;
import org.tensa.stlobjects.poliedro.StarIndex;
import org.tensa.tensada.matrix.Indice;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author mtorres
 */
public class Camara implements IndexadoTriangular, PuntosAgrupados{
    private ArrayList<DoubleVector3DImpl> pointList;
    private ArrayList<StarIndex> starIndexList;
    private ArrayList<ArrayList<Integer>> triangleIndexList;
    protected HashSet<Indice> lineIndexList;
    private Double horizonte;

    public Camara(Double horizonte) {
        this.horizonte = horizonte;
        this.init();
        this.indexar();
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
        StarIndex si;
        starIndexList.add(si =new StarIndex(0, 1));
        IntStream.rangeClosed(1, 5).forEach((i) -> si.add(i));
        IntStream.rangeClosed(1, 6).forEach((i) -> lineIndexList.add( new Indice(i, 1)));
        
                
    }

    @Override
    public ArrayList<DoubleVector3DImpl> getPointList() {
        return pointList;
    }

    @Override
    public void init() {
        pointList = new ArrayList<>(6);
        starIndexList = new ArrayList<>(1);
        triangleIndexList = new ArrayList<>();
        lineIndexList = new HashSet<>();
        
        pointList.add(new DoubleVector3DImpl(0, 0, 0));
        pointList.add(new DoubleVector3DImpl(10, 0, 0));
        pointList.add(new DoubleVector3DImpl(-10, 0, 0));
        pointList.add(new DoubleVector3DImpl(0, 10, 0));
        pointList.add(new DoubleVector3DImpl(0, -10, 0));
        pointList.add(new DoubleVector3DImpl(0, 0, 10));
    }

    public Double getHorizonte() {
        return horizonte;
    }

    @Override
    public HashSet<Indice> getLineIndexList() {
        return lineIndexList;
    }
    
}
