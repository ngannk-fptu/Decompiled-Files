/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.tilecodec;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.text.MessageFormat;
import javax.media.jai.JAI;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;
import javax.media.jai.tilecodec.TileCodecDescriptor;

public class TileCodecUtils {
    private static MessageFormat formatter = new MessageFormat("");
    static /* synthetic */ Class class$java$awt$image$Raster;
    static /* synthetic */ Class class$java$awt$image$SampleModel;

    public static TileCodecDescriptor getTileCodecDescriptor(String registryMode, String formatName) {
        return (TileCodecDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(registryMode, formatName);
    }

    public static Raster deserializeRaster(Object object) {
        SerializableState ss;
        Class c;
        if (!(object instanceof SerializableState)) {
            return null;
        }
        if ((class$java$awt$image$Raster == null ? (class$java$awt$image$Raster = TileCodecUtils.class$("java.awt.image.Raster")) : class$java$awt$image$Raster).isAssignableFrom(c = (ss = (SerializableState)object).getObjectClass())) {
            return (Raster)ss.getObject();
        }
        return null;
    }

    public static SampleModel deserializeSampleModel(Object object) {
        SerializableState ss;
        Class c;
        if (!(object instanceof SerializableState)) {
            return null;
        }
        if ((class$java$awt$image$SampleModel == null ? (class$java$awt$image$SampleModel = TileCodecUtils.class$("java.awt.image.SampleModel")) : class$java$awt$image$SampleModel).isAssignableFrom(c = (ss = (SerializableState)object).getObjectClass())) {
            return (SampleModel)ss.getObject();
        }
        return null;
    }

    public static Object serializeRaster(Raster ras) {
        return SerializerFactory.getState(ras, null);
    }

    public static Object serializeSampleModel(SampleModel sm) {
        return SerializerFactory.getState(sm, null);
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

