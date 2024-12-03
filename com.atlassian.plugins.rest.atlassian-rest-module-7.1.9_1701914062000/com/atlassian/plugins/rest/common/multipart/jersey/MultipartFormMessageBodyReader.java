/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.fileupload.RequestContext
 */
package com.atlassian.plugins.rest.common.multipart.jersey;

import com.atlassian.plugins.rest.common.multipart.MultipartConfig;
import com.atlassian.plugins.rest.common.multipart.MultipartConfigClass;
import com.atlassian.plugins.rest.common.multipart.MultipartForm;
import com.atlassian.plugins.rest.common.multipart.fileupload.CommonsFileUploadMultipartHandler;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.apache.commons.fileupload.RequestContext;

@Provider
public class MultipartFormMessageBodyReader
implements MessageBodyReader<MultipartForm> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(MultipartForm.class);
    }

    @Override
    public MultipartForm readFrom(Class<MultipartForm> type, Type genericType, Annotation[] annotations, final MediaType mediaType, MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        CommonsFileUploadMultipartHandler handler = this.getMultipartHandler(annotations);
        return handler.getForm(new RequestContext(){

            public String getCharacterEncoding() {
                return AbstractMessageReaderWriterProvider.getCharset(mediaType).name();
            }

            public String getContentType() {
                return mediaType.toString();
            }

            public int getContentLength() {
                return -1;
            }

            public InputStream getInputStream() throws IOException {
                return entityStream;
            }
        });
    }

    private CommonsFileUploadMultipartHandler getMultipartHandler(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof MultipartConfigClass)) continue;
            Class<? extends MultipartConfig> configClass = ((MultipartConfigClass)annotation).value();
            try {
                MultipartConfig multipartConfig = configClass.newInstance();
                return new CommonsFileUploadMultipartHandler(multipartConfig.getMaxFileSize(), multipartConfig.getMaxSize(), multipartConfig.getMaxFileCount());
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return new CommonsFileUploadMultipartHandler();
    }
}

