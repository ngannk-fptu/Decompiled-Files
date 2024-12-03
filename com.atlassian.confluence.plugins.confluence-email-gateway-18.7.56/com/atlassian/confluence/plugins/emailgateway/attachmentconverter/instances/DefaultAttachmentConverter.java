/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.xhtml.api.Link
 */
package com.atlassian.confluence.plugins.emailgateway.attachmentconverter.instances;

import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentFile;
import com.atlassian.confluence.xhtml.api.Link;

public class DefaultAttachmentConverter
implements AttachmentConverter<Link> {
    @Override
    public Link convertAttachment(AttachmentFile attachmentFile) {
        AttachmentResourceIdentifier attachmentResourceIdentifier = new AttachmentResourceIdentifier(attachmentFile.getFileName());
        return DefaultLink.builder().withDestinationResourceIdentifier((ResourceIdentifier)attachmentResourceIdentifier).build();
    }

    @Override
    public Class<Link> getConversionClass() {
        return Link.class;
    }
}

