/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.GIFImage;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class GIFImageDecoder
extends ImageDecoderImpl {
    private byte[] globalColorTable = null;
    private boolean maxPageFound = false;
    private int maxPage;
    private int prevPage = -1;
    private int prevSyncedPage = -1;
    private HashMap images = new HashMap();
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$GIFImageDecoder;

    private static byte[] readHeader(SeekableStream input) throws IOException {
        byte[] globalColorTable = null;
        try {
            input.skipBytes(10);
            int packedFields = input.readUnsignedByte();
            boolean globalColorTableFlag = (packedFields & 0x80) != 0;
            int numGCTEntries = 1 << (packedFields & 7) + 1;
            int backgroundColorIndex = input.readUnsignedByte();
            input.read();
            if (globalColorTableFlag) {
                globalColorTable = new byte[3 * numGCTEntries];
                input.readFully(globalColorTable);
            } else {
                globalColorTable = null;
            }
        }
        catch (IOException e) {
            String message = JaiI18N.getString("GIFImageDecoder0");
            ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), class$com$sun$media$jai$codecimpl$GIFImageDecoder == null ? (class$com$sun$media$jai$codecimpl$GIFImageDecoder = GIFImageDecoder.class$("com.sun.media.jai.codecimpl.GIFImageDecoder")) : class$com$sun$media$jai$codecimpl$GIFImageDecoder, false);
        }
        return globalColorTable;
    }

    public GIFImageDecoder(SeekableStream input, ImageDecodeParam param) {
        super(input, param);
    }

    public GIFImageDecoder(InputStream input, ImageDecodeParam param) {
        super(input, param);
    }

    public int getNumPages() throws IOException {
        int page = this.prevPage + 1;
        while (!this.maxPageFound) {
            try {
                this.decodeAsRenderedImage(page++);
            }
            catch (IOException iOException) {}
        }
        return this.maxPage + 1;
    }

    public synchronized RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page < 0 || this.maxPageFound && page > this.maxPage) {
            throw new IOException(JaiI18N.getString("GIFImageDecoder1"));
        }
        Integer pageKey = new Integer(page);
        if (this.images.containsKey(pageKey)) {
            return (RenderedImage)this.images.get(pageKey);
        }
        if (this.prevPage == -1) {
            try {
                this.globalColorTable = GIFImageDecoder.readHeader(this.input);
            }
            catch (IOException e) {
                this.maxPageFound = true;
                this.maxPage = -1;
                throw e;
            }
        }
        if (page > 0) {
            int idx = this.prevSyncedPage + 1;
            while (idx < page) {
                RenderedImage im = (RenderedImage)this.images.get(new Integer(idx));
                im.getTile(0, 0);
                this.prevSyncedPage = idx++;
            }
        }
        GIFImage image = null;
        while (this.prevPage < page) {
            int index = this.prevPage + 1;
            GIFImage ri = null;
            try {
                ri = new GIFImage(this.input, this.globalColorTable);
                this.images.put(new Integer(index), ri);
                if (index < page) {
                    ri.getTile(0, 0);
                    this.prevSyncedPage = index;
                }
                this.prevPage = index;
                if (index != page) continue;
                image = ri;
                break;
            }
            catch (IOException e) {
                this.maxPageFound = true;
                this.maxPage = this.prevPage;
                String message = JaiI18N.getString("GIFImage3");
                ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
            }
        }
        return image;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

