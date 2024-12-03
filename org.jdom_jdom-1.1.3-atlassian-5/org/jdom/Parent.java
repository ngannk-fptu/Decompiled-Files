/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.filter.Filter;

public interface Parent
extends Cloneable,
Serializable {
    public int getContentSize();

    public int indexOf(Content var1);

    public List cloneContent();

    public Content getContent(int var1);

    public List getContent();

    public List getContent(Filter var1);

    public List removeContent();

    public List removeContent(Filter var1);

    public boolean removeContent(Content var1);

    public Content removeContent(int var1);

    public Object clone();

    public Iterator getDescendants();

    public Iterator getDescendants(Filter var1);

    public Parent getParent();

    public Document getDocument();
}

