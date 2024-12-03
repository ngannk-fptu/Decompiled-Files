/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.usermodel.HSLFSheet;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.PlaceholderDetails;

public class HSLFPlaceholderDetails
implements PlaceholderDetails {
    private final HSLFSheet sheet;
    private final Placeholder placeholder;

    HSLFPlaceholderDetails(HSLFSheet sheet, Placeholder placeholder) {
        this.sheet = sheet;
        this.placeholder = placeholder;
    }

    @Override
    public boolean isVisible() {
        Placeholder ph = this.getPlaceholder();
        if (ph == null) {
            return false;
        }
        HeadersFooters headersFooters = this.sheet.getHeadersFooters();
        switch (ph) {
            case HEADER: 
            case TITLE: {
                return headersFooters.isHeaderVisible();
            }
            case FOOTER: {
                return headersFooters.isFooterVisible();
            }
            case DATETIME: {
                return headersFooters.isDateTimeVisible();
            }
            case SLIDE_NUMBER: {
                return headersFooters.isSlideNumberVisible();
            }
        }
        return false;
    }

    @Override
    public void setVisible(boolean isVisible) {
        Placeholder ph = this.getPlaceholder();
        if (ph == null) {
            return;
        }
        HeadersFooters headersFooters = this.sheet.getHeadersFooters();
        switch (ph) {
            case HEADER: 
            case TITLE: {
                headersFooters.setHeaderVisible(isVisible);
                break;
            }
            case FOOTER: {
                headersFooters.setFooterVisible(isVisible);
                break;
            }
            case DATETIME: {
                headersFooters.setDateTimeVisible(isVisible);
                break;
            }
            case SLIDE_NUMBER: {
                headersFooters.setSlideNumberVisible(isVisible);
            }
        }
    }

    @Override
    public Placeholder getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        throw new UnsupportedOperationException("Only sub class(es) of HSLFPlaceholderDetails allow setting the placeholder");
    }

    @Override
    public PlaceholderDetails.PlaceholderSize getSize() {
        return PlaceholderDetails.PlaceholderSize.full;
    }

    @Override
    public void setSize(PlaceholderDetails.PlaceholderSize size) {
        throw new UnsupportedOperationException("Only sub class(es) of HSLFPlaceholderDetails allow setting the size");
    }

    @Override
    public String getText() {
        Placeholder ph = this.getPlaceholder();
        if (ph == null) {
            return null;
        }
        HeadersFooters headersFooters = this.sheet.getHeadersFooters();
        switch (ph) {
            case HEADER: 
            case TITLE: {
                return headersFooters.getHeaderText();
            }
            case FOOTER: {
                return headersFooters.getFooterText();
            }
            case DATETIME: {
                return headersFooters.getDateTimeText();
            }
        }
        return null;
    }

    @Override
    public void setText(String text) {
        Placeholder ph = this.getPlaceholder();
        if (ph == null) {
            return;
        }
        HeadersFooters headersFooters = this.sheet.getHeadersFooters();
        switch (ph) {
            case HEADER: 
            case TITLE: {
                headersFooters.setHeaderText(text);
                break;
            }
            case FOOTER: {
                headersFooters.setFootersText(text);
                break;
            }
            case DATETIME: {
                headersFooters.setDateTimeText(text);
                break;
            }
        }
    }
}

