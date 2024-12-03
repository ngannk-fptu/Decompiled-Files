/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.pages.wysiwyg;

import com.atlassian.confluence.core.ContentEntityObject;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ConfluenceWysiwygConverter {
    public String convertWikiMarkupToXHtml(ContentEntityObject var1, String var2);

    public String convertWikiMarkupToXHtml(ContentEntityObject var1, String var2, String var3);

    public String convertWikiMarkupToXHtml(String var1, String var2);

    public String convertWikiMarkupToXHtml(String var1, String var2, String var3);

    public String convertXHtmlToWikiMarkup(ContentEntityObject var1, String var2);

    public String convertXHtmlToWikiMarkup(String var1, String var2);

    public String convertToPreview(String var1, String var2, String var3, String var4);
}

