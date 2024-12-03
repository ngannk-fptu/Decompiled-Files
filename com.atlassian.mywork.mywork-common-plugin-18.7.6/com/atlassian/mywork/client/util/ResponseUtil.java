/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Charsets
 *  com.google.common.io.ByteStreams
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.input.BoundedInputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.HeaderElement
 *  org.apache.http.NameValuePair
 *  org.apache.http.message.BasicHeaderValueParser
 */
package com.atlassian.mywork.client.util;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;

public class ResponseUtil {
    private static final Charset HTTP_DEFAULT_CHARSET = Charsets.ISO_8859_1;

    public static String readResponseBodyAsString(Response response, int maxLength) throws ResponseException {
        InputStream stream = response.getResponseBodyAsStream();
        if (stream == null) {
            return null;
        }
        BoundedInputStream limitedStream = new BoundedInputStream(stream, (long)(maxLength + 1));
        try {
            Charset charset = ResponseUtil.getResponseCharset(response);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            long read = ByteStreams.copy((InputStream)limitedStream, (OutputStream)baos);
            if (read > (long)maxLength) {
                throw new ResponseException("Response body exceeded maximum length");
            }
            String string = new String(baos.toByteArray(), charset);
            return string;
        }
        catch (IOException e) {
            throw new ResponseException(e.getMessage(), (Throwable)e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)stream);
            IOUtils.closeQuietly((InputStream)limitedStream);
        }
    }

    public static Charset getResponseCharset(Response response) {
        String contentType = response.getHeader("Content-Type");
        if (StringUtils.isBlank((CharSequence)contentType)) {
            return HTTP_DEFAULT_CHARSET;
        }
        HeaderElement element = BasicHeaderValueParser.parseHeaderElement((String)contentType, null);
        if (element == null) {
            return HTTP_DEFAULT_CHARSET;
        }
        NameValuePair charsetNV = element.getParameterByName("charset");
        if (charsetNV == null) {
            return HTTP_DEFAULT_CHARSET;
        }
        try {
            return Charset.forName(charsetNV.getValue());
        }
        catch (IllegalCharsetNameException e) {
            return HTTP_DEFAULT_CHARSET;
        }
        catch (UnsupportedCharsetException e) {
            return HTTP_DEFAULT_CHARSET;
        }
    }
}

