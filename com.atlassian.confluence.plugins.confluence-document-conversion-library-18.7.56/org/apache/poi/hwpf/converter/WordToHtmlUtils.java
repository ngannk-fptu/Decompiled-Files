/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.w3c.dom.Element;

public class WordToHtmlUtils
extends AbstractWordUtils {
    public static void addBold(boolean bold, StringBuilder style) {
        style.append("font-weight:").append(bold ? "bold" : "normal").append(";");
    }

    public static void addBorder(BorderCode borderCode, String where, StringBuilder style) {
        if (borderCode == null || borderCode.isEmpty()) {
            return;
        }
        if (WordToHtmlUtils.isEmpty(where)) {
            style.append("border:");
        } else {
            style.append("border-");
            style.append(where);
        }
        style.append(":");
        if (borderCode.getLineWidth() < 8) {
            style.append("thin");
        } else {
            style.append(WordToHtmlUtils.getBorderWidth(borderCode));
        }
        style.append(' ');
        style.append(WordToHtmlUtils.getBorderType(borderCode));
        style.append(' ');
        style.append(WordToHtmlUtils.getColor(borderCode.getColor()));
        style.append(';');
    }

    public static void addCharactersProperties(CharacterRun characterRun, StringBuilder style) {
        WordToHtmlUtils.addBorder(characterRun.getBorder(), "", style);
        if (characterRun.isCapitalized()) {
            style.append("text-transform:uppercase;");
        }
        if (characterRun.getIco24() != -1) {
            style.append("color:").append(WordToHtmlUtils.getColor24(characterRun.getIco24())).append(";");
        }
        if (characterRun.isHighlighted()) {
            style.append("background-color:").append(WordToHtmlUtils.getColor(characterRun.getHighlightedColor())).append(";");
        }
        if (characterRun.isStrikeThrough()) {
            style.append("text-decoration:line-through;");
        }
        if (characterRun.isShadowed()) {
            style.append("text-shadow:").append(characterRun.getFontSize() / 24).append("pt;");
        }
        if (characterRun.isSmallCaps()) {
            style.append("font-variant:small-caps;");
        }
        if (characterRun.getSubSuperScriptIndex() == 1) {
            style.append("vertical-align:super;");
            style.append("font-size:smaller;");
        }
        if (characterRun.getSubSuperScriptIndex() == 2) {
            style.append("vertical-align:sub;");
            style.append("font-size:smaller;");
        }
        if (characterRun.getUnderlineCode() > 0) {
            style.append("text-decoration:underline;");
        }
        if (characterRun.isVanished()) {
            style.append("visibility:hidden;");
        }
    }

    public static void addFontFamily(String fontFamily, StringBuilder style) {
        if (WordToHtmlUtils.isEmpty(fontFamily)) {
            return;
        }
        style.append("font-family:").append(fontFamily).append(";");
    }

    public static void addFontSize(int fontSize, StringBuilder style) {
        style.append("font-size:").append(fontSize).append("pt;");
    }

    public static void addIndent(Paragraph paragraph, StringBuilder style) {
        WordToHtmlUtils.addIndent(style, "text-indent", paragraph.getFirstLineIndent());
        WordToHtmlUtils.addIndent(style, "margin-left", paragraph.getIndentFromLeft());
        WordToHtmlUtils.addIndent(style, "margin-right", paragraph.getIndentFromRight());
        WordToHtmlUtils.addIndent(style, "margin-top", paragraph.getSpacingBefore());
        WordToHtmlUtils.addIndent(style, "margin-bottom", paragraph.getSpacingAfter());
    }

    private static void addIndent(StringBuilder style, String cssName, int twipsValue) {
        if (twipsValue == 0) {
            return;
        }
        style.append(cssName).append(":").append((float)twipsValue / 1440.0f).append("in;");
    }

    public static void addJustification(Paragraph paragraph, StringBuilder style) {
        String justification = WordToHtmlUtils.getJustification(paragraph.getJustification());
        if (WordToHtmlUtils.isNotEmpty(justification)) {
            style.append("text-align:").append(justification).append(";");
        }
    }

    public static void addParagraphProperties(Paragraph paragraph, StringBuilder style) {
        WordToHtmlUtils.addIndent(paragraph, style);
        WordToHtmlUtils.addJustification(paragraph, style);
        WordToHtmlUtils.addBorder(paragraph.getBottomBorder(), "bottom", style);
        WordToHtmlUtils.addBorder(paragraph.getLeftBorder(), "left", style);
        WordToHtmlUtils.addBorder(paragraph.getRightBorder(), "right", style);
        WordToHtmlUtils.addBorder(paragraph.getTopBorder(), "top", style);
        if (paragraph.pageBreakBefore()) {
            style.append("break-before:page;");
        }
        style.append("hyphenate:").append(paragraph.isAutoHyphenated() ? "auto" : "none").append(";");
        if (paragraph.keepOnPage()) {
            style.append("keep-together.within-page:always;");
        }
        if (paragraph.keepWithNext()) {
            style.append("keep-with-next.within-page:always;");
        }
    }

    public static void addTableCellProperties(TableRow tableRow, TableCell tableCell, boolean toppest, boolean bottomest, boolean leftest, boolean rightest, StringBuilder style) {
        BorderCode left;
        BorderCode bottom;
        BorderCode top;
        style.append("width:").append((float)tableCell.getWidth() / 1440.0f).append("in;");
        style.append("padding-start:").append((float)tableRow.getGapHalf() / 1440.0f).append("in;");
        style.append("padding-end:").append((float)tableRow.getGapHalf() / 1440.0f).append("in;");
        BorderCode borderCode = tableCell.getBrcTop() != null && tableCell.getBrcTop().getBorderType() != 0 ? tableCell.getBrcTop() : (top = toppest ? tableRow.getTopBorder() : tableRow.getHorizontalBorder());
        BorderCode borderCode2 = tableCell.getBrcBottom() != null && tableCell.getBrcBottom().getBorderType() != 0 ? tableCell.getBrcBottom() : (bottom = bottomest ? tableRow.getBottomBorder() : tableRow.getHorizontalBorder());
        BorderCode borderCode3 = tableCell.getBrcLeft() != null && tableCell.getBrcLeft().getBorderType() != 0 ? tableCell.getBrcLeft() : (left = leftest ? tableRow.getLeftBorder() : tableRow.getVerticalBorder());
        BorderCode right = tableCell.getBrcRight() != null && tableCell.getBrcRight().getBorderType() != 0 ? tableCell.getBrcRight() : (rightest ? tableRow.getRightBorder() : tableRow.getVerticalBorder());
        WordToHtmlUtils.addBorder(bottom, "bottom", style);
        WordToHtmlUtils.addBorder(left, "left", style);
        WordToHtmlUtils.addBorder(right, "right", style);
        WordToHtmlUtils.addBorder(top, "top", style);
    }

    public static void addTableRowProperties(TableRow tableRow, StringBuilder style) {
        if (tableRow.getRowHeight() > 0) {
            style.append("height:").append((float)tableRow.getRowHeight() / 1440.0f).append("in;");
        }
        if (!tableRow.cantSplit()) {
            style.append("keep-together:always;");
        }
    }

    static void compactSpans(Element pElement) {
        WordToHtmlUtils.compactChildNodesR(pElement, "span");
    }
}

