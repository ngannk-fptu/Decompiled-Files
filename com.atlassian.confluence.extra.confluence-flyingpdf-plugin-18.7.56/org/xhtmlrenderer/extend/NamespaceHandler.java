/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

public interface NamespaceHandler {
    public String getNamespace();

    public StylesheetInfo getDefaultStylesheet(StylesheetFactory var1);

    public String getDocumentTitle(Document var1);

    public StylesheetInfo[] getStylesheets(Document var1);

    public String getAttributeValue(Element var1, String var2);

    public String getAttributeValue(Element var1, String var2, String var3);

    public String getClass(Element var1);

    public String getID(Element var1);

    public String getElementStyling(Element var1);

    public String getNonCssStyling(Element var1);

    public String getLang(Element var1);

    public String getLinkUri(Element var1);

    public String getAnchorName(Element var1);

    public boolean isImageElement(Element var1);

    public boolean isFormElement(Element var1);

    public String getImageSourceURI(Element var1);
}

