/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xpath.XPathVisitor;

public class XSLTVisitor
extends XPathVisitor {
    public boolean visitInstruction(ElemTemplateElement elem) {
        return true;
    }

    public boolean visitStylesheet(ElemTemplateElement elem) {
        return true;
    }

    public boolean visitTopLevelInstruction(ElemTemplateElement elem) {
        return true;
    }

    public boolean visitTopLevelVariableOrParamDecl(ElemTemplateElement elem) {
        return true;
    }

    public boolean visitVariableOrParamDecl(ElemVariable elem) {
        return true;
    }

    public boolean visitLiteralResultElement(ElemLiteralResult elem) {
        return true;
    }

    public boolean visitAVT(AVT elem) {
        return true;
    }

    public boolean visitExtensionElement(ElemExtensionCall elem) {
        return true;
    }
}

