/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTrackerV2;
import java.util.Objects;
import org.w3c.dom.Node;

public class ModificationStateTrackerV2Adaptor
implements ModificationStateTrackerV2 {
    private ModificationStateTracker modificationStateTracker;

    public ModificationStateTrackerV2Adaptor(ModificationStateTracker modificationStateTracker) {
        this.modificationStateTracker = Objects.requireNonNull(modificationStateTracker);
    }

    @Override
    public boolean shouldProcessText(Node node) {
        return this.modificationStateTracker.shouldProcessText(node);
    }

    @Override
    public void forward(Node node, String tagName) {
        this.modificationStateTracker.forward(tagName);
    }

    @Override
    public void back(Node node, String tagName) {
        this.modificationStateTracker.back(tagName);
    }

    @Override
    public boolean allowInsertion() {
        return this.modificationStateTracker.allowInsertion();
    }
}

