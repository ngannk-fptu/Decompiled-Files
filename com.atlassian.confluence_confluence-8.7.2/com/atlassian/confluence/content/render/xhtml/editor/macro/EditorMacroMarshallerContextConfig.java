/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CustomImageEditorMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CustomPlaceholderEditorMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodilessMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodyMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.ImprovedEditorUnknownMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.impl.content.render.xhtml.editor.macro.DelegatingEditorMacroMarshaller;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EditorMacroMarshallerContextConfig {
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private MacroManager xhtmlMacroManager;
    @Resource
    private CommonMacroAttributeWriter commonMacroAttributeWriter;
    @Resource
    private PlaceholderUrlFactory placeholderUrlFactory;
    @Resource
    private ContextPathHolder contextPathHolder;
    @Resource
    private MacroSchemaMigrator macroSchemaMigrator;

    EditorMacroMarshallerContextConfig() {
    }

    @Bean
    Marshaller<MacroDefinition> editorMacroMarshaller() {
        return new DelegatingEditorMacroMarshaller(new EditorMacroMarshaller(this.xhtmlMacroManager, Arrays.asList(new CustomPlaceholderEditorMarshaller(this.commonMacroAttributeWriter, this.placeholderUrlFactory, this.xmlFragmentOutputFactory), new CustomImageEditorMacroMarshaller(this.commonMacroAttributeWriter, this.contextPathHolder, this.placeholderUrlFactory, this.xmlFragmentOutputFactory), new EditorBodyMacroMarshaller(this.commonMacroAttributeWriter, this.placeholderUrlFactory, this.xmlFragmentOutputFactory), new EditorBodilessMacroMarshaller(this.commonMacroAttributeWriter, this.placeholderUrlFactory, this.xmlFragmentOutputFactory), new ImprovedEditorUnknownMacroMarshaller(this.commonMacroAttributeWriter, this.placeholderUrlFactory, this.xmlFragmentOutputFactory))), this.macroSchemaMigrator);
    }
}

