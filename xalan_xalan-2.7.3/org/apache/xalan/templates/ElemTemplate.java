/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetComposed;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;

public class ElemTemplate
extends ElemTemplateElement {
    static final long serialVersionUID = -5283056789965384058L;
    private String m_publicId;
    private String m_systemId;
    private Stylesheet m_stylesheet;
    private XPath m_matchPattern = null;
    private QName m_name = null;
    private QName m_mode;
    private double m_priority = Double.NEGATIVE_INFINITY;
    public int m_frameSize;
    int m_inArgsSize;
    private int[] m_argsQNameIDs;

    @Override
    public String getPublicId() {
        return this.m_publicId;
    }

    @Override
    public String getSystemId() {
        return this.m_systemId;
    }

    @Override
    public void setLocaterInfo(SourceLocator locator) {
        this.m_publicId = locator.getPublicId();
        this.m_systemId = locator.getSystemId();
        super.setLocaterInfo(locator);
    }

    @Override
    public StylesheetComposed getStylesheetComposed() {
        return this.m_stylesheet.getStylesheetComposed();
    }

    @Override
    public Stylesheet getStylesheet() {
        return this.m_stylesheet;
    }

    public void setStylesheet(Stylesheet sheet) {
        this.m_stylesheet = sheet;
    }

    @Override
    public StylesheetRoot getStylesheetRoot() {
        return this.m_stylesheet.getStylesheetRoot();
    }

    public void setMatch(XPath v) {
        this.m_matchPattern = v;
    }

    public XPath getMatch() {
        return this.m_matchPattern;
    }

    public void setName(QName v) {
        this.m_name = v;
    }

    public QName getName() {
        return this.m_name;
    }

    public void setMode(QName v) {
        this.m_mode = v;
    }

    public QName getMode() {
        return this.m_mode;
    }

    public void setPriority(double v) {
        this.m_priority = v;
    }

    public double getPriority() {
        return this.m_priority;
    }

    @Override
    public int getXSLToken() {
        return 19;
    }

    @Override
    public String getNodeName() {
        return "template";
    }

    @Override
    public void compose(StylesheetRoot sroot) throws TransformerException {
        super.compose(sroot);
        StylesheetRoot.ComposeState cstate = sroot.getComposeState();
        Vector vnames = cstate.getVariableNames();
        if (null != this.m_matchPattern) {
            this.m_matchPattern.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
        }
        cstate.resetStackFrameSize();
        this.m_inArgsSize = 0;
    }

    @Override
    public void endCompose(StylesheetRoot sroot) throws TransformerException {
        StylesheetRoot.ComposeState cstate = sroot.getComposeState();
        super.endCompose(sroot);
        this.m_frameSize = cstate.getFrameSize();
        cstate.resetStackFrameSize();
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        XPathContext xctxt = transformer.getXPathContext();
        transformer.getStackGuard().checkForInfinateLoop();
        xctxt.pushRTFContext();
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEvent(this);
        }
        transformer.executeChildTemplates((ElemTemplateElement)this, true);
        if (transformer.getDebug()) {
            transformer.getTraceManager().fireTraceEndEvent(this);
        }
        xctxt.popRTFContext();
    }

    @Override
    public void recompose(StylesheetRoot root) {
        root.recomposeTemplates(this);
    }
}

