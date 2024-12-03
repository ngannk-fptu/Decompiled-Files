/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.FileCacheImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

public class BufferedImageHttpMessageConverter
implements HttpMessageConverter<BufferedImage> {
    private final List<MediaType> readableMediaTypes = new ArrayList<MediaType>();
    @Nullable
    private MediaType defaultContentType;
    @Nullable
    private File cacheDir;

    public BufferedImageHttpMessageConverter() {
        String[] writerMediaTypes;
        String[] readerMediaTypes;
        for (String mediaType : readerMediaTypes = ImageIO.getReaderMIMETypes()) {
            if (!StringUtils.hasText((String)mediaType)) continue;
            this.readableMediaTypes.add(MediaType.parseMediaType(mediaType));
        }
        for (String mediaType : writerMediaTypes = ImageIO.getWriterMIMETypes()) {
            if (!StringUtils.hasText((String)mediaType)) continue;
            this.defaultContentType = MediaType.parseMediaType(mediaType);
            break;
        }
    }

    public void setDefaultContentType(@Nullable MediaType defaultContentType) {
        Iterator<ImageWriter> imageWriters;
        if (defaultContentType != null && !(imageWriters = ImageIO.getImageWritersByMIMEType(defaultContentType.toString())).hasNext()) {
            throw new IllegalArgumentException("Content-Type [" + defaultContentType + "] is not supported by the Java Image I/O API");
        }
        this.defaultContentType = defaultContentType;
    }

    @Nullable
    public MediaType getDefaultContentType() {
        return this.defaultContentType;
    }

    public void setCacheDir(File cacheDir) {
        Assert.notNull((Object)cacheDir, (String)"'cacheDir' must not be null");
        Assert.isTrue((boolean)cacheDir.isDirectory(), () -> "'cacheDir' is not a directory: " + cacheDir);
        this.cacheDir = cacheDir;
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return BufferedImage.class == clazz && this.isReadable(mediaType);
    }

    private boolean isReadable(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(mediaType.toString());
        return imageReaders.hasNext();
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return BufferedImage.class == clazz && this.isWritable(mediaType);
    }

    private boolean isWritable(@Nullable MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equalsTypeAndSubtype(mediaType)) {
            return true;
        }
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(mediaType.toString());
        return imageWriters.hasNext();
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.readableMediaTypes);
    }

    @Override
    public BufferedImage read(@Nullable Class<? extends BufferedImage> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ImageInputStream imageInputStream = null;
        ImageReader imageReader = null;
        try {
            imageInputStream = this.createImageInputStream(inputMessage.getBody());
            MediaType contentType = inputMessage.getHeaders().getContentType();
            if (contentType == null) {
                throw new HttpMessageNotReadableException("No Content-Type header", inputMessage);
            }
            Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByMIMEType(contentType.toString());
            if (imageReaders.hasNext()) {
                imageReader = imageReaders.next();
                ImageReadParam irp = imageReader.getDefaultReadParam();
                this.process(irp);
                imageReader.setInput(imageInputStream, true);
                BufferedImage bufferedImage = imageReader.read(0, irp);
                return bufferedImage;
            }
            throw new HttpMessageNotReadableException("Could not find javax.imageio.ImageReader for Content-Type [" + contentType + "]", inputMessage);
        }
        finally {
            if (imageReader != null) {
                imageReader.dispose();
            }
            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private ImageInputStream createImageInputStream(InputStream is) throws IOException {
        is = StreamUtils.nonClosing((InputStream)is);
        if (this.cacheDir != null) {
            return new FileCacheImageInputStream(is, this.cacheDir);
        }
        return new MemoryCacheImageInputStream(is);
    }

    @Override
    public void write(BufferedImage image, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType selectedContentType = this.getContentType(contentType);
        outputMessage.getHeaders().setContentType(selectedContentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(outputStream -> this.writeInternal(image, selectedContentType, outputStream));
        } else {
            this.writeInternal(image, selectedContentType, outputMessage.getBody());
        }
    }

    private MediaType getContentType(@Nullable MediaType contentType) {
        if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
            contentType = this.getDefaultContentType();
        }
        Assert.notNull((Object)contentType, (String)"Could not select Content-Type. Please specify one through the 'defaultContentType' property.");
        return contentType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeInternal(BufferedImage image, MediaType contentType, OutputStream body) throws IOException, HttpMessageNotWritableException {
        block10: {
            ImageInputStream imageOutputStream = null;
            ImageWriter imageWriter = null;
            try {
                Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(contentType.toString());
                if (imageWriters.hasNext()) {
                    imageWriter = imageWriters.next();
                    ImageWriteParam iwp = imageWriter.getDefaultWriteParam();
                    this.process(iwp);
                    imageOutputStream = this.createImageOutputStream(body);
                    imageWriter.setOutput(imageOutputStream);
                    imageWriter.write(null, new IIOImage(image, null, null), iwp);
                    break block10;
                }
                throw new HttpMessageNotWritableException("Could not find javax.imageio.ImageWriter for Content-Type [" + contentType + "]");
            }
            finally {
                if (imageWriter != null) {
                    imageWriter.dispose();
                }
                if (imageOutputStream != null) {
                    try {
                        imageOutputStream.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
    }

    private ImageOutputStream createImageOutputStream(OutputStream os) throws IOException {
        if (this.cacheDir != null) {
            return new FileCacheImageOutputStream(os, this.cacheDir);
        }
        return new MemoryCacheImageOutputStream(os);
    }

    protected void process(ImageReadParam irp) {
    }

    protected void process(ImageWriteParam iwp) {
    }
}

