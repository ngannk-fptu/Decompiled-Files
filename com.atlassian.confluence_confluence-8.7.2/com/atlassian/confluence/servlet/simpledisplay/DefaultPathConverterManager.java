/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.PathConverter;
import com.atlassian.confluence.servlet.simpledisplay.PathConverterManager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DefaultPathConverterManager
implements PathConverterManager {
    private final SortedMap<Integer, List<PathConverter>> converters = new TreeMap<Integer, List<PathConverter>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<PathConverter> getPathConverters() {
        SortedMap<Integer, List<PathConverter>> sortedMap = this.converters;
        synchronized (sortedMap) {
            ArrayList<PathConverter> result = new ArrayList<PathConverter>();
            for (List<PathConverter> list : this.converters.values()) {
                result.addAll(list);
            }
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addPathConverter(int weight, PathConverter converter) {
        SortedMap<Integer, List<PathConverter>> sortedMap = this.converters;
        synchronized (sortedMap) {
            ArrayList<PathConverter> list = (ArrayList<PathConverter>)this.converters.get(weight);
            if (list == null) {
                list = new ArrayList<PathConverter>();
            }
            list.add(converter);
            this.converters.put(weight, list);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePathConverter(PathConverter converter) {
        SortedMap<Integer, List<PathConverter>> sortedMap = this.converters;
        synchronized (sortedMap) {
            LinkedList<Integer> emptyWeights = new LinkedList<Integer>();
            for (Map.Entry<Integer, List<PathConverter>> entry : this.converters.entrySet()) {
                int weight = entry.getKey();
                List<PathConverter> list = entry.getValue();
                list.remove(converter);
                if (list.size() != 0) continue;
                emptyWeights.add(weight);
            }
            for (Integer weight : emptyWeights) {
                this.converters.remove(weight);
            }
        }
    }
}

