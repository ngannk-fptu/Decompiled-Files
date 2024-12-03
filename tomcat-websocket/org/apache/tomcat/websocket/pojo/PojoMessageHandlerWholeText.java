/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DecodeException
 *  javax.websocket.Decoder
 *  javax.websocket.Decoder$Text
 *  javax.websocket.Decoder$TextStream
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Session
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket.pojo;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import javax.naming.NamingException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.pojo.PojoMessageHandlerWholeBase;

public class PojoMessageHandlerWholeText
extends PojoMessageHandlerWholeBase<String> {
    private static final StringManager sm = StringManager.getManager(PojoMessageHandlerWholeText.class);
    private final Class<?> primitiveType;

    public PojoMessageHandlerWholeText(Object pojo, Method method, Session session, EndpointConfig config, List<Class<? extends Decoder>> decoderClazzes, Object[] params, int indexPayload, boolean convert, int indexSession, long maxMessageSize) {
        super(pojo, method, session, params, indexPayload, convert, indexSession, maxMessageSize);
        Class<?> type;
        if (maxMessageSize > -1L && maxMessageSize > (long)session.getMaxTextMessageBufferSize()) {
            if (maxMessageSize > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(sm.getString("pojoMessageHandlerWhole.maxBufferSize"));
            }
            session.setMaxTextMessageBufferSize((int)maxMessageSize);
        }
        if (Util.isPrimitive(type = method.getParameterTypes()[indexPayload])) {
            this.primitiveType = type;
            return;
        }
        this.primitiveType = null;
        try {
            if (decoderClazzes != null) {
                for (Class<? extends Decoder> decoderClazz : decoderClazzes) {
                    Decoder.Text decoder;
                    if (Decoder.Text.class.isAssignableFrom(decoderClazz)) {
                        decoder = (Decoder.Text)this.createDecoderInstance(decoderClazz);
                        decoder.init(config);
                        this.decoders.add(decoder);
                        continue;
                    }
                    if (!Decoder.TextStream.class.isAssignableFrom(decoderClazz)) continue;
                    decoder = (Decoder.TextStream)this.createDecoderInstance(decoderClazz);
                    decoder.init(config);
                    this.decoders.add(decoder);
                }
            }
        }
        catch (ReflectiveOperationException | NamingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    protected Object decode(String message) throws DecodeException {
        if (this.primitiveType != null) {
            return Util.coerceToType(this.primitiveType, message);
        }
        for (Decoder decoder : this.decoders) {
            if (decoder instanceof Decoder.Text) {
                if (!((Decoder.Text)decoder).willDecode(message)) continue;
                return ((Decoder.Text)decoder).decode(message);
            }
            StringReader r = new StringReader(message);
            try {
                return ((Decoder.TextStream)decoder).decode((Reader)r);
            }
            catch (IOException ioe) {
                throw new DecodeException(message, sm.getString("pojoMessageHandlerWhole.decodeIoFail"), (Throwable)ioe);
            }
        }
        return null;
    }

    @Override
    protected Object convert(String message) {
        return new StringReader(message);
    }
}

