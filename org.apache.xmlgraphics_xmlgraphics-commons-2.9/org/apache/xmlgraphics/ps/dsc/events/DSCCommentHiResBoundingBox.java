/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBoundingBox;

public class DSCCommentHiResBoundingBox
extends DSCCommentBoundingBox {
    public DSCCommentHiResBoundingBox() {
    }

    public DSCCommentHiResBoundingBox(Rectangle2D bbox) {
        super(bbox);
    }

    @Override
    public String getName() {
        return "HiResBoundingBox";
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        if (this.getBoundingBox() != null) {
            gen.writeDSCComment(this.getName(), new Object[]{this.getBoundingBox().getX(), this.getBoundingBox().getY(), this.getBoundingBox().getX() + this.getBoundingBox().getWidth(), this.getBoundingBox().getY() + this.getBoundingBox().getHeight()});
        } else {
            gen.writeDSCComment(this.getName(), DSCConstants.ATEND);
        }
    }
}

