/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.Wire;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpParser {
    private static final Log LOG = LogFactory.getLog(HttpParser.class);

    private HttpParser() {
    }

    public static byte[] readRawLine(InputStream inputStream) throws IOException {
        int ch;
        LOG.trace((Object)"enter HttpParser.readRawLine()");
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        while ((ch = inputStream.read()) >= 0) {
            buf.write(ch);
            if (ch != 10) continue;
        }
        if (buf.size() == 0) {
            return null;
        }
        return buf.toByteArray();
    }

    public static String readLine(InputStream inputStream, String charset) throws IOException {
        LOG.trace((Object)"enter HttpParser.readLine(InputStream, String)");
        byte[] rawdata = HttpParser.readRawLine(inputStream);
        if (rawdata == null) {
            return null;
        }
        int len = rawdata.length;
        int offset = 0;
        if (len > 0 && rawdata[len - 1] == 10) {
            ++offset;
            if (len > 1 && rawdata[len - 2] == 13) {
                ++offset;
            }
        }
        String result = EncodingUtil.getString(rawdata, 0, len - offset, charset);
        if (Wire.HEADER_WIRE.enabled()) {
            String logoutput = result;
            if (offset == 2) {
                logoutput = result + "\r\n";
            } else if (offset == 1) {
                logoutput = result + "\n";
            }
            Wire.HEADER_WIRE.input(logoutput);
        }
        return result;
    }

    public static String readLine(InputStream inputStream) throws IOException {
        LOG.trace((Object)"enter HttpParser.readLine(InputStream)");
        return HttpParser.readLine(inputStream, "US-ASCII");
    }

    public static Header[] parseHeaders(InputStream is, String charset) throws IOException, HttpException {
        String line;
        LOG.trace((Object)"enter HeaderParser.parseHeaders(InputStream, String)");
        ArrayList<Header> headers = new ArrayList<Header>();
        String name = null;
        StringBuffer value = null;
        while ((line = HttpParser.readLine(is, charset)) != null && line.trim().length() >= 1) {
            int colon;
            if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
                if (value == null) continue;
                value.append(' ');
                value.append(line.trim());
                continue;
            }
            if (name != null) {
                headers.add(new Header(name, value.toString()));
            }
            if ((colon = line.indexOf(":")) < 0) {
                throw new ProtocolException("Unable to parse header: " + line);
            }
            name = line.substring(0, colon).trim();
            value = new StringBuffer(line.substring(colon + 1).trim());
        }
        if (name != null) {
            headers.add(new Header(name, value.toString()));
        }
        return headers.toArray(new Header[headers.size()]);
    }

    public static Header[] parseHeaders(InputStream is) throws IOException, HttpException {
        LOG.trace((Object)"enter HeaderParser.parseHeaders(InputStream, String)");
        return HttpParser.parseHeaders(is, "US-ASCII");
    }
}

