/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class QuerySpacesHelper {
    public static final QuerySpacesHelper INSTANCE = new QuerySpacesHelper();

    private QuerySpacesHelper() {
    }

    public String[] toStringArray(Set spacesSet) {
        return spacesSet.toArray(new String[0]);
    }

    public Set<String> toStringSet(String[] spacesArray) {
        HashSet<String> set = new HashSet<String>();
        Collections.addAll(set, spacesArray);
        return set;
    }

    public Set<Serializable> toSerializableSet(String[] spacesArray) {
        HashSet<Serializable> set = new HashSet<Serializable>();
        Collections.addAll(set, spacesArray);
        return set;
    }
}

