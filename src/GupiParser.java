import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nicolas irs me on 02/12/2015.
 */
public class GupiParser {

    private List<String> stopList = new ArrayList<>();
    private HashMap<String, Integer> totalWords = new HashMap<>();

    public HashMap<String, Integer> getTotalWords() {
        return totalWords;
    }


    public GupiParser() {
        try {
            File stopListFile = new File("res/stoplist/stoplist.txt");
            BufferedReader br = new BufferedReader(new FileReader(stopListFile));
            String line;
            while ((line = br.readLine()) != null) {
                stopList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String,HashMap<String,Integer>> filesToHashMap(List<File> files) throws Exception{

        final HashMap<String,HashMap<String,Integer>> wordsHM = new HashMap<>();

        Document doc;

        for(int i = 0; i<files.size(); i++){
             final File file = files.get(i);
             doc = Jsoup.parse(file, "UTF-8");
             doc.traverse(new NodeVisitor() {

                public void head(Node node, int depth) {
                    //System.out.println("Entering tag: " + node.nodeName());
                    if(node.nodeName().equals("#text")) {
                        nodeToHashMap(file.getName(), wordsHM, node);
                    }
                }

                public void tail(Node node, int depth) {
                    //System.out.println("Exiting tag: " + node.nodeName());
                }
            });


        }



        return wordsHM;

    }

    public List<String> fileToList(File file) throws Exception{

        final List<String> listString = new ArrayList<>();
        Document doc = Jsoup.parse(file, "UTF-8");
        doc.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                //System.out.println("Entering tag: " + node.nodeName());
                if(node.nodeName().equals("#text")) {
                    listString.addAll(nodeToList(node));
                }
            }

            public void tail(Node node, int depth) {
                //System.out.println("Exiting tag: " + node.nodeName());
            }
        });

        return listString;
    }

    public List<String> nodeToList(Node node){
        List<String> listString = new ArrayList<>();
        String temp = node.toString().toLowerCase().replaceAll("[\\p{Punct}]|\n|\t|«", " ");
        String[] tabTemp = temp.split(" ");
        for(int i = 0; i<tabTemp.length; i++){
            if(isAcceptable(tabTemp[i])){
                if(tabTemp[i].length()>7) {
                    listString.add(tabTemp[i].substring(0, 7));
                }else{
                        listString.add(tabTemp[i]);
                }
            }

        }

        return  listString;
    }

    public void nodeToHashMap(String name, HashMap<String,HashMap<String,Integer>> wordsHM, Node node){
        //String temp = node.toString().toLowerCase().replaceAll("[\\p{Punct}]|\n|\t|«", " ");
        //String temp = node.toString().replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();
        String temp = Normalizer
                        .normalize(node.toString().toLowerCase(), Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");

        String[] tabTemp = temp.split("[^a-zA-Z0-9]");
        for(int i = 0; i<tabTemp.length; i++){
            if(isAcceptable(tabTemp[i])){
                if(tabTemp[i].length()>7) {
                    this.addStringToHashMap(name, wordsHM, tabTemp[i].substring(0, 7));
                }else{
                    this.addStringToHashMap(name, wordsHM, tabTemp[i]);
                }

            }
        }
    }

    public void addStringToHashMap(String name, HashMap<String,HashMap<String,Integer>> wordsHM, String word){

        if(totalWords.containsKey(name)){
            totalWords.put(name, new Integer(totalWords.get(name).intValue()+1));
        }else{
            totalWords.put(name,new Integer(1));
        }

        if(wordsHM.containsKey(word)){
            if(wordsHM.get(word).containsKey(name)){
                wordsHM.get(word).put(name,new Integer (wordsHM.get(word).get(name).intValue()+1));
            }else{
                wordsHM.get(word).put(name,new Integer(1));
            }
        }else{
            HashMap<String,Integer> temp = new HashMap<>();
            temp.put(name,new Integer(1));
            wordsHM.put(word,temp);
        }

    }

    public boolean isAcceptable(String string){

        return (string.length()>1)&&(!stopList.contains(string))&&(!string.matches("[0-9]*"));
    }

    public void display(HashMap<String,HashMap<String,Integer>> wordsHM){
        for (String key : wordsHM.keySet()){
            System.out.println(key);
            System.out.println("    "+wordsHM.get(key).toString());
        }
    }
}
