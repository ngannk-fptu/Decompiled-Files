/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.SelectObjectContentEvent;

public abstract class SelectObjectContentEventVisitor {
    public void visit(SelectObjectContentEvent.RecordsEvent event) {
        this.visitDefault(event);
    }

    public void visit(SelectObjectContentEvent.ContinuationEvent event) {
        this.visitDefault(event);
    }

    public void visit(SelectObjectContentEvent.ProgressEvent event) {
        this.visitDefault(event);
    }

    public void visit(SelectObjectContentEvent.StatsEvent event) {
        this.visitDefault(event);
    }

    public void visit(SelectObjectContentEvent.EndEvent event) {
        this.visitDefault(event);
    }

    public void visitDefault(SelectObjectContentEvent selectEvent) {
    }
}

