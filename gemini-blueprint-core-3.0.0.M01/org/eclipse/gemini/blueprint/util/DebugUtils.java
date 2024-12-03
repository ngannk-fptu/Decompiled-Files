/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Version
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.eclipse.gemini.blueprint.util.LogUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public abstract class DebugUtils {
    private static final String EQUALS = "=";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String SEMI_COLON = ";";
    private static final String COMMA = ",";
    private static final Log log = LogUtils.createLogger(DebugUtils.class);
    private static final String PACKAGE_REGEX = "([^;,]+(?:;?\\w+:?=((\"[^\"]+\")|([^,]+)))*)+";
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("([^;,]+(?:;?\\w+:?=((\"[^\"]+\")|([^,]+)))*)+");

    public static void debugClassLoadingThrowable(Throwable loadingThrowable, Bundle bundle, Class<?>[] classes) {
        String className = null;
        if (loadingThrowable instanceof NoClassDefFoundError) {
            className = loadingThrowable.getMessage();
            if (className != null) {
                className = className.replace('/', '.');
            }
        } else if (loadingThrowable instanceof ClassNotFoundException && (className = loadingThrowable.getMessage()) != null) {
            className = className.replace('/', '.');
        }
        if (className != null) {
            DebugUtils.debugClassLoading(bundle, className, null);
            if (!ObjectUtils.isEmpty((Object[])classes) && log.isDebugEnabled()) {
                StringBuilder message = new StringBuilder();
                for (int i = 0; i < classes.length; ++i) {
                    ClassLoader cl = classes[i].getClassLoader();
                    String cansee = "cannot";
                    if (ClassUtils.isPresent((String)className, (ClassLoader)cl)) {
                        cansee = "can";
                    }
                    message.append(classes[i] + " is loaded by " + cl + " which " + cansee + " see " + className);
                }
                log.debug((Object)message);
            }
        }
    }

    public static void debugClassLoading(Bundle bundle, String className, String rootClassName) {
        boolean trace = log.isTraceEnabled();
        if (!trace) {
            return;
        }
        Dictionary dict = bundle.getHeaders();
        String bname = dict.get("Bundle-Name") + "(" + dict.get("Bundle-SymbolicName") + ")";
        if (trace) {
            log.trace((Object)("Could not find class [" + className + "] required by [" + bname + "] scanning available bundles"));
        }
        BundleContext context = OsgiBundleUtils.getBundleContext(bundle);
        int pkgIndex = className.lastIndexOf(46);
        if (pkgIndex < 0) {
            if (trace) {
                log.trace((Object)"Class is not in a package, its unlikely that this will work");
            }
            return;
        }
        String packageName = className.substring(0, pkgIndex);
        Version iversion = DebugUtils.hasImport(bundle, packageName);
        if (iversion != null && context != null) {
            if (trace) {
                log.trace((Object)("Class is correctly imported as version [" + iversion + "], checking providing bundles"));
            }
            Bundle[] bundles = context.getBundles();
            for (int i = 0; i < bundles.length; ++i) {
                Version exported;
                if (bundles[i].getBundleId() == bundle.getBundleId() || (exported = DebugUtils.checkBundleForClass(bundles[i], className, iversion)) == null || !exported.equals((Object)iversion) || rootClassName == null) continue;
                for (int j = 0; j < bundles.length; ++j) {
                    Version rootimport;
                    Version rootexport = DebugUtils.hasExport(bundles[j], rootClassName.substring(0, rootClassName.lastIndexOf(46)));
                    if (rootexport == null || (rootimport = DebugUtils.hasImport(bundles[j], packageName)) != null && rootimport.equals((Object)iversion) || !trace) continue;
                    log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundles[j]) + "] exports [" + rootClassName + "] as version [" + rootexport + "] but does not import dependent package [" + packageName + "] at version [" + iversion + "]"));
                }
            }
        }
        if (DebugUtils.hasExport(bundle, packageName) != null) {
            if (trace) {
                log.trace((Object)"Class is exported, checking this bundle");
            }
            DebugUtils.checkBundleForClass(bundle, className, iversion);
        }
    }

    private static Version checkBundleForClass(Bundle bundle, String name, Version iversion) {
        String packageName = name.substring(0, name.lastIndexOf(46));
        Version hasExport = DebugUtils.hasExport(bundle, packageName);
        if (hasExport != null && !hasExport.equals((Object)iversion)) {
            log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] exports [" + packageName + "] as version [" + hasExport + "] but version [" + iversion + "] was required"));
            return hasExport;
        }
        String cname = name.substring(packageName.length() + 1) + ".class";
        Enumeration e = bundle.findEntries("/" + packageName.replace('.', '/'), cname, false);
        if (e == null) {
            Enumeration pe;
            if (hasExport != null) {
                URL url = DebugUtils.checkBundleJarsForClass(bundle, name);
                if (url != null) {
                    log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains [" + cname + "] in embedded jar [" + url.toString() + "] but exports the package"));
                } else {
                    log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] does not contain [" + cname + "] but exports the package"));
                }
            }
            String root = "/";
            String fileName = packageName;
            if (packageName.lastIndexOf(".") >= 0) {
                root = root + packageName.substring(0, packageName.lastIndexOf(".")).replace('.', '/');
                fileName = packageName.substring(packageName.lastIndexOf(".") + 1).replace('.', '/');
            }
            if ((pe = bundle.findEntries(root, fileName, false)) != null) {
                if (hasExport != null) {
                    log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains package [" + packageName + "] and exports it"));
                } else {
                    log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains package [" + packageName + "] but does not export it"));
                }
            }
        } else if (hasExport != null) {
            log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains resource [" + cname + "] and it is correctly exported as version [" + hasExport + "]"));
            Class c = null;
            try {
                c = bundle.loadClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] loadClass [" + cname + "] returns [" + c + "]"));
        } else {
            log.trace((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName(bundle) + "] contains resource [" + cname + "] but its package is not exported"));
        }
        return hasExport;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static URL checkBundleJarsForClass(Bundle bundle, String name) {
        String cname = name.replace('.', '/') + ".class";
        Enumeration e = bundle.findEntries("/", "*.jar", true);
        while (e != null && e.hasMoreElements()) {
            URL url = (URL)e.nextElement();
            JarInputStream jin = null;
            try {
                jin = new JarInputStream(url.openStream());
                JarEntry ze = jin.getNextJarEntry();
                while (ze != null) {
                    if (ze.getName().equals(cname)) {
                        jin.close();
                        URL uRL = url;
                        return uRL;
                    }
                    ze = jin.getNextJarEntry();
                }
            }
            catch (IOException e1) {
                log.trace((Object)("Skipped " + url.toString() + ": " + e1.getMessage()));
            }
            finally {
                if (jin == null) continue;
                try {
                    jin.close();
                }
                catch (Exception exception) {}
            }
        }
        return null;
    }

    private static Version hasImport(Bundle bundle, String packageName) {
        Dictionary dict = bundle.getHeaders();
        String imports = (String)dict.get("Import-Package");
        Version v = DebugUtils.getVersion(imports, packageName);
        if (v != null) {
            return v;
        }
        String dynimports = (String)dict.get("DynamicImport-Package");
        if (dynimports != null) {
            StringTokenizer strok = new StringTokenizer(dynimports, COMMA);
            while (strok.hasMoreTokens()) {
                StringTokenizer parts = new StringTokenizer(strok.nextToken(), SEMI_COLON);
                String pkg = parts.nextToken().trim();
                if ((!pkg.endsWith(".*") || !packageName.startsWith(pkg.substring(0, pkg.length() - 2))) && !pkg.equals("*")) continue;
                Version version = Version.emptyVersion;
                while (parts.hasMoreTokens()) {
                    String modifier = parts.nextToken().trim();
                    if (!modifier.startsWith("version")) continue;
                    version = Version.parseVersion((String)modifier.substring(modifier.indexOf(EQUALS) + 1).trim());
                }
                return version;
            }
        }
        return null;
    }

    private static Version hasExport(Bundle bundle, String packageName) {
        Dictionary dict = bundle.getHeaders();
        return DebugUtils.getVersion((String)dict.get("Export-Package"), packageName);
    }

    private static Version getVersion(String stmt, String packageName) {
        if (stmt != null) {
            String[] pkgs = DebugUtils.splitIntoPackages(stmt);
            for (int packageIndex = 0; packageIndex < pkgs.length; ++packageIndex) {
                String pkgToken = pkgs[packageIndex].trim();
                String pkg = null;
                Version version = null;
                int firstDirectiveIndex = pkgToken.indexOf(SEMI_COLON);
                if (firstDirectiveIndex > -1) {
                    pkg = pkgToken.substring(0, firstDirectiveIndex);
                } else {
                    pkg = pkgToken;
                    version = Version.emptyVersion;
                }
                if (!pkg.equals(packageName)) continue;
                if (version == null) {
                    String[] directiveTokens = pkgToken.substring(firstDirectiveIndex + 1).split(SEMI_COLON);
                    for (int directiveTokenIndex = 0; directiveTokenIndex < directiveTokens.length; ++directiveTokenIndex) {
                        String directive = directiveTokens[directiveTokenIndex].trim();
                        if (!directive.startsWith("version")) continue;
                        String value = directive.substring(directive.indexOf(EQUALS) + 1).trim();
                        boolean lowEqualTo = value.startsWith("\"[");
                        boolean lowGreaterThen = value.startsWith("\"(");
                        if (lowEqualTo || lowGreaterThen) {
                            boolean highEqualTo = value.endsWith("]\"");
                            boolean highLessThen = value.endsWith(")\"");
                            value = value.substring(2, value.length() - 2);
                            int commaIndex = value.indexOf(COMMA);
                            Version left = Version.parseVersion((String)value.substring(0, commaIndex));
                            Version right = Version.parseVersion((String)value.substring(commaIndex + 1));
                            return left;
                        }
                        if (value.startsWith(DOUBLE_QUOTE)) {
                            return Version.parseVersion((String)value.substring(1, value.length() - 1));
                        }
                        return Version.parseVersion((String)value);
                    }
                    if (version == null) {
                        version = Version.emptyVersion;
                    }
                }
                return version;
            }
        }
        return null;
    }

    private static String[] splitIntoPackages(String stmt) {
        ArrayList<String> pkgs = new ArrayList<String>(2);
        StringBuilder pkg = new StringBuilder();
        boolean ignoreComma = false;
        for (int stringIndex = 0; stringIndex < stmt.length(); ++stringIndex) {
            char currentChar = stmt.charAt(stringIndex);
            if (currentChar == ',') {
                if (ignoreComma) {
                    pkg.append(currentChar);
                    continue;
                }
                pkgs.add(pkg.toString());
                pkg = new StringBuilder();
                ignoreComma = false;
                continue;
            }
            if (currentChar == '\"') {
                ignoreComma = !ignoreComma;
            }
            pkg.append(currentChar);
        }
        pkgs.add(pkg.toString());
        return pkgs.toArray(new String[pkgs.size()]);
    }
}

