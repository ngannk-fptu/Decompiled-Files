/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.DirContextProxy;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.support.LdapUtils;

public abstract class AbstractTlsDirContextAuthenticationStrategy
implements DirContextAuthenticationStrategy {
    private HostnameVerifier hostnameVerifier;
    private boolean shutdownTlsGracefully = false;
    private SSLSocketFactory sslSocketFactory;

    public void setShutdownTlsGracefully(boolean shutdownTlsGracefully) {
        this.shutdownTlsGracefully = shutdownTlsGracefully;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public final void setupEnvironment(Hashtable<String, Object> env, String userDn, String password) {
    }

    @Override
    public final DirContext processContextAfterCreation(DirContext ctx, String userDn, String password) throws NamingException {
        if (ctx instanceof LdapContext) {
            LdapContext ldapCtx = (LdapContext)ctx;
            StartTlsResponse tlsResponse = (StartTlsResponse)ldapCtx.extendedOperation(new StartTlsRequest());
            try {
                if (this.hostnameVerifier != null) {
                    tlsResponse.setHostnameVerifier(this.hostnameVerifier);
                }
                tlsResponse.negotiate(this.sslSocketFactory);
                this.applyAuthentication(ldapCtx, userDn, password);
                if (this.shutdownTlsGracefully) {
                    return (DirContext)Proxy.newProxyInstance(DirContextProxy.class.getClassLoader(), new Class[]{LdapContext.class, DirContextProxy.class}, (InvocationHandler)new TlsAwareDirContextProxy(ldapCtx, tlsResponse));
                }
                return ctx;
            }
            catch (IOException e) {
                LdapUtils.closeContext(ctx);
                throw new UncategorizedLdapException("Failed to negotiate TLS session", e);
            }
        }
        throw new IllegalArgumentException("Processed Context must be an LDAPv3 context, i.e. an LdapContext implementation");
    }

    protected abstract void applyAuthentication(LdapContext var1, String var2, String var3) throws NamingException;

    private static final class TlsAwareDirContextProxy
    implements DirContextProxy,
    InvocationHandler {
        private static final String GET_TARGET_CONTEXT_METHOD_NAME = "getTargetContext";
        private static final String CLOSE_METHOD_NAME = "close";
        private final LdapContext target;
        private final StartTlsResponse tlsResponse;

        public TlsAwareDirContextProxy(LdapContext target, StartTlsResponse tlsResponse) {
            this.target = target;
            this.tlsResponse = tlsResponse;
        }

        @Override
        public DirContext getTargetContext() {
            return this.target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals(CLOSE_METHOD_NAME)) {
                this.tlsResponse.close();
                return method.invoke((Object)this.target, args);
            }
            if (method.getName().equals(GET_TARGET_CONTEXT_METHOD_NAME)) {
                return this.target;
            }
            return method.invoke((Object)this.target, args);
        }
    }
}

