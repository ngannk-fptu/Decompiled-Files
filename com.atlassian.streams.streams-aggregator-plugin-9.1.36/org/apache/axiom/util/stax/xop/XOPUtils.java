/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamReader;
import org.apache.axiom.util.stax.xop.XOPEncodedStream;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamReader;

public class XOPUtils {
    private static final MimePartProvider nullMimePartProvider = new MimePartProvider(){

        public boolean isLoaded(String contentID) {
            throw new IllegalArgumentException("There are no MIME parts!");
        }

        public DataHandler getDataHandler(String contentID) throws IOException {
            throw new IllegalArgumentException("There are no MIME parts!");
        }
    };

    private XOPUtils() {
    }

    public static String getContentIDFromURL(String url) {
        if (url.startsWith("cid:")) {
            try {
                return URLDecoder.decode(url.substring(4), "ascii");
            }
            catch (UnsupportedEncodingException ex) {
                throw new Error(ex);
            }
        }
        throw new IllegalArgumentException("The URL doesn't use the cid scheme");
    }

    public static String getURLForContentID(String contentID) {
        return "cid:" + contentID.replaceAll("%", "%25");
    }

    public static XOPEncodedStream getXOPEncodedStream(XMLStreamReader reader) {
        if (reader instanceof XOPEncodingStreamReader) {
            return new XOPEncodedStream(reader, (MimePartProvider)((Object)reader));
        }
        if (reader instanceof XOPDecodingStreamReader) {
            return ((XOPDecodingStreamReader)reader).getXOPEncodedStream();
        }
        if (XMLStreamReaderUtils.getDataHandlerReader(reader) != null) {
            XOPEncodingStreamReader wrapper = new XOPEncodingStreamReader(reader, ContentIDGenerator.DEFAULT, OptimizationPolicy.ALL);
            return new XOPEncodedStream(wrapper, wrapper);
        }
        return new XOPEncodedStream(reader, nullMimePartProvider);
    }
}

