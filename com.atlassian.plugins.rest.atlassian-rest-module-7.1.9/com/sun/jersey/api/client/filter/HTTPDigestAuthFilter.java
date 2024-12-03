/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;

public final class HTTPDigestAuthFilter
extends ClientFilter {
    private static final int CNONCE_NB_BYTES = 4;
    private static final Charset CHARACTER_SET = Charset.forName("iso-8859-1");
    private static final SecureRandom randomGenerator;
    private static final Pattern TOKENS_PATTERN;
    private final String user;
    private final byte[] password;
    private final ThreadLocal<State> state = new ThreadLocal<State>(){

        @Override
        protected State initialValue() {
            return new State();
        }
    };

    public HTTPDigestAuthFilter(String user, String password) {
        this(user, password.getBytes(CHARACTER_SET));
    }

    public HTTPDigestAuthFilter(String user, byte[] password) {
        this.user = user;
        this.password = password;
    }

    private static void addKeyVal(StringBuffer buffer, String key, String val, boolean withQuotes) {
        String quote = withQuotes ? "\"" : "";
        buffer.append(key).append('=').append(quote).append(val).append(quote).append(',');
    }

    private static void addKeyVal(StringBuffer buffer, String key, String val) {
        HTTPDigestAuthFilter.addKeyVal(buffer, key, val, true);
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; ++i) {
            int halfbyte = data[i] >>> 4 & 0xF;
            int two_halfs = 0;
            do {
                if (0 <= halfbyte && halfbyte <= 9) {
                    buf.append((char)(48 + halfbyte));
                } else {
                    buf.append((char)(97 + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0xF;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    static String MD5(byte[] text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text, 0, text.length);
            byte[] md5hash = md.digest();
            return HTTPDigestAuthFilter.convertToHex(md5hash);
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    private static String md5ForJoined(byte[] ... vals) {
        return HTTPDigestAuthFilter.MD5(HTTPDigestAuthFilter.joined((byte)58, vals));
    }

    private static String md5ForJoined(String ... vals) {
        return HTTPDigestAuthFilter.MD5(HTTPDigestAuthFilter.joined(':', vals).getBytes(CHARACTER_SET));
    }

    private static byte[] joined(byte separator, byte[] ... vals) {
        ByteArrayOutputStream jointArray = new ByteArrayOutputStream();
        boolean firstItem = true;
        for (byte[] val : vals) {
            try {
                if (firstItem) {
                    firstItem = false;
                } else {
                    jointArray.write(separator);
                }
                jointArray.write(val);
            }
            catch (IOException ex) {
                Logger.getLogger(HTTPDigestAuthFilter.class.getName()).log(Level.SEVERE, null, ex);
                throw new WebApplicationException(ex);
            }
        }
        return jointArray.toByteArray();
    }

    private static String joined(char separator, String[] vals) {
        StringBuilder result = new StringBuilder();
        boolean firstValue = true;
        for (String s : vals) {
            if (!firstValue) {
                result.append(separator);
            } else {
                firstValue = false;
            }
            result.append(s);
        }
        return result.toString();
    }

    String randHexBytes(int nbBytes) {
        byte[] bytes = new byte[nbBytes];
        randomGenerator.nextBytes(bytes);
        return HTTPDigestAuthFilter.convertToHex(bytes);
    }

    static HashMap<String, String> parseHeaders(Collection<String> lines) {
        for (String line : lines) {
            String[] parts = line.trim().split("\\s+", 2);
            if (parts.length != 2 || !parts[0].toLowerCase().equals("digest")) continue;
            Matcher match = TOKENS_PATTERN.matcher(parts[1]);
            HashMap<String, String> result = new HashMap<String, String>();
            while (match.find()) {
                int nbGroups = match.groupCount();
                if (nbGroups != 4) continue;
                String key = match.group(1);
                String valNoQuotes = match.group(3);
                String valQuotes = match.group(4);
                result.put(key, valNoQuotes == null ? valQuotes : valNoQuotes);
            }
            return result;
        }
        return null;
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        ClientResponse response;
        boolean reqHadAuthHeaders = false;
        if (this.state.get().nextNonce != null) {
            String response2;
            reqHadAuthHeaders = true;
            String qopStr = null;
            if (this.state.get().qop != null) {
                qopStr = this.state.get().qop == QOP.AUTH_INT ? "auth-int" : "auth";
            }
            StringBuffer buff = new StringBuffer();
            buff.append("Digest ");
            HTTPDigestAuthFilter.addKeyVal(buff, "username", this.user);
            HTTPDigestAuthFilter.addKeyVal(buff, "realm", this.state.get().realm);
            HTTPDigestAuthFilter.addKeyVal(buff, "nonce", this.state.get().nextNonce);
            if (this.state.get().opaque != null) {
                HTTPDigestAuthFilter.addKeyVal(buff, "opaque", this.state.get().opaque);
            }
            if (this.state.get().algorithm != null) {
                HTTPDigestAuthFilter.addKeyVal(buff, "algorithm", this.state.get().algorithm, false);
            }
            if (this.state.get().qop != null) {
                HTTPDigestAuthFilter.addKeyVal(buff, "qop", qopStr, false);
            }
            String HA1 = HTTPDigestAuthFilter.md5ForJoined(this.user.getBytes(CHARACTER_SET), this.state.get().realm.getBytes(CHARACTER_SET), this.password);
            String uri = request.getURI().getRawPath();
            HTTPDigestAuthFilter.addKeyVal(buff, "uri", uri);
            String HA2 = this.state.get().qop == QOP.AUTH_INT && request.getEntity() != null ? HTTPDigestAuthFilter.md5ForJoined(request.getMethod(), uri, request.getEntity().toString()) : HTTPDigestAuthFilter.md5ForJoined(request.getMethod(), uri);
            if (this.state.get().qop == null) {
                response2 = HTTPDigestAuthFilter.md5ForJoined(HA1, this.state.get().nextNonce, HA2);
            } else {
                String cnonce = this.randHexBytes(4);
                String nc = String.format("%08x", this.state.get().counter);
                ++this.state.get().counter;
                HTTPDigestAuthFilter.addKeyVal(buff, "cnonce", cnonce);
                HTTPDigestAuthFilter.addKeyVal(buff, "nc", nc, false);
                response2 = HTTPDigestAuthFilter.md5ForJoined(HA1, this.state.get().nextNonce, nc, cnonce, qopStr, HA2);
            }
            HTTPDigestAuthFilter.addKeyVal(buff, "response", response2);
            buff.deleteCharAt(buff.length() - 1);
            String authLine = buff.toString();
            request.getHeaders().add("Authorization", authLine);
        }
        if ((response = this.getNext().handle(request)).getClientResponseStatus() == ClientResponse.Status.UNAUTHORIZED) {
            boolean stale;
            HashMap<String, String> map = HTTPDigestAuthFilter.parseHeaders((Collection)response.getHeaders().get("WWW-Authenticate"));
            if (map == null) {
                return response;
            }
            this.state.get().realm = map.get("realm");
            this.state.get().nextNonce = map.get("nonce");
            this.state.get().opaque = map.get("opaque");
            this.state.get().algorithm = map.get("algorithm");
            this.state.get().domain = map.get("domain");
            String qop = map.get("qop");
            this.state.get().qop = qop == null ? null : (qop.contains("auth-int") ? QOP.AUTH_INT : (qop.contains("auth") ? QOP.AUTH : null));
            String staleStr = map.get("stale");
            boolean bl = stale = staleStr != null && staleStr.toLowerCase().equals("true");
            if (stale || !reqHadAuthHeaders) {
                response.close();
                return this.handle(request);
            }
            return response;
        }
        return response;
    }

    static {
        try {
            randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (Exception e) {
            throw new Error(e);
        }
        TOKENS_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\"([^\"]+)\"|(\\w+))\\s*,?\\s*");
    }

    private class State {
        String nextNonce;
        String realm;
        String opaque;
        String algorithm;
        String domain;
        QOP qop = null;
        int counter = 1;

        private State() {
        }
    }

    private static enum QOP {
        AUTH,
        AUTH_INT;

    }
}

