/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExtensionNamespaceSupport;
import org.apache.xalan.extensions.ExtensionNamespacesManager;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemExtensionScript;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.StringVector;

public class ElemExtensionDecl
extends ElemTemplateElement {
    static final long serialVersionUID = -4692738885172766789L;
    private String m_prefix = null;
    private StringVector m_functions = new StringVector();
    private StringVector m_elements = null;

    @Override
    public void setPrefix(String v) {
        this.m_prefix = v;
    }

    @Override
    public String getPrefix() {
        return this.m_prefix;
    }

    public void setFunctions(StringVector v) {
        this.m_functions = v;
    }

    public StringVector getFunctions() {
        return this.m_functions;
    }

    public String getFunction(int i) throws ArrayIndexOutOfBoundsException {
        if (null == this.m_functions) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.m_functions.elementAt(i);
    }

    public int getFunctionCount() {
        return null != this.m_functions ? this.m_functions.size() : 0;
    }

    public void setElements(StringVector v) {
        this.m_elements = v;
    }

    public StringVector getElements() {
        return this.m_elements;
    }

    public String getElement(int i) throws ArrayIndexOutOfBoundsException {
        if (null == this.m_elements) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.m_elements.elementAt(i);
    }

    public int getElementCount() {
        return null != this.m_elements ? this.m_elements.size() : 0;
    }

    @Override
    public int getXSLToken() {
        return 85;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        String prefix = this.getPrefix();
        String declNamespace = this.getNamespaceForPrefix(prefix);
        String lang = null;
        String srcURL = null;
        String scriptSrc = null;
        if (null == declNamespace) {
            throw new TransformerException(XSLMessages.createMessage("ER_NO_NAMESPACE_DECL", new Object[]{prefix}));
        }
        for (ElemTemplateElement child = this.getFirstChildElem(); child != null; child = child.getNextSiblingElem()) {
            ElemTextLiteral tl;
            char[] chars;
            if (86 != child.getXSLToken()) continue;
            ElemExtensionScript sdecl = (ElemExtensionScript)child;
            lang = sdecl.getLang();
            srcURL = sdecl.getSrc();
            ElemTemplateElement childOfSDecl = sdecl.getFirstChildElem();
            if (null == childOfSDecl || 78 != childOfSDecl.getXSLToken() || (scriptSrc = new String(chars = (tl = (ElemTextLiteral)childOfSDecl).getChars())).trim().length() != 0) continue;
            scriptSrc = null;
        }
        if (null == lang) {
            lang = "javaclass";
        }
        if (lang.equals("javaclass") && scriptSrc != null) {
            throw new TransformerException(XSLMessages.createMessage("ER_ELEM_CONTENT_NOT_ALLOWED", new Object[]{scriptSrc}));
        }
        ExtensionNamespaceSupport extNsSpt = null;
        ExtensionNamespacesManager extNsMgr = sroot.getExtensionNamespacesManager();
        if (extNsMgr.namespaceIndex(declNamespace, extNsMgr.getExtensions()) == -1) {
            if (lang.equals("javaclass")) {
                if (null == srcURL) {
                    extNsSpt = extNsMgr.defineJavaNamespace(declNamespace);
                } else if (extNsMgr.namespaceIndex(srcURL, extNsMgr.getExtensions()) == -1) {
                    extNsSpt = extNsMgr.defineJavaNamespace(declNamespace, srcURL);
                }
            } else {
                String handler = "org.apache.xalan.extensions.ExtensionHandlerGeneral";
                Object[] args = new Object[]{declNamespace, this.m_elements, this.m_functions, lang, srcURL, scriptSrc, this.getSystemId()};
                extNsSpt = new ExtensionNamespaceSupport(declNamespace, handler, args);
            }
        }
        if (extNsSpt != null) {
            extNsMgr.registerExtension(extNsSpt);
        }
    }

    @Override
    public void runtimeInit(TransformerImpl transformer) throws TransformerException {
    }
}

