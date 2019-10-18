package org.tensa.stlobjects.soporte;

import org.tensa.stlobjects.poliedro.PuntosAgrupados;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 * traslacion rotacion
 * @author mtorres
 */
public class Atril {
    private DoubleVector3DImpl eje;
    private Double angulo;
    private DoubleVector3DImpl talla;
    private DoubleVector3DImpl lugar;
    
    private PuntosAgrupados nube;

    public DoubleVector3DImpl getEje() {
        return eje;
    }

    public void setEje(DoubleVector3DImpl eje) {
        this.eje = eje;
    }

    public Double getAngulo() {
        return angulo;
    }

    public void setAngulo(Double angulo) {
        this.angulo = angulo;
    }

    public DoubleVector3DImpl getLugar() {
        return lugar;
    }

    public void setLugar(DoubleVector3DImpl lugar) {
        this.lugar = lugar;
    }

    public PuntosAgrupados getNube() {
        return nube;
    }

    public void setNube(PuntosAgrupados nube) {
        this.nube = nube;
    }
    
    public void pointTo(DoubleVector3DImpl dest){
        
    }

    public DoubleVector3DImpl getTalla() {
        return talla;
    }

    public void setTalla(DoubleVector3DImpl talla) {
        this.talla = talla;
    }
    
}
