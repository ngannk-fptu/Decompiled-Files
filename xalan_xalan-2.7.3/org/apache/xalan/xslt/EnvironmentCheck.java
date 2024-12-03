/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xslt;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.xalan.xslt.ObjectFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

public class EnvironmentCheck {
    public static final String ERROR = "ERROR.";
    public static final String WARNING = "WARNING.";
    public static final String ERROR_FOUND = "At least one error was found!";
    public static final String VERSION = "version.";
    public static final String FOUNDCLASSES = "foundclasses.";
    public static final String CLASS_PRESENT = "present-unknown-version";
    public static final String CLASS_NOTPRESENT = "not-present";
    public String[] jarNames = new String[]{"xalan.jar", "xalansamples.jar", "xalanj1compat.jar", "xalanservlet.jar", "serializer.jar", "xerces.jar", "xercesImpl.jar", "testxsl.jar", "crimson.jar", "lotusxsl.jar", "jaxp.jar", "parser.jar", "dom.jar", "sax.jar", "xml.jar", "xml-apis.jar", "xsltc.jar"};
    private static Hashtable jarVersions = new Hashtable();
    protected PrintWriter outWriter = new PrintWriter(System.out, true);

    public static void main(String[] args) {
        PrintWriter sendOutputTo = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; ++i) {
            if (!"-out".equalsIgnoreCase(args[i])) continue;
            if (++i < args.length) {
                try {
                    sendOutputTo = new PrintWriter(new FileWriter(args[i], true));
                }
                catch (Exception e) {
                    System.err.println("# WARNING: -out " + args[i] + " threw " + e.toString());
                }
                continue;
            }
            System.err.println("# WARNING: -out argument should have a filename, output sent to console");
        }
        EnvironmentCheck app = new EnvironmentCheck();
        app.checkEnvironment(sendOutputTo);
    }

    public boolean checkEnvironment(PrintWriter pw) {
        Hashtable hash;
        boolean environmentHasErrors;
        if (null != pw) {
            this.outWriter = pw;
        }
        if (environmentHasErrors = this.writeEnvironmentReport(hash = this.getEnvironmentHash())) {
            this.logMsg("# WARNING: Potential problems found in your environment!");
            this.logMsg("#    Check any 'ERROR' items above against the Xalan FAQs");
            this.logMsg("#    to correct potential problems with your classes/jars");
            this.logMsg("#    http://xml.apache.org/xalan-j/faq.html");
            if (null != this.outWriter) {
                this.outWriter.flush();
            }
            return false;
        }
        this.logMsg("# YAHOO! Your environment seems to be OK.");
        if (null != this.outWriter) {
            this.outWriter.flush();
        }
        return true;
    }

    public Hashtable getEnvironmentHash() {
        Hashtable hash = new Hashtable();
        this.checkJAXPVersion(hash);
        this.checkProcessorVersion(hash);
        this.checkParserVersion(hash);
        this.checkAntVersion(hash);
        this.checkDOMVersion(hash);
        this.checkSAXVersion(hash);
        this.checkSystemProperties(hash);
        return hash;
    }

    protected boolean writeEnvironmentReport(Hashtable h) {
        if (null == h) {
            this.logMsg("# ERROR: writeEnvironmentReport called with null Hashtable");
            return false;
        }
        boolean errors = false;
        this.logMsg("#---- BEGIN writeEnvironmentReport($Revision$): Useful stuff found: ----");
        Enumeration keys = h.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String keyStr = (String)key;
            try {
                if (keyStr.startsWith(FOUNDCLASSES)) {
                    Vector v = (Vector)h.get(keyStr);
                    errors |= this.logFoundJars(v, keyStr);
                    continue;
                }
                if (keyStr.startsWith(ERROR)) {
                    errors = true;
                }
                this.logMsg(keyStr + "=" + h.get(keyStr));
            }
            catch (Exception e) {
                this.logMsg("Reading-" + key + "= threw: " + e.toString());
            }
        }
        this.logMsg("#----- END writeEnvironmentReport: Useful properties found: -----");
        return errors;
    }

    protected boolean logFoundJars(Vector v, String desc) {
        if (null == v || v.size() < 1) {
            return false;
        }
        boolean errors = false;
        this.logMsg("#---- BEGIN Listing XML-related jars in: " + desc + " ----");
        for (int i = 0; i < v.size(); ++i) {
            Hashtable subhash = (Hashtable)v.elementAt(i);
            Enumeration keys = subhash.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                String keyStr = (String)key;
                try {
                    if (keyStr.startsWith(ERROR)) {
                        errors = true;
                    }
                    this.logMsg(keyStr + "=" + subhash.get(keyStr));
                }
                catch (Exception e) {
                    errors = true;
                    this.logMsg("Reading-" + key + "= threw: " + e.toString());
                }
            }
        }
        this.logMsg("#----- END Listing XML-related jars in: " + desc + " -----");
        return errors;
    }

    public void appendEnvironmentReport(Node container, Document factory, Hashtable h) {
        if (null == container || null == factory) {
            return;
        }
        try {
            Element envCheckNode = factory.createElement("EnvironmentCheck");
            envCheckNode.setAttribute("version", "$Revision$");
            container.appendChild(envCheckNode);
            if (null == h) {
                Element statusNode = factory.createElement("status");
                statusNode.setAttribute("result", "ERROR");
                statusNode.appendChild(factory.createTextNode("appendEnvironmentReport called with null Hashtable!"));
                envCheckNode.appendChild(statusNode);
                return;
            }
            boolean errors = false;
            Element hashNode = factory.createElement("environment");
            envCheckNode.appendChild(hashNode);
            Enumeration keys = h.keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                String keyStr = (String)key;
                try {
                    if (keyStr.startsWith(FOUNDCLASSES)) {
                        Vector v = (Vector)h.get(keyStr);
                        errors |= this.appendFoundJars(hashNode, factory, v, keyStr);
                        continue;
                    }
                    if (keyStr.startsWith(ERROR)) {
                        errors = true;
                    }
                    Element node = factory.createElement("item");
                    node.setAttribute("key", keyStr);
                    node.appendChild(factory.createTextNode((String)h.get(keyStr)));
                    hashNode.appendChild(node);
                }
                catch (Exception e) {
                    errors = true;
                    Element node = factory.createElement("item");
                    node.setAttribute("key", keyStr);
                    node.appendChild(factory.createTextNode("ERROR. Reading " + key + " threw: " + e.toString()));
                    hashNode.appendChild(node);
                }
            }
            Element statusNode = factory.createElement("status");
            statusNode.setAttribute("result", errors ? "ERROR" : "OK");
            envCheckNode.appendChild(statusNode);
        }
        catch (Exception e2) {
            System.err.println("appendEnvironmentReport threw: " + e2.toString());
            e2.printStackTrace();
        }
    }

    protected boolean appendFoundJars(Node container, Document factory, Vector v, String desc) {
        if (null == v || v.size() < 1) {
            return false;
        }
        boolean errors = false;
        for (int i = 0; i < v.size(); ++i) {
            Hashtable subhash = (Hashtable)v.elementAt(i);
            Enumeration keys = subhash.keys();
            while (keys.hasMoreElements()) {
                Element node;
                Object key = keys.nextElement();
                try {
                    String keyStr = (String)key;
                    if (keyStr.startsWith(ERROR)) {
                        errors = true;
                    }
                    node = factory.createElement("foundJar");
                    node.setAttribute("name", keyStr.substring(0, keyStr.indexOf("-")));
                    node.setAttribute("desc", keyStr.substring(keyStr.indexOf("-") + 1));
                    node.appendChild(factory.createTextNode((String)subhash.get(keyStr)));
                    container.appendChild(node);
                }
                catch (Exception e) {
                    errors = true;
                    node = factory.createElement("foundJar");
                    node.appendChild(factory.createTextNode("ERROR. Reading " + key + " threw: " + e.toString()));
                    container.appendChild(node);
                }
            }
        }
        return errors;
    }

    protected void checkSystemProperties(Hashtable h) {
        if (null == h) {
            h = new Hashtable<String, Object>();
        }
        try {
            String javaVersion = System.getProperty("java.version");
            h.put("java.version", javaVersion);
        }
        catch (SecurityException se) {
            h.put("java.version", "WARNING: SecurityException thrown accessing system version properties");
        }
        try {
            String othercp;
            String cp = System.getProperty("java.class.path");
            h.put("java.class.path", cp);
            Vector classpathJars = this.checkPathForJars(cp, this.jarNames);
            if (null != classpathJars) {
                h.put("foundclasses.java.class.path", classpathJars);
            }
            if (null != (othercp = System.getProperty("sun.boot.class.path"))) {
                h.put("sun.boot.class.path", othercp);
                classpathJars = this.checkPathForJars(othercp, this.jarNames);
                if (null != classpathJars) {
                    h.put("foundclasses.sun.boot.class.path", classpathJars);
                }
            }
            if (null != (othercp = System.getProperty("java.ext.dirs"))) {
                h.put("java.ext.dirs", othercp);
                classpathJars = this.checkPathForJars(othercp, this.jarNames);
                if (null != classpathJars) {
                    h.put("foundclasses.java.ext.dirs", classpathJars);
                }
            }
        }
        catch (SecurityException se2) {
            h.put("java.class.path", "WARNING: SecurityException thrown accessing system classpath properties");
        }
    }

    protected Vector checkPathForJars(String cp, String[] jars) {
        if (null == cp || null == jars || 0 == cp.length() || 0 == jars.length) {
            return null;
        }
        Vector v = new Vector();
        StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
        while (st.hasMoreTokens()) {
            String filename = st.nextToken();
            for (int i = 0; i < jars.length; ++i) {
                Hashtable<String, String> h;
                if (filename.indexOf(jars[i]) <= -1) continue;
                File f = new File(filename);
                if (f.exists()) {
                    try {
                        h = new Hashtable(2);
                        h.put(jars[i] + "-path", f.getAbsolutePath());
                        if (!"xalan.jar".equalsIgnoreCase(jars[i])) {
                            h.put(jars[i] + "-apparent.version", this.getApparentVersion(jars[i], f.length()));
                        }
                        v.addElement(h);
                    }
                    catch (Exception h2) {}
                    continue;
                }
                h = new Hashtable<String, String>(2);
                h.put(jars[i] + "-path", "WARNING. Classpath entry: " + filename + " does not exist");
                h.put(jars[i] + "-apparent.version", CLASS_NOTPRESENT);
                v.addElement(h);
            }
        }
        return v;
    }

    protected String getApparentVersion(String jarName, long jarSize) {
        String foundSize = (String)jarVersions.get(new Long(jarSize));
        if (null != foundSize && foundSize.startsWith(jarName)) {
            return foundSize;
        }
        if ("xerces.jar".equalsIgnoreCase(jarName) || "xercesImpl.jar".equalsIgnoreCase(jarName)) {
            return jarName + " " + WARNING + CLASS_PRESENT;
        }
        return jarName + " " + CLASS_PRESENT;
    }

    protected void checkJAXPVersion(Hashtable h) {
        if (null == h) {
            h = new Hashtable<String, String>();
        }
        Class[] noArgs = new Class[]{};
        Class clazz = null;
        try {
            String JAXP1_CLASS = "javax.xml.parsers.DocumentBuilder";
            String JAXP11_METHOD = "getDOMImplementation";
            clazz = ObjectFactory.findProviderClass("javax.xml.parsers.DocumentBuilder", ObjectFactory.findClassLoader(), true);
            Method method = clazz.getMethod("getDOMImplementation", noArgs);
            h.put("version.JAXP", "1.1 or higher");
        }
        catch (Exception e) {
            if (null != clazz) {
                h.put("ERROR.version.JAXP", "1.0.1");
                h.put(ERROR, ERROR_FOUND);
            }
            h.put("ERROR.version.JAXP", CLASS_NOTPRESENT);
            h.put(ERROR, ERROR_FOUND);
        }
    }

    protected void checkProcessorVersion(Hashtable h) {
        Field f;
        StringBuffer buf;
        Class clazz;
        if (null == h) {
            h = new Hashtable<String, String>();
        }
        try {
            String XALAN1_VERSION_CLASS = "org.apache.xalan.xslt.XSLProcessorVersion";
            clazz = ObjectFactory.findProviderClass("org.apache.xalan.xslt.XSLProcessorVersion", ObjectFactory.findClassLoader(), true);
            buf = new StringBuffer();
            f = clazz.getField("PRODUCT");
            buf.append(f.get(null));
            buf.append(';');
            f = clazz.getField("LANGUAGE");
            buf.append(f.get(null));
            buf.append(';');
            f = clazz.getField("S_VERSION");
            buf.append(f.get(null));
            buf.append(';');
            h.put("version.xalan1", buf.toString());
        }
        catch (Exception e1) {
            h.put("version.xalan1", CLASS_NOTPRESENT);
        }
        try {
            String XALAN2_VERSION_CLASS = "org.apache.xalan.processor.XSLProcessorVersion";
            clazz = ObjectFactory.findProviderClass("org.apache.xalan.processor.XSLProcessorVersion", ObjectFactory.findClassLoader(), true);
            buf = new StringBuffer();
            f = clazz.getField("S_VERSION");
            buf.append(f.get(null));
            h.put("version.xalan2x", buf.toString());
        }
        catch (Exception e2) {
            h.put("version.xalan2x", CLASS_NOTPRESENT);
        }
        try {
            String XALAN2_2_VERSION_CLASS = "org.apache.xalan.Version";
            String XALAN2_2_VERSION_METHOD = "getVersion";
            Class[] noArgs = new Class[]{};
            Class clazz2 = ObjectFactory.findProviderClass("org.apache.xalan.Version", ObjectFactory.findClassLoader(), true);
            Method method = clazz2.getMethod("getVersion", noArgs);
            Object returnValue = method.invoke(null, new Object[0]);
            h.put("version.xalan2_2", (String)returnValue);
        }
        catch (Exception e2) {
            h.put("version.xalan2_2", CLASS_NOTPRESENT);
        }
    }

    protected void checkParserVersion(Hashtable h) {
        String parserVersion;
        Field f;
        Class clazz;
        if (null == h) {
            h = new Hashtable<String, String>();
        }
        try {
            String XERCES1_VERSION_CLASS = "org.apache.xerces.framework.Version";
            clazz = ObjectFactory.findProviderClass("org.apache.xerces.framework.Version", ObjectFactory.findClassLoader(), true);
            f = clazz.getField("fVersion");
            parserVersion = (String)f.get(null);
            h.put("version.xerces1", parserVersion);
        }
        catch (Exception e) {
            h.put("version.xerces1", CLASS_NOTPRESENT);
        }
        try {
            String XERCES2_VERSION_CLASS = "org.apache.xerces.impl.Version";
            clazz = ObjectFactory.findProviderClass("org.apache.xerces.impl.Version", ObjectFactory.findClassLoader(), true);
            f = clazz.getField("fVersion");
            parserVersion = (String)f.get(null);
            h.put("version.xerces2", parserVersion);
        }
        catch (Exception e) {
            h.put("version.xerces2", CLASS_NOTPRESENT);
        }
        try {
            String CRIMSON_CLASS = "org.apache.crimson.parser.Parser2";
            clazz = ObjectFactory.findProviderClass("org.apache.crimson.parser.Parser2", ObjectFactory.findClassLoader(), true);
            h.put("version.crimson", CLASS_PRESENT);
        }
        catch (Exception e) {
            h.put("version.crimson", CLASS_NOTPRESENT);
        }
    }

    protected void checkAntVersion(Hashtable h) {
        if (null == h) {
            h = new Hashtable<String, String>();
        }
        try {
            String ANT_VERSION_CLASS = "org.apache.tools.ant.Main";
            String ANT_VERSION_METHOD = "getAntVersion";
            Class[] noArgs = new Class[]{};
            Class clazz = ObjectFactory.findProviderClass("org.apache.tools.ant.Main", ObjectFactory.findClassLoader(), true);
            Method method = clazz.getMethod("getAntVersion", noArgs);
            Object returnValue = method.invoke(null, new Object[0]);
            h.put("version.ant", (String)returnValue);
        }
        catch (Exception e) {
            h.put("version.ant", CLASS_NOTPRESENT);
        }
    }

    protected void checkDOMVersion(Hashtable h) {
        if (null == h) {
            h = new Hashtable<String, String>();
        }
        String DOM_LEVEL2_CLASS = "org.w3c.dom.Document";
        String DOM_LEVEL2_METHOD = "createElementNS";
        String DOM_LEVEL2WD_CLASS = "org.w3c.dom.Node";
        String DOM_LEVEL2WD_METHOD = "supported";
        String DOM_LEVEL2FD_CLASS = "org.w3c.dom.Node";
        String DOM_LEVEL2FD_METHOD = "isSupported";
        Class[] twoStringArgs = new Class[]{String.class, String.class};
        try {
            Class clazz = ObjectFactory.findProviderClass("org.w3c.dom.Document", ObjectFactory.findClassLoader(), true);
            Method method = clazz.getMethod("createElementNS", twoStringArgs);
            h.put("version.DOM", "2.0");
            try {
                clazz = ObjectFactory.findProviderClass("org.w3c.dom.Node", ObjectFactory.findClassLoader(), true);
                method = clazz.getMethod("supported", twoStringArgs);
                h.put("ERROR.version.DOM.draftlevel", "2.0wd");
                h.put(ERROR, ERROR_FOUND);
            }
            catch (Exception e2) {
                try {
                    clazz = ObjectFactory.findProviderClass("org.w3c.dom.Node", ObjectFactory.findClassLoader(), true);
                    method = clazz.getMethod("isSupported", twoStringArgs);
                    h.put("version.DOM.draftlevel", "2.0fd");
                }
                catch (Exception e3) {
                    h.put("ERROR.version.DOM.draftlevel", "2.0unknown");
                    h.put(ERROR, ERROR_FOUND);
                }
            }
        }
        catch (Exception e) {
            h.put("ERROR.version.DOM", "ERROR attempting to load DOM level 2 class: " + e.toString());
            h.put(ERROR, ERROR_FOUND);
        }
    }

    protected void checkSAXVersion(Hashtable h) {
        if (null == h) {
            h = new Hashtable<String, String>();
        }
        String SAX_VERSION1_CLASS = "org.xml.sax.Parser";
        String SAX_VERSION1_METHOD = "parse";
        String SAX_VERSION2_CLASS = "org.xml.sax.XMLReader";
        String SAX_VERSION2_METHOD = "parse";
        String SAX_VERSION2BETA_CLASSNF = "org.xml.sax.helpers.AttributesImpl";
        String SAX_VERSION2BETA_METHODNF = "setAttributes";
        Class[] oneStringArg = new Class[]{String.class};
        Class[] attributesArg = new Class[]{Attributes.class};
        try {
            Class clazz = ObjectFactory.findProviderClass("org.xml.sax.helpers.AttributesImpl", ObjectFactory.findClassLoader(), true);
            Method method = clazz.getMethod("setAttributes", attributesArg);
            h.put("version.SAX", "2.0");
        }
        catch (Exception e) {
            h.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + e.toString());
            h.put(ERROR, ERROR_FOUND);
            try {
                Class clazz = ObjectFactory.findProviderClass("org.xml.sax.XMLReader", ObjectFactory.findClassLoader(), true);
                Method method = clazz.getMethod("parse", oneStringArg);
                h.put("version.SAX-backlevel", "2.0beta2-or-earlier");
            }
            catch (Exception e2) {
                h.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + e.toString());
                h.put(ERROR, ERROR_FOUND);
                try {
                    Class clazz = ObjectFactory.findProviderClass("org.xml.sax.Parser", ObjectFactory.findClassLoader(), true);
                    Method method = clazz.getMethod("parse", oneStringArg);
                    h.put("version.SAX-backlevel", "1.0");
                }
                catch (Exception e3) {
                    h.put("ERROR.version.SAX-backlevel", "ERROR attempting to load SAX version 1 class: " + e3.toString());
                }
            }
        }
    }

    protected void logMsg(String s) {
        this.outWriter.println(s);
    }

    static {
        jarVersions.put(new Long(857192L), "xalan.jar from xalan-j_1_1");
        jarVersions.put(new Long(440237L), "xalan.jar from xalan-j_1_2");
        jarVersions.put(new Long(436094L), "xalan.jar from xalan-j_1_2_1");
        jarVersions.put(new Long(426249L), "xalan.jar from xalan-j_1_2_2");
        jarVersions.put(new Long(702536L), "xalan.jar from xalan-j_2_0_0");
        jarVersions.put(new Long(720930L), "xalan.jar from xalan-j_2_0_1");
        jarVersions.put(new Long(732330L), "xalan.jar from xalan-j_2_1_0");
        jarVersions.put(new Long(872241L), "xalan.jar from xalan-j_2_2_D10");
        jarVersions.put(new Long(882739L), "xalan.jar from xalan-j_2_2_D11");
        jarVersions.put(new Long(923866L), "xalan.jar from xalan-j_2_2_0");
        jarVersions.put(new Long(905872L), "xalan.jar from xalan-j_2_3_D1");
        jarVersions.put(new Long(906122L), "xalan.jar from xalan-j_2_3_0");
        jarVersions.put(new Long(906248L), "xalan.jar from xalan-j_2_3_1");
        jarVersions.put(new Long(983377L), "xalan.jar from xalan-j_2_4_D1");
        jarVersions.put(new Long(997276L), "xalan.jar from xalan-j_2_4_0");
        jarVersions.put(new Long(1031036L), "xalan.jar from xalan-j_2_4_1");
        jarVersions.put(new Long(596540L), "xsltc.jar from xalan-j_2_2_0");
        jarVersions.put(new Long(590247L), "xsltc.jar from xalan-j_2_3_D1");
        jarVersions.put(new Long(589914L), "xsltc.jar from xalan-j_2_3_0");
        jarVersions.put(new Long(589915L), "xsltc.jar from xalan-j_2_3_1");
        jarVersions.put(new Long(1306667L), "xsltc.jar from xalan-j_2_4_D1");
        jarVersions.put(new Long(1328227L), "xsltc.jar from xalan-j_2_4_0");
        jarVersions.put(new Long(1344009L), "xsltc.jar from xalan-j_2_4_1");
        jarVersions.put(new Long(1348361L), "xsltc.jar from xalan-j_2_5_D1");
        jarVersions.put(new Long(1268634L), "xsltc.jar-bundled from xalan-j_2_3_0");
        jarVersions.put(new Long(100196L), "xml-apis.jar from xalan-j_2_2_0 or xalan-j_2_3_D1");
        jarVersions.put(new Long(108484L), "xml-apis.jar from xalan-j_2_3_0, or xalan-j_2_3_1 from xml-commons-1.0.b2");
        jarVersions.put(new Long(109049L), "xml-apis.jar from xalan-j_2_4_0 from xml-commons RIVERCOURT1 branch");
        jarVersions.put(new Long(113749L), "xml-apis.jar from xalan-j_2_4_1 from factoryfinder-build of xml-commons RIVERCOURT1");
        jarVersions.put(new Long(124704L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons");
        jarVersions.put(new Long(124724L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons, tag: xml-commons-external_1_2_01");
        jarVersions.put(new Long(194205L), "xml-apis.jar from head branch of xml-commons, tag: xml-commons-external_1_3_02");
        jarVersions.put(new Long(424490L), "xalan.jar from Xerces Tools releases - ERROR:DO NOT USE!");
        jarVersions.put(new Long(1591855L), "xerces.jar from xalan-j_1_1 from xerces-1...");
        jarVersions.put(new Long(1498679L), "xerces.jar from xalan-j_1_2 from xerces-1_2_0.bin");
        jarVersions.put(new Long(1484896L), "xerces.jar from xalan-j_1_2_1 from xerces-1_2_1.bin");
        jarVersions.put(new Long(804460L), "xerces.jar from xalan-j_1_2_2 from xerces-1_2_2.bin");
        jarVersions.put(new Long(1499244L), "xerces.jar from xalan-j_2_0_0 from xerces-1_2_3.bin");
        jarVersions.put(new Long(1605266L), "xerces.jar from xalan-j_2_0_1 from xerces-1_3_0.bin");
        jarVersions.put(new Long(904030L), "xerces.jar from xalan-j_2_1_0 from xerces-1_4.bin");
        jarVersions.put(new Long(904030L), "xerces.jar from xerces-1_4_0.bin");
        jarVersions.put(new Long(1802885L), "xerces.jar from xerces-1_4_2.bin");
        jarVersions.put(new Long(1734594L), "xerces.jar from Xerces-J-bin.2.0.0.beta3");
        jarVersions.put(new Long(1808883L), "xerces.jar from xalan-j_2_2_D10,D11,D12 or xerces-1_4_3.bin");
        jarVersions.put(new Long(1812019L), "xerces.jar from xalan-j_2_2_0");
        jarVersions.put(new Long(1720292L), "xercesImpl.jar from xalan-j_2_3_D1");
        jarVersions.put(new Long(1730053L), "xercesImpl.jar from xalan-j_2_3_0 or xalan-j_2_3_1 from xerces-2_0_0");
        jarVersions.put(new Long(1728861L), "xercesImpl.jar from xalan-j_2_4_D1 from xerces-2_0_1");
        jarVersions.put(new Long(972027L), "xercesImpl.jar from xalan-j_2_4_0 from xerces-2_1");
        jarVersions.put(new Long(831587L), "xercesImpl.jar from xalan-j_2_4_1 from xerces-2_2");
        jarVersions.put(new Long(891817L), "xercesImpl.jar from xalan-j_2_5_D1 from xerces-2_3");
        jarVersions.put(new Long(895924L), "xercesImpl.jar from xerces-2_4");
        jarVersions.put(new Long(1010806L), "xercesImpl.jar from Xerces-J-bin.2.6.2");
        jarVersions.put(new Long(1203860L), "xercesImpl.jar from Xerces-J-bin.2.7.1");
        jarVersions.put(new Long(37485L), "xalanj1compat.jar from xalan-j_2_0_0");
        jarVersions.put(new Long(38100L), "xalanj1compat.jar from xalan-j_2_0_1");
        jarVersions.put(new Long(18779L), "xalanservlet.jar from xalan-j_2_0_0");
        jarVersions.put(new Long(21453L), "xalanservlet.jar from xalan-j_2_0_1");
        jarVersions.put(new Long(24826L), "xalanservlet.jar from xalan-j_2_3_1 or xalan-j_2_4_1");
        jarVersions.put(new Long(24831L), "xalanservlet.jar from xalan-j_2_4_1");
        jarVersions.put(new Long(5618L), "jaxp.jar from jaxp1.0.1");
        jarVersions.put(new Long(136133L), "parser.jar from jaxp1.0.1");
        jarVersions.put(new Long(28404L), "jaxp.jar from jaxp-1.1");
        jarVersions.put(new Long(187162L), "crimson.jar from jaxp-1.1");
        jarVersions.put(new Long(801714L), "xalan.jar from jaxp-1.1");
        jarVersions.put(new Long(196399L), "crimson.jar from crimson-1.1.1");
        jarVersions.put(new Long(33323L), "jaxp.jar from crimson-1.1.1 or jakarta-ant-1.4.1b1");
        jarVersions.put(new Long(152717L), "crimson.jar from crimson-1.1.2beta2");
        jarVersions.put(new Long(88143L), "xml-apis.jar from crimson-1.1.2beta2");
        jarVersions.put(new Long(206384L), "crimson.jar from crimson-1.1.3 or jakarta-ant-1.4.1b1");
        jarVersions.put(new Long(136198L), "parser.jar from jakarta-ant-1.3 or 1.2");
        jarVersions.put(new Long(5537L), "jaxp.jar from jakarta-ant-1.3 or 1.2");
    }
}

