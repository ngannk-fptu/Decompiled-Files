/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.DecodeException
 *  javax.websocket.Decoder$Binary
 *  javax.websocket.Decoder$Text
 *  javax.websocket.EncodeException
 *  javax.websocket.Encoder$Binary
 *  javax.websocket.Encoder$Text
 *  javax.websocket.EndpointConfig
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.core.convert.ConversionException
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.context.ContextLoader
 */
package org.springframework.web.socket.adapter.standard;

import java.nio.ByteBuffer;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoader;

public abstract class ConvertingEncoderDecoderSupport<T, M> {
    private static final String CONVERSION_SERVICE_BEAN_NAME = "webSocketConversionService";

    public void init(EndpointConfig config) {
        ApplicationContext applicationContext = this.getApplicationContext();
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext)applicationContext).getBeanFactory();
            beanFactory.autowireBean((Object)this);
        }
    }

    public void destroy() {
    }

    protected ConversionService getConversionService() {
        ApplicationContext applicationContext = this.getApplicationContext();
        Assert.state((applicationContext != null ? 1 : 0) != 0, (String)"Unable to locate the Spring ApplicationContext");
        try {
            return (ConversionService)applicationContext.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class);
        }
        catch (BeansException ex) {
            throw new IllegalStateException("Unable to find ConversionService: please configure a 'webSocketConversionService' or override the getConversionService() method", ex);
        }
    }

    @Nullable
    protected ApplicationContext getApplicationContext() {
        return ContextLoader.getCurrentWebApplicationContext();
    }

    protected TypeDescriptor getType() {
        return TypeDescriptor.valueOf(this.resolveTypeArguments()[0]);
    }

    protected TypeDescriptor getMessageType() {
        return TypeDescriptor.valueOf(this.resolveTypeArguments()[1]);
    }

    private Class<?>[] resolveTypeArguments() {
        Class[] resolved = GenericTypeResolver.resolveTypeArguments(this.getClass(), ConvertingEncoderDecoderSupport.class);
        if (resolved == null) {
            throw new IllegalStateException("ConvertingEncoderDecoderSupport's generic types T and M need to be substituted in subclass: " + this.getClass());
        }
        return resolved;
    }

    @Nullable
    public M encode(T object) throws EncodeException {
        try {
            return (M)this.getConversionService().convert(object, this.getType(), this.getMessageType());
        }
        catch (ConversionException ex) {
            throw new EncodeException(object, "Unable to encode websocket message using ConversionService", (Throwable)ex);
        }
    }

    public boolean willDecode(M bytes) {
        return this.getConversionService().canConvert(this.getType(), this.getMessageType());
    }

    @Nullable
    public T decode(M message) throws DecodeException {
        try {
            return (T)this.getConversionService().convert(message, this.getMessageType(), this.getType());
        }
        catch (ConversionException ex) {
            if (message instanceof String) {
                throw new DecodeException((String)message, "Unable to decode websocket message using ConversionService", (Throwable)ex);
            }
            if (message instanceof ByteBuffer) {
                throw new DecodeException((ByteBuffer)message, "Unable to decode websocket message using ConversionService", (Throwable)ex);
            }
            throw ex;
        }
    }

    public static abstract class TextDecoder<T>
    extends ConvertingEncoderDecoderSupport<T, String>
    implements Decoder.Text<T> {
    }

    public static abstract class TextEncoder<T>
    extends ConvertingEncoderDecoderSupport<T, String>
    implements Encoder.Text<T> {
    }

    public static abstract class BinaryDecoder<T>
    extends ConvertingEncoderDecoderSupport<T, ByteBuffer>
    implements Decoder.Binary<T> {
    }

    public static abstract class BinaryEncoder<T>
    extends ConvertingEncoderDecoderSupport<T, ByteBuffer>
    implements Encoder.Binary<T> {
    }
}

