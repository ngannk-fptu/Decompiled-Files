/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.publisher.internal;

import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public interface PublishedGadgetSpecWriter {
    public void writeGadgetSpecTo(String var1, String var2, OutputStream var3) throws IOException, PublishedGadgetSpecNotFoundException;
}

