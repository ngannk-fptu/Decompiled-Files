/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.internal.serialization.InternalSerializationService
 *  com.hazelcast.internal.serialization.impl.ObjectDataInputStream
 *  com.hazelcast.nio.Bits
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.ObjectDataInputStream;
import com.hazelcast.nio.Bits;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParanoidObjectDataInputStream
extends ObjectDataInputStream {
    private static final Logger log = LoggerFactory.getLogger(ParanoidObjectDataInputStream.class);
    private static final int UTF_LIMIT = 32768;

    public ParanoidObjectDataInputStream(InputStream in, InternalSerializationService serializationService) {
        super(in, serializationService);
    }

    public String readUTF() throws IOException {
        int charCount = this.readInt();
        if (charCount == -1) {
            return null;
        }
        if (charCount > 32768) {
            log.warn("Rejecting request to read {} UTF characters", (Object)charCount);
            throw new UTFDataFormatException("Rejecting request to read " + charCount + " UTF characters");
        }
        char[] charBuffer = new char[charCount];
        for (int i = 0; i < charCount; ++i) {
            byte b = this.readByte();
            charBuffer[i] = b < 0 ? Bits.readUtf8Char((DataInput)((Object)this), (byte)b) : (char)b;
        }
        return new String(charBuffer, 0, charCount);
    }
}

