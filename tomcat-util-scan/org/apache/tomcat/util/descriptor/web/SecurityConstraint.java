/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.HttpConstraintElement
 *  javax.servlet.HttpMethodConstraintElement
 *  javax.servlet.ServletSecurityElement
 *  javax.servlet.annotation.ServletSecurity$EmptyRoleSemantic
 *  javax.servlet.annotation.ServletSecurity$TransportGuarantee
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.HttpConstraintElement;
import javax.servlet.HttpMethodConstraintElement;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.ServletSecurity;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.descriptor.web.Constants;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;
import org.apache.tomcat.util.res.StringManager;

public class SecurityConstraint
extends XmlEncodingBase
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ROLE_ALL_ROLES = "*";
    public static final String ROLE_ALL_AUTHENTICATED_USERS = "**";
    private static final StringManager sm = StringManager.getManager((String)Constants.PACKAGE_NAME);
    private boolean allRoles = false;
    private boolean authenticatedUsers = false;
    private boolean authConstraint = false;
    private String[] authRoles = new String[0];
    private SecurityCollection[] collections = new SecurityCollection[0];
    private String displayName = null;
    private String userConstraint = "NONE";

    public boolean getAllRoles() {
        return this.allRoles;
    }

    public boolean getAuthenticatedUsers() {
        return this.authenticatedUsers;
    }

    public boolean getAuthConstraint() {
        return this.authConstraint;
    }

    public void setAuthConstraint(boolean authConstraint) {
        this.authConstraint = authConstraint;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserConstraint() {
        return this.userConstraint;
    }

    public void setUserConstraint(String userConstraint) {
        if (userConstraint != null) {
            this.userConstraint = userConstraint;
        }
    }

    public void treatAllAuthenticatedUsersAsApplicationRole() {
        if (this.authenticatedUsers) {
            this.authenticatedUsers = false;
            String[] results = Arrays.copyOf(this.authRoles, this.authRoles.length + 1);
            results[this.authRoles.length] = ROLE_ALL_AUTHENTICATED_USERS;
            this.authRoles = results;
            this.authConstraint = true;
        }
    }

    public void addAuthRole(String authRole) {
        if (authRole == null) {
            return;
        }
        if (ROLE_ALL_ROLES.equals(authRole)) {
            this.allRoles = true;
            return;
        }
        if (ROLE_ALL_AUTHENTICATED_USERS.equals(authRole)) {
            this.authenticatedUsers = true;
            return;
        }
        String[] results = Arrays.copyOf(this.authRoles, this.authRoles.length + 1);
        results[this.authRoles.length] = authRole;
        this.authRoles = results;
        this.authConstraint = true;
    }

    @Override
    public void setCharset(Charset charset) {
        super.setCharset(charset);
        for (SecurityCollection collection : this.collections) {
            collection.setCharset(this.getCharset());
        }
    }

    public void addCollection(SecurityCollection collection) {
        if (collection == null) {
            return;
        }
        collection.setCharset(this.getCharset());
        SecurityCollection[] results = Arrays.copyOf(this.collections, this.collections.length + 1);
        results[this.collections.length] = collection;
        this.collections = results;
    }

    public boolean findAuthRole(String role) {
        if (role == null) {
            return false;
        }
        for (String authRole : this.authRoles) {
            if (!role.equals(authRole)) continue;
            return true;
        }
        return false;
    }

    public String[] findAuthRoles() {
        return this.authRoles;
    }

    public SecurityCollection findCollection(String name) {
        if (name == null) {
            return null;
        }
        for (SecurityCollection collection : this.collections) {
            if (!name.equals(collection.getName())) continue;
            return collection;
        }
        return null;
    }

    public SecurityCollection[] findCollections() {
        return this.collections;
    }

    public boolean included(String uri, String method) {
        if (method == null) {
            return false;
        }
        for (SecurityCollection collection : this.collections) {
            String[] patterns;
            if (!collection.findMethod(method)) continue;
            for (String pattern : patterns = collection.findPatterns()) {
                if (!this.matchPattern(uri, pattern)) continue;
                return true;
            }
        }
        return false;
    }

    public void removeAuthRole(String authRole) {
        if (authRole == null) {
            return;
        }
        if (ROLE_ALL_ROLES.equals(authRole)) {
            this.allRoles = false;
            return;
        }
        if (ROLE_ALL_AUTHENTICATED_USERS.equals(authRole)) {
            this.authenticatedUsers = false;
            return;
        }
        int n = -1;
        for (int i = 0; i < this.authRoles.length; ++i) {
            if (!this.authRoles[i].equals(authRole)) continue;
            n = i;
            break;
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.authRoles.length - 1];
            for (int i = 0; i < this.authRoles.length; ++i) {
                if (i == n) continue;
                results[j++] = this.authRoles[i];
            }
            this.authRoles = results;
        }
    }

    public void removeCollection(SecurityCollection collection) {
        if (collection == null) {
            return;
        }
        int n = -1;
        for (int i = 0; i < this.collections.length; ++i) {
            if (!this.collections[i].equals(collection)) continue;
            n = i;
            break;
        }
        if (n >= 0) {
            int j = 0;
            SecurityCollection[] results = new SecurityCollection[this.collections.length - 1];
            for (int i = 0; i < this.collections.length; ++i) {
                if (i == n) continue;
                results[j++] = this.collections[i];
            }
            this.collections = results;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SecurityConstraint[");
        for (int i = 0; i < this.collections.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.collections[i].getName());
        }
        sb.append(']');
        return sb.toString();
    }

    private boolean matchPattern(String path, String pattern) {
        if (path == null || path.length() == 0) {
            path = "/";
        }
        if (pattern == null || pattern.length() == 0) {
            pattern = "/";
        }
        if (path.equals(pattern)) {
            return true;
        }
        if (pattern.startsWith("/") && pattern.endsWith("/*")) {
            if ((pattern = pattern.substring(0, pattern.length() - 2)).length() == 0) {
                return true;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            while (true) {
                if (pattern.equals(path)) {
                    return true;
                }
                int slash = path.lastIndexOf(47);
                if (slash <= 0) break;
                path = path.substring(0, slash);
            }
            return false;
        }
        if (pattern.startsWith("*.")) {
            int slash = path.lastIndexOf(47);
            int period = path.lastIndexOf(46);
            return slash >= 0 && period > slash && path.endsWith(pattern.substring(1));
        }
        return pattern.equals("/");
    }

    public static SecurityConstraint[] createConstraints(ServletSecurityElement element, String urlPattern) {
        HashSet<SecurityConstraint> result = new HashSet<SecurityConstraint>();
        Collection methods = element.getHttpMethodConstraints();
        for (HttpMethodConstraintElement methodElement : methods) {
            SecurityConstraint constraint = SecurityConstraint.createConstraint((HttpConstraintElement)methodElement, urlPattern, true);
            SecurityCollection collection = constraint.findCollections()[0];
            collection.addMethod(methodElement.getMethodName());
            result.add(constraint);
        }
        SecurityConstraint constraint = SecurityConstraint.createConstraint((HttpConstraintElement)element, urlPattern, false);
        if (constraint != null) {
            SecurityCollection collection = constraint.findCollections()[0];
            for (String name : element.getMethodNames()) {
                collection.addOmittedMethod(name);
            }
            result.add(constraint);
        }
        return result.toArray(new SecurityConstraint[0]);
    }

    private static SecurityConstraint createConstraint(HttpConstraintElement element, String urlPattern, boolean alwaysCreate) {
        SecurityConstraint constraint = new SecurityConstraint();
        SecurityCollection collection = new SecurityCollection();
        boolean create = alwaysCreate;
        if (element.getTransportGuarantee() != ServletSecurity.TransportGuarantee.NONE) {
            constraint.setUserConstraint(element.getTransportGuarantee().name());
            create = true;
        }
        if (element.getRolesAllowed().length > 0) {
            String[] roles;
            for (String role : roles = element.getRolesAllowed()) {
                constraint.addAuthRole(role);
            }
            create = true;
        }
        if (element.getEmptyRoleSemantic() != ServletSecurity.EmptyRoleSemantic.PERMIT) {
            constraint.setAuthConstraint(true);
            create = true;
        }
        if (create) {
            collection.addPattern(urlPattern);
            constraint.addCollection(collection);
            return constraint;
        }
        return null;
    }

    public static SecurityConstraint[] findUncoveredHttpMethods(SecurityConstraint[] constraints, boolean denyUncoveredHttpMethods, Log log) {
        HashSet<String> coveredPatterns = new HashSet<String>();
        HashMap<String, Set> urlMethodMap = new HashMap<String, Set>();
        HashMap urlOmittedMethodMap = new HashMap();
        ArrayList<SecurityConstraint> newConstraints = new ArrayList<SecurityConstraint>();
        for (SecurityConstraint constraint : constraints) {
            SecurityCollection[] collections;
            for (SecurityCollection collection : collections = constraint.findCollections()) {
                String[] patterns = collection.findPatterns();
                String[] methods = collection.findMethods();
                String[] omittedMethods = collection.findOmittedMethods();
                if (methods.length == 0 && omittedMethods.length == 0) {
                    coveredPatterns.addAll(Arrays.asList(patterns));
                    continue;
                }
                List<String> omNew = null;
                if (omittedMethods.length != 0) {
                    omNew = Arrays.asList(omittedMethods);
                }
                for (String pattern : patterns) {
                    if (coveredPatterns.contains(pattern)) continue;
                    if (methods.length == 0) {
                        HashSet<String> om = (HashSet<String>)urlOmittedMethodMap.get(pattern);
                        if (om == null) {
                            om = new HashSet<String>();
                            urlOmittedMethodMap.put(pattern, om);
                            om.addAll(omNew);
                            continue;
                        }
                        om.retainAll(omNew);
                        continue;
                    }
                    urlMethodMap.computeIfAbsent(pattern, k -> new HashSet()).addAll(Arrays.asList(methods));
                }
            }
        }
        for (Map.Entry entry : urlMethodMap.entrySet()) {
            String pattern = (String)entry.getKey();
            if (coveredPatterns.contains(pattern)) {
                urlOmittedMethodMap.remove(pattern);
                continue;
            }
            Set omittedMethods = (Set)urlOmittedMethodMap.remove(pattern);
            Set methods = (Set)entry.getValue();
            if (omittedMethods == null) {
                StringBuilder msg = new StringBuilder();
                for (Object method : methods) {
                    msg.append((String)method);
                    msg.append(' ');
                }
                if (denyUncoveredHttpMethods) {
                    Object method;
                    log.info((Object)sm.getString("securityConstraint.uncoveredHttpMethodFix", new Object[]{pattern, msg.toString().trim()}));
                    SecurityCollection collection = new SecurityCollection();
                    method = methods.iterator();
                    while (method.hasNext()) {
                        String method2 = (String)method.next();
                        collection.addOmittedMethod(method2);
                    }
                    collection.addPatternDecoded(pattern);
                    collection.setName("deny-uncovered-http-methods");
                    SecurityConstraint constraint = new SecurityConstraint();
                    constraint.setAuthConstraint(true);
                    constraint.addCollection(collection);
                    newConstraints.add(constraint);
                    continue;
                }
                log.error((Object)sm.getString("securityConstraint.uncoveredHttpMethod", new Object[]{pattern, msg.toString().trim()}));
                continue;
            }
            omittedMethods.removeAll(methods);
            SecurityConstraint.handleOmittedMethods(omittedMethods, pattern, denyUncoveredHttpMethods, newConstraints, log);
        }
        for (Map.Entry entry : urlOmittedMethodMap.entrySet()) {
            String pattern = (String)entry.getKey();
            if (coveredPatterns.contains(pattern)) continue;
            SecurityConstraint.handleOmittedMethods((Set)entry.getValue(), pattern, denyUncoveredHttpMethods, newConstraints, log);
        }
        return newConstraints.toArray(new SecurityConstraint[0]);
    }

    private static void handleOmittedMethods(Set<String> omittedMethods, String pattern, boolean denyUncoveredHttpMethods, List<SecurityConstraint> newConstraints, Log log) {
        if (omittedMethods.size() > 0) {
            StringBuilder msg = new StringBuilder();
            for (String string : omittedMethods) {
                msg.append(string);
                msg.append(' ');
            }
            if (denyUncoveredHttpMethods) {
                log.info((Object)sm.getString("securityConstraint.uncoveredHttpOmittedMethodFix", new Object[]{pattern, msg.toString().trim()}));
                SecurityCollection collection = new SecurityCollection();
                for (String method : omittedMethods) {
                    collection.addMethod(method);
                }
                collection.addPatternDecoded(pattern);
                collection.setName("deny-uncovered-http-methods");
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setAuthConstraint(true);
                securityConstraint.addCollection(collection);
                newConstraints.add(securityConstraint);
            } else {
                log.error((Object)sm.getString("securityConstraint.uncoveredHttpOmittedMethod", new Object[]{pattern, msg.toString().trim()}));
            }
        }
    }
}

