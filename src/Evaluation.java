import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Nicolas on 07/01/2016.
 */
public class Evaluation {

    // 0 for normal evaluation, 1 for enriched request (without weights)
    public void getEvaluation(int choice){
        int relDocsBeforeRank5 = 0;
        int relDocsFromRank5To10 = 0;
        int relDocsFromRank10To25 = 0;

        double precisionAt5TF = 0;
        double precisionAt10TF = 0;
        double precisionAt25TF = 0;

        double precisionAt5IDF = 0;
        double precisionAt10IDF = 0;
        double precisionAt25IDF = 0;

        double precisionAt5Cos = 0;
        double precisionAt10Cos = 0;
        double precisionAt25Cos = 0;

        double tempTF = 0;
        double tempIDF = 0;
        double tempCos = 0;

        GuPiDataBase bdd = new GuPiDataBase();
        bdd.connect();

        String queries2[] = {"Quelles sont les personnes impliquées dans le film Intouchables?",
                "Quel est le lieu de naissance d'Omar Sy?",
                "Qui a até récompensé pour Intouchables?",
                "Quel est le palmarès des Globes de Cristal 2012?",
                "Quels sont les membres du jury du Globes de Cristal 2012?",
                "Quels prix ont été décernés à Omar Sy aux Globes de Cristal 2012?",
                "Où a eu lieu les Globes de Cristal 2012?",
                "Quels prix ont été décernés à Omar Sy?",
                "Quels acteurs ont joué avec Omar Sy?"
        };

        String queries[] = {"personnes, Intouchables",
                "lieu naissance, Omar Sy",
                "personne, récompensées, Intouchables",
                "palmarès, Globes de Cristal 2012",
                "membre, jury, Globes de Cristal 2012",
                "prix, Omar Sy, Globes de Cristal 2012",
                "lieu, Globes de Cristal 2012",
                "prix, Omar Sy",
                "acteurs, joué avec, Omar Sy"
        };

        SparqlClientExample sparql = new SparqlClientExample();
        SparqlClient client = new SparqlClient("127.0.0.1:3030/space");

        for (int i = 0; i< queries.length; i++) {
            HashMap<String, Boolean> docToRelevanceMap = readQrel("qrelQ" + (i + 1) + ".txt");

            // readQrel est une fon[ction à faire qui lit un fichier qrle et il retourne un mapping document -> pertinent. Si dans le fichier qrel un document est attribué un score de 1 ou 0.5 le document est considéré pertinent. Si le qrel contient un score 0 ou pas de score pour le document, le document n'est pas pertient.
            //System.out.println("Ancienne q : "+queries[i]);

            TreeMap<String, Float> resultsReqTF = null;
            TreeMap<String, Float> resultsReqIDF=null;
            TreeMap<String, Float> resultsReqCos = null;
            if(choice==1) {
                String q = sparql.toWordOnly(sparql.enrichissement(client,queries[i]));
                System.out.println("Nouvelle requete : "+q);
                resultsReqTF = bdd.wordsRequestWithTf(q);
                resultsReqIDF = bdd.wordsRequestWithIdf(q);
                resultsReqCos = bdd.wordsRequestCos(q);
            }else{
                resultsReqTF = bdd.wordsRequestWithTf(queries[i]);
                resultsReqIDF = bdd.wordsRequestWithIdf(queries[i]);
                resultsReqCos = bdd.wordsRequestCos(queries[i]);
            }
            System.out.println("------------------------------------------------");
            System.out.println("Requete N°"+(i+1));
            System.out.println("------------------------------------------------");

            tempTF = getPrecision(0,4,resultsReqTF,docToRelevanceMap);
            tempIDF = getPrecision(0,4,resultsReqIDF,docToRelevanceMap);
            tempCos = getPrecision(0,4,resultsReqCos,docToRelevanceMap);
            precisionAt5TF += tempTF;
            precisionAt5IDF += tempIDF;
            precisionAt5Cos += tempCos;
            System.out.println("[SUM-TF] La précision P@5 est " + tempTF);
            System.out.println("[SUM-TFIDF] La précision P@5 est " + tempIDF);
            System.out.println("[COS-TFIDF] La précision P@5 est " + tempCos);

            System.out.println("                -----------                     ");

            tempTF = getPrecision(0,9,resultsReqTF,docToRelevanceMap);
            tempIDF = getPrecision(0,9,resultsReqIDF,docToRelevanceMap);
            tempCos = getPrecision(0,9,resultsReqCos,docToRelevanceMap);
            precisionAt10TF += tempTF;
            precisionAt10IDF += tempIDF;
            precisionAt10Cos += tempCos;
            System.out.println("[SUM-TF] La précision P@10 est " + tempTF);
            System.out.println("[SUM-TFIDF] La précision P@10 est " + tempIDF);
            System.out.println("[COS-TFIDF] La précision P@10 est " + tempCos);

            System.out.println("                -----------                     ");

            tempTF = getPrecision(0,24,resultsReqTF,docToRelevanceMap);
            tempIDF = getPrecision(0,24,resultsReqIDF,docToRelevanceMap);
            tempCos = getPrecision(0,24,resultsReqCos,docToRelevanceMap);
            precisionAt25TF += tempTF;
            precisionAt25IDF += tempIDF;
            precisionAt25Cos += tempCos;
            System.out.println("[SUM-TF] La précision P@25 est " + tempTF);
            System.out.println("[SUM-TFIDF] La précision P@25 est " + tempIDF);
            System.out.println("[COS-TFIDF] La précision P@25 est " + tempCos);

        }


        System.out.println("------------------------------------------------");
        System.out.println("Moyenne");
        System.out.println("------------------------------------------------");

        System.out.println("[SUM-TF]La précision moyene P@5 est " + precisionAt5TF/queries.length);
        System.out.println("[SUM-TFIDF]La précision moyene P@5 est " + precisionAt5IDF/queries.length);
        System.out.println("[COS-TFIDF]La précision moyene P@5 est " + precisionAt5Cos/queries.length);

        System.out.println("                -----------                     ");

        System.out.println("[TF]La précision moyene P@10 est " + precisionAt10TF/queries.length);
        System.out.println("[IDF]La précision moyene P@10 est " + precisionAt10IDF/queries.length);
        System.out.println("[Cos]La précision moyene P@10 est " + precisionAt10Cos/queries.length);

        System.out.println("                -----------                     ");

        System.out.println("[TF]La précision moyene P@25 est " + precisionAt25TF/queries.length);
        System.out.println("[IDF]La précision moyene P@25 est " + precisionAt25IDF/queries.length);
        System.out.println("[Cos]La précision moyene P@25 est " + precisionAt25Cos/queries.length);

        System.out.println("------------------------------------------------");
        System.out.println("----------    End of evaluation    -------------");
        System.out.println("------------------------------------------------");

    }


    public void getEvaluationWeight(){
        int relDocsBeforeRank5 = 0;
        int relDocsFromRank5To10 = 0;
        int relDocsFromRank10To25 = 0;

        double precisionAt5TF = 0;
        double precisionAt10TF = 0;
        double precisionAt25TF = 0;

        double precisionAt5IDF = 0;
        double precisionAt10IDF = 0;
        double precisionAt25IDF = 0;

        double precisionAt5Cos = 0;
        double precisionAt10Cos = 0;
        double precisionAt25Cos = 0;

        double tempTF = 0;
        double tempIDF = 0;
        double tempCos = 0;

        GuPiDataBase bdd = new GuPiDataBase();
        bdd.connect();

        String queries2[] = {"Quelles sont les personnes impliquées dans le film Intouchables?",
                "Quel est le lieu de naissance d'Omar Sy?",
                "Qui a até récompensé pour Intouchables?",
                "Quel est le palmarès des Globes de Cristal 2012?",
                "Quels sont les membres du jury du Globes de Cristal 2012?",
                "Quels prix ont été décernés à Omar Sy aux Globes de Cristal 2012?",
                "Où a eu lieu les Globes de Cristal 2012?",
                "Quels prix ont été décernés à Omar Sy?",
                "Quels acteurs ont joué avec Omar Sy?"
        };

        String queries[] = {"personnes, Intouchables",
                "lieu naissance, Omar Sy",
                "personne, récompensées, Intouchables",
                "palmarès, Globes de Cristal 2012",
                "membre, jury, Globes de Cristal 2012",
                "prix, Omar Sy, Globes de Cristal 2012",
                "lieu, Globes de Cristal 2012",
                "prix, Omar Sy",
                "acteurs, joué avec, Omar Sy"
        };

        SparqlClientExample sparql = new SparqlClientExample();
        SparqlClient client = new SparqlClient("127.0.0.1:3030/space");


        for (int i = 0; i< queries.length; i++) {

            HashMap<String, Boolean> docToRelevanceMap = readQrel("qrelQ" + (i + 1) + ".txt");  // readQrel est une fonction à faire qui lit un fichier qrle et il retourne un mapping document -> pertinent. Si dans le fichier qrel un document est attribué un score de 1 ou 0.5 le document est considéré pertinent. Si le qrel contient un score 0 ou pas de score pour le document, le document n'est pas pertient.
            ArrayList<WordWeight> q = sparql.enrichissement(client, queries[i]);
            System.out.println("Nouvelle requete : "+q.toString());
            TreeMap<String, Float> resultsReqTF = bdd.wordsRequestWithTfWeight(q);
            TreeMap<String, Float> resultsReqIDF = bdd.wordsRequestWithIdfWeight(q);
            TreeMap<String, Float> resultsReqCos = bdd.wordsRequestCosWeight(q);
            System.out.println("------------------------------------------------");
            System.out.println("Requete N°"+(i+1));
            System.out.println("------------------------------------------------");

            tempTF = getPrecision(0,4,resultsReqTF,docToRelevanceMap);
            tempIDF = getPrecision(0,4,resultsReqIDF,docToRelevanceMap);
            tempCos = getPrecision(0,4,resultsReqCos,docToRelevanceMap);
            precisionAt5TF += tempTF;
            precisionAt5IDF += tempIDF;
            precisionAt5Cos += tempCos;
            System.out.println("[SUM-TF] La précision P@5 est " + tempTF);
            System.out.println("[SUM-TFIDF] La précision P@5 est " + tempIDF);
            System.out.println("[COS-TFIDF] La précision P@5 est " + tempCos);

            System.out.println("                -----------                     ");

            tempTF = getPrecision(0,9,resultsReqTF,docToRelevanceMap);
            tempIDF = getPrecision(0,9,resultsReqIDF,docToRelevanceMap);
            tempCos = getPrecision(0,9,resultsReqCos,docToRelevanceMap);
            precisionAt10TF += tempTF;
            precisionAt10IDF += tempIDF;
            precisionAt10Cos += tempCos;
            System.out.println("[SUM-TF] La précision P@10 est " + tempTF);
            System.out.println("[SUM-TFIDF] La précision P@10 est " + tempIDF);
            System.out.println("[COS-TFIDF] La précision P@10 est " + tempCos);

            System.out.println("                -----------                     ");

            tempTF = getPrecision(0,24,resultsReqTF,docToRelevanceMap);
            tempIDF = getPrecision(0,24,resultsReqIDF,docToRelevanceMap);
            tempCos = getPrecision(0,24,resultsReqCos,docToRelevanceMap);
            precisionAt25TF += tempTF;
            precisionAt25IDF += tempIDF;
            precisionAt25Cos += tempCos;
            System.out.println("[SUM-TF] La précision P@25 est " + tempTF);
            System.out.println("[SUM-TFIDF] La précision P@25 est " + tempIDF);
            System.out.println("[COS-TFIDF] La précision P@25 est " + tempCos);

        }


        System.out.println("------------------------------------------------");
        System.out.println("Moyenne");
        System.out.println("------------------------------------------------");

        System.out.println("[SUM-TF]La précision moyene P@5 est " + precisionAt5TF/queries.length);
        System.out.println("[SUM-TFIDF]La précision moyene P@5 est " + precisionAt5IDF/queries.length);
        System.out.println("[COS-TFIDF]La précision moyene P@5 est " + precisionAt5Cos/queries.length);

        System.out.println("                -----------                     ");

        System.out.println("[TF]La précision moyene P@10 est " + precisionAt10TF/queries.length);
        System.out.println("[IDF]La précision moyene P@10 est " + precisionAt10IDF/queries.length);
        System.out.println("[Cos]La précision moyene P@10 est " + precisionAt10Cos/queries.length);

        System.out.println("                -----------                     ");

        System.out.println("[TF]La précision moyene P@25 est " + precisionAt25TF/queries.length);
        System.out.println("[IDF]La précision moyene P@25 est " + precisionAt25IDF/queries.length);
        System.out.println("[Cos]La précision moyene P@25 est " + precisionAt25Cos/queries.length);

        System.out.println("------------------------------------------------");
        System.out.println("----------    End of evaluation    -------------");
        System.out.println("------------------------------------------------");

    }

    private HashMap<String,Boolean> readQrel(String qrelDoc){
        HashMap<String,Boolean> results = new HashMap<>();
        try {
            File qrelFile = new File("res/QREL/"+qrelDoc);
            BufferedReader br = new BufferedReader(new FileReader(qrelFile));
            String line;
            while ((line = br.readLine()) != null) {
                Float relevance;
                String[] splitedLine = line.split("\t|\n");
                relevance = Float.parseFloat(splitedLine[1].replaceAll(",","."));
                results.put(splitedLine[0],relevance>0f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private float getPrecision(int start, int end, TreeMap<String,Float> reqRes, HashMap<String,Boolean> docRelevance){
        float relevance = 0f;
        Set<String> temp = reqRes.keySet();
        List<String> docs = new ArrayList<>();
        docs.addAll(temp);
        float counter = 0f;


        for(int i = start; i<=end&&i<docs.size(); i++){
            counter++;
            if(reqRes.containsKey(docs.get(i))) {
                if(reqRes.get(docs.get(i)) > 0.0f) {
                    if(docRelevance.containsKey(docs.get(i))) {
                        if (docRelevance.get(docs.get(i))) {
                            relevance++;
                        }
                    }
                }
            }
        }

        relevance = relevance/counter;

        return relevance;
    }


    public void testEvaluation(String qrel, String request){

        double tempTF = 0;
        double tempIDF = 0;
        double tempCos = 0;
        GuPiDataBase bdd = new GuPiDataBase();

        SparqlClientExample sparql = new SparqlClientExample();
        SparqlClient client = new SparqlClient("127.0.0.1:3030/space");
        bdd.connect();

        HashMap<String, Boolean> docToRelevanceMap = readQrel(qrel);


        TreeMap<String, Float> resultsReqTF = bdd.wordsRequestWithTf(request);
        TreeMap<String, Float> resultsReqIDF = bdd.wordsRequestWithIdf(request);
        TreeMap<String, Float> resultsReqCos = bdd.wordsRequestCos(request);

        System.out.println("------------------------------------------------");
        System.out.println("Sans enrichissement");
        System.out.println("Requête : "+request);
        System.out.println("------------------------------------------------");

        tempTF = getPrecision(0,4,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,4,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,4,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@5 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@5 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@5 est " + tempCos);

        System.out.println("                -----------                     ");

        tempTF = getPrecision(0,9,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,9,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,9,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@10 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@10 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@10 est " + tempCos);

        System.out.println("                -----------                     ");

        tempTF = getPrecision(0,24,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,24,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,24,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@25 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@25 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@25 est " + tempCos);

        ArrayList<WordWeight> wordWeightList = sparql.enrichissement(client, request);
        String q = sparql.toWordOnly(wordWeightList);
        resultsReqTF = bdd.wordsRequestWithTf(q);
        resultsReqIDF = bdd.wordsRequestWithIdf(q);
        resultsReqCos = bdd.wordsRequestCos(q);

        System.out.println("------------------------------------------------");
        System.out.println("Avec enrichissement");
        System.out.println("Requête : "+q);
        System.out.println("------------------------------------------------");

        tempTF = getPrecision(0,4,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,4,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,4,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@5 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@5 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@5 est " + tempCos);

        System.out.println("                -----------                     ");

        tempTF = getPrecision(0,9,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,9,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,9,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@10 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@10 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@10 est " + tempCos);

        System.out.println("                -----------                     ");

        tempTF = getPrecision(0,24,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,24,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,24,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@25 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@25 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@25 est " + tempCos);




        resultsReqTF = bdd.wordsRequestWithTfWeight(wordWeightList);
        resultsReqIDF = bdd.wordsRequestWithIdfWeight(wordWeightList);
        resultsReqCos = bdd.wordsRequestCosWeight(wordWeightList);

        System.out.println("------------------------------------------------");
        System.out.println("Avec enrichissement et pondération");
        System.out.println("Requête : "+wordWeightList);
        System.out.println("------------------------------------------------");

        tempTF = getPrecision(0,4,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,4,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,4,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@5 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@5 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@5 est " + tempCos);

        System.out.println("                -----------                     ");

        tempTF = getPrecision(0,9,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,9,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,9,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@10 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@10 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@10 est " + tempCos);

        System.out.println("                -----------                     ");

        tempTF = getPrecision(0,24,resultsReqTF,docToRelevanceMap);
        tempIDF = getPrecision(0,24,resultsReqIDF,docToRelevanceMap);
        tempCos = getPrecision(0,24,resultsReqCos,docToRelevanceMap);

        System.out.println("[SUM-TF] La précision P@25 est " + tempTF);
        System.out.println("[SUM-TFIDF] La précision P@25 est " + tempIDF);
        System.out.println("[COS-TFIDF] La précision P@25 est " + tempCos);



    }


}
