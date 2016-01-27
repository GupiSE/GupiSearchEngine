import java.util.*;

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
       if (base.get((String)o1) > base.get((String)o2)) {
            return -1;
       }else if(base.get((String)o1) == base.get((String)o2)){
           return compareKey((String)o1,(String)o2);
        } else {
        return 1;
        }

    }

    private int compareKey(String s1, String s2){
        if(s1.hashCode()>s2.hashCode()){
            return -1;
        }else if(s1.hashCode()==s2.hashCode()){
            return 0;
        }else {
            return 1;
        }
    }





}

