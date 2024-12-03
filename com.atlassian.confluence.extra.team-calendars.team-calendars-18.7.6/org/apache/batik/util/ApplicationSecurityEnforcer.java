/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.net.URL;
import java.security.Policy;
import org.apache.batik.util.BatikSecurityManager;
import org.apache.batik.util.Messages;

public class ApplicationSecurityEnforcer {
    public static final String EXCEPTION_ALIEN_SECURITY_MANAGER = "ApplicationSecurityEnforcer.message.security.exception.alien.security.manager";
    public static final String EXCEPTION_NO_POLICY_FILE = "ApplicationSecurityEnforcer.message.null.pointer.exception.no.policy.file";
    public static final String PROPERTY_JAVA_SECURITY_POLICY = "java.security.policy";
    public static final String JAR_PROTOCOL = "jar:";
    public static final String JAR_URL_FILE_SEPARATOR = "!/";
    public static final String PROPERTY_APP_DEV_BASE = "app.dev.base";
    public static final String PROPERTY_APP_JAR_BASE = "app.jar.base";
    public static final String APP_MAIN_CLASS_DIR = "classes/";
    protected Class appMainClass;
    protected String securityPolicy;
    protected String appMainClassRelativeURL;
    protected BatikSecurityManager lastSecurityManagerInstalled;

    public ApplicationSecurityEnforcer(Class appMainClass, String securityPolicy, String appJarFile) {
        this(appMainClass, securityPolicy);
    }

    public ApplicationSecurityEnforcer(Class appMainClass, String securityPolicy) {
        this.appMainClass = appMainClass;
        this.securityPolicy = securityPolicy;
        this.appMainClassRelativeURL = appMainClass.getName().replace('.', '/') + ".class";
    }

    public void enforceSecurity(boolean enforce) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null && sm != this.lastSecurityManagerInstalled) {
            throw new SecurityException(Messages.getString(EXCEPTION_ALIEN_SECURITY_MANAGER));
        }
        if (enforce) {
            System.setSecurityManager(null);
            this.installSecurityManager();
        } else if (sm != null) {
            System.setSecurityManager(null);
            this.lastSecurityManagerInstalled = null;
        }
    }

    public URL getPolicyURL() {
        ClassLoader cl = this.appMainClass.getClassLoader();
        URL policyURL = cl.getResource(this.securityPolicy);
        if (policyURL == null) {
            throw new NullPointerException(Messages.formatMessage(EXCEPTION_NO_POLICY_FILE, new Object[]{this.securityPolicy}));
        }
        return policyURL;
    }

    public void installSecurityManager() {
        URL mainClassURL;
        Policy policy = Policy.getPolicy();
        BatikSecurityManager securityManager = new BatikSecurityManager();
        ClassLoader cl = this.appMainClass.getClassLoader();
        String securityPolicyProperty = System.getProperty(PROPERTY_JAVA_SECURITY_POLICY);
        if (securityPolicyProperty == null || securityPolicyProperty.equals("")) {
            URL policyURL = this.getPolicyURL();
            System.setProperty(PROPERTY_JAVA_SECURITY_POLICY, policyURL.toString());
        }
        if ((mainClassURL = cl.getResource(this.appMainClassRelativeURL)) == null) {
            throw new RuntimeException(this.appMainClassRelativeURL);
        }
        String expandedMainClassName = mainClassURL.toString();
        if (expandedMainClassName.startsWith(JAR_PROTOCOL)) {
            this.setJarBase(expandedMainClassName);
        } else {
            this.setDevBase(expandedMainClassName);
        }
        System.setSecurityManager(securityManager);
        this.lastSecurityManagerInstalled = securityManager;
        policy.refresh();
        if (securityPolicyProperty == null || securityPolicyProperty.equals("")) {
            System.setProperty(PROPERTY_JAVA_SECURITY_POLICY, "");
        }
    }

    private void setJarBase(String expandedMainClassName) {
        String curAppJarBase = System.getProperty(PROPERTY_APP_JAR_BASE);
        if (curAppJarBase == null) {
            int codeBaseEnd = (expandedMainClassName = expandedMainClassName.substring(JAR_PROTOCOL.length())).indexOf(JAR_URL_FILE_SEPARATOR + this.appMainClassRelativeURL);
            if (codeBaseEnd == -1) {
                throw new RuntimeException();
            }
            String appCodeBase = expandedMainClassName.substring(0, codeBaseEnd);
            appCodeBase = (codeBaseEnd = appCodeBase.lastIndexOf(47)) == -1 ? "" : appCodeBase.substring(0, codeBaseEnd);
            System.setProperty(PROPERTY_APP_JAR_BASE, appCodeBase);
        }
    }

    private void setDevBase(String expandedMainClassName) {
        String curAppCodeBase = System.getProperty(PROPERTY_APP_DEV_BASE);
        if (curAppCodeBase == null) {
            int codeBaseEnd = expandedMainClassName.indexOf(APP_MAIN_CLASS_DIR + this.appMainClassRelativeURL);
            if (codeBaseEnd == -1) {
                throw new RuntimeException();
            }
            String appCodeBase = expandedMainClassName.substring(0, codeBaseEnd);
            System.setProperty(PROPERTY_APP_DEV_BASE, appCodeBase);
        }
    }
}

