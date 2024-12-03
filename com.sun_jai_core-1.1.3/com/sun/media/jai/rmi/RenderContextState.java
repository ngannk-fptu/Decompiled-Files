/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class RenderContextState
extends SerializableStateImpl {
    static /* synthetic */ Class class$java$awt$image$renderable$RenderContext;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$awt$image$renderable$RenderContext == null ? (class$java$awt$image$renderable$RenderContext = RenderContextState.class$("java.awt.image.renderable.RenderContext")) : class$java$awt$image$renderable$RenderContext};
    }

    public RenderContextState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        RenderContext renderContext = (RenderContext)this.theObject;
        AffineTransform usr2dev = renderContext.getTransform();
        RenderingHints hints = renderContext.getRenderingHints();
        Shape aoi = renderContext.getAreaOfInterest();
        out.writeObject(usr2dev);
        out.writeObject(SerializerFactory.getState(aoi));
        out.writeObject(SerializerFactory.getState(hints, null));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        RenderContext renderContext = null;
        AffineTransform usr2dev = (AffineTransform)in.readObject();
        SerializableState aoi = (SerializableState)in.readObject();
        Shape shape = (Shape)aoi.getObject();
        SerializableState rhs = (SerializableState)in.readObject();
        RenderingHints hints = (RenderingHints)rhs.getObject();
        renderContext = new RenderContext(usr2dev, shape, hints);
        this.theObject = renderContext;
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

