/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Launcher
 */
package org.apache.tools.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.launch.Launcher;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JAXPUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.java15.ProxyDiagnostics;
import org.xml.sax.XMLReader;

public final class Diagnostics {
    private static final int BIG_DRIFT_LIMIT = 10000;
    private static final int TEST_FILE_SIZE = 32;
    private static final int KILOBYTE = 1024;
    private static final int SECONDS_PER_MILLISECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    protected static final String ERROR_PROPERTY_ACCESS_BLOCKED = "Access to this property blocked by a security manager";

    private Diagnostics() {
    }

    @Deprecated
    public static boolean isOptionalAvailable() {
        return true;
    }

    @Deprecated
    public static void validateVersion() throws BuildException {
    }

    public static File[] listLibraries() {
        String home = System.getProperty("ant.home");
        if (home == null) {
            return null;
        }
        return Diagnostics.listJarFiles(new File(home, "lib"));
    }

    private static File[] listJarFiles(File libDir) {
        return libDir.listFiles((dir, name) -> name.endsWith(".jar"));
    }

    public static void main(String[] args) {
        Diagnostics.doReport(System.out);
    }

    private static String getImplementationVersion(Class<?> clazz) {
        return clazz.getPackage().getImplementationVersion();
    }

    private static URL getClassLocation(Class<?> clazz) {
        if (clazz.getProtectionDomain().getCodeSource() == null) {
            return null;
        }
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    private static String getXMLParserName() {
        SAXParser saxParser = Diagnostics.getSAXParser();
        if (saxParser == null) {
            return "Could not create an XML Parser";
        }
        return saxParser.getClass().getName();
    }

    private static String getXSLTProcessorName() {
        Transformer transformer = Diagnostics.getXSLTProcessor();
        if (transformer == null) {
            return "Could not create an XSLT Processor";
        }
        return transformer.getClass().getName();
    }

    private static SAXParser getSAXParser() {
        SAXParserFactory saxParserFactory = null;
        try {
            saxParserFactory = SAXParserFactory.newInstance();
        }
        catch (Exception e) {
            Diagnostics.ignoreThrowable(e);
            return null;
        }
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        }
        catch (Exception e) {
            Diagnostics.ignoreThrowable(e);
        }
        return saxParser;
    }

    private static Transformer getXSLTProcessor() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        if (transformerFactory != null) {
            try {
                return transformerFactory.newTransformer();
            }
            catch (Exception e) {
                Diagnostics.ignoreThrowable(e);
            }
        }
        return null;
    }

    private static String getXMLParserLocation() {
        SAXParser saxParser = Diagnostics.getSAXParser();
        if (saxParser == null) {
            return null;
        }
        URL location = Diagnostics.getClassLocation(saxParser.getClass());
        return location != null ? location.toString() : null;
    }

    private static String getNamespaceParserName() {
        try {
            XMLReader reader = JAXPUtils.getNamespaceXMLReader();
            return reader.getClass().getName();
        }
        catch (BuildException e) {
            Diagnostics.ignoreThrowable(e);
            return null;
        }
    }

    private static String getNamespaceParserLocation() {
        try {
            XMLReader reader = JAXPUtils.getNamespaceXMLReader();
            URL location = Diagnostics.getClassLocation(reader.getClass());
            return location != null ? location.toString() : null;
        }
        catch (BuildException e) {
            Diagnostics.ignoreThrowable(e);
            return null;
        }
    }

    private static String getXSLTProcessorLocation() {
        Transformer transformer = Diagnostics.getXSLTProcessor();
        if (transformer == null) {
            return null;
        }
        URL location = Diagnostics.getClassLocation(transformer.getClass());
        return location != null ? location.toString() : null;
    }

    private static void ignoreThrowable(Throwable thrown) {
    }

    public static void doReport(PrintStream out) {
        Diagnostics.doReport(out, 2);
    }

    public static void doReport(PrintStream out, int logLevel) {
        out.println("------- Ant diagnostics report -------");
        out.println(Main.getAntVersion());
        Diagnostics.header(out, "Implementation Version");
        out.println("core tasks     : " + Diagnostics.getImplementationVersion(Main.class) + " in " + Diagnostics.getClassLocation(Main.class));
        Diagnostics.header(out, "ANT PROPERTIES");
        Diagnostics.doReportAntProperties(out);
        Diagnostics.header(out, "ANT_HOME/lib jar listing");
        Diagnostics.doReportAntHomeLibraries(out);
        Diagnostics.header(out, "USER_HOME/.ant/lib jar listing");
        Diagnostics.doReportUserHomeLibraries(out);
        Diagnostics.header(out, "Tasks availability");
        Diagnostics.doReportTasksAvailability(out);
        Diagnostics.header(out, "org.apache.env.Which diagnostics");
        Diagnostics.doReportWhich(out);
        Diagnostics.header(out, "XML Parser information");
        Diagnostics.doReportParserInfo(out);
        Diagnostics.header(out, "XSLT Processor information");
        Diagnostics.doReportXSLTProcessorInfo(out);
        Diagnostics.header(out, "System properties");
        Diagnostics.doReportSystemProperties(out);
        Diagnostics.header(out, "Temp dir");
        Diagnostics.doReportTempDir(out);
        Diagnostics.header(out, "Locale information");
        Diagnostics.doReportLocale(out);
        Diagnostics.header(out, "Proxy information");
        Diagnostics.doReportProxy(out);
        out.println();
    }

    private static void header(PrintStream out, String section) {
        out.println();
        out.println("-------------------------------------------");
        out.print(" ");
        out.println(section);
        out.println("-------------------------------------------");
    }

    private static void doReportSystemProperties(PrintStream out) {
        Properties sysprops = null;
        try {
            sysprops = System.getProperties();
        }
        catch (SecurityException e) {
            Diagnostics.ignoreThrowable(e);
            out.println("Access to System.getProperties() blocked by a security manager");
            return;
        }
        sysprops.stringPropertyNames().stream().map(key -> key + " : " + Diagnostics.getProperty(key)).forEach(out::println);
    }

    private static String getProperty(String key) {
        String value;
        try {
            value = System.getProperty(key);
        }
        catch (SecurityException e) {
            value = ERROR_PROPERTY_ACCESS_BLOCKED;
        }
        return value;
    }

    private static void doReportAntProperties(PrintStream out) {
        Project p = new Project();
        p.initProperties();
        out.println("ant.version: " + p.getProperty("ant.version"));
        out.println("ant.java.version: " + p.getProperty("ant.java.version"));
        out.println("Is this the Apache Harmony VM? " + (JavaEnvUtils.isApacheHarmony() ? "yes" : "no"));
        out.println("Is this the Kaffe VM? " + (JavaEnvUtils.isKaffe() ? "yes" : "no"));
        out.println("Is this gij/gcj? " + (JavaEnvUtils.isGij() ? "yes" : "no"));
        out.println("ant.core.lib: " + p.getProperty("ant.core.lib"));
        out.println("ant.home: " + p.getProperty("ant.home"));
    }

    private static void doReportAntHomeLibraries(PrintStream out) {
        out.println("ant.home: " + System.getProperty("ant.home"));
        Diagnostics.printLibraries(Diagnostics.listLibraries(), out);
    }

    private static void doReportUserHomeLibraries(PrintStream out) {
        String home = System.getProperty("user.home");
        out.println("user.home: " + home);
        File libDir = new File(home, Launcher.USER_LIBDIR);
        Diagnostics.printLibraries(Diagnostics.listJarFiles(libDir), out);
    }

    private static void printLibraries(File[] libs, PrintStream out) {
        if (libs == null) {
            out.println("No such directory.");
            return;
        }
        for (File lib : libs) {
            out.println(lib.getName() + " (" + lib.length() + " bytes)");
        }
    }

    private static void doReportWhich(PrintStream out) {
        Throwable error = null;
        try {
            Class<?> which = Class.forName("org.apache.env.Which");
            Method method = which.getMethod("main", String[].class);
            method.invoke(null, new Object[]{new String[0]});
        }
        catch (ClassNotFoundException e) {
            out.println("Not available.");
            out.println("Download it at https://xml.apache.org/commons/");
        }
        catch (InvocationTargetException e) {
            error = e.getTargetException() == null ? e : e.getTargetException();
        }
        catch (Throwable e) {
            error = e;
        }
        if (error != null) {
            out.println("Error while running org.apache.env.Which");
            error.printStackTrace(out);
        }
    }

    private static void doReportTasksAvailability(PrintStream out) {
        InputStream is = Main.class.getResourceAsStream("/org/apache/tools/ant/taskdefs/defaults.properties");
        if (is == null) {
            out.println("None available");
        } else {
            Properties props = new Properties();
            try {
                props.load(is);
                for (String key : props.stringPropertyNames()) {
                    String classname = props.getProperty(key);
                    try {
                        Class.forName(classname);
                        props.remove(key);
                    }
                    catch (ClassNotFoundException e) {
                        out.println(key + " : Not Available (the implementation class is not present)");
                    }
                    catch (NoClassDefFoundError e) {
                        String pkg = e.getMessage().replace('/', '.');
                        out.println(key + " : Missing dependency " + pkg);
                    }
                    catch (LinkageError e) {
                        out.println(key + " : Initialization error");
                    }
                }
                if (props.size() == 0) {
                    out.println("All defined tasks are available");
                } else {
                    out.println("A task being missing/unavailable should only matter if you are trying to use it");
                }
            }
            catch (IOException e) {
                out.println(e.getMessage());
            }
        }
    }

    private static void doReportParserInfo(PrintStream out) {
        String parserName = Diagnostics.getXMLParserName();
        String parserLocation = Diagnostics.getXMLParserLocation();
        Diagnostics.printParserInfo(out, "XML Parser", parserName, parserLocation);
        Diagnostics.printParserInfo(out, "Namespace-aware parser", Diagnostics.getNamespaceParserName(), Diagnostics.getNamespaceParserLocation());
    }

    private static void doReportXSLTProcessorInfo(PrintStream out) {
        String processorName = Diagnostics.getXSLTProcessorName();
        String processorLocation = Diagnostics.getXSLTProcessorLocation();
        Diagnostics.printParserInfo(out, "XSLT Processor", processorName, processorLocation);
    }

    private static void printParserInfo(PrintStream out, String parserType, String parserName, String parserLocation) {
        if (parserName == null) {
            parserName = "unknown";
        }
        if (parserLocation == null) {
            parserLocation = "unknown";
        }
        out.println(parserType + " : " + parserName);
        out.println(parserType + " Location: " + parserLocation);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private static void doReportTempDir(PrintStream out) {
        block17: {
            String tempdir = System.getProperty("java.io.tmpdir");
            if (tempdir == null) {
                out.println("Warning: java.io.tmpdir is undefined");
                return;
            }
            out.println("Temp dir is " + tempdir);
            File tempDirectory = new File(tempdir);
            if (!tempDirectory.exists()) {
                out.println("Warning, java.io.tmpdir directory does not exist: " + tempdir);
                return;
            }
            long now = System.currentTimeMillis();
            File tempFile = null;
            OutputStream fileout = null;
            InputStream filein = null;
            try {
                tempFile = File.createTempFile("diag", "txt", tempDirectory);
                fileout = Files.newOutputStream(tempFile.toPath(), new OpenOption[0]);
                byte[] buffer = new byte[1024];
                for (int i = 0; i < 32; ++i) {
                    fileout.write(buffer);
                }
                fileout.close();
                fileout = null;
                Thread.sleep(1000L);
                filein = Files.newInputStream(tempFile.toPath(), new OpenOption[0]);
                int total = 0;
                int read = 0;
                while ((read = filein.read(buffer, 0, 1024)) > 0) {
                    total += read;
                }
                filein.close();
                filein = null;
                long filetime = tempFile.lastModified();
                long drift = filetime - now;
                tempFile.delete();
                out.print("Temp dir is writeable");
                if (total != 32768) {
                    out.println(", but seems to be full.  Wrote 32768but could only read " + total + " bytes.");
                } else {
                    out.println();
                }
                out.println("Temp dir alignment with system clock is " + drift + " ms");
                if (Math.abs(drift) > 10000L) {
                    out.println("Warning: big clock drift -maybe a network filesystem");
                }
                FileUtils.close(fileout);
            }
            catch (IOException e) {
                Diagnostics.ignoreThrowable(e);
                out.println("Failed to create a temporary file in the temp dir " + tempdir);
                out.println("File  " + tempFile + " could not be created/written to");
                FileUtils.close(fileout);
                FileUtils.close(filein);
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
                break block17;
            }
            catch (InterruptedException e2) {
                Diagnostics.ignoreThrowable(e2);
                out.println("Failed to check whether tempdir is writable");
                break block17;
                {
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
            }
            finally {
                FileUtils.close(fileout);
                FileUtils.close(filein);
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
            FileUtils.close(filein);
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private static void doReportLocale(PrintStream out) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        out.println("Timezone " + tz.getDisplayName() + " offset=" + tz.getOffset(cal.get(0), cal.get(1), cal.get(2), cal.get(5), cal.get(7), ((cal.get(11) * 60 + cal.get(12)) * 60 + cal.get(13)) * 1000 + cal.get(14)));
    }

    private static void printProperty(PrintStream out, String key) {
        String value = Diagnostics.getProperty(key);
        if (value != null) {
            out.print(key);
            out.print(" = ");
            out.print('\"');
            out.print(value);
            out.println('\"');
        }
    }

    private static void doReportProxy(PrintStream out) {
        Diagnostics.printProperty(out, "http.proxyHost");
        Diagnostics.printProperty(out, "http.proxyPort");
        Diagnostics.printProperty(out, "http.proxyUser");
        Diagnostics.printProperty(out, "http.proxyPassword");
        Diagnostics.printProperty(out, "http.nonProxyHosts");
        Diagnostics.printProperty(out, "https.proxyHost");
        Diagnostics.printProperty(out, "https.proxyPort");
        Diagnostics.printProperty(out, "https.nonProxyHosts");
        Diagnostics.printProperty(out, "ftp.proxyHost");
        Diagnostics.printProperty(out, "ftp.proxyPort");
        Diagnostics.printProperty(out, "ftp.nonProxyHosts");
        Diagnostics.printProperty(out, "socksProxyHost");
        Diagnostics.printProperty(out, "socksProxyPort");
        Diagnostics.printProperty(out, "java.net.socks.username");
        Diagnostics.printProperty(out, "java.net.socks.password");
        Diagnostics.printProperty(out, "java.net.useSystemProxies");
        ProxyDiagnostics proxyDiag = new ProxyDiagnostics();
        out.println("Java1.5+ proxy settings:");
        out.println(proxyDiag.toString());
    }
}

