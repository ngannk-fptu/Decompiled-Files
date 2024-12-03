/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier
 *  com.atlassian.confluence.xhtml.api.EmbeddedImage
 */
package com.atlassian.confluence.plugins.emailgateway.attachmentconverter.instances;

import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentConverter;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentFile;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;

public class ImageAttachmentConverter
implements AttachmentConverter<EmbeddedImage> {
    @Override
    public EmbeddedImage convertAttachment(AttachmentFile attachmentFile) {
        if (!attachmentFile.getContentType().startsWith("image/")) {
            return null;
        }
        AttachmentResourceIdentifier ri = new AttachmentResourceIdentifier(attachmentFile.getFileName());
        return new DefaultEmbeddedImage((NamedResourceIdentifier)ri);
    }

    @Override
    public Class<EmbeddedImage> getConversionClass() {
        return EmbeddedImage.class;
    }
}

