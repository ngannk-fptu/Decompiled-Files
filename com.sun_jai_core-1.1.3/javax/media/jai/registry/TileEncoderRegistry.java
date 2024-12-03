/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.tilecodec.TileEncoder;
import javax.media.jai.tilecodec.TileEncoderFactory;

public final class TileEncoderRegistry {
    private static final String MODE_NAME = "tileEncoder";

    public static void register(OperationRegistry registry, String formatName, String productName, TileEncoderFactory tef) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.registerFactory(MODE_NAME, formatName, productName, tef);
    }

    public static void unregister(OperationRegistry registry, String formatName, String productName, TileEncoderFactory tef) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unregisterFactory(MODE_NAME, formatName, productName, tef);
    }

    public static void setPreference(OperationRegistry registry, String formatName, String productName, TileEncoderFactory preferredTEF, TileEncoderFactory otherTEF) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.setFactoryPreference(MODE_NAME, formatName, productName, preferredTEF, otherTEF);
    }

    public static void unsetPreference(OperationRegistry registry, String formatName, String productName, TileEncoderFactory preferredTEF, TileEncoderFactory otherTEF) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unsetFactoryPreference(MODE_NAME, formatName, productName, preferredTEF, otherTEF);
    }

    public static void clearPreferences(OperationRegistry registry, String formatName, String productName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.clearFactoryPreferences(MODE_NAME, formatName, productName);
    }

    public static List getOrderedList(OperationRegistry registry, String formatName, String productName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return registry.getOrderedFactoryList(MODE_NAME, formatName, productName);
    }

    public static Iterator getIterator(OperationRegistry registry, String formatName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return registry.getFactoryIterator(MODE_NAME, formatName);
    }

    public static TileEncoderFactory get(OperationRegistry registry, String formatName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return (TileEncoderFactory)registry.getFactory(MODE_NAME, formatName);
    }

    public static TileEncoder create(OperationRegistry registry, String formatName, OutputStream output, TileCodecParameterList paramList, SampleModel sampleModel) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        Object[] args = new Object[]{output, paramList, sampleModel};
        return (TileEncoder)registry.invokeFactory(MODE_NAME, formatName, args);
    }

    public static void encode(OperationRegistry registry, String formatName, Raster raster, OutputStream output, TileCodecParameterList param) throws IOException {
        TileEncoder encoder = TileEncoderRegistry.create(registry, formatName, output, param, raster.getSampleModel());
        if (encoder != null) {
            encoder.encode(raster);
        }
    }
}

