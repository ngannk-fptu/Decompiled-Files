/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Labelling;
import java.util.List;

public interface EditableLabelable
extends Labelable {
    public void addLabelling(Labelling var1);

    public void removeLabelling(Labelling var1);

    public List<Labelling> getLabellings();

    public long getId();

    public boolean isPersistent();

    public String getTitle();
}

