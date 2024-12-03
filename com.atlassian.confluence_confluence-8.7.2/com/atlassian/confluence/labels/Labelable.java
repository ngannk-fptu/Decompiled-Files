/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;

public interface Labelable {
    public List<Label> getLabels();

    public int getLabelCount();

    public boolean isFavourite(ConfluenceUser var1);
}

