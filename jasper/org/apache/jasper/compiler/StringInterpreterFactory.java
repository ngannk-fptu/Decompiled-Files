/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.jasper.compiler;

import javax.servlet.ServletContext;
import org.apache.jasper.compiler.Generator;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.StringInterpreter;

public class StringInterpreterFactory {
    public static final String STRING_INTERPRETER_CLASS_NAME = StringInterpreter.class.getName();
    private static final StringInterpreter DEFAULT_INSTANCE = new DefaultStringInterpreter();

    public static StringInterpreter getStringInterpreter(ServletContext context) throws Exception {
        String className;
        StringInterpreter result = null;
        Object attribute = context.getAttribute(STRING_INTERPRETER_CLASS_NAME);
        if (attribute instanceof StringInterpreter) {
            return (StringInterpreter)attribute;
        }
        if (attribute instanceof String) {
            result = StringInterpreterFactory.createInstance(context, (String)attribute);
        }
        if (result == null && (className = context.getInitParameter(STRING_INTERPRETER_CLASS_NAME)) != null) {
            result = StringInterpreterFactory.createInstance(context, className);
        }
        if (result == null) {
            result = DEFAULT_INSTANCE;
        }
        context.setAttribute(STRING_INTERPRETER_CLASS_NAME, (Object)result);
        return result;
    }

    private static StringInterpreter createInstance(ServletContext context, String className) throws Exception {
        return (StringInterpreter)context.getClassLoader().loadClass(className).getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    private StringInterpreterFactory() {
    }

    public static class DefaultStringInterpreter
    implements StringInterpreter {
        @Override
        public String convertString(Class<?> c, String s, String attrName, Class<?> propEditorClass, boolean isNamedAttribute) {
            String quoted = s;
            if (!isNamedAttribute) {
                quoted = Generator.quote(s);
            }
            if (propEditorClass != null) {
                String className = c.getCanonicalName();
                return "(" + className + ")org.apache.jasper.runtime.JspRuntimeLibrary.getValueFromBeanInfoPropertyEditor(" + className + ".class, \"" + attrName + "\", " + quoted + ", " + propEditorClass.getCanonicalName() + ".class)";
            }
            if (c == String.class) {
                return quoted;
            }
            if (c == Boolean.TYPE) {
                return JspUtil.coerceToPrimitiveBoolean(s, isNamedAttribute);
            }
            if (c == Boolean.class) {
                return JspUtil.coerceToBoolean(s, isNamedAttribute);
            }
            if (c == Byte.TYPE) {
                return JspUtil.coerceToPrimitiveByte(s, isNamedAttribute);
            }
            if (c == Byte.class) {
                return JspUtil.coerceToByte(s, isNamedAttribute);
            }
            if (c == Character.TYPE) {
                return JspUtil.coerceToChar(s, isNamedAttribute);
            }
            if (c == Character.class) {
                return JspUtil.coerceToCharacter(s, isNamedAttribute);
            }
            if (c == Double.TYPE) {
                return JspUtil.coerceToPrimitiveDouble(s, isNamedAttribute);
            }
            if (c == Double.class) {
                return JspUtil.coerceToDouble(s, isNamedAttribute);
            }
            if (c == Float.TYPE) {
                return JspUtil.coerceToPrimitiveFloat(s, isNamedAttribute);
            }
            if (c == Float.class) {
                return JspUtil.coerceToFloat(s, isNamedAttribute);
            }
            if (c == Integer.TYPE) {
                return JspUtil.coerceToInt(s, isNamedAttribute);
            }
            if (c == Integer.class) {
                return JspUtil.coerceToInteger(s, isNamedAttribute);
            }
            if (c == Short.TYPE) {
                return JspUtil.coerceToPrimitiveShort(s, isNamedAttribute);
            }
            if (c == Short.class) {
                return JspUtil.coerceToShort(s, isNamedAttribute);
            }
            if (c == Long.TYPE) {
                return JspUtil.coerceToPrimitiveLong(s, isNamedAttribute);
            }
            if (c == Long.class) {
                return JspUtil.coerceToLong(s, isNamedAttribute);
            }
            if (c == Object.class) {
                return quoted;
            }
            String result = this.coerceToOtherType(c, s, isNamedAttribute);
            if (result != null) {
                return result;
            }
            String className = c.getCanonicalName();
            return "(" + className + ")org.apache.jasper.runtime.JspRuntimeLibrary.getValueFromPropertyEditorManager(" + className + ".class, \"" + attrName + "\", " + quoted + ")";
        }

        protected String coerceToOtherType(Class<?> c, String s, boolean isNamedAttribute) {
            return null;
        }
    }
}

