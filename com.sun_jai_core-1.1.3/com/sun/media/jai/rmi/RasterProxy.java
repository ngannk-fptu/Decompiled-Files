/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class RasterProxy
implements Serializable {
    private transient Raster raster;

    public RasterProxy(Raster source) {
        this.raster = source;
    }

    public Raster getRaster() {
        return this.raster;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Raster r;
        if (this.raster.getParent() != null) {
            r = this.raster.createCompatibleWritableRaster(this.raster.getBounds());
            ((WritableRaster)r).setRect(this.raster);
        } else {
            r = this.raster;
        }
        out.writeInt(r.getWidth());
        out.writeInt(r.getHeight());
        out.writeObject(SerializerFactory.getState(r.getSampleModel(), null));
        out.writeObject(SerializerFactory.getState(r.getDataBuffer(), null));
        out.writeObject(new Point(r.getMinX(), r.getMinY()));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        SerializableState sampleModelState = null;
        SerializableState dataBufferState = null;
        Point location = null;
        int width = in.readInt();
        int height = in.readInt();
        sampleModelState = (SerializableState)in.readObject();
        dataBufferState = (SerializableState)in.readObject();
        location = (Point)in.readObject();
        SampleModel sampleModel = (SampleModel)sampleModelState.getObject();
        if (sampleModel == null) {
            this.raster = null;
            return;
        }
        DataBuffer dataBuffer = (DataBuffer)dataBufferState.getObject();
        this.raster = Raster.createRaster(sampleModel, dataBuffer, location);
    }
}

