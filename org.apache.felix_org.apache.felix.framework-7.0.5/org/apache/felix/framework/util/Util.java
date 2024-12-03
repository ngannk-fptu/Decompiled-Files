/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleArchiveRevision;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Resource;

public class Util {
    private static final String DEFAULT_PROPERTIES_FILE = "/default.properties";
    private static final Properties DEFAULTS;
    private static final IOException DEFAULT_EX;
    private static final Map<String, Set<String>> MODULES_MAP;
    private static final byte[] encTab;
    private static final byte[] decTab;
    private static final String DELIM_START = "${";
    private static final String DELIM_STOP = "}";
    private static final List EMPTY_LIST;
    private static final Map EMPTY_MAP;

    public static Properties loadDefaultProperties(Logger logger) {
        if (DEFAULTS.isEmpty()) {
            logger.log(1, "Unable to load any configuration properties.", DEFAULT_EX);
        }
        return DEFAULTS;
    }

    private static Properties loadDefaultProperties() throws IOException {
        Properties defaultProperties = new Properties();
        URL propURL = Util.class.getResource(DEFAULT_PROPERTIES_FILE);
        if (propURL != null) {
            try (InputStream is = propURL.openConnection().getInputStream();){
                defaultProperties.load(is);
            }
        }
        return defaultProperties;
    }

    public static void initializeJPMSEE(String javaVersion, Properties properties, Logger logger) {
        try {
            Version version = new Version(javaVersion);
            if (version.getMajor() >= 9) {
                int i;
                StringBuilder eecap = new StringBuilder(", osgi.ee; osgi.ee=\"OSGi/Minimum\"; version:List<Version>=\"1.0,1.1,1.2\",osgi.ee; osgi.ee=\"JavaSE\"; version:List<Version>=\"1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,9");
                for (i = 10; i <= version.getMajor(); ++i) {
                    eecap.append(',').append(Integer.toString(i));
                }
                eecap.append("\",osgi.ee; osgi.ee=\"JavaSE/compact1\"; version:List<Version>=\"1.8,9");
                for (i = 10; i <= version.getMajor(); ++i) {
                    eecap.append(',').append(Integer.toString(i));
                }
                eecap.append("\",osgi.ee; osgi.ee=\"JavaSE/compact2\"; version:List<Version>=\"1.8,9");
                for (i = 10; i <= version.getMajor(); ++i) {
                    eecap.append(',').append(Integer.toString(i));
                }
                eecap.append("\",osgi.ee; osgi.ee=\"JavaSE/compact3\"; version:List<Version>=\"1.8,9");
                for (i = 10; i <= version.getMajor(); ++i) {
                    eecap.append(',').append(Integer.toString(i));
                }
                eecap.append("\"");
                StringBuilder ee = new StringBuilder();
                for (int i2 = version.getMajor(); i2 > 9; --i2) {
                    ee.append("JavaSE-").append(Integer.toString(i2)).append(',');
                }
                ee.append("JavaSE-9,JavaSE-1.8,JavaSE-1.7,JavaSE-1.6,J2SE-1.5,J2SE-1.4,J2SE-1.3,J2SE-1.2,JRE-1.1,JRE-1.0,OSGi/Minimum-1.2,OSGi/Minimum-1.1,OSGi/Minimum-1.0");
                properties.put("ee-jpms", ee.toString());
                properties.put("eecap-jpms", eecap.toString());
                properties.put("felix.detect.jpms", "jpms");
            }
            properties.put("felix.detect.java.specification.version", version.getMajor() < 9 ? "1." + (version.getMinor() > 6 ? (version.getMinor() < 8 ? version.getMinor() : 8) : 6) : Integer.toString(version.getMajor()));
            if (version.getMajor() < 9) {
                Object[] objectArray = new Object[1];
                objectArray[0] = version.getMinor() > 6 ? (version.getMinor() < 8 ? version.getMinor() : 8) : 6;
                properties.put("felix.detect.java.version", String.format("0.0.0.JavaSE_001_%03d", objectArray));
            } else {
                properties.put("felix.detect.java.version", String.format("0.0.0.JavaSE_%03d", version.getMajor()));
            }
        }
        catch (Exception ex) {
            logger.log(1, "Exception parsing java version", ex);
        }
    }

    private static Map<String, Set<String>> calculateModulesMap() {
        LinkedHashMap<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
        try {
            Class<?> c_ModuleLayer = Felix.class.getClassLoader().loadClass("java.lang.ModuleLayer");
            Class<?> c_Module = Felix.class.getClassLoader().loadClass("java.lang.Module");
            Class<?> c_Descriptor = Felix.class.getClassLoader().loadClass("java.lang.module.ModuleDescriptor");
            Class<?> c_Exports = Felix.class.getClassLoader().loadClass("java.lang.module.ModuleDescriptor$Exports");
            Method m_getLayer = c_Module.getMethod("getLayer", new Class[0]);
            Method m_getModule = Class.class.getMethod("getModule", new Class[0]);
            Method m_getName = c_Module.getMethod("getName", new Class[0]);
            Method isAutomatic = c_Descriptor.getMethod("isAutomatic", new Class[0]);
            Method packagesMethod = c_Descriptor.getMethod("packages", new Class[0]);
            Object self = m_getModule.invoke(Felix.class, new Object[0]);
            Object moduleLayer = m_getLayer.invoke(self, new Object[0]);
            if (moduleLayer == null) {
                moduleLayer = c_ModuleLayer.getMethod("boot", new Class[0]).invoke(null, new Object[0]);
            }
            for (Object module : (Iterable)c_ModuleLayer.getMethod("modules", new Class[0]).invoke(moduleLayer, new Object[0])) {
                if (self.equals(module)) continue;
                String name = (String)m_getName.invoke(module, new Object[0]);
                LinkedHashSet<String> pkgs = new LinkedHashSet<String>();
                Object descriptor = c_Module.getMethod("getDescriptor", new Class[0]).invoke(module, new Object[0]);
                if (!((Boolean)isAutomatic.invoke(descriptor, new Object[0])).booleanValue()) {
                    for (Object export : (Set)c_Descriptor.getMethod("exports", new Class[0]).invoke(descriptor, new Object[0])) {
                        if (!((Set)c_Exports.getMethod("targets", new Class[0]).invoke(export, new Object[0])).isEmpty()) continue;
                        pkgs.add((String)c_Exports.getMethod("source", new Class[0]).invoke(export, new Object[0]));
                    }
                } else {
                    pkgs.addAll((Set)packagesMethod.invoke(descriptor, new Object[0]));
                }
                result.put(name, pkgs);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return result;
    }

    public static Map<String, Set<String>> initializeJPMS(Properties properties) {
        HashMap<String, Set<String>> exports = null;
        if (!MODULES_MAP.isEmpty()) {
            TreeSet<String> modules = new TreeSet<String>();
            exports = new HashMap<String, Set<String>>();
            for (Map.Entry<String, Set<String>> module : MODULES_MAP.entrySet()) {
                String name = module.getKey();
                properties.put("felix.detect.jpms." + name, name);
                modules.add("felix.jpms." + name);
                Set<String> pkgs = module.getValue();
                if (pkgs.isEmpty()) continue;
                exports.put("felix.jpms." + name, pkgs);
            }
            String modulesString = "";
            for (String module : modules) {
                modulesString = modulesString + DELIM_START + module + DELIM_STOP;
            }
            properties.put("jre-jpms", modulesString);
        }
        return exports;
    }

    public static String getPropertyWithSubs(Properties props, String name) {
        String value = props.getProperty(name);
        value = value != null ? Util.substVars(value, name, null, props) : null;
        return value;
    }

    public static Map<String, String> getPropertiesWithPrefix(Properties props, String prefix) {
        HashMap<String, String> result = new HashMap<String, String>();
        Set<String> propertySet = props.stringPropertyNames();
        for (String currentPropertyKey : propertySet) {
            if (!currentPropertyKey.startsWith(prefix)) continue;
            String value = props.getProperty(currentPropertyKey);
            value = value != null ? Util.substVars(value, currentPropertyKey, null, props) : null;
            result.put(currentPropertyKey, value);
        }
        return result;
    }

    public static Properties toProperties(Map map) {
        Properties result = new Properties();
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            result.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    public static long getBundleIdFromRevisionId(String id) {
        try {
            String bundleId = id.indexOf(46) >= 0 ? id.substring(0, id.indexOf(46)) : id;
            return Long.parseLong(bundleId);
        }
        catch (NumberFormatException ex) {
            return -1L;
        }
    }

    public static int getModuleRevisionFromModuleId(String id) {
        try {
            int index = id.indexOf(46);
            if (index >= 0) {
                return Integer.parseInt(id.substring(index + 1));
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return -1;
    }

    public static String getClassName(String className) {
        if (className == null) {
            className = "";
        }
        return className.lastIndexOf(46) < 0 ? "" : className.substring(className.lastIndexOf(46) + 1);
    }

    public static String getClassPackage(String className) {
        if (className == null) {
            className = "";
        }
        return className.lastIndexOf(46) < 0 ? "" : className.substring(0, className.lastIndexOf(46));
    }

    public static String getResourcePackage(String resource) {
        if (resource == null) {
            resource = "";
        }
        String pkgName = resource.startsWith("/") ? resource.substring(1) : resource;
        pkgName = pkgName.lastIndexOf(47) < 0 ? "" : pkgName.substring(0, pkgName.lastIndexOf(47));
        pkgName = pkgName.replace('/', '.');
        return pkgName;
    }

    public static Class loadClassUsingClass(Class clazz, String name, SecureAction action) {
        Class loadedClass = null;
        while (clazz != null) {
            ClassLoader loader = action.getClassLoader(clazz);
            loader = loader == null ? action.getSystemClassLoader() : loader;
            try {
                return loader.loadClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
                Class<?>[] ifcs = clazz.getInterfaces();
                for (int i = 0; i < ifcs.length; ++i) {
                    loadedClass = Util.loadClassUsingClass(ifcs[i], name, action);
                    if (loadedClass == null) continue;
                    return loadedClass;
                }
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public static boolean checkImplementsWithName(Class<?> clazz, String name) {
        while (clazz != null) {
            if (clazz.getName().equals(name)) {
                return true;
            }
            Class<?>[] ifcs = clazz.getInterfaces();
            for (int i = 0; i < ifcs.length; ++i) {
                if (!Util.checkImplementsWithName(ifcs[i], name)) continue;
                return true;
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    public static boolean isServiceAssignable(Bundle requester, ServiceReference ref) {
        boolean allow = true;
        String[] objectClass = (String[])ref.getProperty("objectClass");
        for (int classIdx = 0; allow && classIdx < objectClass.length; ++classIdx) {
            if (ref.isAssignableTo(requester, objectClass[classIdx])) continue;
            allow = false;
        }
        return allow;
    }

    public static List<BundleRequirement> getDynamicRequirements(List<BundleRequirement> reqs) {
        ArrayList<BundleRequirement> result = null;
        if (reqs != null) {
            for (BundleRequirement req : reqs) {
                String resolution = req.getDirectives().get("resolution");
                if (resolution == null || !resolution.equals("dynamic")) continue;
                if (result == null) {
                    result = new ArrayList<BundleRequirement>();
                }
                result.add(req);
            }
        }
        return result;
    }

    public static BundleWire getWire(BundleRevision br, String name) {
        List<BundleWire> wires;
        if (br.getWiring() != null && (wires = br.getWiring().getRequiredWires(null)) != null) {
            for (BundleWire w : wires) {
                if (!w.getCapability().getNamespace().equals("osgi.wiring.package") || !w.getCapability().getAttributes().get("osgi.wiring.package").equals(name)) continue;
                return w;
            }
        }
        return null;
    }

    public static BundleCapability getPackageCapability(BundleRevision br, String name) {
        List<BundleCapability> capabilities;
        if (br.getWiring() != null && (capabilities = br.getWiring().getCapabilities(null)) != null) {
            for (BundleCapability c : capabilities) {
                if (!c.getNamespace().equals("osgi.wiring.package") || !c.getAttributes().get("osgi.wiring.package").equals(name)) continue;
                return c;
            }
        }
        return null;
    }

    public static String base64Encode(String s) throws IOException {
        return Util.encode(s.getBytes(), 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String encode(byte[] in, int len) throws IOException {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        try {
            baos = new ByteArrayOutputStream();
            bais = new ByteArrayInputStream(in);
            Util.encode(bais, baos, len);
            String string = new String(baos.toByteArray());
            return string;
        }
        finally {
            if (baos != null) {
                baos.close();
            }
            if (bais != null) {
                bais.close();
            }
        }
    }

    public static void encode(InputStream in, OutputStream out, int len) throws IOException {
        int b;
        if (len % 4 != 0) {
            throw new IllegalArgumentException("Length must be a multiple of 4");
        }
        int bits = 0;
        int nbits = 0;
        int nbytes = 0;
        while ((b = in.read()) != -1) {
            bits = bits << 8 | b;
            nbits += 8;
            while (nbits >= 6) {
                out.write(encTab[0x3F & bits >> (nbits -= 6)]);
                if (len == 0 || ++nbytes < len) continue;
                out.write(13);
                out.write(10);
                nbytes -= len;
            }
        }
        switch (nbits) {
            case 2: {
                out.write(encTab[0x3F & bits << 4]);
                out.write(61);
                out.write(61);
                break;
            }
            case 4: {
                out.write(encTab[0x3F & bits << 2]);
                out.write(61);
            }
        }
        if (len != 0) {
            if (nbytes != 0) {
                out.write(13);
                out.write(10);
            }
            out.write(13);
            out.write(10);
        }
    }

    public static String substVars(String val, String currentKey, Map cycleMap, Properties configProps) throws IllegalArgumentException {
        String substValue;
        if (cycleMap == null) {
            cycleMap = new HashMap<String, String>();
        }
        cycleMap.put(currentKey, currentKey);
        int stopDelim = -1;
        int startDelim = -1;
        do {
            int idx;
            if ((stopDelim = val.indexOf(DELIM_STOP, stopDelim + 1)) < 0) {
                return val;
            }
            startDelim = val.indexOf(DELIM_START);
            if (startDelim < 0) {
                return val;
            }
            while (stopDelim >= 0 && (idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length())) >= 0 && idx <= stopDelim) {
                if (idx >= stopDelim) continue;
                startDelim = idx;
            }
        } while (startDelim > stopDelim && stopDelim >= 0);
        String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);
        if (cycleMap.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }
        String string = substValue = configProps != null ? configProps.getProperty(variable, null) : null;
        if (substValue == null) {
            substValue = System.getProperty(variable, "");
        }
        cycleMap.remove(variable);
        val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());
        val = Util.substVars(val, currentKey, cycleMap, configProps);
        return val;
    }

    public static boolean isSingleton(BundleRevision revision) {
        List<BundleCapability> caps = revision.getDeclaredCapabilities(null);
        for (BundleCapability cap : caps) {
            if (!cap.getNamespace().equals("osgi.wiring.bundle")) continue;
            for (Map.Entry<String, String> entry : cap.getDirectives().entrySet()) {
                if (!entry.getKey().equalsIgnoreCase("singleton")) continue;
                return Boolean.valueOf(entry.getValue());
            }
        }
        return false;
    }

    public static boolean isFragment(BundleRevision revision) {
        return (revision.getTypes() & 1) > 0;
    }

    public static boolean isFragment(Resource resource) {
        if (resource instanceof BundleRevision) {
            return Util.isFragment((BundleRevision)resource);
        }
        return false;
    }

    public static List<BundleRevision> getFragments(BundleWiring wiring) {
        List<BundleWire> wires;
        ArrayList<BundleRevision> fragments = Collections.EMPTY_LIST;
        if (wiring != null && (wires = wiring.getProvidedWires(null)) != null) {
            for (BundleWire w : wires) {
                if (!w.getCapability().getNamespace().equals("osgi.wiring.host")) continue;
                if (fragments.isEmpty()) {
                    fragments = new ArrayList<BundleRevision>();
                }
                fragments.add(w.getRequirerWiring().getRevision());
            }
        }
        return fragments;
    }

    public static String randomUUID(boolean secure) {
        Random rng;
        byte[] data = new byte[16];
        if (secure) {
            rng = new SecureRandom();
            ((SecureRandom)rng).nextBytes(data);
        } else {
            rng = new Random();
            rng.nextBytes(data);
        }
        long mostSigBits = ((long)data[0] & 0xFFL) << 56;
        mostSigBits |= ((long)data[1] & 0xFFL) << 48;
        mostSigBits |= ((long)data[2] & 0xFFL) << 40;
        mostSigBits |= ((long)data[3] & 0xFFL) << 32;
        mostSigBits |= ((long)data[4] & 0xFFL) << 24;
        mostSigBits |= ((long)data[5] & 0xFFL) << 16;
        mostSigBits |= ((long)data[6] & 0xFL) << 8;
        mostSigBits |= 0x4000L;
        mostSigBits |= (long)data[7] & 0xFFL;
        long leastSigBits = ((long)data[8] & 0x3FL) << 56;
        leastSigBits |= Long.MIN_VALUE;
        leastSigBits |= ((long)data[9] & 0xFFL) << 48;
        leastSigBits |= ((long)data[10] & 0xFFL) << 40;
        leastSigBits |= ((long)data[11] & 0xFFL) << 32;
        leastSigBits |= ((long)data[12] & 0xFFL) << 24;
        leastSigBits |= ((long)data[13] & 0xFFL) << 16;
        leastSigBits |= ((long)data[14] & 0xFFL) << 8;
        int msbHash = (int)(mostSigBits ^ mostSigBits >>> 32);
        int lsbHash = (int)((leastSigBits |= (long)data[15] & 0xFFL) ^ leastSigBits >>> 32);
        int hash = msbHash ^ lsbHash;
        int variant = (leastSigBits & Long.MIN_VALUE) == 0L ? 0 : ((leastSigBits & 0x4000000000000000L) != 0L ? (int)((leastSigBits & 0xE000000000000000L) >>> 61) : 2);
        int version = (int)((mostSigBits & 0xF000L) >>> 12);
        if (variant == 2 || version == 1) {
            long timeLow = (mostSigBits & 0xFFFFFFFF00000000L) >>> 32;
            long timeMid = (mostSigBits & 0xFFFF0000L) << 16;
            long timeHigh = (mostSigBits & 0xFFFL) << 48;
            long timestamp = timeLow | timeMid | timeHigh;
            int clockSequence = (int)((leastSigBits & 0x3FFF000000000000L) >>> 48);
            long l = leastSigBits & 0xFFFFFFFFFFFFL;
        }
        StringBuilder builder = new StringBuilder(36);
        String msbStr = Long.toHexString(mostSigBits);
        if (msbStr.length() < 16) {
            int diff = 16 - msbStr.length();
            for (int i = 0; i < diff; ++i) {
                builder.append('0');
            }
        }
        builder.append(msbStr);
        builder.insert(8, '-');
        builder.insert(13, '-');
        builder.append('-');
        String lsbStr = Long.toHexString(leastSigBits);
        if (lsbStr.length() < 16) {
            int diff = 16 - lsbStr.length();
            for (int i = 0; i < diff; ++i) {
                builder.append('0');
            }
        }
        builder.append(lsbStr);
        builder.insert(23, '-');
        return builder.toString();
    }

    public static <K, V> V putIfAbsentAndReturn(ConcurrentHashMap<K, V> map, K key, V value) {
        V result = map.putIfAbsent(key, value);
        return result != null ? result : value;
    }

    public static String getFrameworkUUIDFromURL(String host) {
        int idx;
        if (host != null && (idx = host.indexOf(95)) > 0) {
            return host.substring(0, idx);
        }
        return null;
    }

    public static String getRevisionIdFromURL(String host) {
        if (host != null) {
            int idx = host.indexOf(95);
            if (idx > 0 && idx < host.length()) {
                return host.substring(idx + 1);
            }
            return host;
        }
        return null;
    }

    public static Map<String, Object> getMultiReleaseAwareManifestHeaders(String version, BundleArchiveRevision revision) throws Exception {
        Map<String, Object> manifest = revision.getManifestHeader();
        if (manifest == null) {
            throw new FileNotFoundException("META-INF/MANIFEST.MF");
        }
        if ("true".equals(manifest.get("Multi-Release"))) {
            for (int major = Version.parseVersion(version).getMajor(); major >= 9; --major) {
                byte[] versionManifestInput = revision.getContent().getEntryAsBytes("META-INF/versions/" + major + "/OSGI-INF/MANIFEST.MF");
                if (versionManifestInput == null) continue;
                Map<String, Object> versionManifest = BundleCache.getMainAttributes((Map<String, Object>)new StringMap(), (InputStream)new ByteArrayInputStream(versionManifestInput), versionManifestInput.length);
                if (versionManifest.get("Import-Package") != null) {
                    manifest.put("Import-Package", versionManifest.get("Import-Package"));
                }
                if (versionManifest.get("Require-Capability") == null) break;
                manifest.put("Require-Capability", versionManifest.get("Require-Capability"));
                break;
            }
        }
        return manifest;
    }

    public static <T> List<T> newImmutableList(List<T> list) {
        return list == null || list.isEmpty() ? EMPTY_LIST : Collections.unmodifiableList(list);
    }

    public static <K, V> Map<K, V> newImmutableMap(Map<K, V> map) {
        return map == null || map.isEmpty() ? EMPTY_MAP : Collections.unmodifiableMap(map);
    }

    static {
        Properties defaults = null;
        IOException defaultEX = null;
        try {
            defaults = Util.loadDefaultProperties();
        }
        catch (IOException ex) {
            defaultEX = ex;
        }
        DEFAULTS = defaults;
        DEFAULT_EX = defaultEX;
        MODULES_MAP = Util.calculateModulesMap();
        encTab = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        decTab = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};
        EMPTY_LIST = Collections.unmodifiableList(Collections.EMPTY_LIST);
        EMPTY_MAP = Collections.unmodifiableMap(Collections.EMPTY_MAP);
    }
}

