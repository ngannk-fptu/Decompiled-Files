/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.vcache.marshallers;

import com.atlassian.annotations.Internal;
import com.atlassian.vcache.Marshaller;
import com.atlassian.vcache.MarshallerException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Deprecated
@Internal
class StringMarshaller
implements Marshaller<String> {
    private static final Charset ENCODING_CHARSET = StandardCharsets.UTF_8;

    StringMarshaller() {
    }

    @Override
    public byte[] marshall(String str) throws MarshallerException {
        CharsetEncoder encoder = ENCODING_CHARSET.newEncoder();
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        try {
            ByteBuffer buffer = encoder.encode(CharBuffer.wrap(str));
            return Arrays.copyOf(buffer.array(), buffer.limit());
        }
        catch (CharacterCodingException e) {
            throw new MarshallerException("Unable to encode: " + str, e);
        }
    }

    @Override
    public String unmarshall(byte[] raw) {
        return new String(raw, ENCODING_CHARSET);
    }
}

