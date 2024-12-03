/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import org.apache.axiom.util.stax.dialect.BEADialect;
import org.apache.axiom.util.stax.dialect.SJSXPDialect;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.UnknownStAXDialect;
import org.apache.axiom.util.stax.dialect.Version;
import org.apache.axiom.util.stax.dialect.Woodstox3Dialect;
import org.apache.axiom.util.stax.dialect.Woodstox4Dialect;
import org.apache.axiom.util.stax.dialect.XLXP1Dialect;
import org.apache.axiom.util.stax.dialect.XLXP2Dialect;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StAXDialectDetector {
    private static final Log log = LogFactory.getLog(StAXDialectDetector.class);
    private static final Attributes.Name IMPLEMENTATION_TITLE = new Attributes.Name("Implementation-Title");
    private static final Attributes.Name IMPLEMENTATION_VENDOR = new Attributes.Name("Implementation-Vendor");
    private static final Attributes.Name IMPLEMENTATION_VERSION = new Attributes.Name("Implementation-Version");
    private static final Attributes.Name BUNDLE_SYMBOLIC_NAME = new Attributes.Name("Bundle-SymbolicName");
    private static final Attributes.Name BUNDLE_VENDOR = new Attributes.Name("Bundle-Vendor");
    private static final Attributes.Name BUNDLE_VERSION = new Attributes.Name("Bundle-Version");
    private static final Map dialectByUrl = Collections.synchronizedMap(new HashMap());

    private StAXDialectDetector() {
    }

    private static URL getRootUrlForResource(ClassLoader classLoader, String resource) {
        URL url;
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        if ((url = classLoader.getResource(resource)) == null) {
            return null;
        }
        String file = url.getFile();
        if (file.endsWith(resource)) {
            try {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), file.substring(0, file.length() - resource.length()));
            }
            catch (MalformedURLException ex) {
                return null;
            }
        }
        return null;
    }

    private static URL getRootUrlForClass(Class cls) {
        return StAXDialectDetector.getRootUrlForResource(cls.getClassLoader(), cls.getName().replace('.', '/') + ".class");
    }

    public static XMLInputFactory normalize(XMLInputFactory factory) {
        return StAXDialectDetector.getDialect(factory.getClass()).normalize(factory);
    }

    public static XMLOutputFactory normalize(XMLOutputFactory factory) {
        return StAXDialectDetector.getDialect(factory.getClass()).normalize(factory);
    }

    public static StAXDialect getDialect(Class implementationClass) {
        URL rootUrl = StAXDialectDetector.getRootUrlForClass(implementationClass);
        if (rootUrl == null) {
            log.warn((Object)("Unable to determine location of StAX implementation containing class " + implementationClass.getName() + "; using default dialect"));
            return UnknownStAXDialect.INSTANCE;
        }
        return StAXDialectDetector.getDialect(implementationClass.getClassLoader(), rootUrl);
    }

    private static StAXDialect getDialect(ClassLoader classLoader, URL rootUrl) {
        StAXDialect dialect = (StAXDialect)dialectByUrl.get(rootUrl);
        if (dialect != null) {
            return dialect;
        }
        dialect = StAXDialectDetector.detectDialect(classLoader, rootUrl);
        dialectByUrl.put(rootUrl, dialect);
        return dialect;
    }

    private static StAXDialect detectDialect(ClassLoader classLoader, URL rootUrl) {
        StAXDialect dialect = StAXDialectDetector.detectDialectFromJarManifest(rootUrl);
        if (dialect == null) {
            dialect = StAXDialectDetector.detectDialectFromClasses(classLoader, rootUrl);
        }
        if (dialect == null) {
            log.warn((Object)("Unable to determine dialect of the StAX implementation at " + rootUrl));
            return UnknownStAXDialect.INSTANCE;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Detected StAX dialect: " + dialect.getName()));
        }
        return dialect;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static StAXDialect detectDialectFromJarManifest(URL rootUrl) {
        String versionString;
        String vendor;
        int i;
        Manifest manifest;
        try {
            URL metaInfUrl = new URL(rootUrl, "META-INF/MANIFEST.MF");
            InputStream is = metaInfUrl.openStream();
            try {
                manifest = new Manifest(is);
            }
            finally {
                is.close();
            }
        }
        catch (IOException ex) {
            log.warn((Object)("Unable to load manifest for StAX implementation at " + rootUrl));
            return UnknownStAXDialect.INSTANCE;
        }
        Attributes attrs = manifest.getMainAttributes();
        String title = attrs.getValue(IMPLEMENTATION_TITLE);
        String symbolicName = attrs.getValue(BUNDLE_SYMBOLIC_NAME);
        if (symbolicName != null && (i = symbolicName.indexOf(59)) != -1) {
            symbolicName = symbolicName.substring(0, i);
        }
        if ((vendor = attrs.getValue(IMPLEMENTATION_VENDOR)) == null) {
            vendor = attrs.getValue(BUNDLE_VENDOR);
        }
        if ((versionString = attrs.getValue(IMPLEMENTATION_VERSION)) == null) {
            versionString = attrs.getValue(BUNDLE_VERSION);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("StAX implementation at " + rootUrl + " is:\n" + "  Title:         " + title + "\n" + "  Symbolic name: " + symbolicName + "\n" + "  Vendor:        " + vendor + "\n" + "  Version:       " + versionString));
        }
        if (vendor != null && vendor.toLowerCase().indexOf("woodstox") != -1) {
            Version version = new Version(versionString);
            switch (version.getComponent(0)) {
                case 3: {
                    return Woodstox3Dialect.INSTANCE;
                }
                case 4: {
                    return new Woodstox4Dialect(version.getComponent(1) == 0 && version.getComponent(2) < 11 || version.getComponent(1) == 1 && version.getComponent(2) < 3);
                }
            }
            return null;
        }
        if (title != null && title.indexOf("SJSXP") != -1) {
            return new SJSXPDialect(false);
        }
        if ("com.bea.core.weblogic.stax".equals(symbolicName)) {
            log.warn((Object)"Weblogic's StAX implementation is unsupported and some Axiom features will not work as expected! Please use Woodstox instead.");
            return BEADialect.INSTANCE;
        }
        if ("BEA".equals(vendor)) {
            return BEADialect.INSTANCE;
        }
        if ("com.ibm.ws.prereq.banshee".equals(symbolicName)) {
            return XLXP2Dialect.INSTANCE;
        }
        return null;
    }

    private static Class loadClass(ClassLoader classLoader, URL rootUrl, String name) {
        try {
            Class<?> cls;
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            return rootUrl.equals(StAXDialectDetector.getRootUrlForClass(cls = classLoader.loadClass(name))) ? cls : null;
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static StAXDialect detectDialectFromClasses(ClassLoader classLoader, URL rootUrl) {
        Class cls = StAXDialectDetector.loadClass(classLoader, rootUrl, "com.sun.xml.internal.stream.XMLOutputFactoryImpl");
        if (cls != null) {
            boolean isUnsafeStreamResult;
            try {
                cls.getDeclaredField("fStreamResult");
                isUnsafeStreamResult = true;
            }
            catch (NoSuchFieldException ex) {
                isUnsafeStreamResult = false;
            }
            return new SJSXPDialect(isUnsafeStreamResult);
        }
        cls = StAXDialectDetector.loadClass(classLoader, rootUrl, "com.ibm.xml.xlxp.api.stax.StAXImplConstants");
        if (cls != null) {
            boolean isSetPrefixBroken;
            try {
                cls.getField("IS_SETPREFIX_BEFORE_STARTELEMENT");
                isSetPrefixBroken = false;
            }
            catch (NoSuchFieldException ex) {
                isSetPrefixBroken = true;
            }
            return new XLXP1Dialect(isSetPrefixBroken);
        }
        cls = StAXDialectDetector.loadClass(classLoader, rootUrl, "com.ibm.xml.xlxp2.api.stax.StAXImplConstants");
        if (cls != null) {
            return new XLXP2Dialect();
        }
        return null;
    }
}

