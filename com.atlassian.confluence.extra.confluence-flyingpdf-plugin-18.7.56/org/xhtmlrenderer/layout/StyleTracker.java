/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.style.CalculatedStyle;

public class StyleTracker {
    private List _styles = new ArrayList();

    public void addStyle(CascadedStyle style) {
        this._styles.add(style);
    }

    public void removeLast() {
        if (this._styles.size() != 0) {
            this._styles.remove(this._styles.size() - 1);
        }
    }

    public boolean hasStyles() {
        return this._styles.size() != 0;
    }

    public void clearStyles() {
        this._styles.clear();
    }

    public CalculatedStyle deriveAll(CalculatedStyle start) {
        CalculatedStyle result = start;
        Iterator i = this.getStyles().iterator();
        while (i.hasNext()) {
            result = result.deriveStyle((CascadedStyle)i.next());
        }
        return result;
    }

    public List getStyles() {
        return this._styles;
    }

    public StyleTracker copyOf() {
        StyleTracker result = new StyleTracker();
        result._styles.addAll(this._styles);
        return result;
    }
}

