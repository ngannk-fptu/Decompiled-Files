/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.extractor;

import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;

public class XSLFExtractor
extends SlideShowExtractor<XSLFShape, XSLFTextParagraph>
implements POIXMLTextExtractor {
    public XSLFExtractor(XMLSlideShow slideshow) {
        super(slideshow);
    }

    @Override
    public XMLSlideShow getDocument() {
        return (XMLSlideShow)this.slideshow;
    }

    @Override
    public POIXMLPropertiesTextExtractor getMetadataTextExtractor() {
        return POIXMLTextExtractor.super.getMetadataTextExtractor();
    }
}

