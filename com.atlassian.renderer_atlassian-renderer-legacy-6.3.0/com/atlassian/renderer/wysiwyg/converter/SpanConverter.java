/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.WysiwygMacroHelper;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.w3c.dom.Node;

public class SpanConverter
implements Converter {
    static final SpanConverter INSTANCE = new SpanConverter();

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.getNode().getNodeName().equalsIgnoreCase("span");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        Node node = nodeContext.getNode();
        String className = nodeContext.getAttribute("class");
        if (className != null) {
            String macroName = WysiwygMacroHelper.getMacroName(node);
            if (macroName != null) {
                Macro macro = wysiwygConverter.getMacroManager().getEnabledMacro(macroName);
                NodeContext contextWithConvertText = new NodeContext.Builder(nodeContext).ignoreText(false).build();
                return WysiwygMacroHelper.convertMacroFromNode(contextWithConvertText, wysiwygConverter, macro);
            }
            if (className.indexOf("wikisrc") != -1) {
                return DefaultWysiwygConverter.getRawChildText(node, false);
            }
            if (className.indexOf("error") != -1) {
                return "";
            }
        }
        return wysiwygConverter.convertChildren(nodeContext);
    }
}

