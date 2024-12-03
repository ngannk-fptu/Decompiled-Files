/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import org.apache.felix.resolver.util.CandidateSelector;
import org.apache.felix.resolver.util.OpenHashMap;
import org.osgi.resource.Requirement;

public class OpenHashMapList
extends OpenHashMap<Requirement, CandidateSelector> {
    private static final long serialVersionUID = 0L;

    public OpenHashMapList() {
    }

    public OpenHashMapList(int initialCapacity) {
        super(initialCapacity);
    }

    public OpenHashMapList deepClone() {
        OpenHashMapList copy = (OpenHashMapList)super.clone();
        Object[] values = copy.value;
        int i = values.length;
        while (i-- > 0) {
            if (values[i] == null) continue;
            values[i] = ((CandidateSelector)values[i]).copy();
        }
        return copy;
    }
}

