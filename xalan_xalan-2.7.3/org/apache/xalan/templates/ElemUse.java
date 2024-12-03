/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.util.ArrayList;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemAttributeSet;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;

public class ElemUse
extends ElemTemplateElement {
    static final long serialVersionUID = 5830057200289299736L;
    private QName[] m_attributeSetsNames = null;

    public void setUseAttributeSets(Vector v) {
        int n = v.size();
        this.m_attributeSetsNames = new QName[n];
        for (int i = 0; i < n; ++i) {
            this.m_attributeSetsNames[i] = (QName)v.elementAt(i);
        }
    }

    public void setUseAttributeSets(QName[] v) {
        this.m_attributeSetsNames = v;
    }

    public QName[] getUseAttributeSets() {
        return this.m_attributeSetsNames;
    }

    public void applyAttrSets(TransformerImpl transformer, StylesheetRoot stylesheet) throws TransformerException {
        this.applyAttrSets(transformer, stylesheet, this.m_attributeSetsNames);
    }

    private void applyAttrSets(TransformerImpl transformer, StylesheetRoot stylesheet, QName[] attributeSetsNames) throws TransformerException {
        if (null != attributeSetsNames) {
            for (QName qname : attributeSetsNames) {
                ArrayList attrSets = stylesheet.getAttributeSetComposed(qname);
                if (null != attrSets) {
                    int nSets = attrSets.size();
                    for (int k = nSets - 1; k >= 0; --k) {
                        ElemAttributeSet attrSet = (ElemAttributeSet)attrSets.get(k);
                        attrSet.execute(transformer);
                    }
                    continue;
                }
                throw new TransformerException(XSLMessages.createMessage("ER_NO_ATTRIB_SET", new Object[]{qname}), this);
            }
        }
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        if (null != this.m_attributeSetsNames) {
            this.applyAttrSets(transformer, this.getStylesheetRoot(), this.m_attributeSetsNames);
        }
    }
}

