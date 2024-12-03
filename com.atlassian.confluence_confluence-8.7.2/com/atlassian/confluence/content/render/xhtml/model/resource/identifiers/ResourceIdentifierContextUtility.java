/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageTemplateResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.templates.PageTemplate;

public interface ResourceIdentifierContextUtility {
    public ResourceIdentifier createAbsoluteResourceIdentifier(ContentEntityObject var1);

    default public ResourceIdentifier createAbsolutePageTemplateResourceIdentifier(PageTemplate template) {
        return new PageTemplateResourceIdentifier(template.getId());
    }

    public ResourceIdentifier convertToAbsolute(ResourceIdentifier var1, ContentEntityObject var2);

    public ResourceIdentifier convertToRelative(ResourceIdentifier var1, ContentEntityObject var2);
}

