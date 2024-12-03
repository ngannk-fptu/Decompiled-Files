/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateXMLOutputModel;
import freemarker.core.XHTMLOutputFormat;

public class TemplateXHTMLOutputModel
extends TemplateXMLOutputModel {
    protected TemplateXHTMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override
    public XHTMLOutputFormat getOutputFormat() {
        return XHTMLOutputFormat.INSTANCE;
    }
}

