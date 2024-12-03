/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import org.apache.xalan.templates.ElemTemplateElement;

public class ElemOtherwise
extends ElemTemplateElement {
    static final long serialVersionUID = 1863944560970181395L;

    @Override
    public int getXSLToken() {
        return 39;
    }

    @Override
    public String getNodeName() {
        return "otherwise";
    }
}

