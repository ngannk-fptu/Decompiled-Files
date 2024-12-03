/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.LinkedList;
import java.util.List;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.layout.BoxRangeData;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.XRRuntimeException;

public class BoxRangeHelper {
    private LinkedList _clipRegionStack = new LinkedList();
    private OutputDevice _outputDevice;
    private List _rangeList;
    private int _rangeIndex = 0;
    private BoxRangeData _current = null;

    public BoxRangeHelper(OutputDevice outputDevice, List rangeList) {
        this._outputDevice = outputDevice;
        this._rangeList = rangeList;
        if (rangeList.size() > 0) {
            this._current = (BoxRangeData)rangeList.get(0);
        }
    }

    public void checkFinished() {
        if (this._clipRegionStack.size() != 0) {
            throw new XRRuntimeException("internal error");
        }
    }

    public void pushClipRegion(RenderingContext c, int contentIndex) {
        while (this._current != null && this._current.getRange().getStart() == contentIndex) {
            this._current.setClip(this._outputDevice.getClip());
            this._clipRegionStack.add(this._current);
            this._outputDevice.clip(this._current.getBox().getChildrenClipEdge(c));
            if (this._rangeIndex == this._rangeList.size() - 1) {
                this._current = null;
                continue;
            }
            this._current = (BoxRangeData)this._rangeList.get(++this._rangeIndex);
        }
    }

    public void popClipRegions(RenderingContext c, int contentIndex) {
        BoxRangeData data;
        while (this._clipRegionStack.size() > 0 && (data = (BoxRangeData)this._clipRegionStack.getLast()).getRange().getEnd() == contentIndex) {
            this._outputDevice.setClip(data.getClip());
            this._clipRegionStack.removeLast();
        }
    }
}

