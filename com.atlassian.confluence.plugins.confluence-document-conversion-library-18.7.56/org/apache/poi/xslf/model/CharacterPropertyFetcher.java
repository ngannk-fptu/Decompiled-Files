/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.model;

import java.util.function.Consumer;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.model.ParagraphPropertyFetcher;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;

@Internal
public final class CharacterPropertyFetcher<T>
extends PropertyFetcher<T> {
    private final XSLFTextRun run;
    int _level;
    private final CharPropFetcher<T> fetcher;

    public CharacterPropertyFetcher(XSLFTextRun run, CharPropFetcher<T> fetcher) {
        this._level = run.getParagraph().getIndentLevel();
        this.fetcher = fetcher;
        this.run = run;
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
        this.fetchRunProp();
        if (!(sheet instanceof XSLFSlideMaster)) {
            this.fetchParagraphDefaultRunProp();
            this.fetchShapeProp(shape);
            this.fetchThemeProp(shape);
        }
        this.fetchMasterProp();
        return this.isSet() ? (T)this.getValue() : null;
    }

    private void fetchRunProp() {
        this.fetchProp(this.run.getRPr(false));
    }

    private void fetchParagraphDefaultRunProp() {
        CTTextParagraphProperties pr;
        if (!this.isSet() && (pr = this.run.getParagraph().getXmlObject().getPPr()) != null) {
            this.fetchProp(pr.getDefRPr());
        }
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
            this.fetchProp(this.run.getParagraph().getDefaultMasterStyle());
        }
    }

    private void fetchProp(CTTextParagraphProperties props) {
        if (props != null) {
            this.fetchProp(props.getDefRPr());
        }
    }

    private void fetchProp(CTTextCharacterProperties props) {
        if (props != null) {
            this.fetcher.fetch(props, this::setValue);
        }
    }

    public static interface CharPropFetcher<S> {
        public void fetch(CTTextCharacterProperties var1, Consumer<S> var2);
    }
}

