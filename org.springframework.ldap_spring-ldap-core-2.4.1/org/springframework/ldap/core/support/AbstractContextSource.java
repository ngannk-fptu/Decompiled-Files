/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.ldap.core.support;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractContextSource
implements BaseLdapPathContextSource,
InitializingBean {
    private static final String DEFAULT_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final Class<DefaultDirObjectFactory> DEFAULT_DIR_OBJECT_FACTORY = DefaultDirObjectFactory.class;
    private static final boolean DONT_DISABLE_POOLING = false;
    private static final boolean EXPLICITLY_DISABLE_POOLING = true;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private Class<?> dirObjectFactory = DEFAULT_DIR_OBJECT_FACTORY;
    private Class<?> contextFactory;
    private LdapName base = LdapUtils.emptyLdapName();
    @Deprecated
    protected String userDn = "";
    @Deprecated
    protected String password = "";
    private String[] urls;
    private boolean pooled = false;
    private Hashtable<String, Object> baseEnv = new Hashtable();
    private Hashtable<String, Object> anonymousEnv;
    private AuthenticationSource authenticationSource;
    private boolean cacheEnvironmentProperties = true;
    private boolean anonymousReadOnly = false;
    private String referral = null;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractContextSource.class);
    public static final String SUN_LDAP_POOLING_FLAG = "com.sun.jndi.ldap.connect.pool";
    private static final String JDK_142 = "1.4.2";
    private DirContextAuthenticationStrategy authenticationStrategy = new SimpleDirContextAuthenticationStrategy();

    public AbstractContextSource() {
        try {
            this.contextFactory = Class.forName(DEFAULT_CONTEXT_FACTORY);
        }
        catch (ClassNotFoundException e) {
            LOG.trace("The default for contextFactory cannot be resolved", (Throwable)e);
        }
    }

    @Override
    public DirContext getContext(String principal, String credentials) {
        return this.doGetContext(principal, credentials, true);
    }

    private DirContext doGetContext(String principal, String credentials, boolean explicitlyDisablePooling) {
        Hashtable<String, Object> env = this.getAuthenticatedEnv(principal, credentials);
        if (explicitlyDisablePooling) {
            env.remove(SUN_LDAP_POOLING_FLAG);
        }
        DirContext ctx = this.createContext(env);
        try {
            DirContext processedDirContext = this.authenticationStrategy.processContextAfterCreation(ctx, principal, credentials);
            return processedDirContext;
        }
        catch (NamingException e) {
            this.closeContext(ctx);
            throw LdapUtils.convertLdapException(e);
        }
    }

    @Override
    public DirContext getReadOnlyContext() {
        if (!this.anonymousReadOnly) {
            return this.doGetContext(this.authenticationSource.getPrincipal(), this.authenticationSource.getCredentials(), false);
        }
        return this.createContext(this.getAnonymousEnv());
    }

    @Override
    public DirContext getReadWriteContext() {
        return this.doGetContext(this.authenticationSource.getPrincipal(), this.authenticationSource.getCredentials(), false);
    }

    protected void setupAuthenticatedEnvironment(Hashtable<String, Object> env, String principal, String credentials) {
        try {
            this.authenticationStrategy.setupEnvironment(env, principal, credentials);
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
    }

    private void closeContext(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            }
            catch (Exception e) {
                LOG.debug("Exception closing context", (Throwable)e);
            }
        }
    }

    public String assembleProviderUrlString(String[] ldapUrls) {
        StringBuilder providerUrlBuffer = new StringBuilder(1024);
        for (String ldapUrl : ldapUrls) {
            providerUrlBuffer.append(ldapUrl);
            if (!this.base.isEmpty() && !ldapUrl.endsWith("/")) {
                providerUrlBuffer.append("/");
            }
            providerUrlBuffer.append(AbstractContextSource.formatForUrl(this.base));
            providerUrlBuffer.append(' ');
        }
        return providerUrlBuffer.toString().trim();
    }

    static String formatForUrl(LdapName ldapName) {
        StringBuilder sb = new StringBuilder();
        ListIterator<Rdn> it = ldapName.getRdns().listIterator(ldapName.size());
        while (it.hasPrevious()) {
            Rdn component = it.previous();
            Attributes attributes = component.toAttributes();
            NamingEnumeration<? extends Attribute> allAttributes = attributes.getAll();
            while (allAttributes.hasMoreElements()) {
                NamingEnumeration<?> allValues;
                Attribute oneAttribute = (Attribute)allAttributes.nextElement();
                String encodedAttributeName = AbstractContextSource.nameEncodeForUrl(oneAttribute.getID());
                try {
                    allValues = oneAttribute.getAll();
                }
                catch (NamingException e) {
                    throw new UncategorizedLdapException("Unexpected error occurred formatting base URL", e);
                }
                while (allValues.hasMoreElements()) {
                    sb.append(encodedAttributeName).append('=');
                    Object oneValue = allValues.nextElement();
                    if (!(oneValue instanceof String)) {
                        throw new IllegalArgumentException("Binary attributes not supported for base URL");
                    }
                    String oneString = (String)oneValue;
                    sb.append(AbstractContextSource.nameEncodeForUrl(oneString));
                    if (!allValues.hasMoreElements()) continue;
                    sb.append('+');
                }
                if (!allAttributes.hasMoreElements()) continue;
                sb.append('+');
            }
            if (!it.hasPrevious()) continue;
            sb.append(',');
        }
        return sb.toString();
    }

    static String nameEncodeForUrl(String value) {
        try {
            String ldapEncoded = LdapEncoder.nameEncode(value);
            URI valueUri = new URI(null, null, ldapEncoded, null);
            return valueUri.toString();
        }
        catch (URISyntaxException e) {
            throw new UncategorizedLdapException("This really shouldn't happen - report this", e);
        }
    }

    public void setBase(String base) {
        this.base = base != null ? LdapUtils.newLdapName(base) : LdapUtils.emptyLdapName();
    }

    @Override
    public DistinguishedName getBaseLdapPath() {
        return new DistinguishedName(this.base);
    }

    @Override
    public LdapName getBaseLdapName() {
        return (LdapName)this.base.clone();
    }

    @Override
    public String getBaseLdapPathAsString() {
        return this.getBaseLdapName().toString();
    }

    protected DirContext createContext(Hashtable<String, Object> environment) {
        DirContext ctx = null;
        try {
            ctx = this.getDirContextInstance(environment);
            if (LOG.isInfoEnabled()) {
                Hashtable<?, ?> ctxEnv = ctx.getEnvironment();
                String ldapUrl = (String)ctxEnv.get("java.naming.provider.url");
                LOG.debug("Got Ldap context on server '" + ldapUrl + "'");
            }
            return ctx;
        }
        catch (NamingException e) {
            this.closeContext(ctx);
            throw LdapUtils.convertLdapException(e);
        }
    }

    public void setContextFactory(Class<?> contextFactory) {
        this.contextFactory = contextFactory;
    }

    public Class<?> getContextFactory() {
        return this.contextFactory;
    }

    public void setDirObjectFactory(Class<?> dirObjectFactory) {
        this.dirObjectFactory = dirObjectFactory;
    }

    public Class<?> getDirObjectFactory() {
        return this.dirObjectFactory;
    }

    public void afterPropertiesSet() {
        if (ObjectUtils.isEmpty((Object[])this.urls)) {
            throw new IllegalArgumentException("At least one server url must be set");
        }
        if (this.contextFactory == null) {
            throw new IllegalArgumentException("contextFactory must be set");
        }
        if (this.authenticationSource == null) {
            LOG.debug("AuthenticationSource not set - using default implementation");
            if (!StringUtils.hasText((String)this.userDn)) {
                LOG.info("Property 'userDn' not set - anonymous context will be used for read-write operations");
                this.anonymousReadOnly = true;
            }
            if (!this.anonymousReadOnly) {
                if (this.password == null) {
                    throw new IllegalArgumentException("Property 'password' cannot be null. To use a blank password, please ensure it is set to \"\"");
                }
                if (!StringUtils.hasText((String)this.password)) {
                    LOG.info("Property 'password' not set - blank password will be used");
                }
            }
            this.authenticationSource = new SimpleAuthenticationSource();
        }
        if (this.cacheEnvironmentProperties) {
            this.anonymousEnv = this.setupAnonymousEnv();
        }
    }

    private Hashtable<String, Object> setupAnonymousEnv() {
        if (this.pooled) {
            this.baseEnv.put(SUN_LDAP_POOLING_FLAG, "true");
            LOG.debug("Using LDAP pooling.");
        } else {
            this.baseEnv.remove(SUN_LDAP_POOLING_FLAG);
            LOG.debug("Not using LDAP pooling");
        }
        Hashtable<String, Object> env = new Hashtable<String, Object>(this.baseEnv);
        env.put("java.naming.factory.initial", this.contextFactory.getName());
        env.put("java.naming.provider.url", this.assembleProviderUrlString(this.urls));
        if (this.dirObjectFactory != null) {
            env.put("java.naming.factory.object", this.dirObjectFactory.getName());
        }
        if (StringUtils.hasText((String)this.referral)) {
            env.put("java.naming.referral", this.referral);
        }
        if (!this.base.isEmpty()) {
            env.put("org.springframework.ldap.base.path", this.base);
        }
        LOG.debug("Trying provider Urls: " + this.assembleProviderUrlString(this.urls));
        return env;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUserDn(String userDn) {
        this.userDn = userDn;
    }

    public String getUserDn() {
        return this.userDn;
    }

    public void setUrls(String[] urls) {
        this.urls = (String[])urls.clone();
    }

    public String[] getUrls() {
        return (String[])this.urls.clone();
    }

    public void setUrl(String url) {
        this.urls = new String[]{url};
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public boolean isPooled() {
        return this.pooled;
    }

    public void setBaseEnvironmentProperties(Map<String, Object> baseEnvironmentProperties) {
        this.baseEnv = new Hashtable<String, Object>(baseEnvironmentProperties);
    }

    protected Hashtable<String, Object> getAnonymousEnv() {
        if (this.cacheEnvironmentProperties) {
            return this.anonymousEnv;
        }
        return this.setupAnonymousEnv();
    }

    protected Hashtable<String, Object> getAuthenticatedEnv(String principal, String credentials) {
        Hashtable<String, Object> env = new Hashtable<String, Object>(this.getAnonymousEnv());
        this.setupAuthenticatedEnvironment(env, principal, credentials);
        return env;
    }

    public void setAuthenticationSource(AuthenticationSource authenticationSource) {
        this.authenticationSource = authenticationSource;
    }

    public AuthenticationSource getAuthenticationSource() {
        return this.authenticationSource;
    }

    public void setCacheEnvironmentProperties(boolean cacheEnvironmentProperties) {
        this.cacheEnvironmentProperties = cacheEnvironmentProperties;
    }

    public void setAnonymousReadOnly(boolean anonymousReadOnly) {
        this.anonymousReadOnly = anonymousReadOnly;
    }

    public boolean isAnonymousReadOnly() {
        return this.anonymousReadOnly;
    }

    public void setAuthenticationStrategy(DirContextAuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    protected abstract DirContext getDirContextInstance(Hashtable<String, Object> var1) throws NamingException;

    class SimpleAuthenticationSource
    implements AuthenticationSource {
        SimpleAuthenticationSource() {
        }

        @Override
        public String getPrincipal() {
            return AbstractContextSource.this.userDn;
        }

        @Override
        public String getCredentials() {
            return AbstractContextSource.this.password;
        }
    }
}

