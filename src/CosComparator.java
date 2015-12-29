import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicolas on 16/12/2015.
 */
public class CosComparator implements Comparator {
    private HashMap<String,HashMap<String,Float>> base;
    private String[] request;

    public CosComparator(HashMap<String,HashMap<String,Float>> base, String[] request) {

        this.base = base;
        this.request=request;
    }

    private float computeDistance(HashMap<String,Float> wordsHM){

        float normR = 0;
        float normD = 0;
        float normLAHAUT = 0;

        for(int i=0; i<request.length; i++){
            normR = normR + ((1/request.length)*(1/request.length));
            if(wordsHM.containsKey(request[i])){
                normLAHAUT = normLAHAUT + (wordsHM.get(request[i])*(1/request.length))*(wordsHM.get(request[i])*(1/request.length));
                normD = normD + (wordsHM.get(request[i])*wordsHM.get(request[i]));

            }
        }
        normR = (float)Math.sqrt(normR);
        normD = (float)Math.sqrt(normD);
        normLAHAUT = (float)Math.sqrt(normLAHAUT);

        Float result = normLAHAUT/(normD*normR);

        return result;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (computeDistance(base.get((String)o1)) >= computeDistance(base.get((String)o2))) {
            return -1;
        } else {
            return 1;
        }
    }
}
