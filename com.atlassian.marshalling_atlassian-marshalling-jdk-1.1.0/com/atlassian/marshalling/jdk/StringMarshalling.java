/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.marshalling.api.Marshaller
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.marshalling.api.Unmarshaller
 */
package com.atlassian.marshalling.jdk;

import com.atlassian.annotations.PublicApi;
import com.atlassian.marshalling.api.Marshaller;
import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.marshalling.api.Unmarshaller;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@PublicApi
public class StringMarshalling
implements Marshaller<String>,
Unmarshaller<String> {
    private static final Charset ENCODING_CHARSET = StandardCharsets.UTF_8;

    public byte[] marshallToBytes(String str) throws MarshallingException {
        CharsetEncoder encoder = ENCODING_CHARSET.newEncoder();
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        try {
            ByteBuffer buffer = encoder.encode(CharBuffer.wrap(str));
            return Arrays.copyOf(buffer.array(), buffer.limit());
        }
        catch (CharacterCodingException e) {
            throw new MarshallingException("Unable to encode: " + str, (Throwable)e);
        }
    }

    public String unmarshallFrom(byte[] raw) throws MarshallingException {
        return new String(raw, ENCODING_CHARSET);
    }

    public static MarshallingPair<String> pair() {
        StringMarshalling sm = new StringMarshalling();
        return new MarshallingPair((Marshaller)sm, (Unmarshaller)sm);
    }
}

