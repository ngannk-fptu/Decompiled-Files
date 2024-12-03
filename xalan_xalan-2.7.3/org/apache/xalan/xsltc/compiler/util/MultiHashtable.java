/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import java.util.Hashtable;
import java.util.Vector;

public final class MultiHashtable
extends Hashtable {
    static final long serialVersionUID = -6151608290510033572L;

    @Override
    public Object put(Object key, Object value) {
        Vector<Object> vector = (Vector<Object>)this.get(key);
        if (vector == null) {
            vector = new Vector<Object>();
            super.put(key, vector);
        }
        vector.add(value);
        return vector;
    }

    public Object maps(Object from, Object to) {
        if (from == null) {
            return null;
        }
        Vector vector = (Vector)this.get(from);
        if (vector != null) {
            int n = vector.size();
            for (int i = 0; i < n; ++i) {
                Object item = vector.elementAt(i);
                if (!item.equals(to)) continue;
                return item;
            }
        }
        return null;
    }
}

