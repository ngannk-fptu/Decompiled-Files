/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class StringUtils {
    private static final String EOL = System.getProperty("line.separator");

    public String concat(List list) {
        StringBuffer sb = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            sb.append(list.get(i).toString());
        }
        return sb.toString();
    }

    public static String getPackageAsPath(String pckge) {
        return pckge.replace('.', File.separator.charAt(0)) + File.separator;
    }

    public static String removeUnderScores(String data) {
        String temp = null;
        StringBuffer out = new StringBuffer();
        temp = data;
        StringTokenizer st = new StringTokenizer(temp, "_");
        while (st.hasMoreTokens()) {
            String element = (String)st.nextElement();
            out.append(StringUtils.firstLetterCaps(element));
        }
        return out.toString();
    }

    public static String removeAndHump(String data) {
        return StringUtils.removeAndHump(data, "_");
    }

    public static String removeAndHump(String data, String replaceThis) {
        String temp = null;
        StringBuffer out = new StringBuffer();
        temp = data;
        StringTokenizer st = new StringTokenizer(temp, replaceThis);
        while (st.hasMoreTokens()) {
            String element = (String)st.nextElement();
            out.append(StringUtils.capitalizeFirstLetter(element));
        }
        return out.toString();
    }

    public static String firstLetterCaps(String data) {
        String firstLetter = data.substring(0, 1).toUpperCase();
        String restLetters = data.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }

    public static String capitalizeFirstLetter(String data) {
        String firstLetter = data.substring(0, 1).toUpperCase();
        String restLetters = data.substring(1);
        return firstLetter + restLetters;
    }

    public static String[] split(String line, String delim) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer t = new StringTokenizer(line, delim);
        while (t.hasMoreTokens()) {
            list.add(t.nextToken());
        }
        return list.toArray(new String[list.size()]);
    }

    public static String chop(String s, int i) {
        return StringUtils.chop(s, i, EOL);
    }

    public static String chop(String s, int i, String eol) {
        if (i == 0 || s == null || eol == null) {
            return s;
        }
        int length = s.length();
        if (eol.length() == 2 && s.endsWith(eol)) {
            length -= 2;
            --i;
        }
        if (i > 0) {
            length -= i;
        }
        if (length < 0) {
            length = 0;
        }
        return s.substring(0, length);
    }

    public static StringBuffer stringSubstitution(String argStr, Hashtable vars) {
        return StringUtils.stringSubstitution(argStr, (Map)vars);
    }

    public static StringBuffer stringSubstitution(String argStr, Map vars) {
        StringBuffer argBuf = new StringBuffer();
        int cIdx = 0;
        block3: while (cIdx < argStr.length()) {
            char ch = argStr.charAt(cIdx);
            switch (ch) {
                case '$': {
                    String value;
                    StringBuffer nameBuf = new StringBuffer();
                    ++cIdx;
                    while (cIdx < argStr.length() && ((ch = argStr.charAt(cIdx)) == '_' || Character.isLetterOrDigit(ch))) {
                        nameBuf.append(ch);
                        ++cIdx;
                    }
                    if (nameBuf.length() <= 0 || (value = (String)vars.get(nameBuf.toString())) == null) continue block3;
                    argBuf.append(value);
                    break;
                }
                default: {
                    argBuf.append(ch);
                    ++cIdx;
                }
            }
        }
        return argBuf;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String fileContentsToString(String file) {
        String contents;
        block8: {
            contents = "";
            File f = null;
            try {
                f = new File(file);
                if (!f.exists()) break block8;
                try (FileReader fr = null;){
                    fr = new FileReader(f);
                    char[] template = new char[(int)f.length()];
                    fr.read(template);
                    contents = new String(template);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contents;
    }

    public static String collapseNewlines(String argStr) {
        char last = argStr.charAt(0);
        StringBuffer argBuf = new StringBuffer();
        for (int cIdx = 0; cIdx < argStr.length(); ++cIdx) {
            char ch = argStr.charAt(cIdx);
            if (ch == '\n' && last == '\n') continue;
            argBuf.append(ch);
            last = ch;
        }
        return argBuf.toString();
    }

    public static String collapseSpaces(String argStr) {
        char last = argStr.charAt(0);
        StringBuffer argBuf = new StringBuffer();
        for (int cIdx = 0; cIdx < argStr.length(); ++cIdx) {
            char ch = argStr.charAt(cIdx);
            if (ch == ' ' && last == ' ') continue;
            argBuf.append(ch);
            last = ch;
        }
        return argBuf.toString();
    }

    public static final String sub(String line, String oldString, String newString) {
        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            int j = i += oLength;
            while ((i = line.indexOf(oldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                j = i += oLength;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    public static final String stackTrace(Throwable e) {
        String foo = null;
        try {
            ByteArrayOutputStream ostr = new ByteArrayOutputStream();
            e.printStackTrace(new PrintWriter(ostr, true));
            foo = ostr.toString();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return foo;
    }

    public static final String normalizePath(String path) {
        int index;
        String normalized = path;
        if (normalized.indexOf(92) >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        while ((index = normalized.indexOf("//")) >= 0) {
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while ((index = normalized.indexOf("%20")) >= 0) {
            normalized = normalized.substring(0, index) + " " + normalized.substring(index + 3);
        }
        while ((index = normalized.indexOf("/./")) >= 0) {
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while ((index = normalized.indexOf("/../")) >= 0) {
            if (index == 0) {
                return null;
            }
            int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
        return normalized;
    }

    public String select(boolean state, String trueString, String falseString) {
        if (state) {
            return trueString;
        }
        return falseString;
    }

    public boolean allEmpty(List list) {
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            if (list.get(i) == null || list.get(i).toString().length() <= 0) continue;
            return false;
        }
        return true;
    }

    public static List trimStrings(List list) {
        if (list == null) {
            return null;
        }
        int sz = list.size();
        for (int i = 0; i < sz; ++i) {
            list.set(i, StringUtils.nullTrim((String)list.get(i)));
        }
        return list;
    }

    public static String nullTrim(String s) {
        if (s == null) {
            return null;
        }
        return s.trim();
    }
}

