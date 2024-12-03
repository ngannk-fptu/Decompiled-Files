/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.WysiwygMacroHelper;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.apache.commons.lang.StringUtils;

final class DivConverter
implements Converter {
    static DivConverter INSTANCE = new DivConverter();

    private DivConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("div");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        if (nodeContext.hasClass("error")) {
            return "";
        }
        String macroName = WysiwygMacroHelper.getMacroName(nodeContext.getNode());
        if (macroName != null) {
            Macro macro = wysiwygConverter.getMacroManager().getEnabledMacro(macroName);
            NodeContext macroConversionContext = new NodeContext.Builder(nodeContext).ignoreText(false).build();
            String macroMarkup = WysiwygMacroHelper.convertMacroFromNode(macroConversionContext, wysiwygConverter, macro);
            if (StringUtils.isNotBlank((String)macroMarkup)) {
                return macroMarkup;
            }
        }
        if (WysiwygMacroHelper.isMacroBody(nodeContext.getNode())) {
            NodeContext conversionContext = new NodeContext.Builder(nodeContext).ignoreText(false).previousSibling(null).build();
            return wysiwygConverter.convertChildren(conversionContext);
        }
        NodeContext.Builder contextBuilder = new NodeContext.Builder(nodeContext).ignoreText(false);
        if (nodeContext.getPreviousSibling() == null) {
            contextBuilder.previousSibling(nodeContext.getNode());
        }
        return wysiwygConverter.convertChildren(contextBuilder.build());
    }
}

