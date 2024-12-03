/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.http.parser.Authorization
 *  org.apache.tomcat.util.security.ConcurrentMessageDigest
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.parser.Authorization;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;

public class DigestAuthenticator
extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(DigestAuthenticator.class);
    protected static final String QOP = "auth";
    private static final AuthDigest FALLBACK_DIGEST = AuthDigest.MD5;
    private static final String NONCE_DIGEST = "SHA-256";
    private static final Map<String, AuthDigest> PERMITTED_ALGORITHMS = new HashMap<String, AuthDigest>();
    protected Map<String, NonceInfo> nonces;
    protected long lastTimestamp = 0L;
    protected final Object lastTimestampLock = new Object();
    protected int nonceCacheSize = 1000;
    protected int nonceCountWindowSize = 100;
    protected String key = null;
    protected long nonceValidity = 300000L;
    protected String opaque;
    protected boolean validateUri = true;
    private List<AuthDigest> algorithms = Arrays.asList(AuthDigest.SHA_256, AuthDigest.MD5);

    public DigestAuthenticator() {
        this.setCache(false);
    }

    public int getNonceCountWindowSize() {
        return this.nonceCountWindowSize;
    }

    public void setNonceCountWindowSize(int nonceCountWindowSize) {
        this.nonceCountWindowSize = nonceCountWindowSize;
    }

    public int getNonceCacheSize() {
        return this.nonceCacheSize;
    }

    public void setNonceCacheSize(int nonceCacheSize) {
        this.nonceCacheSize = nonceCacheSize;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getNonceValidity() {
        return this.nonceValidity;
    }

    public void setNonceValidity(long nonceValidity) {
        this.nonceValidity = nonceValidity;
    }

    public String getOpaque() {
        return this.opaque;
    }

    public void setOpaque(String opaque) {
        this.opaque = opaque;
    }

    public boolean isValidateUri() {
        return this.validateUri;
    }

    public void setValidateUri(boolean validateUri) {
        this.validateUri = validateUri;
    }

    public String getAlgorithms() {
        StringBuilder result = new StringBuilder();
        StringUtils.join(this.algorithms, (char)',', x -> x.getRfcName(), (StringBuilder)result);
        return result.toString();
    }

    public void setAlgorithms(String algorithmsString) {
        String[] algorithmsArray = algorithmsString.split(",");
        ArrayList<AuthDigest> algorithms = new ArrayList<AuthDigest>();
        for (String algorithm : algorithmsArray) {
            AuthDigest authDigest = PERMITTED_ALGORITHMS.get(algorithm);
            if (authDigest == null) {
                this.log.warn((Object)sm.getString("digestAuthenticator.invalidAlgorithm", new Object[]{algorithmsString, algorithm}));
                return;
            }
            algorithms.add(authDigest);
        }
        this.initAlgorithms(algorithms);
        this.algorithms = algorithms;
    }

    private void initAlgorithms(List<AuthDigest> algorithms) {
        Iterator<AuthDigest> algorithmIterator = algorithms.iterator();
        while (algorithmIterator.hasNext()) {
            AuthDigest algorithm = algorithmIterator.next();
            try {
                ConcurrentMessageDigest.init((String)algorithm.getJavaName());
            }
            catch (NoSuchAlgorithmException e) {
                this.log.warn((Object)sm.getString("digestAuthenticator.unsupportedAlgorithm", new Object[]{algorithms, algorithm.getJavaName()}), (Throwable)e);
                algorithmIterator.remove();
            }
        }
    }

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (this.checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        Principal principal = null;
        String authorization = request.getHeader("authorization");
        DigestInfo digestInfo = new DigestInfo(this.getOpaque(), this.getNonceValidity(), this.getKey(), this.nonces, this.isValidateUri());
        if (authorization != null && digestInfo.parse(request, authorization)) {
            if (digestInfo.validate(request, this.algorithms)) {
                principal = digestInfo.authenticate(this.context.getRealm());
            }
            if (principal != null && !digestInfo.isNonceStale()) {
                this.register(request, response, principal, "DIGEST", digestInfo.getUsername(), null);
                return true;
            }
        }
        String nonce = this.generateNonce(request);
        this.setAuthenticateHeader(request, response, nonce, principal != null && digestInfo.isNonceStale());
        response.sendError(401);
        return false;
    }

    @Override
    protected String getAuthMethod() {
        return "DIGEST";
    }

    protected static String removeQuotes(String quotedString, boolean quotesRequired) {
        if (quotedString.length() > 0 && quotedString.charAt(0) != '\"' && !quotesRequired) {
            return quotedString;
        }
        if (quotedString.length() > 2) {
            return quotedString.substring(1, quotedString.length() - 1);
        }
        return "";
    }

    protected static String removeQuotes(String quotedString) {
        return DigestAuthenticator.removeQuotes(quotedString, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String generateNonce(Request request) {
        long currentTime = System.currentTimeMillis();
        Object object = this.lastTimestampLock;
        synchronized (object) {
            if (currentTime > this.lastTimestamp) {
                this.lastTimestamp = currentTime;
            } else {
                currentTime = ++this.lastTimestamp;
            }
        }
        String ipTimeKey = request.getRemoteAddr() + ":" + currentTime + ":" + this.getKey();
        byte[] buffer = ConcurrentMessageDigest.digest((String)NONCE_DIGEST, (byte[][])new byte[][]{ipTimeKey.getBytes(StandardCharsets.ISO_8859_1)});
        String nonce = currentTime + ":" + HexUtils.toHexString((byte[])buffer);
        NonceInfo info = new NonceInfo(currentTime, this.getNonceCountWindowSize());
        Map<String, NonceInfo> map = this.nonces;
        synchronized (map) {
            this.nonces.put(nonce, info);
        }
        return nonce;
    }

    protected void setAuthenticateHeader(HttpServletRequest request, HttpServletResponse response, String nonce, boolean isNonceStale) {
        String realmName = DigestAuthenticator.getRealmName(this.context);
        boolean first = true;
        for (AuthDigest algorithm : this.algorithms) {
            StringBuilder authenticateHeader = new StringBuilder(200);
            authenticateHeader.append("Digest realm=\"");
            authenticateHeader.append(realmName);
            authenticateHeader.append("\", qop=\"");
            authenticateHeader.append(QOP);
            authenticateHeader.append("\", nonce=\"");
            authenticateHeader.append(nonce);
            authenticateHeader.append("\", opaque=\"");
            authenticateHeader.append(this.getOpaque());
            authenticateHeader.append("\"");
            if (isNonceStale) {
                authenticateHeader.append(", stale=true");
            }
            authenticateHeader.append(", algorithm=");
            authenticateHeader.append(algorithm.getRfcName());
            if (first) {
                response.setHeader("WWW-Authenticate", authenticateHeader.toString());
                first = false;
                continue;
            }
            response.addHeader("WWW-Authenticate", authenticateHeader.toString());
        }
    }

    @Override
    protected boolean isPreemptiveAuthPossible(Request request) {
        MessageBytes authorizationHeader = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        return authorizationHeader != null && authorizationHeader.startsWithIgnoreCase("digest ", 0);
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (this.getKey() == null) {
            this.setKey(this.sessionIdGenerator.generateSessionId());
        }
        if (this.getOpaque() == null) {
            this.setOpaque(this.sessionIdGenerator.generateSessionId());
        }
        this.nonces = new LinkedHashMap<String, NonceInfo>(){
            private static final long serialVersionUID = 1L;
            private static final long LOG_SUPPRESS_TIME = 300000L;
            private long lastLog = 0L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, NonceInfo> eldest) {
                long currentTime = System.currentTimeMillis();
                if (this.size() > DigestAuthenticator.this.getNonceCacheSize()) {
                    if (this.lastLog < currentTime && currentTime - eldest.getValue().getTimestamp() < DigestAuthenticator.this.getNonceValidity()) {
                        DigestAuthenticator.this.log.warn((Object)AuthenticatorBase.sm.getString("digestAuthenticator.cacheRemove"));
                        this.lastLog = currentTime + 300000L;
                    }
                    return true;
                }
                return false;
            }
        };
        this.initAlgorithms(this.algorithms);
        try {
            ConcurrentMessageDigest.init((String)NONCE_DIGEST);
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            // empty catch block
        }
    }

    static {
        for (AuthDigest authDigest : AuthDigest.values()) {
            PERMITTED_ALGORITHMS.put(authDigest.getJavaName(), authDigest);
            PERMITTED_ALGORITHMS.put(authDigest.getRfcName(), authDigest);
        }
    }

    public static enum AuthDigest {
        MD5("MD5", "MD5"),
        SHA_256("SHA-256", "SHA-256"),
        SHA_512_256("SHA-512/256", "SHA-512-256");

        private final String javaName;
        private final String rfcName;

        private AuthDigest(String javaName, String rfcName) {
            this.javaName = javaName;
            this.rfcName = rfcName;
        }

        public String getJavaName() {
            return this.javaName;
        }

        public String getRfcName() {
            return this.rfcName;
        }
    }

    public static class DigestInfo {
        private final String opaque;
        private final long nonceValidity;
        private final String key;
        private final Map<String, NonceInfo> nonces;
        private boolean validateUri = true;
        private String userName = null;
        private String method = null;
        private String uri = null;
        private String response = null;
        private String nonce = null;
        private String nc = null;
        private String cnonce = null;
        private String realmName = null;
        private String qop = null;
        private String opaqueReceived = null;
        private boolean nonceStale = false;
        private AuthDigest algorithm = null;

        public DigestInfo(String opaque, long nonceValidity, String key, Map<String, NonceInfo> nonces, boolean validateUri) {
            this.opaque = opaque;
            this.nonceValidity = nonceValidity;
            this.key = key;
            this.nonces = nonces;
            this.validateUri = validateUri;
        }

        public String getUsername() {
            return this.userName;
        }

        public boolean parse(Request request, String authorization) {
            Map directives;
            if (authorization == null) {
                return false;
            }
            try {
                directives = Authorization.parseAuthorizationDigest((StringReader)new StringReader(authorization));
            }
            catch (IOException e) {
                return false;
            }
            if (directives == null) {
                return false;
            }
            this.method = request.getMethod();
            this.userName = (String)directives.get("username");
            this.realmName = (String)directives.get("realm");
            this.nonce = (String)directives.get("nonce");
            this.nc = (String)directives.get("nc");
            this.cnonce = (String)directives.get("cnonce");
            this.qop = (String)directives.get("qop");
            this.uri = (String)directives.get("uri");
            this.response = (String)directives.get("response");
            this.opaqueReceived = (String)directives.get("opaque");
            this.algorithm = (AuthDigest)((Object)PERMITTED_ALGORITHMS.get(directives.get("algorithm")));
            if (this.algorithm == null) {
                this.algorithm = FALLBACK_DIGEST;
            }
            return true;
        }

        @Deprecated
        public boolean validate(Request request) {
            List<AuthDigest> fallbackList = Arrays.asList(FALLBACK_DIGEST);
            return this.validate(request, fallbackList);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean validate(Request request, List<AuthDigest> algorithms) {
            String serverIpTimeKey;
            byte[] buffer;
            String digestServerIpTimeKey;
            long nonceTime;
            String lcRealm;
            String query;
            String uriQuery;
            if (this.userName == null || this.realmName == null || this.nonce == null || this.uri == null || this.response == null) {
                return false;
            }
            if (this.validateUri && !this.uri.equals(uriQuery = (query = request.getQueryString()) == null ? request.getRequestURI() : request.getRequestURI() + "?" + query)) {
                String host = request.getHeader("host");
                String scheme = request.getScheme();
                if (host != null && !uriQuery.startsWith(scheme)) {
                    StringBuilder absolute = new StringBuilder();
                    absolute.append(scheme);
                    absolute.append("://");
                    absolute.append(host);
                    absolute.append(uriQuery);
                    if (!this.uri.equals(absolute.toString())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (!(lcRealm = AuthenticatorBase.getRealmName(request.getContext())).equals(this.realmName)) {
                return false;
            }
            if (!this.opaque.equals(this.opaqueReceived)) {
                return false;
            }
            int i = this.nonce.indexOf(58);
            if (i < 0 || i + 1 == this.nonce.length()) {
                return false;
            }
            try {
                nonceTime = Long.parseLong(this.nonce.substring(0, i));
            }
            catch (NumberFormatException nfe) {
                return false;
            }
            String digestclientIpTimeKey = this.nonce.substring(i + 1);
            long currentTime = System.currentTimeMillis();
            if (currentTime - nonceTime > this.nonceValidity) {
                this.nonceStale = true;
                Map<String, NonceInfo> map = this.nonces;
                synchronized (map) {
                    this.nonces.remove(this.nonce);
                }
            }
            if (!(digestServerIpTimeKey = HexUtils.toHexString((byte[])(buffer = ConcurrentMessageDigest.digest((String)DigestAuthenticator.NONCE_DIGEST, (byte[][])new byte[][]{(serverIpTimeKey = request.getRemoteAddr() + ":" + nonceTime + ":" + this.key).getBytes(StandardCharsets.ISO_8859_1)})))).equals(digestclientIpTimeKey)) {
                return false;
            }
            if (this.qop != null && !DigestAuthenticator.QOP.equals(this.qop)) {
                return false;
            }
            if (this.qop == null) {
                if (this.cnonce != null || this.nc != null) {
                    return false;
                }
            } else {
                NonceInfo info;
                long count;
                if (this.cnonce == null || this.nc == null) {
                    return false;
                }
                if (this.nc.length() < 6 || this.nc.length() > 8) {
                    return false;
                }
                try {
                    count = Long.parseLong(this.nc, 16);
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
                Map<String, NonceInfo> map = this.nonces;
                synchronized (map) {
                    info = this.nonces.get(this.nonce);
                }
                if (info == null) {
                    this.nonceStale = true;
                } else if (!info.nonceCountValid(count)) {
                    return false;
                }
            }
            return algorithms.contains((Object)this.algorithm);
        }

        public boolean isNonceStale() {
            return this.nonceStale;
        }

        public Principal authenticate(Realm realm) {
            String a2 = this.method + ":" + this.uri;
            byte[] buffer = ConcurrentMessageDigest.digest((String)this.algorithm.getJavaName(), (byte[][])new byte[][]{a2.getBytes(StandardCharsets.ISO_8859_1)});
            String digestA2 = HexUtils.toHexString((byte[])buffer);
            return realm.authenticate(this.userName, this.response, this.nonce, this.nc, this.cnonce, this.qop, this.realmName, digestA2, this.algorithm.getJavaName());
        }
    }

    public static class NonceInfo {
        private final long timestamp;
        private final boolean[] seen;
        private final int offset;
        private int count = 0;

        public NonceInfo(long currentTime, int seenWindowSize) {
            this.timestamp = currentTime;
            this.seen = new boolean[seenWindowSize];
            this.offset = seenWindowSize / 2;
        }

        public synchronized boolean nonceCountValid(long nonceCount) {
            if ((long)(this.count - this.offset) >= nonceCount || nonceCount > (long)(this.count - this.offset + this.seen.length)) {
                return false;
            }
            int checkIndex = (int)((nonceCount + (long)this.offset) % (long)this.seen.length);
            if (this.seen[checkIndex]) {
                return false;
            }
            this.seen[checkIndex] = true;
            this.seen[this.count % this.seen.length] = false;
            ++this.count;
            return true;
        }

        public long getTimestamp() {
            return this.timestamp;
        }
    }
}

