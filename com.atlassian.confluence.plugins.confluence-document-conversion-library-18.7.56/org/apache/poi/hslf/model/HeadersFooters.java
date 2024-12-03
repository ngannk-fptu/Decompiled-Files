/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model;

import org.apache.poi.hslf.record.CString;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.HeadersFootersContainer;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.SheetContainer;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.sl.usermodel.Placeholder;

public final class HeadersFooters {
    private static final String _ppt2007tag = "___PPT12";
    private final HeadersFootersContainer _container;
    private final HSLFSheet _sheet;
    private final boolean _ppt2007;

    public HeadersFooters(HSLFSlideShow ppt, short headerFooterType) {
        this(ppt.getSlideMasters().get(0), headerFooterType);
    }

    public HeadersFooters(HSLFSheet sheet, short headerFooterType) {
        this._sheet = sheet;
        HSLFSlideShow ppt = this._sheet.getSlideShow();
        Document doc = ppt.getDocumentRecord();
        String tag = ppt.getSlideMasters().get(0).getProgrammableTag();
        this._ppt2007 = _ppt2007tag.equals(tag);
        SheetContainer sc = this._sheet.getSheetContainer();
        HeadersFootersContainer hdd = (HeadersFootersContainer)sc.findFirstOfType(RecordTypes.HeadersFooters.typeID);
        if (hdd == null) {
            for (Record ch : doc.getChildRecords()) {
                if (!(ch instanceof HeadersFootersContainer) || ((HeadersFootersContainer)ch).getOptions() != headerFooterType) continue;
                hdd = (HeadersFootersContainer)ch;
                break;
            }
        }
        if (hdd == null) {
            hdd = new HeadersFootersContainer(headerFooterType);
            Record lst = doc.findFirstOfType(RecordTypes.List.typeID);
            doc.addChildAfter(hdd, lst);
        }
        this._container = hdd;
    }

    public String getHeaderText() {
        CString cs = this._container == null ? null : this._container.getHeaderAtom();
        return this.getPlaceholderText(Placeholder.HEADER, cs);
    }

    public void setHeaderText(String text) {
        this.setHeaderVisible(true);
        CString cs = this._container.getHeaderAtom();
        if (cs == null) {
            cs = this._container.addHeaderAtom();
        }
        cs.setText(text);
    }

    public String getFooterText() {
        CString cs = this._container == null ? null : this._container.getFooterAtom();
        return this.getPlaceholderText(Placeholder.FOOTER, cs);
    }

    public void setFootersText(String text) {
        this.setFooterVisible(true);
        CString cs = this._container.getFooterAtom();
        if (cs == null) {
            cs = this._container.addFooterAtom();
        }
        cs.setText(text);
    }

    public String getDateTimeText() {
        CString cs = this._container == null ? null : this._container.getUserDateAtom();
        return this.getPlaceholderText(Placeholder.DATETIME, cs);
    }

    public void setDateTimeText(String text) {
        this.setUserDateVisible(true);
        this.setDateTimeVisible(true);
        CString cs = this._container.getUserDateAtom();
        if (cs == null) {
            cs = this._container.addUserDateAtom();
        }
        cs.setText(text);
    }

    public boolean isFooterVisible() {
        return this.isVisible(32, Placeholder.FOOTER);
    }

    public void setFooterVisible(boolean flag) {
        this.setFlag(32, flag);
    }

    public boolean isHeaderVisible() {
        return this.isVisible(16, Placeholder.HEADER);
    }

    public void setHeaderVisible(boolean flag) {
        this.setFlag(16, flag);
    }

    public boolean isDateTimeVisible() {
        return this.isVisible(1, Placeholder.DATETIME);
    }

    public void setDateTimeVisible(boolean flag) {
        this.setFlag(1, flag);
    }

    public boolean isUserDateVisible() {
        return this.isVisible(4, Placeholder.DATETIME);
    }

    public CString getHeaderAtom() {
        return this._container.getHeaderAtom();
    }

    public CString getFooterAtom() {
        return this._container.getFooterAtom();
    }

    public CString getUserDateAtom() {
        return this._container.getUserDateAtom();
    }

    public void setUserDateVisible(boolean flag) {
        this.setFlag(4, flag);
    }

    public boolean isTodayDateVisible() {
        return this.isVisible(2, Placeholder.DATETIME);
    }

    public void setTodayDateVisible(boolean flag) {
        this.setFlag(2, flag);
    }

    public boolean isSlideNumberVisible() {
        return this.isVisible(8, Placeholder.SLIDE_NUMBER);
    }

    public void setSlideNumberVisible(boolean flag) {
        this.setFlag(8, flag);
    }

    public int getDateTimeFormat() {
        return this._container.getHeadersFootersAtom().getFormatId();
    }

    public void setDateTimeFormat(int formatId) {
        this._container.getHeadersFootersAtom().setFormatId(formatId);
    }

    private boolean isVisible(int flag, Placeholder placeholderId) {
        HSLFSimpleShape ss;
        boolean visible = this._ppt2007 ? (ss = this._sheet.getPlaceholder(placeholderId)) instanceof HSLFTextShape && ((HSLFTextShape)ss).getText() != null : this._container.getHeadersFootersAtom().getFlag(flag);
        return visible;
    }

    private String getPlaceholderText(Placeholder ph, CString cs) {
        String text;
        if (this._ppt2007) {
            HSLFSimpleShape ss = this._sheet.getPlaceholder(ph);
            String string = text = ss instanceof HSLFTextShape ? ((HSLFTextShape)ss).getText() : null;
            if ("*".equals(text)) {
                text = null;
            }
        } else {
            text = cs == null ? null : cs.getText();
        }
        return text;
    }

    private void setFlag(int type, boolean flag) {
        this._container.getHeadersFootersAtom().setFlag(type, flag);
    }

    public boolean isPpt2007() {
        return this._ppt2007;
    }

    public HeadersFootersContainer getContainer() {
        return this._container;
    }
}

