/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.usermodel.HSLFMasterSheet;
import org.apache.poi.hslf.usermodel.HSLFPlaceholderDetails;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.sl.usermodel.Placeholder;

public final class HSLFNotes
extends HSLFSheet
implements Notes<HSLFShape, HSLFTextParagraph> {
    private static final Logger LOG = LogManager.getLogger(HSLFNotes.class);
    private final List<List<HSLFTextParagraph>> _paragraphs = new ArrayList<List<HSLFTextParagraph>>();

    public HSLFNotes(org.apache.poi.hslf.record.Notes notes) {
        super(notes, notes.getNotesAtom().getSlideID());
        for (List<HSLFTextParagraph> l : HSLFTextParagraph.findTextParagraphs(this.getPPDrawing(), (HSLFSheet)this)) {
            if (this._paragraphs.contains(l)) continue;
            this._paragraphs.add(l);
        }
        if (this._paragraphs.isEmpty()) {
            LOG.atWarn().log("No text records found for notes sheet");
        }
    }

    @Override
    public List<List<HSLFTextParagraph>> getTextParagraphs() {
        return this._paragraphs;
    }

    @Override
    public HSLFMasterSheet getMasterSheet() {
        return null;
    }

    @Override
    public HeadersFooters getHeadersFooters() {
        return new HeadersFooters(this, 79);
    }

    @Override
    public HSLFPlaceholderDetails getPlaceholderDetails(Placeholder placeholder) {
        if (placeholder == null) {
            return null;
        }
        if (placeholder == Placeholder.HEADER || placeholder == Placeholder.FOOTER) {
            return new HSLFPlaceholderDetails(this, placeholder);
        }
        return super.getPlaceholderDetails(placeholder);
    }
}

