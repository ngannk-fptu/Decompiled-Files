/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 */
package com.atlassian.gadgets.publisher.internal;

import com.atlassian.gadgets.GadgetParsingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface GadgetProcessor {
    public void process(InputStream var1, OutputStream var2) throws IOException, GadgetParsingException;
}

