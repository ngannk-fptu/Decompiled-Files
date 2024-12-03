/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class LocationTypeList
implements Serializable,
Iterable<String> {
    private static final long serialVersionUID = -9181735547604179160L;
    private List<String> locationTypes = new CopyOnWriteArrayList<String>();

    public LocationTypeList() {
    }

    public LocationTypeList(String aValue) {
        StringTokenizer t = new StringTokenizer(aValue, ",");
        while (t.hasMoreTokens()) {
            this.locationTypes.add(t.nextToken());
        }
    }

    public final String toString() {
        return this.locationTypes.stream().collect(Collectors.joining(","));
    }

    public final boolean add(String locationType) {
        return this.locationTypes.add(locationType);
    }

    public final boolean isEmpty() {
        return this.locationTypes.isEmpty();
    }

    @Override
    public final Iterator<String> iterator() {
        return this.locationTypes.iterator();
    }

    public final boolean remove(String locationType) {
        return this.locationTypes.remove(locationType);
    }

    public final int size() {
        return this.locationTypes.size();
    }
}

