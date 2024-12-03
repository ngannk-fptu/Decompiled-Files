/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.AreaOpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;

public class AreaOpPropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$javax$media$jai$ROI;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;

    public AreaOpPropertyGenerator() {
        super(new String[]{"ROI"}, new Class[]{class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = AreaOpPropertyGenerator.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = AreaOpPropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp});
    }

    public Object getProperty(String name, Object opNode) {
        this.validate(name, opNode);
        if (opNode instanceof RenderedOp && name.equalsIgnoreCase("roi")) {
            RenderedOp op = (RenderedOp)opNode;
            ParameterBlock pb = op.getParameterBlock();
            PlanarImage src = (PlanarImage)pb.getRenderedSource(0);
            Object roiProperty = src.getProperty("ROI");
            if (roiProperty == null || roiProperty == Image.UndefinedProperty || !(roiProperty instanceof ROI)) {
                return Image.UndefinedProperty;
            }
            ROI roi = (ROI)roiProperty;
            Rectangle dstBounds = null;
            PlanarImage dst = op.getRendering();
            if (dst instanceof AreaOpImage && ((AreaOpImage)dst).getBorderExtender() == null) {
                AreaOpImage aoi = (AreaOpImage)dst;
                dstBounds = new Rectangle(aoi.getMinX() + aoi.getLeftPadding(), aoi.getMinY() + aoi.getTopPadding(), aoi.getWidth() - aoi.getLeftPadding() - aoi.getRightPadding(), aoi.getHeight() - aoi.getTopPadding() - aoi.getBottomPadding());
            } else {
                dstBounds = dst.getBounds();
            }
            if (!dstBounds.contains(roi.getBounds())) {
                roi = roi.intersect(new ROIShape(dstBounds));
            }
            return roi;
        }
        return Image.UndefinedProperty;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

