/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Paragraph
 *  com.aspose.words.ParagraphFormat
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter;

import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphFormat;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.Doc2Wiki;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.SplitImportContext;

public class DocSplitter
extends Doc2Wiki<SplitImportContext> {
    public DocSplitter(SplitImportContext importContext, boolean doFootnotes) {
        super(importContext, doFootnotes);
    }

    @Override
    protected int handleParagraphStyle(Paragraph paragraph, ParagraphFormat format) throws Exception {
        int styleID = format.getStyleIdentifier();
        int headingLvl = 0;
        String text = paragraph.getText().trim();
        if (text.length() == 0) {
            return 0;
        }
        switch (styleID) {
            case 1: {
                headingLvl = 1;
                break;
            }
            case 2: {
                headingLvl = 2;
                break;
            }
            case 3: {
                headingLvl = 3;
                break;
            }
            case 4: {
                headingLvl = 4;
                break;
            }
            case 5: {
                headingLvl = 5;
                break;
            }
            case 6: {
                headingLvl = 6;
                break;
            }
            case 7: {
                headingLvl = 7;
                break;
            }
            case 8: {
                headingLvl = 8;
                break;
            }
            case 9: {
                headingLvl = 9;
                break;
            }
            case 4094: {
                String name = format.getStyleName();
                if (!name.equalsIgnoreCase("blockquote")) break;
                this._out.append("bq.");
            }
        }
        if (headingLvl > 0) {
            int adjustedLvl;
            if (((SplitImportContext)this._importContext).getSplitLevel() > 0) {
                if (((SplitImportContext)this._importContext).splitPage(this._out, this)) {
                    this._out = new StringBuilder();
                    return 1;
                }
                adjustedLvl = ((SplitImportContext)this._importContext).getNodeLevel() - ((SplitImportContext)this._importContext).getSplitLevel();
            } else {
                adjustedLvl = headingLvl;
            }
            if (adjustedLvl > 0) {
                this._out.append("h").append(adjustedLvl).append(".");
                paragraph.getParagraphFormat().setStyleIdentifier(0);
                return 0;
            }
        }
        return 0;
    }
}

