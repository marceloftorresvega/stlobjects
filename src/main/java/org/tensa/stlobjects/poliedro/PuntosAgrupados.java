package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author mtorres
 */
public interface PuntosAgrupados {

    ArrayList<DoubleVector3DImpl> getPointList();

    void init();
    
}
