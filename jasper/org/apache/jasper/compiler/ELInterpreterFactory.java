/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.apache.jasper.compiler;

import javax.servlet.ServletContext;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ELInterpreter;
import org.apache.jasper.compiler.JspUtil;

public class ELInterpreterFactory {
    public static final String EL_INTERPRETER_CLASS_NAME = ELInterpreter.class.getName();
    private static final ELInterpreter DEFAULT_INSTANCE = new DefaultELInterpreter();

    public static ELInterpreter getELInterpreter(ServletContext context) throws Exception {
        String className;
        ELInterpreter result = null;
        Object attribute = context.getAttribute(EL_INTERPRETER_CLASS_NAME);
        if (attribute instanceof ELInterpreter) {
            return (ELInterpreter)attribute;
        }
        if (attribute instanceof String) {
            result = ELInterpreterFactory.createInstance(context, (String)attribute);
        }
        if (result == null && (className = context.getInitParameter(EL_INTERPRETER_CLASS_NAME)) != null) {
            result = ELInterpreterFactory.createInstance(context, className);
        }
        if (result == null) {
            result = DEFAULT_INSTANCE;
        }
        context.setAttribute(EL_INTERPRETER_CLASS_NAME, (Object)result);
        return result;
    }

    private static ELInterpreter createInstance(ServletContext context, String className) throws Exception {
        return (ELInterpreter)context.getClassLoader().loadClass(className).getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    private ELInterpreterFactory() {
    }

    public static class DefaultELInterpreter
    implements ELInterpreter {
        @Override
        public String interpreterCall(JspCompilationContext context, boolean isTagFile, String expression, Class<?> expectedType, String fnmapvar) {
            return JspUtil.interpreterCall(isTagFile, expression, expectedType, fnmapvar);
        }
    }
}

