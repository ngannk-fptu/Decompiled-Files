/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.io;

import com.atlassian.annotations.ExperimentalApi;
import java.io.IOException;
import java.io.InputStream;

@ExperimentalApi
public interface InputStreamSource {
    public InputStream getInputStream() throws IOException;
}

