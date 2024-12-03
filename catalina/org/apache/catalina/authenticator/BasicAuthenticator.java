/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.codec.binary.Base64
 */
package org.apache.catalina.authenticator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.codec.binary.Base64;

public class BasicAuthenticator
extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(BasicAuthenticator.class);
    private Charset charset = StandardCharsets.ISO_8859_1;
    private String charsetString = null;
    private boolean trimCredentials = true;

    public String getCharset() {
        return this.charsetString;
    }

    public void setCharset(String charsetString) {
        if (charsetString == null || charsetString.isEmpty()) {
            this.charset = StandardCharsets.ISO_8859_1;
        } else if ("UTF-8".equalsIgnoreCase(charsetString)) {
            this.charset = StandardCharsets.UTF_8;
        } else {
            throw new IllegalArgumentException(sm.getString("basicAuthenticator.invalidCharset"));
        }
        this.charsetString = charsetString;
    }

    public boolean getTrimCredentials() {
        return this.trimCredentials;
    }

    public void setTrimCredentials(boolean trimCredentials) {
        this.trimCredentials = trimCredentials;
    }

    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        block6: {
            if (this.checkForCachedAuthentication(request, response, true)) {
                return true;
            }
            MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
            if (authorization != null) {
                authorization.toBytes();
                ByteChunk authorizationBC = authorization.getByteChunk();
                BasicCredentials credentials = null;
                try {
                    credentials = new BasicCredentials(authorizationBC, this.charset, this.getTrimCredentials());
                    String username = credentials.getUsername();
                    String password = credentials.getPassword();
                    Principal principal = this.context.getRealm().authenticate(username, password);
                    if (principal != null) {
                        this.register(request, response, principal, "BASIC", username, password);
                        return true;
                    }
                }
                catch (IllegalArgumentException iae) {
                    if (!this.log.isDebugEnabled()) break block6;
                    this.log.debug((Object)sm.getString("basicAuthenticator.invalidAuthorization", new Object[]{iae.getMessage()}));
                }
            }
        }
        StringBuilder value = new StringBuilder(16);
        value.append("Basic realm=\"");
        value.append(BasicAuthenticator.getRealmName(this.context));
        value.append('\"');
        if (this.charsetString != null && !this.charsetString.isEmpty()) {
            value.append(", charset=");
            value.append(this.charsetString);
        }
        response.setHeader("WWW-Authenticate", value.toString());
        response.sendError(401);
        return false;
    }

    @Override
    protected String getAuthMethod() {
        return "BASIC";
    }

    @Override
    protected boolean isPreemptiveAuthPossible(Request request) {
        MessageBytes authorizationHeader = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        return authorizationHeader != null && authorizationHeader.startsWithIgnoreCase("basic ", 0);
    }

    public static class BasicCredentials {
        private static final String METHOD = "basic ";
        private final Charset charset;
        private final boolean trimCredentials;
        private final ByteChunk authorization;
        private final int initialOffset;
        private int base64blobOffset;
        private int base64blobLength;
        private String username = null;
        private String password = null;

        @Deprecated
        public BasicCredentials(ByteChunk input, Charset charset) throws IllegalArgumentException {
            this(input, charset, true);
        }

        public BasicCredentials(ByteChunk input, Charset charset, boolean trimCredentials) throws IllegalArgumentException {
            this.authorization = input;
            this.initialOffset = input.getOffset();
            this.charset = charset;
            this.trimCredentials = trimCredentials;
            this.parseMethod();
            byte[] decoded = this.parseBase64();
            this.parseCredentials(decoded);
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        private void parseMethod() throws IllegalArgumentException {
            if (!this.authorization.startsWithIgnoreCase(METHOD, 0)) {
                throw new IllegalArgumentException(AuthenticatorBase.sm.getString("basicAuthenticator.notBasic"));
            }
            this.base64blobOffset = this.initialOffset + METHOD.length();
            this.base64blobLength = this.authorization.getLength() - METHOD.length();
        }

        private byte[] parseBase64() throws IllegalArgumentException {
            byte[] decoded = Base64.decodeBase64((byte[])this.authorization.getBuffer(), (int)this.base64blobOffset, (int)this.base64blobLength);
            this.authorization.setOffset(this.initialOffset);
            if (decoded == null) {
                throw new IllegalArgumentException(AuthenticatorBase.sm.getString("basicAuthenticator.notBase64"));
            }
            return decoded;
        }

        private void parseCredentials(byte[] decoded) throws IllegalArgumentException {
            int colon = -1;
            for (int i = 0; i < decoded.length; ++i) {
                if (decoded[i] != 58) continue;
                colon = i;
                break;
            }
            if (colon < 0) {
                this.username = new String(decoded, this.charset);
            } else {
                this.username = new String(decoded, 0, colon, this.charset);
                this.password = new String(decoded, colon + 1, decoded.length - colon - 1, this.charset);
                if (this.password.length() > 1 && this.trimCredentials) {
                    this.password = this.password.trim();
                }
            }
            if (this.username.length() > 1 && this.trimCredentials) {
                this.username = this.username.trim();
            }
        }
    }
}

