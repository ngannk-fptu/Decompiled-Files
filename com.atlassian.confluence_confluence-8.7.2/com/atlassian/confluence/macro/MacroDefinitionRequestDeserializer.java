/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroDefinitionDeserializer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;

public class MacroDefinitionRequestDeserializer
implements MacroDefinitionDeserializer {
    private final MacroDefinitionDeserializer wikiMarkupDeserializer;

    public MacroDefinitionRequestDeserializer(MacroDefinitionDeserializer wikiMarkupDeserializer) {
        this.wikiMarkupDeserializer = wikiMarkupDeserializer;
    }

    @Override
    public MacroDefinition deserialize(String serializedValue) {
        return this.wikiMarkupDeserializer.deserialize(this.decodeParams(serializedValue));
    }

    @Override
    public MacroDefinition deserializeWithTypedParameters(String serializedValue, ConversionContext conversionContext) {
        return this.wikiMarkupDeserializer.deserializeWithTypedParameters(this.decodeParams(serializedValue), conversionContext);
    }

    private String decodeParams(String serializedValue) {
        String decodedParams;
        try {
            decodedParams = new String(Base64.decodeBase64((String)serializedValue), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return decodedParams;
    }
}

