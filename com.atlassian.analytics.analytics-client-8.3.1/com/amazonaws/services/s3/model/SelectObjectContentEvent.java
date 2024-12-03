/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.Progress;
import com.amazonaws.services.s3.model.SelectObjectContentEventVisitor;
import com.amazonaws.services.s3.model.Stats;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class SelectObjectContentEvent
implements Serializable,
Cloneable {
    public void visit(SelectObjectContentEventVisitor visitor) {
        visitor.visitDefault(this);
    }

    public SelectObjectContentEvent clone() {
        try {
            return (SelectObjectContentEvent)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class EndEvent
    extends SelectObjectContentEvent {
        @Override
        public void visit(SelectObjectContentEventVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class ContinuationEvent
    extends SelectObjectContentEvent {
        @Override
        public void visit(SelectObjectContentEventVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class ProgressEvent
    extends SelectObjectContentEvent {
        private Progress details;

        public Progress getDetails() {
            return this.details;
        }

        public void setDetails(Progress details) {
            this.details = details;
        }

        public ProgressEvent withDetails(Progress details) {
            this.setDetails(details);
            return this;
        }

        @Override
        public void visit(SelectObjectContentEventVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class StatsEvent
    extends SelectObjectContentEvent {
        private Stats details;

        public Stats getDetails() {
            return this.details;
        }

        public void setDetails(Stats details) {
            this.details = details;
        }

        public StatsEvent withDetails(Stats details) {
            this.setDetails(details);
            return this;
        }

        @Override
        public void visit(SelectObjectContentEventVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class RecordsEvent
    extends SelectObjectContentEvent {
        private ByteBuffer payload;

        public ByteBuffer getPayload() {
            return this.payload;
        }

        public void setPayload(ByteBuffer payload) {
            this.payload = payload;
        }

        public RecordsEvent withPayload(ByteBuffer payload) {
            this.setPayload(payload);
            return this;
        }

        @Override
        public void visit(SelectObjectContentEventVisitor visitor) {
            visitor.visit(this);
        }
    }
}

