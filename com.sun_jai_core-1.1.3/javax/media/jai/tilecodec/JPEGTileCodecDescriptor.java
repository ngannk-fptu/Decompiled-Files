/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.tilecodec;

import java.awt.image.SampleModel;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.tilecodec.JaiI18N;
import javax.media.jai.tilecodec.TileCodecDescriptorImpl;
import javax.media.jai.tilecodec.TileCodecParameterList;
import javax.media.jai.util.Range;

public class JPEGTileCodecDescriptor
extends TileCodecDescriptorImpl {
    private static final int[] lumQuantizationTable = new int[]{16, 11, 12, 14, 12, 10, 16, 14, 13, 14, 18, 17, 16, 19, 24, 40, 26, 24, 22, 22, 24, 49, 35, 37, 29, 40, 58, 51, 61, 60, 57, 51, 56, 55, 64, 72, 92, 78, 64, 68, 87, 69, 55, 56, 80, 109, 81, 87, 95, 98, 103, 104, 103, 62, 77, 113, 121, 112, 100, 120, 92, 101, 103, 99};
    private static final int[] chromQuantizationTable = new int[]{17, 18, 18, 24, 21, 24, 47, 26, 26, 47, 99, 66, 56, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99};
    private static final String[] paramNames = new String[]{"quality", "qualitySet", "horizontalSubsampling", "verticalSubsampling", "quantizationTableMapping", "quantizationTable0", "quantizationTable1", "quantizationTable2", "quantizationTable3", "restartInterval", "writeImageInfo", "writeTableInfo", "writeJFIFHeader"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = JPEGTileCodecDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JPEGTileCodecDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, array$I == null ? (array$I = JPEGTileCodecDescriptor.class$("[I")) : array$I, class$java$lang$Integer == null ? (class$java$lang$Integer = JPEGTileCodecDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JPEGTileCodecDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JPEGTileCodecDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$java$lang$Boolean == null ? (class$java$lang$Boolean = JPEGTileCodecDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean};
    private static final Object[] paramDefaults = new Object[]{new Float(0.75f), new Boolean(true), new int[]{1, 1, 1}, new int[]{1, 1, 1}, new int[]{0, 1, 1}, lumQuantizationTable, chromQuantizationTable, chromQuantizationTable, chromQuantizationTable, new Integer(0), new Boolean(true), new Boolean(true), new Boolean(false)};
    private static final Object[] validParamValues = new Object[]{new Range(class$java$lang$Float == null ? (class$java$lang$Float = JPEGTileCodecDescriptor.class$("java.lang.Float")) : class$java$lang$Float, new Float(0.0f), new Float(1.0f)), null, null, null, null, null, null, null, null, new Range(class$java$lang$Integer == null ? (class$java$lang$Integer = JPEGTileCodecDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, new Integer(0), null), null, null, null};
    private static ParameterListDescriptor paramListDescriptor = new ParameterListDescriptorImpl(null, paramNames, paramClasses, paramDefaults, validParamValues);
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class array$I;
    static /* synthetic */ Class class$java$lang$Integer;

    public JPEGTileCodecDescriptor() {
        super("jpeg", true, true);
    }

    public TileCodecParameterList getCompatibleParameters(String modeName, TileCodecParameterList otherParamList) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        if (otherParamList == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl3"));
        }
        String name = this.getName();
        if (!otherParamList.getFormatName().equals(name)) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec2"));
        }
        if (otherParamList.isValidForMode(modeName)) {
            return otherParamList;
        }
        if (modeName.equalsIgnoreCase("tileDecoder")) {
            return new TileCodecParameterList(name, new String[]{"tileDecoder"}, otherParamList.getParameterListDescriptor());
        }
        if (modeName.equalsIgnoreCase("tileEncoder")) {
            return new TileCodecParameterList(name, new String[]{"tileEncoder"}, otherParamList.getParameterListDescriptor());
        }
        throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
    }

    public TileCodecParameterList getDefaultParameters(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        String[] validNames = this.getSupportedModes();
        boolean valid = false;
        for (int i = 0; i < validNames.length; ++i) {
            if (!modeName.equalsIgnoreCase(validNames[i])) continue;
            valid = true;
            break;
        }
        if (!valid) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }
        return new TileCodecParameterList("jpeg", new String[]{"tileDecoder", "tileEncoder"}, paramListDescriptor);
    }

    public TileCodecParameterList getDefaultParameters(String modeName, SampleModel sm) {
        return this.getDefaultParameters(modeName);
    }

    public ParameterListDescriptor getParameterListDescriptor(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodecDescriptorImpl1"));
        }
        String[] validNames = this.getSupportedModes();
        boolean valid = false;
        for (int i = 0; i < validNames.length; ++i) {
            if (!modeName.equalsIgnoreCase(validNames[i])) continue;
            valid = true;
            break;
        }
        if (!valid) {
            throw new IllegalArgumentException(JaiI18N.getString("TileCodec1"));
        }
        return paramListDescriptor;
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

