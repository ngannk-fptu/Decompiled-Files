/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.strings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {
    public static String join(String middle, Iterable<?> objects) {
        return Strings.join(middle, objects, null, null);
    }

    public static String join(Iterable<?> objects) {
        return Strings.join(",", objects, null, null);
    }

    public static String join(String middle, Iterable<?> objects, Pattern pattern, String replace) {
        StringBuilder sb = new StringBuilder();
        Strings.join(sb, middle, objects, pattern, replace);
        return sb.toString();
    }

    public static void join(StringBuilder sb, String middle, Iterable<?> objects, Pattern pattern, String replace) {
        String del = "";
        if (objects == null) {
            return;
        }
        for (Object o : objects) {
            if (o == null) continue;
            sb.append(del);
            String s = o.toString();
            if (pattern != null) {
                Matcher matcher = pattern.matcher(s);
                if (!matcher.matches()) continue;
                s = matcher.replaceAll(replace);
            }
            sb.append(s);
            del = middle;
        }
    }

    public static String join(String middle, Object[] segments) {
        return Strings.join(middle, Arrays.asList(segments));
    }

    public static String display(Object o, Object ... ifNull) {
        if (o != null) {
            return o.toString();
        }
        for (int i = 0; i < ifNull.length; ++i) {
            if (ifNull[i] == null) continue;
            return ifNull[i].toString();
        }
        return "";
    }

    public static String join(String[] strings) {
        return Strings.join(",", strings);
    }

    public static String join(Object[] strings) {
        return Strings.join(",", strings);
    }

    public static String getLastSegment(String name, char c) {
        return name.substring(name.lastIndexOf(c) + 1);
    }

    public static String getLastSegment(String name) {
        return Strings.getLastSegment(name, '.');
    }

    public static String trim(String s) {
        int end;
        int start;
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }
        for (start = 0; start < s.length() && Character.isWhitespace(s.charAt(start)); ++start) {
        }
        for (end = s.length(); end > start && Character.isWhitespace(s.charAt(end - 1)); --end) {
        }
        if (start == 0 && end == s.length()) {
            return s;
        }
        return s.substring(start, end);
    }

    public static List<String> split(String s) {
        return Strings.split("\\s*,\\s*", s);
    }

    public static List<String> split(String regex, String s) {
        if (s == null) {
            return Collections.emptyList();
        }
        String[] split = s.split(regex);
        ArrayList<String> l = new ArrayList<String>(split.length);
        for (int i = 0; i < split.length; ++i) {
            l.add(split[i]);
        }
        return l;
    }

    public static boolean in(String[] skip, String key) {
        for (String s : skip) {
            if (!key.equals(s)) continue;
            return true;
        }
        return false;
    }

    public static char charAt(String s, int n) {
        return s.charAt(Strings.adjustBegin(s, n));
    }

    public static String from(String s, int n) {
        return s.substring(Strings.adjustBegin(s, n));
    }

    public static String substring(String s, int begin, int end) {
        return s.substring(Strings.adjustBegin(s, begin), Strings.adjustEnd(s, end));
    }

    public static String substring(String s, int begin, int end, int stride) {
        StringBuilder sb = new StringBuilder();
        begin = Strings.adjustBegin(s, begin);
        end = Strings.adjustEnd(s, end);
        if (stride == 0) {
            stride = 1;
        }
        if (stride < 0) {
            for (int i = end - 1; i >= begin; i += stride) {
                sb.append(s.charAt(i));
            }
        } else {
            for (int i = begin; i < end; i += stride) {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String delete(String s, int begin, int end) {
        return s.substring(0, Strings.adjustBegin(s, begin)) + s.substring(Strings.adjustEnd(s, end));
    }

    public static String to(String s, int end) {
        return s.substring(0, Strings.adjustEnd(s, end));
    }

    public static int adjustBegin(String s, int n) {
        if (n < 0) {
            n = s.length() + n;
        }
        return n;
    }

    public static int adjustEnd(String s, int n) {
        if (n <= 0) {
            n = s.length() + n;
        }
        return n;
    }

    public static String[] extension(String s) {
        return Strings.last(s, '.');
    }

    public static String[] lastPathSegment(String s) {
        return Strings.last(s, '/');
    }

    public static String[] last(String s, char separator) {
        int n = s.lastIndexOf(separator);
        if (n >= 0) {
            String[] answer = new String[]{s.substring(0, n), s.substring(n + 1)};
            return answer;
        }
        return null;
    }

    public static String[] first(String s, char separator) {
        int n = s.indexOf(separator);
        if (n >= 0) {
            String[] answer = new String[]{s.substring(0, n), s.substring(n + 1)};
            return answer;
        }
        return null;
    }

    public static String stripPrefix(String s, String prefix) {
        Pattern p = Pattern.compile(prefix);
        return Strings.stripPrefix(s, p);
    }

    public static String stripPrefix(String s, Pattern p) {
        Matcher matcher = p.matcher(s);
        if (matcher.lookingAt()) {
            return s.substring(matcher.end());
        }
        return null;
    }

    public static String stripSuffix(String s, String prefix) {
        Pattern p = Pattern.compile(prefix);
        return Strings.stripSuffix(s, p);
    }

    public static String stripSuffix(String s, Pattern p) {
        Matcher matcher = p.matcher(s);
        while (matcher.find()) {
            if (matcher.end() != s.length()) continue;
            return s.substring(0, matcher.start());
        }
        return null;
    }

    public static String ensureSuffix(String s, String suffix) {
        if (s.endsWith(suffix)) {
            return s;
        }
        return s + suffix;
    }

    public static String ensurePrefix(String s, String prefix) {
        if (s.startsWith(prefix)) {
            return s;
        }
        return prefix + s;
    }

    public static String times(String s, int times) {
        if (times <= 1) {
            return s;
        }
        StringBuilder sb = new StringBuilder(times * s.length());
        for (int i = 0; i < times; ++i) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String format(String string, Object ... parms) {
        if (parms == null) {
            parms = new Object[]{};
        }
        return String.format(string, Strings.makePrintableArray(parms));
    }

    private static Object[] makePrintableArray(Object array) {
        int length = Array.getLength(array);
        Object[] output = new Object[length];
        for (int i = 0; i < length; ++i) {
            output[i] = Strings.makePrintable(Array.get(array, i));
        }
        return output;
    }

    private static Object makePrintable(Object object) {
        if (object == null) {
            return null;
        }
        if (object.getClass().isArray()) {
            return Arrays.toString(Strings.makePrintableArray(object));
        }
        return object;
    }
}

