/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import org.apache.commons.lang3.StringUtils;

public class SpaceResourceIdentifierResolver
implements ResourceIdentifierResolver<SpaceResourceIdentifier, Space> {
    private final SpaceManager spaceManager;

    public SpaceResourceIdentifierResolver(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public Space resolve(SpaceResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        String spaceKey = resourceIdentifier.getSpaceKey();
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new CannotResolveResourceIdentifierException(resourceIdentifier, "The resource identifier '" + resourceIdentifier + "' cannot be resolved. A spaceKey is required.");
        }
        return this.spaceManager.getSpace(spaceKey);
    }
}

