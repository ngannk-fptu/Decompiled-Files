/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.macro.MacroDefinitionSerializer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;

public class MacroDefinitionRequestSerializer
implements MacroDefinitionSerializer {
    private final MacroDefinitionSerializer wikiMarkupSerializer;

    public MacroDefinitionRequestSerializer(MacroDefinitionSerializer wikiMarkupSerializer) {
        this.wikiMarkupSerializer = wikiMarkupSerializer;
    }

    @Override
    public String serialize(MacroDefinition macroDefinition) {
        byte[] paramsBytes;
        MacroDefinition macroDefinitionWithoutBody = new MacroDefinition(macroDefinition);
        macroDefinitionWithoutBody.setBody(null);
        macroDefinitionWithoutBody.setTypedParameters(null);
        String wikiMarkup = this.wikiMarkupSerializer.serialize(macroDefinitionWithoutBody);
        try {
            paramsBytes = wikiMarkup.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Base64.encodeBase64URLSafeString((byte[])paramsBytes);
    }
}

