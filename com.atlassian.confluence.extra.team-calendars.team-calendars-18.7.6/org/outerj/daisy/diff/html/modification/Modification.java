/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.modification;

import java.util.List;
import org.outerj.daisy.diff.html.modification.HtmlLayoutChange;
import org.outerj.daisy.diff.html.modification.ModificationType;

public class Modification
implements Cloneable {
    private ModificationType type;
    private long id = -1L;
    private Modification prevMod = null;
    private Modification nextMod = null;
    private boolean firstOfID = false;
    private List<HtmlLayoutChange> htmlLayoutChanges = null;
    private String changes;

    public Modification(ModificationType type) {
        this.type = type;
    }

    public Modification clone() {
        Modification newM = new Modification(this.getType());
        newM.setID(this.getID());
        newM.setChanges(this.getChanges());
        newM.setHtmlLayoutChanges(this.getHtmlLayoutChanges());
        newM.setFirstOfID(this.isFirstOfID());
        newM.setNext(this.getNext());
        newM.setPrevious(this.getPrevious());
        return newM;
    }

    public ModificationType getType() {
        return this.type;
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getID() {
        return this.id;
    }

    public void setPrevious(Modification m) {
        this.prevMod = m;
    }

    public Modification getPrevious() {
        return this.prevMod;
    }

    public void setNext(Modification m) {
        this.nextMod = m;
    }

    public Modification getNext() {
        return this.nextMod;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getChanges() {
        return this.changes;
    }

    public boolean isFirstOfID() {
        return this.firstOfID;
    }

    public void setFirstOfID(boolean firstOfID) {
        this.firstOfID = firstOfID;
    }

    public List<HtmlLayoutChange> getHtmlLayoutChanges() {
        return this.htmlLayoutChanges;
    }

    public void setHtmlLayoutChanges(List<HtmlLayoutChange> htmlLayoutChanges) {
        this.htmlLayoutChanges = htmlLayoutChanges;
    }
}

