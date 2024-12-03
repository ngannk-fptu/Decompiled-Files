/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.DeweyDecimal;
import org.apache.tools.ant.util.FileUtils;

public final class JavaEnvUtils {
    private static final boolean IS_DOS = Os.isFamily("dos");
    private static final boolean IS_NETWARE = Os.isName("netware");
    private static final boolean IS_AIX = Os.isName("aix");
    private static final String JAVA_HOME = System.getProperty("java.home");
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static String javaVersion;
    private static int javaVersionNumber;
    private static final DeweyDecimal parsedJavaVersion;
    public static final String JAVA_1_0 = "1.0";
    public static final int VERSION_1_0 = 10;
    public static final String JAVA_1_1 = "1.1";
    public static final int VERSION_1_1 = 11;
    public static final String JAVA_1_2 = "1.2";
    public static final int VERSION_1_2 = 12;
    public static final String JAVA_1_3 = "1.3";
    public static final int VERSION_1_3 = 13;
    public static final String JAVA_1_4 = "1.4";
    public static final int VERSION_1_4 = 14;
    public static final String JAVA_1_5 = "1.5";
    public static final int VERSION_1_5 = 15;
    public static final String JAVA_1_6 = "1.6";
    public static final int VERSION_1_6 = 16;
    public static final String JAVA_1_7 = "1.7";
    public static final int VERSION_1_7 = 17;
    public static final String JAVA_1_8 = "1.8";
    public static final int VERSION_1_8 = 18;
    @Deprecated
    public static final String JAVA_1_9 = "1.9";
    @Deprecated
    public static final int VERSION_1_9 = 19;
    public static final String JAVA_9 = "9";
    public static final int VERSION_9 = 90;
    public static final String JAVA_10 = "10";
    public static final int VERSION_10 = 100;
    public static final String JAVA_11 = "11";
    public static final int VERSION_11 = 110;
    public static final String JAVA_12 = "12";
    public static final int VERSION_12 = 120;
    private static boolean kaffeDetected;
    private static boolean classpathDetected;
    private static boolean gijDetected;
    private static boolean harmonyDetected;
    private static Vector<String> jrePackages;

    private JavaEnvUtils() {
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    @Deprecated
    public static int getJavaVersionNumber() {
        return javaVersionNumber;
    }

    public static DeweyDecimal getParsedJavaVersion() {
        return parsedJavaVersion;
    }

    public static boolean isJavaVersion(String version) {
        return javaVersion.equals(version) || javaVersion.equals(JAVA_9) && JAVA_1_9.equals(version);
    }

    public static boolean isAtLeastJavaVersion(String version) {
        return parsedJavaVersion.compareTo(new DeweyDecimal(version)) >= 0;
    }

    public static boolean isKaffe() {
        return kaffeDetected;
    }

    public static boolean isClasspathBased() {
        return classpathDetected;
    }

    public static boolean isGij() {
        return gijDetected;
    }

    public static boolean isApacheHarmony() {
        return harmonyDetected;
    }

    public static String getJreExecutable(String command) {
        if (IS_NETWARE) {
            return command;
        }
        File jExecutable = null;
        if (IS_AIX) {
            jExecutable = JavaEnvUtils.findInDir(JAVA_HOME + "/sh", command);
        }
        if (jExecutable == null) {
            jExecutable = JavaEnvUtils.findInDir(JAVA_HOME + "/bin", command);
        }
        if (jExecutable != null) {
            return jExecutable.getAbsolutePath();
        }
        return JavaEnvUtils.addExtension(command);
    }

    public static String getJdkExecutable(String command) {
        if (IS_NETWARE) {
            return command;
        }
        File jExecutable = null;
        if (IS_AIX) {
            jExecutable = JavaEnvUtils.findInDir(JAVA_HOME + "/../sh", command);
        }
        if (jExecutable == null) {
            jExecutable = JavaEnvUtils.findInDir(JAVA_HOME + "/../bin", command);
        }
        if (jExecutable != null) {
            return jExecutable.getAbsolutePath();
        }
        return JavaEnvUtils.getJreExecutable(command);
    }

    private static String addExtension(String command) {
        return command + (IS_DOS ? ".exe" : "");
    }

    private static File findInDir(String dirName, String commandName) {
        File dir = FILE_UTILS.normalize(dirName);
        File executable = null;
        if (dir.exists() && !(executable = new File(dir, JavaEnvUtils.addExtension(commandName))).exists()) {
            executable = null;
        }
        return executable;
    }

    private static void buildJrePackages() {
        jrePackages = new Vector();
        jrePackages.addElement("sun");
        jrePackages.addElement("java");
        jrePackages.addElement("javax");
        jrePackages.addElement("com.sun.java");
        jrePackages.addElement("com.sun.image");
        jrePackages.addElement("org.omg");
        jrePackages.addElement("com.sun.corba");
        jrePackages.addElement("com.sun.jndi");
        jrePackages.addElement("com.sun.media");
        jrePackages.addElement("com.sun.naming");
        jrePackages.addElement("com.sun.org.omg");
        jrePackages.addElement("com.sun.rmi");
        jrePackages.addElement("sunw.io");
        jrePackages.addElement("sunw.util");
        jrePackages.addElement("org.ietf.jgss");
        jrePackages.addElement("org.w3c.dom");
        jrePackages.addElement("org.xml.sax");
        jrePackages.addElement("com.sun.org.apache");
        jrePackages.addElement("jdk");
    }

    public static Vector<String> getJrePackageTestCases() {
        Vector<String> tests = new Vector<String>();
        tests.addElement("java.lang.Object");
        tests.addElement("sun.reflect.SerializationConstructorAccessorImpl");
        tests.addElement("sun.net.www.http.HttpClient");
        tests.addElement("sun.audio.AudioPlayer");
        tests.addElement("javax.accessibility.Accessible");
        tests.addElement("sun.misc.BASE64Encoder");
        tests.addElement("com.sun.image.codec.jpeg.JPEGCodec");
        tests.addElement("org.omg.CORBA.Any");
        tests.addElement("com.sun.corba.se.internal.corba.AnyImpl");
        tests.addElement("com.sun.jndi.ldap.LdapURL");
        tests.addElement("com.sun.media.sound.Printer");
        tests.addElement("com.sun.naming.internal.VersionHelper");
        tests.addElement("com.sun.org.omg.CORBA.Initializer");
        tests.addElement("sunw.io.Serializable");
        tests.addElement("sunw.util.EventListener");
        tests.addElement("sun.audio.AudioPlayer");
        tests.addElement("org.ietf.jgss.Oid");
        tests.addElement("org.w3c.dom.Attr");
        tests.addElement("org.xml.sax.XMLReader");
        tests.addElement("com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl");
        tests.addElement("jdk.net.Sockets");
        return tests;
    }

    public static Vector<String> getJrePackages() {
        if (jrePackages == null) {
            JavaEnvUtils.buildJrePackages();
        }
        return jrePackages;
    }

    public static File createVmsJavaOptionFile(String[] cmds) throws IOException {
        File script = FILE_UTILS.createTempFile(null, "ANT", ".JAVA_OPTS", null, false, true);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(script));){
            for (String cmd : cmds) {
                out.write(cmd);
                out.newLine();
            }
        }
        return script;
    }

    public static String getJavaHome() {
        return JAVA_HOME;
    }

    static {
        try {
            javaVersion = JAVA_1_8;
            javaVersionNumber = 18;
            Class.forName("java.lang.module.ModuleDescriptor");
            String v = System.getProperty("java.specification.version");
            DeweyDecimal pv = new DeweyDecimal(v);
            javaVersionNumber = pv.get(0) * 10;
            if (pv.getSize() > 1) {
                javaVersionNumber += pv.get(1);
            }
            javaVersion = pv.toString();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        parsedJavaVersion = new DeweyDecimal(javaVersion);
        kaffeDetected = false;
        try {
            Class.forName("kaffe.util.NotImplemented");
            kaffeDetected = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        classpathDetected = false;
        try {
            Class.forName("gnu.classpath.Configuration");
            classpathDetected = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        gijDetected = false;
        try {
            Class.forName("gnu.gcj.Core");
            gijDetected = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        harmonyDetected = false;
        try {
            Class.forName("org.apache.harmony.luni.util.Base64");
            harmonyDetected = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

