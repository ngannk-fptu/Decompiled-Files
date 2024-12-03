/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.ByteArrayOutputStream
 */
package org.apache.xmlgraphics.xmp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPParser;

public final class XMPPacketParser {
    private static final byte[] PACKET_HEADER;
    private static final byte[] PACKET_HEADER_END;
    private static final byte[] PACKET_TRAILER;

    private XMPPacketParser() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Metadata parse(InputStream in) throws IOException, TransformerException {
        Metadata metadata;
        boolean foundXMP;
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        if (!(foundXMP = XMPPacketParser.skipAfter(in, PACKET_HEADER))) {
            return null;
        }
        if (!XMPPacketParser.skipAfter(in, PACKET_HEADER_END)) {
            throw new IOException("Invalid XMP packet header!");
        }
        ByteArrayOutputStream baout = null;
        try {
            baout = new ByteArrayOutputStream();
            if (!XMPPacketParser.skipAfter(in, PACKET_TRAILER, (OutputStream)baout)) {
                throw new IOException("XMP packet not properly terminated!");
            }
            metadata = XMPParser.parseXMP(new StreamSource(new ByteArrayInputStream(baout.toByteArray())));
        }
        finally {
            IOUtils.closeQuietly((OutputStream)baout);
        }
        return metadata;
    }

    private static boolean skipAfter(InputStream in, byte[] match) throws IOException {
        return XMPPacketParser.skipAfter(in, match, null);
    }

    private static boolean skipAfter(InputStream in, byte[] match, OutputStream out) throws IOException {
        int b;
        int found = 0;
        int len = match.length;
        while ((b = in.read()) >= 0) {
            if (b == match[found]) {
                if (++found != len) continue;
                return true;
            }
            if (out != null) {
                if (found > 0) {
                    out.write(match, 0, found);
                }
                out.write(b);
            }
            found = 0;
        }
        return false;
    }

    static {
        try {
            PACKET_HEADER = "<?xpacket begin=".getBytes("US-ASCII");
            PACKET_HEADER_END = "?>".getBytes("US-ASCII");
            PACKET_TRAILER = "<?xpacket".getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Incompatible JVM! US-ASCII encoding not supported.");
        }
    }
}

