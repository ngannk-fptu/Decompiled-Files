/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.tabbedui;

import javax.swing.JComponent;
import org.jfree.ui.tabbedui.RootEditor;

public abstract class RootPanel
extends JComponent
implements RootEditor {
    private boolean active;

    public final boolean isActive() {
        return this.active;
    }

    protected void panelActivated() {
    }

    protected void panelDeactivated() {
    }

    public final void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        if (active) {
            this.panelActivated();
        } else {
            this.panelDeactivated();
        }
    }

    public JComponent getMainPanel() {
        return this;
    }

    public JComponent getToolbar() {
        return null;
    }
}

