/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.descriptor;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.Constants;
import org.apache.tomcat.util.descriptor.LocalResolver;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.res.StringManager;

public class DigesterFactory {
    private static final StringManager sm = StringManager.getManager((String)Constants.PACKAGE_NAME);
    private static final Class<ServletContext> CLASS_SERVLET_CONTEXT = ServletContext.class;
    private static final Class<?> CLASS_JSP_CONTEXT;
    public static final Map<String, String> SERVLET_API_PUBLIC_IDS;
    public static final Map<String, String> SERVLET_API_SYSTEM_IDS;

    private static void addSelf(Map<String, String> ids, String id) {
        String location = DigesterFactory.locationFor(id);
        if (location != null) {
            ids.put(id, location);
            ids.put(location, location);
        }
    }

    private static void add(Map<String, String> ids, String id, String location) {
        if (location != null) {
            ids.put(id, location);
            if (id.startsWith("http://")) {
                String httpsId = "https://" + id.substring(7);
                ids.put(httpsId, location);
            }
        }
    }

    private static String locationFor(String name) {
        URL location = CLASS_SERVLET_CONTEXT.getResource("resources/" + name);
        if (location == null && CLASS_JSP_CONTEXT != null) {
            location = CLASS_JSP_CONTEXT.getResource("resources/" + name);
        }
        if (location == null) {
            Log log = LogFactory.getLog(DigesterFactory.class);
            log.warn((Object)sm.getString("digesterFactory.missingSchema", new Object[]{name}));
            return null;
        }
        return location.toExternalForm();
    }

    public static Digester newDigester(boolean xmlValidation, boolean xmlNamespaceAware, RuleSet rule, boolean blockExternal) {
        Digester digester = new Digester();
        digester.setNamespaceAware(xmlNamespaceAware);
        digester.setValidating(xmlValidation);
        digester.setUseContextClassLoader(true);
        LocalResolver resolver = new LocalResolver(SERVLET_API_PUBLIC_IDS, SERVLET_API_SYSTEM_IDS, blockExternal);
        digester.setEntityResolver(resolver);
        if (rule != null) {
            digester.addRuleSet(rule);
        }
        return digester;
    }

    static {
        Class<?> jspContext = null;
        try {
            jspContext = Class.forName("javax.servlet.jsp.JspContext");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        CLASS_JSP_CONTEXT = jspContext;
        HashMap<String, String> publicIds = new HashMap<String, String>();
        HashMap<String, String> systemIds = new HashMap<String, String>();
        DigesterFactory.add(publicIds, "-//W3C//DTD XMLSCHEMA 200102//EN", DigesterFactory.locationFor("XMLSchema.dtd"));
        DigesterFactory.add(publicIds, "datatypes", DigesterFactory.locationFor("datatypes.dtd"));
        DigesterFactory.add(systemIds, "http://www.w3.org/2001/xml.xsd", DigesterFactory.locationFor("xml.xsd"));
        DigesterFactory.add(publicIds, "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", DigesterFactory.locationFor("web-app_2_2.dtd"));
        DigesterFactory.add(publicIds, "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.1//EN", DigesterFactory.locationFor("web-jsptaglibrary_1_1.dtd"));
        DigesterFactory.add(publicIds, "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", DigesterFactory.locationFor("web-app_2_3.dtd"));
        DigesterFactory.add(publicIds, "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN", DigesterFactory.locationFor("web-jsptaglibrary_1_2.dtd"));
        DigesterFactory.add(systemIds, "http://www.ibm.com/webservices/xsd/j2ee_web_services_1_1.xsd", DigesterFactory.locationFor("j2ee_web_services_1_1.xsd"));
        DigesterFactory.add(systemIds, "http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd", DigesterFactory.locationFor("j2ee_web_services_client_1_1.xsd"));
        DigesterFactory.add(systemIds, "http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd", DigesterFactory.locationFor("web-app_2_4.xsd"));
        DigesterFactory.add(systemIds, "http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd", DigesterFactory.locationFor("web-jsptaglibrary_2_0.xsd"));
        DigesterFactory.addSelf(systemIds, "j2ee_1_4.xsd");
        DigesterFactory.addSelf(systemIds, "jsp_2_0.xsd");
        DigesterFactory.add(systemIds, "http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd", DigesterFactory.locationFor("web-app_2_5.xsd"));
        DigesterFactory.add(systemIds, "http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd", DigesterFactory.locationFor("web-jsptaglibrary_2_1.xsd"));
        DigesterFactory.addSelf(systemIds, "javaee_5.xsd");
        DigesterFactory.addSelf(systemIds, "jsp_2_1.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_web_services_1_2.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_web_services_client_1_2.xsd");
        DigesterFactory.add(systemIds, "http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd", DigesterFactory.locationFor("web-app_3_0.xsd"));
        DigesterFactory.add(systemIds, "http://java.sun.com/xml/ns/javaee/web-fragment_3_0.xsd", DigesterFactory.locationFor("web-fragment_3_0.xsd"));
        DigesterFactory.addSelf(systemIds, "web-common_3_0.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_6.xsd");
        DigesterFactory.addSelf(systemIds, "jsp_2_2.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_web_services_1_3.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_web_services_client_1_3.xsd");
        DigesterFactory.add(systemIds, "http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd", DigesterFactory.locationFor("web-app_3_1.xsd"));
        DigesterFactory.add(systemIds, "http://xmlns.jcp.org/xml/ns/javaee/web-fragment_3_1.xsd", DigesterFactory.locationFor("web-fragment_3_1.xsd"));
        DigesterFactory.addSelf(systemIds, "web-common_3_1.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_7.xsd");
        DigesterFactory.addSelf(systemIds, "jsp_2_3.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_web_services_1_4.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_web_services_client_1_4.xsd");
        DigesterFactory.add(systemIds, "http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd", DigesterFactory.locationFor("web-app_4_0.xsd"));
        DigesterFactory.add(systemIds, "http://xmlns.jcp.org/xml/ns/javaee/web-fragment_4_0.xsd", DigesterFactory.locationFor("web-fragment_4_0.xsd"));
        DigesterFactory.addSelf(systemIds, "web-common_4_0.xsd");
        DigesterFactory.addSelf(systemIds, "javaee_8.xsd");
        SERVLET_API_PUBLIC_IDS = Collections.unmodifiableMap(publicIds);
        SERVLET_API_SYSTEM_IDS = Collections.unmodifiableMap(systemIds);
    }
}

