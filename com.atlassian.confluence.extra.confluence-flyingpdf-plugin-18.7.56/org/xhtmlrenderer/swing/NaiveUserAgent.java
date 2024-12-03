/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.util.FontUtil;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class NaiveUserAgent
implements UserAgentCallback,
DocumentListener {
    private static final int DEFAULT_IMAGE_CACHE_SIZE = 16;
    protected LinkedHashMap _imageCache;
    private int _imageCacheCapacity;
    private String _baseURL;

    public NaiveUserAgent() {
        this(16);
    }

    public NaiveUserAgent(int imgCacheSize) {
        this._imageCacheCapacity = imgCacheSize;
        this._imageCache = new LinkedHashMap(this._imageCacheCapacity, 0.75f, true);
    }

    public void shrinkImageCache() {
        int ovr = this._imageCache.size() - this._imageCacheCapacity;
        Iterator it = this._imageCache.keySet().iterator();
        while (it.hasNext() && ovr-- > 0) {
            it.next();
            it.remove();
        }
    }

    public void clearImageCache() {
        this._imageCache.clear();
    }

    protected InputStream resolveAndOpenStream(String uri) {
        InputStream is = null;
        String resolvedUri = this.resolveURI(uri);
        try {
            is = FontUtil.isEmbeddedBase64Font(uri).booleanValue() ? FontUtil.getEmbeddedBase64Data(uri) : this.openStream(resolvedUri);
        }
        catch (MalformedURLException e) {
            XRLog.exception("bad URL given: " + resolvedUri, e);
        }
        catch (FileNotFoundException e) {
            XRLog.exception("item at URI " + resolvedUri + " not found");
        }
        catch (IOException e) {
            XRLog.exception("IO problem for " + resolvedUri, e);
        }
        return is;
    }

    protected InputStream openStream(String uri) throws MalformedURLException, IOException {
        return this.openConnection(uri).getInputStream();
    }

    protected URLConnection openConnection(String uri) throws IOException {
        URLConnection connection = new URL(uri).openConnection();
        if (connection instanceof HttpURLConnection) {
            connection = this.onHttpConnection((HttpURLConnection)connection);
        }
        return connection;
    }

    protected URLConnection onHttpConnection(HttpURLConnection origin) throws MalformedURLException, IOException {
        URLConnection connection = origin;
        int status = origin.getResponseCode();
        if (this.needsRedirect(status)) {
            String newUrl = origin.getHeaderField("Location");
            if (origin.getInstanceFollowRedirects()) {
                XRLog.load("Connection is redirected to: " + newUrl);
                connection = new URL(newUrl).openConnection();
            } else {
                XRLog.load("Redirect is required but not allowed to: " + newUrl);
            }
        }
        return connection;
    }

    protected final boolean needsRedirect(int status) {
        return status != 200 && (status == 302 || status == 301 || status == 303);
    }

    @Override
    public CSSResource getCSSResource(String uri) {
        return new CSSResource(this.resolveAndOpenStream(uri));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ImageResource getImageResource(String uri) {
        ImageResource ir;
        if (ImageUtil.isEmbeddedBase64Image(uri)) {
            BufferedImage image = ImageUtil.loadEmbeddedBase64Image(uri);
            ir = this.createImageResource(null, image);
        } else {
            InputStream is;
            ir = (ImageResource)this._imageCache.get(uri = this.resolveURI(uri));
            if (ir == null && (is = this.resolveAndOpenStream(uri)) != null) {
                try {
                    BufferedImage img = ImageIO.read(is);
                    if (img == null) {
                        throw new IOException("ImageIO.read() returned null");
                    }
                    ir = this.createImageResource(uri, img);
                    this._imageCache.put(uri, ir);
                }
                catch (FileNotFoundException e) {
                    XRLog.exception("Can't read image file; image at URI '" + uri + "' not found");
                }
                catch (IOException e) {
                    XRLog.exception("Can't read image file; unexpected problem for URI '" + uri + "'", e);
                }
                finally {
                    try {
                        is.close();
                    }
                    catch (IOException e) {}
                }
            }
            if (ir == null) {
                ir = this.createImageResource(uri, null);
            }
        }
        return ir;
    }

    protected ImageResource createImageResource(String uri, Image img) {
        return new ImageResource(uri, AWTFSImage.createImage(img));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XMLResource getXMLResource(String uri) {
        XMLResource xmlResource;
        InputStream inputStream = this.resolveAndOpenStream(uri);
        try {
            xmlResource = XMLResource.load(inputStream);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
        return xmlResource;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getBinaryResource(String uri) {
        InputStream is = this.resolveAndOpenStream(uri);
        if (is == null) {
            return null;
        }
        try {
            int i;
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buf = new byte[10240];
            while ((i = is.read(buf)) != -1) {
                result.write(buf, 0, i);
            }
            is.close();
            is = null;
            byte[] byArray = result.toByteArray();
            return byArray;
        }
        catch (IOException e) {
            byte[] byArray = null;
            return byArray;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    @Override
    public boolean isVisited(String uri) {
        return false;
    }

    @Override
    public void setBaseURL(String url) {
        this._baseURL = url;
    }

    @Override
    public String resolveURI(String uri) {
        Exception t;
        if (uri == null) {
            return null;
        }
        if (this._baseURL == null) {
            try {
                URI result = new URI(uri);
                if (result.isAbsolute()) {
                    this.setBaseURL(result.toString());
                }
            }
            catch (URISyntaxException e) {
                XRLog.exception("The default NaiveUserAgent could not use the URL as base url: " + uri, e);
            }
            if (this._baseURL == null) {
                try {
                    this.setBaseURL(new File(".").toURI().toURL().toExternalForm());
                }
                catch (Exception e1) {
                    XRLog.exception("The default NaiveUserAgent doesn't know how to resolve the base URL for " + uri);
                    return null;
                }
            }
        }
        try {
            URI result = new URI(uri);
            if (result.isAbsolute()) {
                return result.toString();
            }
            XRLog.load(uri + " is not a URL; may be relative. Testing using parent URL " + this._baseURL);
            URI baseURI = new URI(this._baseURL);
            if (!baseURI.isOpaque()) {
                return baseURI.resolve(result).toString();
            }
            try {
                return new URL(new URL(this._baseURL), uri).toExternalForm();
            }
            catch (MalformedURLException ex) {
                t = ex;
            }
        }
        catch (URISyntaxException e) {
            t = e;
        }
        XRLog.exception("The default NaiveUserAgent cannot resolve the URL " + uri + " with base URL " + this._baseURL, t);
        return null;
    }

    @Override
    public String getBaseURL() {
        return this._baseURL;
    }

    @Override
    public void documentStarted() {
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
}

