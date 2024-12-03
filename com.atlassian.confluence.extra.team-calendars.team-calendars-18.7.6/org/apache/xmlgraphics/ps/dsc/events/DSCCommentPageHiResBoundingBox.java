/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.awt.geom.Rectangle2D;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentHiResBoundingBox;

public class DSCCommentPageHiResBoundingBox
extends DSCCommentHiResBoundingBox {
    public DSCCommentPageHiResBoundingBox() {
    }

    public DSCCommentPageHiResBoundingBox(Rectangle2D bbox) {
        super(bbox);
    }

    @Override
    public String getName() {
        return "PageHiResBoundingBox";
    }
}

