/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.ImageSource;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.image.loader.util.SoftMapCache;
import org.apache.xmlgraphics.io.XmlSourceUtil;

public abstract class AbstractImageSessionContext
implements ImageSessionContext {
    private static final Log log = LogFactory.getLog(AbstractImageSessionContext.class);
    private static boolean noSourceReuse = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

        @Override
        public Boolean run() {
            String noSourceReuseString = System.getProperty(AbstractImageSessionContext.class.getName() + ".no-source-reuse");
            return Boolean.valueOf(noSourceReuseString);
        }
    });
    private final FallbackResolver fallbackResolver;
    private SoftMapCache sessionSources = new SoftMapCache(false);

    public AbstractImageSessionContext() {
        this.fallbackResolver = new UnrestrictedFallbackResolver();
    }

    public AbstractImageSessionContext(FallbackResolver fallbackResolver) {
        this.fallbackResolver = fallbackResolver;
    }

    protected abstract Source resolveURI(String var1);

    @Override
    public Source newSource(String uri) {
        Source source = this.resolveURI(uri);
        if (source instanceof StreamSource || source instanceof SAXSource) {
            return this.fallbackResolver.createSource(source, uri);
        }
        return source;
    }

    protected static ImageInputStream createImageInputStream(InputStream in) throws IOException {
        ImageInputStream iin = ImageIO.createImageInputStream(in);
        return (ImageInputStream)Proxy.newProxyInstance(ImageInputStream.class.getClassLoader(), new Class[]{ImageInputStream.class}, (InvocationHandler)new ObservingImageInputStreamInvocationHandler(iin, in));
    }

    public static File toFile(URL url) {
        if (url == null || !url.getProtocol().equals("file")) {
            return null;
        }
        try {
            String filename = "";
            if (url.getHost() != null && url.getHost().length() > 0) {
                filename = filename + Character.toString(File.separatorChar) + Character.toString(File.separatorChar) + url.getHost();
            }
            filename = filename + url.getFile().replace('/', File.separatorChar);
            File f = new File(filename = URLDecoder.decode(filename, "UTF-8"));
            if (!f.isFile()) {
                return null;
            }
            return f;
        }
        catch (UnsupportedEncodingException uee) {
            assert (false);
            return null;
        }
    }

    @Override
    public Source getSource(String uri) {
        return (Source)this.sessionSources.remove(uri);
    }

    @Override
    public Source needSource(String uri) throws FileNotFoundException {
        Source src = this.getSource(uri);
        if (src == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Creating new Source for " + uri));
            }
            if ((src = this.newSource(uri)) == null) {
                throw new FileNotFoundException("Image not found: " + uri);
            }
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("Reusing Source for " + uri));
        }
        return src;
    }

    @Override
    public void returnSource(String uri, Source src) {
        ImageInputStream in = ImageUtil.getImageInputStream(src);
        try {
            if (in != null && in.getStreamPosition() != 0L) {
                throw new IllegalStateException("ImageInputStream is not reset for: " + uri);
            }
        }
        catch (IOException ioe) {
            XmlSourceUtil.closeQuietly(src);
        }
        if (this.isReusable(src)) {
            log.debug((Object)("Returning Source for " + uri));
            this.sessionSources.put(uri, src);
        } else {
            XmlSourceUtil.closeQuietly(src);
        }
    }

    protected boolean isReusable(Source src) {
        ImageSource is;
        if (noSourceReuse) {
            return false;
        }
        if (src instanceof ImageSource && (is = (ImageSource)src).getImageInputStream() != null) {
            return true;
        }
        return src instanceof DOMSource;
    }

    private static ImageSource createImageSource(InputStream in, Source source) {
        try {
            return new ImageSource(AbstractImageSessionContext.createImageInputStream(ImageUtil.autoDecorateInputStream(in)), source.getSystemId(), false);
        }
        catch (IOException ioe) {
            log.error((Object)("Unable to create ImageInputStream for InputStream from system identifier '" + source.getSystemId() + "' (" + ioe.getMessage() + ")"));
            return null;
        }
    }

    public static final class RestrictedFallbackResolver
    implements FallbackResolver {
        @Override
        public Source createSource(Source source, String uri) {
            if (source == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("URI could not be resolved: " + uri));
                }
                return null;
            }
            if (ImageUtil.hasInputStream(source)) {
                return AbstractImageSessionContext.createImageSource(XmlSourceUtil.getInputStream(source), source);
            }
            throw new UnsupportedOperationException("There are no contingency mechanisms for I/O.");
        }
    }

    public static final class UnrestrictedFallbackResolver
    implements FallbackResolver {
        @Override
        public Source createSource(Source source, String uri) {
            URL url;
            if (source == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("URI could not be resolved: " + uri));
                }
                return null;
            }
            ImageSource imageSource = null;
            String resolvedURI = source.getSystemId();
            try {
                url = new URL(resolvedURI);
            }
            catch (MalformedURLException e) {
                url = null;
            }
            File f = AbstractImageSessionContext.toFile(url);
            if (f != null) {
                boolean directFileAccess = true;
                assert (source instanceof StreamSource || source instanceof SAXSource);
                InputStream in = XmlSourceUtil.getInputStream(source);
                if (in == null) {
                    try {
                        in = new FileInputStream(f);
                    }
                    catch (FileNotFoundException fnfe) {
                        log.error((Object)("Error while opening file. Could not load image from system identifier '" + source.getSystemId() + "' (" + fnfe.getMessage() + ")"));
                        return null;
                    }
                }
                in = ImageUtil.decorateMarkSupported(in);
                try {
                    if (ImageUtil.isGZIPCompressed(in)) {
                        directFileAccess = false;
                    }
                }
                catch (IOException ioe) {
                    log.error((Object)("Error while checking the InputStream for GZIP compression. Could not load image from system identifier '" + source.getSystemId() + "' (" + ioe.getMessage() + ")"));
                    return null;
                }
                if (directFileAccess) {
                    IOUtils.closeQuietly((InputStream)in);
                    try {
                        ImageInputStream newInputStream = ImageIO.createImageInputStream(f);
                        if (newInputStream == null) {
                            log.error((Object)("Unable to create ImageInputStream for local file " + f + " from system identifier '" + source.getSystemId() + "'"));
                            return null;
                        }
                        imageSource = new ImageSource(newInputStream, resolvedURI, true);
                    }
                    catch (IOException ioe) {
                        log.error((Object)("Unable to create ImageInputStream for local file from system identifier '" + source.getSystemId() + "' (" + ioe.getMessage() + ")"));
                    }
                }
            }
            if (imageSource == null) {
                if (XmlSourceUtil.hasReader(source) && !ImageUtil.hasInputStream(source)) {
                    return source;
                }
                InputStream in = XmlSourceUtil.getInputStream(source);
                if (in == null && url != null) {
                    try {
                        in = url.openStream();
                    }
                    catch (Exception ex) {
                        log.error((Object)("Unable to obtain stream from system identifier '" + source.getSystemId() + "'"));
                    }
                }
                if (in == null) {
                    log.error((Object)("The Source that was returned from URI resolution didn't contain an InputStream for URI: " + uri));
                    return null;
                }
                return AbstractImageSessionContext.createImageSource(in, source);
            }
            return imageSource;
        }
    }

    public static interface FallbackResolver {
        public Source createSource(Source var1, String var2);
    }

    private static class ObservingImageInputStreamInvocationHandler
    implements InvocationHandler {
        private ImageInputStream iin;
        private InputStream in;

        public ObservingImageInputStreamInvocationHandler(ImageInputStream iin, InputStream underlyingStream) {
            this.iin = iin;
            this.in = underlyingStream;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object object;
            if (!"close".equals(method.getName())) return method.invoke((Object)this.iin, args);
            try {
                object = method.invoke((Object)this.iin, args);
            }
            catch (Throwable throwable) {
                try {
                    IOUtils.closeQuietly((InputStream)this.in);
                    this.in = null;
                    throw throwable;
                }
                catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
            IOUtils.closeQuietly((InputStream)this.in);
            this.in = null;
            return object;
        }
    }
}

