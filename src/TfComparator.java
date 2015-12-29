import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicolas on 16/12/2015.
 */
public class TfComparator implements Comparator {
    HashMap<String,Float> base;

    public TfComparator(HashMap<String, Float> base) {
        this.base = base;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (base.get((String)o1) >= base.get((String)o2)) {
            return -1;
        } else {
            return 1;
        }
    }
}
