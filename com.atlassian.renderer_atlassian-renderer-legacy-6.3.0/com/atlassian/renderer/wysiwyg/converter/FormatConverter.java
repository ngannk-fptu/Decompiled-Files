/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class FormatConverter
implements Converter {
    public static FormatConverter INSTANCE = new FormatConverter();
    private static final Map<String, String> STYLE_MAPPINGS;
    static final Collection<String> STYLE_NODE_TYPES;

    private FormatConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return STYLE_MAPPINGS.keySet().contains(nodeContext.getNode().getNodeName().toLowerCase());
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String style = STYLE_MAPPINGS.get(nodeContext.getNode().getNodeName().toLowerCase());
        return wysiwygConverter.getSeparator("text", nodeContext) + wysiwygConverter.convertChildren(new NodeContext.Builder(nodeContext).addStyle(style).ignoreText(false).previousSibling(null).build());
    }

    static {
        HashMap<String, String> temp = new HashMap<String, String>();
        temp.put("b", "font-weight: bold");
        temp.put("strong", "font-weight: bold");
        temp.put("i", "font-style: italic");
        temp.put("em", "font-style: italic");
        temp.put("del", "text-decoration: line-through");
        temp.put("strike", "text-decoration: line-through");
        temp.put("ins", "text-decoration: underline");
        temp.put("u", "text-decoration: underline");
        temp.put("sub", "baseline-shift: sub");
        temp.put("sup", "baseline-shift: sup");
        temp.put("cite", "style-citation");
        temp.put("code", "style-monospace");
        STYLE_MAPPINGS = Collections.unmodifiableMap(temp);
        STYLE_NODE_TYPES = Collections.unmodifiableSet(STYLE_MAPPINGS.keySet());
    }
}

