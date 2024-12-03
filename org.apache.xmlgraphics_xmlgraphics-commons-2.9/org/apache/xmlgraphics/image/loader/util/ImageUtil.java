/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.ImageProcessingHints;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.ImageSource;
import org.apache.xmlgraphics.io.XmlSourceUtil;

public final class ImageUtil {
    private static final byte[] GZIP_MAGIC = new byte[]{31, -117};
    private static final String PAGE_INDICATOR = "page=";

    private ImageUtil() {
    }

    @Deprecated
    public static InputStream getInputStream(Source src) {
        return XmlSourceUtil.getInputStream(src);
    }

    public static ImageInputStream getImageInputStream(Source src) {
        if (src instanceof ImageSource) {
            return ((ImageSource)src).getImageInputStream();
        }
        return null;
    }

    @Deprecated
    public static InputStream needInputStream(Source src) {
        return XmlSourceUtil.needInputStream(src);
    }

    public static ImageInputStream needImageInputStream(Source src) {
        if (src instanceof ImageSource) {
            ImageSource isrc = (ImageSource)src;
            if (isrc.getImageInputStream() == null) {
                throw new IllegalArgumentException("ImageInputStream is null/cleared on ImageSource");
            }
            return isrc.getImageInputStream();
        }
        throw new IllegalArgumentException("Source must be an ImageSource");
    }

    public static boolean hasInputStream(Source src) {
        return XmlSourceUtil.hasInputStream(src) || ImageUtil.hasImageInputStream(src);
    }

    @Deprecated
    public static boolean hasReader(Source src) {
        return XmlSourceUtil.hasReader(src);
    }

    public static boolean hasImageInputStream(Source src) {
        return ImageUtil.getImageInputStream(src) != null;
    }

    @Deprecated
    public static void removeStreams(Source src) {
        XmlSourceUtil.removeStreams(src);
    }

    @Deprecated
    public static void closeQuietly(Source src) {
        XmlSourceUtil.closeQuietly(src);
    }

    public static ImageInputStream ignoreFlushing(final ImageInputStream in) {
        return (ImageInputStream)Proxy.newProxyInstance(in.getClass().getClassLoader(), new Class[]{ImageInputStream.class}, new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (!methodName.startsWith("flush")) {
                    try {
                        return method.invoke((Object)in, args);
                    }
                    catch (InvocationTargetException ite) {
                        throw ite.getCause();
                    }
                }
                return null;
            }
        });
    }

    public static boolean isGZIPCompressed(InputStream in) throws IOException {
        if (!in.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark()!");
        }
        byte[] data = new byte[2];
        in.mark(2);
        in.read(data);
        in.reset();
        return data[0] == GZIP_MAGIC[0] && data[1] == GZIP_MAGIC[1];
    }

    public static InputStream decorateMarkSupported(InputStream in) {
        if (in.markSupported()) {
            return in;
        }
        return new BufferedInputStream(in);
    }

    public static InputStream autoDecorateInputStream(InputStream in) throws IOException {
        if (ImageUtil.isGZIPCompressed(in = ImageUtil.decorateMarkSupported(in))) {
            return new GZIPInputStream(in);
        }
        return in;
    }

    public static Map getDefaultHints(ImageSessionContext session) {
        HashMap<Object, Object> hints = new HashMap<Object, Object>();
        hints.put(ImageProcessingHints.SOURCE_RESOLUTION, Float.valueOf(session.getParentContext().getSourceResolution()));
        hints.put(ImageProcessingHints.TARGET_RESOLUTION, Float.valueOf(session.getTargetResolution()));
        hints.put(ImageProcessingHints.IMAGE_SESSION_CONTEXT, session);
        return hints;
    }

    public static Integer getPageIndexFromURI(String uri) {
        if (uri.indexOf(35) < 0) {
            return null;
        }
        try {
            int pos;
            URI u = new URI(uri);
            String fragment = u.getFragment();
            if (fragment != null && (pos = fragment.indexOf(PAGE_INDICATOR)) >= 0) {
                char c;
                pos += PAGE_INDICATOR.length();
                StringBuffer sb = new StringBuffer();
                while (pos < fragment.length() && (c = fragment.charAt(pos)) >= '0' && c <= '9') {
                    sb.append(c);
                    ++pos;
                }
                if (sb.length() > 0) {
                    int pageIndex = Integer.parseInt(sb.toString()) - 1;
                    pageIndex = Math.max(0, pageIndex);
                    return pageIndex;
                }
            }
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI is invalid: " + e.getLocalizedMessage());
        }
        return null;
    }

    public static int needPageIndexFromURI(String uri) {
        Integer res = ImageUtil.getPageIndexFromURI(uri);
        if (res != null) {
            return res;
        }
        return 0;
    }
}

