/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.ObjectNode
 */
package com.benryan.conversion.macro;

import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class MacroParameters {
    private final Map macroParameters;

    public MacroParameters(Map macroParameters) {
        this.macroParameters = macroParameters;
    }

    public Map get() {
        return this.macroParameters;
    }

    public ObjectNode toJson() {
        this.macroParameters.remove(": = | RAW | = :");
        ObjectNode json = (ObjectNode)new ObjectMapper().valueToTree((Object)this.macroParameters);
        return json;
    }
}

