/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.util;

import java.util.HashMap;
import java.util.Map;

public final class Container {
    public static final int UNKNOWN = 0;
    public static final int TOMCAT = 1;
    public static final int RESIN = 2;
    public static final int ORION = 3;
    public static final int WEBLOGIC = 4;
    public static final int HPAS = 5;
    public static final int JRUN = 6;
    public static final int WEBSPHERE = 7;
    private static int result = -1;
    private static Map classMappings = null;

    public static int get() {
        if (result == -1) {
            String classMatch = Container.searchForClosestClass(classMappings);
            result = classMatch == null ? 0 : (Integer)classMappings.get(classMatch);
        }
        return result;
    }

    private static String searchForClosestClass(Map classMappings) {
        for (ClassLoader loader = Container.class.getClassLoader(); loader != null; loader = loader.getParent()) {
            for (String className : classMappings.keySet()) {
                try {
                    loader.loadClass(className);
                    return className;
                }
                catch (ClassNotFoundException e) {
                }
            }
        }
        return null;
    }

    static {
        classMappings = new HashMap(6);
        classMappings.put("org.apache.jasper.runtime.JspFactoryImpl", new Integer(1));
        classMappings.put("com.caucho.jsp.JspServlet", new Integer(2));
        classMappings.put("com.evermind.server.http.JSPServlet", new Integer(3));
        classMappings.put("weblogic.servlet.JSPServlet", new Integer(4));
        classMappings.put("com.hp.mwlabs.j2ee.containers.servlet.jsp.JspServlet", new Integer(5));
        classMappings.put("jrun.servlet.WebApplicationService", new Integer(6));
        classMappings.put("com.ibm.ws.webcontainer.jsp.servlet.JspServlet", new Integer(7));
    }
}

