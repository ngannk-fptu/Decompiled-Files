/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.collections;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public interface List {
    public void add(Object var1);

    public void append(Object var1);

    public Object elementAt(int var1) throws NoSuchElementException;

    public Enumeration elements();

    public boolean includes(Object var1);

    public int length();
}

