/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.zip.GZIPInputStream;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonValue;
import software.amazon.ion.impl.IonReaderBinarySystemX;
import software.amazon.ion.impl.IonReaderBinaryUserX;
import software.amazon.ion.impl.IonReaderTextSystemX;
import software.amazon.ion.impl.IonReaderTextUserX;
import software.amazon.ion.impl.IonReaderTreeSystem;
import software.amazon.ion.impl.IonReaderTreeUserX;
import software.amazon.ion.impl.PrivateIonConstants;
import software.amazon.ion.impl.UnifiedInputStreamX;
import software.amazon.ion.util.IonStreamUtils;

@Deprecated
public final class PrivateIonReaderFactory {
    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, byte[] bytes) {
        return PrivateIonReaderFactory.makeReader(system, catalog, bytes, 0, bytes.length);
    }

    public static IonReader makeSystemReader(IonSystem system, byte[] bytes) {
        return PrivateIonReaderFactory.makeSystemReader(system, bytes, 0, bytes.length);
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, byte[] bytes, int offset, int length) {
        try {
            UnifiedInputStreamX uis = PrivateIonReaderFactory.makeUnifiedStream(bytes, offset, length);
            return PrivateIonReaderFactory.makeReader(system, catalog, uis, offset);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static IonReader makeSystemReader(IonSystem system, byte[] bytes, int offset, int length) {
        try {
            UnifiedInputStreamX uis = PrivateIonReaderFactory.makeUnifiedStream(bytes, offset, length);
            return PrivateIonReaderFactory.makeSystemReader(system, uis, offset);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, char[] chars) {
        return PrivateIonReaderFactory.makeReader(system, catalog, chars, 0, chars.length);
    }

    public static final IonReader makeSystemReader(IonSystem system, char[] chars) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars);
        return new IonReaderTextSystemX(system, in);
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, char[] chars, int offset, int length) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars, offset, length);
        return new IonReaderTextUserX(system, catalog, in, offset);
    }

    public static final IonReader makeSystemReader(IonSystem system, char[] chars, int offset, int length) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars, offset, length);
        return new IonReaderTextSystemX(system, in);
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, CharSequence chars) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars);
        return new IonReaderTextUserX(system, catalog, in);
    }

    public static final IonReader makeSystemReader(IonSystem system, CharSequence chars) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars);
        return new IonReaderTextSystemX(system, in);
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, CharSequence chars, int offset, int length) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars, offset, length);
        return new IonReaderTextUserX(system, catalog, in, offset);
    }

    public static final IonReader makeSystemReader(IonSystem system, CharSequence chars, int offset, int length) {
        UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars, offset, length);
        return new IonReaderTextSystemX(system, in);
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, InputStream is) {
        try {
            UnifiedInputStreamX uis = PrivateIonReaderFactory.makeUnifiedStream(is);
            return PrivateIonReaderFactory.makeReader(system, catalog, uis, 0);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static IonReader makeSystemReader(IonSystem system, InputStream is) {
        try {
            UnifiedInputStreamX uis = PrivateIonReaderFactory.makeUnifiedStream(is);
            return PrivateIonReaderFactory.makeSystemReader(system, uis, 0);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, Reader chars) {
        try {
            UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars);
            return new IonReaderTextUserX(system, catalog, in);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static final IonReader makeSystemReader(IonSystem system, Reader chars) {
        try {
            UnifiedInputStreamX in = UnifiedInputStreamX.makeStream(chars);
            return new IonReaderTextSystemX(system, in);
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public static final IonReader makeReader(IonSystem system, IonCatalog catalog, IonValue value) {
        return new IonReaderTreeUserX(value, catalog);
    }

    public static final IonReader makeSystemReader(IonSystem system, IonValue value) {
        if (system != null && system != value.getSystem()) {
            throw new IonException("you can't mix values from different systems");
        }
        return new IonReaderTreeSystem(value);
    }

    private static IonReader makeReader(IonSystem system, IonCatalog catalog, UnifiedInputStreamX uis, int offset) throws IOException {
        IonReader r = PrivateIonReaderFactory.has_binary_cookie(uis) ? new IonReaderBinaryUserX(system, catalog, uis, offset) : new IonReaderTextUserX(system, catalog, uis, offset);
        return r;
    }

    private static IonReader makeSystemReader(IonSystem system, UnifiedInputStreamX uis, int offset) throws IOException {
        IonReader r = PrivateIonReaderFactory.has_binary_cookie(uis) ? new IonReaderBinarySystemX(system, uis) : new IonReaderTextSystemX(system, uis);
        return r;
    }

    private static UnifiedInputStreamX makeUnifiedStream(byte[] bytes, int offset, int length) throws IOException {
        UnifiedInputStreamX uis;
        if (IonStreamUtils.isGzip(bytes, offset, length)) {
            ByteArrayInputStream baos = new ByteArrayInputStream(bytes, offset, length);
            GZIPInputStream gzip = new GZIPInputStream(baos);
            uis = UnifiedInputStreamX.makeStream(gzip);
        } else {
            uis = UnifiedInputStreamX.makeStream(bytes, offset, length);
        }
        return uis;
    }

    private static UnifiedInputStreamX makeUnifiedStream(InputStream in) throws IOException {
        in.getClass();
        in = IonStreamUtils.unGzip(in);
        UnifiedInputStreamX uis = UnifiedInputStreamX.makeStream(in);
        return uis;
    }

    private static final boolean has_binary_cookie(UnifiedInputStreamX uis) throws IOException {
        int c;
        int len;
        byte[] bytes = new byte[PrivateIonConstants.BINARY_VERSION_MARKER_SIZE];
        for (len = 0; len < PrivateIonConstants.BINARY_VERSION_MARKER_SIZE && (c = uis.read()) != -1; ++len) {
            bytes[len] = (byte)c;
        }
        int ii = len;
        while (ii > 0) {
            uis.unread(bytes[--ii] & 0xFF);
        }
        boolean is_cookie = IonStreamUtils.isIonBinary(bytes, 0, len);
        return is_cookie;
    }
}

