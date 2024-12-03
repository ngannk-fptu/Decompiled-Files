/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import nonapi.io.github.classgraph.utils.JarUtils;
import org.w3c.dom.Document;

public final class VersionFinder {
    private static final String MAVEN_PACKAGE = "io.github.classgraph";
    private static final String MAVEN_ARTIFACT = "classgraph";
    public static final OperatingSystem OS;
    public static final String JAVA_VERSION;
    public static final int JAVA_MAJOR_VERSION;
    public static final int JAVA_MINOR_VERSION;
    public static final int JAVA_SUB_VERSION;
    public static final boolean JAVA_IS_EA_VERSION;

    private VersionFinder() {
    }

    public static String getProperty(String propName) {
        try {
            return System.getProperty(propName);
        }
        catch (SecurityException e) {
            return null;
        }
    }

    public static String getProperty(String propName, String defaultVal) {
        try {
            return System.getProperty(propName, defaultVal);
        }
        catch (SecurityException e) {
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static synchronized String getVersion() {
        Class<ClassGraph> cls;
        block25: {
            cls = ClassGraph.class;
            try {
                int i;
                String className = cls.getName();
                URL classpathResource = cls.getResource("/" + JarUtils.classNameToClassfilePath(className));
                if (classpathResource == null) break block25;
                Path absolutePackagePath = Paths.get(classpathResource.toURI()).getParent();
                int packagePathSegments = className.length() - className.replace(".", "").length();
                Path path = absolutePackagePath;
                for (i = 0; i < packagePathSegments && path != null; path = path.getParent(), ++i) {
                }
                for (i = 0; i < 3 && path != null; ++i, path = path.getParent()) {
                    Path pom = path.resolve("pom.xml");
                    try (InputStream is = Files.newInputStream(pom, new OpenOption[0]);){
                        Document doc = VersionFinder.getSecureDocumentBuilderFactory().newDocumentBuilder().parse(is);
                        doc.getDocumentElement().normalize();
                        String version = (String)VersionFinder.getSecureXPathFactory().newXPath().compile("/project/version").evaluate(doc, XPathConstants.STRING);
                        if (version == null || (version = version.trim()).isEmpty()) continue;
                        String string = version;
                        return string;
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            catch (Exception className) {
                // empty catch block
            }
        }
        try (InputStream is2 = cls.getResourceAsStream("/META-INF/maven/io.github.classgraph/classgraph/pom.properties");){
            if (is2 != null) {
                Properties p = new Properties();
                p.load(is2);
                String version = p.getProperty("version", "").trim();
                if (!version.isEmpty()) {
                    String string = version;
                    return string;
                }
            }
        }
        catch (IOException is2) {
            // empty catch block
        }
        Package pkg = cls.getPackage();
        if (pkg == null) return "unknown";
        String version = pkg.getImplementationVersion();
        if (version == null) {
            version = "";
        }
        if ((version = version.trim()).isEmpty()) {
            version = pkg.getSpecificationVersion();
            if (version == null) {
                version = "";
            }
            version = version.trim();
        }
        if (version.isEmpty()) return "unknown";
        return version;
    }

    private static DocumentBuilderFactory getSecureDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setXIncludeAware(false);
        dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        dbf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        dbf.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        dbf.setExpandEntityReferences(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return dbf;
    }

    private static XPathFactory getSecureXPathFactory() throws XPathFactoryConfigurationException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        return xPathFactory;
    }

    static {
        JAVA_VERSION = VersionFinder.getProperty("java.version");
        int javaMajorVersion = 0;
        int javaMinorVersion = 0;
        int javaSubVersion = 0;
        ArrayList<Integer> versionParts = new ArrayList<Integer>();
        if (JAVA_VERSION != null) {
            for (String versionPart : JAVA_VERSION.split("[^0-9]+")) {
                try {
                    versionParts.add(Integer.parseInt(versionPart));
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            if (!versionParts.isEmpty() && (Integer)versionParts.get(0) == 1) {
                versionParts.remove(0);
            }
            if (versionParts.isEmpty()) {
                throw new RuntimeException("Could not determine Java version: " + JAVA_VERSION);
            }
            javaMajorVersion = (Integer)versionParts.get(0);
            if (versionParts.size() > 1) {
                javaMinorVersion = (Integer)versionParts.get(1);
            }
            if (versionParts.size() > 2) {
                javaSubVersion = (Integer)versionParts.get(2);
            }
        }
        JAVA_MAJOR_VERSION = javaMajorVersion;
        JAVA_MINOR_VERSION = javaMinorVersion;
        JAVA_SUB_VERSION = javaSubVersion;
        JAVA_IS_EA_VERSION = JAVA_VERSION != null && JAVA_VERSION.endsWith("-ea");
        String osName = VersionFinder.getProperty("os.name", "unknown").toLowerCase(Locale.ENGLISH);
        OS = File.separatorChar == '\\' ? OperatingSystem.Windows : (osName == null ? OperatingSystem.Unknown : (osName.contains("win") ? OperatingSystem.Windows : (osName.contains("mac") || osName.contains("darwin") ? OperatingSystem.MacOSX : (osName.contains("nux") ? OperatingSystem.Linux : (osName.contains("sunos") || osName.contains("solaris") ? OperatingSystem.Solaris : (osName.contains("bsd") ? OperatingSystem.Unix : (osName.contains("nix") || osName.contains("aix") ? OperatingSystem.Unix : OperatingSystem.Unknown)))))));
    }

    public static enum OperatingSystem {
        Windows,
        MacOSX,
        Linux,
        Solaris,
        BSD,
        Unix,
        Unknown;

    }
}

