/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.MimeUtility
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MimeTypeUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.util.CollectionUtils;
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
        this.supportedMediaTypes.add(MediaType.MULTIPART_MIXED);
        this.partConverters.add(new ByteArrayHttpMessageConverter());
        this.partConverters.add(new StringHttpMessageConverter());
        this.partConverters.add(new ResourceHttpMessageConverter());
        this.applyDefaultCharset();
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        Assert.notNull(supportedMediaTypes, (String)"'supportedMediaTypes' must not be null");
        this.supportedMediaTypes = new ArrayList<MediaType>(supportedMediaTypes);
    }

    public void addSupportedMediaTypes(MediaType ... supportedMediaTypes) {
        Assert.notNull((Object)supportedMediaTypes, (String)"'supportedMediaTypes' must not be null");
        Assert.noNullElements((Object[])supportedMediaTypes, (String)"'supportedMediaTypes' must not contain null elements");
        Collections.addAll(this.supportedMediaTypes, supportedMediaTypes);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
        Assert.notEmpty(partConverters, (String)"'partConverters' must not be empty");
        this.partConverters = partConverters;
    }

    public List<HttpMessageConverter<?>> getPartConverters() {
        return Collections.unmodifiableList(this.partConverters);
    }

    public void addPartConverter(HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, (String)"'partConverter' must not be null");
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
            if (supportedMediaType.getType().equalsIgnoreCase("multipart") || !supportedMediaType.includes(mediaType)) continue;
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
        String body = StreamUtils.copyToString((InputStream)inputMessage.getBody(), (Charset)charset);
        String[] pairs = StringUtils.tokenizeToStringArray((String)body, (String)"&");
        LinkedMultiValueMap result = new LinkedMultiValueMap(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf(61);
            if (idx == -1) {
                result.add((Object)URLDecoder.decode(pair, charset.name()), null);
                continue;
            }
            String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
            String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
            result.add((Object)name, (Object)value);
        }
        return result;
    }

    @Override
    public void write(MultiValueMap<String, ?> map, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (this.isMultipart(map, contentType)) {
            this.writeMultipart(map, contentType, outputMessage);
        } else {
            this.writeForm(map, contentType, outputMessage);
        }
    }

    private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
        if (contentType != null) {
            return contentType.getType().equalsIgnoreCase("multipart");
        }
        for (List values : map.values()) {
            for (Object value : values) {
                if (value == null || value instanceof String) continue;
                return true;
            }
        }
        return false;
    }

    private void writeForm(MultiValueMap<String, Object> formData, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        contentType = this.getFormContentType(contentType);
        outputMessage.getHeaders().setContentType(contentType);
        Charset charset = contentType.getCharset();
        Assert.notNull((Object)charset, (String)"No charset");
        byte[] bytes = this.serializeForm(formData, charset).getBytes(charset);
        outputMessage.getHeaders().setContentLength(bytes.length);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(outputStream -> StreamUtils.copy((byte[])bytes, (OutputStream)outputStream));
        } else {
            StreamUtils.copy((byte[])bytes, (OutputStream)outputMessage.getBody());
        }
    }

    protected MediaType getFormContentType(@Nullable MediaType contentType) {
        if (contentType == null) {
            return DEFAULT_FORM_DATA_MEDIA_TYPE;
        }
        if (contentType.getCharset() == null) {
            return new MediaType(contentType, this.charset);
        }
        return contentType;
    }

    protected String serializeForm(MultiValueMap<String, Object> formData, Charset charset) {
        StringBuilder builder = new StringBuilder();
        formData.forEach((name, values) -> {
            if (name == null) {
                Assert.isTrue((boolean)CollectionUtils.isEmpty((Collection)values), () -> "Null name in form data: " + formData);
                return;
            }
            values.forEach(value -> {
                try {
                    if (builder.length() != 0) {
                        builder.append('&');
                    }
                    builder.append(URLEncoder.encode(name, charset.name()));
                    if (value != null) {
                        builder.append('=');
                        builder.append(URLEncoder.encode(String.valueOf(value), charset.name()));
                    }
                }
                catch (UnsupportedEncodingException ex) {
                    throw new IllegalStateException(ex);
                }
            });
        });
        return builder.toString();
    }

    private void writeMultipart(MultiValueMap<String, Object> parts, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        if (contentType == null) {
            contentType = MediaType.MULTIPART_FORM_DATA;
        }
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>(contentType.getParameters().size() + 2);
        parameters.putAll(contentType.getParameters());
        byte[] boundary = this.generateMultipartBoundary();
        if (!(this.isFilenameCharsetSet() || this.charset.equals(StandardCharsets.UTF_8) || this.charset.equals(StandardCharsets.US_ASCII))) {
            parameters.put("charset", this.charset.name());
        }
        parameters.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
        contentType = new MediaType(contentType, parameters);
        outputMessage.getHeaders().setContentType(contentType);
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
                multipartMessage.getHeaders().putAll((Map<? extends String, ? extends List<String>>)((Object)partHeaders));
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

