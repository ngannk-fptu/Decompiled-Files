/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.WysiwygConverter;

public interface MacroBodyConverter {
    public String convertXhtmlToWikiMarkup(NodeContext var1, WysiwygConverter var2);
}

