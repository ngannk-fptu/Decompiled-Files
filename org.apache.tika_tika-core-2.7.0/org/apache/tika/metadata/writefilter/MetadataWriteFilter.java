/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.writefilter;

import java.io.Serializable;
import java.util.Map;

public interface MetadataWriteFilter
extends Serializable {
    public void filterExisting(Map<String, String[]> var1);

    public void add(String var1, String var2, Map<String, String[]> var3);

    public void set(String var1, String var2, Map<String, String[]> var3);
}

