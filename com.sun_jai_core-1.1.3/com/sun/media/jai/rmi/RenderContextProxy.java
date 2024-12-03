/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.RenderingHintsProxy;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.ROIShape;

public class RenderContextProxy
implements Serializable {
    private transient RenderContext renderContext;

    public RenderContextProxy(RenderContext source) {
        this.renderContext = source;
    }

    public RenderContext getRenderContext() {
        return this.renderContext;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        boolean isNull = this.renderContext == null;
        out.writeBoolean(isNull);
        if (isNull) {
            return;
        }
        AffineTransform usr2dev = this.renderContext.getTransform();
        RenderingHintsProxy rhp = new RenderingHintsProxy(this.renderContext.getRenderingHints());
        Shape aoi = this.renderContext.getAreaOfInterest();
        out.writeObject(usr2dev);
        out.writeBoolean(aoi != null);
        if (aoi != null) {
            if (aoi instanceof Serializable) {
                out.writeObject(aoi);
            } else {
                out.writeObject(new ROIShape(aoi));
            }
        }
        out.writeObject(rhp);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            this.renderContext = null;
            return;
        }
        AffineTransform usr2dev = (AffineTransform)in.readObject();
        Shape shape = null;
        Object aoi = in.readBoolean() ? in.readObject() : null;
        RenderingHintsProxy rhp = (RenderingHintsProxy)in.readObject();
        RenderingHints hints = rhp.getRenderingHints();
        if (aoi != null) {
            shape = aoi instanceof ROIShape ? ((ROIShape)aoi).getAsShape() : (Shape)aoi;
        }
        this.renderContext = aoi == null && hints.isEmpty() ? new RenderContext(usr2dev) : (aoi == null ? new RenderContext(usr2dev, hints) : (hints.isEmpty() ? new RenderContext(usr2dev, shape) : new RenderContext(usr2dev, shape, hints)));
    }
}

