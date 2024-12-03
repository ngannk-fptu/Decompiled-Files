/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DecodeException
 *  javax.websocket.Decoder
 *  javax.websocket.Decoder$Binary
 *  javax.websocket.Decoder$BinaryStream
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Session
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.pojo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;
import javax.naming.NamingException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase;

public class PojoMessageHandlerWholeBinary
extends PojoMessageHandlerWholeBase<ByteBuffer> {
    private static final StringManager sm = StringManager.getManager(PojoMessageHandlerWholeBinary.class);
    private final boolean isForInputStream;

    public PojoMessageHandlerWholeBinary(Object pojo, Method method, Session session, EndpointConfig config, List<Class<? extends Decoder>> decoderClazzes, Object[] params, int indexPayload, boolean convert, int indexSession, boolean isForInputStream, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        if (maxMessageSize > -1L && maxMessageSize > (long)session.getMaxBinaryMessageBufferSize()) {
            if (maxMessageSize > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(sm.getString("pojoMessageHandlerWhole.maxBufferSize"));
            }
            session.setMaxBinaryMessageBufferSize((int)maxMessageSize);
        }
        try {
            if (decoderClazzes != null) {
                for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                    Decoder.Binary decoder;
                    if (Decoder.Binary.class.isAssignableFrom(decoderClazz)) {
                        decoder = (Decoder.Binary)this.createDecoderInstance(decoderClazz);
                        decoder.init(config);
                        this.decoders.add(decoder);
                        continue;
                    }
                    if (!Decoder.BinaryStream.class.isAssignableFrom(decoderClazz)) continue;
                    decoder = (Decoder.BinaryStream)this.createDecoderInstance(decoderClazz);
                    decoder.init(config);
                    this.decoders.add(decoder);
                }
            }
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new IllegalArgumentException(e);
        }
        this.isForInputStream = isForInputStream;
    }

    @Override
    protected Object decode(ByteBuffer message) throws DecodeException {
        for (Decoder decoder : this.decoders) {
            if (decoder instanceof Decoder.Binary) {
                if (!((Decoder.Binary)decoder).willDecode(message)) continue;
                return ((Decoder.Binary)decoder).decode(message);
            }
            byte[] array = new byte[message.limit() - message.position()];
            message.get(array);
            ByteArrayInputStream bais = new ByteArrayInputStream(array);
            try {
                return ((Decoder.BinaryStream)decoder).decode((InputStream)bais);
            }
            catch (IOException ioe) {
                throw new DecodeException(message, sm.getString("pojoMessageHandlerWhole.decodeIoFail"), (Throwable)ioe);
            }
        }
        return null;
    }

    @Override
    protected Object convert(ByteBuffer message) {
        byte[] array = new byte[message.remaining()];
        message.get(array);
        if (this.isForInputStream) {
            return new ByteArrayInputStream(array);
        }
        return array;
    }
}

