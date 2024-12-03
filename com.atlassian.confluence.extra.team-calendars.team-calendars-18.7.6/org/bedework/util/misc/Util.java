/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.misc;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

public class Util {
    private static final DateTimeFormatter icalUTCTimestampFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final char[] randChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private Util() {
    }

    public static String icalUTCTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(icalUTCTimestampFormatter);
    }

    public static <T> AdjustCollectionResult<T> adjustCollection(Collection<T> newCol, Collection<T> toAdjust) {
        AdjustCollectionResult acr = new AdjustCollectionResult();
        acr.removed = new ArrayList();
        acr.added = new ArrayList();
        acr.added.addAll(newCol);
        if (toAdjust != null) {
            for (T ent : toAdjust) {
                if (newCol.contains(ent)) {
                    acr.added.remove(ent);
                    continue;
                }
                acr.removed.add(ent);
            }
            for (T ent : acr.added) {
                toAdjust.add(ent);
                ++acr.numAdded;
            }
            for (T ent : acr.removed) {
                if (!toAdjust.remove(ent)) continue;
                ++acr.numRemoved;
            }
        }
        return acr;
    }

    public static String buildPath(boolean endWithSep, String ... val) {
        StringBuilder path = new StringBuilder();
        for (String s : val) {
            if (s == null) continue;
            path.append(s);
        }
        String s = path.toString().replaceAll("/+", "/");
        if (endWithSep) {
            if (!s.endsWith("/")) {
                s = s + "/";
            }
        } else if (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String[] splitName(String href) {
        if (href == null || href.length() == 0) {
            return null;
        }
        String stripped = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
        int pos = stripped.lastIndexOf("/");
        if (pos <= 0) {
            return null;
        }
        return new String[]{stripped.substring(0, pos), stripped.substring(pos + 1)};
    }

    public static String pathElement(int index, String path) {
        String[] paths = path.split("/");
        int idx = index;
        if (paths[0] == null || paths[0].length() == 0) {
            ++idx;
        }
        if (idx >= paths.length) {
            return null;
        }
        return paths[idx];
    }

    public static Locale makeLocale(String val) throws Throwable {
        String lang = null;
        String country = "";
        String variant = "";
        if (val == null) {
            throw new Exception("Bad Locale: NULL");
        }
        if (val.length() == 2) {
            lang = val;
        } else {
            int pos = val.indexOf(95);
            if (pos != 2) {
                throw new Exception("Bad Locale: " + val);
            }
            lang = val.substring(0, 2);
            pos = val.indexOf("_", 3);
            if (pos < 0) {
                if (val.length() != 5) {
                    throw new Exception("Bad Locale: " + val);
                }
                country = val.substring(3);
            } else {
                country = val.substring(3, 5);
                if (val.length() > 6) {
                    variant = val.substring(6);
                }
            }
        }
        return new Locale(lang, country, variant);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties getPropertiesFromResource(String name) throws Throwable {
        Properties pr = new Properties();
        InputStream is = null;
        try {
            try {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                is = cl.getResourceAsStream(name);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (is == null) {
                is = Util.class.getResourceAsStream(name);
            }
            if (is == null) {
                throw new Exception("Unable to load properties file" + name);
            }
            pr.load(is);
            Properties properties = pr;
            return properties;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Throwable throwable) {}
            }
        }
    }

    public static Object getObject(String className, Class cl) throws Exception {
        try {
            Object o = Class.forName(className).newInstance();
            if (o == null) {
                throw new Exception("Class " + className + " not found");
            }
            if (!cl.isInstance(o)) {
                throw new Exception("Class " + className + " is not a subclass of " + cl.getName());
            }
            return o;
        }
        catch (Exception e) {
            throw e;
        }
        catch (Throwable t) {
            throw new Exception(t);
        }
    }

    public static String propertyReplace(String val, PropertyFetcher props) {
        StringBuilder sb;
        block6: {
            if (val == null) {
                return null;
            }
            int pos = val.indexOf("${");
            if (pos < 0) {
                return val;
            }
            sb = new StringBuilder(val.length());
            int segStart = 0;
            do {
                int end;
                if (pos > 0) {
                    sb.append(val.substring(segStart, pos));
                }
                if ((end = val.indexOf("}", pos)) < 0) {
                    sb.append(val.substring(pos));
                    break block6;
                }
                String pval = props.get(val.substring(pos + 2, end).trim());
                if (pval != null) {
                    sb.append(pval);
                }
                if ((segStart = end + 1) > val.length()) break block6;
            } while ((pos = val.indexOf("${", segStart)) >= 0);
            sb.append(val.substring(segStart));
        }
        return sb.toString();
    }

    public static String fmtMsg(String fmt, String arg) {
        Object[] o = new Object[]{arg};
        return MessageFormat.format(fmt, o);
    }

    public static String fmtMsg(String fmt, String arg1, String arg2) {
        Object[] o = new Object[]{arg1, arg2};
        return MessageFormat.format(fmt, o);
    }

    public static String fmtMsg(String fmt, int arg) {
        Object[] o = new Object[]{new Integer(arg)};
        return MessageFormat.format(fmt, o);
    }

    public static String makeRandomString(int length, int maxVal) {
        if (length < 0) {
            return null;
        }
        length = Math.min(length, 1025);
        if (maxVal < 0) {
            return null;
        }
        maxVal = Math.min(maxVal, 35);
        StringBuffer res = new StringBuffer();
        Random rand = new Random();
        for (int i = 0; i <= length; ++i) {
            res.append(randChars[rand.nextInt(maxVal + 1)]);
        }
        return res.toString();
    }

    public static String[] appendTextToArray(String[] sarray, String val, int maxEntries) {
        if (sarray == null) {
            if (maxEntries > 0) {
                sarray = new String[]{val};
            }
            return sarray;
        }
        if (sarray.length > maxEntries) {
            String[] neb = new String[maxEntries];
            System.arraycopy(sarray, sarray.length - maxEntries, neb, 0, maxEntries);
            sarray = neb;
            sarray[sarray.length - 1] = val;
            neb = null;
            return sarray;
        }
        if (sarray.length < maxEntries) {
            int newLen = sarray.length + 1;
            String[] neb = new String[newLen];
            System.arraycopy(sarray, 0, neb, 0, sarray.length);
            sarray = neb;
            sarray[sarray.length - 1] = val;
            neb = null;
            return sarray;
        }
        if (maxEntries > 1) {
            System.arraycopy(sarray, 1, sarray, 0, sarray.length - 1);
        }
        sarray[sarray.length - 1] = val;
        return sarray;
    }

    public static String encodeArray(String[] val) {
        if (val == null) {
            return null;
        }
        int len = val.length;
        if (len == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            String s = val[i];
            try {
                if (s == null) {
                    sb.append("\t");
                    continue;
                }
                sb.append(URLEncoder.encode(s, "UTF-8"));
                continue;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return sb.toString();
    }

    public static String[] decodeArray(String val) {
        if (val == null) {
            return null;
        }
        int len = val.length();
        if (len == 0) {
            return new String[0];
        }
        ArrayList<String> al = new ArrayList<String>();
        int i = 0;
        while (i < len) {
            String s;
            int end = val.indexOf(" ", i);
            if (end < 0) {
                s = val.substring(i);
                i = len;
            } else {
                s = val.substring(i, end);
                i = end + 1;
            }
            try {
                if (s.equals("\t")) {
                    al.add(null);
                    continue;
                }
                al.add(URLDecoder.decode(s, "UTF-8"));
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return al.toArray(new String[al.size()]);
    }

    public static boolean equalsString(String thisStr, String thatStr) {
        if (thisStr == null && thatStr == null) {
            return true;
        }
        if (thisStr == null) {
            return false;
        }
        return thisStr.equals(thatStr);
    }

    public static int compareStrings(String s1, String s2) {
        if (s1 == null) {
            if (s2 != null) {
                return -1;
            }
            return 0;
        }
        if (s2 == null) {
            return 1;
        }
        return s1.compareTo(s2);
    }

    public static String checkNull(String val) {
        if (val == null) {
            return null;
        }
        if ((val = val.trim()).length() == 0) {
            return null;
        }
        return val;
    }

    public static boolean present(String val) {
        return Util.checkNull(val) != null;
    }

    public static List<String> getList(String val, boolean emptyOk) throws Throwable {
        LinkedList<String> l = new LinkedList<String>();
        if (val == null || val.length() == 0) {
            return l;
        }
        StringTokenizer st = new StringTokenizer(val, ",", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token == null || token.length() == 0) {
                if (!emptyOk) {
                    throw new Exception("List has an empty element.");
                }
                l.add("");
                continue;
            }
            l.add(token);
        }
        return l;
    }

    public static int cmpObjval(Comparable thisone, Comparable thatone) {
        if (thisone == null) {
            if (thatone == null) {
                return 0;
            }
            return -1;
        }
        if (thatone == null) {
            return 1;
        }
        return thisone.compareTo(thatone);
    }

    public static int cmpObjval(Collection<? extends Comparable> thisone, Collection<? extends Comparable> thatone) {
        int thatLen;
        if (thisone == null) {
            if (thatone == null) {
                return 0;
            }
            return -1;
        }
        if (thatone == null) {
            return 1;
        }
        int thisLen = thisone.size();
        int res = Util.cmpIntval(thisLen, thatLen = thatone.size());
        if (res != 0) {
            return res;
        }
        Iterator<? extends Comparable> thatIt = thatone.iterator();
        for (Comparable comparable : thisone) {
            res = Util.cmpObjval(comparable, thatIt.next());
            if (res == 0) continue;
            return res;
        }
        return 0;
    }

    public static int cmpBoolval(boolean thisone, boolean thatone) {
        if (thisone == thatone) {
            return 0;
        }
        if (!thisone) {
            return -1;
        }
        return 1;
    }

    public static int cmpIntval(int thisone, int thatone) {
        if (thisone == thatone) {
            return 0;
        }
        if (thisone < thatone) {
            return -1;
        }
        return 1;
    }

    public static int compare(char[] thisone, char[] thatone) {
        if (thisone == thatone) {
            return 0;
        }
        if (thisone == null) {
            return -1;
        }
        if (thatone == null) {
            return 1;
        }
        if (thisone.length < thatone.length) {
            return -1;
        }
        if (thisone.length > thatone.length) {
            return -1;
        }
        for (int i = 0; i < thisone.length; ++i) {
            char thisc = thisone[i];
            char thatc = thatone[i];
            if (thisc < thatc) {
                return -1;
            }
            if (thisc <= thatc) continue;
            return 1;
        }
        return 0;
    }

    public static boolean isEmpty(Collection val) {
        if (val == null) {
            return true;
        }
        return val.isEmpty();
    }

    public static URI validURI(String val) {
        try {
            return new URI(val);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static class PropertiesPropertyFetcher
    implements PropertyFetcher {
        private final Properties props;

        public PropertiesPropertyFetcher(Properties props) {
            this.props = props;
        }

        @Override
        public String get(String name) {
            return this.props.getProperty(name);
        }
    }

    public static interface PropertyFetcher {
        public String get(String var1);
    }

    public static class AdjustCollectionResult<T> {
        public Collection<T> removed;
        public Collection<T> added;
        public int numAdded;
        public int numRemoved;
    }
}

