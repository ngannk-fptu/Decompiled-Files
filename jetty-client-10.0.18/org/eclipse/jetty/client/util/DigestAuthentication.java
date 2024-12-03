/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.util.Attributes
 *  org.eclipse.jetty.util.StringUtil
 *  org.eclipse.jetty.util.TypeUtil
 */
package org.eclipse.jetty.client.util;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.AbstractAuthentication;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;

public class DigestAuthentication
extends AbstractAuthentication {
    private final Random random;
    private final String user;
    private final String password;

    public DigestAuthentication(URI uri, String realm, String user, String password) {
        this(uri, realm, user, password, new SecureRandom());
    }

    public DigestAuthentication(URI uri, String realm, String user, String password, Random random) {
        super(uri, realm);
        Objects.requireNonNull(random);
        this.random = random;
        this.user = user;
        this.password = password;
    }

    @Override
    public String getType() {
        return "Digest";
    }

    @Override
    public boolean matches(String type, URI uri, String realm) {
        if (realm == null) {
            return false;
        }
        return super.matches(type, uri, realm);
    }

    @Override
    public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context) {
        String realm;
        MessageDigest digester;
        Map<String, String> params = headerInfo.getParameters();
        String nonce = params.get("nonce");
        if (nonce == null || nonce.length() == 0) {
            return null;
        }
        String opaque = params.get("opaque");
        String algorithm = params.get("algorithm");
        if (algorithm == null) {
            algorithm = "MD5";
        }
        if ((digester = this.getMessageDigest(algorithm)) == null) {
            return null;
        }
        String serverQOP = params.get("qop");
        String clientQOP = null;
        if (serverQOP != null) {
            List serverQOPValues = StringUtil.csvSplit(null, (String)serverQOP, (int)0, (int)serverQOP.length());
            if (serverQOPValues.contains("auth")) {
                clientQOP = "auth";
            } else if (serverQOPValues.contains("auth-int")) {
                clientQOP = "auth-int";
            }
        }
        if ("<<ANY_REALM>>".equals(realm = this.getRealm())) {
            realm = headerInfo.getRealm();
        }
        return new DigestResult(headerInfo.getHeader(), response.getContent(), realm, this.user, this.password, algorithm, nonce, clientQOP, opaque);
    }

    private MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException x) {
            return null;
        }
    }

    private class DigestResult
    implements Authentication.Result {
        private final AtomicInteger nonceCount = new AtomicInteger();
        private final HttpHeader header;
        private final byte[] content;
        private final String realm;
        private final String user;
        private final String password;
        private final String algorithm;
        private final String nonce;
        private final String qop;
        private final String opaque;

        public DigestResult(HttpHeader header, byte[] content, String realm, String user, String password, String algorithm, String nonce, String qop, String opaque) {
            this.header = header;
            this.content = content;
            this.realm = realm;
            this.user = user;
            this.password = password;
            this.algorithm = algorithm;
            this.nonce = nonce;
            this.qop = qop;
            this.opaque = opaque;
        }

        @Override
        public URI getURI() {
            return DigestAuthentication.this.getURI();
        }

        @Override
        public void apply(Request request) {
            String a3;
            String clientNonce;
            String nonceCount;
            MessageDigest digester = DigestAuthentication.this.getMessageDigest(this.algorithm);
            if (digester == null) {
                return;
            }
            String a1 = this.user + ":" + this.realm + ":" + this.password;
            String hashA1 = this.toHexString(digester.digest(a1.getBytes(StandardCharsets.ISO_8859_1)));
            String query = request.getQuery();
            String path = request.getPath();
            String uri = query == null ? path : path + "?" + query;
            String a2 = request.getMethod() + ":" + uri;
            if ("auth-int".equals(this.qop)) {
                a2 = a2 + ":" + this.toHexString(digester.digest(this.content));
            }
            String hashA2 = this.toHexString(digester.digest(a2.getBytes(StandardCharsets.ISO_8859_1)));
            if (this.qop != null) {
                nonceCount = this.nextNonceCount();
                clientNonce = this.newClientNonce();
                a3 = hashA1 + ":" + this.nonce + ":" + nonceCount + ":" + clientNonce + ":" + this.qop + ":" + hashA2;
            } else {
                nonceCount = null;
                clientNonce = null;
                a3 = hashA1 + ":" + this.nonce + ":" + hashA2;
            }
            String hashA3 = this.toHexString(digester.digest(a3.getBytes(StandardCharsets.ISO_8859_1)));
            StringBuilder value = new StringBuilder("Digest");
            value.append(" username=\"").append(this.user).append("\"");
            value.append(", realm=\"").append(this.realm).append("\"");
            value.append(", nonce=\"").append(this.nonce).append("\"");
            if (this.opaque != null) {
                value.append(", opaque=\"").append(this.opaque).append("\"");
            }
            value.append(", algorithm=\"").append(this.algorithm).append("\"");
            value.append(", uri=\"").append(uri).append("\"");
            if (this.qop != null) {
                value.append(", qop=\"").append(this.qop).append("\"");
                value.append(", nc=\"").append(nonceCount).append("\"");
                value.append(", cnonce=\"").append(clientNonce).append("\"");
            }
            value.append(", response=\"").append(hashA3).append("\"");
            request.headers(headers -> headers.add(this.header, value.toString()));
        }

        private String nextNonceCount() {
            String padding = "00000000";
            String next = Integer.toHexString(this.nonceCount.incrementAndGet()).toLowerCase(Locale.ENGLISH);
            return padding.substring(0, padding.length() - next.length()) + next;
        }

        private String newClientNonce() {
            byte[] bytes = new byte[8];
            DigestAuthentication.this.random.nextBytes(bytes);
            return this.toHexString(bytes);
        }

        private String toHexString(byte[] bytes) {
            return TypeUtil.toHexString((byte[])bytes).toLowerCase(Locale.ENGLISH);
        }
    }
}

