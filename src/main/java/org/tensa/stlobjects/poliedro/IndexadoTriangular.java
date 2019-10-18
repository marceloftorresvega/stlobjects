package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;
import java.util.HashSet;
import org.tensa.tensada.matrix.Indice;

/**
 *
 * @author mtorres
 */
public interface IndexadoTriangular {

    ArrayList<StarIndex> getStarIndexList();

    ArrayList<ArrayList<Integer>> getTriangleIndexList();
    
    HashSet<Indice> getLineIndexList();

    void indexar();
    
}
