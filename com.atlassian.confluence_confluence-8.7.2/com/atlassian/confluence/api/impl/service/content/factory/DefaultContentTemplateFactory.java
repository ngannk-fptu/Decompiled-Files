/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentBody$ContentBodyBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate$ContentTemplateBuilder
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ContentTemplateFactory;
import com.atlassian.confluence.api.impl.service.content.factory.LabelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintId;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.plugin.ModuleCompleteKey;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Strings;
import java.util.stream.Collectors;

public class DefaultContentTemplateFactory
extends ModelFactory<PageTemplate, ContentTemplate>
implements com.atlassian.confluence.pages.templates.ContentTemplateFactory,
ContentTemplateFactory {
    private final SpaceFactory spaceFactory;
    private final LabelFactory labelFactory;

    public DefaultContentTemplateFactory(SpaceFactory spaceFactory, LabelFactory labelFactory) {
        this.spaceFactory = spaceFactory;
        this.labelFactory = labelFactory;
    }

    @Override
    public ContentTemplate buildFrom(PageTemplate hibernateObject, Expansions expansions) {
        com.atlassian.plugin.ModuleCompleteKey referencingModuleCompleteKey = hibernateObject.getReferencingModuleCompleteKey();
        Space space = hibernateObject.getSpace();
        ContentTemplate.ContentTemplateBuilder contentTemplateBuilder = ContentTemplate.builder().name(hibernateObject.getName()).description(hibernateObject.getDescription()).templateId(hibernateObject.getContentTemplateId()).templateType(ContentTemplateType.PAGE).labels(hibernateObject.getLabels().stream().map(l -> this.labelFactory.buildFrom((Label)l, expansions)).collect(Collectors.toList()));
        if (space != null) {
            contentTemplateBuilder = contentTemplateBuilder.space(this.spaceFactory.buildFrom(space, expansions));
        }
        if (hibernateObject.getModuleCompleteKey() != null) {
            contentTemplateBuilder = contentTemplateBuilder.originalTemplate(new ModuleCompleteKey(hibernateObject.getPluginKey(), hibernateObject.getModuleKey()));
        }
        if (referencingModuleCompleteKey != null) {
            String spaceKey = space != null ? space.getKey() : null;
            contentTemplateBuilder = contentTemplateBuilder.referencingBlueprint(ContentBlueprintId.fromKeyAndSpaceString((String)referencingModuleCompleteKey.getCompleteKey(), (String)spaceKey));
        }
        if (expansions.canExpand("body")) {
            ContentBody contentBody = ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().value(Strings.isNullOrEmpty((String)hibernateObject.getContent()) ? "" : hibernateObject.getContent())).representation(ContentRepresentation.STORAGE)).build();
            contentTemplateBuilder.body(contentBody);
        }
        return contentTemplateBuilder.build();
    }
}

