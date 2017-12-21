import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class NewClass {
    
    private static List<String> readWords( String str ) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(str));
        String oneLine;
        List<String> lst = new ArrayList<>( );

        while( ( oneLine = in.readLine( ) ) != null ) {
            lst.add(oneLine);
        }

        return lst;
    }
    
    private static void writeWords( List<String> lst, String filename ) throws IOException {
            
        BufferedWriter out = new BufferedWriter(new FileWriter(new File(filename)));

        for (String oneLine : lst) {
           out.write(oneLine);
           out.newLine();
        }
        out.close();
    }

    private List<String> getcultures() {
        File folder = new File("history/characters");
        File[] list = folder.listFiles();
        List<String> cultures = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            cultures.add(list[i].getName());
        }
        return cultures;
    }
    
    private Map<String, Integer> updateTraits(Map<String, Integer> m, String filename) throws IOException {
        List<String> list = readWords(filename);
        int bracket = 0;
        String trait = "";
        Integer value = 0;
        for (String line : list) {
            if (!line.startsWith("#")) {
                if (bracket == 0 && line.contains("=")) {
                    trait = line.split("=")[0].trim();
                    value = 0;
                }
                if (line.trim().startsWith("diplomacy =") || line.trim().startsWith("martial =") || line.trim().startsWith("stewardship =") || line.trim().startsWith("intrigue =") || line.trim().startsWith("learning =")) {
                    value = value + Integer.valueOf(line.split("=")[1].trim());
                    m.put(trait, value);
                }
                if (line.contains("{")) {
                    bracket++;
                }
                if (line.contains("}")) {
                    bracket--;
                }
            }
        }
        return m;
    }

    private Map<String, Integer> getTraits() throws IOException {
        Map<String,Integer> traits = new TreeMap<>();
        traits = updateTraits(traits,"00_traits.txt");
        traits = updateTraits(traits,"01_traits.txt");
        traits = updateTraits(traits,"02_traits.txt");
        return traits;
    }

    private Map<Integer,Integer> updateCharacters(Map<Integer, Integer> m, String filename, Map<String, Integer> traits) throws IOException {
        List<String> list = readWords("history/characters/" + filename);
        int bracket = 0;
        Integer character = 0;
        Integer value = 0;
        for (String line : list) {
            if (!line.startsWith("#")) {
                if (line.contains("#")) {
                    line = line.split("#")[0];
                }
                if (bracket == 0 && line.contains("=")) {
                    character = Integer.valueOf(line.split("=")[0].trim());
                    value = 0;
                }
                if (line.contains("trait") && traits.get(line.split("=")[1].trim().replaceAll("\"", "")) != null && bracket < 3) {
                    value = value + traits.get(line.split("=")[1].trim().replaceAll("\"", ""));
                    m.put(character,value);
                }
                if (line.contains("{")) {
                    bracket++;
                }
                if (line.contains("}")) {
                    bracket--;
                }
            }
        }
        return m;
    }

    private Map<Integer, Integer> getCharacters(List<String> cultures, Map<String, Integer> traits) throws IOException {
        Map<Integer,Integer> characters = new TreeMap<>();
        for (String line : cultures) {
            characters = updateCharacters(characters,line,traits);
        }
        return characters;
    }
    
    private class compareMap implements Comparator<Map.Entry<Integer,Integer>> {
        public int compare(Map.Entry<Integer,Integer> a, Map.Entry<Integer,Integer> b) {
            if (a.getValue() == b.getValue()) {
                return a.getKey().compareTo(b.getKey());
            }
            return Integer.compare(b.getValue(), a.getValue());
        }
    }

    private List<Map.Entry<Integer, Integer>> sortMap(Map<Integer, Integer> characters) {
        List<Map.Entry<Integer,Integer>> sorted = new ArrayList<>(characters.entrySet());
        Collections.sort(sorted, new compareMap());
        return sorted;
    }
    
    public NewClass() throws IOException {
        List<String> cultures = getcultures();
        Map<String,Integer> traits = getTraits();
        Map<Integer,Integer> characters = getCharacters(cultures,traits);
        List<Map.Entry<Integer,Integer>> sorted = sortMap(characters);
        for (int i = 0; i < 200; i++) {
            System.out.println(sorted.get(i).getKey());
            System.out.println(sorted.get(i).getValue());
        }
    }
    
    public static void main( String [ ] args ) throws IOException {
        NewClass nc = new NewClass();
    }
}
