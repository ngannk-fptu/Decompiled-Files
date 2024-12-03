/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.IllegalAddException;
import org.jdom2.NamespaceAware;
import org.jdom2.filter.Filter;
import org.jdom2.util.IteratorIterable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Parent
extends Cloneable,
NamespaceAware,
Serializable {
    public int getContentSize();

    public int indexOf(Content var1);

    public List<Content> cloneContent();

    public Content getContent(int var1);

    public List<Content> getContent();

    public <E extends Content> List<E> getContent(Filter<E> var1);

    public List<Content> removeContent();

    public <E extends Content> List<E> removeContent(Filter<E> var1);

    public boolean removeContent(Content var1);

    public Content removeContent(int var1);

    public Object clone();

    public IteratorIterable<Content> getDescendants();

    public <E extends Content> IteratorIterable<E> getDescendants(Filter<E> var1);

    public Parent getParent();

    public Document getDocument();

    public void canContainContent(Content var1, int var2, boolean var3) throws IllegalAddException;

    public Parent addContent(Content var1);

    public Parent addContent(Collection<? extends Content> var1);

    public Parent addContent(int var1, Content var2);

    public Parent addContent(int var1, Collection<? extends Content> var2);
}

