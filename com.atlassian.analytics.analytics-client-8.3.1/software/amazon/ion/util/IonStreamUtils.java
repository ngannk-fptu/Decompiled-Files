/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.util;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.impl.PrivateIonConstants;
import software.amazon.ion.impl.PrivateListWriter;
import software.amazon.ion.util.GzipOrRawInputStream;

public class IonStreamUtils {
    public static boolean isIonBinary(byte[] buffer) {
        return buffer != null && IonStreamUtils.isIonBinary(buffer, 0, buffer.length);
    }

    public static boolean isIonBinary(byte[] buffer, int offset, int length) {
        return IonStreamUtils.cookieMatches(PrivateIonConstants.BINARY_VERSION_MARKER_1_0, buffer, offset, length);
    }

    public static boolean isGzip(byte[] buffer, int offset, int length) {
        return IonStreamUtils.cookieMatches(GzipOrRawInputStream.GZIP_HEADER, buffer, offset, length);
    }

    private static boolean cookieMatches(byte[] cookie, byte[] buffer, int offset, int length) {
        if (buffer == null || length < cookie.length) {
            return false;
        }
        for (int i = 0; i < cookie.length; ++i) {
            if (cookie[i] == buffer[offset + i]) continue;
            return false;
        }
        return true;
    }

    public static InputStream unGzip(InputStream in) throws IOException {
        return new GzipOrRawInputStream(in);
    }

    public static void writeBoolList(IonWriter writer, boolean[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeBoolList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeBool(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeFloatList(IonWriter writer, float[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeFloatList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeFloat(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeFloatList(IonWriter writer, double[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeFloatList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeFloat(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeIntList(IonWriter writer, byte[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeIntList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeInt(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeIntList(IonWriter writer, short[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeIntList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeInt(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeIntList(IonWriter writer, int[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeIntList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeInt(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeIntList(IonWriter writer, long[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeIntList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeInt(values[ii]);
        }
        writer.stepOut();
    }

    public static void writeStringList(IonWriter writer, String[] values) throws IOException {
        if (writer instanceof PrivateListWriter) {
            ((PrivateListWriter)writer).writeStringList(values);
            return;
        }
        writer.stepIn(IonType.LIST);
        for (int ii = 0; ii < values.length; ++ii) {
            writer.writeString(values[ii]);
        }
        writer.stepOut();
    }
}

