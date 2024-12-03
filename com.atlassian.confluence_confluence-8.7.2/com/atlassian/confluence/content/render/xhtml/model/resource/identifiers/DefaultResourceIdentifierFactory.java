/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierCreationException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResourceIdentifierFactory
implements ResourceIdentifierFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultResourceIdentifierFactory.class);

    @Override
    public ResourceIdentifier getResourceIdentifier(Object resource, ConversionContext context) throws ResourceIdentifierCreationException {
        ResourceIdentifier resourceIdentifier;
        if (resource instanceof AbstractPage && ((AbstractPage)resource).isDraft()) {
            resourceIdentifier = new ContentEntityResourceIdentifier(((AbstractPage)resource).getId());
        } else if (resource instanceof Page) {
            Page page = (Page)resource;
            resourceIdentifier = new PageResourceIdentifier(page.getSpaceKey(), page.getTitle());
        } else if (resource instanceof BlogPost) {
            BlogPost blogPost = (BlogPost)resource;
            resourceIdentifier = new BlogPostResourceIdentifier(blogPost.getSpaceKey(), blogPost.getTitle(), blogPost.getPostingCalendarDate());
        } else {
            if (resource instanceof Attachment) {
                Attachment attachment = (Attachment)resource;
                ResourceIdentifier attachmentContainerResourceIdentifier = null;
                ContentEntityObject entity = context.getEntity();
                ContentEntityObject container = attachment.getContainer();
                if (entity == null || container == null || entity.getId() != container.getId()) {
                    logger.debug("Entity [{}] # Attachment Container [{}]", (Object)entity, (Object)container);
                    attachmentContainerResourceIdentifier = this.getResourceIdentifier(container, context);
                }
                if (attachmentContainerResourceIdentifier != null && !(attachmentContainerResourceIdentifier instanceof AttachmentContainerResourceIdentifier)) {
                    throw new ResourceIdentifierCreationException(resource, "Invalid attachment container: " + attachmentContainerResourceIdentifier);
                }
                return new AttachmentResourceIdentifier((AttachmentContainerResourceIdentifier)attachmentContainerResourceIdentifier, attachment.getFileName());
            }
            if (resource instanceof Draft) {
                resourceIdentifier = new DraftResourceIdentifier(((Draft)resource).getId());
            } else if (resource instanceof PersonalInformation) {
                ConfluenceUser user = ((PersonalInformation)resource).getUser();
                resourceIdentifier = UserResourceIdentifier.create(user.getKey());
            } else if (resource instanceof Space) {
                resourceIdentifier = new SpaceResourceIdentifier(((Space)resource).getKey());
            } else if (resource instanceof ContentEntityObject) {
                resourceIdentifier = new ContentEntityResourceIdentifier(((ContentEntityObject)resource).getId());
            } else if (resource instanceof PageTemplate) {
                resourceIdentifier = new PageTemplateResourceIdentifier(((PageTemplate)resource).getId());
            } else {
                throw new ResourceIdentifierCreationException(resource, "Resource not supported.");
            }
        }
        return resourceIdentifier;
    }
}

