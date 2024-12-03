/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.security;

import java.security.Security;
import org.apache.catalina.startup.CatalinaProperties;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class SecurityConfig {
    private static final Object singletonLock = new Object();
    private static volatile SecurityConfig singleton = null;
    private static final Log log = LogFactory.getLog(SecurityConfig.class);
    private static final String PACKAGE_ACCESS = "sun.,org.apache.catalina.,org.apache.jasper.,org.apache.coyote.,org.apache.tomcat.";
    private static final String PACKAGE_DEFINITION = "java.,sun.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper.";
    private final String packageDefinition;
    private final String packageAccess;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SecurityConfig() {
        String definition = null;
        String access = null;
        try {
            definition = CatalinaProperties.getProperty("package.definition");
            access = CatalinaProperties.getProperty("package.access");
        }
        catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"Unable to load properties using CatalinaProperties", (Throwable)ex);
            }
        }
        finally {
            this.packageDefinition = definition;
            this.packageAccess = access;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SecurityConfig newInstance() {
        if (singleton == null) {
            Object object = singletonLock;
            synchronized (object) {
                if (singleton == null) {
                    singleton = new SecurityConfig();
                }
            }
        }
        return singleton;
    }

    public void setPackageAccess() {
        if (this.packageAccess == null) {
            this.setSecurityProperty("package.access", PACKAGE_ACCESS);
        } else {
            this.setSecurityProperty("package.access", this.packageAccess);
        }
    }

    public void setPackageDefinition() {
        if (this.packageDefinition == null) {
            this.setSecurityProperty("package.definition", PACKAGE_DEFINITION);
        } else {
            this.setSecurityProperty("package.definition", this.packageDefinition);
        }
    }

    private void setSecurityProperty(String properties, String packageList) {
        if (System.getSecurityManager() != null) {
            String definition = Security.getProperty(properties);
            if (definition != null && definition.length() > 0) {
                if (packageList.length() > 0) {
                    definition = definition + ',' + packageList;
                }
            } else {
                definition = packageList;
            }
            Security.setProperty(properties, definition);
        }
    }
}

