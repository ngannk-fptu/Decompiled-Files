/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.util.Attributes
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.AbstractAuthentication;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPNEGOAuthentication
extends AbstractAuthentication {
    private static final Logger LOG = LoggerFactory.getLogger(SPNEGOAuthentication.class);
    private static final String NEGOTIATE = HttpHeader.NEGOTIATE.asString();
    private final GSSManager gssManager = GSSManager.getInstance();
    private String userName;
    private String userPassword;
    private Path userKeyTabPath;
    private String serviceName;
    private boolean useTicketCache;
    private Path ticketCachePath;
    private boolean renewTGT;

    public SPNEGOAuthentication(URI uri) {
        super(uri, "<<ANY_REALM>>");
    }

    @Override
    public String getType() {
        return NEGOTIATE;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Path getUserKeyTabPath() {
        return this.userKeyTabPath;
    }

    public void setUserKeyTabPath(Path userKeyTabPath) {
        this.userKeyTabPath = userKeyTabPath;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isUseTicketCache() {
        return this.useTicketCache;
    }

    public void setUseTicketCache(boolean useTicketCache) {
        this.useTicketCache = useTicketCache;
    }

    public Path getTicketCachePath() {
        return this.ticketCachePath;
    }

    public void setTicketCachePath(Path ticketCachePath) {
        this.ticketCachePath = ticketCachePath;
    }

    public boolean isRenewTGT() {
        return this.renewTGT;
    }

    public void setRenewTGT(boolean renewTGT) {
        this.renewTGT = renewTGT;
    }

    @Override
    public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context) {
        String b64Input;
        SPNEGOContext spnegoContext = (SPNEGOContext)context.getAttribute(SPNEGOContext.ATTRIBUTE);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authenticate with context {}", (Object)spnegoContext);
        }
        if (spnegoContext == null) {
            spnegoContext = this.login();
            context.setAttribute(SPNEGOContext.ATTRIBUTE, (Object)spnegoContext);
        }
        byte[] input = (b64Input = headerInfo.getBase64()) == null ? new byte[]{} : Base64.getDecoder().decode(b64Input);
        byte[] output = Subject.doAs(spnegoContext.subject, this.initGSSContext(spnegoContext, request.getHost(), input));
        String b64Output = output == null ? null : new String(Base64.getEncoder().encode(output));
        return new SPNEGOResult(null, b64Output);
    }

    private SPNEGOContext login() {
        try {
            String user = this.getUserName();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Logging in user {}", (Object)user);
            }
            PasswordCallbackHandler callbackHandler = new PasswordCallbackHandler();
            LoginContext loginContext = new LoginContext("", null, callbackHandler, new SPNEGOConfiguration());
            loginContext.login();
            Subject subject = loginContext.getSubject();
            SPNEGOContext spnegoContext = new SPNEGOContext();
            spnegoContext.subject = subject;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Initialized {}", (Object)spnegoContext);
            }
            return spnegoContext;
        }
        catch (LoginException x) {
            throw new RuntimeException(x);
        }
    }

    private PrivilegedAction<byte[]> initGSSContext(SPNEGOContext spnegoContext, String host, byte[] bytes) {
        return () -> {
            try {
                GSSContext gssContext = spnegoContext.gssContext;
                if (gssContext == null) {
                    String principal = this.getServiceName() + "@" + host;
                    GSSName serviceName = this.gssManager.createName(principal, GSSName.NT_HOSTBASED_SERVICE);
                    Oid spnegoOid = new Oid("1.3.6.1.5.5.2");
                    spnegoContext.gssContext = gssContext = this.gssManager.createContext(serviceName, spnegoOid, null, Integer.MAX_VALUE);
                    gssContext.requestMutualAuth(true);
                }
                byte[] result = gssContext.initSecContext(bytes, 0, bytes.length);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} {}", (Object)(gssContext.isEstablished() ? "Initialized" : "Initializing"), (Object)gssContext);
                }
                return result;
            }
            catch (GSSException x) {
                throw new RuntimeException(x);
            }
        };
    }

    private static class SPNEGOContext {
        private static final String ATTRIBUTE = SPNEGOContext.class.getName();
        private Subject subject;
        private GSSContext gssContext;

        private SPNEGOContext() {
        }

        public String toString() {
            return String.format("%s@%x[context=%s]", this.getClass().getSimpleName(), this.hashCode(), this.gssContext);
        }
    }

    public static class SPNEGOResult
    implements Authentication.Result {
        private final URI uri;
        private final HttpHeader header;
        private final String value;

        public SPNEGOResult(URI uri, String token) {
            this(uri, HttpHeader.AUTHORIZATION, token);
        }

        public SPNEGOResult(URI uri, HttpHeader header, String token) {
            this.uri = uri;
            this.header = header;
            this.value = NEGOTIATE + (String)(token == null ? "" : " " + token);
        }

        @Override
        public URI getURI() {
            return this.uri;
        }

        @Override
        public void apply(Request request) {
            request.headers(headers -> headers.add(this.header, this.value));
        }
    }

    private class PasswordCallbackHandler
    implements CallbackHandler {
        private PasswordCallbackHandler() {
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException {
            PasswordCallback callback = Arrays.stream(callbacks).filter(PasswordCallback.class::isInstance).map(PasswordCallback.class::cast).findAny().filter(c -> c.getPrompt().contains(SPNEGOAuthentication.this.getUserName())).orElseThrow(IOException::new);
            callback.setPassword(SPNEGOAuthentication.this.getUserPassword().toCharArray());
        }
    }

    private class SPNEGOConfiguration
    extends Configuration {
        private SPNEGOConfiguration() {
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            boolean useTicketCache;
            HashMap<String, String> options = new HashMap<String, String>();
            if (LOG.isDebugEnabled()) {
                options.put("debug", "true");
            }
            options.put("refreshKrb5Config", "true");
            options.put("principal", SPNEGOAuthentication.this.getUserName());
            options.put("isInitiator", "true");
            Path keyTabPath = SPNEGOAuthentication.this.getUserKeyTabPath();
            if (keyTabPath != null) {
                options.put("doNotPrompt", "true");
                options.put("useKeyTab", "true");
                options.put("keyTab", keyTabPath.toAbsolutePath().toString());
                options.put("storeKey", "true");
            }
            if (useTicketCache = SPNEGOAuthentication.this.isUseTicketCache()) {
                options.put("useTicketCache", "true");
                Path ticketCachePath = SPNEGOAuthentication.this.getTicketCachePath();
                if (ticketCachePath != null) {
                    options.put("ticketCache", ticketCachePath.toAbsolutePath().toString());
                }
                options.put("renewTGT", String.valueOf(SPNEGOAuthentication.this.isRenewTGT()));
            }
            String moduleClass = "com.sun.security.auth.module.Krb5LoginModule";
            AppConfigurationEntry config = new AppConfigurationEntry(moduleClass, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
            return new AppConfigurationEntry[]{config};
        }
    }
}

