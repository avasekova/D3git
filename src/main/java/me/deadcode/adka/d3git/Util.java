package me.deadcode.adka.d3git;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Util {

    /*  [
          {"key":"2016-01-01"," value":90},
          {"key":"250"," y-coordinate":"50"}
          ...
        ]
    */
    public static String mapToJSON(Map<LocalDate, Long> map) {
        StringBuilder builder = new StringBuilder("[");

        boolean comma = false;
        for (Map.Entry<LocalDate, Long> entry : map.entrySet()) {
            if (comma) {
                builder.append(",");
            } else {
                comma = true;
            }

            builder.append("{\"key\":\"").append(entry.getKey()).append("\",\"value\":").append(entry.getValue()).append("}");
        }

        builder.append("]");

        return builder.toString();
    }


    public static Map<LocalDate, Long> fillGaps(Map<LocalDate, Long> numsPerDay) {
        Map<LocalDate, Long> newEntries = new HashMap<>();
        if (numsPerDay.size() > 1) {
            Iterator<LocalDate> iterator = numsPerDay.keySet().iterator();
            LocalDate currentEntry = iterator.next();
            LocalDate expectedNext = currentEntry.plusDays(1);
            LocalDate nextEntry = iterator.next();

            while (true) { //'while (iterator.hasNext())' is not enough, still needs to fill in everything until the last entry
                while (! nextEntry.equals(expectedNext)) { //there is a gap
                    newEntries.put(expectedNext, 0L);
                    currentEntry = expectedNext;
                    expectedNext = currentEntry.plusDays(1);
                }

                if (iterator.hasNext()) {
                    currentEntry = nextEntry;
                    expectedNext = currentEntry.plusDays(1);
                    nextEntry = iterator.next();
                } else {
                    break;
                }
            }
        }

        return newEntries;
    }
}
