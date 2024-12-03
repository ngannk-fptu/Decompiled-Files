/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.dashboard.spi.changes;

import com.atlassian.gadgets.dashboard.spi.changes.AddGadgetChange;
import com.atlassian.gadgets.dashboard.spi.changes.GadgetColorChange;
import com.atlassian.gadgets.dashboard.spi.changes.RemoveGadgetChange;
import com.atlassian.gadgets.dashboard.spi.changes.UpdateGadgetUserPrefsChange;
import com.atlassian.gadgets.dashboard.spi.changes.UpdateLayoutChange;

public interface DashboardChange {
    public void accept(Visitor var1);

    public static interface Visitor {
        public void visit(AddGadgetChange var1);

        public void visit(GadgetColorChange var1);

        public void visit(RemoveGadgetChange var1);

        public void visit(UpdateGadgetUserPrefsChange var1);

        public void visit(UpdateLayoutChange var1);
    }
}

