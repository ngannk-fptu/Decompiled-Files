/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Util {
    private Util() {
    }

    public static void executeCellProcessors(List<Object> destination, List<?> source, CellProcessor[] processors, int lineNo, int rowNo) {
        if (destination == null) {
            throw new NullPointerException("destination should not be null");
        }
        if (source == null) {
            throw new NullPointerException("source should not be null");
        }
        if (processors == null) {
            throw new NullPointerException("processors should not be null");
        }
        CsvContext context = new CsvContext(lineNo, rowNo, 1);
        context.setRowSource(new ArrayList<Object>(source));
        if (source.size() != processors.length) {
            throw new SuperCsvException(String.format("The number of columns to be processed (%d) must match the number of CellProcessors (%d): check that the number of CellProcessors you have defined matches the expected number of columns being read/written", source.size(), processors.length), context);
        }
        destination.clear();
        for (int i = 0; i < source.size(); ++i) {
            context.setColumnNumber(i + 1);
            if (processors[i] == null) {
                destination.add(source.get(i));
                continue;
            }
            destination.add(processors[i].execute(source.get(i), context));
        }
    }

    public static <T> void filterListToMap(Map<String, T> destinationMap, String[] nameMapping, List<? extends T> sourceList) {
        if (destinationMap == null) {
            throw new NullPointerException("destinationMap should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        if (sourceList == null) {
            throw new NullPointerException("sourceList should not be null");
        }
        if (nameMapping.length != sourceList.size()) {
            throw new SuperCsvException(String.format("the nameMapping array and the sourceList should be the same size (nameMapping length = %d, sourceList size = %d)", nameMapping.length, sourceList.size()));
        }
        destinationMap.clear();
        for (int i = 0; i < nameMapping.length; ++i) {
            String key = nameMapping[i];
            if (key == null) continue;
            if (destinationMap.containsKey(key)) {
                throw new SuperCsvException(String.format("duplicate nameMapping '%s' at index %d", key, i));
            }
            destinationMap.put(key, sourceList.get(i));
        }
    }

    public static List<Object> filterMapToList(Map<String, ?> map, String[] nameMapping) {
        if (map == null) {
            throw new NullPointerException("map should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        ArrayList<Object> result = new ArrayList<Object>(nameMapping.length);
        for (String key : nameMapping) {
            result.add(map.get(key));
        }
        return result;
    }

    public static Object[] filterMapToObjectArray(Map<String, ?> values, String[] nameMapping) {
        if (values == null) {
            throw new NullPointerException("values should not be null");
        }
        if (nameMapping == null) {
            throw new NullPointerException("nameMapping should not be null");
        }
        Object[] targetArray = new Object[nameMapping.length];
        int i = 0;
        for (String name : nameMapping) {
            targetArray[i++] = values.get(name);
        }
        return targetArray;
    }

    public static String[] objectArrayToStringArray(Object[] objectArray) {
        if (objectArray == null) {
            return null;
        }
        String[] stringArray = new String[objectArray.length];
        for (int i = 0; i < objectArray.length; ++i) {
            stringArray[i] = objectArray[i] != null ? objectArray[i].toString() : null;
        }
        return stringArray;
    }

    public static String[] objectListToStringArray(List<?> objectList) {
        if (objectList == null) {
            return null;
        }
        String[] stringArray = new String[objectList.size()];
        for (int i = 0; i < objectList.size(); ++i) {
            stringArray[i] = objectList.get(i) != null ? objectList.get(i).toString() : null;
        }
        return stringArray;
    }
}

