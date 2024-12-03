/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.action;

import java.util.ArrayList;
import javax.swing.Action;

public class ActionConcentrator {
    private final ArrayList actions = new ArrayList();

    public void addAction(Action a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.actions.add(a);
    }

    public void removeAction(Action a) {
        if (a == null) {
            throw new NullPointerException();
        }
        this.actions.remove(a);
    }

    public void setEnabled(boolean b) {
        for (int i = 0; i < this.actions.size(); ++i) {
            Action a = (Action)this.actions.get(i);
            a.setEnabled(b);
        }
    }

    public boolean isEnabled() {
        for (int i = 0; i < this.actions.size(); ++i) {
            Action a = (Action)this.actions.get(i);
            if (!a.isEnabled()) continue;
            return true;
        }
        return false;
    }
}

