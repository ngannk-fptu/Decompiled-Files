/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

import com.twelvemonkeys.util.StringTokenIterator;
import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class StringUtil {
    public static final String DELIMITER_STRING = ", \t\n\r\f";

    private StringUtil() {
    }

    public static String decode(byte[] byArray, int n, int n2, String string) {
        try {
            return new String(byArray, n, n2, string);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new UnsupportedCharsetException(string);
        }
    }

    public static String valueOf(Object object) {
        return object != null ? object.toString() : null;
    }

    public static String toUpperCase(String string) {
        if (string != null) {
            return string.toUpperCase();
        }
        return null;
    }

    public static String toLowerCase(String string) {
        if (string != null) {
            return string.toLowerCase();
        }
        return null;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isEmpty(String[] stringArray) {
        if (stringArray == null) {
            return true;
        }
        for (String string : stringArray) {
            if (StringUtil.isEmpty(string)) continue;
            return false;
        }
        return true;
    }

    public static boolean contains(String string, String string2) {
        return string != null && string2 != null && string.indexOf(string2) >= 0;
    }

    public static boolean containsIgnoreCase(String string, String string2) {
        return StringUtil.indexOfIgnoreCase(string, string2, 0) >= 0;
    }

    public static boolean contains(String string, int n) {
        return string != null && string.indexOf(n) >= 0;
    }

    public static boolean containsIgnoreCase(String string, int n) {
        return string != null && (string.indexOf(Character.toLowerCase((char)n)) >= 0 || string.indexOf(Character.toUpperCase((char)n)) >= 0);
    }

    public static int indexOfIgnoreCase(String string, String string2) {
        return StringUtil.indexOfIgnoreCase(string, string2, 0);
    }

    public static int indexOfIgnoreCase(String string, String string2, int n) {
        if (string == null || string2 == null) {
            return -1;
        }
        if (string2.length() == 0) {
            return n;
        }
        if (string2.length() > string.length()) {
            return -1;
        }
        char c = Character.toLowerCase(string2.charAt(0));
        char c2 = Character.toUpperCase(string2.charAt(0));
        int n2 = 0;
        int n3 = 0;
        for (int i = n; i <= string.length() - string2.length(); ++i) {
            n2 = n2 >= 0 && n2 <= i ? string.indexOf(c, i) : n2;
            int n4 = n3 = n3 >= 0 && n3 <= i ? string.indexOf(c2, i) : n3;
            if (n2 < 0) {
                if (n3 < 0) {
                    return -1;
                }
                i = n3;
            } else if (n3 < 0) {
                i = n2;
            } else {
                int n5 = i = n2 < n3 ? n2 : n3;
            }
            if (string2.length() == 1) {
                return i;
            }
            if (i > string.length() - string2.length()) {
                return -1;
            }
            if (string.charAt(i + string2.length() - 1) != Character.toLowerCase(string2.charAt(string2.length() - 1)) && string.charAt(i + string2.length() - 1) != Character.toUpperCase(string2.charAt(string2.length() - 1)) || string2.length() > 2 && !string.regionMatches(true, i + 1, string2, 1, string2.length() - 2)) continue;
            return i;
        }
        return -1;
    }

    public static int lastIndexOfIgnoreCase(String string, String string2) {
        return StringUtil.lastIndexOfIgnoreCase(string, string2, string != null ? string.length() - 1 : -1);
    }

    public static int lastIndexOfIgnoreCase(String string, String string2, int n) {
        if (string == null || string2 == null) {
            return -1;
        }
        if (string2.length() == 0) {
            return n;
        }
        if (string2.length() > string.length()) {
            return -1;
        }
        char c = Character.toLowerCase(string2.charAt(0));
        char c2 = Character.toUpperCase(string2.charAt(0));
        int n2 = n;
        int n3 = n;
        for (int i = n; i >= 0; --i) {
            n2 = n2 >= 0 && n2 >= i ? string.lastIndexOf(c, i) : n2;
            int n4 = n3 = n3 >= 0 && n3 >= i ? string.lastIndexOf(c2, i) : n3;
            if (n2 < 0) {
                if (n3 < 0) {
                    return -1;
                }
                i = n3;
            } else if (n3 < 0) {
                i = n2;
            } else {
                int n5 = i = n2 > n3 ? n2 : n3;
            }
            if (string2.length() == 1) {
                return i;
            }
            if (i > string.length() - string2.length() || string.charAt(i + string2.length() - 1) != Character.toLowerCase(string2.charAt(string2.length() - 1)) && string.charAt(i + string2.length() - 1) != Character.toUpperCase(string2.charAt(string2.length() - 1)) || string2.length() > 2 && !string.regionMatches(true, i + 1, string2, 1, string2.length() - 2)) continue;
            return i;
        }
        return -1;
    }

    public static int indexOfIgnoreCase(String string, int n) {
        return StringUtil.indexOfIgnoreCase(string, n, 0);
    }

    public static int indexOfIgnoreCase(String string, int n, int n2) {
        if (string == null) {
            return -1;
        }
        char c = Character.toLowerCase((char)n);
        char c2 = Character.toUpperCase((char)n);
        int n3 = string.indexOf(c, n2);
        int n4 = string.indexOf(c2, n2);
        if (n3 < 0) {
            return n4;
        }
        if (n4 < 0) {
            return n3;
        }
        return n3 < n4 ? n3 : n4;
    }

    public static int lastIndexOfIgnoreCase(String string, int n) {
        return StringUtil.lastIndexOfIgnoreCase(string, n, string != null ? string.length() : -1);
    }

    public static int lastIndexOfIgnoreCase(String string, int n, int n2) {
        if (string == null) {
            return -1;
        }
        char c = Character.toLowerCase((char)n);
        char c2 = Character.toUpperCase((char)n);
        int n3 = string.lastIndexOf(c, n2);
        int n4 = string.lastIndexOf(c2, n2);
        if (n3 < 0) {
            return n4;
        }
        if (n4 < 0) {
            return n3;
        }
        return n3 > n4 ? n3 : n4;
    }

    public static String ltrim(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isWhitespace(string.charAt(i))) continue;
            if (i == 0) {
                return string;
            }
            return string.substring(i);
        }
        return "";
    }

    public static String rtrim(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        for (int i = string.length(); i > 0; --i) {
            if (Character.isWhitespace(string.charAt(i - 1))) continue;
            if (i == string.length()) {
                return string;
            }
            return string.substring(0, i);
        }
        return "";
    }

    public static String replace(String string, String string2, String string3) {
        int n;
        if (string2.length() == 0) {
            return string;
        }
        int n2 = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((n = string.indexOf(string2, n2)) != -1) {
            stringBuilder.append(string.substring(n2, n));
            stringBuilder.append(string3);
            n2 = n + string2.length();
        }
        stringBuilder.append(string.substring(n2));
        return stringBuilder.toString();
    }

    public static String replaceIgnoreCase(String string, String string2, String string3) {
        int n;
        if (string2.length() == 0) {
            return string;
        }
        int n2 = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((n = StringUtil.indexOfIgnoreCase(string, string2, n2)) != -1) {
            stringBuilder.append(string.substring(n2, n));
            stringBuilder.append(string3);
            n2 = n + string2.length();
        }
        stringBuilder.append(string.substring(n2));
        return stringBuilder.toString();
    }

    public static String cut(String string, int n, String string2) {
        int n2;
        if (string == null) {
            return null;
        }
        if (string2 == null) {
            string2 = "";
        }
        if ((n2 = string.length()) <= n) {
            return string;
        }
        n2 = string.lastIndexOf(32, n - string2.length());
        return string.substring(0, n2) + string2;
    }

    public static String capitalize(String string, int n) {
        if (n < 0) {
            throw new IndexOutOfBoundsException("Negative index not allowed: " + n);
        }
        if (string == null || string.length() <= n) {
            return string;
        }
        if (Character.isUpperCase(string.charAt(n))) {
            return string;
        }
        char[] cArray = string.toCharArray();
        cArray[n] = Character.toUpperCase(cArray[n]);
        return new String(cArray);
    }

    public static String capitalize(String string) {
        return StringUtil.capitalize(string, 0);
    }

    @Deprecated
    static String formatNumber(long l, int n) throws IllegalArgumentException {
        StringBuilder stringBuilder = new StringBuilder();
        if ((double)l >= Math.pow(10.0, n)) {
            throw new IllegalArgumentException("The number to format cannot contain more digits than the length argument specifies!");
        }
        for (int i = n; i > 1 && (double)l < Math.pow(10.0, i - 1); --i) {
            stringBuilder.append('0');
        }
        stringBuilder.append(l);
        return stringBuilder.toString();
    }

    public static String pad(String string, int n, String string2, boolean bl) {
        if (string2 == null || string2.length() == 0) {
            throw new IllegalArgumentException("Pad string: \"" + string2 + "\"");
        }
        if (string.length() >= n) {
            return string;
        }
        int n2 = n - string.length();
        StringBuilder stringBuilder = new StringBuilder(string2);
        while (stringBuilder.length() < n2) {
            stringBuilder.append((CharSequence)stringBuilder);
        }
        if (stringBuilder.length() > n2) {
            stringBuilder.delete(n2, stringBuilder.length());
        }
        return bl ? stringBuilder.append(string).toString() : stringBuilder.insert(0, string).toString();
    }

    public static Date toDate(String string) {
        return StringUtil.toDate(string, DateFormat.getInstance());
    }

    public static Date toDate(String string, String string2) {
        return StringUtil.toDate(string, new SimpleDateFormat(string2));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date toDate(String string, DateFormat dateFormat) {
        try {
            DateFormat dateFormat2 = dateFormat;
            synchronized (dateFormat2) {
                return dateFormat.parse(string);
            }
        }
        catch (ParseException parseException) {
            throw new IllegalArgumentException(parseException.getMessage());
        }
    }

    public static Timestamp toTimestamp(String string) {
        return Timestamp.valueOf(string);
    }

    public static String[] toStringArray(String string, String string2) {
        if (StringUtil.isEmpty(string)) {
            return new String[0];
        }
        StringTokenIterator stringTokenIterator = new StringTokenIterator(string, string2);
        ArrayList<String> arrayList = new ArrayList<String>();
        while (stringTokenIterator.hasMoreElements()) {
            arrayList.add(stringTokenIterator.nextToken());
        }
        return arrayList.toArray(new String[arrayList.size()]);
    }

    public static String[] toStringArray(String string) {
        return StringUtil.toStringArray(string, DELIMITER_STRING);
    }

    public static int[] toIntArray(String string, String string2, int n) {
        if (StringUtil.isEmpty(string)) {
            return new int[0];
        }
        String[] stringArray = StringUtil.toStringArray(string, string2);
        int[] nArray = new int[stringArray.length];
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = Integer.parseInt(stringArray[i], n);
        }
        return nArray;
    }

    public static int[] toIntArray(String string) {
        return StringUtil.toIntArray(string, DELIMITER_STRING, 10);
    }

    public static int[] toIntArray(String string, String string2) {
        return StringUtil.toIntArray(string, string2, 10);
    }

    public static long[] toLongArray(String string, String string2) {
        if (StringUtil.isEmpty(string)) {
            return new long[0];
        }
        String[] stringArray = StringUtil.toStringArray(string, string2);
        long[] lArray = new long[stringArray.length];
        for (int i = 0; i < lArray.length; ++i) {
            lArray[i] = Long.parseLong(stringArray[i]);
        }
        return lArray;
    }

    public static long[] toLongArray(String string) {
        return StringUtil.toLongArray(string, DELIMITER_STRING);
    }

    public static double[] toDoubleArray(String string, String string2) {
        if (StringUtil.isEmpty(string)) {
            return new double[0];
        }
        String[] stringArray = StringUtil.toStringArray(string, string2);
        double[] dArray = new double[stringArray.length];
        for (int i = 0; i < dArray.length; ++i) {
            dArray[i] = Double.valueOf(stringArray[i]);
        }
        return dArray;
    }

    public static double[] toDoubleArray(String string) {
        return StringUtil.toDoubleArray(string, DELIMITER_STRING);
    }

    public static Color toColor(String string) {
        if (string == null) {
            return null;
        }
        if (string.charAt(0) == '#') {
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            int n4 = -1;
            if (string.length() >= 7) {
                int n5 = 1;
                if (string.length() >= 9) {
                    n4 = Integer.parseInt(string.substring(n5, n5 + 2), 16);
                    n5 += 2;
                }
                n = Integer.parseInt(string.substring(n5, n5 + 2), 16);
                n2 = Integer.parseInt(string.substring(n5 + 2, n5 + 4), 16);
                n3 = Integer.parseInt(string.substring(n5 + 4, n5 + 6), 16);
            } else if (string.length() >= 4) {
                int n6 = 1;
                if (string.length() >= 5) {
                    n4 = Integer.parseInt(string.substring(n6++, n6), 16) * 16;
                }
                n = Integer.parseInt(string.substring(n6++, n6), 16) * 16;
                n2 = Integer.parseInt(string.substring(n6++, n6), 16) * 16;
                n3 = Integer.parseInt(string.substring(n6++, n6), 16) * 16;
            }
            if (n4 != -1) {
                return new Color(n, n2, n3, n4);
            }
            return new Color(n, n2, n3);
        }
        try {
            int n;
            Class<Color> clazz = Color.class;
            Field field = null;
            try {
                field = clazz.getField(string);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (field == null) {
                field = clazz.getField(string.toLowerCase());
            }
            if (Modifier.isPublic(n = field.getModifiers()) && Modifier.isStatic(n)) {
                return (Color)field.get(null);
            }
        }
        catch (NoSuchFieldException noSuchFieldException) {
            throw new IllegalArgumentException("No such color: " + string);
        }
        catch (SecurityException securityException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return null;
    }

    public static String toColorString(Color color) {
        if (color == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(Integer.toHexString(color.getRGB()));
        for (int i = stringBuilder.length(); i < 8; ++i) {
            stringBuilder.insert(0, '0');
        }
        if (stringBuilder.charAt(0) == 'f' && stringBuilder.charAt(1) == 'f') {
            stringBuilder.delete(0, 2);
        }
        return stringBuilder.insert(0, '#').toString();
    }

    public static boolean isNumber(String string) {
        if (StringUtil.isEmpty(string)) {
            return false;
        }
        char c = string.charAt(0);
        if (c != '-' && !Character.isDigit(c)) {
            return false;
        }
        for (int i = 1; i < string.length(); ++i) {
            if (Character.isDigit(string.charAt(i))) continue;
            return false;
        }
        return true;
    }

    static String ensureIncludesAt(String string, String string2, int n) {
        StringBuilder stringBuilder = new StringBuilder(string);
        try {
            String string3 = string.substring(n, n + string2.length());
            if (!string3.equalsIgnoreCase(string2)) {
                stringBuilder.insert(n, string2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return stringBuilder.toString();
    }

    static String ensureExcludesAt(String string, String string2, int n) {
        StringBuilder stringBuilder = new StringBuilder(string);
        try {
            String string3 = string.substring(n + 1, n + string2.length() + 1);
            if (!string3.equalsIgnoreCase(string2)) {
                stringBuilder.delete(n, n + string2.length());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return stringBuilder.toString();
    }

    public static String substring(String string, String string2, String string3, int n) {
        int n2 = n < 0 ? 0 : n;
        int n3 = string.indexOf(string2, n2) + string2.length();
        if (n3 < 0) {
            return null;
        }
        int n4 = string.indexOf(string3, n3);
        if (n4 < 0) {
            return null;
        }
        return string.substring(n3, n4);
    }

    @Deprecated
    static String removeSubstring(String string, char c, char c2, int n) {
        char[] cArray;
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = false;
        for (char c3 : cArray = string.toCharArray()) {
            if (!bl) {
                if (c3 == c) {
                    bl = true;
                    continue;
                }
                stringBuilder.append(c3);
                continue;
            }
            if (c3 != c2) continue;
            bl = false;
        }
        return stringBuilder.toString();
    }

    static String removeSubstrings(String string, char c, char c2) {
        char[] cArray;
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = false;
        for (char c3 : cArray = string.toCharArray()) {
            if (!bl) {
                if (c3 == c) {
                    bl = true;
                    continue;
                }
                stringBuilder.append(c3);
                continue;
            }
            if (c3 != c2) continue;
            bl = false;
        }
        return stringBuilder.toString();
    }

    public static String getFirstElement(String string, String string2) {
        if (string2 == null) {
            throw new IllegalArgumentException("delimiter == null");
        }
        if (StringUtil.isEmpty(string)) {
            return string;
        }
        int n = string.indexOf(string2);
        if (n >= 0) {
            return string.substring(0, n);
        }
        return string;
    }

    public static String getLastElement(String string, String string2) {
        if (string2 == null) {
            throw new IllegalArgumentException("delimiter == null");
        }
        if (StringUtil.isEmpty(string)) {
            return string;
        }
        int n = string.lastIndexOf(string2);
        if (n >= 0) {
            return string.substring(n + 1);
        }
        return string;
    }

    public static String toCSVString(Object[] objectArray) {
        return StringUtil.toCSVString(objectArray, ", ");
    }

    public static String toCSVString(Object[] objectArray, String string) {
        if (objectArray == null) {
            return "";
        }
        if (string == null) {
            throw new IllegalArgumentException("delimiter == null");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < objectArray.length; ++i) {
            if (i > 0) {
                stringBuilder.append(string);
            }
            stringBuilder.append(objectArray[i]);
        }
        return stringBuilder.toString();
    }

    public static String deepToString(Object object) {
        return StringUtil.deepToString(object, false, 1);
    }

    public static String deepToString(Object object, boolean bl, int n) {
        if (object == null) {
            return null;
        }
        if (!bl && !StringUtil.isIdentityToString(object)) {
            return object.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (object.getClass().isArray()) {
            Class<?> clazz = object.getClass();
            while (clazz.isArray()) {
                stringBuilder.append('[');
                stringBuilder.append(Array.getLength(object));
                stringBuilder.append(']');
                clazz = clazz.getComponentType();
            }
            stringBuilder.insert(0, clazz);
            stringBuilder.append(" {hashCode=");
            stringBuilder.append(Integer.toHexString(object.hashCode()));
            stringBuilder.append("}");
        } else {
            Method[] methodArray;
            if (StringUtil.isIdentityToString(object)) {
                stringBuilder.append(" {");
            } else {
                stringBuilder.append(" {toString=");
                stringBuilder.append(object.toString());
                stringBuilder.append(", ");
            }
            stringBuilder.append("hashCode=");
            stringBuilder.append(Integer.toHexString(object.hashCode()));
            for (Method method : methodArray = object.getClass().getMethods()) {
                Class<?>[] classArray;
                if (!Modifier.isPublic(method.getModifiers())) continue;
                String string = method.getName();
                String string2 = null;
                if (!string.equals("getClass") && string.length() > 3 && string.startsWith("get") && Character.isUpperCase(string.charAt(3))) {
                    string2 = string.substring(3);
                } else if (string.length() > 2 && string.startsWith("is") && Character.isUpperCase(string.charAt(2))) {
                    string2 = string.substring(2);
                }
                if (string2 == null) continue;
                if (string2.length() > 1 && Character.isLowerCase(string2.charAt(1))) {
                    string2 = Character.toLowerCase(string2.charAt(0)) + string2.substring(1);
                }
                boolean bl2 = (classArray = method.getParameterTypes()) != null && classArray.length > 0;
                boolean bl3 = Void.TYPE.equals(method.getReturnType());
                if (bl3 || bl2) continue;
                try {
                    Object object2 = method.invoke(object, new Object[0]);
                    stringBuilder.append(", ");
                    stringBuilder.append(string2);
                    stringBuilder.append('=');
                    if (n != 0 && object2 != null && StringUtil.isIdentityToString(object2)) {
                        stringBuilder.append(StringUtil.deepToString(object2, bl, n > 0 ? n - 1 : -1));
                        continue;
                    }
                    stringBuilder.append(object2);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            stringBuilder.append('}');
            stringBuilder.insert(0, object.getClass().getName());
        }
        return stringBuilder.toString();
    }

    private static boolean isIdentityToString(Object object) {
        try {
            Method method = object.getClass().getMethod("toString", new Class[0]);
            if (method.getDeclaringClass() == Object.class) {
                return true;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    public static String identityToString(Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(object));
    }

    public boolean matches(String string, String string2) throws PatternSyntaxException {
        return Pattern.matches(string2, string);
    }

    public String replaceFirst(String string, String string2, String string3) {
        return Pattern.compile(string2).matcher(string).replaceFirst(string3);
    }

    public String replaceAll(String string, String string2, String string3) {
        return Pattern.compile(string2).matcher(string).replaceAll(string3);
    }

    public String[] split(String string, String string2, int n) {
        return Pattern.compile(string2).split(string, n);
    }

    public String[] split(String string, String string2) {
        return this.split(string, string2, 0);
    }

    public static String camelToLisp(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (string.length() == 0) {
            return string;
        }
        StringBuilder stringBuilder = null;
        int n = 0;
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 1; i < string.length(); ++i) {
            char c;
            char c2 = string.charAt(i);
            if (Character.isUpperCase(c2)) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder(string.length() + 3);
                }
                if (bl2) {
                    bl2 = false;
                    stringBuilder.append(string.substring(n, i));
                    if (c2 != '-') {
                        stringBuilder.append('-');
                    }
                    n = i;
                    continue;
                }
                c = string.charAt(i - 1);
                if (i == n || Character.isUpperCase(c)) {
                    bl = true;
                    continue;
                }
                stringBuilder.append(string.substring(n, i).toLowerCase());
                if (c != '-') {
                    stringBuilder.append('-');
                }
                stringBuilder.append(Character.toLowerCase(c2));
                n = i + 1;
                continue;
            }
            if (Character.isDigit(c2)) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder(string.length() + 3);
                }
                if (bl) {
                    bl = false;
                    stringBuilder.append(string.substring(n, i).toLowerCase());
                    if (c2 != '-') {
                        stringBuilder.append('-');
                    }
                    n = i;
                    continue;
                }
                c = string.charAt(i - 1);
                if (i == n || Character.isDigit(c)) {
                    bl2 = true;
                    continue;
                }
                stringBuilder.append(string.substring(n, i).toLowerCase());
                if (c != '-') {
                    stringBuilder.append('-');
                }
                stringBuilder.append(Character.toLowerCase(c2));
                n = i + 1;
                continue;
            }
            if (bl2) {
                bl2 = false;
                stringBuilder.append(string.substring(n, i));
                if (c2 != '-') {
                    stringBuilder.append('-');
                }
                n = i;
                continue;
            }
            if (!bl) continue;
            bl = false;
            stringBuilder.append(string.substring(n, i - 1).toLowerCase());
            if (c2 != '-') {
                stringBuilder.append('-');
            }
            n = i - 1;
        }
        if (stringBuilder != null) {
            stringBuilder.append(string.substring(n).toLowerCase());
            return stringBuilder.toString();
        }
        return Character.isUpperCase(string.charAt(0)) ? string.toLowerCase() : string;
    }

    public static String lispToCamel(String string) {
        return StringUtil.lispToCamel(string, false);
    }

    public static String lispToCamel(String string, boolean bl) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (string.length() == 0) {
            return string;
        }
        StringBuilder stringBuilder = null;
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c != '-') continue;
            if (stringBuilder == null) {
                stringBuilder = new StringBuilder(string.length() - 1);
            }
            if (n != 0 || bl) {
                stringBuilder.append(Character.toUpperCase(string.charAt(n)));
                ++n;
            }
            stringBuilder.append(string.substring(n, i).toLowerCase());
            n = i + 1;
        }
        if (stringBuilder != null) {
            stringBuilder.append(Character.toUpperCase(string.charAt(n)));
            stringBuilder.append(string.substring(n + 1).toLowerCase());
            return stringBuilder.toString();
        }
        if (bl && !Character.isUpperCase(string.charAt(0))) {
            return StringUtil.capitalize(string, 0);
        }
        if (!bl && Character.isUpperCase(string.charAt(0))) {
            return Character.toLowerCase(string.charAt(0)) + string.substring(1);
        }
        return string;
    }

    public static String reverse(String string) {
        char[] cArray = new char[string.length()];
        string.getChars(0, cArray.length, cArray, 0);
        for (int i = 0; i < cArray.length / 2; ++i) {
            char c = cArray[i];
            cArray[i] = cArray[cArray.length - 1 - i];
            cArray[cArray.length - 1 - i] = c;
        }
        return new String(cArray);
    }
}

