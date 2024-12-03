/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.sheet.FontFaceRule;
import org.xhtmlrenderer.css.sheet.MediaRule;
import org.xhtmlrenderer.css.sheet.PageRule;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.RulesetContainer;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

public class Stylesheet
implements RulesetContainer {
    private String _uri;
    private int _origin;
    private List _fontFaceRules = new ArrayList();
    private List _importRules = new ArrayList();
    private List _contents = new ArrayList();

    public Stylesheet(String uri, int origin) {
        this._uri = uri;
        this._origin = origin;
    }

    @Override
    public int getOrigin() {
        return this._origin;
    }

    public String getURI() {
        return this._uri;
    }

    @Override
    public void addContent(Ruleset ruleset) {
        this._contents.add(ruleset);
    }

    public void addContent(MediaRule rule) {
        this._contents.add(rule);
    }

    public void addContent(PageRule rule) {
        this._contents.add(rule);
    }

    public List getContents() {
        return this._contents;
    }

    public void addImportRule(StylesheetInfo info) {
        this._importRules.add(info);
    }

    public List getImportRules() {
        return this._importRules;
    }

    public void addFontFaceRule(FontFaceRule rule) {
        this._fontFaceRules.add(rule);
    }

    public List getFontFaceRules() {
        return this._fontFaceRules;
    }
}

