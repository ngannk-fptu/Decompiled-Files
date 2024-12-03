/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.compound;

import java.io.Serializable;

@Deprecated
public interface CopyStrategy
extends Serializable {
    public <T> T copy(T var1);
}

