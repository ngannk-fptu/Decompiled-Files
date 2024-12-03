/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.wysiwyg;

import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.util.RendererUtil;
import com.opensymphony.util.TextUtils;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

public class WysiwygLinkHelper {
    static final String LINK_TYPE_ATTR = "linktype";
    static final String RAW_LINK_TYPE_ATTR_VALUE = "raw";
    static final String WIKI_DESTINATION_ATTR = "wikidestination";
    static final String ORIGINAL_ALIAS_ATTR = "originalalias";
    static final String ALIAS_SPECIFIED_ATTR = "aliasspecified";
    static final String WIKI_TITLE_ATTR = "wikititle";
    static final String CLASS_ATTR = "class";
    public static final String ERROR_CLASS_ATTR_VALUE = "linkerror";

    public static String getLinkInfoAttributes(Link link) {
        StringBuffer buffer = new StringBuffer();
        RendererUtil.appendAttribute(LINK_TYPE_ATTR, RAW_LINK_TYPE_ATTR_VALUE, buffer);
        RendererUtil.appendAttribute(WIKI_DESTINATION_ATTR, TextUtils.htmlEncode((String)link.getWikiDestination()), buffer);
        if (link.isAliasSpecified()) {
            RendererUtil.appendAttribute(ALIAS_SPECIFIED_ATTR, "true", buffer);
        } else {
            RendererUtil.appendAttribute(ORIGINAL_ALIAS_ATTR, TextUtils.htmlEncode((String)link.getLinkBody()), buffer);
        }
        if (link.getWikiTitle() != null) {
            RendererUtil.appendAttribute(WIKI_TITLE_ATTR, TextUtils.htmlEncode((String)link.getWikiTitle()), buffer);
        }
        if (link instanceof UnpermittedLink || link instanceof UnresolvedLink) {
            RendererUtil.appendAttribute(CLASS_ATTR, ERROR_CLASS_ATTR_VALUE, buffer);
        }
        return buffer.toString();
    }

    public static String createLinkWikiText(Node node, String newAlias) {
        String linkType = WysiwygLinkHelper.getNodeAttributeValue(node, LINK_TYPE_ATTR);
        if (!linkType.equals(RAW_LINK_TYPE_ATTR_VALUE)) {
            return "";
        }
        if (newAlias.startsWith("TEXTSEP")) {
            newAlias = newAlias.substring("TEXTSEP".length());
        }
        newAlias = newAlias.trim();
        String wikiDestination = WysiwygLinkHelper.getNodeAttributeValue(node, WIKI_DESTINATION_ATTR);
        String originalAlias = WysiwygLinkHelper.getNodeAttributeValue(node, ORIGINAL_ALIAS_ATTR);
        boolean aliasSpecified = Boolean.parseBoolean(WysiwygLinkHelper.getNodeAttributeValue(node, ALIAS_SPECIFIED_ATTR));
        String wikititle = WysiwygLinkHelper.getNodeAttributeValue(node, WIKI_TITLE_ATTR);
        ArrayList<String> components = new ArrayList<String>(3);
        if (aliasSpecified || !newAlias.equals(originalAlias)) {
            components.add(newAlias);
        }
        components.add(wikiDestination);
        if (wikititle != null) {
            components.add(wikititle);
        }
        return "[" + StringUtils.join(components.iterator(), (char)'|') + "]";
    }

    private static String getNodeAttributeValue(Node node, String attributeName) {
        Node wikititleNode = node.getAttributes().getNamedItem(attributeName);
        return wikititleNode == null ? null : wikititleNode.getNodeValue();
    }
}

