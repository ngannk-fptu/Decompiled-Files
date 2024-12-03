/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

public interface ForkProxy
extends Serializable {
    public void init(DataInputStream var1, DataOutputStream var2);
}

