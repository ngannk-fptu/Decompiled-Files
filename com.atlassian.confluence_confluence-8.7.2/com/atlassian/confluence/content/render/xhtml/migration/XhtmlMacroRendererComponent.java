/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.WikiMarkupParser
 *  com.atlassian.renderer.v2.components.MacroTag
 *  com.atlassian.renderer.v2.components.RendererComponent
 *  com.atlassian.renderer.v2.components.WikiContentHandler
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRenderer;
import com.atlassian.confluence.content.render.xhtml.migration.MacroReplacementRendererRepository;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.UnknownMacroMigrationException;
import com.atlassian.confluence.macro.MacroDefinitionDeserializer;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.macro.xhtml.MacroMigrationManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlMacroRendererComponent
implements RendererComponent {
    private static final Logger log = LoggerFactory.getLogger(XhtmlMacroRendererComponent.class);
    public static final String MACRO_DECLARATION_POSITION = "macro-declaration-position";
    private final MacroManager v2MacroManager;
    private final MacroMigrationManager macroMigrationManager;
    private final Marshaller<MacroDefinition> macroMarshaller;
    private final MacroReplacementRendererRepository replacementRepository;
    private final MacroDefinitionDeserializer macroDefinitionWikiMarkupDeserializer;

    public XhtmlMacroRendererComponent(MacroManager v2MacroManager, MacroMigrationManager macroMigrationManager, Marshaller<MacroDefinition> macroMarshaller, MacroReplacementRendererRepository replacementRepository, MacroDefinitionDeserializer macroDefinitionWikiMarkupDeserializer) {
        this.v2MacroManager = v2MacroManager;
        this.macroMigrationManager = macroMigrationManager;
        this.macroMarshaller = macroMarshaller;
        this.replacementRepository = replacementRepository;
        this.macroDefinitionWikiMarkupDeserializer = macroDefinitionWikiMarkupDeserializer;
    }

    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderMacros();
    }

    public String render(String wiki, RenderContext context) {
        WikiMarkupParser parser = new WikiMarkupParser(this.v2MacroManager, (WikiContentHandler)new MacroHandler(context, wiki));
        return parser.parse(wiki);
    }

    private class MacroHandler
    implements WikiContentHandler {
        private final RenderContext renderContext;
        private final DefaultConversionContext conversionContext;
        private final String wiki;

        public MacroHandler(RenderContext renderContext, String wiki) {
            this.renderContext = renderContext;
            this.wiki = wiki;
            this.conversionContext = new DefaultConversionContext(renderContext);
        }

        public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
            log.debug("Migrating the macro tag '{}'", (Object)macroTag);
            String macroName = macroTag.command.toLowerCase();
            Macro v2Macro = XhtmlMacroRendererComponent.this.v2MacroManager.getEnabledMacro(macroName);
            if (v2Macro == null) {
                throw new UnknownMacroMigrationException(macroName);
            }
            String macroClass = v2Macro.getClass().getName();
            if (v2Macro instanceof ResourceAwareMacroDecorator) {
                macroClass = ((ResourceAwareMacroDecorator)v2Macro).getMacro().getClass().getName();
            }
            try {
                String result;
                log.debug("The macro tag '{}' matches enabled macro of class '{}'.", (Object)macroTag, (Object)macroClass);
                MacroDefinition macroDefinition = XhtmlMacroRendererComponent.this.macroDefinitionWikiMarkupDeserializer.deserializeWithTypedParameters(macroTag.originalText, this.conversionContext);
                PlainTextMacroBody macroBody = v2Macro.hasBody() ? new PlainTextMacroBody(body) : null;
                macroDefinition.setBody(macroBody);
                TokenType tokenType = v2Macro.getTokenType(macroDefinition.getParameters(), body, this.renderContext);
                MacroReplacementRenderer macroReplacementRenderer = XhtmlMacroRendererComponent.this.replacementRepository.getMacroReplacementRenderer(macroClass);
                if (macroReplacementRenderer != null) {
                    result = macroReplacementRenderer.render(macroDefinition, this.renderContext);
                } else {
                    MacroMigration macroMigration = XhtmlMacroRendererComponent.this.macroMigrationManager.getMacroMigration(macroName);
                    if (macroTag.getEndTag() != null && !this.isMacroDeclaredOnSingleLineByItself(macroTag)) {
                        this.conversionContext.setProperty(XhtmlMacroRendererComponent.MACRO_DECLARATION_POSITION, "inline");
                    }
                    macroDefinition = macroMigration.migrate(macroDefinition, this.conversionContext);
                    result = Streamables.writeToString(XhtmlMacroRendererComponent.this.macroMarshaller.marshal(macroDefinition, this.conversionContext));
                }
                buffer.append(this.renderContext.addRenderedContent((Object)result, tokenType));
            }
            catch (AbstractMethodError err) {
                log.warn("The macro tag '{}' of class '{}' threw an AbstractMethodError: {} ", (Object[])new String[]{macroTag.toString(), macroClass, err.getMessage()});
                throw new RuntimeException("AbstractMethodError migrating macro: " + macroName, err);
            }
            catch (Throwable e) {
                throw new RuntimeException("Error migrating macro: " + macroName, e);
            }
        }

        private boolean isMacroDeclaredOnSingleLineByItself(MacroTag macroTag) {
            char c;
            char c2;
            int i;
            if (macroTag.getEndTag() == null) {
                throw new IllegalArgumentException("This method only supported for macros that have bodies");
            }
            for (i = macroTag.startIndex - 1; i >= 0 && (c2 = this.wiki.charAt(i)) != '\n'; --i) {
                if (Character.isWhitespace(c2)) continue;
                return false;
            }
            int l = this.wiki.length();
            for (i = macroTag.getEndTag().endIndex + 1; i < l && (c = this.wiki.charAt(i)) != '\n'; ++i) {
                if (Character.isWhitespace(c)) continue;
                return false;
            }
            return true;
        }

        public void handleText(StringBuffer buffer, String s) {
            buffer.append(s);
        }
    }
}

