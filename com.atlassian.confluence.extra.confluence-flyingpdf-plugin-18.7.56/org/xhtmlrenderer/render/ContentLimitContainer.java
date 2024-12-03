/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.ContentLimit;
import org.xhtmlrenderer.render.PageBox;

public class ContentLimitContainer {
    private ContentLimitContainer _parent;
    private int _initialPageNo;
    private List _contentLimits = new ArrayList();
    private PageBox _lastPage;

    public ContentLimitContainer(LayoutContext c, int startAbsY) {
        this._initialPageNo = this.getPage(c, startAbsY).getPageNo();
    }

    public int getInitialPageNo() {
        return this._initialPageNo;
    }

    public int getLastPageNo() {
        return this._initialPageNo + this._contentLimits.size() - 1;
    }

    public ContentLimit getContentLimit(int pageNo) {
        return this.getContentLimit(pageNo, false);
    }

    private ContentLimit getContentLimit(int pageNo, boolean addAsNeeded) {
        int target;
        if (addAsNeeded) {
            while (this._contentLimits.size() < pageNo - this._initialPageNo + 1) {
                this._contentLimits.add(new ContentLimit());
            }
        }
        if ((target = pageNo - this._initialPageNo) >= 0 && target < this._contentLimits.size()) {
            return (ContentLimit)this._contentLimits.get(pageNo - this._initialPageNo);
        }
        return null;
    }

    public void updateTop(LayoutContext c, int absY) {
        PageBox page = this.getPage(c, absY);
        this.getContentLimit(page.getPageNo(), true).updateTop(absY);
        ContentLimitContainer parent = this.getParent();
        if (parent != null) {
            parent.updateTop(c, absY);
        }
    }

    public void updateBottom(LayoutContext c, int absY) {
        PageBox page = this.getPage(c, absY);
        this.getContentLimit(page.getPageNo(), true).updateBottom(absY);
        ContentLimitContainer parent = this.getParent();
        if (parent != null) {
            parent.updateBottom(c, absY);
        }
    }

    public PageBox getPage(LayoutContext c, int absY) {
        PageBox page;
        PageBox last = this.getLastPage();
        if (last != null && absY >= last.getTop() && absY < last.getBottom()) {
            page = last;
        } else {
            page = c.getRootLayer().getPage(c, absY);
            this.setLastPage(page);
        }
        return page;
    }

    private PageBox getLastPage() {
        ContentLimitContainer c = this;
        while (c.getParent() != null) {
            c = c.getParent();
        }
        return c._lastPage;
    }

    private void setLastPage(PageBox page) {
        ContentLimitContainer c = this;
        while (c.getParent() != null) {
            c = c.getParent();
        }
        c._lastPage = page;
    }

    public ContentLimitContainer getParent() {
        return this._parent;
    }

    public void setParent(ContentLimitContainer parent) {
        this._parent = parent;
    }

    public boolean isContainsMultiplePages() {
        return this._contentLimits.size() > 1;
    }

    public String toString() {
        return "[initialPageNo=" + this._initialPageNo + ", limits=" + this._contentLimits + "]";
    }
}

