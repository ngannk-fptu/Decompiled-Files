/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.ResponseUtil;
import org.apache.tomcat.util.http.parser.AcceptEncoding;
import org.apache.tomcat.util.http.parser.TokenList;
import org.apache.tomcat.util.res.StringManager;

public class CompressionConfig {
    private static final Log log = LogFactory.getLog(CompressionConfig.class);
    private static final StringManager sm = StringManager.getManager(CompressionConfig.class);
    private int compressionLevel = 0;
    private Pattern noCompressionUserAgents = null;
    private String compressibleMimeType = "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml";
    private String[] compressibleMimeTypes = null;
    private int compressionMinSize = 2048;
    private boolean noCompressionStrongETag = true;

    public void setCompression(String compression) {
        if (compression.equals("on")) {
            this.compressionLevel = 1;
        } else if (compression.equals("force")) {
            this.compressionLevel = 2;
        } else if (compression.equals("off")) {
            this.compressionLevel = 0;
        } else {
            try {
                this.setCompressionMinSize(Integer.parseInt(compression));
                this.compressionLevel = 1;
            }
            catch (Exception e) {
                this.compressionLevel = 0;
            }
        }
    }

    public String getCompression() {
        switch (this.compressionLevel) {
            case 0: {
                return "off";
            }
            case 1: {
                return "on";
            }
            case 2: {
                return "force";
            }
        }
        return "off";
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public String getNoCompressionUserAgents() {
        if (this.noCompressionUserAgents == null) {
            return null;
        }
        return this.noCompressionUserAgents.toString();
    }

    public Pattern getNoCompressionUserAgentsPattern() {
        return this.noCompressionUserAgents;
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        this.noCompressionUserAgents = noCompressionUserAgents == null || noCompressionUserAgents.length() == 0 ? null : Pattern.compile(noCompressionUserAgents);
    }

    public String getCompressibleMimeType() {
        return this.compressibleMimeType;
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressibleMimeType = valueS;
        this.compressibleMimeTypes = null;
    }

    public String[] getCompressibleMimeTypes() {
        String[] result = this.compressibleMimeTypes;
        if (result != null) {
            return result;
        }
        ArrayList<String> values = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(this.compressibleMimeType, ",");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.length() <= 0) continue;
            values.add(token);
        }
        result = values.toArray(new String[0]);
        this.compressibleMimeTypes = result;
        return result;
    }

    public int getCompressionMinSize() {
        return this.compressionMinSize;
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionMinSize = compressionMinSize;
    }

    @Deprecated
    public boolean getNoCompressionStrongETag() {
        return this.noCompressionStrongETag;
    }

    @Deprecated
    public void setNoCompressionStrongETag(boolean noCompressionStrongETag) {
        this.noCompressionStrongETag = noCompressionStrongETag;
    }

    public boolean useCompression(Request request, Response response) {
        String userAgentValue;
        MessageBytes userAgentValueMB;
        Pattern noCompressionUserAgents;
        String eTag;
        if (this.compressionLevel == 0) {
            return false;
        }
        MimeHeaders responseHeaders = response.getMimeHeaders();
        MessageBytes contentEncodingMB = responseHeaders.getValue("Content-Encoding");
        if (contentEncodingMB != null) {
            HashSet<String> tokens = new HashSet<String>();
            try {
                TokenList.parseTokenList(responseHeaders.values("Content-Encoding"), tokens);
            }
            catch (IOException e) {
                log.warn((Object)sm.getString("compressionConfig.ContentEncodingParseFail"), (Throwable)e);
                return false;
            }
            if (tokens.contains("gzip") || tokens.contains("br")) {
                return false;
            }
        }
        if (this.compressionLevel != 2) {
            long contentLength = response.getContentLengthLong();
            if (contentLength != -1L && contentLength < (long)this.compressionMinSize) {
                return false;
            }
            String[] compressibleMimeTypes = this.getCompressibleMimeTypes();
            if (compressibleMimeTypes != null && !CompressionConfig.startsWithStringArray(compressibleMimeTypes, response.getContentType())) {
                return false;
            }
        }
        if (this.noCompressionStrongETag && (eTag = responseHeaders.getHeader("ETag")) != null && !eTag.trim().startsWith("W/")) {
            return false;
        }
        ResponseUtil.addVaryFieldName(responseHeaders, "accept-encoding");
        Enumeration<String> headerValues = request.getMimeHeaders().values("accept-encoding");
        boolean foundGzip = false;
        block4: while (!foundGzip && headerValues.hasMoreElements()) {
            List<AcceptEncoding> acceptEncodings = null;
            try {
                acceptEncodings = AcceptEncoding.parse(new StringReader(headerValues.nextElement()));
            }
            catch (IOException ioe) {
                return false;
            }
            for (AcceptEncoding acceptEncoding : acceptEncodings) {
                if (!"gzip".equalsIgnoreCase(acceptEncoding.getEncoding())) continue;
                foundGzip = true;
                continue block4;
            }
        }
        if (!foundGzip) {
            return false;
        }
        if (this.compressionLevel != 2 && (noCompressionUserAgents = this.noCompressionUserAgents) != null && (userAgentValueMB = request.getMimeHeaders().getValue("user-agent")) != null && noCompressionUserAgents.matcher(userAgentValue = userAgentValueMB.toString()).matches()) {
            return false;
        }
        response.setContentLength(-1L);
        responseHeaders.setValue("Content-Encoding").setString("gzip");
        return true;
    }

    private static boolean startsWithStringArray(String[] sArray, String value) {
        if (value == null) {
            return false;
        }
        for (String s : sArray) {
            if (!value.startsWith(s)) continue;
            return true;
        }
        return false;
    }
}

