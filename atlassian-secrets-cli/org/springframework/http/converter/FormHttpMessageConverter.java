/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.MimeUtility
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeUtility;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

public class FormHttpMessageConverter
implements HttpMessageConverter<MultiValueMap<String, ?>> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final MediaType DEFAULT_FORM_DATA_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET);
    private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
    private List<HttpMessageConverter<?>> partConverters = new ArrayList();
    private Charset charset = DEFAULT_CHARSET;
    @Nullable
    private Charset multipartCharset;

    public FormHttpMessageConverter() {
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        this.partConverters.add(new ByteArrayHttpMessageConverter());
        this.partConverters.add(stringHttpMessageConverter);
        this.partConverters.add(new ResourceHttpMessageConverter());
        this.applyDefaultCharset();
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
        Assert.notEmpty(partConverters, "'partConverters' must not be empty");
        this.partConverters = partConverters;
    }

    public void addPartConverter(HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, "'partConverter' must not be null");
        this.partConverters.add(partConverter);
    }

    public void setCharset(@Nullable Charset charset) {
        if (charset != this.charset) {
            this.charset = charset != null ? charset : DEFAULT_CHARSET;
            this.applyDefaultCharset();
        }
    }

    private void applyDefaultCharset() {
        for (HttpMessageConverter<?> candidate : this.partConverters) {
            AbstractHttpMessageConverter converter;
            if (!(candidate instanceof AbstractHttpMessageConverter) || (converter = (AbstractHttpMessageConverter)candidate).getDefaultCharset() == null) continue;
            converter.setDefaultCharset(this.charset);
        }
    }

    public void setMultipartCharset(Charset charset) {
        this.multipartCharset = charset;
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) || !supportedMediaType.includes(mediaType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (!supportedMediaType.isCompatibleWith(mediaType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public MultiValueMap<String, String> read(@Nullable Class<? extends MultiValueMap<String, ?>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = contentType != null && contentType.getCharset() != null ? contentType.getCharset() : this.charset;
        String body = StreamUtils.copyToString(inputMessage.getBody(), charset);
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf(61);
            if (idx == -1) {
                result.add(URLDecoder.decode(pair, charset.name()), null);
                continue;
            }
            String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
            String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
            result.add(name, value);
        }
        return result;
    }

    @Override
    public void write(MultiValueMap<String, ?> map, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (!this.isMultipart(map, contentType)) {
            this.writeForm(map, contentType, outputMessage);
        } else {
            this.writeMultipart(map, outputMessage);
        }
    }

    private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
        if (contentType != null) {
            return MediaType.MULTIPART_FORM_DATA.includes(contentType);
        }
        for (List values : map.values()) {
            for (Object value : values) {
                if (value == null || value instanceof String) continue;
                return true;
            }
        }
        return false;
    }

    private void writeForm(MultiValueMap<String, String> formData, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        contentType = this.getMediaType(contentType);
        outputMessage.getHeaders().setContentType(contentType);
        Charset charset = contentType.getCharset();
        Assert.notNull((Object)charset, "No charset");
        StringBuilder builder = new StringBuilder();
        formData.forEach((name, values) -> values.forEach(value -> {
            try {
                if (builder.length() != 0) {
                    builder.append('&');
                }
                builder.append(URLEncoder.encode(name, charset.name()));
                if (value != null) {
                    builder.append('=');
                    builder.append(URLEncoder.encode(value, charset.name()));
                }
            }
            catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }));
        byte[] bytes = builder.toString().getBytes(charset);
        outputMessage.getHeaders().setContentLength(bytes.length);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(outputStream -> StreamUtils.copy(bytes, outputStream));
        } else {
            StreamUtils.copy(bytes, outputMessage.getBody());
        }
    }

    private MediaType getMediaType(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return DEFAULT_FORM_DATA_MEDIA_TYPE;
        }
        if (mediaType.getCharset() == null) {
            return new MediaType(mediaType, this.charset);
        }
        return mediaType;
    }

    private void writeMultipart(MultiValueMap<String, Object> parts, HttpOutputMessage outputMessage) throws IOException {
        byte[] boundary = this.generateMultipartBoundary();
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>(2);
        if (!this.isFilenameCharsetSet()) {
            parameters.put("charset", this.charset.name());
        }
        parameters.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
        MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(contentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(outputStream -> {
                this.writeParts(outputStream, parts, boundary);
                FormHttpMessageConverter.writeEnd(outputStream, boundary);
            });
        } else {
            this.writeParts(outputMessage.getBody(), parts, boundary);
            FormHttpMessageConverter.writeEnd(outputMessage.getBody(), boundary);
        }
    }

    private boolean isFilenameCharsetSet() {
        return this.multipartCharset != null;
    }

    private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
        for (Map.Entry entry : parts.entrySet()) {
            String name = (String)entry.getKey();
            for (Object part : (List)entry.getValue()) {
                if (part == null) continue;
                this.writeBoundary(os, boundary);
                this.writePart(name, this.getHttpEntity(part), os);
                FormHttpMessageConverter.writeNewLine(os);
            }
        }
    }

    private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
        Object partBody = partEntity.getBody();
        if (partBody == null) {
            throw new IllegalStateException("Empty body for part '" + name + "': " + partEntity);
        }
        Class<?> partType = partBody.getClass();
        HttpHeaders partHeaders = partEntity.getHeaders();
        MediaType partContentType = partHeaders.getContentType();
        for (HttpMessageConverter<?> messageConverter : this.partConverters) {
            if (!messageConverter.canWrite(partType, partContentType)) continue;
            Charset charset = this.isFilenameCharsetSet() ? StandardCharsets.US_ASCII : this.charset;
            MultipartHttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os, charset);
            multipartMessage.getHeaders().setContentDispositionFormData(name, this.getFilename(partBody));
            if (!partHeaders.isEmpty()) {
                multipartMessage.getHeaders().putAll(partHeaders);
            }
            messageConverter.write(partBody, partContentType, multipartMessage);
            return;
        }
        throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter found for request type [" + partType.getName() + "]");
    }

    protected byte[] generateMultipartBoundary() {
        return MimeTypeUtils.generateMultipartBoundary();
    }

    protected HttpEntity<?> getHttpEntity(Object part) {
        return part instanceof HttpEntity ? (HttpEntity<Object>)part : new HttpEntity<Object>(part);
    }

    @Nullable
    protected String getFilename(Object part) {
        if (part instanceof Resource) {
            Resource resource = (Resource)part;
            String filename = resource.getFilename();
            if (filename != null && this.multipartCharset != null) {
                filename = MimeDelegate.encode(filename, this.multipartCharset.name());
            }
            return filename;
        }
        return null;
    }

    private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
        os.write(45);
        os.write(45);
        os.write(boundary);
        FormHttpMessageConverter.writeNewLine(os);
    }

    private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
        os.write(45);
        os.write(45);
        os.write(boundary);
        os.write(45);
        os.write(45);
        FormHttpMessageConverter.writeNewLine(os);
    }

    private static void writeNewLine(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    private static class MimeDelegate {
        private MimeDelegate() {
        }

        public static String encode(String value, String charset) {
            try {
                return MimeUtility.encodeText((String)value, (String)charset, null);
            }
            catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    private static class MultipartHttpOutputMessage
    implements HttpOutputMessage {
        private final OutputStream outputStream;
        private final Charset charset;
        private final HttpHeaders headers = new HttpHeaders();
        private boolean headersWritten = false;

        public MultipartHttpOutputMessage(OutputStream outputStream, Charset charset) {
            this.outputStream = outputStream;
            this.charset = charset;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            this.writeHeaders();
            return this.outputStream;
        }

        private void writeHeaders() throws IOException {
            if (!this.headersWritten) {
                for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
                    byte[] headerName = this.getBytes(entry.getKey());
                    for (String headerValueString : entry.getValue()) {
                        byte[] headerValue = this.getBytes(headerValueString);
                        this.outputStream.write(headerName);
                        this.outputStream.write(58);
                        this.outputStream.write(32);
                        this.outputStream.write(headerValue);
                        FormHttpMessageConverter.writeNewLine(this.outputStream);
                    }
                }
                FormHttpMessageConverter.writeNewLine(this.outputStream);
                this.headersWritten = true;
            }
        }

        private byte[] getBytes(String name) {
            return name.getBytes(this.charset);
        }
    }
}

