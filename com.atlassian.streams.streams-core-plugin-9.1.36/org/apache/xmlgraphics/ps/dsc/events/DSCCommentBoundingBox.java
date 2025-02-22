/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public class DSCCommentBoundingBox
extends AbstractDSCComment {
    private Rectangle2D bbox;

    public DSCCommentBoundingBox() {
    }

    public DSCCommentBoundingBox(Rectangle2D bbox) {
        this.setBoundingBox(bbox);
    }

    public Rectangle2D getBoundingBox() {
        return this.bbox;
    }

    public void setBoundingBox(Rectangle2D bbox) {
        this.bbox = bbox;
    }

    @Override
    public String getName() {
        return "BoundingBox";
    }

    @Override
    public boolean hasValues() {
        return true;
    }

    @Override
    public void parseValue(String value) {
        List params = this.splitParams(value);
        Iterator iter = params.iterator();
        double x1 = Double.parseDouble((String)iter.next());
        double y1 = Double.parseDouble((String)iter.next());
        double x2 = Double.parseDouble((String)iter.next());
        double y2 = Double.parseDouble((String)iter.next());
        this.bbox = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        if (this.getBoundingBox() != null) {
            gen.writeDSCComment(this.getName(), new Object[]{(int)Math.floor(this.bbox.getX()), (int)Math.floor(this.bbox.getY()), (int)Math.ceil(this.bbox.getX() + this.bbox.getWidth()), (int)Math.ceil(this.bbox.getY() + this.bbox.getHeight())});
        } else {
            gen.writeDSCComment(this.getName(), DSCConstants.ATEND);
        }
    }
}

