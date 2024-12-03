/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.util.uri;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.util.io.Base64DecodeStream;

public class DataURIResolver
implements URIResolver {
    private static final Log LOG = LogFactory.getLog(URIResolver.class);

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        if (href.startsWith("data:")) {
            return this.parseDataURI(href);
        }
        return null;
    }

    private Source parseDataURI(String href) {
        int commaPos = href.indexOf(44);
        String header = href.substring(0, commaPos);
        String data = href.substring(commaPos + 1);
        if (header.endsWith(";base64")) {
            byte[] bytes = new byte[]{};
            try {
                bytes = data.getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ByteArrayInputStream encodedStream = new ByteArrayInputStream(bytes);
            Base64DecodeStream decodedStream = new Base64DecodeStream(encodedStream);
            return new StreamSource(decodedStream, href);
        }
        String encoding = "UTF-8";
        int charsetpos = header.indexOf(";charset=");
        if (charsetpos > 0) {
            encoding = header.substring(charsetpos + 9);
        }
        try {
            String unescapedString = URLDecoder.decode(data, encoding);
            return new StreamSource(new StringReader(unescapedString), href);
        }
        catch (IllegalArgumentException e) {
            LOG.warn((Object)e.getMessage());
        }
        catch (UnsupportedEncodingException e) {
            LOG.warn((Object)e.getMessage());
        }
        return null;
    }
}

