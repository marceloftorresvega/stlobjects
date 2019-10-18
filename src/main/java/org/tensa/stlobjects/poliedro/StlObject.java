/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;
import java.util.HashSet;
import org.tensa.tensada.matrix.Indice;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author lorenzo
 */
public class StlObject implements IndexadoTriangular, PuntosAgrupados{

    private ArrayList<StarIndex>  starIndexList;
    private ArrayList<ArrayList<Integer>> triangleIndexList;
    private HashSet<Indice> lineIndexList;
    private ArrayList<DoubleVector3DImpl> pointList;

    public StlObject() {
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
    public HashSet<Indice> getLineIndexList() {
        return lineIndexList;
    }

    @Override
    public void indexar() {
        starIndexList = new ArrayList<>();
        triangleIndexList = new ArrayList<>();
        lineIndexList = new HashSet<>();
        
    }
    
    @Override
    public ArrayList<DoubleVector3DImpl> getPointList() {
        return pointList;
    }

    @Override
    public void init() {
        pointList =  new ArrayList<>();
                
    }
    
}
