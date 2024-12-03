/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.Serializable;

interface Clause
extends Serializable {
    public boolean eval(byte[] var1);

    public int size();
}

