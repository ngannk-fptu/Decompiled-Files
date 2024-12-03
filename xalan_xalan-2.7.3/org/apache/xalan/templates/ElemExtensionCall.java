/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionsTable;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemExtensionDecl;
import org.apache.xalan.templates.ElemFallback;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetComposed;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XSLTVisitor;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ElemExtensionCall
extends ElemLiteralResult {
    static final long serialVersionUID = 3171339708500216920L;
    String m_extns;
    String m_lang;
    String m_srcURL;
    String m_scriptSrc;
    ElemExtensionDecl m_decl = null;

    @Override
    public int getXSLToken() {
        return 79;
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        this.m_extns = this.getNamespace();
        this.m_decl = this.getElemExtensionDecl(sroot, this.m_extns);
        if (this.m_decl == null) {
            sroot.getExtensionNamespacesManager().registerExtension(this.m_extns);
        }
    }

    private ElemExtensionDecl getElemExtensionDecl(StylesheetRoot stylesheet, String namespace) {
        ElemExtensionDecl decl = null;
        int n = stylesheet.getGlobalImportCount();
        for (int i = 0; i < n; ++i) {
            StylesheetComposed imported = stylesheet.getGlobalImport(i);
            for (ElemTemplateElement child = imported.getFirstChildElem(); child != null; child = child.getNextSiblingElem()) {
                String prefix;
                String declNamespace;
                if (85 != child.getXSLToken() || !namespace.equals(declNamespace = child.getNamespaceForPrefix(prefix = (decl = (ElemExtensionDecl)child).getPrefix()))) continue;
                return decl;
            }
        }
        return null;
    }

    private void executeFallbacks(TransformerImpl transformer) throws TransformerException {
        ElemTemplateElement child = this.m_firstChild;
        while (child != null) {
            if (child.getXSLToken() == 57) {
                try {
                    transformer.pushElemTemplateElement(child);
                    ((ElemFallback)child).executeFallback(transformer);
                }
                finally {
                    transformer.popElemTemplateElement();
                }
            }
            child = child.m_nextSibling;
        }
    }

    private boolean hasFallbackChildren() {
        ElemTemplateElement child = this.m_firstChild;
        while (child != null) {
            if (child.getXSLToken() == 57) {
                return true;
            }
            child = child.m_nextSibling;
        }
        return false;
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        block15: {
            if (transformer.getStylesheet().isSecureProcessing()) {
                throw new TransformerException(XSLMessages.createMessage("ER_EXTENSION_ELEMENT_NOT_ALLOWED_IN_SECURE_PROCESSING", new Object[]{this.getRawName()}));
            }
            if (transformer.getDebug()) {
                transformer.getTraceManager().fireTraceEvent(this);
            }
            try {
                ExtensionHandler nsh;
                transformer.getResultTreeHandler().flushPending();
                ExtensionsTable etable = transformer.getExtensionsTable();
                ExtensionHandler extensionHandler = nsh = etable != null ? etable.get(this.m_extns) : null;
                if (null == nsh) {
                    if (this.hasFallbackChildren()) {
                        this.executeFallbacks(transformer);
                    } else {
                        TransformerException te = new TransformerException(XSLMessages.createMessage("ER_CALL_TO_EXT_FAILED", new Object[]{this.getNodeName()}));
                        transformer.getErrorListener().fatalError(te);
                    }
                    return;
                }
                try {
                    nsh.processElement(this.getLocalName(), this, transformer, this.getStylesheet(), this);
                }
                catch (Exception e) {
                    if (this.hasFallbackChildren()) {
                        this.executeFallbacks(transformer);
                        break block15;
                    }
                    if (e instanceof TransformerException) {
                        TransformerException te = (TransformerException)e;
                        if (null == te.getLocator()) {
                            te.setLocator(this);
                        }
                        transformer.getErrorListener().fatalError(te);
                        break block15;
                    }
                    if (e instanceof RuntimeException) {
                        transformer.getErrorListener().fatalError(new TransformerException(e));
                        break block15;
                    }
                    transformer.getErrorListener().warning(new TransformerException(e));
                }
            }
            catch (TransformerException e) {
                transformer.getErrorListener().fatalError(e);
            }
            catch (SAXException se) {
                throw new TransformerException(se);
            }
        }
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
    }

    public String getAttribute(String rawName, Node sourceNode, TransformerImpl transformer) throws TransformerException {
        AVT avt = this.getLiteralResultAttribute(rawName);
        if (null != avt && avt.getRawName().equals(rawName)) {
            XPathContext xctxt = transformer.getXPathContext();
            return avt.evaluate(xctxt, xctxt.getDTMHandleFromNode(sourceNode), this);
        }
        return null;
    }

    @Override
    protected boolean accept(XSLTVisitor visitor) {
        return visitor.visitExtensionElement(this);
    }
}

