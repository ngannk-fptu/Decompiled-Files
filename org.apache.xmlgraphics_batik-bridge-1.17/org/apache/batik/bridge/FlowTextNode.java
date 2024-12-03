/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.FlowTextPainter;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextPainter;

public class FlowTextNode
extends TextNode {
    public FlowTextNode() {
        this.textPainter = FlowTextPainter.getInstance();
    }

    @Override
    public void setTextPainter(TextPainter textPainter) {
        this.textPainter = textPainter == null ? FlowTextPainter.getInstance() : textPainter;
    }
}

