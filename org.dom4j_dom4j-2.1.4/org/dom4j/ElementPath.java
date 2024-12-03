/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j;

import org.dom4j.Element;
import org.dom4j.ElementHandler;

public interface ElementPath {
    public int size();

    public Element getElement(int var1);

    public String getPath();

    public Element getCurrent();

    public void addHandler(String var1, ElementHandler var2);

    public void removeHandler(String var1);
}

