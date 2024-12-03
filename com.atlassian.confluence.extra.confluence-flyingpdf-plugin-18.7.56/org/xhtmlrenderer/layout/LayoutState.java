/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.LinkedList;
import org.xhtmlrenderer.layout.StyleTracker;
import org.xhtmlrenderer.render.MarkerData;

public class LayoutState {
    private StyleTracker _firstLines;
    private StyleTracker _firstLetters;
    private MarkerData _currentMarkerData;
    private LinkedList _BFCs;
    private String _pageName;
    private int _extraSpaceTop;
    private int _extraSpaceBottom;
    private int _noPageBreak;

    public LinkedList getBFCs() {
        return this._BFCs;
    }

    public void setBFCs(LinkedList s) {
        this._BFCs = s;
    }

    public MarkerData getCurrentMarkerData() {
        return this._currentMarkerData;
    }

    public void setCurrentMarkerData(MarkerData currentMarkerData) {
        this._currentMarkerData = currentMarkerData;
    }

    public StyleTracker getFirstLetters() {
        return this._firstLetters;
    }

    public void setFirstLetters(StyleTracker firstLetters) {
        this._firstLetters = firstLetters;
    }

    public StyleTracker getFirstLines() {
        return this._firstLines;
    }

    public void setFirstLines(StyleTracker firstLines) {
        this._firstLines = firstLines;
    }

    public String getPageName() {
        return this._pageName;
    }

    public void setPageName(String pageName) {
        this._pageName = pageName;
    }

    public int getExtraSpaceTop() {
        return this._extraSpaceTop;
    }

    public void setExtraSpaceTop(int extraSpaceTop) {
        this._extraSpaceTop = extraSpaceTop;
    }

    public int getExtraSpaceBottom() {
        return this._extraSpaceBottom;
    }

    public void setExtraSpaceBottom(int extraSpaceBottom) {
        this._extraSpaceBottom = extraSpaceBottom;
    }

    public int getNoPageBreak() {
        return this._noPageBreak;
    }

    public void setNoPageBreak(int noPageBreak) {
        this._noPageBreak = noPageBreak;
    }
}

