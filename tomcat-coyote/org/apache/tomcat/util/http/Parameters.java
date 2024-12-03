/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

public final class Parameters {
    private static final Log log = LogFactory.getLog(Parameters.class);
    private static final UserDataHelper userDataLog = new UserDataHelper(log);
    private static final UserDataHelper maxParamCountLog = new UserDataHelper(log);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.tomcat.util.http");
    private final Map<String, ArrayList<String>> paramHashValues = new LinkedHashMap<String, ArrayList<String>>();
    private boolean didQueryParameters = false;
    private MessageBytes queryMB;
    private UDecoder urlDec;
    private final MessageBytes decodedQuery = MessageBytes.newInstance();
    private Charset charset = StandardCharsets.ISO_8859_1;
    private Charset queryStringCharset = StandardCharsets.UTF_8;
    private int limit = -1;
    private int parameterCount = 0;
    private FailReason parseFailedReason = null;
    private final ByteChunk tmpName = new ByteChunk();
    private final ByteChunk tmpValue = new ByteChunk();
    private final ByteChunk origName = new ByteChunk();
    private final ByteChunk origValue = new ByteChunk();
    private static final Charset DEFAULT_BODY_CHARSET = StandardCharsets.ISO_8859_1;
    private static final Charset DEFAULT_URI_CHARSET = StandardCharsets.UTF_8;

    public void setQuery(MessageBytes queryMB) {
        this.queryMB = queryMB;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        if (charset == null) {
            charset = DEFAULT_BODY_CHARSET;
        }
        this.charset = charset;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Set encoding to " + charset.name()));
        }
    }

    public void setQueryStringCharset(Charset queryStringCharset) {
        if (queryStringCharset == null) {
            queryStringCharset = DEFAULT_URI_CHARSET;
        }
        this.queryStringCharset = queryStringCharset;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Set query string encoding to " + queryStringCharset.name()));
        }
    }

    public boolean isParseFailed() {
        return this.parseFailedReason != null;
    }

    public FailReason getParseFailedReason() {
        return this.parseFailedReason;
    }

    public void setParseFailedReason(FailReason failReason) {
        if (this.parseFailedReason == null) {
            this.parseFailedReason = failReason;
        }
    }

    public int size() {
        return this.parameterCount;
    }

    public void recycle() {
        this.parameterCount = 0;
        this.paramHashValues.clear();
        this.didQueryParameters = false;
        this.charset = DEFAULT_BODY_CHARSET;
        this.decodedQuery.recycle();
        this.parseFailedReason = null;
    }

    public String[] getParameterValues(String name) {
        this.handleQueryParameters();
        ArrayList<String> values = this.paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        return values.toArray(new String[0]);
    }

    public Enumeration<String> getParameterNames() {
        this.handleQueryParameters();
        return Collections.enumeration(this.paramHashValues.keySet());
    }

    public String getParameter(String name) {
        this.handleQueryParameters();
        ArrayList<String> values = this.paramHashValues.get(name);
        if (values != null) {
            if (values.size() == 0) {
                return "";
            }
            return values.get(0);
        }
        return null;
    }

    public void handleQueryParameters() {
        if (this.didQueryParameters) {
            return;
        }
        this.didQueryParameters = true;
        if (this.queryMB == null || this.queryMB.isNull()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Decoding query " + this.decodedQuery + " " + this.queryStringCharset.name()));
        }
        try {
            this.decodedQuery.duplicate(this.queryMB);
        }
        catch (IOException e) {
            log.error((Object)sm.getString("parameters.copyFail"), (Throwable)e);
        }
        this.processParameters(this.decodedQuery, this.queryStringCharset);
    }

    public void addParameter(String key, String value) throws IllegalStateException {
        if (key == null) {
            return;
        }
        if (this.limit > -1 && this.parameterCount >= this.limit) {
            this.setParseFailedReason(FailReason.TOO_MANY_PARAMETERS);
            throw new IllegalStateException(sm.getString("parameters.maxCountFail", new Object[]{this.limit}));
        }
        ++this.parameterCount;
        this.paramHashValues.computeIfAbsent(key, k -> new ArrayList(1)).add(value);
    }

    public void setURLDecoder(UDecoder u) {
        this.urlDec = u;
    }

    public void processParameters(byte[] bytes, int start, int len) {
        this.processParameters(bytes, start, len, this.charset);
    }

    private void processParameters(byte[] bytes, int start, int len, Charset charset) {
        UserDataHelper.Mode logMode;
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("parameters.bytes", new Object[]{new String(bytes, start, len, DEFAULT_BODY_CHARSET)}));
        }
        int decodeFailCount = 0;
        int pos = start;
        int end = start + len;
        while (pos < end) {
            block58: {
                String message;
                int nameStart = pos;
                int nameEnd = -1;
                int valueStart = -1;
                int valueEnd = -1;
                boolean parsingName = true;
                boolean decodeName = false;
                boolean decodeValue = false;
                boolean parameterComplete = false;
                do {
                    switch (bytes[pos]) {
                        case 61: {
                            if (parsingName) {
                                nameEnd = pos++;
                                parsingName = false;
                                valueStart = pos;
                                break;
                            }
                            ++pos;
                            break;
                        }
                        case 38: {
                            if (parsingName) {
                                nameEnd = pos;
                            } else {
                                valueEnd = pos;
                            }
                            parameterComplete = true;
                            ++pos;
                            break;
                        }
                        case 37: 
                        case 43: {
                            if (parsingName) {
                                decodeName = true;
                            } else {
                                decodeValue = true;
                            }
                            ++pos;
                            break;
                        }
                        default: {
                            ++pos;
                        }
                    }
                } while (!parameterComplete && pos < end);
                if (pos == end) {
                    if (nameEnd == -1) {
                        nameEnd = pos;
                    } else if (valueStart > -1 && valueEnd == -1) {
                        valueEnd = pos;
                    }
                }
                if (log.isDebugEnabled() && valueStart == -1) {
                    log.debug((Object)sm.getString("parameters.noequal", new Object[]{nameStart, nameEnd, new String(bytes, nameStart, nameEnd - nameStart, DEFAULT_BODY_CHARSET)}));
                }
                if (nameEnd <= nameStart) {
                    if (valueStart == -1) {
                        if (!log.isDebugEnabled()) continue;
                        log.debug((Object)sm.getString("parameters.emptyChunk"));
                        continue;
                    }
                    UserDataHelper.Mode logMode2 = userDataLog.getNextMode();
                    if (logMode2 != null) {
                        String extract = valueEnd > nameStart ? new String(bytes, nameStart, valueEnd - nameStart, DEFAULT_BODY_CHARSET) : "";
                        message = sm.getString("parameters.invalidChunk", new Object[]{nameStart, valueEnd, extract});
                        switch (logMode2) {
                            case INFO_THEN_DEBUG: {
                                message = message + sm.getString("parameters.fallToDebug");
                            }
                            case INFO: {
                                log.info((Object)message);
                                break;
                            }
                            case DEBUG: {
                                log.debug((Object)message);
                            }
                        }
                    }
                    this.setParseFailedReason(FailReason.NO_NAME);
                    continue;
                }
                this.tmpName.setBytes(bytes, nameStart, nameEnd - nameStart);
                if (valueStart >= 0) {
                    this.tmpValue.setBytes(bytes, valueStart, valueEnd - valueStart);
                } else {
                    this.tmpValue.setBytes(bytes, 0, 0);
                }
                if (log.isDebugEnabled()) {
                    try {
                        this.origName.append(bytes, nameStart, nameEnd - nameStart);
                        if (valueStart >= 0) {
                            this.origValue.append(bytes, valueStart, valueEnd - valueStart);
                        } else {
                            this.origValue.append(bytes, 0, 0);
                        }
                    }
                    catch (IOException ioe) {
                        log.error((Object)sm.getString("parameters.copyFail"), (Throwable)ioe);
                    }
                }
                try {
                    String value;
                    if (decodeName) {
                        this.urlDecode(this.tmpName);
                    }
                    this.tmpName.setCharset(charset);
                    String name = this.tmpName.toString();
                    if (valueStart >= 0) {
                        if (decodeValue) {
                            this.urlDecode(this.tmpValue);
                        }
                        this.tmpValue.setCharset(charset);
                        value = this.tmpValue.toString();
                    } else {
                        value = "";
                    }
                    try {
                        this.addParameter(name, value);
                    }
                    catch (IllegalStateException ise) {
                        UserDataHelper.Mode logMode3 = maxParamCountLog.getNextMode();
                        if (logMode3 == null) break;
                        String message2 = ise.getMessage();
                        switch (logMode3) {
                            case INFO_THEN_DEBUG: {
                                message2 = message2 + sm.getString("parameters.maxCountFail.fallToDebug");
                            }
                            case INFO: {
                                log.info((Object)message2);
                                break;
                            }
                            case DEBUG: {
                                log.debug((Object)message2);
                            }
                        }
                        break;
                    }
                }
                catch (IOException e) {
                    UserDataHelper.Mode logMode4;
                    this.setParseFailedReason(FailReason.URL_DECODING);
                    if (++decodeFailCount != 1 && !log.isDebugEnabled()) break block58;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("parameters.decodeFail.debug", new Object[]{this.origName.toString(), this.origValue.toString()}), (Throwable)e);
                    }
                    if (!log.isInfoEnabled() || (logMode4 = userDataLog.getNextMode()) == null) break block58;
                    message = sm.getString("parameters.decodeFail.info", new Object[]{this.tmpName.toString(), this.tmpValue.toString()});
                    switch (logMode4) {
                        case INFO_THEN_DEBUG: {
                            message = message + sm.getString("parameters.fallToDebug");
                        }
                        case INFO: {
                            log.info((Object)message);
                            break;
                        }
                        case DEBUG: {
                            log.debug((Object)message);
                        }
                    }
                }
            }
            this.tmpName.recycle();
            this.tmpValue.recycle();
            if (!log.isDebugEnabled()) continue;
            this.origName.recycle();
            this.origValue.recycle();
        }
        if (decodeFailCount > 1 && !log.isDebugEnabled() && (logMode = userDataLog.getNextMode()) != null) {
            String message = sm.getString("parameters.multipleDecodingFail", new Object[]{decodeFailCount});
            switch (logMode) {
                case INFO_THEN_DEBUG: {
                    message = message + sm.getString("parameters.fallToDebug");
                }
                case INFO: {
                    log.info((Object)message);
                    break;
                }
                case DEBUG: {
                    log.debug((Object)message);
                }
            }
        }
    }

    private void urlDecode(ByteChunk bc) throws IOException {
        if (this.urlDec == null) {
            this.urlDec = new UDecoder();
        }
        this.urlDec.convert(bc, true);
    }

    public void processParameters(MessageBytes data, Charset charset) {
        if (data == null || data.isNull() || data.getLength() <= 0) {
            return;
        }
        if (data.getType() != 2) {
            data.toBytes();
        }
        ByteChunk bc = data.getByteChunk();
        this.processParameters(bc.getBytes(), bc.getOffset(), bc.getLength(), charset);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ArrayList<String>> e : this.paramHashValues.entrySet()) {
            sb.append(e.getKey()).append('=');
            StringUtils.join((Iterable)e.getValue(), (char)',', (StringBuilder)sb);
            sb.append('\n');
        }
        return sb.toString();
    }

    public static enum FailReason {
        CLIENT_DISCONNECT,
        MULTIPART_CONFIG_INVALID,
        INVALID_CONTENT_TYPE,
        IO_ERROR,
        NO_NAME,
        POST_TOO_LARGE,
        REQUEST_BODY_INCOMPLETE,
        TOO_MANY_PARAMETERS,
        UNKNOWN,
        URL_DECODING;

    }
}

