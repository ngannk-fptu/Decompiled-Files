/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.placeholder;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.editor.placeholder.EditorPlaceholderMarshaller;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.Placeholder;

public class ViewPlaceholderMarshaller
extends EditorPlaceholderMarshaller {
    public ViewPlaceholderMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        super(xmlStreamWriterTemplate);
    }

    @Override
    public Streamable marshal(Placeholder placeholder, ConversionContext conversionContext) throws XhtmlException {
        if (this.isTemplate(conversionContext)) {
            return super.marshal(placeholder, conversionContext);
        }
        return Streamables.empty();
    }

    private boolean isTemplate(ConversionContext conversionContext) {
        PageContext pageContext = conversionContext.getPageContext();
        if (pageContext != null) {
            Object contextParam = pageContext.getParam("com.atlassian.confluence.plugins.templates");
            return contextParam != null && (Boolean)contextParam != false;
        }
        return false;
    }
}

