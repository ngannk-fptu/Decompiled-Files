/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.wysiwyg.MacroBodyConverter;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.WysiwygConverter;
import com.opensymphony.util.TextUtils;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InlineHtmlMacro
extends BaseMacro
implements MacroBodyConverter {
    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        return body;
    }

    @Override
    public String convertXhtmlToWikiMarkup(NodeContext nodeContext, WysiwygConverter wysiwygConverter) {
        String markup = this.writeNodeListHTML(nodeContext.getNode().getChildNodes());
        markup = RenderUtils.trimInitialNewline(markup);
        markup = StringUtils.chomp((String)markup, (String)"\n");
        return markup;
    }

    private String writeNodeListHTML(NodeList childNodes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            sb.append(this.writeNodeHTML(childNodes.item(i)));
        }
        return sb.toString();
    }

    private String writeNodeHTML(Node node) {
        if (node.getNodeType() == 3) {
            return node.getNodeValue();
        }
        StringBuffer sb = new StringBuffer();
        return sb.append("<").append(node.getNodeName()).append(this.writeNodeAttributes(node)).append(">").append(this.writeNodeListHTML(node.getChildNodes())).append("</").append(node.getNodeName()).append(">").toString();
    }

    private String writeNodeAttributes(Node node) {
        StringBuffer sb = new StringBuffer("");
        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); ++i) {
                Node attr = attrs.item(i);
                if (TextUtils.stringSet((String)attr.getNodeValue())) {
                    sb.append(" ").append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\"");
                    continue;
                }
                sb.append(" ").append(attr.getNodeName());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return false;
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }
}

