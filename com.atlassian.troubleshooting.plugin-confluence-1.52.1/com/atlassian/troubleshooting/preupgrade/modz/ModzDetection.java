/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.preupgrade.modz.Modification;
import java.util.List;

public interface ModzDetection {
    public List<Modification> getModifiedFiles();

    public List<Modification> getRemovedFiles();
}

