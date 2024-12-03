/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.annotation.ServletSecurity$TransportGuarantee
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.descriptor.web.SecurityCollection
 *  org.apache.tomcat.util.descriptor.web.SecurityConstraint
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.ConcurrentMessageDigest
 */
package org.apache.catalina.realm;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.servlet.annotation.ServletSecurity;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.DigestCredentialHandlerBase;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.MessageDigestCredentialHandler;
import org.apache.catalina.realm.SecretKeyCredentialHandler;
import org.apache.catalina.realm.X509SubjectDnRetriever;
import org.apache.catalina.realm.X509UsernameRetriever;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.SessionConfig;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

public abstract class RealmBase
extends LifecycleMBeanBase
implements Realm {
    private static final Log log = LogFactory.getLog(RealmBase.class);
    protected static final String USER_ATTRIBUTES_DELIMITER = ",";
    protected static final String USER_ATTRIBUTES_WILDCARD = "*";
    private static final List<Class<? extends DigestCredentialHandlerBase>> credentialHandlerClasses = new ArrayList<Class<? extends DigestCredentialHandlerBase>>();
    protected Container container = null;
    protected Log containerLog = null;
    private CredentialHandler credentialHandler;
    protected static final StringManager sm;
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected boolean validate = true;
    protected String x509UsernameRetrieverClassName;
    protected X509UsernameRetriever x509UsernameRetriever;
    protected AllRolesMode allRolesMode = AllRolesMode.STRICT_MODE;
    protected boolean stripRealmForGss = true;
    private int transportGuaranteeRedirectStatus = 302;
    protected String userAttributes = null;
    protected List<String> userAttributesList = null;
    protected String realmPath = "/realm0";

    public int getTransportGuaranteeRedirectStatus() {
        return this.transportGuaranteeRedirectStatus;
    }

    public void setTransportGuaranteeRedirectStatus(int transportGuaranteeRedirectStatus) {
        this.transportGuaranteeRedirectStatus = transportGuaranteeRedirectStatus;
    }

    @Override
    public CredentialHandler getCredentialHandler() {
        return this.credentialHandler;
    }

    @Override
    public void setCredentialHandler(CredentialHandler credentialHandler) {
        this.credentialHandler = credentialHandler;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        Container oldContainer = this.container;
        this.container = container;
        this.support.firePropertyChange("container", oldContainer, this.container);
    }

    public String getAllRolesMode() {
        return this.allRolesMode.toString();
    }

    public void setAllRolesMode(String allRolesMode) {
        this.allRolesMode = AllRolesMode.toMode(allRolesMode);
    }

    public boolean getValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getX509UsernameRetrieverClassName() {
        return this.x509UsernameRetrieverClassName;
    }

    public void setX509UsernameRetrieverClassName(String className) {
        this.x509UsernameRetrieverClassName = className;
    }

    public boolean isStripRealmForGss() {
        return this.stripRealmForGss;
    }

    public void setStripRealmForGss(boolean stripRealmForGss) {
        this.stripRealmForGss = stripRealmForGss;
    }

    public String getUserAttributes() {
        return this.userAttributes;
    }

    public void setUserAttributes(String userAttributes) {
        this.userAttributes = userAttributes;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override
    public Principal authenticate(String username) {
        if (username == null) {
            return null;
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)sm.getString("realmBase.authenticateSuccess", new Object[]{username}));
        }
        return this.getPrincipal(username);
    }

    @Override
    public Principal authenticate(String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("realmBase.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        String serverCredentials = this.getPassword(username);
        if (serverCredentials == null) {
            this.getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("realmBase.authenticateFailure", new Object[]{username}));
            }
            return null;
        }
        boolean validated = this.getCredentialHandler().matches(credentials, serverCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace((Object)sm.getString("realmBase.authenticateSuccess", new Object[]{username}));
            }
            return this.getPrincipal(username);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace((Object)sm.getString("realmBase.authenticateFailure", new Object[]{username}));
        }
        return null;
    }

    @Override
    @Deprecated
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2) {
        return this.authenticate(username, clientDigest, nonce, nc, cnonce, qop, realm, digestA2, "MD5");
    }

    @Override
    public Principal authenticate(String username, String clientDigest, String nonce, String nc, String cnonce, String qop, String realm, String digestA2, String algorithm) {
        String digestA1 = this.getDigest(username, realm, algorithm);
        if (digestA1 == null) {
            return null;
        }
        digestA1 = digestA1.toLowerCase(Locale.ENGLISH);
        String serverDigestValue = qop == null ? digestA1 + ":" + nonce + ":" + digestA2 : digestA1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + digestA2;
        byte[] valueBytes = null;
        try {
            valueBytes = serverDigestValue.getBytes(this.getDigestCharset());
        }
        catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(sm.getString("realmBase.invalidDigestEncoding", new Object[]{this.getDigestEncoding()}), uee);
        }
        String serverDigest = HexUtils.toHexString((byte[])ConcurrentMessageDigest.digest((String)algorithm, (byte[][])new byte[][]{valueBytes}));
        if (log.isDebugEnabled()) {
            log.debug((Object)("Digest : " + clientDigest + " Username:" + username + " ClientDigest:" + clientDigest + " nonce:" + nonce + " nc:" + nc + " cnonce:" + cnonce + " qop:" + qop + " realm:" + realm + "digestA2:" + digestA2 + " Server digest:" + serverDigest));
        }
        if (serverDigest.equals(clientDigest)) {
            return this.getPrincipal(username);
        }
        return null;
    }

    @Override
    public Principal authenticate(X509Certificate[] certs) {
        if (certs == null || certs.length < 1) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Authenticating client certificate chain");
        }
        if (this.validate) {
            for (X509Certificate cert : certs) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)(" Checking validity for '" + cert.getSubjectX500Principal().toString() + "'"));
                }
                try {
                    cert.checkValidity();
                }
                catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"  Validity exception", (Throwable)e);
                    }
                    return null;
                }
            }
        }
        return this.getPrincipal(certs[0]);
    }

    @Override
    public Principal authenticate(GSSContext gssContext, boolean storeCred) {
        if (gssContext.isEstablished()) {
            GSSName gssName = null;
            try {
                gssName = gssContext.getSrcName();
            }
            catch (GSSException e) {
                log.warn((Object)sm.getString("realmBase.gssNameFail"), (Throwable)e);
            }
            if (gssName != null) {
                GSSCredential gssCredential = null;
                if (storeCred) {
                    if (gssContext.getCredDelegState()) {
                        try {
                            gssCredential = gssContext.getDelegCred();
                        }
                        catch (GSSException e) {
                            log.warn((Object)sm.getString("realmBase.delegatedCredentialFail", new Object[]{gssName}), (Throwable)e);
                        }
                    } else if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("realmBase.credentialNotDelegated", new Object[]{gssName}));
                    }
                }
                return this.getPrincipal(gssName, gssCredential);
            }
        } else {
            log.error((Object)sm.getString("realmBase.gssContextNotEstablished"));
        }
        return null;
    }

    @Override
    public Principal authenticate(GSSName gssName, GSSCredential gssCredential) {
        if (gssName == null) {
            return null;
        }
        return this.getPrincipal(gssName, gssCredential);
    }

    @Override
    public void backgroundProcess() {
    }

    @Override
    public SecurityConstraint[] findSecurityConstraints(Request request, Context context) {
        SecurityCollection[] collection;
        String[] patterns;
        int i;
        ArrayList<SecurityConstraint> results = null;
        SecurityConstraint[] constraints = context.findConstraints();
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"  No applicable constraints defined");
            }
            return null;
        }
        String uri = request.getRequestPathMB().toString();
        if (uri == null || uri.length() == 0) {
            uri = "/";
        }
        String method = request.getMethod();
        boolean found = false;
        for (i = 0; i < constraints.length; ++i) {
            SecurityCollection[] collections = constraints[i].findCollections();
            if (collections == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
            }
            for (SecurityCollection securityCollection : collections) {
                patterns = securityCollection.findPatterns();
                if (patterns == null) continue;
                for (String string : patterns) {
                    if (!uri.equals(string) && (string.length() != 0 || !uri.equals("/"))) continue;
                    found = true;
                    if (!securityCollection.findMethod(method)) continue;
                    if (results == null) {
                        results = new ArrayList();
                    }
                    results.add(constraints[i]);
                }
            }
        }
        if (found) {
            return this.resultsToArray(results);
        }
        int longest = -1;
        for (i = 0; i < constraints.length; ++i) {
            collection = constraints[i].findCollections();
            if (collection == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
            }
            for (SecurityCollection securityCollection : collection) {
                String[] patterns2 = securityCollection.findPatterns();
                if (patterns2 == null) continue;
                boolean matched = false;
                int length = -1;
                String[] stringArray = patterns2;
                int n = stringArray.length;
                for (int j = 0; j < n; ++j) {
                    String pattern2 = stringArray[j];
                    if (!pattern2.startsWith("/") || !pattern2.endsWith("/*") || pattern2.length() < longest) continue;
                    if (pattern2.length() == 2) {
                        matched = true;
                        length = pattern2.length();
                        continue;
                    }
                    if (!pattern2.regionMatches(0, uri, 0, pattern2.length() - 1) && (pattern2.length() - 2 != uri.length() || !pattern2.regionMatches(0, uri, 0, pattern2.length() - 2))) continue;
                    matched = true;
                    length = pattern2.length();
                }
                if (!matched) continue;
                if (length > longest) {
                    found = false;
                    if (results != null) {
                        results.clear();
                    }
                    longest = length;
                }
                if (!securityCollection.findMethod(method)) continue;
                found = true;
                if (results == null) {
                    results = new ArrayList();
                }
                results.add(constraints[i]);
            }
        }
        if (found) {
            return this.resultsToArray(results);
        }
        for (i = 0; i < constraints.length; ++i) {
            collection = constraints[i].findCollections();
            if (collection == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
            }
            boolean matched = false;
            int pos = -1;
            for (int j = 0; j < collection.length; ++j) {
                patterns = collection[j].findPatterns();
                if (patterns == null) continue;
                for (int k = 0; k < patterns.length && !matched; ++k) {
                    String pattern = patterns[k];
                    if (!pattern.startsWith("*.")) continue;
                    int slash = uri.lastIndexOf(47);
                    int n = uri.lastIndexOf(46);
                    if (slash < 0 || n <= slash || n == uri.length() - 1 || uri.length() - n != pattern.length() - 1 || !pattern.regionMatches(1, uri, n, uri.length() - n)) continue;
                    matched = true;
                    pos = j;
                }
            }
            if (!matched) continue;
            found = true;
            if (!collection[pos].findMethod(method)) continue;
            if (results == null) {
                results = new ArrayList<SecurityConstraint>();
            }
            results.add(constraints[i]);
        }
        if (found) {
            return this.resultsToArray(results);
        }
        for (i = 0; i < constraints.length; ++i) {
            collection = constraints[i].findCollections();
            if (collection == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Checking constraint '" + constraints[i] + "' against " + method + " " + uri + " --> " + constraints[i].included(uri, method)));
            }
            for (SecurityCollection securityCollection : collection) {
                String[] patterns3 = securityCollection.findPatterns();
                if (patterns3 == null) continue;
                boolean matched = false;
                for (String pattern : patterns3) {
                    if (!pattern.equals("/")) continue;
                    matched = true;
                    break;
                }
                if (!matched) continue;
                if (results == null) {
                    results = new ArrayList();
                }
                results.add(constraints[i]);
            }
        }
        if (results == null && log.isDebugEnabled()) {
            log.debug((Object)"  No applicable constraint located");
        }
        return this.resultsToArray(results);
    }

    private SecurityConstraint[] resultsToArray(ArrayList<SecurityConstraint> results) {
        if (results == null || results.size() == 0) {
            return null;
        }
        return results.toArray(new SecurityConstraint[0]);
    }

    @Override
    public boolean hasResourcePermission(Request request, Response response, SecurityConstraint[] constraints, Context context) throws IOException {
        String[] roles;
        if (constraints == null || constraints.length == 0) {
            return true;
        }
        Principal principal = request.getPrincipal();
        boolean status = false;
        boolean denyfromall = false;
        for (SecurityConstraint constraint : constraints) {
            roles = constraint.getAllRoles() ? request.getContext().findSecurityRoles() : constraint.findAuthRoles();
            if (roles == null) {
                roles = new String[]{};
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("  Checking roles " + principal));
            }
            if (constraint.getAuthenticatedUsers() && principal != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Passing all authenticated users");
                }
                status = true;
                continue;
            }
            if (roles.length == 0 && !constraint.getAllRoles() && !constraint.getAuthenticatedUsers()) {
                if (constraint.getAuthConstraint()) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"No roles");
                    }
                    status = false;
                    denyfromall = true;
                    break;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Passing all access");
                }
                status = true;
                continue;
            }
            if (principal == null) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)"  No user authenticated, cannot grant access");
                continue;
            }
            for (String role : roles) {
                if (this.hasRole(request.getWrapper(), principal, role)) {
                    status = true;
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Role found:  " + role));
                    continue;
                }
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("No role found:  " + role));
            }
        }
        if (!denyfromall && this.allRolesMode != AllRolesMode.STRICT_MODE && !status && principal != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Checking for all roles mode: " + this.allRolesMode));
            }
            for (SecurityConstraint constraint : constraints) {
                if (!constraint.getAllRoles()) continue;
                if (this.allRolesMode == AllRolesMode.AUTH_ONLY_MODE) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)"Granting access for role-name=*, auth-only");
                    }
                    status = true;
                    break;
                }
                roles = request.getContext().findSecurityRoles();
                if (roles == null) {
                    roles = new String[]{};
                }
                if (roles.length != 0 || this.allRolesMode != AllRolesMode.STRICT_AUTH_ONLY_MODE) continue;
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Granting access for role-name=*, strict auth-only");
                }
                status = true;
                break;
            }
        }
        if (!status) {
            response.sendError(403, sm.getString("realmBase.forbidden"));
        }
        return status;
    }

    @Override
    public boolean hasRole(Wrapper wrapper, Principal principal, String role) {
        String realRole;
        if (wrapper != null && (realRole = wrapper.findSecurityReference(role)) != null) {
            role = realRole;
        }
        if (principal == null || role == null) {
            return false;
        }
        boolean result = this.hasRoleInternal(principal, role);
        if (log.isDebugEnabled()) {
            String name = principal.getName();
            if (result) {
                log.debug((Object)sm.getString("realmBase.hasRoleSuccess", new Object[]{name, role}));
            } else {
                log.debug((Object)sm.getString("realmBase.hasRoleFailure", new Object[]{name, role}));
            }
        }
        return result;
    }

    protected List<String> parseUserAttributes(String userAttributes) {
        if (userAttributes == null) {
            return null;
        }
        ArrayList<String> attrs = new ArrayList<String>();
        for (String name : userAttributes.split(USER_ATTRIBUTES_DELIMITER)) {
            if ((name = name.trim()).length() == 0) continue;
            if (name.equals(USER_ATTRIBUTES_WILDCARD)) {
                return Collections.singletonList(USER_ATTRIBUTES_WILDCARD);
            }
            if (attrs.contains(name)) continue;
            attrs.add(name);
        }
        return attrs.size() > 0 ? attrs : null;
    }

    protected boolean hasRoleInternal(Principal principal, String role) {
        if (!(principal instanceof GenericPrincipal)) {
            return false;
        }
        GenericPrincipal gp = (GenericPrincipal)principal;
        return gp.hasRole(role);
    }

    @Override
    public boolean hasUserDataPermission(Request request, Response response, SecurityConstraint[] constraints) throws IOException {
        String queryString;
        if (constraints == null || constraints.length == 0) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"  No applicable security constraint defined");
            }
            return true;
        }
        for (SecurityConstraint constraint : constraints) {
            String userConstraint = constraint.getUserConstraint();
            if (userConstraint == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"  No applicable user data constraint defined");
                }
                return true;
            }
            if (!userConstraint.equals(ServletSecurity.TransportGuarantee.NONE.name())) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)"  User data constraint has no restrictions");
            }
            return true;
        }
        if (request.getRequest().isSecure()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"  User data constraint already satisfied");
            }
            return true;
        }
        int redirectPort = request.getConnector().getRedirectPortWithOffset();
        if (redirectPort <= 0) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"  SSL redirect is disabled");
            }
            response.sendError(403, request.getRequestURI());
            return false;
        }
        StringBuilder file = new StringBuilder();
        String protocol = "https";
        String host = request.getServerName();
        file.append(protocol).append("://").append(host);
        if (redirectPort != 443) {
            file.append(':').append(redirectPort);
        }
        file.append(request.getRequestURI());
        String requestedSessionId = request.getRequestedSessionId();
        if (requestedSessionId != null && request.isRequestedSessionIdFromURL()) {
            file.append(';');
            file.append(SessionConfig.getSessionUriParamName(request.getContext()));
            file.append('=');
            file.append(requestedSessionId);
        }
        if ((queryString = request.getQueryString()) != null) {
            file.append('?');
            file.append(queryString);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("  Redirecting to " + file.toString()));
        }
        response.sendRedirect(file.toString(), this.transportGuaranteeRedirectStatus);
        return false;
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.container != null) {
            this.containerLog = this.container.getLogger();
        }
        this.x509UsernameRetriever = RealmBase.createUsernameRetriever(this.x509UsernameRetrieverClassName);
    }

    @Override
    protected void startInternal() throws LifecycleException {
        if (this.credentialHandler == null) {
            this.credentialHandler = new MessageDigestCredentialHandler();
        }
        if (this.userAttributes != null) {
            this.userAttributesList = this.parseUserAttributes(this.userAttributes);
        }
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
    }

    public String toString() {
        return ToStringUtil.toString(this);
    }

    protected boolean hasMessageDigest(String algorithm) {
        String realmAlgorithm;
        CredentialHandler ch = this.credentialHandler;
        if (ch instanceof MessageDigestCredentialHandler && (realmAlgorithm = ((MessageDigestCredentialHandler)ch).getAlgorithm()) != null) {
            if (realmAlgorithm.equals(algorithm)) {
                return true;
            }
            log.debug((Object)sm.getString("relamBase.digestMismatch", new Object[]{algorithm, realmAlgorithm}));
        }
        return false;
    }

    @Deprecated
    protected String getDigest(String username, String realmName) {
        return this.getDigest(username, realmName, "MD5");
    }

    protected String getDigest(String username, String realmName, String algorithm) {
        if (this.hasMessageDigest(algorithm)) {
            return this.getPassword(username);
        }
        String digestValue = username + ":" + realmName + ":" + this.getPassword(username);
        byte[] valueBytes = null;
        try {
            valueBytes = digestValue.getBytes(this.getDigestCharset());
        }
        catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(sm.getString("realmBase.invalidDigestEncoding", new Object[]{this.getDigestEncoding()}), uee);
        }
        return HexUtils.toHexString((byte[])ConcurrentMessageDigest.digest((String)algorithm, (byte[][])new byte[][]{valueBytes}));
    }

    private String getDigestEncoding() {
        CredentialHandler ch = this.credentialHandler;
        if (ch instanceof MessageDigestCredentialHandler) {
            return ((MessageDigestCredentialHandler)ch).getEncoding();
        }
        return null;
    }

    private Charset getDigestCharset() throws UnsupportedEncodingException {
        String charset = this.getDigestEncoding();
        if (charset == null) {
            return StandardCharsets.ISO_8859_1;
        }
        return B2CConverter.getCharset((String)charset);
    }

    protected abstract String getPassword(String var1);

    protected Principal getPrincipal(X509Certificate usercert) {
        String username = this.x509UsernameRetriever.getUsername(usercert);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("realmBase.gotX509Username", new Object[]{username}));
        }
        return this.getPrincipal(username);
    }

    protected abstract Principal getPrincipal(String var1);

    @Deprecated
    protected Principal getPrincipal(String username, GSSCredential gssCredential) {
        Principal p = this.getPrincipal(username);
        if (p instanceof GenericPrincipal) {
            ((GenericPrincipal)p).setGssCredential(gssCredential);
        }
        return p;
    }

    protected Principal getPrincipal(GSSName gssName, GSSCredential gssCredential) {
        Principal p;
        int i;
        String name = gssName.toString();
        if (this.isStripRealmForGss() && (i = name.indexOf(64)) > 0) {
            name = name.substring(0, i);
        }
        if ((p = this.getPrincipal(name)) instanceof GenericPrincipal) {
            ((GenericPrincipal)p).setGssCredential(gssCredential);
        }
        return p;
    }

    protected Server getServer() {
        Service s;
        Container c = this.container;
        if (c instanceof Context) {
            c = c.getParent();
        }
        if (c instanceof Host) {
            c = c.getParent();
        }
        if (c instanceof Engine && (s = ((Engine)c).getService()) != null) {
            return s.getServer();
        }
        return null;
    }

    public static void main(String[] args) {
        int saltLength = -1;
        int iterations = -1;
        int keyLength = -1;
        String encoding = Charset.defaultCharset().name();
        String algorithm = null;
        String handlerClassName = null;
        if (args.length == 0) {
            RealmBase.usage();
            return;
        }
        int argIndex = 0;
        while (args.length > argIndex + 2 && args[argIndex].length() == 2 && args[argIndex].charAt(0) == '-') {
            switch (args[argIndex].charAt(1)) {
                case 'a': {
                    algorithm = args[argIndex + 1];
                    break;
                }
                case 'e': {
                    encoding = args[argIndex + 1];
                    break;
                }
                case 'i': {
                    iterations = Integer.parseInt(args[argIndex + 1]);
                    break;
                }
                case 's': {
                    saltLength = Integer.parseInt(args[argIndex + 1]);
                    break;
                }
                case 'k': {
                    keyLength = Integer.parseInt(args[argIndex + 1]);
                    break;
                }
                case 'h': {
                    handlerClassName = args[argIndex + 1];
                    break;
                }
                default: {
                    RealmBase.usage();
                    return;
                }
            }
            argIndex += 2;
        }
        if (algorithm == null && handlerClassName == null) {
            algorithm = "SHA-512";
        }
        CredentialHandler handler = null;
        if (handlerClassName == null) {
            for (Class<? extends DigestCredentialHandlerBase> clazz : credentialHandlerClasses) {
                try {
                    handler = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    if (!IntrospectionUtils.setProperty((Object)handler, (String)"algorithm", (String)algorithm)) continue;
                    break;
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                Class<?> clazz = Class.forName(handlerClassName);
                handler = (DigestCredentialHandlerBase)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                IntrospectionUtils.setProperty((Object)handler, (String)"algorithm", (String)algorithm);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        if (handler == null) {
            throw new RuntimeException(new NoSuchAlgorithmException(algorithm));
        }
        IntrospectionUtils.setProperty(handler, (String)"encoding", (String)encoding);
        if (iterations > 0) {
            IntrospectionUtils.setProperty((Object)handler, (String)"iterations", (String)Integer.toString(iterations));
        }
        if (saltLength > -1) {
            IntrospectionUtils.setProperty((Object)handler, (String)"saltLength", (String)Integer.toString(saltLength));
        }
        if (keyLength > 0) {
            IntrospectionUtils.setProperty((Object)handler, (String)"keyLength", (String)Integer.toString(keyLength));
        }
        while (argIndex < args.length) {
            String credential = args[argIndex];
            System.out.print(credential + ":");
            System.out.println(handler.mutate(credential));
            ++argIndex;
        }
    }

    private static void usage() {
        System.out.println("Usage: RealmBase [-a <algorithm>] [-e <encoding>] [-i <iterations>] [-s <salt-length>] [-k <key-length>] [-h <handler-class-name>] <credentials>");
    }

    @Override
    public String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("type=Realm");
        keyProperties.append(this.getRealmSuffix());
        keyProperties.append(this.container.getMBeanKeyProperties());
        return keyProperties.toString();
    }

    @Override
    public String getDomainInternal() {
        return this.container.getDomain();
    }

    public String getRealmPath() {
        return this.realmPath;
    }

    public void setRealmPath(String theRealmPath) {
        this.realmPath = theRealmPath;
    }

    protected String getRealmSuffix() {
        return ",realmPath=" + this.getRealmPath();
    }

    private static X509UsernameRetriever createUsernameRetriever(String className) throws LifecycleException {
        if (null == className || className.trim().isEmpty()) {
            return new X509SubjectDnRetriever();
        }
        try {
            Class<?> clazz = Class.forName(className);
            return (X509UsernameRetriever)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new LifecycleException(sm.getString("realmBase.createUsernameRetriever.newInstance", new Object[]{className}), e);
        }
        catch (ClassCastException e) {
            throw new LifecycleException(sm.getString("realmBase.createUsernameRetriever.ClassCastException", new Object[]{className}), e);
        }
    }

    @Override
    public String[] getRoles(Principal principal) {
        if (principal instanceof GenericPrincipal) {
            return ((GenericPrincipal)principal).getRoles();
        }
        String className = principal.getClass().getSimpleName();
        throw new IllegalStateException(sm.getString("realmBase.cannotGetRoles", new Object[]{className}));
    }

    static {
        credentialHandlerClasses.add(MessageDigestCredentialHandler.class);
        credentialHandlerClasses.add(SecretKeyCredentialHandler.class);
        sm = StringManager.getManager(RealmBase.class);
    }

    protected static class AllRolesMode {
        private final String name;
        public static final AllRolesMode STRICT_MODE = new AllRolesMode("strict");
        public static final AllRolesMode AUTH_ONLY_MODE = new AllRolesMode("authOnly");
        public static final AllRolesMode STRICT_AUTH_ONLY_MODE = new AllRolesMode("strictAuthOnly");

        static AllRolesMode toMode(String name) {
            AllRolesMode mode;
            if (name.equalsIgnoreCase(AllRolesMode.STRICT_MODE.name)) {
                mode = STRICT_MODE;
            } else if (name.equalsIgnoreCase(AllRolesMode.AUTH_ONLY_MODE.name)) {
                mode = AUTH_ONLY_MODE;
            } else if (name.equalsIgnoreCase(AllRolesMode.STRICT_AUTH_ONLY_MODE.name)) {
                mode = STRICT_AUTH_ONLY_MODE;
            } else {
                throw new IllegalStateException(sm.getString("realmBase.unknownAllRolesMode", new Object[]{name}));
            }
            return mode;
        }

        private AllRolesMode(String name) {
            this.name = name;
        }

        public boolean equals(Object o) {
            boolean equals = false;
            if (o instanceof AllRolesMode) {
                AllRolesMode mode = (AllRolesMode)o;
                equals = this.name.equals(mode.name);
            }
            return equals;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name;
        }
    }
}

