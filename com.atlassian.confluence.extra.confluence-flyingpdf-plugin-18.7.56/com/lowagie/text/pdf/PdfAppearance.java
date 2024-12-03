/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DocumentFont;
import com.lowagie.text.pdf.FontDetails;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.util.HashMap;
import java.util.Map;

public class PdfAppearance
extends PdfTemplate {
    public static final Map<String, PdfName> stdFieldFontNames = new HashMap<String, PdfName>();

    PdfAppearance() {
        this.separator = 32;
    }

    PdfAppearance(PdfIndirectReference iref) {
        this.thisReference = iref;
    }

    PdfAppearance(PdfWriter wr) {
        super(wr);
        this.separator = 32;
    }

    public static PdfAppearance createAppearance(PdfWriter writer, float width, float height) {
        return PdfAppearance.createAppearance(writer, width, height, null);
    }

    private static PdfAppearance createAppearance(PdfWriter writer, float width, float height, PdfName forcedName) {
        PdfAppearance template = new PdfAppearance(writer);
        template.setWidth(width);
        template.setHeight(height);
        writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    @Override
    public void setFontAndSize(BaseFont bf, float size) {
        this.checkWriter();
        this.state.size = size;
        this.state.fontDetails = bf.getFontType() == 4 ? new FontDetails(null, ((DocumentFont)bf).getIndirectReference(), bf) : this.writer.addSimple(bf);
        PdfName psn = stdFieldFontNames.get(bf.getPostscriptFontName());
        if (psn == null) {
            if (bf.isSubset() && bf.getFontType() == 3) {
                psn = this.state.fontDetails.getFontName();
            } else {
                psn = new PdfName(bf.getPostscriptFontName());
                this.state.fontDetails.setSubset(false);
            }
        }
        PageResources prs = this.getPageResources();
        prs.addFont(psn, this.state.fontDetails.getIndirectReference());
        this.content.append(psn.getBytes()).append(' ').append(size).append(" Tf").append_i(this.separator);
    }

    @Override
    public PdfContentByte getDuplicate() {
        PdfAppearance tpl = new PdfAppearance();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.group = this.group;
        tpl.layer = this.layer;
        if (this.matrix != null) {
            tpl.matrix = new PdfArray(this.matrix);
        }
        tpl.separator = this.separator;
        return tpl;
    }

    static {
        stdFieldFontNames.put("Courier-BoldOblique", new PdfName("CoBO"));
        stdFieldFontNames.put("Courier-Bold", new PdfName("CoBo"));
        stdFieldFontNames.put("Courier-Oblique", new PdfName("CoOb"));
        stdFieldFontNames.put("Courier", new PdfName("Cour"));
        stdFieldFontNames.put("Helvetica-BoldOblique", new PdfName("HeBO"));
        stdFieldFontNames.put("Helvetica-Bold", new PdfName("HeBo"));
        stdFieldFontNames.put("Helvetica-Oblique", new PdfName("HeOb"));
        stdFieldFontNames.put("Helvetica", PdfName.HELV);
        stdFieldFontNames.put("Symbol", new PdfName("Symb"));
        stdFieldFontNames.put("Times-BoldItalic", new PdfName("TiBI"));
        stdFieldFontNames.put("Times-Bold", new PdfName("TiBo"));
        stdFieldFontNames.put("Times-Italic", new PdfName("TiIt"));
        stdFieldFontNames.put("Times-Roman", new PdfName("TiRo"));
        stdFieldFontNames.put("ZapfDingbats", PdfName.ZADB);
        stdFieldFontNames.put("HYSMyeongJo-Medium", new PdfName("HySm"));
        stdFieldFontNames.put("HYGoThic-Medium", new PdfName("HyGo"));
        stdFieldFontNames.put("HeiseiKakuGo-W5", new PdfName("KaGo"));
        stdFieldFontNames.put("HeiseiMin-W3", new PdfName("KaMi"));
        stdFieldFontNames.put("MHei-Medium", new PdfName("MHei"));
        stdFieldFontNames.put("MSung-Light", new PdfName("MSun"));
        stdFieldFontNames.put("STSong-Light", new PdfName("STSo"));
        stdFieldFontNames.put("MSungStd-Light", new PdfName("MSun"));
        stdFieldFontNames.put("STSongStd-Light", new PdfName("STSo"));
        stdFieldFontNames.put("HYSMyeongJoStd-Medium", new PdfName("HySm"));
        stdFieldFontNames.put("KozMinPro-Regular", new PdfName("KaMi"));
    }
}

