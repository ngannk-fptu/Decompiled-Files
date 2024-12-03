/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.newmatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.constants.MarginBoxName;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class PageInfo {
    private final List _properties;
    private final CascadedStyle _pageStyle;
    private final Map _marginBoxes;
    private final List _xmpPropertyList;

    public PageInfo(List properties, CascadedStyle pageStyle, Map marginBoxes) {
        this._properties = properties;
        this._pageStyle = pageStyle;
        this._marginBoxes = marginBoxes;
        this._xmpPropertyList = (List)marginBoxes.remove(MarginBoxName.FS_PDF_XMP_METADATA);
    }

    public Map getMarginBoxes() {
        return this._marginBoxes;
    }

    public CascadedStyle getPageStyle() {
        return this._pageStyle;
    }

    public List getProperties() {
        return this._properties;
    }

    public CascadedStyle createMarginBoxStyle(MarginBoxName marginBox, boolean alwaysCreate) {
        ArrayList<PropertyDeclaration> all;
        List marginProps = (List)this._marginBoxes.get(marginBox);
        if (!(marginProps != null && marginProps.size() != 0 || alwaysCreate)) {
            return null;
        }
        if (marginProps != null) {
            all = new ArrayList(marginProps.size() + 3);
            all.addAll(marginProps);
        } else {
            all = new ArrayList<PropertyDeclaration>(3);
        }
        all.add(CascadedStyle.createLayoutPropertyDeclaration(CSSName.DISPLAY, IdentValue.TABLE_CELL));
        all.add(new PropertyDeclaration(CSSName.VERTICAL_ALIGN, new PropertyValue(marginBox.getInitialVerticalAlign()), false, 0));
        all.add(new PropertyDeclaration(CSSName.TEXT_ALIGN, new PropertyValue(marginBox.getInitialTextAlign()), false, 0));
        return new CascadedStyle(all.iterator());
    }

    public boolean hasAny(MarginBoxName[] marginBoxes) {
        for (int i = 0; i < marginBoxes.length; ++i) {
            if (!this._marginBoxes.containsKey(marginBoxes[i])) continue;
            return true;
        }
        return false;
    }

    public List getXMPPropertyList() {
        return this._xmpPropertyList;
    }
}

