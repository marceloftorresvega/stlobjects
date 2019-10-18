package org.tensa.stlobjects.codec;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.tensa.stlobjects.poliedro.StarIndex;
import org.tensa.stlobjects.poliedro.StlObject;
import org.tensa.tensada.matrix.DoubleMatriz;
import org.tensa.tensada.matrix.Indice;
import org.tensa.tensada.matrix.NumericMatriz;
import org.tensa.tensada.vector.Double3DVector;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author lorenzo
 */
public class StlCodec {

    /**
     *
     */
    private StlCodec() {
    }

    public static void asciiEnode(StlObject input, String filename) throws IOException{
        try( BufferedWriter bw = new BufferedWriter(new FileWriter(filename), 15000 *1024)){
            bw.write("solid ");
            bw.write(filename);
            bw.newLine();
                for( ArrayList<Integer> t : input.getTriangleIndexList() ){

                    DoubleMatriz p0 = DoubleMatriz.fromVector(input.getPointList().get(t.get(1)).diff(input.getPointList().get(t.get(0))));
                    DoubleMatriz p1 = DoubleMatriz.fromVector(input.getPointList().get(t.get(2)).diff(input.getPointList().get(t.get(0))));
                    NumericMatriz<Double> pcruz = p0.productoCruz(p1);
                    
                    Double3DVector normal = ((DoubleMatriz)pcruz).toVector().normal();
//                    Double3DVector normal = ((DoubleMatriz)magnitud.inversa().producto(pcruz)).toVector();
//                    Double3DVector normal = ((DoubleMatriz)pcruz.producto(magnitud.inversa())).toVector();
                    bw.write("facet normal ");
                    String normalFormatted = String.format("%f %f %f", 
                            (float)normal.getX(), 
                            (float)normal.getY(), 
                            (float)normal.getZ());                    
                    bw.write(normalFormatted);
                    bw.newLine();
                    
                    bw.write("outer loop");
                    bw.newLine();
                    int test =0;
                    for( Integer idx : t){
                        
                        DoubleVector3DImpl point = input.getPointList().get(idx);
                        if(idx.equals(t.get(0)) && test++==1){
                            continue;
                        }
                        bw.write("vertex ");
                        String vertexFormatted = String.format("%f %f %f", 
                                (float)point.getX(), 
                                (float)point.getY(), 
                                (float)point.getZ());                    
                        bw.write(vertexFormatted);
                        bw.newLine();
                    }
                    bw.write("endloop");
                    bw.newLine();


                    bw.write("endfacet");
                    bw.newLine();
                }
                
            
            bw.write("endsolid");
            bw.flush();
        } catch(IOException  ex){
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static StlObject asciiDecode(String filename) throws IOException {

        StlObject stlObject = new StlObject();

        List<DoubleVector3DImpl> shortVectorList;
        List<DoubleVector3DImpl> vectorList;
        Set<Indice> lineList;
        ArrayList<StarIndex> starList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> triangleList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename), 15000 * 1024)) {
            br.mark(100);
            System.out.println(br.readLine());
            br.reset();

            vectorList = br.lines()
                    .map(String::trim)
                    //                .filter( l -> l.startsWith("vertex") || l.startsWith("outer"))
                    .filter(l -> l.startsWith("vertex"))
                    //                .peek(l -> System.out.println(l))
                    .map(l -> l.split(" "))
                    .map((String[] vl) -> {

                        DoubleVector3DImpl v;
                        double[] coor = Arrays.stream(vl).skip(1)
                        .mapToDouble(Double::valueOf)
                        //                .peek(l -> System.out.println(l))
                        .toArray();

                        v = new DoubleVector3DImpl(coor[0], coor[1], coor[2]);
                        return v;
                    })
                    .collect(Collectors.toList());

            shortVectorList = vectorList.stream()
                    .distinct()
                    .collect(Collectors.toList());

            HashMap<DoubleVector3DImpl, Integer> shortVectorMap = new HashMap<>();
            for (int i = 0; i < shortVectorList.size(); i++) {
                shortVectorMap.put(shortVectorList.get(i), i);
            }

            ArrayList<Integer> triangle = null;

            for (int i = 0; i < vectorList.size(); i++) {
                int ciclo = i % 3;
                //            int cara = i / 3;

                if (ciclo == 0) {
                    if (Objects.nonNull(triangle)) {
                        triangle.add(triangle.get(0));

                    }
                    triangle = new ArrayList<>();
                    triangleList.add(triangle);
                }
                int tranceIndex = shortVectorMap.get(vectorList.get(i));
                triangle.add(tranceIndex);

            }
            triangle.add(triangle.get(0));

            lineList = triangleList.stream()
                    //                .peek(t -> System.out.println(t.size()))
                    .flatMap((ArrayList<Integer> t) -> IntStream.range(0, 3)
                            .mapToObj(i -> new Indice(t.get(i), t.get(i + 1)))
                    ).collect(Collectors.toSet());

            HashSet<Integer> starPoints = new HashSet<>();

            for (int i = 1; i <= shortVectorList.size(); i++) {
                StarIndex star = new StarIndex(i - 1, 6);
                starPoints.add(i);

                for (int j = 1; j <= shortVectorList.size(); j++) {
                    if (i == j) {
                        continue;
                    }

                    if (starPoints.contains(j)) {
                        continue;
                    }
                    boolean existe = lineList.contains(new Indice(i, j));
                    if (existe) {
                        star.add(j - 1);
                    }
                }
                starList.add(star);
            }

            lineList = lineList.stream()
                    .map( indice -> new Indice(indice.getFila() + 1, indice.getColumna() + 1))
                    .collect( Collectors.toSet());

            stlObject.getPointList().addAll(shortVectorList);
            stlObject.getLineIndexList().addAll(lineList);
            stlObject.getTriangleIndexList().addAll(triangleList);
            stlObject.getStarIndexList().addAll(starList);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        return stlObject;
    }

    public static StlObject hardBinaryDecode(String filename) throws IOException {
        
        byte[] headerPage = new byte[80];
        int triCount;
        float[] vectorPage = new float[12];
        short byteCount;

        ArrayList<DoubleVector3DImpl> vectorList = new ArrayList<>();

        StlObject stlObject = new StlObject();

        try (FileChannel fc = (FileChannel) Files
                .newByteChannel(Paths.get(filename), StandardOpenOption.READ)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            fc.read(byteBuffer);
            byteBuffer.flip();

            byteBuffer.get(headerPage);
            System.out.write(headerPage);
            System.out.println();
            triCount = byteBuffer.getInt();

            for (int i = 0; i < triCount; i++) {
                byteBuffer.asFloatBuffer().get(vectorPage);
                for (int k = 0; k < 12; k++) {
                    vectorPage[k] = (float) (Math.round(vectorPage[k] * 1000000.0) / 1000000.0);
                }

                for (int j = 0; j < 4; j++) {

                    DoubleVector3DImpl doubleVector3DImpl = new DoubleVector3DImpl(vectorPage[j * 3 + 0], vectorPage[j * 3 + 1], vectorPage[j * 3 + 2]);
//                    System.out.println(doubleVector3DImpl.toString());

                    if (j == 0) {
                        continue;
                    }
                    vectorList.add(doubleVector3DImpl);

                }
                byteBuffer.position(byteBuffer.position() + 48);
                byteCount = byteBuffer.getShort();
//                System.out.println(byteCount);
            }

        List<DoubleVector3DImpl> shortVectorList;
//        shortVectorList = vectorList;
        shortVectorList = vectorList.stream()
                //                .peek( v -> System.out.println(v))
                .distinct()
                .collect(Collectors.toList());

//        System.out.print(vectorList.size());
//        System.out.print(" - ");
//        System.out.println(shortVectorList.size());
        HashMap<DoubleVector3DImpl, Integer> shortVectorMap = new HashMap<>();
        for (int i = 0; i < shortVectorList.size(); i++) {
//        System.out.println(shortVectorList.get(i));
            shortVectorMap.put(shortVectorList.get(i), i);
        }
        System.out.println(shortVectorMap.size());

        ArrayList<ArrayList<Integer>> triangleList = new ArrayList<>();
        ArrayList<Integer> triangle = null;

        for (int i = 0; i < vectorList.size(); i++) {
            int ciclo = i % 3;
//            int cara = i / 3;

            if (ciclo == 0) {
                if (Objects.nonNull(triangle)) {
                    triangle.add(triangle.get(0));

                }
                triangle = new ArrayList<>();
                triangleList.add(triangle);
            }
            int tranceIndex = shortVectorMap.get(vectorList.get(i));
//            int tranceIndex = i;
            triangle.add(tranceIndex);

        }
        triangle.add(triangle.get(0));

        System.out.println(triangleList.size());

        Set<Indice> lineList = triangleList.stream()
                //                .peek(t -> System.out.println(t.size()))
                .flatMap((ArrayList<Integer> t) -> IntStream.range(0, 3)
                        .mapToObj(i -> new Indice(t.get(i), t.get(i + 1)))
                )
                //                .peek(t -> System.out.println(vectorList.get(t.getFila())))
                //                .peek(t -> System.out.println(vectorList.get(t.getColumna())))
                .collect(Collectors.toSet());

        ArrayList<StarIndex> starList = new ArrayList<>();
        HashSet<Integer> starPoints = new HashSet<>();

        
            lineList = lineList.stream()
                    .map( indice -> new Indice(indice.getFila() + 1, indice.getColumna() + 1))
                    .collect( Collectors.toSet());
            
        stlObject.getPointList().addAll(shortVectorList);
        stlObject.getLineIndexList().addAll(lineList);
        stlObject.getTriangleIndexList().addAll(triangleList);
        stlObject.getStarIndexList().addAll(starList);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return stlObject;
    }

    public static void binaryEncode(StlObject input, String filename) throws IOException {
        
         try( FileOutputStream fos = new FileOutputStream(filename) )   {
             
            ByteBuffer headBuffer = ByteBuffer.allocate(84);
            headBuffer.order(ByteOrder.LITTLE_ENDIAN);
            
            ByteBuffer triangleBuffer = ByteBuffer.allocate(50);
            triangleBuffer.order(ByteOrder.LITTLE_ENDIAN);
            
            headBuffer.put(filename.getBytes(Charset.forName("UTF-8")), 0, 80);
            headBuffer.putInt(input.getTriangleIndexList().size());
            headBuffer.flip();
            fos.write(headBuffer.array());
            
            for( ArrayList<Integer> t : input.getTriangleIndexList() ){
                
                    DoubleMatriz p0 = DoubleMatriz.fromVector(input.getPointList().get(t.get(1)).diff(input.getPointList().get(t.get(0))));
                    DoubleMatriz p1 = DoubleMatriz.fromVector(input.getPointList().get(t.get(2)).diff(input.getPointList().get(t.get(0))));
                    NumericMatriz<Double> pcruz = p0.productoCruz(p1);
                    
                    Double3DVector normal = ((DoubleMatriz)pcruz).toVector().normal();
                    
                    triangleBuffer.clear();
                    triangleBuffer.putFloat((float)normal.getX());
                    triangleBuffer.putFloat((float)normal.getY());
                    triangleBuffer.putFloat((float)normal.getZ());
                    
                    int test = 0;
                    for( Integer idx : t){
                        
                        DoubleVector3DImpl point = input.getPointList().get(idx);
                        if(idx.equals(t.get(0)) && test++==1){
                            continue;
                        }
                        
                        triangleBuffer.putFloat((float)point.getX());
                        triangleBuffer.putFloat((float)point.getY());
                        triangleBuffer.putFloat((float)point.getZ());
                    }
                    triangleBuffer.putShort((short)0);
                    triangleBuffer.flip();
                    fos.write(triangleBuffer.array());
            }
             
         } catch (FileNotFoundException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static StlObject binaryDecode(String fileName) throws IOException {

        Set<Indice> lineList;
        List<DoubleVector3DImpl> shortVectorList;
        ArrayList<ArrayList<Integer>> triangleList;
        ArrayList<StarIndex> starList = new ArrayList<>();
        StlObject stlObject = new StlObject();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName), 15000 * 1024)) {

            byte[] header = new byte[80];
            byte[] count = new byte[4];
            byte[] trianglePage = new byte[50];

            bis.read(header);
            String stringHeader = new String(header);
            System.out.println(stringHeader);
            bis.read(count);
            ByteBuffer countBuffer = ByteBuffer.wrap(count);
            countBuffer.order(ByteOrder.LITTLE_ENDIAN);

            long numTriangles = countBuffer.getInt();
            System.out.println(numTriangles);
            ArrayList<DoubleVector3DImpl> vectorList = new ArrayList<>();

            ByteBuffer buffer = ByteBuffer.allocate(50);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < numTriangles; i++) {
//            bis.skip(12); // normal o sheader property
                bis.read(trianglePage);
                buffer.put(trianglePage);
                buffer.flip();

                float[] vectorPage = new float[12];
                buffer.asFloatBuffer().get(vectorPage);
//                System.out.print("> ");
                for (int j = 0; j < 4; j++) {

                    DoubleVector3DImpl doubleVector3DImpl = new DoubleVector3DImpl(vectorPage[j * 3 + 0], vectorPage[j * 3 + 1], vectorPage[j * 3 + 2]);
//                    System.out.println(doubleVector3DImpl.toString());
                    if (j == 0) {
                        continue;
                    }
                    vectorList.add(doubleVector3DImpl);

                }
//            bis.skip(2); //stl color data o byte count
//                buffer.position(48);
//                short test = buffer.getShort();
                buffer.clear();
//                System.out.println(test);
            }

            shortVectorList = vectorList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            System.out.print(vectorList.size());
            System.out.print(" - ");
            System.out.println(shortVectorList.size());
            HashMap<DoubleVector3DImpl, Integer> shortVectorMap = new HashMap<>();
            for (int i = 0; i < shortVectorList.size(); i++) {
                shortVectorMap.put(shortVectorList.get(i), i);
            }

            triangleList = new ArrayList<>();
            ArrayList<Integer> triangle = null;

            for (int i = 0; i < vectorList.size(); i++) {
                int ciclo = i % 3;
//            int cara = i / 3;

                if (ciclo == 0) {
                    if (Objects.nonNull(triangle)) {
                        triangle.add(triangle.get(0));

                    }
                    triangle = new ArrayList<>();
                    triangleList.add(triangle);
                }
                int tranceIndex = shortVectorMap.get(vectorList.get(i));
                triangle.add(tranceIndex);

            }
            triangle.add(triangle.get(0));

            lineList = triangleList.stream()
                    //                .peek(t -> System.out.println(t.size()))
                    .flatMap((ArrayList<Integer> t) -> IntStream.range(0, 3)
                            .mapToObj(i -> new Indice(t.get(i), t.get(i + 1)))
                    ).collect(Collectors.toSet());

            HashSet<Integer> starPoints = new HashSet<>();

            for (int i = 1; i <= shortVectorList.size(); i++) {
                StarIndex star = new StarIndex(i - 1, 6);
                starPoints.add(i);

                for (int j = 1; j <= shortVectorList.size(); j++) {
                    if (i == j) {
                        continue;
                    }

                    if (starPoints.contains(j)) {
                        continue;
                    }
                    boolean existe = lineList.contains(new Indice(i, j));
                    if (existe) {
                        star.add(j - 1);
                    }
                }
                starList.add(star);
            }
            
            lineList = lineList.stream()
                    .map( indice -> new Indice(indice.getFila() + 1, indice.getColumna() + 1))
                    .collect( Collectors.toSet());

            stlObject.getPointList().addAll(shortVectorList);
            stlObject.getLineIndexList().addAll(lineList);
            stlObject.getTriangleIndexList().addAll(triangleList);
            stlObject.getStarIndexList().addAll(starList);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        return stlObject;
    }
}
