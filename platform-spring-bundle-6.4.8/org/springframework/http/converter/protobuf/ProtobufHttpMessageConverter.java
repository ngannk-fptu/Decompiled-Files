/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.CodedOutputStream
 *  com.google.protobuf.ExtensionRegistry
 *  com.google.protobuf.ExtensionRegistryLite
 *  com.google.protobuf.Message
 *  com.google.protobuf.Message$Builder
 *  com.google.protobuf.MessageOrBuilder
 *  com.google.protobuf.TextFormat
 *  com.google.protobuf.util.JsonFormat
 *  com.google.protobuf.util.JsonFormat$Parser
 *  com.google.protobuf.util.JsonFormat$Printer
 *  com.googlecode.protobuf.format.FormatFactory
 *  com.googlecode.protobuf.format.FormatFactory$Formatter
 *  com.googlecode.protobuf.format.ProtobufFormatter
 */
package org.springframework.http.converter.protobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import com.google.protobuf.util.JsonFormat;
import com.googlecode.protobuf.format.FormatFactory;
import com.googlecode.protobuf.format.ProtobufFormatter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.protobuf.ExtensionRegistryInitializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

public class ProtobufHttpMessageConverter
extends AbstractHttpMessageConverter<Message> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);
    public static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
    public static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";
    private static final Map<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap();
    final ExtensionRegistry extensionRegistry;
    @Nullable
    private final ProtobufFormatSupport protobufFormatSupport;

    public ProtobufHttpMessageConverter() {
        this((ProtobufFormatSupport)null, (ExtensionRegistry)null);
    }

    @Deprecated
    public ProtobufHttpMessageConverter(@Nullable ExtensionRegistryInitializer registryInitializer) {
        this((ProtobufFormatSupport)null, (ExtensionRegistry)null);
        if (registryInitializer != null) {
            registryInitializer.initializeExtensionRegistry(this.extensionRegistry);
        }
    }

    public ProtobufHttpMessageConverter(ExtensionRegistry extensionRegistry) {
        this(null, extensionRegistry);
    }

    ProtobufHttpMessageConverter(@Nullable ProtobufFormatSupport formatSupport, @Nullable ExtensionRegistry extensionRegistry) {
        MediaType[] mediaTypeArray;
        this.protobufFormatSupport = formatSupport != null ? formatSupport : (ClassUtils.isPresent("com.googlecode.protobuf.format.FormatFactory", this.getClass().getClassLoader()) ? new ProtobufJavaFormatSupport() : (ClassUtils.isPresent("com.google.protobuf.util.JsonFormat", this.getClass().getClassLoader()) ? new ProtobufJavaUtilSupport(null, null) : null));
        if (this.protobufFormatSupport != null) {
            mediaTypeArray = this.protobufFormatSupport.supportedMediaTypes();
        } else {
            MediaType[] mediaTypeArray2 = new MediaType[2];
            mediaTypeArray2[0] = PROTOBUF;
            mediaTypeArray = mediaTypeArray2;
            mediaTypeArray2[1] = MediaType.TEXT_PLAIN;
        }
        this.setSupportedMediaTypes(Arrays.asList(mediaTypeArray));
        this.extensionRegistry = extensionRegistry == null ? ExtensionRegistry.newInstance() : extensionRegistry;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    @Override
    protected MediaType getDefaultContentType(Message message) {
        return PROTOBUF;
    }

    @Override
    protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Charset charset;
        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        if ((charset = contentType.getCharset()) == null) {
            charset = DEFAULT_CHARSET;
        }
        Message.Builder builder = this.getMessageBuilder(clazz);
        if (PROTOBUF.isCompatibleWith(contentType)) {
            builder.mergeFrom(inputMessage.getBody(), (ExtensionRegistryLite)this.extensionRegistry);
        } else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            InputStreamReader reader = new InputStreamReader(inputMessage.getBody(), charset);
            TextFormat.merge((Readable)reader, (ExtensionRegistry)this.extensionRegistry, (Message.Builder)builder);
        } else if (this.protobufFormatSupport != null) {
            this.protobufFormatSupport.merge(inputMessage.getBody(), charset, contentType, this.extensionRegistry, builder);
        }
        return builder.build();
    }

    private Message.Builder getMessageBuilder(Class<? extends Message> clazz) {
        try {
            Method method = methodCache.get(clazz);
            if (method == null) {
                method = clazz.getMethod("newBuilder", new Class[0]);
                methodCache.put(clazz, method);
            }
            return (Message.Builder)method.invoke(clazz, new Object[0]);
        }
        catch (Exception ex) {
            throw new HttpMessageConversionException("Invalid Protobuf Message type: no invocable newBuilder() method on " + clazz, ex);
        }
    }

    @Override
    protected boolean canWrite(@Nullable MediaType mediaType) {
        return super.canWrite(mediaType) || this.protobufFormatSupport != null && this.protobufFormatSupport.supportsWriteOnly(mediaType);
    }

    @Override
    protected void writeInternal(Message message, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Charset charset;
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = this.getDefaultContentType(message);
            Assert.state(contentType != null, "No content type");
        }
        if ((charset = contentType.getCharset()) == null) {
            charset = DEFAULT_CHARSET;
        }
        if (PROTOBUF.isCompatibleWith(contentType)) {
            this.setProtoHeader(outputMessage, message);
            CodedOutputStream codedOutputStream = CodedOutputStream.newInstance((OutputStream)outputMessage.getBody());
            message.writeTo(codedOutputStream);
            codedOutputStream.flush();
        } else if (MediaType.TEXT_PLAIN.isCompatibleWith(contentType)) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(), charset);
            TextFormat.print((MessageOrBuilder)message, (Appendable)outputStreamWriter);
            outputStreamWriter.flush();
            outputMessage.getBody().flush();
        } else if (this.protobufFormatSupport != null) {
            this.protobufFormatSupport.print(message, outputMessage.getBody(), contentType, charset);
            outputMessage.getBody().flush();
        }
    }

    private void setProtoHeader(HttpOutputMessage response, Message message) {
        response.getHeaders().set(X_PROTOBUF_SCHEMA_HEADER, message.getDescriptorForType().getFile().getName());
        response.getHeaders().set(X_PROTOBUF_MESSAGE_HEADER, message.getDescriptorForType().getFullName());
    }

    static class ProtobufJavaUtilSupport
    implements ProtobufFormatSupport {
        private final JsonFormat.Parser parser;
        private final JsonFormat.Printer printer;

        public ProtobufJavaUtilSupport(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer) {
            this.parser = parser != null ? parser : JsonFormat.parser();
            this.printer = printer != null ? printer : JsonFormat.printer();
        }

        @Override
        public MediaType[] supportedMediaTypes() {
            return new MediaType[]{PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON};
        }

        @Override
        public boolean supportsWriteOnly(@Nullable MediaType mediaType) {
            return false;
        }

        @Override
        public void merge(InputStream input, Charset charset, MediaType contentType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException {
            if (!contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                throw new HttpMessageConversionException("protobuf-java-util does not support parsing " + contentType);
            }
            InputStreamReader reader = new InputStreamReader(input, charset);
            this.parser.merge((Reader)reader, builder);
        }

        @Override
        public void print(Message message, OutputStream output, MediaType contentType, Charset charset) throws IOException, HttpMessageConversionException {
            if (!contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                throw new HttpMessageConversionException("protobuf-java-util does not support printing " + contentType);
            }
            OutputStreamWriter writer = new OutputStreamWriter(output, charset);
            this.printer.appendTo((MessageOrBuilder)message, (Appendable)writer);
            writer.flush();
        }
    }

    static class ProtobufJavaFormatSupport
    implements ProtobufFormatSupport {
        private final ProtobufFormatter jsonFormatter;
        private final ProtobufFormatter xmlFormatter;
        private final ProtobufFormatter htmlFormatter;

        public ProtobufJavaFormatSupport() {
            FormatFactory formatFactory = new FormatFactory();
            this.jsonFormatter = formatFactory.createFormatter(FormatFactory.Formatter.JSON);
            this.xmlFormatter = formatFactory.createFormatter(FormatFactory.Formatter.XML);
            this.htmlFormatter = formatFactory.createFormatter(FormatFactory.Formatter.HTML);
        }

        @Override
        public MediaType[] supportedMediaTypes() {
            return new MediaType[]{PROTOBUF, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON};
        }

        @Override
        public boolean supportsWriteOnly(@Nullable MediaType mediaType) {
            return MediaType.TEXT_HTML.isCompatibleWith(mediaType);
        }

        @Override
        public void merge(InputStream input, Charset charset, MediaType contentType, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException, HttpMessageConversionException {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                this.jsonFormatter.merge(input, charset, extensionRegistry, builder);
            } else if (contentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
                this.xmlFormatter.merge(input, charset, extensionRegistry, builder);
            } else {
                throw new HttpMessageConversionException("protobuf-java-format does not support parsing " + contentType);
            }
        }

        @Override
        public void print(Message message, OutputStream output, MediaType contentType, Charset charset) throws IOException, HttpMessageConversionException {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                this.jsonFormatter.print(message, output, charset);
            } else if (contentType.isCompatibleWith(MediaType.APPLICATION_XML)) {
                this.xmlFormatter.print(message, output, charset);
            } else if (contentType.isCompatibleWith(MediaType.TEXT_HTML)) {
                this.htmlFormatter.print(message, output, charset);
            } else {
                throw new HttpMessageConversionException("protobuf-java-format does not support printing " + contentType);
            }
        }
    }

    static interface ProtobufFormatSupport {
        public MediaType[] supportedMediaTypes();

        public boolean supportsWriteOnly(@Nullable MediaType var1);

        public void merge(InputStream var1, Charset var2, MediaType var3, ExtensionRegistry var4, Message.Builder var5) throws IOException, HttpMessageConversionException;

        public void print(Message var1, OutputStream var2, MediaType var3, Charset var4) throws IOException, HttpMessageConversionException;
    }
}

