/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.ser;

import java.io.IOException;
import java.io.Serializable;

public interface IndirectlySerialized
extends Serializable {
    public Object getObject() throws ClassNotFoundException, IOException;
}

