/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xpath.XPath;

public class WhiteSpaceInfo
extends ElemTemplate {
    static final long serialVersionUID = 6389208261999943836L;
    private boolean m_shouldStripSpace;

    public boolean getShouldStripSpace() {
        return this.m_shouldStripSpace;
    }

    public WhiteSpaceInfo(Stylesheet thisSheet) {
        this.setStylesheet(thisSheet);
    }

    public WhiteSpaceInfo(XPath matchPattern, boolean shouldStripSpace, Stylesheet thisSheet) {
        this.m_shouldStripSpace = shouldStripSpace;
        this.setMatch(matchPattern);
        this.setStylesheet(thisSheet);
    }

    @Override
    public void recompose(StylesheetRoot root) {
        root.recomposeWhiteSpaceInfo(this);
    }
}

