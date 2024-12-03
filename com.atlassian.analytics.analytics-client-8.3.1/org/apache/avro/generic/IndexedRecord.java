/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import org.apache.avro.generic.GenericContainer;

public interface IndexedRecord
extends GenericContainer {
    public void put(int var1, Object var2);

    public Object get(int var1);
}

