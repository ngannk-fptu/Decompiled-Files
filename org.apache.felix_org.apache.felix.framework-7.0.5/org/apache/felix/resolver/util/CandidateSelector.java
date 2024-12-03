/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.osgi.resource.Capability;

public class CandidateSelector {
    protected final AtomicBoolean isUnmodifiable;
    protected final List<Capability> unmodifiable;
    private int currentIndex = 0;

    public CandidateSelector(List<Capability> candidates, AtomicBoolean isUnmodifiable) {
        this.isUnmodifiable = isUnmodifiable;
        this.unmodifiable = new ArrayList<Capability>(candidates);
    }

    protected CandidateSelector(CandidateSelector candidateSelector) {
        this.isUnmodifiable = candidateSelector.isUnmodifiable;
        this.unmodifiable = candidateSelector.unmodifiable;
        this.currentIndex = candidateSelector.currentIndex;
    }

    public CandidateSelector copy() {
        return new CandidateSelector(this);
    }

    public int getRemainingCandidateCount() {
        return this.unmodifiable.size() - this.currentIndex;
    }

    public Capability getCurrentCandidate() {
        return this.currentIndex < this.unmodifiable.size() ? this.unmodifiable.get(this.currentIndex) : null;
    }

    public List<Capability> getRemainingCandidates() {
        return Collections.unmodifiableList(this.unmodifiable.subList(this.currentIndex, this.unmodifiable.size()));
    }

    public boolean isEmpty() {
        return this.unmodifiable.size() <= this.currentIndex;
    }

    public Capability removeCurrentCandidate() {
        Capability current = this.getCurrentCandidate();
        if (current != null) {
            ++this.currentIndex;
        }
        return current;
    }

    public String toString() {
        return this.getRemainingCandidates().toString();
    }

    public int remove(Capability cap) {
        this.checkModifiable();
        int index = this.unmodifiable.indexOf(cap);
        if (index != -1) {
            this.unmodifiable.remove(index);
        }
        return index;
    }

    protected void checkModifiable() {
        if (this.isUnmodifiable.get()) {
            throw new IllegalStateException("Trying to mutate after candidates have been prepared.");
        }
    }
}

