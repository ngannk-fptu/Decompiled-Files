/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper;

public class Constants {
    public static final String JSP_SERVLET_BASE = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.JSP_SERVLET_BASE", "org.apache.sling.scripting.jsp.jasper.runtime.HttpJspBase");
    public static final String SERVICE_METHOD_NAME = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.SERVICE_METHOD_NAME", "_jspService");
    public static final String SERVLET_CONTENT_TYPE = "text/html";
    public static final String[] STANDARD_IMPORTS = new String[]{"javax.servlet.*", "javax.servlet.http.*", "javax.servlet.jsp.*"};
    public static final String SERVLET_CLASSPATH = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.SERVLET_CLASSPATH", "org.apache.catalina.jsp_classpath");
    public static final String JSP_FILE = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.JSP_FILE", "org.apache.catalina.jsp_file");
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_TAG_BUFFER_SIZE = 512;
    public static final int MAX_POOL_SIZE = 5;
    public static final String PRECOMPILE = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.PRECOMPILE", "jsp_precompile");
    public static final String JSP_PACKAGE_NAME_PROPERTY_NAME = "org.apache.sling.scripting.jsp.jasper.Constants.JSP_PACKAGE_NAME";
    public static final String JSP_PACKAGE_NAME = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.JSP_PACKAGE_NAME", "org.apache.jsp");
    public static final String TAG_FILE_PACKAGE_NAME = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.TAG_FILE_PACKAGE_NAME", "org.apache.jsp.tag");
    public static final String INC_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String TMP_DIR = "javax.servlet.context.tempdir";
    public static final String ALT_DD_ATTR = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.ALT_DD_ATTR", "org.apache.catalina.deploy.alt_dd");
    public static final String TAGLIB_DTD_PUBLIC_ID_11 = "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.1//EN";
    public static final String TAGLIB_DTD_RESOURCE_PATH_11 = "/javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd";
    public static final String TAGLIB_DTD_PUBLIC_ID_12 = "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN";
    public static final String TAGLIB_DTD_RESOURCE_PATH_12 = "/javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd";
    public static final String WEBAPP_DTD_PUBLIC_ID_22 = "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN";
    public static final String WEBAPP_DTD_RESOURCE_PATH_22 = "/javax/servlet/resources/web-app_2_2.dtd";
    public static final String WEBAPP_DTD_PUBLIC_ID_23 = "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
    public static final String WEBAPP_DTD_RESOURCE_PATH_23 = "/javax/servlet/resources/web-app_2_3.dtd";
    public static final String[] CACHED_DTD_PUBLIC_IDS = new String[]{"-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.1//EN", "-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN", "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"};
    public static final String[] CACHED_DTD_RESOURCE_PATHS = new String[]{"/javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd", "/javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd", "/javax/servlet/resources/web-app_2_2.dtd", "/javax/servlet/resources/web-app_2_3.dtd"};
    public static final String NS_PLUGIN_URL = "http://java.sun.com/products/plugin/";
    public static final String IE_PLUGIN_URL = "http://java.sun.com/products/plugin/1.2.2/jinstall-1_2_2-win.cab#Version=1,2,2,0";
    public static final String TEMP_VARIABLE_NAME_PREFIX = System.getProperty("org.apache.sling.scripting.jsp.jasper.Constants.TEMP_VARIABLE_NAME_PREFIX", "_jspx_temp");
    public static final char ESC = '\u001b';
    public static final String ESCStr = "'\\u001b'";
    public static final boolean IS_SECURITY_ENABLED = System.getSecurityManager() != null;
    public static final String SESSION_PARAMETER_NAME = "jsessionid";
}

