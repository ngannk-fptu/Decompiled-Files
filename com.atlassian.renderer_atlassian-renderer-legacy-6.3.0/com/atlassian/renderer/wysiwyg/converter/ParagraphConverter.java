/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.apache.commons.lang.StringUtils;

class ParagraphConverter
implements Converter {
    public static ParagraphConverter INSTANCE = new ParagraphConverter();

    private ParagraphConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.getNode().getNodeName().equalsIgnoreCase("p");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        if (DefaultWysiwygConverter.isUserNewline(nodeContext.getNode())) {
            return wysiwygConverter.getSeparator("userNewline", nodeContext) + (nodeContext.isInTable() ? "\\\\\n " : "\n");
        }
        String children = wysiwygConverter.convertChildren(new NodeContext.Builder(nodeContext).ignoreText(false).previousSibling(null).build());
        if (StringUtils.isBlank((String)children)) {
            return null;
        }
        String paragraphClass = nodeContext.getAttribute("class");
        if ("atl_conf_pad".equals(paragraphClass) && children.trim().equals("&nbsp;")) {
            return null;
        }
        if (children.trim().equals("&nbsp;")) {
            return wysiwygConverter.getSeparator("forcedNewline", nodeContext) + "\\\\ ";
        }
        return wysiwygConverter.getSeparator("p", nodeContext) + children;
    }
}

