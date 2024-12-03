/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.swing.UriResolver;
import org.xhtmlrenderer.util.IOUtil;
import org.xhtmlrenderer.util.StreamResource;

public class DelegatingUserAgent
implements UserAgentCallback,
DocumentListener {
    private UriResolver _uriResolver = new UriResolver();
    private ImageResourceLoader _imageResourceLoader;

    public void setImageResourceLoader(ImageResourceLoader loader) {
        this._imageResourceLoader = loader;
    }

    public void shrinkImageCache() {
        this._imageResourceLoader.shrink();
    }

    public void clearImageCache() {
        this._imageResourceLoader.clear();
    }

    protected InputStream resolveAndOpenStream(String uri) {
        return IOUtil.openStreamAtUrl(this._uriResolver.resolve(uri));
    }

    @Override
    public CSSResource getCSSResource(String uri) {
        return new CSSResource(this.resolveAndOpenStream(uri));
    }

    @Override
    public ImageResource getImageResource(String uri) {
        return this._imageResourceLoader.get(this.resolveURI(uri));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XMLResource getXMLResource(String uri) {
        String ruri = this._uriResolver.resolve(uri);
        StreamResource sr = new StreamResource(ruri);
        try {
            sr.connect();
            BufferedInputStream bis = sr.bufferedStream();
            XMLResource xMLResource = XMLResource.load(bis);
            return xMLResource;
        }
        catch (IOException e) {
            XMLResource xMLResource = null;
            return xMLResource;
        }
        finally {
            sr.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getBinaryResource(String uri) {
        String ruri = this._uriResolver.resolve(uri);
        StreamResource sr = new StreamResource(ruri);
        try {
            int i;
            sr.connect();
            BufferedInputStream bis = sr.bufferedStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream(sr.hasStreamLength() ? sr.streamLength() : 4096);
            byte[] buf = new byte[10240];
            while ((i = bis.read(buf)) != -1) {
                result.write(buf, 0, i);
            }
            byte[] byArray = result.toByteArray();
            return byArray;
        }
        catch (IOException e) {
            byte[] byArray = null;
            return byArray;
        }
        finally {
            sr.close();
        }
    }

    @Override
    public boolean isVisited(String uri) {
        return false;
    }

    @Override
    public void setBaseURL(String uri) {
        this._uriResolver.setBaseUri(uri);
    }

    @Override
    public String resolveURI(String uri) {
        return this._uriResolver.resolve(uri);
    }

    @Override
    public String getBaseURL() {
        return this._uriResolver.getBaseUri();
    }

    @Override
    public void documentStarted() {
        this._imageResourceLoader.stopLoading();
        this.shrinkImageCache();
    }

    @Override
    public void documentLoaded() {
    }

    @Override
    public void onLayoutException(Throwable t) {
    }

    @Override
    public void onRenderException(Throwable t) {
    }

    public void setRepaintListener(RepaintListener listener) {
    }
}

