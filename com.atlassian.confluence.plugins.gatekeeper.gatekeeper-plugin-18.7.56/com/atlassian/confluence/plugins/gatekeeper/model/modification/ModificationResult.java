/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.modification;

import com.atlassian.confluence.plugins.gatekeeper.model.comparator.SpaceComparator;
import com.atlassian.confluence.plugins.gatekeeper.model.modification.ModificationResultEntry;
import java.util.ArrayList;
import java.util.List;

public class ModificationResult {
    private String status = "success";
    private List<ModificationResultEntry> succeed = new ArrayList<ModificationResultEntry>(0);
    private List<ModificationResultEntry> failed = new ArrayList<ModificationResultEntry>(0);

    public void addSuccessful(String spaceKey, String spaceName) {
        this.succeed.add(new ModificationResultEntry(spaceKey, spaceName));
    }

    public void addFailed(String spaceKey, String spaceName, String message) {
        this.failed.add(new ModificationResultEntry(spaceKey, spaceName, message));
    }

    public void finish() {
        this.succeed.sort(SpaceComparator.SPACE_COMPARATOR);
        this.failed.sort(SpaceComparator.SPACE_COMPARATOR);
    }

    public String getStatus() {
        return this.status;
    }

    public List<ModificationResultEntry> getSucceed() {
        return this.succeed;
    }

    public List<ModificationResultEntry> getFailed() {
        return this.failed;
    }
}

