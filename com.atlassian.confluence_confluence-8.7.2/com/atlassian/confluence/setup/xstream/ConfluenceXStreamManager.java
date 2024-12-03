/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.xstream;

import com.atlassian.confluence.setup.xstream.ConfluenceXStream;

public interface ConfluenceXStreamManager {
    public ConfluenceXStream getPluginXStream(ClassLoader var1);

    public ConfluenceXStream getConfluenceXStream();

    public void resetXStream();
}

