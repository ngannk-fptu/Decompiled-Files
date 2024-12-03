/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import java.util.Map;

public interface MacroParameterSerializer {
    public String serialize(Map<String, String> var1);

    public Map<String, String> deserialize(String var1);
}

