/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.TiledImage;
import javax.media.jai.remote.SerializableRenderedImage;
import javax.media.jai.tilecodec.TileCodecParameterList;

public final class RenderedImageState
extends SerializableStateImpl {
    private boolean isWritable;
    private transient boolean useDeepCopy;
    private transient OperationRegistry registry;
    private transient String formatName;
    private transient TileCodecParameterList encodingParam;
    private transient TileCodecParameterList decodingParam;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$java$awt$image$WritableRenderedImage;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = RenderedImageState.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage, class$java$awt$image$WritableRenderedImage == null ? (class$java$awt$image$WritableRenderedImage = RenderedImageState.class$("java.awt.image.WritableRenderedImage")) : class$java$awt$image$WritableRenderedImage};
    }

    public RenderedImageState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
        this.isWritable = o instanceof WritableRenderedImage;
        if (h != null) {
            Object value = h.get(JAI.KEY_SERIALIZE_DEEP_COPY);
            this.useDeepCopy = value != null ? (Boolean)value : false;
            value = h.get(JAI.KEY_OPERATION_REGISTRY);
            if (value != null) {
                this.registry = (OperationRegistry)value;
            }
            if ((value = h.get(JAI.KEY_TILE_CODEC_FORMAT)) != null) {
                this.formatName = (String)value;
            }
            if ((value = h.get(JAI.KEY_TILE_ENCODING_PARAM)) != null) {
                this.encodingParam = (TileCodecParameterList)value;
            }
            if ((value = h.get(JAI.KEY_TILE_DECODING_PARAM)) != null) {
                this.decodingParam = (TileCodecParameterList)value;
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        SerializableRenderedImage sri = this.formatName == null || this.encodingParam == null || this.decodingParam == null ? new SerializableRenderedImage((RenderedImage)this.theObject, this.useDeepCopy) : new SerializableRenderedImage((RenderedImage)this.theObject, this.useDeepCopy, this.registry, this.formatName, this.encodingParam, this.decodingParam);
        out.writeObject(sri);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.theObject = in.readObject();
        if (this.isWritable) {
            this.theObject = new TiledImage((RenderedImage)this.theObject, true);
        }
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

