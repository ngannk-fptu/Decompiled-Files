/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.PDFAsImage;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.swing.NaiveUserAgent;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.ContentTypeDetectingInputStreamWrapper;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class ITextUserAgent
extends NaiveUserAgent {
    private static final int IMAGE_CACHE_CAPACITY = 32;
    private SharedContext _sharedContext;
    private final ITextOutputDevice _outputDevice;

    public ITextUserAgent(ITextOutputDevice outputDevice) {
        super(Configuration.valueAsInt("xr.image.cache-capacity", 32));
        this._outputDevice = outputDevice;
    }

    private byte[] readStream(InputStream is) throws IOException {
        int i;
        ByteArrayOutputStream out = new ByteArrayOutputStream(is.available());
        byte[] buf = new byte[10240];
        while ((i = is.read(buf)) != -1) {
            out.write(buf, 0, i);
        }
        out.close();
        return out.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ImageResource getImageResource(String uriStr) {
        ImageResource resource;
        if (!ImageUtil.isEmbeddedBase64Image(uriStr)) {
            uriStr = this.resolveURI(uriStr);
        }
        if ((resource = (ImageResource)this._imageCache.get(uriStr)) == null) {
            if (ImageUtil.isEmbeddedBase64Image(uriStr)) {
                resource = this.loadEmbeddedBase64ImageResource(uriStr);
                this._imageCache.put(uriStr, resource);
            } else {
                InputStream is = this.resolveAndOpenStream(uriStr);
                if (is != null) {
                    try {
                        ContentTypeDetectingInputStreamWrapper cis = new ContentTypeDetectingInputStreamWrapper(is);
                        is = cis;
                        if (cis.isPdf()) {
                            URI uri = new URI(uriStr);
                            PdfReader reader = this._outputDevice.getReader(uri);
                            PDFAsImage image = new PDFAsImage(uri);
                            Rectangle rect = reader.getPageSizeWithRotation(1);
                            image.setInitialWidth(rect.getWidth() * this._outputDevice.getDotsPerPoint());
                            image.setInitialHeight(rect.getHeight() * this._outputDevice.getDotsPerPoint());
                            resource = new ImageResource(uriStr, image);
                        } else {
                            Image image = Image.getInstance(this.readStream(is));
                            this.scaleToOutputResolution(image);
                            resource = new ImageResource(uriStr, new ITextFSImage(image));
                        }
                        this._imageCache.put(uriStr, resource);
                    }
                    catch (Exception e) {
                        XRLog.exception("Can't read image file; unexpected problem for URI '" + uriStr + "'", e);
                    }
                    finally {
                        try {
                            is.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
            }
        }
        if (resource != null) {
            FSImage image = resource.getImage();
            if (image instanceof ITextFSImage) {
                image = (FSImage)((ITextFSImage)resource.getImage()).clone();
            }
            resource = new ImageResource(resource.getImageUri(), image);
        } else {
            resource = new ImageResource(uriStr, null);
        }
        return resource;
    }

    private ImageResource loadEmbeddedBase64ImageResource(String uri) {
        try {
            byte[] buffer = ImageUtil.getEmbeddedBase64Image(uri);
            Image image = Image.getInstance(buffer);
            this.scaleToOutputResolution(image);
            return new ImageResource(null, new ITextFSImage(image));
        }
        catch (Exception e) {
            XRLog.exception("Can't read XHTML embedded image.", e);
            return new ImageResource(null, null);
        }
    }

    private void scaleToOutputResolution(Image image) {
        float factor = this._sharedContext.getDotsPerPixel();
        if (factor != 1.0f) {
            image.scaleAbsolute(image.getPlainWidth() * factor, image.getPlainHeight() * factor);
        }
    }

    public SharedContext getSharedContext() {
        return this._sharedContext;
    }

    public void setSharedContext(SharedContext sharedContext) {
        this._sharedContext = sharedContext;
    }
}

