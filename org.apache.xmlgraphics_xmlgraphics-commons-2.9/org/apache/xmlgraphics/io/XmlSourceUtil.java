/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.xmlgraphics.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.xmlgraphics.image.loader.ImageSource;
import org.apache.xmlgraphics.image.loader.util.ImageInputStreamAdapter;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.xml.sax.InputSource;

public final class XmlSourceUtil {
    private XmlSourceUtil() {
    }

    public static InputStream getInputStream(Source src) {
        try {
            if (src instanceof StreamSource) {
                return ((StreamSource)src).getInputStream();
            }
            if (src instanceof DOMSource) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                StreamResult xmlSource = new StreamResult(outStream);
                TransformerFactory.newInstance().newTransformer().transform(src, xmlSource);
                return new ByteArrayInputStream(outStream.toByteArray());
            }
            if (src instanceof SAXSource) {
                return ((SAXSource)src).getInputSource().getByteStream();
            }
            if (src instanceof ImageSource) {
                return new ImageInputStreamAdapter(((ImageSource)src).getImageInputStream());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static InputStream needInputStream(Source src) {
        InputStream in = XmlSourceUtil.getInputStream(src);
        if (in != null) {
            return in;
        }
        throw new IllegalArgumentException("Source must be a StreamSource with an InputStream or an ImageSource");
    }

    public static boolean hasReader(Source src) {
        InputSource is;
        if (src instanceof StreamSource) {
            Reader reader = ((StreamSource)src).getReader();
            return reader != null;
        }
        if (src instanceof SAXSource && (is = ((SAXSource)src).getInputSource()) != null) {
            return is.getCharacterStream() != null;
        }
        return false;
    }

    public static void removeStreams(Source src) {
        InputSource is;
        if (src instanceof ImageSource) {
            ImageSource isrc = (ImageSource)src;
            isrc.setImageInputStream(null);
        } else if (src instanceof StreamSource) {
            StreamSource ssrc = (StreamSource)src;
            ssrc.setInputStream(null);
            ssrc.setReader(null);
        } else if (src instanceof SAXSource && (is = ((SAXSource)src).getInputSource()) != null) {
            is.setByteStream(null);
            is.setCharacterStream(null);
        }
    }

    public static void closeQuietly(Source src) {
        InputSource is;
        if (src instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)src;
            IOUtils.closeQuietly((Reader)streamSource.getReader());
        } else if (src instanceof ImageSource) {
            if (ImageUtil.getImageInputStream(src) != null) {
                try {
                    ImageUtil.getImageInputStream(src).close();
                }
                catch (IOException streamSource) {}
            }
        } else if (src instanceof SAXSource && (is = ((SAXSource)src).getInputSource()) != null) {
            IOUtils.closeQuietly((InputStream)is.getByteStream());
            IOUtils.closeQuietly((Reader)is.getCharacterStream());
        }
        XmlSourceUtil.removeStreams(src);
    }

    public static boolean hasInputStream(Source src) {
        return XmlSourceUtil.getInputStream(src) != null;
    }
}

