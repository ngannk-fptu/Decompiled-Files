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
 *  org.apache.tomcat.util.compat.JreVendor
 */
package org.apache.catalina.authenticator;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.compat.JreVendor;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;

public class SpnegoAuthenticator
extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(SpnegoAuthenticator.class);
    private static final String AUTH_HEADER_VALUE_NEGOTIATE = "Negotiate";
    private String loginConfigName = "com.sun.security.jgss.krb5.accept";
    private boolean storeDelegatedCredential = true;
    private Pattern noKeepAliveUserAgents = null;
    private boolean applyJava8u40Fix = true;

    public String getLoginConfigName() {
        return this.loginConfigName;
    }

    public void setLoginConfigName(String loginConfigName) {
        this.loginConfigName = loginConfigName;
    }

    public boolean isStoreDelegatedCredential() {
        return this.storeDelegatedCredential;
    }

    public void setStoreDelegatedCredential(boolean storeDelegatedCredential) {
        this.storeDelegatedCredential = storeDelegatedCredential;
    }

    public String getNoKeepAliveUserAgents() {
        Pattern p = this.noKeepAliveUserAgents;
        if (p == null) {
            return null;
        }
        return p.pattern();
    }

    public void setNoKeepAliveUserAgents(String noKeepAliveUserAgents) {
        this.noKeepAliveUserAgents = noKeepAliveUserAgents == null || noKeepAliveUserAgents.length() == 0 ? null : Pattern.compile(noKeepAliveUserAgents);
    }

    public boolean getApplyJava8u40Fix() {
        return this.applyJava8u40Fix;
    }

    public void setApplyJava8u40Fix(boolean applyJava8u40Fix) {
        this.applyJava8u40Fix = applyJava8u40Fix;
    }

    @Override
    protected String getAuthMethod() {
        return "SPNEGO";
    }

    @Override
    protected void initInternal() throws LifecycleException {
        String jaasConf;
        super.initInternal();
        String krb5Conf = System.getProperty("java.security.krb5.conf");
        if (krb5Conf == null) {
            File krb5ConfFile = new File(this.container.getCatalinaBase(), "conf/krb5.ini");
            System.setProperty("java.security.krb5.conf", krb5ConfFile.getAbsolutePath());
        }
        if ((jaasConf = System.getProperty("java.security.auth.login.config")) == null) {
            File jaasConfFile = new File(this.container.getCatalinaBase(), "conf/jaas.conf");
            System.setProperty("java.security.auth.login.config", jaasConfFile.getAbsolutePath());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (this.checkForCachedAuthentication(request, response, true)) {
            return true;
        }
        MessageBytes authorization = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        if (authorization == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("authenticator.noAuthHeader"));
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            return false;
        }
        authorization.toBytes();
        ByteChunk authorizationBC = authorization.getByteChunk();
        if (!authorizationBC.startsWithIgnoreCase("negotiate ", 0)) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("spnegoAuthenticator.authHeaderNotNego"));
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            return false;
        }
        authorizationBC.setOffset(authorizationBC.getOffset() + 10);
        byte[] decoded = Base64.decodeBase64((byte[])authorizationBC.getBuffer(), (int)authorizationBC.getOffset(), (int)authorizationBC.getLength());
        if (this.getApplyJava8u40Fix()) {
            SpnegoTokenFixer.fix(decoded);
        }
        if (decoded.length == 0) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("spnegoAuthenticator.authHeaderNoToken"));
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            return false;
        }
        LoginContext lc = null;
        GSSContext gssContext = null;
        byte[] outToken = null;
        Principal principal = null;
        try {
            try {
                lc = new LoginContext(this.getLoginConfigName());
                lc.login();
            }
            catch (LoginException e) {
                this.log.error((Object)sm.getString("spnegoAuthenticator.serviceLoginFail"), (Throwable)e);
                response.sendError(500);
                boolean bl = false;
                if (gssContext != null) {
                    try {
                        gssContext.dispose();
                    }
                    catch (GSSException gSSException) {
                        // empty catch block
                    }
                }
                if (lc != null) {
                    try {
                        lc.logout();
                    }
                    catch (LoginException loginException) {
                        // empty catch block
                    }
                }
                return bl;
            }
            Subject subject = lc.getSubject();
            GSSManager manager = GSSManager.getInstance();
            int credentialLifetime = JreVendor.IS_IBM_JVM ? Integer.MAX_VALUE : 0;
            PrivilegedExceptionAction<GSSCredential> action = () -> manager.createCredential(null, credentialLifetime, new Oid("1.3.6.1.5.5.2"), 2);
            gssContext = manager.createContext(Subject.doAs(subject, action));
            outToken = Subject.doAs(lc.getSubject(), new AcceptAction(gssContext, decoded));
            if (outToken == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("spnegoAuthenticator.ticketValidateFail"));
                }
                response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
                response.sendError(401);
                boolean bl = false;
                return bl;
            }
            principal = Subject.doAs(subject, new AuthenticateAction(this.context.getRealm(), gssContext, this.storeDelegatedCredential));
        }
        catch (GSSException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)sm.getString("spnegoAuthenticator.ticketValidateFail"), (Throwable)e);
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            boolean manager = false;
            return manager;
        }
        catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof GSSException) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)sm.getString("spnegoAuthenticator.serviceLoginFail"), (Throwable)e);
                }
            } else {
                this.log.error((Object)sm.getString("spnegoAuthenticator.serviceLoginFail"), (Throwable)e);
            }
            response.setHeader("WWW-Authenticate", AUTH_HEADER_VALUE_NEGOTIATE);
            response.sendError(401);
            boolean bl = false;
            return bl;
        }
        finally {
            if (gssContext != null) {
                try {
                    gssContext.dispose();
                }
                catch (GSSException gSSException) {}
            }
            if (lc != null) {
                try {
                    lc.logout();
                }
                catch (LoginException loginException) {}
            }
        }
        response.setHeader("WWW-Authenticate", "Negotiate " + Base64.encodeBase64String((byte[])outToken));
        if (principal != null) {
            MessageBytes ua;
            this.register(request, response, principal, "SPNEGO", principal.getName(), null);
            Pattern p = this.noKeepAliveUserAgents;
            if (p != null && (ua = request.getCoyoteRequest().getMimeHeaders().getValue("user-agent")) != null && p.matcher(ua.toString()).matches()) {
                response.setHeader("Connection", "close");
            }
            return true;
        }
        response.sendError(401);
        return false;
    }

    @Override
    protected boolean isPreemptiveAuthPossible(Request request) {
        MessageBytes authorizationHeader = request.getCoyoteRequest().getMimeHeaders().getValue("authorization");
        return authorizationHeader != null && authorizationHeader.startsWithIgnoreCase("negotiate ", 0);
    }

    public static class SpnegoTokenFixer {
        private final byte[] token;
        private int pos = 0;

        public static void fix(byte[] token) {
            SpnegoTokenFixer fixer = new SpnegoTokenFixer(token);
            fixer.fix();
        }

        private SpnegoTokenFixer(byte[] token) {
            this.token = token;
        }

        private void fix() {
            if (!this.tag(96)) {
                return;
            }
            if (!this.length()) {
                return;
            }
            if (!this.oid("1.3.6.1.5.5.2")) {
                return;
            }
            if (!this.tag(160)) {
                return;
            }
            if (!this.length()) {
                return;
            }
            if (!this.tag(48)) {
                return;
            }
            if (!this.length()) {
                return;
            }
            if (!this.tag(160)) {
                return;
            }
            this.lengthAsInt();
            if (!this.tag(48)) {
                return;
            }
            int mechTypesLen = this.lengthAsInt();
            int mechTypesStart = this.pos;
            LinkedHashMap<String, int[]> mechTypeEntries = new LinkedHashMap<String, int[]>();
            while (this.pos < mechTypesStart + mechTypesLen) {
                int[] value = new int[2];
                value[0] = this.pos;
                String key = this.oidAsString();
                value[1] = this.pos - value[0];
                mechTypeEntries.put(key, value);
            }
            byte[] replacement = new byte[mechTypesLen];
            int replacementPos = 0;
            int[] first = (int[])mechTypeEntries.remove("1.2.840.113554.1.2.2");
            if (first != null) {
                System.arraycopy(this.token, first[0], replacement, replacementPos, first[1]);
                replacementPos += first[1];
            }
            for (int[] markers : mechTypeEntries.values()) {
                System.arraycopy(this.token, markers[0], replacement, replacementPos, markers[1]);
                replacementPos += markers[1];
            }
            System.arraycopy(replacement, 0, this.token, mechTypesStart, mechTypesLen);
        }

        private boolean tag(int expected) {
            return (this.token[this.pos++] & 0xFF) == expected;
        }

        private boolean length() {
            int len = this.lengthAsInt();
            return this.pos + len == this.token.length;
        }

        private int lengthAsInt() {
            int len;
            if ((len = this.token[this.pos++] & 0xFF) > 127) {
                int bytes = len - 128;
                len = 0;
                for (int i = 0; i < bytes; ++i) {
                    len <<= 8;
                    len += this.token[this.pos++] & 0xFF;
                }
            }
            return len;
        }

        private boolean oid(String expected) {
            return expected.equals(this.oidAsString());
        }

        private String oidAsString() {
            if (!this.tag(6)) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            int len = this.lengthAsInt();
            int v = this.token[this.pos++] & 0xFF;
            int c2 = v % 40;
            int c1 = (v - c2) / 40;
            result.append(c1);
            result.append('.');
            result.append(c2);
            int c = 0;
            boolean write = false;
            for (int i = 1; i < len; ++i) {
                int b;
                if ((b = this.token[this.pos++] & 0xFF) > 127) {
                    b -= 128;
                } else {
                    write = true;
                }
                c <<= 7;
                c += b;
                if (!write) continue;
                result.append('.');
                result.append(c);
                c = 0;
                write = false;
            }
            return result.toString();
        }
    }

    public static class AcceptAction
    implements PrivilegedExceptionAction<byte[]> {
        GSSContext gssContext;
        byte[] decoded;

        public AcceptAction(GSSContext context, byte[] decodedToken) {
            this.gssContext = context;
            this.decoded = decodedToken;
        }

        @Override
        public byte[] run() throws GSSException {
            return this.gssContext.acceptSecContext(this.decoded, 0, this.decoded.length);
        }
    }

    public static class AuthenticateAction
    implements PrivilegedAction<Principal> {
        private final Realm realm;
        private final GSSContext gssContext;
        private final boolean storeDelegatedCredential;

        public AuthenticateAction(Realm realm, GSSContext gssContext, boolean storeDelegatedCredential) {
            this.realm = realm;
            this.gssContext = gssContext;
            this.storeDelegatedCredential = storeDelegatedCredential;
        }

        @Override
        public Principal run() {
            return this.realm.authenticate(this.gssContext, this.storeDelegatedCredential);
        }
    }
}

