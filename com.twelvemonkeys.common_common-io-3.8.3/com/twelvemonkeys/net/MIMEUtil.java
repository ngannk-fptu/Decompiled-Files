/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.SystemUtil
 */
package com.twelvemonkeys.net;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.SystemUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class MIMEUtil {
    private static Map<String, List<String>> sExtToMIME = new HashMap<String, List<String>>();
    private static Map<String, List<String>> sUnmodifiableExtToMIME = Collections.unmodifiableMap(sExtToMIME);
    private static Map<String, List<String>> sMIMEToExt = new HashMap<String, List<String>>();
    private static Map<String, List<String>> sUnmodifiableMIMEToExt = Collections.unmodifiableMap(sMIMEToExt);

    private MIMEUtil() {
    }

    public static String getMIMEType(String string) {
        List<String> list = sExtToMIME.get(StringUtil.toLowerCase((String)string));
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public static List<String> getMIMETypes(String string) {
        List<String> list = sExtToMIME.get(StringUtil.toLowerCase((String)string));
        return MIMEUtil.maskNull(list);
    }

    public static Map<String, List<String>> getMIMETypeMappings() {
        return sUnmodifiableExtToMIME;
    }

    public static String getExtension(String string) {
        String string2 = MIMEUtil.bareMIME(StringUtil.toLowerCase((String)string));
        List<String> list = sMIMEToExt.get(string2);
        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public static List<String> getExtensions(String string) {
        String string2 = MIMEUtil.bareMIME(StringUtil.toLowerCase((String)string));
        if (string2.endsWith("/*")) {
            return MIMEUtil.getExtensionForWildcard(string2);
        }
        List<String> list = sMIMEToExt.get(string2);
        return MIMEUtil.maskNull(list);
    }

    private static List<String> getExtensionForWildcard(String string) {
        String string2 = string.substring(0, string.length() - 1);
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (Map.Entry<String, List<String>> entry : sMIMEToExt.entrySet()) {
            if (!"*/".equals(string2) && !entry.getKey().startsWith(string2)) continue;
            linkedHashSet.addAll(entry.getValue());
        }
        return Collections.unmodifiableList(new ArrayList(linkedHashSet));
    }

    public static Map<String, List<String>> getExtensionMappings() {
        return sUnmodifiableMIMEToExt;
    }

    static boolean includes(String string, String string2) {
        String string3 = MIMEUtil.bareMIME(string2);
        return string3.equals(string) || "*/*".equals(string) || string.endsWith("/*") && string.startsWith(string3.substring(0, string3.indexOf(47)));
    }

    public static String bareMIME(String string) {
        int n;
        if (string != null && (n = string.indexOf(59)) >= 0) {
            return string.substring(0, n);
        }
        return string;
    }

    private static List<String> maskNull(List<String> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /*
     * WARNING - void declaration
     */
    public static void main(String[] stringArray) {
        Object object;
        Iterator<String> iterator;
        Object[] objectArray;
        Object object2;
        if (stringArray.length > 1) {
            object2 = stringArray[0];
            objectArray = stringArray[1];
            boolean n = MIMEUtil.includes((String)objectArray, (String)object2);
            System.out.println("Mime type family " + (String)objectArray + (n ? " includes " : " does not include ") + "type " + (String)object2);
        }
        if (stringArray.length > 0) {
            object2 = stringArray[0];
            if (((String)object2).indexOf(47) >= 0) {
                objectArray = MIMEUtil.getExtension((String)object2);
                System.out.println("Default extension for MIME type '" + (String)object2 + "' is " + (objectArray != null ? ": '" + (String)objectArray + "'" : "unknown") + ".");
                System.out.println("All possible: " + MIMEUtil.getExtensions((String)object2));
            } else {
                objectArray = MIMEUtil.getMIMEType((String)object2);
                System.out.println("Default MIME type for extension '" + (String)object2 + "' is " + (objectArray != null ? ": '" + (String)objectArray + "'" : "unknown") + ".");
                System.out.println("All possible: " + MIMEUtil.getMIMETypes((String)object2));
            }
            return;
        }
        object2 = sMIMEToExt.keySet();
        objectArray = new String[object2.size()];
        boolean bl = false;
        Iterator iterator2 = object2.iterator();
        while (iterator2.hasNext()) {
            void var3_5;
            iterator = (String)iterator2.next();
            objectArray[var3_5] = iterator;
            ++var3_5;
        }
        Arrays.sort(objectArray);
        System.out.println("Known MIME types (" + objectArray.length + "):");
        for (int i = 0; i < objectArray.length; ++i) {
            iterator = objectArray[i];
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print((String)((Object)iterator));
        }
        System.out.println("\n");
        object2 = sExtToMIME.keySet();
        Object[] objectArray2 = new String[object2.size()];
        boolean bl2 = false;
        iterator = object2.iterator();
        while (iterator.hasNext()) {
            void var3_7;
            objectArray2[var3_7] = object = iterator.next();
            ++var3_7;
        }
        Arrays.sort(objectArray2);
        System.out.println("Known file types (" + objectArray2.length + "):");
        for (int i = 0; i < objectArray2.length; ++i) {
            object = objectArray2[i];
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print((String)object);
        }
        System.out.println();
    }

    static {
        try {
            Properties properties = SystemUtil.loadProperties(MIMEUtil.class);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String string = StringUtil.toLowerCase((String)((String)entry.getKey()));
                List<String> list = Collections.unmodifiableList(Arrays.asList(StringUtil.toStringArray((String)string, (String)";, ")));
                String string2 = StringUtil.toLowerCase((String)((String)entry.getValue()));
                List<String> list2 = Collections.unmodifiableList(Arrays.asList(StringUtil.toStringArray((String)string2, (String)";, ")));
                for (String string3 : list) {
                    sExtToMIME.put(string3, list2);
                }
                for (String string3 : list2) {
                    sMIMEToExt.put(string3, list);
                }
            }
        }
        catch (IOException iOException) {
            System.err.println("Could not read properties for MIMEUtil: " + iOException.getMessage());
            iOException.printStackTrace();
        }
    }
}

