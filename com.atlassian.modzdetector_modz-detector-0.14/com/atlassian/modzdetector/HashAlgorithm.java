/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import java.io.InputStream;

public interface HashAlgorithm {
    public String getHash(InputStream var1);

    public String getHash(byte[] var1);
}

