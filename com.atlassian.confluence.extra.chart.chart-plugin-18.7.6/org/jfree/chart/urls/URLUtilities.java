/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.urls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

public class URLUtilities {
    private static final Class[] STRING_ARGS_2 = new Class[]{class$java$lang$String == null ? (class$java$lang$String = URLUtilities.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = URLUtilities.class$("java.lang.String")) : class$java$lang$String};
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$net$URLEncoder;

    public static String encode(String s, String encoding) {
        Class c = class$java$net$URLEncoder == null ? (class$java$net$URLEncoder = URLUtilities.class$("java.net.URLEncoder")) : class$java$net$URLEncoder;
        String result = null;
        try {
            Method m = c.getDeclaredMethod("encode", STRING_ARGS_2);
            try {
                result = (String)m.invoke(null, s, encoding);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e) {
            result = URLEncoder.encode(s);
        }
        return result;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

