/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public final class JarUtils {
    public static final Pattern URL_SCHEME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9+-.]+[:].*");
    private static final Pattern DASH_VERSION = Pattern.compile("-(\\d+(\\.|$))");
    private static final Pattern NON_ALPHANUM = Pattern.compile("[^A-Za-z0-9]");
    private static final Pattern REPEATING_DOTS = Pattern.compile("(\\.)(\\1)+");
    private static final Pattern LEADING_DOTS = Pattern.compile("^\\.");
    private static final Pattern TRAILING_DOTS = Pattern.compile("\\.$");
    private static final String[] UNIX_NON_PATH_SEPARATORS = new String[]{"jar:", "file:", "http://", "https://", "\\:"};
    private static final int[] UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS = new int[UNIX_NON_PATH_SEPARATORS.length];

    private JarUtils() {
    }

    public static String[] smartPathSplit(String pathStr, ScanSpec scanSpec) {
        return JarUtils.smartPathSplit(pathStr, File.pathSeparatorChar, scanSpec);
    }

    public static String[] smartPathSplit(String pathStr, char separatorChar, ScanSpec scanSpec) {
        if (pathStr == null || pathStr.isEmpty()) {
            return new String[0];
        }
        if (separatorChar != ':') {
            ArrayList<String> partsFiltered = new ArrayList<String>();
            for (String part : pathStr.split(String.valueOf(separatorChar))) {
                String partFiltered = part.trim();
                if (partFiltered.isEmpty()) continue;
                partsFiltered.add(partFiltered);
            }
            return partsFiltered.toArray(new String[0]);
        }
        HashSet<Integer> splitPoints = new HashSet<Integer>();
        int i = -1;
        do {
            boolean foundNonPathSeparator = false;
            for (int j = 0; j < UNIX_NON_PATH_SEPARATORS.length; ++j) {
                int startIdx = i - UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS[j];
                if (!pathStr.regionMatches(true, startIdx, UNIX_NON_PATH_SEPARATORS[j], 0, UNIX_NON_PATH_SEPARATORS[j].length()) || startIdx != 0 && pathStr.charAt(startIdx - 1) != ':') continue;
                foundNonPathSeparator = true;
                break;
            }
            if (!foundNonPathSeparator && scanSpec != null && scanSpec.allowedURLSchemes != null && !scanSpec.allowedURLSchemes.isEmpty()) {
                for (String scheme : scanSpec.allowedURLSchemes) {
                    int schemeLen;
                    int startIdx;
                    if (scheme.equals("http") || scheme.equals("https") || scheme.equals("jar") || scheme.equals("file") || !pathStr.regionMatches(true, startIdx = i - (schemeLen = scheme.length()), scheme, 0, schemeLen) || startIdx != 0 && pathStr.charAt(startIdx - 1) != ':') continue;
                    foundNonPathSeparator = true;
                    break;
                }
            }
            if (foundNonPathSeparator) continue;
            splitPoints.add(i);
        } while ((i = pathStr.indexOf(58, i + 1)) >= 0);
        splitPoints.add(pathStr.length());
        ArrayList splitPointsSorted = new ArrayList(splitPoints);
        CollectionUtils.sortIfNotEmpty(splitPointsSorted);
        ArrayList<String> parts = new ArrayList<String>();
        for (int i2 = 1; i2 < splitPointsSorted.size(); ++i2) {
            int idx0 = (Integer)splitPointsSorted.get(i2 - 1);
            int idx1 = (Integer)splitPointsSorted.get(i2);
            String part = pathStr.substring(idx0 + 1, idx1).trim();
            if ((part = part.replaceAll("\\\\:", ":")).isEmpty()) continue;
            parts.add(part);
        }
        return parts.toArray(new String[0]);
    }

    private static void appendPathElt(Object pathElt, StringBuilder buf) {
        if (buf.length() > 0) {
            buf.append(File.pathSeparatorChar);
        }
        String path = File.separatorChar == '\\' ? pathElt.toString() : pathElt.toString().replaceAll(File.pathSeparator, "\\" + File.pathSeparator);
        buf.append(path);
    }

    public static String pathElementsToPathStr(Object ... pathElts) {
        StringBuilder buf = new StringBuilder();
        for (Object pathElt : pathElts) {
            JarUtils.appendPathElt(pathElt, buf);
        }
        return buf.toString();
    }

    public static String pathElementsToPathStr(Iterable<?> pathElts) {
        StringBuilder buf = new StringBuilder();
        for (Object pathElt : pathElts) {
            JarUtils.appendPathElt(pathElt, buf);
        }
        return buf.toString();
    }

    public static String leafName(String path) {
        int bangIdx = path.indexOf(33);
        int endIdx = bangIdx >= 0 ? bangIdx : path.length();
        int leafStartIdx = 1 + (File.separatorChar == '/' ? path.lastIndexOf(47, endIdx) : Math.max(path.lastIndexOf(47, endIdx), path.lastIndexOf(File.separatorChar, endIdx)));
        int sepIdx = path.indexOf("---");
        if (sepIdx >= 0) {
            sepIdx += "---".length();
        }
        leafStartIdx = Math.max(leafStartIdx, sepIdx);
        leafStartIdx = Math.min(leafStartIdx, endIdx);
        return path.substring(leafStartIdx, endIdx);
    }

    public static String classfilePathToClassName(String classfilePath) {
        if (!classfilePath.endsWith(".class")) {
            throw new IllegalArgumentException("Classfile path does not end with \".class\": " + classfilePath);
        }
        return classfilePath.substring(0, classfilePath.length() - 6).replace('/', '.');
    }

    public static String classNameToClassfilePath(String className) {
        return className.replace('.', '/') + ".class";
    }

    public static String derivedAutomaticModuleName(String jarPath) {
        int len;
        String moduleName;
        Matcher matcher;
        int endIdx = jarPath.length();
        int lastPlingIdx = jarPath.lastIndexOf(33);
        if (lastPlingIdx > 0 && jarPath.lastIndexOf(46) <= Math.max(lastPlingIdx, jarPath.lastIndexOf(47))) {
            endIdx = lastPlingIdx;
        }
        int secondToLastPlingIdx = endIdx == 0 ? -1 : jarPath.lastIndexOf("!", endIdx - 1);
        int startIdx = Math.max(secondToLastPlingIdx, jarPath.lastIndexOf(47, endIdx - 1)) + 1;
        int lastDotBeforeLastPlingIdx = jarPath.lastIndexOf(46, endIdx - 1);
        if (lastDotBeforeLastPlingIdx > startIdx) {
            endIdx = lastDotBeforeLastPlingIdx;
        }
        if ((matcher = DASH_VERSION.matcher(moduleName = jarPath.substring(startIdx, endIdx))).find()) {
            moduleName = moduleName.substring(0, matcher.start());
        }
        moduleName = NON_ALPHANUM.matcher(moduleName).replaceAll(".");
        if ((moduleName = REPEATING_DOTS.matcher(moduleName).replaceAll(".")).length() > 0 && moduleName.charAt(0) == '.') {
            moduleName = LEADING_DOTS.matcher(moduleName).replaceAll("");
        }
        if ((len = moduleName.length()) > 0 && moduleName.charAt(len - 1) == '.') {
            moduleName = TRAILING_DOTS.matcher(moduleName).replaceAll("");
        }
        return moduleName;
    }

    static {
        for (int i = 0; i < UNIX_NON_PATH_SEPARATORS.length; ++i) {
            JarUtils.UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS[i] = UNIX_NON_PATH_SEPARATORS[i].indexOf(58);
            if (UNIX_NON_PATH_SEPARATOR_COLON_POSITIONS[i] >= 0) continue;
            throw new RuntimeException("Could not find ':' in \"" + UNIX_NON_PATH_SEPARATORS[i] + "\"");
        }
    }
}

