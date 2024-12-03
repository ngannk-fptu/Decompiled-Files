/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util.manifestparser;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.VersionConverter;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

public class NativeLibraryClause {
    private static final String OS_AIX = "aix";
    private static final String OS_DIGITALUNIX = "digitalunix";
    private static final String OS_EPOC = "epoc32";
    private static final String OS_HPUX = "hpux";
    private static final String OS_IRIX = "irix";
    private static final String OS_LINUX = "linux";
    private static final String OS_MACOS = "macos";
    private static final String OS_MACOSX = "macosx";
    private static final String OS_NETBSD = "netbsd";
    private static final String OS_NETWARE = "netware";
    private static final String OS_OPENBSD = "openbsd";
    private static final String OS_OS2 = "os2";
    private static final String OS_QNX = "qnx";
    private static final String OS_SOLARIS = "solaris";
    private static final String OS_SUNOS = "sunos";
    private static final String OS_VXWORKS = "vxworks";
    private static final String OS_WINDOWS_2000 = "windows2000";
    private static final String OS_WINDOWS_2003 = "windows2003";
    private static final String OS_WINDOWS_7 = "windows7";
    private static final String OS_WINDOWS_8 = "windows8";
    private static final String OS_WINDOWS_9 = "windows9";
    private static final String OS_WINDOWS_10 = "windows10";
    private static final String OS_WINDOWS_95 = "windows95";
    private static final String OS_WINDOWS_98 = "windows98";
    private static final String OS_WINDOWS_CE = "windowsce";
    private static final String OS_WINDOWS_NT = "windowsnt";
    private static final String OS_WINDOWS_SERVER_2008 = "windowsserver2008";
    private static final String OS_WINDOWS_SERVER_2012 = "windowsserver2012";
    private static final String OS_WINDOWS_SERVER_2016 = "windowsserver2016";
    private static final String OS_WINDOWS_SERVER_2019 = "windowsserver2019";
    private static final String OS_WINDOWS_VISTA = "windowsvista";
    private static final String OS_WINDOWS_XP = "windowsxp";
    private static final String OS_WIN_32 = "win32";
    private static final String PROC_X86_64 = "x86-64";
    private static final String PROC_X86 = "x86";
    private static final String PROC_68K = "68k";
    private static final String PROC_ARM_LE = "arm_le";
    private static final String PROC_ARM_BE = "arm_be";
    private static final String PROC_ARM = "arm";
    private static final String PROC_ALPHA = "alpha";
    private static final String PROC_IGNITE = "ignite";
    private static final String PROC_MIPS = "mips";
    private static final String PROC_PARISC = "parisc";
    private static final String PROC_POWER_PC = "powerpc";
    private static final String PROC_SPARC = "sparc";
    private static final Map<String, List<String>> OS_ALIASES = new HashMap<String, List<String>>();
    private static final Map<String, List<String>> PROC_ALIASES = new HashMap<String, List<String>>();
    private final String[] m_libraryEntries;
    private final String[] m_osnames;
    private final String[] m_processors;
    private final String[] m_osversions;
    private final String[] m_languages;
    private final String m_selectionFilter;

    public NativeLibraryClause(String[] libraryEntries, String[] osnames, String[] processors, String[] osversions, String[] languages, String selectionFilter) {
        this.m_libraryEntries = libraryEntries;
        this.m_osnames = osnames;
        this.m_processors = processors;
        this.m_osversions = osversions;
        this.m_languages = languages;
        this.m_selectionFilter = selectionFilter;
    }

    public NativeLibraryClause(NativeLibraryClause library) {
        this(library.m_libraryEntries, library.m_osnames, library.m_osversions, library.m_processors, library.m_languages, library.m_selectionFilter);
    }

    public static synchronized void initializeNativeAliases(Map configMap) {
        Map<String, String> osNameKeyMap = NativeLibraryClause.getAllKeysWithPrefix("felix.native.osname.alias", configMap);
        Map<String, String> processorKeyMap = NativeLibraryClause.getAllKeysWithPrefix("felix.native.processor.alias", configMap);
        NativeLibraryClause.parseNativeAliases(osNameKeyMap, OS_ALIASES);
        NativeLibraryClause.parseNativeAliases(processorKeyMap, PROC_ALIASES);
    }

    private static void parseNativeAliases(Map<String, String> aliasStringMap, Map<String, List<String>> aliasMap) {
        for (Map.Entry<String, String> aliasEntryString : aliasStringMap.entrySet()) {
            String currentAliasKey = aliasEntryString.getKey();
            String currentNormalizedName = currentAliasKey.substring(currentAliasKey.lastIndexOf(".") + 1);
            String currentAliasesString = aliasEntryString.getValue();
            if (currentAliasesString != null) {
                String[] aliases = currentAliasesString.split(",");
                ArrayList<String> fullAliasList = new ArrayList<String>();
                fullAliasList.add(currentNormalizedName);
                fullAliasList.addAll(Arrays.asList(aliases));
                aliasMap.put(currentNormalizedName, fullAliasList);
                for (String currentAlias : aliases) {
                    List<String> aliasList = aliasMap.get(currentAlias);
                    if (aliasList == null) {
                        aliasMap.put(currentAlias, fullAliasList);
                        continue;
                    }
                    for (String newAliases : aliases) {
                        if (aliasList.contains(newAliases)) continue;
                        aliasList.add(newAliases);
                    }
                }
                continue;
            }
            List<String> aliasList = aliasMap.get(currentNormalizedName);
            if (aliasList == null) {
                aliasMap.put(currentNormalizedName, new ArrayList<String>(Collections.singletonList(currentNormalizedName)));
                continue;
            }
            aliasList.add(0, currentNormalizedName);
        }
    }

    private static Map<String, String> getAllKeysWithPrefix(String prefix, Map<String, String> configMap) {
        HashMap<String, String> keysWithPrefix = new HashMap<String, String>();
        for (Map.Entry<String, String> currentEntry : configMap.entrySet()) {
            if (!currentEntry.getKey().startsWith(prefix)) continue;
            keysWithPrefix.put(currentEntry.getKey(), currentEntry.getValue());
        }
        return keysWithPrefix;
    }

    public String[] getLibraryEntries() {
        return this.m_libraryEntries;
    }

    public String[] getOSNames() {
        return this.m_osnames;
    }

    public String[] getProcessors() {
        return this.m_processors;
    }

    public String[] getOSVersions() {
        return this.m_osversions;
    }

    public String[] getLanguages() {
        return this.m_languages;
    }

    public String getSelectionFilter() {
        return this.m_selectionFilter;
    }

    public boolean match(Map configMap) throws BundleException {
        String osName = (String)configMap.get("org.osgi.framework.os.name");
        String processorName = (String)configMap.get("org.osgi.framework.processor");
        String osVersion = (String)configMap.get("org.osgi.framework.os.version");
        String language = (String)configMap.get("org.osgi.framework.language");
        if (this.getOSNames() != null && this.getOSNames().length > 0 && !this.checkOSNames(osName, this.getOSNames())) {
            return false;
        }
        if (this.getProcessors() != null && this.getProcessors().length > 0 && !this.checkProcessors(processorName, this.getProcessors())) {
            return false;
        }
        if (this.getOSVersions() != null && this.getOSVersions().length > 0 && !this.checkOSVersions(osVersion, this.getOSVersions())) {
            return false;
        }
        if (this.getLanguages() != null && this.getLanguages().length > 0 && !this.checkLanguages(language, this.getLanguages())) {
            return false;
        }
        return this.getSelectionFilter() == null || this.checkSelectionFilter(configMap, this.getSelectionFilter());
    }

    private boolean checkOSNames(String osName, String[] osnames) {
        List<String> capabilityOsNames = NativeLibraryClause.getOsNameWithAliases(osName);
        if (capabilityOsNames != null && osnames != null) {
            for (String curOsName : osnames) {
                if (!capabilityOsNames.contains(curOsName)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkProcessors(String processorName, String[] processors) {
        List<String> capabilitiesProcessors = NativeLibraryClause.getProcessorWithAliases(processorName);
        if (capabilitiesProcessors != null && processors != null) {
            for (String currentProcessor : processors) {
                if (!capabilitiesProcessors.contains(currentProcessor)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkOSVersions(String osVersion, String[] osversions) throws BundleException {
        Version currentOSVersion = VersionConverter.toOsgiVersion(osVersion);
        for (int i = 0; osversions != null && i < osversions.length; ++i) {
            try {
                VersionRange range = new VersionRange(osversions[i]);
                if (!range.includes(currentOSVersion)) continue;
                return true;
            }
            catch (Exception ex) {
                throw new BundleException("Error evaluating osversion: " + osversions[i], ex);
            }
        }
        return false;
    }

    private boolean checkLanguages(String currentLanguage, String[] languages) {
        for (int i = 0; languages != null && i < languages.length; ++i) {
            if (!languages[i].equals(currentLanguage)) continue;
            return true;
        }
        return false;
    }

    private boolean checkSelectionFilter(Map configMap, String expr) throws BundleException {
        Hashtable dict = new Hashtable();
        for (Object key : configMap.keySet()) {
            ((Dictionary)dict).put(key, configMap.get(key));
        }
        try {
            Filter filter = FrameworkUtil.createFilter(expr);
            return filter.match(dict);
        }
        catch (Exception ex) {
            throw new BundleException("Error evaluating filter expression: " + expr, ex);
        }
    }

    public static NativeLibraryClause parse(Logger logger, String s) {
        try {
            if (s == null || s.length() == 0) {
                return null;
            }
            if ((s = s.trim()).equals("*")) {
                return new NativeLibraryClause(null, null, null, null, null, null);
            }
            StringTokenizer st = new StringTokenizer(s, ";");
            String[] libEntries = new String[st.countTokens()];
            ArrayList<String> osNameList = new ArrayList<String>();
            ArrayList<String> osVersionList = new ArrayList<String>();
            ArrayList<String> processorList = new ArrayList<String>();
            ArrayList<String> languageList = new ArrayList<String>();
            String selectionFilter = null;
            int libCount = 0;
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if (token.indexOf(61) < 0) {
                    libEntries[libCount] = token.charAt(0) == '/' ? token.substring(1) : token;
                    ++libCount;
                    continue;
                }
                String property = null;
                String value = null;
                if (token.indexOf("=") <= 1) {
                    throw new IllegalArgumentException("Bundle manifest native library entry malformed: " + token);
                }
                property = token.substring(0, token.indexOf("=")).trim().toLowerCase();
                value = token.substring(token.indexOf("=") + 1, token.length()).trim();
                if (value.charAt(0) == '\"') {
                    value = value.charAt(value.length() - 1) == '\"' ? value.substring(1, value.length() - 1) : value.substring(1);
                }
                if (value != null) {
                    value = value.toLowerCase();
                }
                if (property.equals("osname")) {
                    osNameList.add(value);
                    continue;
                }
                if (property.equals("osversion")) {
                    osVersionList.add(NativeLibraryClause.normalizeOSVersionRange(value));
                    continue;
                }
                if (property.equals("processor")) {
                    processorList.add(value);
                    continue;
                }
                if (property.equals("language")) {
                    languageList.add(value);
                    continue;
                }
                if (!property.equals("selection-filter")) continue;
                selectionFilter = value;
            }
            if (libCount == 0) {
                return null;
            }
            String[] actualLibEntries = new String[libCount];
            System.arraycopy(libEntries, 0, actualLibEntries, 0, libCount);
            return new NativeLibraryClause(actualLibEntries, osNameList.toArray(new String[osNameList.size()]), processorList.toArray(new String[processorList.size()]), osVersionList.toArray(new String[osVersionList.size()]), languageList.toArray(new String[languageList.size()]), selectionFilter);
        }
        catch (RuntimeException ex) {
            logger.log(1, "Error parsing native library header.", ex);
            throw ex;
        }
    }

    public static List<String> getOsNameWithAliases(String osName) {
        List<String> result = OS_ALIASES.get(osName = NativeLibraryClause.normalizeOSName(osName));
        if (result == null) {
            result = Collections.singletonList(osName);
        }
        return Collections.unmodifiableList(result);
    }

    public static List<String> getProcessorWithAliases(String processor) {
        List<String> result = PROC_ALIASES.get(processor = NativeLibraryClause.normalizeProcessor(processor));
        if (result == null) {
            result = Collections.singletonList(processor);
        }
        return Collections.unmodifiableList(result);
    }

    public static String normalizeOSName(String value) {
        if (OS_ALIASES.containsKey(value = value.toLowerCase())) {
            return OS_ALIASES.get(value).get(0);
        }
        if (value.startsWith("win")) {
            String os = OS_WIN_32;
            if (value.indexOf("32") >= 0 || value.indexOf("*") >= 0) {
                os = OS_WIN_32;
            } else if (value.indexOf("95") >= 0) {
                os = OS_WINDOWS_95;
            } else if (value.indexOf("98") >= 0) {
                os = OS_WINDOWS_98;
            } else if (value.indexOf("nt") >= 0) {
                os = OS_WINDOWS_NT;
            } else if (value.indexOf("2000") >= 0) {
                os = OS_WINDOWS_2000;
            } else if (value.indexOf("2003") >= 0) {
                os = OS_WINDOWS_2003;
            } else if (value.indexOf("2008") >= 0) {
                os = OS_WINDOWS_SERVER_2008;
            } else if (value.indexOf("2012") >= 0) {
                os = OS_WINDOWS_SERVER_2012;
            } else if (value.indexOf("2016") >= 0) {
                os = OS_WINDOWS_SERVER_2016;
            } else if (value.indexOf("2019") >= 0) {
                os = OS_WINDOWS_SERVER_2019;
            } else if (value.indexOf("xp") >= 0) {
                os = OS_WINDOWS_XP;
            } else if (value.indexOf("ce") >= 0) {
                os = OS_WINDOWS_CE;
            } else if (value.indexOf("vista") >= 0) {
                os = OS_WINDOWS_VISTA;
            } else if (value.indexOf(" 7") >= 0 || value.startsWith(OS_WINDOWS_7) || value.equals("win7")) {
                os = OS_WINDOWS_7;
            } else if (value.indexOf(" 8") >= 0 || value.startsWith(OS_WINDOWS_8) || value.equals("win8")) {
                os = OS_WINDOWS_8;
            } else if (value.indexOf(" 9") >= 0 || value.startsWith(OS_WINDOWS_9) || value.equals("win9")) {
                os = OS_WINDOWS_9;
            } else if (value.indexOf(" 10") >= 0 || value.startsWith(OS_WINDOWS_10) || value.equals("win10")) {
                os = OS_WINDOWS_10;
            }
            return os;
        }
        if (value.startsWith(OS_LINUX)) {
            return OS_LINUX;
        }
        if (value.startsWith(OS_AIX)) {
            return OS_AIX;
        }
        if (value.startsWith(OS_DIGITALUNIX)) {
            return OS_DIGITALUNIX;
        }
        if (value.startsWith(OS_HPUX)) {
            return OS_HPUX;
        }
        if (value.startsWith(OS_IRIX)) {
            return OS_IRIX;
        }
        if (value.startsWith(OS_MACOSX) || value.startsWith("mac os x")) {
            return OS_MACOSX;
        }
        if (value.startsWith(OS_MACOS) || value.startsWith("mac os")) {
            return OS_MACOS;
        }
        if (value.startsWith(OS_NETWARE)) {
            return OS_NETWARE;
        }
        if (value.startsWith(OS_OPENBSD)) {
            return OS_OPENBSD;
        }
        if (value.startsWith(OS_NETBSD)) {
            return OS_NETBSD;
        }
        if (value.startsWith(OS_OS2) || value.startsWith("os/2")) {
            return OS_OS2;
        }
        if (value.startsWith(OS_QNX) || value.startsWith("procnto")) {
            return OS_QNX;
        }
        if (value.startsWith(OS_SOLARIS)) {
            return OS_SOLARIS;
        }
        if (value.startsWith(OS_SUNOS)) {
            return OS_SUNOS;
        }
        if (value.startsWith(OS_VXWORKS)) {
            return OS_VXWORKS;
        }
        if (value.startsWith(OS_EPOC)) {
            return OS_EPOC;
        }
        return value;
    }

    public static String normalizeProcessor(String value) {
        if (PROC_ALIASES.containsKey(value = value.toLowerCase())) {
            return PROC_ALIASES.get(value).get(0);
        }
        if (value.startsWith(PROC_X86_64) || value.startsWith("amd64") || value.startsWith("em64") || value.startsWith("x86_64")) {
            return PROC_X86_64;
        }
        if (value.startsWith(PROC_X86) || value.startsWith("pentium") || value.startsWith("i386") || value.startsWith("i486") || value.startsWith("i586") || value.startsWith("i686")) {
            return PROC_X86;
        }
        if (value.startsWith(PROC_68K)) {
            return PROC_68K;
        }
        if (value.startsWith(PROC_ARM_LE)) {
            return PROC_ARM_LE;
        }
        if (value.startsWith(PROC_ARM_BE)) {
            return PROC_ARM_BE;
        }
        if (value.startsWith(PROC_ARM)) {
            return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? PROC_ARM_BE : PROC_ARM_LE;
        }
        if (value.startsWith(PROC_ALPHA)) {
            return PROC_ALPHA;
        }
        if (value.startsWith(PROC_IGNITE) || value.startsWith("psc1k")) {
            return PROC_IGNITE;
        }
        if (value.startsWith(PROC_MIPS)) {
            return PROC_MIPS;
        }
        if (value.startsWith(PROC_PARISC)) {
            return PROC_PARISC;
        }
        if (value.startsWith(PROC_POWER_PC) || value.startsWith("power") || value.startsWith("ppc")) {
            return PROC_POWER_PC;
        }
        if (value.startsWith(PROC_SPARC)) {
            return PROC_SPARC;
        }
        return value;
    }

    public static String normalizeOSVersionRange(String value) {
        if (value.indexOf(44) >= 0) {
            try {
                String s = value.substring(1, value.length() - 1);
                String vlo = s.substring(0, s.indexOf(44)).trim();
                String vhi = s.substring(s.indexOf(44) + 1, s.length()).trim();
                return new VersionRange(value.charAt(0), VersionConverter.toOsgiVersion(vlo), VersionConverter.toOsgiVersion(vhi), value.charAt(value.length() - 1)).toString();
            }
            catch (Exception ex) {
                return Version.emptyVersion.toString();
            }
        }
        return VersionConverter.toOsgiVersion(value).toString();
    }

    @Deprecated
    public static String normalizeOSVersion(String value) {
        return VersionConverter.toOsgiVersion(value).toString();
    }
}

