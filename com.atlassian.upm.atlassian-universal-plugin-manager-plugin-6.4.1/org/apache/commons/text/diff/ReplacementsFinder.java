/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.diff;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.ReplacementsHandler;

public class ReplacementsFinder<T>
implements CommandVisitor<T> {
    private final List<T> pendingInsertions = new ArrayList<T>();
    private final List<T> pendingDeletions = new ArrayList<T>();
    private int skipped = 0;
    private final ReplacementsHandler<T> handler;

    public ReplacementsFinder(ReplacementsHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public void visitDeleteCommand(T object) {
        this.pendingDeletions.add(object);
    }

    @Override
    public void visitInsertCommand(T object) {
        this.pendingInsertions.add(object);
    }

    @Override
    public void visitKeepCommand(T object) {
        if (this.pendingDeletions.isEmpty() && this.pendingInsertions.isEmpty()) {
            ++this.skipped;
        } else {
            this.handler.handleReplacement(this.skipped, this.pendingDeletions, this.pendingInsertions);
            this.pendingDeletions.clear();
            this.pendingInsertions.clear();
            this.skipped = 1;
        }
    }
}

