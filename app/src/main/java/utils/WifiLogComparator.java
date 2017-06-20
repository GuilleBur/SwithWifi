package utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Guille on 6/6/2017.
 */

public class WifiLogComparator implements Comparator <String> {

 public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss", Locale.US);

    public int compare(String strA, String strB) {

        Date dateA = null;
        Date dateB = null;
        try {
            dateA = dateFormat.parse(strA.substring(0, strA.indexOf("\"")-1));
            dateB = dateFormat.parse(strB.substring(0, strB.indexOf("\"")-1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int compare = dateB.compareTo(dateA);
        if(compare<0){
            return -1;
        }
        else if(compare>0) {
            return 1;
        }
        return 0;
    }
}
