/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 */
package com.atlassian.confluence.impl.hibernate;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class TransactionInfo {
    private static final int MAX_CHILDREN = 10;
    private final int sessionId;
    private final TransactionDefinition txDef;
    private final Optional<TransactionInfo> parent;
    private final List<TransactionInfo> children = new ArrayList<TransactionInfo>();

    private TransactionInfo(TransactionDefinition txDef, Optional<TransactionInfo> parent, int sessionId) {
        this.txDef = new DefaultTransactionDefinition(txDef);
        this.parent = parent;
        this.sessionId = sessionId;
    }

    Optional<TransactionInfo> getParent() {
        return this.parent;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.root().toString(sb, 0, this);
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    static TransactionInfo createChild(Optional<TransactionInfo> parent, TransactionDefinition txDef, int sessionId) {
        TransactionInfo child = new TransactionInfo(txDef, parent, sessionId);
        parent.ifPresent(p -> p.children.add(child));
        return child;
    }

    private TransactionInfo root() {
        return this.parent.map(TransactionInfo::root).orElse(this);
    }

    private void toString(StringBuilder sb, int level, @Nullable TransactionInfo activeTransactionInfo) {
        sb.append(Strings.repeat((String)"    ", (int)(level + 1)));
        if (this == activeTransactionInfo) {
            sb.setLength(sb.length() - 2);
            sb.append("->");
        }
        sb.append("[").append(this.txDef.getName()).append("]: ").append(this.txDef).append(" (Session #").append(this.sessionId).append(")\n");
        if (this.children.size() > 10) {
            sb.append(Strings.repeat((String)"    ", (int)(level + 2)));
            sb.append("Showing ").append(10).append(" last transactions at this level out of ").append(this.children.size()).append(" in total:\n");
        }
        List<TransactionInfo> latest = this.children.subList(Math.max(0, this.children.size() - 10), this.children.size());
        latest.forEach(child -> child.toString(sb, level + 1, activeTransactionInfo));
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TransactionInfo that = (TransactionInfo)o;
        return this.sessionId == that.sessionId && Objects.equals(this.txDef, that.txDef) && Objects.equals(this.parent, that.parent) && Objects.equals(this.children, that.children);
    }

    public int hashCode() {
        return Objects.hash(this.sessionId, this.txDef, this.parent, this.children);
    }
}

