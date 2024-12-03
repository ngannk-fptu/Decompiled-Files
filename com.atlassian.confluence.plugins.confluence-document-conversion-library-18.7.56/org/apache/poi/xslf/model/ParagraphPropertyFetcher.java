/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.model;

import java.util.function.Consumer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

@Internal
public final class ParagraphPropertyFetcher<T>
extends PropertyFetcher<T> {
    static final String PML_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    static final String DML_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private static final QName[] TX_BODY = new QName[]{new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "txBody")};
    private static final QName[] LST_STYLE = new QName[]{new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lstStyle")};
    private final XSLFTextParagraph para;
    int _level;
    private final ParaPropFetcher<T> fetcher;

    public ParagraphPropertyFetcher(XSLFTextParagraph para, ParaPropFetcher<T> fetcher) {
        this.para = para;
        this._level = para.getIndentLevel();
        this.fetcher = fetcher;
    }

    @Override
    public boolean fetch(XSLFShape shape) {
        try {
            this.fetchProp(ParagraphPropertyFetcher.select(shape, this._level));
        }
        catch (XmlException xmlException) {
            // empty catch block
        }
        return this.isSet();
    }

    public T fetchProperty(XSLFShape shape) {
        XSLFSheet sheet = shape.getSheet();
        this.fetchParagraphProp();
        if (!(sheet instanceof XSLFSlideMaster)) {
            this.fetchShapeProp(shape);
            this.fetchThemeProp(shape);
        }
        this.fetchMasterProp();
        return this.isSet() ? (T)this.getValue() : null;
    }

    private void fetchParagraphProp() {
        this.fetchProp(this.para.getXmlObject().getPPr());
    }

    private void fetchShapeProp(XSLFShape shape) {
        if (!this.isSet()) {
            shape.fetchShapeProperty(this);
        }
    }

    private void fetchThemeProp(XSLFShape shape) {
        if (!this.isSet()) {
            this.fetchProp(ParagraphPropertyFetcher.getThemeProps(shape, this._level));
        }
    }

    private void fetchMasterProp() {
        if (!this.isSet()) {
            this.fetchProp(this.para.getDefaultMasterStyle());
        }
    }

    private void fetchProp(CTTextParagraphProperties props) {
        if (props != null) {
            this.fetcher.fetch(props, this::setValue);
        }
    }

    static CTTextParagraphProperties select(XSLFShape shape, int level) throws XmlException {
        QName[] lvlProp = new QName[]{new QName(DML_NS, "lvl" + (level + 1) + "pPr")};
        return XPathHelper.selectProperty(shape.getXmlObject(), CTTextParagraphProperties.class, ParagraphPropertyFetcher::parse, TX_BODY, LST_STYLE, lvlProp);
    }

    static CTTextParagraphProperties parse(XMLStreamReader reader) throws XmlException {
        CTTextParagraph para = (CTTextParagraph)CTTextParagraph.Factory.parse(reader);
        return para != null && para.isSetPPr() ? para.getPPr() : null;
    }

    static CTTextParagraphProperties getThemeProps(XSLFShape shape, int _level) {
        if (shape.isPlaceholder()) {
            return null;
        }
        XMLSlideShow ppt = shape.getSheet().getSlideShow();
        CTTextListStyle dts = ppt.getCTPresentation().getDefaultTextStyle();
        if (dts == null) {
            return null;
        }
        switch (_level) {
            case 0: {
                return dts.getLvl1PPr();
            }
            case 1: {
                return dts.getLvl2PPr();
            }
            case 2: {
                return dts.getLvl3PPr();
            }
            case 3: {
                return dts.getLvl4PPr();
            }
            case 4: {
                return dts.getLvl5PPr();
            }
            case 5: {
                return dts.getLvl6PPr();
            }
            case 6: {
                return dts.getLvl7PPr();
            }
            case 7: {
                return dts.getLvl8PPr();
            }
            case 8: {
                return dts.getLvl9PPr();
            }
        }
        return null;
    }

    public static interface ParaPropFetcher<S> {
        public void fetch(CTTextParagraphProperties var1, Consumer<S> var2);
    }
}

