/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.io;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.io.InputStreamSource;

@ExperimentalApi
public interface DataCompressor {
    public InputStreamSource uncompress(InputStreamSource var1);

    public InputStreamSource compress(InputStreamSource var1);

    public byte[] uncompress(byte[] var1);

    public byte[] compress(byte[] var1);
}

