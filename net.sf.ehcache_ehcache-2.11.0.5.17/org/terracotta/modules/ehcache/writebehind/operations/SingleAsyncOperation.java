/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind.operations;

import java.io.IOException;
import java.io.Serializable;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;

public interface SingleAsyncOperation
extends Serializable {
    public void performSingleOperation(CacheWriter var1) throws ClassNotFoundException, IOException;

    public Object getKey();

    public Element getElement();

    public long getCreationTime();

    public void throwAwayElement(CacheWriter var1, RuntimeException var2);
}

