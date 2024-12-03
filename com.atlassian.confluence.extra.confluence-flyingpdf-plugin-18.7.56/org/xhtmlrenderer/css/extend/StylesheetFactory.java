/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.extend;

import java.io.Reader;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

public interface StylesheetFactory {
    public Stylesheet parse(Reader var1, StylesheetInfo var2);

    public Ruleset parseStyleDeclaration(int var1, String var2);

    public Stylesheet getStylesheet(StylesheetInfo var1);
}

