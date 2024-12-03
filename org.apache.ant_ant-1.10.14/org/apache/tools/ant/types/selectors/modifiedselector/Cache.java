/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.util.Iterator;

public interface Cache {
    public boolean isValid();

    public void delete();

    public void load();

    public void save();

    public Object get(Object var1);

    public void put(Object var1, Object var2);

    public Iterator<String> iterator();
}

