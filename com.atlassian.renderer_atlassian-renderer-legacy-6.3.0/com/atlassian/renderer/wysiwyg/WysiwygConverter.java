/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.wysiwyg.ListContext;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.Styles;
import org.w3c.dom.Node;

public interface WysiwygConverter {
    public String getAttribute(Node var1, String var2);

    public String convertChildren(Node var1, Styles var2, ListContext var3, boolean var4, boolean var5, boolean var6, boolean var7, Node var8);

    public String convertChildren(NodeContext var1);

    public String convertNode(NodeContext var1);

    public String convertXHtmlToWikiMarkup(String var1);

    public String convertWikiMarkupToXHtml(RenderContext var1, String var2);

    public String getMacroInfoHtml(RenderContext var1, String var2, int var3, int var4);

    public String getSep(Node var1, String var2, boolean var3, boolean var4);
}

