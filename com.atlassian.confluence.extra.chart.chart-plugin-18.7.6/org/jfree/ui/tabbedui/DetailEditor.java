/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.tabbedui;

import javax.swing.JComponent;

public abstract class DetailEditor
extends JComponent {
    private Object object;
    private boolean confirmed;

    public void update() {
        if (this.object == null) {
            throw new IllegalStateException();
        }
        this.updateObject(this.object);
        this.setConfirmed(false);
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        this.object = object;
        this.setConfirmed(false);
        this.fillObject();
    }

    protected static int parseInt(String text, int def) {
        try {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException fe) {
            return def;
        }
    }

    public abstract void clear();

    protected abstract void fillObject();

    protected abstract void updateObject(Object var1);

    public boolean isConfirmed() {
        return this.confirmed;
    }

    protected void setConfirmed(boolean confirmed) {
        boolean oldConfirmed = this.confirmed;
        this.confirmed = confirmed;
        this.firePropertyChange("confirmed", oldConfirmed, confirmed);
    }
}

