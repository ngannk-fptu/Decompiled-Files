/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.extended.StackTraceElementFactory;
import com.thoughtworks.xstream.core.JVM;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceElementConverter
extends AbstractSingleValueConverter {
    private static final Pattern PATTERN = Pattern.compile("^(.+)\\.([^\\(]+)\\(([^:]*)(:(\\d+))?\\)$");
    private static final StackTraceElementFactory FACTORY;

    public boolean canConvert(Class type) {
        return StackTraceElement.class.equals(type) && FACTORY != null;
    }

    public String toString(Object obj) {
        String s = super.toString(obj);
        return s.replaceFirst(":\\?\\?\\?", "");
    }

    public Object fromString(String str) {
        Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            String declaringClass = matcher.group(1);
            String methodName = matcher.group(2);
            String fileName = matcher.group(3);
            if (fileName.equals("Unknown Source")) {
                return FACTORY.unknownSourceElement(declaringClass, methodName);
            }
            if (fileName.equals("Native Method")) {
                return FACTORY.nativeMethodElement(declaringClass, methodName);
            }
            if (matcher.group(4) != null) {
                int lineNumber = Integer.parseInt(matcher.group(5));
                return FACTORY.element(declaringClass, methodName, fileName, lineNumber);
            }
            return FACTORY.element(declaringClass, methodName, fileName);
        }
        throw new ConversionException("Could not parse StackTraceElement : " + str);
    }

    static {
        StackTraceElementFactory factory = null;
        if (JVM.isVersion(5)) {
            Class factoryType = JVM.loadClassForName("com.thoughtworks.xstream.converters.extended.StackTraceElementFactory15", false);
            try {
                factory = (StackTraceElementFactory)factoryType.newInstance();
            }
            catch (Exception exception) {
            }
            catch (LinkageError linkageError) {
                // empty catch block
            }
        }
        if (factory == null) {
            factory = new StackTraceElementFactory();
        }
        try {
            factory.unknownSourceElement("a", "b");
        }
        catch (Exception e) {
            factory = null;
        }
        catch (NoClassDefFoundError e) {
            factory = null;
        }
        FACTORY = factory;
    }
}

