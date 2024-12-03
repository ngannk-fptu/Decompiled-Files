/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.DelegatingEditorResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorIdAndTypeResourceIdentifierUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorPageResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorShortcutResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorSpaceResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.EditorUserResourceIdentifierMarshallerAndUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers.IdAndTypeAnalyzingResourceIdentifierUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.setup.settings.SettingsManager;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EditorResourceIdentifierContextConfig {
    @Resource
    private ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> idAndTypeResourceIdentifierResolver;
    @Resource
    private ResourceIdentifierFactory resourceIdentifierFactory;
    @Resource
    private ResourceIdentifierContextUtility resourceIdentifierContextUtility;
    @Resource
    private SettingsManager settingsManager;

    EditorResourceIdentifierContextConfig() {
    }

    @Bean
    EditorIdAndTypeResourceIdentifierUnmarshaller editorIdAndTypeResourceIdentifierUnmarshaller() {
        return new EditorIdAndTypeResourceIdentifierUnmarshaller();
    }

    @Bean
    EditorPageResourceIdentifierMarshallerAndUnmarshaller editorPageResourceIdentifierMarshallerAndUnmarshaller() {
        return new EditorPageResourceIdentifierMarshallerAndUnmarshaller();
    }

    @Bean
    EditorSpaceResourceIdentifierMarshallerAndUnmarshaller editorSpaceResourceIdentifierMarshallerAndUnmarshaller() {
        return new EditorSpaceResourceIdentifierMarshallerAndUnmarshaller();
    }

    @Bean
    EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller editorBlogPostResourceIdentifierMarshallerAndUnmarshaller() {
        return new EditorBlogPostResourceIdentifierMarshallerAndUnmarshaller();
    }

    @Bean
    EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller editorAttachmentResourceIdentifierMarshallerAndUnmarshaller() {
        return new EditorAttachmentResourceIdentifierMarshallerAndUnmarshaller(this.editorPageResourceIdentifierMarshallerAndUnmarshaller(), this.editorBlogPostResourceIdentifierMarshallerAndUnmarshaller());
    }

    @Bean
    EditorShortcutResourceIdentifierMarshallerAndUnmarshaller editorShortcutResourceIdentifierMarshallerAndUnmarshaller() {
        return new EditorShortcutResourceIdentifierMarshallerAndUnmarshaller();
    }

    @Bean
    EditorUserResourceIdentifierMarshallerAndUnmarshaller editorUserResourceIdentifierMarshallerAndUnmarshaller() {
        return new EditorUserResourceIdentifierMarshallerAndUnmarshaller();
    }

    @Bean
    IdAndTypeAnalyzingResourceIdentifierUnmarshaller idAndTypeAnalyzingResourceIdentifierUnmarshaller() {
        return new IdAndTypeAnalyzingResourceIdentifierUnmarshaller(this.idAndTypeResourceIdentifierResolver, this.resourceIdentifierFactory, this.resourceIdentifierContextUtility, this.settingsManager);
    }

    @Bean
    DelegatingEditorResourceIdentifierMarshaller editorResourceIdentifierMarshaller() {
        return new DelegatingEditorResourceIdentifierMarshaller(this.editorPageResourceIdentifierMarshallerAndUnmarshaller(), this.editorBlogPostResourceIdentifierMarshallerAndUnmarshaller(), this.editorAttachmentResourceIdentifierMarshallerAndUnmarshaller(), this.editorShortcutResourceIdentifierMarshallerAndUnmarshaller(), this.editorSpaceResourceIdentifierMarshallerAndUnmarshaller(), this.editorUserResourceIdentifierMarshallerAndUnmarshaller());
    }
}

