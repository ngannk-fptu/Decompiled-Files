/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model;

import com.sun.jersey.server.impl.model.method.ResourceMethod;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ResourceMethodMap
extends HashMap<String, List<ResourceMethod>> {
    ResourceMethodMap() {
    }

    public void put(ResourceMethod method) {
        ArrayList<ResourceMethod> l = (ArrayList<ResourceMethod>)this.get(method.getHttpMethod());
        if (l == null) {
            l = new ArrayList<ResourceMethod>();
            this.put(method.getHttpMethod(), l);
        }
        l.add(method);
    }

    public void sort() {
        for (Map.Entry e : this.entrySet()) {
            Collections.sort((List)e.getValue(), ResourceMethod.COMPARATOR);
        }
    }
}

