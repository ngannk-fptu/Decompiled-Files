/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.collections.SynchronizedStack
 */
package org.apache.catalina.realm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;

public class JNDIRealm
extends RealmBase {
    protected String authentication = null;
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    protected String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    protected String derefAliases = null;
    public static final String DEREF_ALIASES = "java.naming.ldap.derefAliases";
    protected String protocol = null;
    protected boolean adCompat = false;
    protected String referrals = null;
    protected String userBase = "";
    protected String userSearch = null;
    private boolean userSearchAsUser = false;
    protected boolean userSubtree = false;
    protected String userPassword = null;
    protected String userRoleAttribute = null;
    protected String[] userPatternArray = null;
    protected String userPattern = null;
    protected String roleBase = "";
    protected String userRoleName = null;
    protected String roleName = null;
    protected String roleSearch = null;
    protected boolean roleSubtree = false;
    protected boolean roleNested = false;
    protected boolean roleSearchAsUser = false;
    protected String alternateURL;
    protected int connectionAttempt = 0;
    protected String commonRole = null;
    protected String connectionTimeout = "5000";
    protected String readTimeout = "5000";
    protected long sizeLimit = 0L;
    protected int timeLimit = 0;
    protected boolean useDelegatedCredential = true;
    protected String spnegoDelegationQop = "auth-conf";
    private boolean useStartTls = false;
    private StartTlsResponse tls = null;
    private String[] cipherSuitesArray = null;
    private HostnameVerifier hostnameVerifier = null;
    private SSLSocketFactory sslSocketFactory = null;
    private String sslSocketFactoryClassName;
    private String cipherSuites;
    private String hostNameVerifierClassName;
    private String sslProtocol;
    private boolean forceDnHexEscape = false;
    protected JNDIConnection singleConnection = new JNDIConnection();
    protected final Lock singleConnectionLock = new ReentrantLock();
    protected SynchronizedStack<JNDIConnection> connectionPool = null;
    protected int connectionPoolSize = 1;
    protected boolean useContextClassLoader = true;

    public boolean getForceDnHexEscape() {
        return this.forceDnHexEscape;
    }

    public void setForceDnHexEscape(boolean forceDnHexEscape) {
        this.forceDnHexEscape = forceDnHexEscape;
    }

    public String getAuthentication() {
        return this.authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getContextFactory() {
        return this.contextFactory;
    }

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }

    public String getDerefAliases() {
        return this.derefAliases;
    }

    public void setDerefAliases(String derefAliases) {
        this.derefAliases = derefAliases;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean getAdCompat() {
        return this.adCompat;
    }

    public void setAdCompat(boolean adCompat) {
        this.adCompat = adCompat;
    }

    public String getReferrals() {
        return this.referrals;
    }

    public void setReferrals(String referrals) {
        this.referrals = referrals;
    }

    public String getUserBase() {
        return this.userBase;
    }

    public void setUserBase(String userBase) {
        this.userBase = userBase;
    }

    public String getUserSearch() {
        return this.userSearch;
    }

    public void setUserSearch(String userSearch) {
        this.userSearch = userSearch;
        this.singleConnection = this.create();
    }

    public boolean isUserSearchAsUser() {
        return this.userSearchAsUser;
    }

    public void setUserSearchAsUser(boolean userSearchAsUser) {
        this.userSearchAsUser = userSearchAsUser;
    }

    public boolean getUserSubtree() {
        return this.userSubtree;
    }

    public void setUserSubtree(boolean userSubtree) {
        this.userSubtree = userSubtree;
    }

    public String getUserRoleName() {
        return this.userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public String getRoleBase() {
        return this.roleBase;
    }

    public void setRoleBase(String roleBase) {
        this.roleBase = roleBase;
        this.singleConnection = this.create();
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleSearch() {
        return this.roleSearch;
    }

    public void setRoleSearch(String roleSearch) {
        this.roleSearch = roleSearch;
        this.singleConnection = this.create();
    }

    public boolean isRoleSearchAsUser() {
        return this.roleSearchAsUser;
    }

    public void setRoleSearchAsUser(boolean roleSearchAsUser) {
        this.roleSearchAsUser = roleSearchAsUser;
    }

    public boolean getRoleSubtree() {
        return this.roleSubtree;
    }

    public void setRoleSubtree(boolean roleSubtree) {
        this.roleSubtree = roleSubtree;
    }

    public boolean getRoleNested() {
        return this.roleNested;
    }

    public void setRoleNested(boolean roleNested) {
        this.roleNested = roleNested;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRoleAttribute() {
        return this.userRoleAttribute;
    }

    public void setUserRoleAttribute(String userRoleAttribute) {
        this.userRoleAttribute = userRoleAttribute;
    }

    public String getUserPattern() {
        return this.userPattern;
    }

    public void setUserPattern(String userPattern) {
        this.userPattern = userPattern;
        if (userPattern == null) {
            this.userPatternArray = null;
        } else {
            this.userPatternArray = this.parseUserPatternString(userPattern);
            this.singleConnection = this.create();
        }
    }

    public String getAlternateURL() {
        return this.alternateURL;
    }

    public void setAlternateURL(String alternateURL) {
        this.alternateURL = alternateURL;
    }

    public String getCommonRole() {
        return this.commonRole;
    }

    public void setCommonRole(String commonRole) {
        this.commonRole = commonRole;
    }

    public String getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(String timeout) {
        this.connectionTimeout = timeout;
    }

    public String getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(String timeout) {
        this.readTimeout = timeout;
    }

    public long getSizeLimit() {
        return this.sizeLimit;
    }

    public void setSizeLimit(long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean isUseDelegatedCredential() {
        return this.useDelegatedCredential;
    }

    public void setUseDelegatedCredential(boolean useDelegatedCredential) {
        this.useDelegatedCredential = useDelegatedCredential;
    }

    public String getSpnegoDelegationQop() {
        return this.spnegoDelegationQop;
    }

    public void setSpnegoDelegationQop(String spnegoDelegationQop) {
        this.spnegoDelegationQop = spnegoDelegationQop;
    }

    public boolean getUseStartTls() {
        return this.useStartTls;
    }

    public void setUseStartTls(boolean useStartTls) {
        this.useStartTls = useStartTls;
    }

    private String[] getCipherSuitesArray() {
        if (this.cipherSuites == null || this.cipherSuitesArray != null) {
            return this.cipherSuitesArray;
        }
        if (this.cipherSuites.trim().isEmpty()) {
            this.containerLog.warn((Object)sm.getString("jndiRealm.emptyCipherSuites"));
            this.cipherSuitesArray = null;
        } else {
            this.cipherSuitesArray = this.cipherSuites.trim().split("\\s*,\\s*");
            this.containerLog.debug((Object)sm.getString("jndiRealm.cipherSuites", new Object[]{Arrays.toString(this.cipherSuitesArray)}));
        }
        return this.cipherSuitesArray;
    }

    public void setCipherSuites(String suites) {
        this.cipherSuites = suites;
    }

    public int getConnectionPoolSize() {
        return this.connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public String getHostnameVerifierClassName() {
        if (this.hostnameVerifier == null) {
            return "";
        }
        return this.hostnameVerifier.getClass().getCanonicalName();
    }

    public void setHostnameVerifierClassName(String verifierClassName) {
        this.hostNameVerifierClassName = verifierClassName != null ? verifierClassName.trim() : null;
    }

    public HostnameVerifier getHostnameVerifier() {
        if (this.hostnameVerifier != null) {
            return this.hostnameVerifier;
        }
        if (this.hostNameVerifierClassName == null || this.hostNameVerifierClassName.equals("")) {
            return null;
        }
        try {
            Object o = this.constructInstance(this.hostNameVerifierClassName);
            if (o instanceof HostnameVerifier) {
                this.hostnameVerifier = (HostnameVerifier)o;
                return this.hostnameVerifier;
            }
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidHostnameVerifier", new Object[]{this.hostNameVerifierClassName}));
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidHostnameVerifier", new Object[]{this.hostNameVerifierClassName}), e);
        }
    }

    public void setSslSocketFactoryClassName(String factoryClassName) {
        this.sslSocketFactoryClassName = factoryClassName;
    }

    public void setSslProtocol(String protocol) {
        this.sslProtocol = protocol;
    }

    private String[] getSupportedSslProtocols() {
        try {
            SSLContext sslContext = SSLContext.getDefault();
            return sslContext.getSupportedSSLParameters().getProtocols();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(sm.getString("jndiRealm.exception"), e);
        }
    }

    private Object constructInstance(String className) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(className);
        return clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    public void setUseContextClassLoader(boolean useContext) {
        this.useContextClassLoader = useContext;
    }

    public boolean isUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(String username, String credentials) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        JNDIConnection connection = null;
        Principal principal = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            connection = this.get();
            try {
                principal = this.authenticate(connection, username, credentials);
            }
            catch (NullPointerException | NamingException e) {
                this.containerLog.info((Object)sm.getString("jndiRealm.exception.retry"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                connection = this.get();
                principal = this.authenticate(connection, username, credentials);
            }
            this.release(connection);
            Principal e = principal;
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return e;
        }
        catch (Exception e) {
            try {
                this.containerLog.error((Object)sm.getString("jndiRealm.exception"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug((Object)"Returning null principal.");
                }
                Principal principal2 = null;
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                return principal2;
            }
            catch (Throwable throwable) {
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                throw throwable;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Principal authenticate(JNDIConnection connection, String username, String credentials) throws NamingException {
        if (username == null || username.equals("") || credentials == null || credentials.equals("")) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"username null or empty: returning null principal.");
            }
            return null;
        }
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            if (this.userPatternArray != null) {
            } else {
                User user;
                block23: {
                    user = this.getUser(connection, username, credentials);
                    if (user == null) {
                        Principal user2 = null;
                        if (currentThread != null) {
                            currentThread.setContextClassLoader(ocl);
                        }
                        return user2;
                    }
                    if (this.checkCredentials(connection.context, user, credentials)) break block23;
                    Principal user2 = null;
                    if (currentThread != null) {
                        currentThread.setContextClassLoader(ocl);
                    }
                    return user2;
                }
                List<String> roles = this.getRoles(connection, user);
                if (this.containerLog.isDebugEnabled()) {
                    this.containerLog.debug((Object)("Found roles: " + (roles == null ? "" : roles.toString())));
                }
                GenericPrincipal genericPrincipal = new GenericPrincipal(username, credentials, roles);
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                return genericPrincipal;
            }
            for (int curUserPattern = 0; curUserPattern < this.userPatternArray.length; ++curUserPattern) {
                User user2 = this.getUser(connection, username, credentials, curUserPattern);
                if (user2 == null) continue;
                try {
                    if (!this.checkCredentials(connection.context, user2, credentials)) continue;
                    List<String> roles = this.getRoles(connection, user2);
                    if (this.containerLog.isDebugEnabled()) {
                        this.containerLog.debug((Object)("Found roles: " + (roles == null ? "" : roles.toString())));
                    }
                    GenericPrincipal genericPrincipal = new GenericPrincipal(username, credentials, roles);
                    if (currentThread != null) {
                        currentThread.setContextClassLoader(ocl);
                    }
                    return genericPrincipal;
                }
                catch (InvalidNameException ine) {
                    this.containerLog.warn((Object)sm.getString("jndiRealm.exception"), (Throwable)ine);
                }
            }
            Principal curUserPattern = null;
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return curUserPattern;
        }
        catch (Throwable throwable) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(String username) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            Principal principal = super.authenticate(username);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return principal;
        }
        catch (Throwable throwable) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2, String algorithm) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            Principal principal = super.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realm, digestA2, algorithm);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return principal;
        }
        catch (Throwable throwable) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(X509Certificate[] certs) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            Principal principal = super.authenticate(certs);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return principal;
        }
        catch (Throwable throwable) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(GSSContext gssContext, boolean storeCred) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            Principal principal = super.authenticate(gssContext, storeCred);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return principal;
        }
        catch (Throwable throwable) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        ClassLoader ocl = null;
        Thread currentThread = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            Principal principal = super.authenticate(gssName, gssCredential);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            return principal;
        }
        catch (Throwable throwable) {
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
            throw throwable;
        }
    }

    protected User getUser(JNDIConnection connection, String username) throws NamingException {
        return this.getUser(connection, username, null, -1);
    }

    protected User getUser(JNDIConnection connection, String username, String credentials) throws NamingException {
        return this.getUser(connection, username, credentials, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected User getUser(JNDIConnection connection, String username, String credentials, int curUserPattern) throws NamingException {
        User user = null;
        ArrayList<String> list = new ArrayList<String>();
        if (this.userPassword != null) {
            list.add(this.userPassword);
        }
        if (this.userRoleName != null) {
            list.add(this.userRoleName);
        }
        if (this.userRoleAttribute != null) {
            list.add(this.userRoleAttribute);
        }
        String[] attrIds = list.toArray(new String[0]);
        if (this.userPatternArray != null && curUserPattern >= 0) {
            user = this.getUserByPattern(connection, username, credentials, attrIds, curUserPattern);
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)("Found user by pattern [" + user + "]"));
            }
        } else {
            boolean thisUserSearchAsUser = this.isUserSearchAsUser();
            try {
                if (thisUserSearchAsUser) {
                    this.userCredentialsAdd(connection.context, username, credentials);
                }
                user = this.getUserBySearch(connection, username, attrIds);
            }
            finally {
                if (thisUserSearchAsUser) {
                    this.userCredentialsRemove(connection.context);
                }
            }
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)("Found user by search [" + user + "]"));
            }
        }
        if (this.userPassword == null && credentials != null && user != null) {
            return new User(user.getUserName(), user.getDN(), credentials, user.getRoles(), user.getUserRoleId());
        }
        return user;
    }

    protected User getUserByPattern(DirContext context, String username, String[] attrIds, String dn) throws NamingException {
        if (attrIds == null || attrIds.length == 0) {
            return new User(username, dn, null, null, null);
        }
        Attributes attrs = null;
        try {
            attrs = context.getAttributes(dn, attrIds);
        }
        catch (NameNotFoundException e) {
            return null;
        }
        if (attrs == null) {
            return null;
        }
        String password = null;
        if (this.userPassword != null) {
            password = this.getAttributeValue(this.userPassword, attrs);
        }
        String userRoleAttrValue = null;
        if (this.userRoleAttribute != null) {
            userRoleAttrValue = this.getAttributeValue(this.userRoleAttribute, attrs);
        }
        ArrayList<String> roles = null;
        if (this.userRoleName != null) {
            roles = this.addAttributeValues(this.userRoleName, attrs, roles);
        }
        return new User(username, dn, password, roles, userRoleAttrValue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected User getUserByPattern(JNDIConnection connection, String username, String credentials, String[] attrIds, int curUserPattern) throws NamingException {
        User user = null;
        if (username == null || this.userPatternArray[curUserPattern] == null) {
            return null;
        }
        String dn = connection.userPatternFormatArray[curUserPattern].format(new String[]{this.doAttributeValueEscaping(username)});
        try {
            user = this.getUserByPattern(connection.context, username, attrIds, dn);
        }
        catch (NameNotFoundException e) {
            return null;
        }
        catch (NamingException e) {
            try {
                this.userCredentialsAdd(connection.context, dn, credentials);
                user = this.getUserByPattern(connection.context, username, attrIds, dn);
            }
            finally {
                this.userCredentialsRemove(connection.context);
            }
        }
        return user;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected User getUserBySearch(JNDIConnection connection, String username, String[] attrIds) throws NamingException {
        Attributes attrs;
        SearchResult result;
        if (username == null) return null;
        if (connection.userSearchFormat == null) {
            return null;
        }
        String filter = connection.userSearchFormat.format(new String[]{this.doFilterEscaping(username)});
        SearchControls constraints = new SearchControls();
        if (this.userSubtree) {
            constraints.setSearchScope(2);
        } else {
            constraints.setSearchScope(1);
        }
        constraints.setCountLimit(this.sizeLimit);
        constraints.setTimeLimit(this.timeLimit);
        if (attrIds == null) {
            attrIds = new String[]{};
        }
        constraints.setReturningAttributes(attrIds);
        NamingEnumeration<SearchResult> results = connection.context.search(this.userBase, filter, constraints);
        try {
            if (results == null || !results.hasMore()) {
                User user = null;
                return user;
            }
            result = results.next();
            try {
                if (results.hasMore()) {
                    if (this.containerLog.isInfoEnabled()) {
                        this.containerLog.info((Object)sm.getString("jndiRealm.multipleEntries", new Object[]{username}));
                    }
                    User user = null;
                    return user;
                }
            }
            catch (PartialResultException ex) {
                if (!this.adCompat) {
                    throw ex;
                }
            }
        }
        catch (PartialResultException ex) {
            if (!this.adCompat) {
                throw ex;
            }
            User user = null;
            return user;
        }
        String dn = this.getDistinguishedName(connection.context, this.userBase, result);
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  entry found for " + username + " with dn " + dn));
        }
        if ((attrs = result.getAttributes()) == null) {
            return null;
        }
        String password = null;
        if (this.userPassword != null) {
            password = this.getAttributeValue(this.userPassword, attrs);
        }
        String userRoleAttrValue = null;
        if (this.userRoleAttribute != null) {
            userRoleAttrValue = this.getAttributeValue(this.userRoleAttribute, attrs);
        }
        ArrayList<String> roles = null;
        if (this.userRoleName == null) return new User(username, dn, password, roles, userRoleAttrValue);
        roles = this.addAttributeValues(this.userRoleName, attrs, roles);
        return new User(username, dn, password, roles, userRoleAttrValue);
        finally {
            if (results != null) {
                results.close();
            }
        }
    }

    protected boolean checkCredentials(DirContext context, User user, String credentials) throws NamingException {
        boolean validated = false;
        validated = this.userPassword == null ? this.bindAsUser(context, user, credentials) : this.compareCredentials(context, user, credentials);
        if (this.containerLog.isTraceEnabled()) {
            if (validated) {
                this.containerLog.trace((Object)sm.getString("jndiRealm.authenticateSuccess", new Object[]{user.getUserName()}));
            } else {
                this.containerLog.trace((Object)sm.getString("jndiRealm.authenticateFailure", new Object[]{user.getUserName()}));
            }
        }
        return validated;
    }

    protected boolean compareCredentials(DirContext context, User info, String credentials) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)"  validating credentials");
        }
        if (info == null || credentials == null) {
            return false;
        }
        String password = info.getPassword();
        return this.getCredentialHandler().matches(credentials, password);
    }

    protected boolean bindAsUser(DirContext context, User user, String credentials) throws NamingException {
        boolean validated;
        block6: {
            if (credentials == null || user == null) {
                return false;
            }
            String dn = user.getDN();
            if (dn == null) {
                return false;
            }
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)"  validating credentials by binding as the user");
            }
            this.userCredentialsAdd(context, dn, credentials);
            validated = false;
            try {
                if (this.containerLog.isTraceEnabled()) {
                    this.containerLog.trace((Object)("  binding as " + dn));
                }
                context.getAttributes("", null);
                validated = true;
            }
            catch (AuthenticationException e) {
                if (!this.containerLog.isTraceEnabled()) break block6;
                this.containerLog.trace((Object)"  bind attempt failed");
            }
        }
        this.userCredentialsRemove(context);
        return validated;
    }

    private void userCredentialsAdd(DirContext context, String dn, String credentials) throws NamingException {
        context.addToEnvironment("java.naming.security.principal", dn);
        context.addToEnvironment("java.naming.security.credentials", credentials);
    }

    private void userCredentialsRemove(DirContext context) throws NamingException {
        if (this.connectionName != null) {
            context.addToEnvironment("java.naming.security.principal", this.connectionName);
        } else {
            context.removeFromEnvironment("java.naming.security.principal");
        }
        if (this.connectionPassword != null) {
            context.addToEnvironment("java.naming.security.credentials", this.connectionPassword);
        } else {
            context.removeFromEnvironment("java.naming.security.credentials");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected List<String> getRoles(JNDIConnection connection, User user) throws NamingException {
        if (user == null) {
            return null;
        }
        String dn = user.getDN();
        String username = user.getUserName();
        String userRoleId = user.getUserRoleId();
        if (dn == null || username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  getRoles(" + dn + ")"));
        }
        ArrayList<String> list = new ArrayList<String>();
        List<String> userRoles = user.getRoles();
        if (userRoles != null) {
            list.addAll(userRoles);
        }
        if (this.commonRole != null) {
            list.add(this.commonRole);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  Found " + list.size() + " user internal roles"));
            this.containerLog.trace((Object)("  Found user internal roles " + ((Object)list).toString()));
        }
        if (connection.roleFormat == null || this.roleName == null) {
            return list;
        }
        String filter = connection.roleFormat.format(new String[]{this.doFilterEscaping(dn), this.doFilterEscaping(this.doAttributeValueEscaping(username)), this.doFilterEscaping(this.doAttributeValueEscaping(userRoleId))});
        SearchControls controls = new SearchControls();
        if (this.roleSubtree) {
            controls.setSearchScope(2);
        } else {
            controls.setSearchScope(1);
        }
        controls.setReturningAttributes(new String[]{this.roleName});
        String base = null;
        if (connection.roleBaseFormat != null) {
            NameParser np = connection.context.getNameParser("");
            Name name = np.parse(dn);
            String[] nameParts = new String[name.size()];
            for (int i = 0; i < name.size(); ++i) {
                nameParts[i] = JNDIRealm.convertToHexEscape(name.get(i));
            }
            base = connection.roleBaseFormat.format(nameParts);
        } else {
            base = "";
        }
        NamingEnumeration<SearchResult> results = this.searchAsUser(connection.context, user, base, filter, controls, this.isRoleSearchAsUser());
        if (results == null) {
            return list;
        }
        HashMap<String, String> groupMap = new HashMap<String, String>();
        try {
            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                if (attrs == null) continue;
                String dname = this.getDistinguishedName(connection.context, base, result);
                String name = this.getAttributeValue(this.roleName, attrs);
                if (name == null || dname == null) continue;
                groupMap.put(dname, name);
            }
        }
        catch (PartialResultException ex) {
            if (!this.adCompat) {
                throw ex;
            }
        }
        finally {
            results.close();
        }
        if (this.containerLog.isTraceEnabled()) {
            Set entries = groupMap.entrySet();
            this.containerLog.trace((Object)("  Found " + entries.size() + " direct roles"));
            for (Map.Entry entry : entries) {
                this.containerLog.trace((Object)("  Found direct role " + (String)entry.getKey() + " -> " + (String)entry.getValue()));
            }
        }
        if (this.getRoleNested()) {
            HashMap newGroups = new HashMap(groupMap);
            while (!newGroups.isEmpty()) {
                HashMap<String, String> newThisRound = new HashMap<String, String>();
                for (Map.Entry group : newGroups.entrySet()) {
                    filter = connection.roleFormat.format(new String[]{this.doFilterEscaping((String)group.getKey()), this.doFilterEscaping(this.doAttributeValueEscaping((String)group.getValue())), this.doFilterEscaping(this.doAttributeValueEscaping((String)group.getValue()))});
                    if (this.containerLog.isTraceEnabled()) {
                        this.containerLog.trace((Object)("Perform a nested group search with base " + this.roleBase + " and filter " + filter));
                    }
                    results = this.searchAsUser(connection.context, user, base, filter, controls, this.isRoleSearchAsUser());
                    try {
                        while (results.hasMore()) {
                            SearchResult result = results.next();
                            Attributes attrs = result.getAttributes();
                            if (attrs == null) continue;
                            String dname = this.getDistinguishedName(connection.context, this.roleBase, result);
                            String name = this.getAttributeValue(this.roleName, attrs);
                            if (name == null || dname == null || groupMap.keySet().contains(dname)) continue;
                            groupMap.put(dname, name);
                            newThisRound.put(dname, name);
                            if (!this.containerLog.isTraceEnabled()) continue;
                            this.containerLog.trace((Object)("  Found nested role " + dname + " -> " + name));
                        }
                    }
                    catch (PartialResultException ex) {
                        if (this.adCompat) continue;
                        throw ex;
                    }
                    finally {
                        results.close();
                    }
                }
                newGroups = newThisRound;
            }
        }
        list.addAll(groupMap.values());
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private NamingEnumeration<SearchResult> searchAsUser(DirContext context, User user, String base, String filter, SearchControls controls, boolean searchAsUser) throws NamingException {
        NamingEnumeration<SearchResult> results;
        try {
            if (searchAsUser) {
                this.userCredentialsAdd(context, user.getDN(), user.getPassword());
            }
            results = context.search(base, filter, controls);
        }
        finally {
            if (searchAsUser) {
                this.userCredentialsRemove(context);
            }
        }
        return results;
    }

    private String getAttributeValue(String attrId, Attributes attrs) throws NamingException {
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  retrieving attribute " + attrId));
        }
        if (attrId == null || attrs == null) {
            return null;
        }
        Attribute attr = attrs.get(attrId);
        if (attr == null) {
            return null;
        }
        Object value = attr.get();
        if (value == null) {
            return null;
        }
        String valueString = null;
        valueString = value instanceof byte[] ? new String((byte[])value) : value.toString();
        return valueString;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ArrayList<String> addAttributeValues(String attrId, Attributes attrs, ArrayList<String> values) throws NamingException {
        Attribute attr;
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)("  retrieving values for attribute " + attrId));
        }
        if (attrId == null || attrs == null) {
            return values;
        }
        if (values == null) {
            values = new ArrayList();
        }
        if ((attr = attrs.get(attrId)) == null) {
            return values;
        }
        try (NamingEnumeration<?> e = attr.getAll();){
            while (e.hasMore()) {
                String value = (String)e.next();
                values.add(value);
            }
        }
        return values;
    }

    protected void close(JNDIConnection connection) {
        if (connection == null || connection.context == null) {
            if (this.connectionPool == null) {
                this.singleConnectionLock.unlock();
            }
            return;
        }
        if (this.tls != null) {
            try {
                this.tls.close();
            }
            catch (IOException e) {
                this.containerLog.error((Object)sm.getString("jndiRealm.tlsClose"), (Throwable)e);
            }
        }
        try {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug((Object)"Closing directory context");
            }
            connection.context.close();
        }
        catch (NamingException e) {
            this.containerLog.error((Object)sm.getString("jndiRealm.close"), (Throwable)e);
        }
        connection.context = null;
        if (this.connectionPool == null) {
            this.singleConnectionLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closePooledConnections() {
        if (this.connectionPool != null) {
            SynchronizedStack<JNDIConnection> synchronizedStack = this.connectionPool;
            synchronized (synchronizedStack) {
                JNDIConnection connection = null;
                while ((connection = (JNDIConnection)this.connectionPool.pop()) != null) {
                    this.close(connection);
                }
            }
        }
    }

    @Override
    protected String getPassword(String username) {
        String userPassword = this.getUserPassword();
        if (userPassword == null || userPassword.isEmpty()) {
            return null;
        }
        JNDIConnection connection = null;
        User user = null;
        try {
            connection = this.get();
            try {
                user = this.getUser(connection, username, null);
            }
            catch (NullPointerException | NamingException e) {
                this.containerLog.info((Object)sm.getString("jndiRealm.exception.retry"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                connection = this.get();
                user = this.getUser(connection, username, null);
            }
            this.release(connection);
            if (user == null) {
                return null;
            }
            return user.getPassword();
        }
        catch (Exception e) {
            this.containerLog.error((Object)sm.getString("jndiRealm.exception"), (Throwable)e);
            this.close(connection);
            this.closePooledConnections();
            return null;
        }
    }

    @Override
    protected Principal getPrincipal(String username) {
        return this.getPrincipal(username, null);
    }

    @Override
    protected Principal getPrincipal(GSSName gssName, GSSCredential gssCredential) {
        int i;
        String name = gssName.toString();
        if (this.isStripRealmForGss() && (i = name.indexOf(64)) > 0) {
            name = name.substring(0, i);
        }
        return this.getPrincipal(name, gssCredential);
    }

    @Override
    protected Principal getPrincipal(String username, GSSCredential gssCredential) {
        JNDIConnection connection = null;
        Principal principal = null;
        try {
            connection = this.get();
            try {
                principal = this.getPrincipal(connection, username, gssCredential);
            }
            catch (NamingException e) {
                this.containerLog.info((Object)sm.getString("jndiRealm.exception.retry"), (Throwable)e);
                this.close(connection);
                this.closePooledConnections();
                connection = this.get();
                principal = this.getPrincipal(connection, username, gssCredential);
            }
            this.release(connection);
            return principal;
        }
        catch (Exception e) {
            this.containerLog.error((Object)sm.getString("jndiRealm.exception"), (Throwable)e);
            this.close(connection);
            this.closePooledConnections();
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Principal getPrincipal(JNDIConnection connection, String username, GSSCredential gssCredential) throws NamingException {
        User user = null;
        List<String> roles = null;
        Hashtable<?, ?> preservedEnvironment = null;
        DirContext context = connection.context;
        try {
            if (gssCredential != null && this.isUseDelegatedCredential()) {
                preservedEnvironment = context.getEnvironment();
                context.addToEnvironment("java.naming.security.authentication", "GSSAPI");
                context.addToEnvironment("javax.security.sasl.server.authentication", "true");
                context.addToEnvironment("javax.security.sasl.qop", this.spnegoDelegationQop);
            }
            if ((user = this.getUser(connection, username)) != null) {
                roles = this.getRoles(connection, user);
            }
        }
        finally {
            if (gssCredential != null && this.isUseDelegatedCredential()) {
                this.restoreEnvironmentParameter(context, "java.naming.security.authentication", preservedEnvironment);
                this.restoreEnvironmentParameter(context, "javax.security.sasl.server.authentication", preservedEnvironment);
                this.restoreEnvironmentParameter(context, "javax.security.sasl.qop", preservedEnvironment);
            }
        }
        if (user != null) {
            return new GenericPrincipal(user.getUserName(), user.getPassword(), roles, null, null, gssCredential, null);
        }
        return null;
    }

    private void restoreEnvironmentParameter(DirContext context, String parameterName, Hashtable<?, ?> preservedEnvironment) {
        try {
            context.removeFromEnvironment(parameterName);
            if (preservedEnvironment != null && preservedEnvironment.containsKey(parameterName)) {
                context.addToEnvironment(parameterName, preservedEnvironment.get(parameterName));
            }
        }
        catch (NamingException namingException) {
            // empty catch block
        }
    }

    protected JNDIConnection get() throws NamingException {
        JNDIConnection connection = null;
        if (this.connectionPool != null) {
            connection = (JNDIConnection)this.connectionPool.pop();
            if (connection == null) {
                connection = this.create();
            }
        } else {
            this.singleConnectionLock.lock();
            connection = this.singleConnection;
        }
        if (connection.context == null) {
            this.open(connection);
        }
        return connection;
    }

    protected void release(JNDIConnection connection) {
        if (this.connectionPool != null) {
            if (connection != null && !this.connectionPool.push((Object)connection)) {
                this.close(connection);
            }
        } else {
            this.singleConnectionLock.unlock();
        }
    }

    protected JNDIConnection create() {
        JNDIConnection connection = new JNDIConnection();
        if (this.userSearch != null) {
            connection.userSearchFormat = new MessageFormat(this.userSearch);
        }
        if (this.userPattern != null) {
            int len = this.userPatternArray.length;
            connection.userPatternFormatArray = new MessageFormat[len];
            for (int i = 0; i < len; ++i) {
                connection.userPatternFormatArray[i] = new MessageFormat(this.userPatternArray[i]);
            }
        }
        if (this.roleBase != null) {
            connection.roleBaseFormat = new MessageFormat(this.roleBase);
        }
        if (this.roleSearch != null) {
            connection.roleFormat = new MessageFormat(this.roleSearch);
        }
        return connection;
    }

    protected void open(JNDIConnection connection) throws NamingException {
        try {
            connection.context = this.createDirContext(this.getDirectoryContextEnvironment());
        }
        catch (Exception e) {
            if (this.alternateURL == null || this.alternateURL.length() == 0) {
                throw e;
            }
            this.connectionAttempt = 1;
            this.containerLog.info((Object)sm.getString("jndiRealm.exception.retry"), (Throwable)e);
            connection.context = this.createDirContext(this.getDirectoryContextEnvironment());
        }
        finally {
            this.connectionAttempt = 0;
        }
    }

    @Override
    public boolean isAvailable() {
        return this.connectionPool != null || this.singleConnection.context != null;
    }

    private DirContext createDirContext(Hashtable<String, String> env) throws NamingException {
        if (this.useStartTls) {
            return this.createTlsDirContext(env);
        }
        return new InitialDirContext(env);
    }

    private SSLSocketFactory getSSLSocketFactory() {
        if (this.sslSocketFactory != null) {
            return this.sslSocketFactory;
        }
        SSLSocketFactory result = this.sslSocketFactoryClassName != null && !this.sslSocketFactoryClassName.trim().equals("") ? this.createSSLSocketFactoryFromClassName(this.sslSocketFactoryClassName) : this.createSSLContextFactoryFromProtocol(this.sslProtocol);
        this.sslSocketFactory = result;
        return result;
    }

    private SSLSocketFactory createSSLSocketFactoryFromClassName(String className) {
        try {
            Object o = this.constructInstance(className);
            if (o instanceof SSLSocketFactory) {
                return this.sslSocketFactory;
            }
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslSocketFactory", new Object[]{className}));
        }
        catch (ReflectiveOperationException | SecurityException e) {
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslSocketFactory", new Object[]{className}), e);
        }
    }

    private SSLSocketFactory createSSLContextFactoryFromProtocol(String protocol) {
        try {
            SSLContext sslContext;
            if (protocol != null) {
                sslContext = SSLContext.getInstance(protocol);
                sslContext.init(null, null, null);
            } else {
                sslContext = SSLContext.getDefault();
            }
            return sslContext.getSocketFactory();
        }
        catch (KeyManagementException | NoSuchAlgorithmException e) {
            List<String> allowedProtocols = Arrays.asList(this.getSupportedSslProtocols());
            throw new IllegalArgumentException(sm.getString("jndiRealm.invalidSslProtocol", new Object[]{protocol, allowedProtocols}), e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private DirContext createTlsDirContext(Hashtable<String, String> env) throws NamingException {
        HashMap<String, String> savedEnv = new HashMap<String, String>();
        for (String key : Arrays.asList("java.naming.security.authentication", "java.naming.security.credentials", "java.naming.security.principal", "java.naming.security.protocol")) {
            String entry = env.remove(key);
            if (entry == null) continue;
            savedEnv.put(key, entry);
        }
        InitialLdapContext result = null;
        try {
            result = new InitialLdapContext(env, null);
            this.tls = (StartTlsResponse)result.extendedOperation(new StartTlsRequest());
            if (this.getHostnameVerifier() != null) {
                this.tls.setHostnameVerifier(this.getHostnameVerifier());
            }
            if (this.getCipherSuitesArray() != null) {
                this.tls.setEnabledCipherSuites(this.getCipherSuitesArray());
            }
            try {
                SSLSession negotiate = this.tls.negotiate(this.getSSLSocketFactory());
                this.containerLog.debug((Object)sm.getString("jndiRealm.negotiatedTls", new Object[]{negotiate.getProtocol()}));
            }
            catch (IOException e) {
                throw new NamingException(e.getMessage());
            }
        }
        finally {
            if (result != null) {
                for (Map.Entry savedEntry : savedEnv.entrySet()) {
                    result.addToEnvironment((String)savedEntry.getKey(), savedEntry.getValue());
                }
            }
        }
        return result;
    }

    protected Hashtable<String, String> getDirectoryContextEnvironment() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        if (this.containerLog.isDebugEnabled() && this.connectionAttempt == 0) {
            this.containerLog.debug((Object)("Connecting to URL " + this.connectionURL));
        } else if (this.containerLog.isDebugEnabled() && this.connectionAttempt > 0) {
            this.containerLog.debug((Object)("Connecting to URL " + this.alternateURL));
        }
        env.put("java.naming.factory.initial", this.contextFactory);
        if (this.connectionName != null) {
            env.put("java.naming.security.principal", this.connectionName);
        }
        if (this.connectionPassword != null) {
            env.put("java.naming.security.credentials", this.connectionPassword);
        }
        if (this.connectionURL != null && this.connectionAttempt == 0) {
            env.put("java.naming.provider.url", this.connectionURL);
        } else if (this.alternateURL != null && this.connectionAttempt > 0) {
            env.put("java.naming.provider.url", this.alternateURL);
        }
        if (this.authentication != null) {
            env.put("java.naming.security.authentication", this.authentication);
        }
        if (this.protocol != null) {
            env.put("java.naming.security.protocol", this.protocol);
        }
        if (this.referrals != null) {
            env.put("java.naming.referral", this.referrals);
        }
        if (this.derefAliases != null) {
            env.put(DEREF_ALIASES, this.derefAliases);
        }
        if (this.connectionTimeout != null) {
            env.put("com.sun.jndi.ldap.connect.timeout", this.connectionTimeout);
        }
        if (this.readTimeout != null) {
            env.put("com.sun.jndi.ldap.read.timeout", this.readTimeout);
        }
        return env;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void startInternal() throws LifecycleException {
        if (this.connectionPoolSize != 1) {
            this.connectionPool = new SynchronizedStack(128, this.connectionPoolSize);
        }
        ClassLoader ocl = null;
        Thread currentThread = null;
        JNDIConnection connection = null;
        try {
            if (!this.isUseContextClassLoader()) {
                currentThread = Thread.currentThread();
                ocl = currentThread.getContextClassLoader();
                currentThread.setContextClassLoader(this.getClass().getClassLoader());
            }
            connection = this.get();
            this.release(connection);
            if (currentThread != null) {
                currentThread.setContextClassLoader(ocl);
            }
        }
        catch (NamingException e) {
            try {
                this.containerLog.error((Object)sm.getString("jndiRealm.open"), (Throwable)e);
                this.release(connection);
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
            }
            catch (Throwable throwable) {
                this.release(connection);
                if (currentThread != null) {
                    currentThread.setContextClassLoader(ocl);
                }
                throw throwable;
            }
        }
        super.startInternal();
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (this.connectionPool == null) {
            this.singleConnectionLock.lock();
            this.close(this.singleConnection);
        } else {
            this.closePooledConnections();
            this.connectionPool = null;
        }
    }

    protected String[] parseUserPatternString(String userPatternString) {
        if (userPatternString != null) {
            ArrayList<String> pathList = new ArrayList<String>();
            int startParenLoc = userPatternString.indexOf(40);
            if (startParenLoc == -1) {
                return new String[]{userPatternString};
            }
            int startingPoint = 0;
            while (startParenLoc > -1) {
                int endParenLoc = 0;
                while (userPatternString.charAt(startParenLoc + 1) == '|' || startParenLoc != 0 && userPatternString.charAt(startParenLoc - 1) == '\\') {
                    startParenLoc = userPatternString.indexOf(40, startParenLoc + 1);
                }
                endParenLoc = userPatternString.indexOf(41, startParenLoc + 1);
                while (userPatternString.charAt(endParenLoc - 1) == '\\') {
                    endParenLoc = userPatternString.indexOf(41, endParenLoc + 1);
                }
                String nextPathPart = userPatternString.substring(startParenLoc + 1, endParenLoc);
                pathList.add(nextPathPart);
                startingPoint = endParenLoc + 1;
                startParenLoc = userPatternString.indexOf(40, startingPoint);
            }
            return pathList.toArray(new String[0]);
        }
        return null;
    }

    @Deprecated
    protected String doRFC2254Encoding(String inString) {
        return this.doFilterEscaping(inString);
    }

    protected String doFilterEscaping(String inString) {
        if (inString == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(inString.length());
        block7: for (int i = 0; i < inString.length(); ++i) {
            char c = inString.charAt(i);
            switch (c) {
                case '\\': {
                    buf.append("\\5c");
                    continue block7;
                }
                case '*': {
                    buf.append("\\2a");
                    continue block7;
                }
                case '(': {
                    buf.append("\\28");
                    continue block7;
                }
                case ')': {
                    buf.append("\\29");
                    continue block7;
                }
                case '\u0000': {
                    buf.append("\\00");
                    continue block7;
                }
                default: {
                    buf.append(c);
                }
            }
        }
        return buf.toString();
    }

    protected String getDistinguishedName(DirContext context, String base, SearchResult result) throws NamingException {
        Name name;
        String resultName = result.getName();
        if (result.isRelative()) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)("  search returned relative name: " + resultName));
            }
            NameParser parser = context.getNameParser("");
            Name contextName = parser.parse(context.getNameInNamespace());
            Name baseName = parser.parse(base);
            Name entryName = parser.parse(new CompositeName(resultName).get(0));
            name = contextName.addAll(baseName);
            name = name.addAll(entryName);
        } else {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)("  search returned absolute name: " + resultName));
            }
            try {
                NameParser parser = context.getNameParser("");
                URI userNameUri = new URI(resultName);
                String pathComponent = userNameUri.getPath();
                if (pathComponent.length() < 1) {
                    throw new InvalidNameException(sm.getString("jndiRealm.invalidName", new Object[]{resultName}));
                }
                name = parser.parse(pathComponent.substring(1));
            }
            catch (URISyntaxException e) {
                throw new InvalidNameException(sm.getString("jndiRealm.invalidName", new Object[]{resultName}));
            }
        }
        if (this.getForceDnHexEscape()) {
            return JNDIRealm.convertToHexEscape(name.toString());
        }
        return name.toString();
    }

    protected String doAttributeValueEscaping(String input) {
        if (input == null) {
            return null;
        }
        int len = input.length();
        StringBuilder result = new StringBuilder();
        block12: for (int i = 0; i < len; ++i) {
            char c = input.charAt(i);
            switch (c) {
                case ' ': {
                    if (i == 0 || i == len - 1) {
                        result.append("\\20");
                        continue block12;
                    }
                    result.append(c);
                    continue block12;
                }
                case '#': {
                    if (i == 0) {
                        result.append("\\23");
                        continue block12;
                    }
                    result.append(c);
                    continue block12;
                }
                case '\"': {
                    result.append("\\22");
                    continue block12;
                }
                case '+': {
                    result.append("\\2B");
                    continue block12;
                }
                case ',': {
                    result.append("\\2C");
                    continue block12;
                }
                case ';': {
                    result.append("\\3B");
                    continue block12;
                }
                case '<': {
                    result.append("\\3C");
                    continue block12;
                }
                case '>': {
                    result.append("\\3E");
                    continue block12;
                }
                case '\\': {
                    result.append("\\5C");
                    continue block12;
                }
                case '\u0000': {
                    result.append("\\00");
                    continue block12;
                }
                default: {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    protected static String convertToHexEscape(String input) {
        if (input.indexOf(92) == -1) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length() + 6);
        boolean previousSlash = false;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (previousSlash) {
                switch (c) {
                    case ' ': {
                        result.append("\\20");
                        break;
                    }
                    case '\"': {
                        result.append("\\22");
                        break;
                    }
                    case '#': {
                        result.append("\\23");
                        break;
                    }
                    case '+': {
                        result.append("\\2B");
                        break;
                    }
                    case ',': {
                        result.append("\\2C");
                        break;
                    }
                    case ';': {
                        result.append("\\3B");
                        break;
                    }
                    case '<': {
                        result.append("\\3C");
                        break;
                    }
                    case '=': {
                        result.append("\\3D");
                        break;
                    }
                    case '>': {
                        result.append("\\3E");
                        break;
                    }
                    case '\\': {
                        result.append("\\5C");
                        break;
                    }
                    default: {
                        result.append('\\');
                        result.append(c);
                    }
                }
                previousSlash = false;
                continue;
            }
            if (c == '\\') {
                previousSlash = true;
                continue;
            }
            result.append(c);
        }
        if (previousSlash) {
            result.append('\\');
        }
        return result.toString();
    }

    protected static class JNDIConnection {
        public MessageFormat userSearchFormat = null;
        public MessageFormat[] userPatternFormatArray = null;
        public MessageFormat roleBaseFormat = null;
        public MessageFormat roleFormat = null;
        public DirContext context = null;

        protected JNDIConnection() {
        }
    }

    protected static class User {
        private final String username;
        private final String dn;
        private final String password;
        private final List<String> roles;
        private final String userRoleId;

        public User(String username, String dn, String password, List<String> roles, String userRoleId) {
            this.username = username;
            this.dn = dn;
            this.password = password;
            this.roles = roles == null ? Collections.emptyList() : Collections.unmodifiableList(roles);
            this.userRoleId = userRoleId;
        }

        public String getUserName() {
            return this.username;
        }

        public String getDN() {
            return this.dn;
        }

        public String getPassword() {
            return this.password;
        }

        public List<String> getRoles() {
            return this.roles;
        }

        public String getUserRoleId() {
            return this.userRoleId;
        }
    }
}

