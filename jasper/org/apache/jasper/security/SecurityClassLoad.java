/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.jasper.security;

import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.security.SecurityUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class SecurityClassLoad {
    public static void securityClassLoad(ClassLoader loader) {
        if (System.getSecurityManager() == null) {
            return;
        }
        String basePackage = "org.apache.jasper.";
        try {
            loader.loadClass("org.apache.jasper.compiler.EncodingDetector");
            loader.loadClass("org.apache.jasper.runtime.JspContextWrapper");
            loader.loadClass("org.apache.jasper.runtime.JspFactoryImpl$PrivilegedGetPageContext");
            loader.loadClass("org.apache.jasper.runtime.JspFactoryImpl$PrivilegedReleasePageContext");
            loader.loadClass("org.apache.jasper.runtime.JspFragmentHelper");
            Class<?> clazz = loader.loadClass("org.apache.jasper.runtime.JspRuntimeLibrary");
            clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            loader.loadClass("org.apache.jasper.runtime.PageContextImpl");
            loader.loadClass("org.apache.jasper.runtime.ProtectedFunctionMapper");
            loader.loadClass("org.apache.jasper.runtime.ServletResponseWrapperInclude");
            loader.loadClass("org.apache.jasper.runtime.TagHandlerPool");
            SecurityUtil.isPackageProtectionEnabled();
            loader.loadClass("org.apache.jasper.servlet.JspServletWrapper");
        }
        catch (Exception ex) {
            Log log = LogFactory.getLog(SecurityClassLoad.class);
            log.error((Object)Localizer.getMessage("jsp.error.securityPreload"), (Throwable)ex);
        }
    }
}

