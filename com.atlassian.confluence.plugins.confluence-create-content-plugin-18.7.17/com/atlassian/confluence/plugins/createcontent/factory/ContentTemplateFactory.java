/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentBody$ContentBodyBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate$ContentTemplateBuilder
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.plugin.ModuleCompleteKey
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Strings
 */
package com.atlassian.confluence.plugins.createcontent.factory;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.plugin.ModuleCompleteKey;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugins.createcontent.factory.LabelFactory;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.fugue.Option;
import com.google.common.base.Strings;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContentTemplateFactory {
    private static ContentTemplate buildFrom(Optional<Space> space, ContentTemplateRef templateRef, PageTemplate pageTemplate, Expansion[] expansions, boolean newlyCreatedPageTemplate) {
        Expansions myExpansions = new Expansions(expansions);
        ContentTemplate.ContentTemplateBuilder contentTemplateBuilder = ContentTemplate.builder().templateType(ContentTemplateType.BLUEPRINT).originalTemplate(new ModuleCompleteKey(templateRef.getModuleCompleteKey())).referencingBlueprint(templateRef.getParent().getContentBlueprintId()).name(pageTemplate.getName()).description(pageTemplate.getDescription()).labels(pageTemplate.getLabels().stream().map(input -> LabelFactory.buildFrom(input, myExpansions)).collect(Collectors.toList()));
        space.ifPresent(arg_0 -> ((ContentTemplate.ContentTemplateBuilder)contentTemplateBuilder).space(arg_0));
        if (templateRef.getTemplateId() != 0L || newlyCreatedPageTemplate) {
            contentTemplateBuilder.templateId(ContentTemplateId.fromString((String)String.valueOf(pageTemplate.getId())));
        } else {
            contentTemplateBuilder.templateId(ContentTemplateId.fromString((String)templateRef.getId().toString()));
        }
        if (myExpansions.canExpand("body") && !Strings.isNullOrEmpty((String)pageTemplate.getContent())) {
            ContentBody contentBody = ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().value(pageTemplate.getContent())).representation(ContentRepresentation.STORAGE)).build();
            contentTemplateBuilder.body(contentBody);
        }
        return contentTemplateBuilder.build();
    }

    @Deprecated
    public static ContentTemplate buildFromNewPageTemplate(Option<Space> space, ContentTemplateRef templateRef, PageTemplate pageTemplate, Expansion[] expansions) {
        return ContentTemplateFactory.buildFromNewPageTemplate(Optional.ofNullable((Space)space.getOrNull()), templateRef, pageTemplate, expansions);
    }

    public static ContentTemplate buildFromNewPageTemplate(Optional<Space> space, ContentTemplateRef templateRef, PageTemplate pageTemplate, Expansion[] expansions) {
        return ContentTemplateFactory.buildFrom(space, templateRef, pageTemplate, expansions, true);
    }

    @Deprecated
    public static ContentTemplate buildFrom(Option<Space> space, ContentTemplateRef templateRef, PageTemplate pageTemplate, Expansion[] expansions) {
        return ContentTemplateFactory.buildFrom(Optional.ofNullable((Space)space.getOrNull()), templateRef, pageTemplate, expansions);
    }

    public static ContentTemplate buildFrom(Optional<Space> space, ContentTemplateRef templateRef, PageTemplate pageTemplate, Expansion[] expansions) {
        return ContentTemplateFactory.buildFrom(space, templateRef, pageTemplate, expansions, false);
    }
}

