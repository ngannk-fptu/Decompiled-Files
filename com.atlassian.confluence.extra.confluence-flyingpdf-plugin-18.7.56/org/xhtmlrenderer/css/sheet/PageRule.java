/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xhtmlrenderer.css.constants.MarginBoxName;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.RulesetContainer;

public class PageRule
implements RulesetContainer {
    private String _name;
    private String _pseudoPage;
    private Ruleset _ruleset;
    private int _origin;
    private Map _marginBoxes = new HashMap();
    private int _pos;
    private int _specificityF;
    private int _specificityG;
    private int _specificityH;

    public PageRule(int origin) {
        this._origin = origin;
    }

    public String getPseudoPage() {
        return this._pseudoPage;
    }

    public void setPseudoPage(String pseudoPage) {
        this._pseudoPage = pseudoPage;
        if (pseudoPage.equals("first")) {
            this._specificityG = 1;
        } else {
            this._specificityH = 1;
        }
    }

    public Ruleset getRuleset() {
        return this._ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this._ruleset = ruleset;
    }

    @Override
    public void addContent(Ruleset ruleset) {
        if (this._ruleset != null) {
            throw new IllegalStateException("Ruleset has already been set");
        }
        this._ruleset = ruleset;
    }

    @Override
    public int getOrigin() {
        return this._origin;
    }

    public void setOrigin(int origin) {
        this._origin = origin;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
        this._specificityF = 1;
    }

    public List getMarginBoxProperties(MarginBoxName name) {
        return (List)this._marginBoxes.get(name);
    }

    public void addMarginBoxProperties(MarginBoxName name, List props) {
        this._marginBoxes.put(name, props);
    }

    public Map getMarginBoxes() {
        return this._marginBoxes;
    }

    public long getOrder() {
        long result = 0L;
        result |= (long)this._specificityF << 32;
        result |= (long)this._specificityG << 24;
        result |= (long)this._specificityH << 16;
        return result |= (long)this._pos;
    }

    public boolean applies(String pageName, String pseudoPage) {
        if (this._name == null && this._pseudoPage == null) {
            return true;
        }
        if (this._name == null && this._pseudoPage != null && (this._pseudoPage.equals(pseudoPage) || this._pseudoPage.equals("right") && pseudoPage != null && pseudoPage.equals("first"))) {
            return true;
        }
        if (this._name != null && this._name.equals(pageName) && this._pseudoPage == null) {
            return true;
        }
        return this._name != null && this._name.equals(pageName) && this._pseudoPage != null && this._pseudoPage.equals(pseudoPage);
    }

    public int getPos() {
        return this._pos;
    }

    public void setPos(int pos) {
        this._pos = pos;
    }
}

