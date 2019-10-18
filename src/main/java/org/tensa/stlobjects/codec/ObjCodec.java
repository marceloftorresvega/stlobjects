package org.tensa.stlobjects.codec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.tensa.stlobjects.poliedro.StarIndex;
import org.tensa.stlobjects.poliedro.StlObject;
import org.tensa.tensada.matrix.Indice;
import org.tensa.tensada.vector.impl.DoubleVector3DImpl;

/**
 *
 * @author lorenzo
 */
public class ObjCodec {
    
    public static void asciiEnode(StlObject input, String filename) throws IOException{
        
        try( BufferedWriter bw = new BufferedWriter(new FileWriter(filename), 15000 *1024)){
            bw.write("o ");
            bw.write(filename);
            bw.newLine();
            
            for (DoubleVector3DImpl pointList : input.getPointList()) {
                String vertexFormatted = String.format("v %f %f %f", 
                                (float)pointList.getX(), 
                                (float)pointList.getY(), 
                                (float)pointList.getZ());                    
                bw.write(vertexFormatted);
                bw.newLine();
            }
            
            for (ArrayList<Integer> triangleIndex : input.getTriangleIndexList()) {
                String indexFormatted = String.format("f %d %d %d",
                        triangleIndex.get(0),
                        triangleIndex.get(1),
                        triangleIndex.get(2));                
                bw.write(indexFormatted);
                bw.newLine();
            }
            
            bw.flush();
        } catch(IOException  ex){
            Logger.getLogger(StlCodec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static StlObject asciiDecode(String filename) throws IOException {
        
        StlObject stlObject = new StlObject();

        List<DoubleVector3DImpl> shortVectorList;
        Set<Indice> lineList;
        ArrayList<StarIndex> starList = new ArrayList<>();
        List<ArrayList<Integer>> triangleList ;

        try (BufferedReader br = new BufferedReader(new FileReader(filename), 15000 * 1024)){
         
            br.mark(100);
            System.out.println(br.readLine());
            br.reset();

            
            Map<Boolean, List<String[]>> instructionList = br.lines()
                    .map(String::trim)
                    //                .filter( l -> l.startsWith("vertex") || l.startsWith("outer"))
                    .filter(l -> l.startsWith("v ") || l.startsWith("f "))
//                                    .peek(l -> System.out.println(l))
                    .map(l -> l.split("\\s+"))
                    .collect( Collectors.partitioningBy(instruction -> "v".equalsIgnoreCase(instruction[0])));
            
            shortVectorList = instructionList.get(true).stream()
                    .map((String[] vl) -> {

                        DoubleVector3DImpl v;
                        double[] coor = Arrays.stream(vl).skip(1)
                                .mapToDouble(Double::valueOf)
//                                                .peek(l -> System.out.println(l))
                                .toArray();
                        
                        v = new DoubleVector3DImpl(coor[0], coor[1], coor[2]);
                        return v;
                    })
                    .collect(Collectors.toList());
            
            triangleList = instructionList.get(false).stream()
                    .flatMap((String[] index) -> {
                        ArrayList<ArrayList<Integer>> related = new ArrayList();
                        ArrayList<Integer> triangle;
//                        System.out.println(index[0]);
                        String punta = index[1];
                        for(int i=2;i < index.length-1;i++){
                            triangle = new ArrayList(4);
                            triangle.add(getIntIndex(punta));
                            for(int j=0;j<2;j++){
                                Integer indice = getIntIndex(index[i+j]);
                                triangle.add(indice);
                            }
                            triangle.add(getIntIndex(punta));
                            related.add(triangle);
                        }
                        return related.stream();
                    })
                    .collect(Collectors.toList());
//                    .collect(Collectors.collectingAndThen(Collectors.toList(), triangleList::addAll));
            
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
    
    private static Integer getIntIndex(String index){
        String soloIdx = index.replaceFirst("/.+", "");
        return Integer.valueOf(soloIdx) -1 ;
    }
}
