/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import org.w3c.dom.Node;

public interface ModificationStateTrackerV2
extends ModificationStateTracker {
    @Override
    default public void forward(String tagName) {
        this.forward(null, tagName);
    }

    @Override
    default public void back(String tagName) {
        this.forward(null, tagName);
    }

    public void forward(Node var1, String var2);

    public void back(Node var1, String var2);
}

