/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorPageResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorShortcutResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorSpaceResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorUserResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatingEditorResourceIdentifierMarshaller
implements StaxStreamMarshaller<ResourceIdentifier> {
    private static final Logger log = LoggerFactory.getLogger(DelegatingEditorResourceIdentifierMarshaller.class);
    private final EditorPageResourceIdentifierMarshallerAndUnmarshaller pageResourceIdentifierMarshallerAndUnmarshaller;
    private final EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller blogPostResourceIdentifierMarshallerAndUnmarshaller;
    private final EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller attachmentResourceIdentifierMarshallerAndUnmarshaller;
    private final EditorShortcutResourceIdentifierMarshallerAndUnmarshaller shortcutResourceIdentifierMarshallerAndUnmarshaller;
    private final EditorSpaceResourceIdentifierMarshallerAndUnmarshaller spaceResourceIdentifierMarshallerAndUnmarshaller;
    private final EditorUserResourceIdentifierMarshallerAndUnmarshaller userResourceIdentifierMarshallerAndUnmarshaller;

    public DelegatingEditorResourceIdentifierMarshaller(EditorPageResourceIdentifierMarshallerAndUnmarshaller pageResourceIdentifierMarshallerAndUnmarshaller, EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller blogPostResourceIdentifierMarshallerAndUnmarshaller, EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller attachmentResourceIdentifierMarshallerAndUnmarshaller, EditorShortcutResourceIdentifierMarshallerAndUnmarshaller shortcutResourceIdentifierMarshallerAndUnmarshaller, EditorSpaceResourceIdentifierMarshallerAndUnmarshaller spaceResourceIdentifierMarshallerAndUnmarshaller, EditorUserResourceIdentifierMarshallerAndUnmarshaller userResourceIdentifierMarshallerAndUnmarshaller) {
        this.pageResourceIdentifierMarshallerAndUnmarshaller = pageResourceIdentifierMarshallerAndUnmarshaller;
        this.blogPostResourceIdentifierMarshallerAndUnmarshaller = blogPostResourceIdentifierMarshallerAndUnmarshaller;
        this.attachmentResourceIdentifierMarshallerAndUnmarshaller = attachmentResourceIdentifierMarshallerAndUnmarshaller;
        this.shortcutResourceIdentifierMarshallerAndUnmarshaller = shortcutResourceIdentifierMarshallerAndUnmarshaller;
        this.spaceResourceIdentifierMarshallerAndUnmarshaller = spaceResourceIdentifierMarshallerAndUnmarshaller;
        this.userResourceIdentifierMarshallerAndUnmarshaller = userResourceIdentifierMarshallerAndUnmarshaller;
    }

    @Override
    public void marshal(ResourceIdentifier resourceIdentifier, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        StaxStreamMarshaller<PageResourceIdentifier> staxStreamMarshaller = null;
        if (resourceIdentifier instanceof PageResourceIdentifier) {
            staxStreamMarshaller = this.pageResourceIdentifierMarshallerAndUnmarshaller;
        } else if (resourceIdentifier instanceof BlogPostResourceIdentifier) {
            staxStreamMarshaller = this.blogPostResourceIdentifierMarshallerAndUnmarshaller;
        } else if (resourceIdentifier instanceof AttachmentResourceIdentifier) {
            staxStreamMarshaller = this.attachmentResourceIdentifierMarshallerAndUnmarshaller;
        } else if (resourceIdentifier instanceof ShortcutResourceIdentifier) {
            staxStreamMarshaller = this.shortcutResourceIdentifierMarshallerAndUnmarshaller;
        } else if (resourceIdentifier instanceof SpaceResourceIdentifier) {
            staxStreamMarshaller = this.spaceResourceIdentifierMarshallerAndUnmarshaller;
        } else if (resourceIdentifier instanceof UserResourceIdentifier) {
            staxStreamMarshaller = this.userResourceIdentifierMarshallerAndUnmarshaller;
        } else {
            log.debug("No marshaller found to marshal resource identifier: " + resourceIdentifier);
        }
        if (staxStreamMarshaller != null) {
            staxStreamMarshaller.marshal((PageResourceIdentifier)resourceIdentifier, xmlStreamWriter, context);
        }
    }
}

