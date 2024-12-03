/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ObjectFactory;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xml.utils.StringVector;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xpath.XPathProcessorException;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.XObject;

public class ExtensionHandlerGeneral
extends ExtensionHandler {
    private String m_scriptSrc;
    private String m_scriptSrcURL;
    private Hashtable m_functions = new Hashtable();
    private Hashtable m_elements = new Hashtable();
    private Object m_engine;
    private Method m_engineCall = null;
    private static String BSF_MANAGER;
    private static final String DEFAULT_BSF_MANAGER = "org.apache.bsf.BSFManager";
    private static final String propName = "org.apache.xalan.extensions.bsf.BSFManager";
    private static final Integer ZEROINT;

    public ExtensionHandlerGeneral(String namespaceUri, StringVector elemNames, StringVector funcNames, String scriptLang, String scriptSrcURL, String scriptSrc, String systemId) throws TransformerException {
        super(namespaceUri, scriptLang);
        String tok;
        int i;
        int n;
        Object junk;
        if (elemNames != null) {
            junk = new Object();
            n = elemNames.size();
            for (i = 0; i < n; ++i) {
                tok = elemNames.elementAt(i);
                this.m_elements.put(tok, junk);
            }
        }
        if (funcNames != null) {
            junk = new Object();
            n = funcNames.size();
            for (i = 0; i < n; ++i) {
                tok = funcNames.elementAt(i);
                this.m_functions.put(tok, junk);
            }
        }
        this.m_scriptSrcURL = scriptSrcURL;
        this.m_scriptSrc = scriptSrc;
        if (this.m_scriptSrcURL != null) {
            URL url = null;
            try {
                url = new URL(this.m_scriptSrcURL);
            }
            catch (MalformedURLException mue) {
                int indexOfColon = this.m_scriptSrcURL.indexOf(58);
                int indexOfSlash = this.m_scriptSrcURL.indexOf(47);
                if (indexOfColon != -1 && indexOfSlash != -1 && indexOfColon < indexOfSlash) {
                    url = null;
                    throw new TransformerException(XSLMessages.createMessage("ER_COULD_NOT_FIND_EXTERN_SCRIPT", new Object[]{this.m_scriptSrcURL}), mue);
                }
                try {
                    url = new URL(new URL(SystemIDResolver.getAbsoluteURI(systemId)), this.m_scriptSrcURL);
                }
                catch (MalformedURLException mue2) {
                    throw new TransformerException(XSLMessages.createMessage("ER_COULD_NOT_FIND_EXTERN_SCRIPT", new Object[]{this.m_scriptSrcURL}), mue2);
                }
            }
            if (url != null) {
                try {
                    URLConnection uc = url.openConnection();
                    InputStream is = uc.getInputStream();
                    byte[] bArray = new byte[uc.getContentLength()];
                    is.read(bArray);
                    this.m_scriptSrc = new String(bArray);
                }
                catch (IOException ioe) {
                    throw new TransformerException(XSLMessages.createMessage("ER_COULD_NOT_FIND_EXTERN_SCRIPT", new Object[]{this.m_scriptSrcURL}), ioe);
                }
            }
        }
        Object manager = null;
        try {
            manager = ObjectFactory.newInstance(BSF_MANAGER, ObjectFactory.findClassLoader(), true);
        }
        catch (ObjectFactory.ConfigurationError e) {
            e.printStackTrace();
        }
        if (manager == null) {
            throw new TransformerException(XSLMessages.createMessage("ER_CANNOT_INIT_BSFMGR", null));
        }
        try {
            Method loadScriptingEngine = manager.getClass().getMethod("loadScriptingEngine", String.class);
            this.m_engine = loadScriptingEngine.invoke(manager, scriptLang);
            Method engineExec = this.m_engine.getClass().getMethod("exec", String.class, Integer.TYPE, Integer.TYPE, Object.class);
            engineExec.invoke(this.m_engine, "XalanScript", ZEROINT, ZEROINT, this.m_scriptSrc);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new TransformerException(XSLMessages.createMessage("ER_CANNOT_CMPL_EXTENSN", null), e);
        }
    }

    @Override
    public boolean isFunctionAvailable(String function) {
        return this.m_functions.get(function) != null;
    }

    @Override
    public boolean isElementAvailable(String element) {
        return this.m_elements.get(element) != null;
    }

    @Override
    public Object callFunction(String funcName, Vector args, Object methodKey, ExpressionContext exprContext) throws TransformerException {
        try {
            Object[] argArray = new Object[args.size()];
            for (int i = 0; i < argArray.length; ++i) {
                Object o = args.get(i);
                argArray[i] = o instanceof XObject ? ((XObject)o).object() : o;
                o = argArray[i];
                if (null == o || !(o instanceof DTMIterator)) continue;
                argArray[i] = new DTMNodeList((DTMIterator)o);
            }
            if (this.m_engineCall == null) {
                this.m_engineCall = this.m_engine.getClass().getMethod("call", Object.class, String.class, Object[].class);
            }
            return this.m_engineCall.invoke(this.m_engine, null, funcName, argArray);
        }
        catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (null != msg) {
                if (msg.startsWith("Stopping after fatal error:")) {
                    msg = msg.substring("Stopping after fatal error:".length());
                }
                throw new TransformerException(e);
            }
            throw new TransformerException(XSLMessages.createMessage("ER_CANNOT_CREATE_EXTENSN", new Object[]{funcName, e}));
        }
    }

    @Override
    public Object callFunction(FuncExtFunction extFunction, Vector args, ExpressionContext exprContext) throws TransformerException {
        return this.callFunction(extFunction.getFunctionName(), args, extFunction.getMethodKey(), exprContext);
    }

    @Override
    public void processElement(String localPart, ElemTemplateElement element, TransformerImpl transformer, Stylesheet stylesheetTree, Object methodKey) throws TransformerException, IOException {
        Object result = null;
        XSLProcessorContext xpc = new XSLProcessorContext(transformer, stylesheetTree);
        try {
            Vector<Object> argv = new Vector<Object>(2);
            argv.add(xpc);
            argv.add(element);
            result = this.callFunction(localPart, argv, methodKey, transformer.getXPathContext().getExpressionContext());
        }
        catch (XPathProcessorException e) {
            throw new TransformerException(e.getMessage(), e);
        }
        if (result != null) {
            xpc.outputToResultTree(stylesheetTree, result);
        }
    }

    static {
        ZEROINT = new Integer(0);
        BSF_MANAGER = ObjectFactory.lookUpFactoryClassName(propName, null, null);
        if (BSF_MANAGER == null) {
            BSF_MANAGER = DEFAULT_BSF_MANAGER;
        }
    }
}

