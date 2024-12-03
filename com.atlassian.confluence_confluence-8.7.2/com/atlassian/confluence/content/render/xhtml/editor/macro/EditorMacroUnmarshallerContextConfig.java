/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEntityExpander;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorMacroUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroNameAndParameterSubParser;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterSerializer;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.annotation.Resource;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EditorMacroUnmarshallerContextConfig {
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private HtmlToXmlConverter htmlToXmlConverter;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private XMLEventFactory xmlEventFactory;
    @Resource
    private XmlEntityExpander xmlEntityExpander;
    @Resource
    private MacroParameterSerializer defaultMacroParameterSerializer;
    @Resource
    private MacroParameterTypeParser macroParameterTypeParser;

    EditorMacroUnmarshallerContextConfig() {
    }

    @Bean
    Unmarshaller<MacroDefinition> editorMacroUnmarshaller() {
        return new EditorMacroUnmarshaller(this.xmlFragmentOutputFactory, this.xmlEventFactory, new MacroNameAndParameterSubParser(this.defaultMacroParameterSerializer, this.macroParameterTypeParser), this.xmlEventReaderFactory, this.xmlEntityExpander, this.htmlToXmlConverter);
    }
}

