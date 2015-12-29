import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Nicolas on 11/12/2015.
 */
public class GuPiDataBase {

    Connection connection;

        public GuPiDataBase() {
        }


    public void connect(){
                try {
                    Class.forName("org.postgresql.Driver");

                    System.out.println("Driver O.K.");

                    String url = "jdbc:postgresql://localhost:5432/GuPiBDD";
                    String user = "postgres";
                    String passwd = "kikooez666";

                    connection = DriverManager.getConnection(url, user, passwd);
                    System.out.println("Connexion effective !");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

    }
    public void destroyAllTables(){
        try {
            Statement state = connection.createStatement();
            state.executeUpdate("DROP TABLE IF EXISTS word");
            state.executeUpdate("DROP TABLE IF EXISTS document");
            state.executeUpdate("DROP TABLE IF EXISTS frequency");




        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertAllWords(HashMap<String,HashMap<String,Integer>> wordHM){

        int id = 1;
        Statement state = null;
        try {
            state = connection.createStatement();
            state.executeUpdate("CREATE TABLE IF NOT EXISTS word(id int PRIMARY KEY,name VARCHAR(100))");
            for( String key : wordHM.keySet()){
                state.executeUpdate("INSERT INTO word (id, name) VALUES('"+id+"','"+key+"')");
                id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAllDocuments(HashMap<String, Integer> documents){

        int id = 1;
        Statement state = null;
        try {
            state = connection.createStatement();
            state.executeUpdate("CREATE TABLE IF NOT EXISTS document(id int PRIMARY KEY,name VARCHAR(100))");
            for( String key : documents.keySet()){
                state.executeUpdate("INSERT INTO document (id, name) VALUES('"+id+"','"+key+"')");
                id++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getWordId(String word){

        Statement state = null;
        int result=0;

        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM word WHERE name='"+word+"'");
            while (res.next()){
                result = res.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;

    }

    public int getDocumentId(String document){

        Statement state = null;
        int result=0;

        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM document WHERE name='"+document+"'");
            while (res.next()){
                result = res.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;

    }


    public void insertAllFrequencies(HashMap<String,HashMap<String,Integer>> wordHM, HashMap<String, Integer> documents){
        int wordId;
        int documentId;

        Statement state = null;
        try {
            state = connection.createStatement();
            state.executeUpdate("CREATE TABLE IF NOT EXISTS frequency(idWord int, idDocument int ,frequency  float,idf float, PRIMARY KEY(idWord, idDocument))");


        for(String keyWord : wordHM.keySet()){
            for(String keyDoc : wordHM.get(keyWord).keySet()){

                wordId = getWordId(keyWord);
                documentId = getDocumentId(keyDoc);
                float tf = (float)wordHM.get(keyWord).get(keyDoc)/(float)documents.get(keyDoc);
                float idf = (float) Math.log(documents.size()/(wordHM.get(keyWord).size()));
                state.executeUpdate("INSERT INTO frequency (idWord, idDocument, frequency, idf) VALUES('"+wordId+"','"+documentId+"','"+tf+"','"+idf+"')");
            }
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void displayTableFrequency() {

        Statement state = null;

        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM frequency");
            while (res.next()) {
                System.out.println();
                System.out.print(res.getInt("idWord") + " : ");
                System.out.print(res.getInt("idDocument") + " : ");
                System.out.print(res.getString("frequency"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayTable(String tableName){

        Statement state = null;

        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM "+tableName);
            while (res.next()){
                System.out.println();
                System.out.print(res.getInt("id") + " : ");
                System.out.print(res.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public String getDocNameFromId(int id){
        Statement state = null;
        String result=null;
        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM document WHERE id='"+id+"'");
            while (res.next()){
                result = res.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getWordFromId(int id){
        Statement state = null;
        String result=null;
        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM word WHERE id='"+id+"'");
            while (res.next()){
                result = res.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public HashMap<String,Float> wordRequestWithTf(String word){
        int wordId = getWordId(word);
        HashMap<String,Float> result = new HashMap<>();
        Statement state = null;
        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM frequency WHERE idWord='"+wordId+"'");
            while (res.next()){
                String docName = getDocNameFromId(res.getInt("idDocument"));
                float freq = res.getFloat("frequency");
                result.put(docName,freq);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TfComparator tfComparator = new TfComparator(result);
        TreeMap<String,Float> treeMap = new TreeMap<>(tfComparator);
        treeMap.putAll(result);
        return result;
    }

    public HashMap<String,Float> wordRequestWithIdf(String word){
        int wordId = getWordId(word);
        HashMap<String,Float> result = new HashMap<>();
        Statement state = null;
        try {
            state = connection.createStatement();
            ResultSet res = state.executeQuery("SELECT * FROM frequency WHERE idWord='"+wordId+"'");
            while (res.next()){
                String docName = getDocNameFromId(res.getInt("idDocument"));
                float freq = res.getFloat("frequency");
                float idf = res.getFloat("idf");

                result.put(docName,freq*idf);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TfComparator tfComparator = new TfComparator(result);
        TreeMap<String,Float> treeMap = new TreeMap<>(tfComparator);
        treeMap.putAll(result);
        return result;
    }


    public TreeMap<String,Float> wordsRequestWithTf(String words){

        String temp = Normalizer
                .normalize(words.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        String[] tabTemp2 = temp.split("[^a-zA-Z0-9]");
        String[] tabTemp = new String[tabTemp2.length];
        for(int j=0; j<tabTemp2.length; j++){
            if(tabTemp2[j].length()>7) {
                tabTemp[j] = tabTemp2[j].substring(0, 7);
            }else {
                tabTemp[j] = tabTemp2[j];
            }
        }



        HashMap<String,Float> result = new HashMap<>();

        for(int i=0; i<tabTemp.length; i++){
            addHashMap(result, wordRequestWithTf(tabTemp[i]));
        }

        TfComparator tfComparator = new TfComparator(result);
        TreeMap<String,Float> treeMap = new TreeMap<>(tfComparator);
        treeMap.putAll(result);


        return treeMap;
    }


    public TreeMap<String,Float> wordsRequestWithIdf(String words){

        String temp = Normalizer
                .normalize(words.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        String[] tabTemp2 = temp.split("[^a-zA-Z0-9]");
        String[] tabTemp = new String[tabTemp2.length];
        for(int j=0; j<tabTemp2.length; j++){
            if(tabTemp2[j].length()>7) {
                tabTemp[j] = tabTemp2[j].substring(0, 7);
            }else {
                tabTemp[j] = tabTemp2[j];
            }
        }
        HashMap<String,Float> result = new HashMap<>();

        for(int i=0; i<tabTemp.length; i++){
            addHashMap(result, wordRequestWithIdf(tabTemp[i]));
        }

        TfComparator tfComparator = new TfComparator(result);
        TreeMap<String,Float> treeMap = new TreeMap<>(tfComparator);
        treeMap.putAll(result);


        return treeMap;
    }

    private void addHashMap(HashMap<String, Float> result, HashMap<String, Float> stringFloatHashMap) {

        for( String key : stringFloatHashMap.keySet()){

            if(result.containsKey(key)){
                result.put(key,result.get(key)+stringFloatHashMap.get(key));
            }else{
                result.put(key,stringFloatHashMap.get(key));
            }
        }
    }

    public TreeMap<String,Float> wordsRequestCos(String words) throws SQLException {

        HashMap<String,HashMap<String,Float>> docHM;
        String temp = Normalizer
                .normalize(words.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        String[] tabTemp2 = temp.split("[^a-zA-Z0-9]");
        String[] tabTemp = new String[tabTemp2.length];
        for(int j=0; j<tabTemp2.length; j++){
            if(tabTemp2[j].length()>7) {
                tabTemp[j] = tabTemp2[j].substring(0, 7);
            }else {
                tabTemp[j] = tabTemp2[j];
            }
        }

        docHM = getDocHMContainingWords(tabTemp);
        HashMap<String,Float> docAndDistanceHM = new HashMap<>();
        for(String keyDoc : docHM.keySet()){
            docAndDistanceHM.put(keyDoc,computeDistance(docHM.get(keyDoc),tabTemp));
        }

        TfComparator tfComparator = new TfComparator(docAndDistanceHM);
        TreeMap<String,Float> finalResult = new TreeMap<String,Float>(tfComparator);
        finalResult.putAll(docAndDistanceHM);

        return finalResult;
    }

    private float computeDistance(HashMap<String,Float> wordsHM,String[] request){

        float normR = 0;
        float normD = 0;
        float normLAHAUT = 0;

        for(int i=0; i<request.length; i++){
            normR = normR + ((1/(float)request.length)*(1/(float)request.length));
            if(wordsHM.containsKey(request[i])){
                normLAHAUT = normLAHAUT + (wordsHM.get(request[i])*(1/(float)request.length))*(wordsHM.get(request[i])*(1/(float)request.length));

            }
            for(String keyWord : wordsHM.keySet()){
                normD = normD + (wordsHM.get(keyWord)*wordsHM.get(keyWord));

            }
        }

        normR = (float)Math.sqrt(normR);
        normD = (float)Math.sqrt(normD);
        normLAHAUT = (float)Math.sqrt(normLAHAUT);
        //System.out.println("NORMES : D " + normD + " R " + normR + " LAHAUT " + normLAHAUT);
        Float result = normLAHAUT/(normD*normR);

        return result;
    }


    private void wordRequestCos(String word, HashMap<String,HashMap<String,Float>> docMap) throws SQLException {
        Statement state = connection.createStatement();
        int wordId = getWordId(word);
        ResultSet res = state.executeQuery("SELECT * FROM frequency WHERE idWord='"+wordId+"'");
        while (res.next()){
            String doc = getDocNameFromId(res.getInt("idDocument"));
            if(docMap.containsKey(doc)){
                docMap.get(doc).put(word,res.getFloat("idf")*res.getFloat("frequency"));
            }
            else{
                HashMap<String,Float> temp = new HashMap<String,Float>();
                temp.put(word,res.getFloat("idf")*res.getFloat("frequency"));
                docMap.put(doc,temp);
            }
        }


    }




    private HashMap<String,Float> getWordsForDocId(int docId) throws SQLException {

        HashMap<String,Float> result = new HashMap<>();

        Statement state = connection.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM frequency WHERE idDocument='"+docId+"'");

        while ((res.next())){
            result.put(getWordFromId(res.getInt("idWord")),res.getFloat("idf")*res.getFloat("frequency"));
        }

        return result;

    }


    private List<Integer> getDocIdsContainingWord(String word) throws SQLException {

        List<Integer> result = new ArrayList<>();
        Statement state = connection.createStatement();
        ResultSet res = state.executeQuery("SELECT * FROM frequency WHERE idWord='"+getWordId(word)+"'");

        while ((res.next())){
            result.add(res.getInt("idDocument"));
        }

        return result;

    }

    private List<Integer> getDocIdsContainingWords(String[] words) throws SQLException {


        List<Integer> result = new ArrayList<>();

        List<Integer> temp;

        for(int i = 0; i<words.length ; i++){
            temp = getDocIdsContainingWord(words[i]);

            for(int j = 0; j<temp.size() ; j++){
                if(!result.contains(temp.get(j))){
                    result.add(temp.get(j));
                }
            }
        }

        return result;
    }

    private HashMap<String,HashMap<String,Float>> getDocHMContainingWords(String[] words) throws SQLException {

        HashMap<String,HashMap<String,Float>> result = new HashMap<>();
        List<Integer> docIds = getDocIdsContainingWords(words);

        for (int i = 0 ; i < docIds.size() ; i++){
            result.put(getDocNameFromId(docIds.get(i)),getWordsForDocId(docIds.get(i)));
        }


        return result;
    }







}
