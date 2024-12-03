/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.security;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class SecurityClassLoad {
    private static Log log = LogFactory.getLog(SecurityClassLoad.class);

    public static void securityClassLoad(ClassLoader loader) {
        if (System.getSecurityManager() == null) {
            return;
        }
        String basePackage = "org.apache.sling.scripting.jsp.jasper.";
        try {
            loader.loadClass(basePackage + "runtime.JspFactoryImpl$PrivilegedGetPageContext");
            loader.loadClass(basePackage + "runtime.JspFactoryImpl$PrivilegedReleasePageContext");
            loader.loadClass(basePackage + "runtime.JspRuntimeLibrary");
            loader.loadClass(basePackage + "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
            loader.loadClass(basePackage + "runtime.ServletResponseWrapperInclude");
            loader.loadClass(basePackage + "runtime.TagHandlerPool");
            loader.loadClass(basePackage + "runtime.JspFragmentHelper");
            loader.loadClass(basePackage + "runtime.ProtectedFunctionMapper");
            loader.loadClass(basePackage + "runtime.ProtectedFunctionMapper$1");
            loader.loadClass(basePackage + "runtime.ProtectedFunctionMapper$2");
            loader.loadClass(basePackage + "runtime.ProtectedFunctionMapper$3");
            loader.loadClass(basePackage + "runtime.ProtectedFunctionMapper$4");
            loader.loadClass(basePackage + "runtime.PageContextImpl");
            loader.loadClass(basePackage + "runtime.PageContextImpl$1");
            loader.loadClass(basePackage + "runtime.PageContextImpl$2");
            loader.loadClass(basePackage + "runtime.PageContextImpl$3");
            loader.loadClass(basePackage + "runtime.PageContextImpl$4");
            loader.loadClass(basePackage + "runtime.PageContextImpl$5");
            loader.loadClass(basePackage + "runtime.PageContextImpl$6");
            loader.loadClass(basePackage + "runtime.PageContextImpl$7");
            loader.loadClass(basePackage + "runtime.PageContextImpl$8");
            loader.loadClass(basePackage + "runtime.PageContextImpl$9");
            loader.loadClass(basePackage + "runtime.PageContextImpl$10");
            loader.loadClass(basePackage + "runtime.PageContextImpl$11");
            loader.loadClass(basePackage + "runtime.PageContextImpl$12");
            loader.loadClass(basePackage + "runtime.PageContextImpl$13");
            loader.loadClass(basePackage + "runtime.JspContextWrapper");
            loader.loadClass(basePackage + "servlet.JspServletWrapper");
            loader.loadClass(basePackage + "runtime.JspWriterImpl$1");
        }
        catch (ClassNotFoundException ex) {
            log.error("SecurityClassLoad", ex);
        }
    }
}

