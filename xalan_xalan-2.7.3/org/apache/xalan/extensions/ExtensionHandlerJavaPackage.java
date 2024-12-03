/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.ExtensionHandlerJava;
import org.apache.xalan.extensions.MethodResolver;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.trace.ExtensionEvent;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.XObject;

public class ExtensionHandlerJavaPackage
extends ExtensionHandlerJava {
    public ExtensionHandlerJavaPackage(String namespaceUri, String scriptLang, String className) {
        super(namespaceUri, scriptLang, className);
    }

    @Override
    public boolean isFunctionAvailable(String function) {
        try {
            String fullName = this.m_className + function;
            int lastDot = fullName.lastIndexOf(46);
            if (lastDot >= 0) {
                Class myClass = ExtensionHandlerJavaPackage.getClassForName(fullName.substring(0, lastDot));
                Method[] methods = myClass.getMethods();
                int nMethods = methods.length;
                function = fullName.substring(lastDot + 1);
                for (int i = 0; i < nMethods; ++i) {
                    if (!methods[i].getName().equals(function)) continue;
                    return true;
                }
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return false;
    }

    @Override
    public boolean isElementAvailable(String element) {
        try {
            String fullName = this.m_className + element;
            int lastDot = fullName.lastIndexOf(46);
            if (lastDot >= 0) {
                Class myClass = ExtensionHandlerJavaPackage.getClassForName(fullName.substring(0, lastDot));
                Method[] methods = myClass.getMethods();
                int nMethods = methods.length;
                element = fullName.substring(lastDot + 1);
                for (int i = 0; i < nMethods; ++i) {
                    Class<?>[] paramTypes;
                    if (!methods[i].getName().equals(element) || (paramTypes = methods[i].getParameterTypes()).length != 2 || !paramTypes[0].isAssignableFrom(XSLProcessorContext.class) || !paramTypes[1].isAssignableFrom(ElemExtensionCall.class)) continue;
                    return true;
                }
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return false;
    }

    @Override
    public Object callFunction(String funcName, Vector args, Object methodKey, ExpressionContext exprContext) throws TransformerException {
        int lastDot = funcName.lastIndexOf(46);
        try {
            Method m;
            TransformerImpl trans;
            TransformerImpl transformerImpl = trans = exprContext != null ? (TransformerImpl)exprContext.getXPathContext().getOwnerObject() : null;
            if (funcName.endsWith(".new")) {
                Class classObj;
                Constructor c;
                Object[] methodArgs = new Object[args.size()];
                Object[][] convertedArgs = new Object[1][];
                for (int i = 0; i < methodArgs.length; ++i) {
                    methodArgs[i] = args.get(i);
                }
                Constructor constructor = c = methodKey != null ? (Constructor)this.getFromCache(methodKey, null, methodArgs) : null;
                if (c != null) {
                    try {
                        Class[] paramTypes = c.getParameterTypes();
                        MethodResolver.convertParams(methodArgs, convertedArgs, paramTypes, exprContext);
                        return c.newInstance(convertedArgs[0]);
                    }
                    catch (InvocationTargetException ite) {
                        throw ite;
                    }
                    catch (Exception ite) {
                        // empty catch block
                    }
                }
                String className = this.m_className + funcName.substring(0, lastDot);
                try {
                    classObj = ExtensionHandlerJavaPackage.getClassForName(className);
                }
                catch (ClassNotFoundException e) {
                    throw new TransformerException(e);
                }
                c = MethodResolver.getConstructor(classObj, methodArgs, convertedArgs, exprContext);
                if (methodKey != null) {
                    this.putToCache(methodKey, null, methodArgs, c);
                }
                if (trans != null && trans.getDebug()) {
                    Object result;
                    trans.getTraceManager().fireExtensionEvent(new ExtensionEvent(trans, c, convertedArgs[0]));
                    try {
                        result = c.newInstance(convertedArgs[0]);
                    }
                    catch (Exception e) {
                        throw e;
                    }
                    finally {
                        trans.getTraceManager().fireExtensionEndEvent(new ExtensionEvent(trans, c, convertedArgs[0]));
                    }
                    return result;
                }
                return c.newInstance(convertedArgs[0]);
            }
            if (-1 != lastDot) {
                Class classObj;
                Method m2;
                Object[] methodArgs = new Object[args.size()];
                Object[][] convertedArgs = new Object[1][];
                for (int i = 0; i < methodArgs.length; ++i) {
                    methodArgs[i] = args.get(i);
                }
                Method method = m2 = methodKey != null ? (Method)this.getFromCache(methodKey, null, methodArgs) : null;
                if (m2 != null && !trans.getDebug()) {
                    try {
                        Class[] paramTypes = m2.getParameterTypes();
                        MethodResolver.convertParams(methodArgs, convertedArgs, paramTypes, exprContext);
                        return m2.invoke(null, convertedArgs[0]);
                    }
                    catch (InvocationTargetException ite) {
                        throw ite;
                    }
                    catch (Exception ite) {
                        // empty catch block
                    }
                }
                String className = this.m_className + funcName.substring(0, lastDot);
                String methodName = funcName.substring(lastDot + 1);
                try {
                    classObj = ExtensionHandlerJavaPackage.getClassForName(className);
                }
                catch (ClassNotFoundException e) {
                    throw new TransformerException(e);
                }
                m2 = MethodResolver.getMethod(classObj, methodName, methodArgs, convertedArgs, exprContext, 1);
                if (methodKey != null) {
                    this.putToCache(methodKey, null, methodArgs, m2);
                }
                if (trans != null && trans.getDebug()) {
                    Object result;
                    trans.getTraceManager().fireExtensionEvent(m2, null, convertedArgs[0]);
                    try {
                        result = m2.invoke(null, convertedArgs[0]);
                    }
                    catch (Exception e) {
                        throw e;
                    }
                    finally {
                        trans.getTraceManager().fireExtensionEndEvent(m2, null, convertedArgs[0]);
                    }
                    return result;
                }
                return m2.invoke(null, convertedArgs[0]);
            }
            if (args.size() < 1) {
                throw new TransformerException(XSLMessages.createMessage("ER_INSTANCE_MTHD_CALL_REQUIRES", new Object[]{funcName}));
            }
            Object targetObject = args.get(0);
            if (targetObject instanceof XObject) {
                targetObject = ((XObject)targetObject).object();
            }
            Object[] methodArgs = new Object[args.size() - 1];
            Object[][] convertedArgs = new Object[1][];
            for (int i = 0; i < methodArgs.length; ++i) {
                methodArgs[i] = args.get(i + 1);
            }
            Method method = m = methodKey != null ? (Method)this.getFromCache(methodKey, targetObject, methodArgs) : null;
            if (m != null) {
                try {
                    Class[] paramTypes = m.getParameterTypes();
                    MethodResolver.convertParams(methodArgs, convertedArgs, paramTypes, exprContext);
                    return m.invoke(targetObject, convertedArgs[0]);
                }
                catch (InvocationTargetException ite) {
                    throw ite;
                }
                catch (Exception ite) {
                    // empty catch block
                }
            }
            Class<?> classObj = targetObject.getClass();
            m = MethodResolver.getMethod(classObj, funcName, methodArgs, convertedArgs, exprContext, 2);
            if (methodKey != null) {
                this.putToCache(methodKey, targetObject, methodArgs, m);
            }
            if (trans != null && trans.getDebug()) {
                Object result;
                trans.getTraceManager().fireExtensionEvent(m, targetObject, convertedArgs[0]);
                try {
                    result = m.invoke(targetObject, convertedArgs[0]);
                }
                catch (Exception e) {
                    throw e;
                }
                finally {
                    trans.getTraceManager().fireExtensionEndEvent(m, targetObject, convertedArgs[0]);
                }
                return result;
            }
            return m.invoke(targetObject, convertedArgs[0]);
        }
        catch (InvocationTargetException ite) {
            Throwable resultException = ite;
            Throwable targetException = ite.getTargetException();
            if (targetException instanceof TransformerException) {
                throw (TransformerException)targetException;
            }
            if (targetException != null) {
                resultException = targetException;
            }
            throw new TransformerException(resultException);
        }
        catch (Exception e) {
            throw new TransformerException(e);
        }
    }

    @Override
    public Object callFunction(FuncExtFunction extFunction, Vector args, ExpressionContext exprContext) throws TransformerException {
        return this.callFunction(extFunction.getFunctionName(), args, extFunction.getMethodKey(), exprContext);
    }

    @Override
    public void processElement(String localPart, ElemTemplateElement element, TransformerImpl transformer, Stylesheet stylesheetTree, Object methodKey) throws TransformerException, IOException {
        XSLProcessorContext xpc;
        Object result;
        block18: {
            result = null;
            Method m = (Method)this.getFromCache(methodKey, null, null);
            if (null == m) {
                try {
                    Class classObj;
                    String fullName = this.m_className + localPart;
                    int lastDot = fullName.lastIndexOf(46);
                    if (lastDot < 0) {
                        throw new TransformerException(XSLMessages.createMessage("ER_INVALID_ELEMENT_NAME", new Object[]{fullName}));
                    }
                    try {
                        classObj = ExtensionHandlerJavaPackage.getClassForName(fullName.substring(0, lastDot));
                    }
                    catch (ClassNotFoundException e) {
                        throw new TransformerException(e);
                    }
                    localPart = fullName.substring(lastDot + 1);
                    m = MethodResolver.getElementMethod(classObj, localPart);
                    if (!Modifier.isStatic(m.getModifiers())) {
                        throw new TransformerException(XSLMessages.createMessage("ER_ELEMENT_NAME_METHOD_STATIC", new Object[]{fullName}));
                    }
                }
                catch (Exception e) {
                    throw new TransformerException(e);
                }
                this.putToCache(methodKey, null, null, m);
            }
            xpc = new XSLProcessorContext(transformer, stylesheetTree);
            try {
                if (transformer.getDebug()) {
                    transformer.getTraceManager().fireExtensionEvent(m, null, new Object[]{xpc, element});
                    try {
                        result = m.invoke(null, xpc, element);
                    }
                    catch (Exception e) {
                        try {
                            throw e;
                        }
                        catch (Throwable throwable) {
                            transformer.getTraceManager().fireExtensionEndEvent(m, null, new Object[]{xpc, element});
                            throw throwable;
                        }
                    }
                    transformer.getTraceManager().fireExtensionEndEvent(m, null, new Object[]{xpc, element});
                    break block18;
                }
                result = m.invoke(null, xpc, element);
            }
            catch (InvocationTargetException ite) {
                Throwable resultException = ite;
                Throwable targetException = ite.getTargetException();
                if (targetException instanceof TransformerException) {
                    throw (TransformerException)targetException;
                }
                if (targetException != null) {
                    resultException = targetException;
                }
                throw new TransformerException(resultException);
            }
            catch (Exception e) {
                throw new TransformerException(e);
            }
        }
        if (result != null) {
            xpc.outputToResultTree(stylesheetTree, result);
        }
    }
}

