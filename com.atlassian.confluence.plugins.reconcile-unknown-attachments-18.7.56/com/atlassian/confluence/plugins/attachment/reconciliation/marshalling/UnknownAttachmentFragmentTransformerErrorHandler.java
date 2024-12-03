/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.transformers.ThrowExceptionOnFragmentTransformationError
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.ThrowExceptionOnFragmentTransformationError;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentFormatException;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.xml.stream.XMLEventReader;

@ConfluenceComponent
public class UnknownAttachmentFragmentTransformerErrorHandler
extends ThrowExceptionOnFragmentTransformationError {
    private XmlOutputFactory xmlOutputFactory;

    public UnknownAttachmentFragmentTransformerErrorHandler(@ComponentImport XmlOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    public String handle(XMLEventReader fragmentReader, Exception e) {
        if (e instanceof UnknownAttachmentFormatException) {
            return StaxUtils.toXmlString((XMLEventReader)fragmentReader, (XmlOutputFactory)this.xmlOutputFactory);
        }
        return super.handle(fragmentReader, e);
    }
}

