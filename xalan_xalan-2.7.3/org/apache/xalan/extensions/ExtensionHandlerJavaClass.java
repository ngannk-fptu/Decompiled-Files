/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.ExtensionHandlerJava;
import org.apache.xalan.extensions.MethodResolver;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.trace.ExtensionEvent;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.functions.FuncExtFunction;

public class ExtensionHandlerJavaClass
extends ExtensionHandlerJava {
    private Class m_classObj = null;
    private Object m_defaultInstance = null;

    public ExtensionHandlerJavaClass(String namespaceUri, String scriptLang, String className) {
        super(namespaceUri, scriptLang, className);
        try {
            this.m_classObj = ExtensionHandlerJavaClass.getClassForName(className);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    @Override
    public boolean isFunctionAvailable(String function) {
        Method[] methods = this.m_classObj.getMethods();
        int nMethods = methods.length;
        for (int i = 0; i < nMethods; ++i) {
            if (!methods[i].getName().equals(function)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isElementAvailable(String element) {
        Method[] methods = this.m_classObj.getMethods();
        int nMethods = methods.length;
        for (int i = 0; i < nMethods; ++i) {
            Class<?>[] paramTypes;
            if (!methods[i].getName().equals(element) || (paramTypes = methods[i].getParameterTypes()).length != 2 || !paramTypes[0].isAssignableFrom(XSLProcessorContext.class) || !paramTypes[1].isAssignableFrom(ElemExtensionCall.class)) continue;
            return true;
        }
        return false;
    }

    /*
     * Exception decompiling
     */
    @Override
    public Object callFunction(String funcName, Vector args, Object methodKey, ExpressionContext exprContext) throws TransformerException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [18[CATCHBLOCK]], but top level block is 5[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public Object callFunction(FuncExtFunction extFunction, Vector args, ExpressionContext exprContext) throws TransformerException {
        return this.callFunction(extFunction.getFunctionName(), args, extFunction.getMethodKey(), exprContext);
    }

    @Override
    public void processElement(String localPart, ElemTemplateElement element, TransformerImpl transformer, Stylesheet stylesheetTree, Object methodKey) throws TransformerException, IOException {
        XSLProcessorContext xpc;
        Object result;
        block21: {
            result = null;
            Method m = (Method)this.getFromCache(methodKey, null, null);
            if (null == m) {
                block20: {
                    try {
                        m = MethodResolver.getElementMethod(this.m_classObj, localPart);
                        if (null != this.m_defaultInstance || Modifier.isStatic(m.getModifiers())) break block20;
                        if (transformer.getDebug()) {
                            transformer.getTraceManager().fireExtensionEvent(new ExtensionEvent(transformer, this.m_classObj));
                            try {
                                this.m_defaultInstance = this.m_classObj.newInstance();
                                break block20;
                            }
                            catch (Exception e) {
                                throw e;
                            }
                            finally {
                                transformer.getTraceManager().fireExtensionEndEvent(new ExtensionEvent(transformer, this.m_classObj));
                            }
                        }
                        this.m_defaultInstance = this.m_classObj.newInstance();
                    }
                    catch (Exception e) {
                        throw new TransformerException(e.getMessage(), e);
                    }
                }
                this.putToCache(methodKey, null, null, m);
            }
            xpc = new XSLProcessorContext(transformer, stylesheetTree);
            try {
                if (transformer.getDebug()) {
                    transformer.getTraceManager().fireExtensionEvent(m, this.m_defaultInstance, new Object[]{xpc, element});
                    try {
                        result = m.invoke(this.m_defaultInstance, xpc, element);
                    }
                    catch (Exception e) {
                        try {
                            throw e;
                        }
                        catch (Throwable throwable) {
                            transformer.getTraceManager().fireExtensionEndEvent(m, this.m_defaultInstance, new Object[]{xpc, element});
                            throw throwable;
                        }
                    }
                    transformer.getTraceManager().fireExtensionEndEvent(m, this.m_defaultInstance, new Object[]{xpc, element});
                    break block21;
                }
                result = m.invoke(this.m_defaultInstance, xpc, element);
            }
            catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof TransformerException) {
                    throw (TransformerException)targetException;
                }
                if (targetException != null) {
                    throw new TransformerException(targetException.getMessage(), targetException);
                }
                throw new TransformerException(e.getMessage(), e);
            }
            catch (Exception e) {
                throw new TransformerException(e.getMessage(), e);
            }
        }
        if (result != null) {
            xpc.outputToResultTree(stylesheetTree, result);
        }
    }
}

