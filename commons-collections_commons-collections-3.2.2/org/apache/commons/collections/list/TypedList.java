/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.List;
import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.list.PredicatedList;

public class TypedList {
    public static List decorate(List list, Class type) {
        return new PredicatedList(list, InstanceofPredicate.getInstance(type));
    }

    protected TypedList() {
    }
}

