/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonTemplateMarkupOutputModel;
import freemarker.core.XMLOutputFormat;

public class TemplateXMLOutputModel
extends CommonTemplateMarkupOutputModel<TemplateXMLOutputModel> {
    protected TemplateXMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override
    public XMLOutputFormat getOutputFormat() {
        return XMLOutputFormat.INSTANCE;
    }
}

