/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;

public class HappyClient {
    PrintStream out;

    public HappyClient(PrintStream out) {
        this.out = out;
    }

    Class classExists(String classname) {
        try {
            return Class.forName(classname);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    boolean resourceExists(String resource) {
        boolean found;
        InputStream instream = ClassUtils.getResourceAsStream(this.getClass(), resource);
        boolean bl = found = instream != null;
        if (instream != null) {
            try {
                instream.close();
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        return found;
    }

    int probeClass(String category, String classname, String jarFile, String description, String errorText, String homePage) throws IOException {
        String url = "";
        if (homePage != null) {
            url = Messages.getMessage("happyClientHomepage", homePage);
        }
        String errorLine = "";
        if (errorText != null) {
            errorLine = Messages.getMessage(errorText);
        }
        try {
            Class clazz = this.classExists(classname);
            if (clazz == null) {
                String text = Messages.getMessage("happyClientMissingClass", category, classname, jarFile);
                this.out.println(text);
                this.out.println(url);
                return 1;
            }
            String location = this.getLocation(clazz);
            String text = location == null ? Messages.getMessage("happyClientFoundDescriptionClass", description, classname) : Messages.getMessage("happyClientFoundDescriptionClassLocation", description, classname, location);
            this.out.println(text);
            return 0;
        }
        catch (NoClassDefFoundError ncdfe) {
            this.out.println(Messages.getMessage("happyClientNoDependency", category, classname, jarFile));
            this.out.println(errorLine);
            this.out.println(url);
            this.out.println(ncdfe.getMessage());
            return 1;
        }
    }

    String getLocation(Class clazz) {
        try {
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            String location = url.toString();
            if (location.startsWith("jar")) {
                url = ((JarURLConnection)url.openConnection()).getJarFileURL();
                location = url.toString();
            }
            if (location.startsWith("file")) {
                File file = new File(url.getFile());
                return file.getAbsolutePath();
            }
            return url.toString();
        }
        catch (Throwable throwable) {
            return Messages.getMessage("happyClientUnknownLocation");
        }
    }

    int needClass(String classname, String jarFile, String description, String errorText, String homePage) throws IOException {
        return this.probeClass(Messages.getMessage("happyClientError"), classname, jarFile, description, errorText, homePage);
    }

    int wantClass(String classname, String jarFile, String description, String errorText, String homePage) throws IOException {
        return this.probeClass(Messages.getMessage("happyClientWarning"), classname, jarFile, description, errorText, homePage);
    }

    int wantResource(String resource, String errorText) throws Exception {
        if (!this.resourceExists(resource)) {
            this.out.println(Messages.getMessage("happyClientNoResource", resource));
            this.out.println(errorText);
            return 0;
        }
        this.out.println(Messages.getMessage("happyClientFoundResource", resource));
        return 1;
    }

    private String getParserName() {
        SAXParser saxParser = this.getSAXParser();
        if (saxParser == null) {
            return Messages.getMessage("happyClientNoParser");
        }
        String saxParserName = saxParser.getClass().getName();
        return saxParserName;
    }

    private SAXParser getSAXParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        if (saxParserFactory == null) {
            return null;
        }
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return saxParser;
    }

    private String getParserLocation() {
        SAXParser saxParser = this.getSAXParser();
        if (saxParser == null) {
            return null;
        }
        String location = this.getLocation(saxParser.getClass());
        return location;
    }

    public int getJavaVersionNumber() {
        int javaVersionNumber = 10;
        try {
            Class.forName("java.lang.Void");
            ++javaVersionNumber;
            Class.forName("java.lang.ThreadLocal");
            ++javaVersionNumber;
            Class.forName("java.lang.StrictMath");
            ++javaVersionNumber;
            Class.forName("java.lang.CharSequence");
            ++javaVersionNumber;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return javaVersionNumber;
    }

    private void title(String title) {
        this.out.println();
        String message = Messages.getMessage(title);
        this.out.println(message);
        for (int i = 0; i < message.length(); ++i) {
            this.out.print("=");
        }
        this.out.println();
    }

    public boolean verifyClientIsHappy(boolean warningsAsErrors) throws IOException {
        boolean happy;
        int needed = 0;
        int wanted = 0;
        this.out.println();
        this.title("happyClientTitle");
        this.title("happyClientNeeded");
        needed = this.needClass("javax.xml.soap.SOAPMessage", "saaj.jar", "SAAJ", "happyClientNoAxis", "http://xml.apache.org/axis/");
        needed += this.needClass("javax.xml.rpc.Service", "jaxrpc.jar", "JAX-RPC", "happyClientNoAxis", "http://xml.apache.org/axis/");
        needed += this.needClass("org.apache.commons.discovery.Resource", "commons-discovery.jar", "Jakarta-Commons Discovery", "happyClientNoAxis", "http://jakarta.apache.org/commons/discovery.html");
        needed += this.needClass("org.apache.commons.logging.Log", "commons-logging.jar", "Jakarta-Commons Logging", "happyClientNoAxis", "http://jakarta.apache.org/commons/logging.html");
        needed += this.needClass("org.apache.log4j.Layout", "log4j-1.2.4.jar", "Log4j", "happyClientNoLog4J", "http://jakarta.apache.org/log4j");
        needed += this.needClass("com.ibm.wsdl.factory.WSDLFactoryImpl", "wsdl4j.jar", "WSDL4Java", "happyClientNoAxis", null);
        needed += this.needClass("javax.xml.parsers.SAXParserFactory", "xerces.jar", "JAXP", "happyClientNoAxis", "http://xml.apache.org/xerces-j/");
        this.title("happyClientOptional");
        wanted += this.wantClass("javax.mail.internet.MimeMessage", "mail.jar", "Mail", "happyClientNoAttachments", "http://java.sun.com/products/javamail/");
        wanted += this.wantClass("javax.activation.DataHandler", "activation.jar", "Activation", "happyClientNoAttachments", "http://java.sun.com/products/javabeans/glasgow/jaf.html");
        wanted += this.wantClass("org.apache.xml.security.Init", "xmlsec.jar", "XML Security", "happyClientNoSecurity", "http://xml.apache.org/security/");
        wanted += this.wantClass("javax.net.ssl.SSLSocketFactory", Messages.getMessage("happyClientJSSEsources"), "Java Secure Socket Extension", "happyClientNoHTTPS", "http://java.sun.com/products/jsse/");
        int warningMessages = 0;
        String xmlParser = this.getParserName();
        String xmlParserLocation = this.getParserLocation();
        this.out.println(Messages.getMessage("happyClientXMLinfo", xmlParser, xmlParserLocation));
        if (xmlParser.indexOf("xerces") <= 0) {
            ++warningMessages;
            this.out.println();
            this.out.println(Messages.getMessage("happyClientRecommendXerces"));
        }
        if (this.getJavaVersionNumber() < 13) {
            ++warningMessages;
            this.out.println();
            this.out.println(Messages.getMessage("happyClientUnsupportedJVM"));
        }
        this.title("happyClientSummary");
        if (needed == 0) {
            this.out.println(Messages.getMessage("happyClientCorePresent"));
            happy = true;
        } else {
            happy = false;
            this.out.println(Messages.getMessage("happyClientCoreMissing", Integer.toString(needed)));
        }
        if (wanted > 0) {
            this.out.println();
            this.out.println(Messages.getMessage("happyClientOptionalMissing", Integer.toString(wanted)));
            this.out.println(Messages.getMessage("happyClientOptionalOK"));
            if (warningsAsErrors) {
                happy = false;
            }
        } else {
            this.out.println(Messages.getMessage("happyClientOptionalPresent"));
        }
        if (warningMessages > 0) {
            this.out.println(Messages.getMessage("happyClientWarningMessageCount", Integer.toString(warningMessages)));
            if (warningsAsErrors) {
                happy = false;
            }
        }
        return happy;
    }

    public static void main(String[] args) {
        boolean isHappy = HappyClient.isClientHappy(args);
        System.exit(isHappy ? 0 : -1);
    }

    private static boolean isClientHappy(String[] args) {
        boolean isHappy;
        HappyClient happy = new HappyClient(System.out);
        int missing = 0;
        try {
            isHappy = happy.verifyClientIsHappy(false);
            for (int i = 0; i < args.length; ++i) {
                missing += happy.probeClass("argument", args[i], null, null, null, null);
            }
            if (missing > 0) {
                isHappy = false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            isHappy = false;
        }
        return isHappy;
    }
}

