/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.awt.geom.Rectangle2D;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBoundingBox;

public class DSCCommentPageBoundingBox
extends DSCCommentBoundingBox {
    public DSCCommentPageBoundingBox() {
    }

    public DSCCommentPageBoundingBox(Rectangle2D bbox) {
        super(bbox);
    }

    @Override
    public String getName() {
        return "PageBoundingBox";
    }
}

