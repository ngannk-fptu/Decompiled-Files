/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import org.apache.poi.hwpf.converter.AbstractWordUtils;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.w3c.dom.Element;

public class WordToFoUtils
extends AbstractWordUtils {
    static void compactInlines(Element blockElement) {
        WordToFoUtils.compactChildNodesR(blockElement, "fo:inline");
    }

    public static void setBold(Element element, boolean bold) {
        element.setAttribute("font-weight", bold ? "bold" : "normal");
    }

    public static void setBorder(Element element, BorderCode borderCode, String where) {
        if (element == null) {
            throw new IllegalArgumentException("element is null");
        }
        if (borderCode == null || borderCode.isEmpty()) {
            return;
        }
        if (WordToFoUtils.isEmpty(where)) {
            element.setAttribute("border-style", WordToFoUtils.getBorderType(borderCode));
            element.setAttribute("border-color", WordToFoUtils.getColor(borderCode.getColor()));
            element.setAttribute("border-width", WordToFoUtils.getBorderWidth(borderCode));
        } else {
            element.setAttribute("border-" + where + "-style", WordToFoUtils.getBorderType(borderCode));
            element.setAttribute("border-" + where + "-color", WordToFoUtils.getColor(borderCode.getColor()));
            element.setAttribute("border-" + where + "-width", WordToFoUtils.getBorderWidth(borderCode));
        }
    }

    public static void setCharactersProperties(CharacterRun characterRun, Element inline) {
        StringBuilder textDecorations = new StringBuilder();
        WordToFoUtils.setBorder(inline, characterRun.getBorder(), "");
        if (characterRun.getIco24() != -1) {
            inline.setAttribute("color", WordToFoUtils.getColor24(characterRun.getIco24()));
        }
        if (characterRun.isCapitalized()) {
            inline.setAttribute("text-transform", "uppercase");
        }
        if (characterRun.isHighlighted()) {
            inline.setAttribute("background-color", WordToFoUtils.getColor(characterRun.getHighlightedColor()));
        }
        if (characterRun.isStrikeThrough()) {
            if (textDecorations.length() > 0) {
                textDecorations.append(" ");
            }
            textDecorations.append("line-through");
        }
        if (characterRun.isShadowed()) {
            inline.setAttribute("text-shadow", characterRun.getFontSize() / 24 + "pt");
        }
        if (characterRun.isSmallCaps()) {
            inline.setAttribute("font-variant", "small-caps");
        }
        if (characterRun.getSubSuperScriptIndex() == 1) {
            inline.setAttribute("baseline-shift", "super");
            inline.setAttribute("font-size", "smaller");
        }
        if (characterRun.getSubSuperScriptIndex() == 2) {
            inline.setAttribute("baseline-shift", "sub");
            inline.setAttribute("font-size", "smaller");
        }
        if (characterRun.getUnderlineCode() > 0) {
            if (textDecorations.length() > 0) {
                textDecorations.append(" ");
            }
            textDecorations.append("underline");
        }
        if (characterRun.isVanished()) {
            inline.setAttribute("visibility", "hidden");
        }
        if (textDecorations.length() > 0) {
            inline.setAttribute("text-decoration", textDecorations.toString());
        }
    }

    public static void setFontFamily(Element element, String fontFamily) {
        if (WordToFoUtils.isEmpty(fontFamily)) {
            return;
        }
        element.setAttribute("font-family", fontFamily);
    }

    public static void setFontSize(Element element, int fontSize) {
        element.setAttribute("font-size", String.valueOf(fontSize));
    }

    public static void setIndent(Paragraph paragraph, Element block) {
        if (paragraph.getFirstLineIndent() != 0) {
            block.setAttribute("text-indent", paragraph.getFirstLineIndent() / 20 + "pt");
        }
        if (paragraph.getIndentFromLeft() != 0) {
            block.setAttribute("start-indent", paragraph.getIndentFromLeft() / 20 + "pt");
        }
        if (paragraph.getIndentFromRight() != 0) {
            block.setAttribute("end-indent", paragraph.getIndentFromRight() / 20 + "pt");
        }
        if (paragraph.getSpacingBefore() != 0) {
            block.setAttribute("space-before", paragraph.getSpacingBefore() / 20 + "pt");
        }
        if (paragraph.getSpacingAfter() != 0) {
            block.setAttribute("space-after", paragraph.getSpacingAfter() / 20 + "pt");
        }
    }

    public static void setItalic(Element element, boolean italic) {
        element.setAttribute("font-style", italic ? "italic" : "normal");
    }

    public static void setJustification(Paragraph paragraph, Element element) {
        String justification = WordToFoUtils.getJustification(paragraph.getJustification());
        if (WordToFoUtils.isNotEmpty(justification)) {
            element.setAttribute("text-align", justification);
        }
    }

    public static void setLanguage(CharacterRun characterRun, Element inline) {
        String language;
        if (characterRun.getLanguageCode() != 0 && WordToFoUtils.isNotEmpty(language = WordToFoUtils.getLanguage(characterRun.getLanguageCode()))) {
            inline.setAttribute("language", language);
        }
    }

    public static void setParagraphProperties(Paragraph paragraph, Element block) {
        WordToFoUtils.setIndent(paragraph, block);
        WordToFoUtils.setJustification(paragraph, block);
        WordToFoUtils.setBorder(block, paragraph.getBottomBorder(), "bottom");
        WordToFoUtils.setBorder(block, paragraph.getLeftBorder(), "left");
        WordToFoUtils.setBorder(block, paragraph.getRightBorder(), "right");
        WordToFoUtils.setBorder(block, paragraph.getTopBorder(), "top");
        if (paragraph.pageBreakBefore()) {
            block.setAttribute("break-before", "page");
        }
        block.setAttribute("hyphenate", String.valueOf(paragraph.isAutoHyphenated()));
        if (paragraph.keepOnPage()) {
            block.setAttribute("keep-together.within-page", "always");
        }
        if (paragraph.keepWithNext()) {
            block.setAttribute("keep-with-next.within-page", "always");
        }
        block.setAttribute("linefeed-treatment", "preserve");
        block.setAttribute("white-space-collapse", "false");
    }

    public static void setPictureProperties(Picture picture, Element graphicElement) {
        int horizontalScale = picture.getHorizontalScalingFactor();
        int verticalScale = picture.getVerticalScalingFactor();
        if (horizontalScale > 0) {
            graphicElement.setAttribute("content-width", picture.getDxaGoal() * horizontalScale / 1000 / 20 + "pt");
        } else {
            graphicElement.setAttribute("content-width", picture.getDxaGoal() / 20 + "pt");
        }
        if (verticalScale > 0) {
            graphicElement.setAttribute("content-height", picture.getDyaGoal() * verticalScale / 1000 / 20 + "pt");
        } else {
            graphicElement.setAttribute("content-height", picture.getDyaGoal() / 20 + "pt");
        }
        if (horizontalScale <= 0 || verticalScale <= 0) {
            graphicElement.setAttribute("scaling", "uniform");
        } else {
            graphicElement.setAttribute("scaling", "non-uniform");
        }
        graphicElement.setAttribute("vertical-align", "text-bottom");
        if (picture.getDyaCropTop() != 0 || picture.getDxaCropRight() != 0 || picture.getDyaCropBottom() != 0 || picture.getDxaCropLeft() != 0) {
            int rectTop = picture.getDyaCropTop() / 20;
            int rectRight = picture.getDxaCropRight() / 20;
            int rectBottom = picture.getDyaCropBottom() / 20;
            int rectLeft = picture.getDxaCropLeft() / 20;
            graphicElement.setAttribute("clip", "rect(" + rectTop + "pt, " + rectRight + "pt, " + rectBottom + "pt, " + rectLeft + "pt)");
            graphicElement.setAttribute("overflow", "hidden");
        }
    }

    public static void setTableCellProperties(TableRow tableRow, TableCell tableCell, Element element, boolean toppest, boolean bottomest, boolean leftest, boolean rightest) {
        BorderCode left;
        BorderCode bottom;
        BorderCode top;
        element.setAttribute("width", (float)tableCell.getWidth() / 1440.0f + "in");
        element.setAttribute("padding-start", (float)tableRow.getGapHalf() / 1440.0f + "in");
        element.setAttribute("padding-end", (float)tableRow.getGapHalf() / 1440.0f + "in");
        BorderCode borderCode = tableCell.getBrcTop() != null && tableCell.getBrcTop().getBorderType() != 0 ? tableCell.getBrcTop() : (top = toppest ? tableRow.getTopBorder() : tableRow.getHorizontalBorder());
        BorderCode borderCode2 = tableCell.getBrcBottom() != null && tableCell.getBrcBottom().getBorderType() != 0 ? tableCell.getBrcBottom() : (bottom = bottomest ? tableRow.getBottomBorder() : tableRow.getHorizontalBorder());
        BorderCode borderCode3 = tableCell.getBrcLeft() != null && tableCell.getBrcLeft().getBorderType() != 0 ? tableCell.getBrcLeft() : (left = leftest ? tableRow.getLeftBorder() : tableRow.getVerticalBorder());
        BorderCode right = tableCell.getBrcRight() != null && tableCell.getBrcRight().getBorderType() != 0 ? tableCell.getBrcRight() : (rightest ? tableRow.getRightBorder() : tableRow.getVerticalBorder());
        WordToFoUtils.setBorder(element, bottom, "bottom");
        WordToFoUtils.setBorder(element, left, "left");
        WordToFoUtils.setBorder(element, right, "right");
        WordToFoUtils.setBorder(element, top, "top");
    }

    public static void setTableRowProperties(TableRow tableRow, Element tableRowElement) {
        if (tableRow.getRowHeight() > 0) {
            tableRowElement.setAttribute("height", (float)tableRow.getRowHeight() / 1440.0f + "in");
        }
        if (!tableRow.cantSplit()) {
            tableRowElement.setAttribute("keep-together.within-column", "always");
        }
    }
}

