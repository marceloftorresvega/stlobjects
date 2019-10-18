package org.tensa.stlobjects.poliedro;

import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author admin
 */
public class EsferaGeodecica extends Icosaedro implements Frecuenciado, Radial, IndexadoTriangular, PuntosAgrupados {

    private Integer frecuencia;

    public EsferaGeodecica(Integer frecuencia, Integer radius) {
        super(radius);
        this.frecuencia = frecuencia;
        for (int i = 1; i < frecuencia;i++) {

            this.ajustaFrecuencia();
            this.equidistancia();
            super.indexar();
        }

    }

    @Override
    public Integer getFrecuencia() {
        return frecuencia;
    }

    @Override
    public void ajustaFrecuencia() {

        DoubleVector3DImpl[] points;
        points = (DoubleVector3DImpl[]) pointList.toArray(new DoubleVector3DImpl[1]);

        starIndexList.forEach(star -> {
            star.forEach(i -> {
                pointList.add((DoubleVector3DImpl)DoubleVector3DImpl.add(points[star.getCentro()], points[i]).escalar(0.5));

//                IntStream.range(1, frecuencia)
//                        .mapToDouble(Double::new)
//                        .forEach(f -> {
//                            pointList.add(DoubleVector3DImpl.add(
//                                    points[star.getCentro()].escalar( 1 - f / frecuencia.doubleValue()),
//                                    points[i].escalar(f / frecuencia.doubleValue())
//                            ));
//                        });
            });
        });
    }

    @Override
    public void equidistancia() {

        int n;
        DoubleVector3DImpl[] points;
        final DoubleVector3DImpl centroPoint = new DoubleVector3DImpl(0, 0, 0);
        points = (DoubleVector3DImpl[]) pointList.toArray(new DoubleVector3DImpl[1]);
        n = points.length;
//        System.out.println(points.length);
        for (int j = 0; j < n; j++) {
            double dist;
            dist = points[j].distance(centroPoint);
            pointList.remove(points[j]);
            pointList.add((DoubleVector3DImpl)points[j].escalar(radius / dist));
//            System.out.println(dist);

        }

    }

}
