/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.LayoutManager;
import javax.swing.JPanel;
import org.jfree.ui.WizardDialog;

public abstract class WizardPanel
extends JPanel {
    private WizardDialog owner;

    protected WizardPanel(LayoutManager layout) {
        super(layout);
    }

    public WizardDialog getOwner() {
        return this.owner;
    }

    public void setOwner(WizardDialog owner) {
        this.owner = owner;
    }

    public Object getResult() {
        return null;
    }

    public abstract void returnFromLaterStep();

    public abstract boolean canRedisplayNextPanel();

    public abstract boolean hasNextPanel();

    public abstract boolean canFinish();

    public abstract WizardPanel getNextPanel();
}

