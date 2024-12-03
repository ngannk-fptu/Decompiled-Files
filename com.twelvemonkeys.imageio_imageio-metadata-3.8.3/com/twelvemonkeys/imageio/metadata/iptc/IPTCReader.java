/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.iptc;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.metadata.iptc.IPTC;
import com.twelvemonkeys.imageio.metadata.iptc.IPTCDirectory;
import com.twelvemonkeys.imageio.metadata.iptc.IPTCEntry;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.LinkedHashMap;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

public final class IPTCReader
extends MetadataReader {
    private static final int ENCODING_UNKNOWN = -1;
    private static final int ENCODING_UNSPECIFIED = 0;
    private static final int ENCODING_UTF_8 = 1779015;
    private int encoding = 0;

    @Override
    public Directory read(ImageInputStream imageInputStream) throws IOException {
        Validate.notNull((Object)imageInputStream, (String)"input");
        LinkedHashMap<Short, IPTCEntry> linkedHashMap = new LinkedHashMap<Short, IPTCEntry>();
        while (imageInputStream.read() == 28) {
            boolean bl;
            int n;
            short s = imageInputStream.readShort();
            IPTCEntry iPTCEntry = this.readEntry(imageInputStream, s, n = imageInputStream.readUnsignedShort(), bl, (bl = IPTC.Tags.isArray(s)) ? (Entry)linkedHashMap.get(s) : null);
            if (iPTCEntry == null) continue;
            linkedHashMap.put(s, iPTCEntry);
        }
        return new IPTCDirectory(linkedHashMap.values());
    }

    private IPTCEntry mergeEntries(short s, Object object, Entry entry) {
        Object[] objectArray;
        Object[] objectArray2;
        Object[] objectArray3 = objectArray2 = entry != null ? (Object[])entry.getValue() : null;
        if (object instanceof String) {
            if (objectArray2 == null) {
                objectArray = new String[]{(String)object};
            } else {
                String[] stringArray = (String[])objectArray2;
                objectArray = Arrays.copyOf(stringArray, stringArray.length + 1);
                objectArray[objectArray.length - 1] = object;
            }
        } else if (objectArray2 == null) {
            objectArray = new Object[]{object};
        } else {
            objectArray = Arrays.copyOf(objectArray2, objectArray2.length + 1);
            objectArray[objectArray.length - 1] = object;
        }
        return new IPTCEntry(s, (Object)objectArray);
    }

    private IPTCEntry readEntry(ImageInputStream imageInputStream, short s, int n, boolean bl, Entry entry) throws IOException {
        Object object;
        switch (s) {
            case 346: {
                this.encoding = this.parseEncoding(imageInputStream, n);
                return null;
            }
            case 512: {
                object = imageInputStream.readUnsignedShort();
                break;
            }
            default: {
                if ((s & 0xFF00) == 512) {
                    if (n < 1) {
                        object = null;
                        break;
                    }
                    object = this.parseString(imageInputStream, n);
                    break;
                }
                byte[] byArray = new byte[n];
                imageInputStream.readFully(byArray);
                object = byArray;
            }
        }
        return bl ? this.mergeEntries(s, object, entry) : new IPTCEntry(s, object);
    }

    private int parseEncoding(ImageInputStream imageInputStream, int n) throws IOException {
        return n == 3 && (imageInputStream.readUnsignedByte() << 16 | imageInputStream.readUnsignedByte() << 8 | imageInputStream.readUnsignedByte()) == 1779015 ? 1779015 : -1;
    }

    private String parseString(ImageInputStream imageInputStream, int n) throws IOException {
        byte[] byArray = new byte[n];
        imageInputStream.readFully(byArray);
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder charsetDecoder = charset.newDecoder();
        try {
            CharBuffer charBuffer = charsetDecoder.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(byArray));
            return charBuffer.toString();
        }
        catch (CharacterCodingException characterCodingException) {
            if (this.encoding == 1779015) {
                throw new IIOException("Wrong encoding of IPTC data, explicitly set to UTF-8 in DataSet 1:90", characterCodingException);
            }
            return StringUtil.decode((byte[])byArray, (int)0, (int)byArray.length, (String)"ISO8859_1");
        }
    }
}

