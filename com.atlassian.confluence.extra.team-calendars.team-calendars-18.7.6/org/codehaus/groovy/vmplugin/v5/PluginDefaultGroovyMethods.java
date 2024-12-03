/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v5;

import groovy.lang.EmptyRange;
import groovy.lang.IntRange;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.RangeInfo;

public class PluginDefaultGroovyMethods
extends DefaultGroovyMethodsSupport {
    private static final Object[] NO_ARGS = new Object[0];

    public static Object next(Enum self) {
        Object[] values;
        Method[] methods = self.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (!method.getName().equals("next") || method.getParameterTypes().length != 0) continue;
            return InvokerHelper.invokeMethod(self, "next", NO_ARGS);
        }
        int index = Arrays.asList(values = (Object[])InvokerHelper.invokeStaticMethod(self.getClass(), "values", (Object)NO_ARGS)).indexOf(self);
        return values[index < values.length - 1 ? index + 1 : 0];
    }

    public static Object previous(Enum self) {
        Object[] values;
        Method[] methods = self.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method method = methods[i];
            if (!method.getName().equals("previous") || method.getParameterTypes().length != 0) continue;
            return InvokerHelper.invokeMethod(self, "previous", NO_ARGS);
        }
        int index = Arrays.asList(values = (Object[])InvokerHelper.invokeStaticMethod(self.getClass(), "values", (Object)NO_ARGS)).indexOf(self);
        return values[index > 0 ? index - 1 : values.length - 1];
    }

    public static int size(StringBuilder builder) {
        return builder.length();
    }

    public static StringBuilder leftShift(StringBuilder self, Object value) {
        if (value instanceof CharSequence) {
            return self.append((CharSequence)value);
        }
        return self.append(value);
    }

    public static void putAt(StringBuilder self, IntRange range, Object value) {
        RangeInfo info = PluginDefaultGroovyMethods.subListBorders(self.length(), range);
        self.replace(info.from, info.to, value.toString());
    }

    public static void putAt(StringBuilder self, EmptyRange range, Object value) {
        RangeInfo info = PluginDefaultGroovyMethods.subListBorders(self.length(), range);
        self.replace(info.from, info.to, value.toString());
    }

    public static String plus(StringBuilder self, String value) {
        return self + value;
    }
}

