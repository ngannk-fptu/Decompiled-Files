/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom;

import com.atlassian.plugins.conversion.confluence.dom.Hyperlink;
import com.atlassian.plugins.conversion.confluence.dom.MacroInfo;
import com.atlassian.plugins.conversion.confluence.dom.TextFormat;
import java.util.Hashtable;

public interface IWikiDocument {
    public void startDocument() throws Exception;

    public void startParagraph() throws Exception;

    public void startNumberedParagraph(int var1) throws Exception;

    public void startBulletedParagraph(int var1) throws Exception;

    public void endHardParagraph() throws Exception;

    public void endSoftParagraph() throws Exception;

    public void startTable() throws Exception;

    public void startTableCell(boolean var1) throws Exception;

    public void endTableCell() throws Exception;

    public void startTableRow() throws Exception;

    public void endTableRow() throws Exception;

    public void endTable() throws Exception;

    public void setHeading(String var1) throws Exception;

    public void setHyperlink(Hyperlink var1, TextFormat var2, String var3) throws Exception;

    public void setBlockQuote() throws Exception;

    public void html(String var1) throws Exception;

    public void addImage(String var1, String var2, String var3, Hashtable var4, String var5) throws Exception;

    public MacroInfo getMacroInfo(String var1);

    public MacroInfo macroStart(String var1, String var2, String var3, Hashtable var4, TextFormat var5, boolean var6) throws Exception;

    public boolean macroEnd(MacroInfo var1, String var2, String var3, String var4, Hashtable var5, TextFormat var6) throws Exception;

    public void addText(String var1, TextFormat var2) throws Exception;

    public void horizontalRule() throws Exception;

    public void endDocument() throws Exception;
}

