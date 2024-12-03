/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;
import java.util.Iterator;

public interface HierarchicalStreamReader
extends ErrorReporter {
    public boolean hasMoreChildren();

    public void moveDown();

    public void moveUp();

    public String getNodeName();

    public String getValue();

    public String getAttribute(String var1);

    public String getAttribute(int var1);

    public int getAttributeCount();

    public String getAttributeName(int var1);

    public Iterator getAttributeNames();

    public void appendErrors(ErrorWriter var1);

    public void close();

    public HierarchicalStreamReader underlyingReader();
}

