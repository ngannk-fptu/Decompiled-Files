/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.jsp;

public class IteratorStatus {
    protected StatusState state;

    public IteratorStatus(StatusState aState) {
        this.state = aState;
    }

    public int getCount() {
        return this.state.index + 1;
    }

    public boolean isEven() {
        return (this.state.index + 1) % 2 == 0;
    }

    public boolean isFirst() {
        return this.state.index == 0;
    }

    public int getIndex() {
        return this.state.index;
    }

    public boolean isLast() {
        return this.state.last;
    }

    public boolean isOdd() {
        return (this.state.index + 1) % 2 != 0;
    }

    public int modulus(int operand) {
        return (this.state.index + 1) % operand;
    }

    public static class StatusState {
        boolean last = false;
        int index = 0;

        public void setLast(boolean isLast) {
            this.last = isLast;
        }

        public void next() {
            ++this.index;
        }
    }
}

