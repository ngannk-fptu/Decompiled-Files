/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import java.io.Serializable;

public interface ReadWriteCopyStrategy<T>
extends Serializable {
    public static final long serialVersionUID = 7167094683291072136L;

    public T copyForWrite(T var1, ClassLoader var2);

    public T copyForRead(T var1, ClassLoader var2);
}

