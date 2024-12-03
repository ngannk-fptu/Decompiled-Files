/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.WikiMarkupParser
 *  com.atlassian.renderer.v2.components.MacroTag
 *  com.atlassian.renderer.v2.components.WikiContentHandler
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.StrTokenizer
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.macro.MacroDefinitionDeserializer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.MacroManager;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;

public class MacroDefinitionWikiMarkupDeserializer
implements MacroDefinitionDeserializer {
    private final MacroManager dummyMacroManager;
    private final MacroParameterTypeParser macroParameterTypeParser;

    public MacroDefinitionWikiMarkupDeserializer(MacroParameterTypeParser macroParameterTypeParser) {
        this.macroParameterTypeParser = macroParameterTypeParser;
        this.dummyMacroManager = name -> null;
    }

    @Override
    public MacroDefinition deserialize(String serializedValue) {
        MacroDefinitionBuilder macroDefinitionBuilder = MacroDefinition.builder().withStorageVersion("0");
        WikiMarkupParser wikiMarkupParser = this.getWikiMarkupParser(macroDefinitionBuilder, null);
        wikiMarkupParser.parse(serializedValue);
        return macroDefinitionBuilder.build();
    }

    @Override
    public MacroDefinition deserializeWithTypedParameters(String serializedValue, ConversionContext conversionContext) {
        MacroDefinitionBuilder macroDefinitionBuilder = MacroDefinition.builder().withStorageVersion("0");
        WikiMarkupParser wikiMarkupParser = this.getWikiMarkupParser(macroDefinitionBuilder, conversionContext);
        wikiMarkupParser.parse(serializedValue);
        return macroDefinitionBuilder.build();
    }

    private WikiMarkupParser getWikiMarkupParser(final MacroDefinitionBuilder macroDefinitionBuilder, final ConversionContext conversionContext) {
        return new WikiMarkupParser(this.dummyMacroManager, new WikiContentHandler(){

            public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
                macroDefinitionBuilder.withName(macroTag.command.toLowerCase());
                try {
                    MacroDefinitionWikiMarkupDeserializer.this.parseMacroParameters(macroTag.argString, macroDefinitionBuilder, conversionContext);
                }
                catch (InvalidMacroParameterException e) {
                    throw new RuntimeException(e);
                }
            }

            public void handleText(StringBuffer buffer, String s) {
            }
        });
    }

    private void parseMacroParameters(String paramsDeclaration, MacroDefinitionBuilder macroDefinitionBuilder, ConversionContext conversionContext) throws InvalidMacroParameterException {
        if (StringUtils.isEmpty((CharSequence)paramsDeclaration)) {
            return;
        }
        StrTokenizer paramsTokenizer = new StrTokenizer(paramsDeclaration, '|');
        if (paramsTokenizer.hasNext()) {
            String parameterString = paramsTokenizer.nextToken();
            int splitPosition = parameterString.indexOf(61);
            if (splitPosition == -1) {
                macroDefinitionBuilder.setDefaultParameterValue(parameterString);
            } else {
                this.setParameterFromString(macroDefinitionBuilder, parameterString, splitPosition);
            }
        }
        int index = 1;
        while (paramsTokenizer.hasNext()) {
            String parameterString = paramsTokenizer.nextToken();
            int splitPosition = parameterString.indexOf(61);
            if (splitPosition == -1) {
                macroDefinitionBuilder.withParameter(String.valueOf(index), parameterString);
            } else {
                this.setParameterFromString(macroDefinitionBuilder, parameterString, splitPosition);
            }
            ++index;
        }
        if (conversionContext != null) {
            Map<String, Object> typedParameters = this.macroParameterTypeParser.parseMacroParameters(macroDefinitionBuilder.getName(), macroDefinitionBuilder.getParameters(), conversionContext);
            macroDefinitionBuilder.withTypedParameters(typedParameters);
        }
    }

    private void setParameterFromString(MacroDefinitionBuilder macroDefinitionBuilder, String parameterString, int splitPosition) {
        String parameterName = parameterString.substring(0, splitPosition).trim();
        String parameterValue = parameterString.substring(splitPosition + 1).trim();
        macroDefinitionBuilder.withParameter(parameterName, parameterValue);
    }
}

