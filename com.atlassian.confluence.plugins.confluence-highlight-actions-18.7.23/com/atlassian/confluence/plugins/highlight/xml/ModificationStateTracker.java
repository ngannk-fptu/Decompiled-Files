/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.xml;

import org.w3c.dom.Node;

public interface ModificationStateTracker {
    public boolean shouldProcessText(Node var1);

    public void forward(String var1);

    public void back(String var1);

    public boolean allowInsertion();
}

