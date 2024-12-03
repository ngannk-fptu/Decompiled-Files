/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.RulesetContainer;

public class MediaRule
implements RulesetContainer {
    private List _mediaTypes = new ArrayList();
    private List _contents = new ArrayList();
    private int _origin;

    public MediaRule(int origin) {
        this._origin = origin;
    }

    public void addMedium(String medium) {
        this._mediaTypes.add(medium);
    }

    public boolean matches(String medium) {
        if (medium.equalsIgnoreCase("all") || this._mediaTypes.contains("all")) {
            return true;
        }
        return this._mediaTypes.contains(medium.toLowerCase());
    }

    @Override
    public void addContent(Ruleset ruleset) {
        this._contents.add(ruleset);
    }

    public List getContents() {
        return this._contents;
    }

    @Override
    public int getOrigin() {
        return this._origin;
    }
}

