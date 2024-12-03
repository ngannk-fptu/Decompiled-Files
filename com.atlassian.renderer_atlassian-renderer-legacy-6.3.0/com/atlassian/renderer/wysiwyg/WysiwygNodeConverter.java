/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

import com.atlassian.renderer.wysiwyg.ListContext;
import com.atlassian.renderer.wysiwyg.Styles;
import com.atlassian.renderer.wysiwyg.WysiwygConverter;
import org.w3c.dom.Node;

public interface WysiwygNodeConverter {
    public static final String WYSIWYG_ATTRIBUTE = "wysiwyg";

    public String convertXHtmlToWikiMarkup(Node var1, Node var2, WysiwygConverter var3, Styles var4, ListContext var5, boolean var6, boolean var7, boolean var8);
}

