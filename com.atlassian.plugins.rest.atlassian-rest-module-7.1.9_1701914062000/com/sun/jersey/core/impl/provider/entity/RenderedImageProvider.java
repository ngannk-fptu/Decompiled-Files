/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Produces(value={"image/*"})
@Consumes(value={"image/*", "application/octet-stream"})
public final class RenderedImageProvider
extends AbstractMessageReaderWriterProvider<RenderedImage> {
    private static final MediaType IMAGE_MEDIA_TYPE = new MediaType("image", "*");

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return RenderedImage.class == type || BufferedImage.class == type;
    }

    @Override
    public RenderedImage readFrom(Class<RenderedImage> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        if (IMAGE_MEDIA_TYPE.isCompatible(mediaType)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mediaType.toString());
            if (!readers.hasNext()) {
                throw new IOException("The image-based media type " + mediaType + "is not supported for reading");
            }
            ImageReader reader = readers.next();
            ImageInputStream in = ImageIO.createImageInputStream(entityStream);
            reader.setInput(in, true, true);
            BufferedImage bi = reader.read(0, reader.getDefaultReadParam());
            in.close();
            reader.dispose();
            return bi;
        }
        return ImageIO.read(entityStream);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return RenderedImage.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(RenderedImage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        String formatName = this.getWriterFormatName(mediaType);
        if (formatName == null) {
            throw new IOException("The image-based media type " + mediaType + " is not supported for writing");
        }
        ImageIO.write(t, formatName, entityStream);
    }

    private String getWriterFormatName(MediaType t) {
        return this.getWriterFormatName(t.toString());
    }

    private String getWriterFormatName(String t) {
        Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType(t);
        if (!i.hasNext()) {
            return null;
        }
        return i.next().getOriginatingProvider().getFormatNames()[0];
    }
}

