/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor;

import java.util.ArrayList;
import java.util.List;
import org.outerj.daisy.diff.html.modification.HtmlLayoutChange;

public class AncestorComparatorResult {
    private boolean changed = false;
    private String changes = null;
    private List<HtmlLayoutChange> htmlLayoutChanges = new ArrayList<HtmlLayoutChange>();

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getChanges() {
        return this.changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public List<HtmlLayoutChange> getHtmlLayoutChanges() {
        return this.htmlLayoutChanges;
    }

    public void setHtmlLayoutChanges(List<HtmlLayoutChange> htmlLayoutChanges) {
        this.htmlLayoutChanges = htmlLayoutChanges;
    }
}

