/**
 * Created by Nicolas on 02/12/2015.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {



    public static void main (String[] args){
        try {
            File rep = new File ("res/CORPUS");
            File[] tabFiles = rep.listFiles();
            List<File> files = new ArrayList<>(Arrays.asList(tabFiles));

            GupiParser  parser = new GupiParser();
            System.out.println(parser.getTotalWords().toString());
            //System.out.println(parser.fileToList(new File("res/CORPUS/D13.html")));
            HashMap<String,HashMap<String,Integer>> parsed = parser.filesToHashMap(files);
            GuPiDataBase bdd = new GuPiDataBase();
            bdd.connect();
            //bdd.destroyAllTables();
            //bdd.insertAllWords(parsed);
            //bdd.insertAllDocuments(parser.getTotalWords());
            //bdd.insertAllFrequencies(parsed, parser.getTotalWords());
            //bdd.displayTableFrequency();
            System.out.println(bdd.wordRequestWithTf("omar"));
            System.out.println(bdd.wordRequestWithTf("sy"));
            System.out.println(bdd.wordRequestWithTf("beau"));
            System.out.println(bdd.wordRequestWithTf("eliott"));
            System.out.println("TF : "+bdd.wordsRequestWithTf("omar sy beau eliott"));
            System.out.println("IDF : "+bdd.wordsRequestWithIdf("omar sy beau eliott"));
            System.out.println("COS : "+bdd.wordsRequestCos("omar sy beau eliott"));
        }catch (Exception e){
            e.printStackTrace();
        }



        /*’ '' ? ?
        ????????
        «*/
    }
}
