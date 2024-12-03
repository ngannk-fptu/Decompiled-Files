/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.MimeEnabledImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImage;

public class ImageRawStream
extends AbstractImage {
    private ImageFlavor flavor;
    private InputStreamFactory streamFactory;

    public ImageRawStream(ImageInfo info, ImageFlavor flavor, InputStreamFactory streamFactory) {
        super(info);
        this.flavor = flavor;
        this.setInputStreamFactory(streamFactory);
    }

    public ImageRawStream(ImageInfo info, ImageFlavor flavor, InputStream in) {
        this(info, flavor, new SingleStreamFactory(in));
    }

    @Override
    public ImageFlavor getFlavor() {
        return this.flavor;
    }

    public String getMimeType() {
        if (this.getFlavor() instanceof MimeEnabledImageFlavor) {
            return this.getFlavor().getMimeType();
        }
        return "application/octet-stream";
    }

    @Override
    public boolean isCacheable() {
        return !this.streamFactory.isUsedOnceOnly();
    }

    public void setInputStreamFactory(InputStreamFactory factory) {
        if (this.streamFactory != null) {
            this.streamFactory.close();
        }
        this.streamFactory = factory;
    }

    public InputStream createInputStream() {
        return this.streamFactory.createInputStream();
    }

    public void writeTo(OutputStream out) throws IOException {
        InputStream in = this.createInputStream();
        try {
            IOUtils.copy((InputStream)in, (OutputStream)out);
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
    }

    public static class ByteArrayStreamFactory
    implements InputStreamFactory {
        private byte[] data;

        public ByteArrayStreamFactory(byte[] data) {
            this.data = data;
        }

        @Override
        public InputStream createInputStream() {
            return new ByteArrayInputStream(this.data);
        }

        @Override
        public void close() {
        }

        @Override
        public boolean isUsedOnceOnly() {
            return false;
        }
    }

    private static class SingleStreamFactory
    implements InputStreamFactory {
        private InputStream in;

        public SingleStreamFactory(InputStream in) {
            this.in = in;
        }

        @Override
        public synchronized InputStream createInputStream() {
            if (this.in != null) {
                InputStream tempin = this.in;
                this.in = null;
                return tempin;
            }
            throw new IllegalStateException("Can only create an InputStream once!");
        }

        @Override
        public synchronized void close() {
            IOUtils.closeQuietly((InputStream)this.in);
            this.in = null;
        }

        @Override
        public boolean isUsedOnceOnly() {
            return true;
        }

        protected void finalize() {
            this.close();
        }
    }

    public static interface InputStreamFactory {
        public boolean isUsedOnceOnly();

        public InputStream createInputStream();

        public void close();
    }
}

