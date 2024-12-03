/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.util;

import com.google.inject.Key;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Sets;
import java.lang.annotation.Annotation;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class Node {
    private final Key<?> key;
    private int appliedScope = Integer.MAX_VALUE;
    private Node effectiveScopeDependency;
    private int effectiveScope = Integer.MIN_VALUE;
    private Class<? extends Annotation> appliedScopeAnnotation;
    private Set<Node> users = $ImmutableSet.of();

    Node(Key<?> key) {
        this.key = key;
    }

    void setScopeRank(int rank, Class<? extends Annotation> annotation) {
        this.appliedScope = rank;
        this.effectiveScope = rank;
        this.appliedScopeAnnotation = annotation;
    }

    private void setEffectiveScope(int effectiveScope, Node effectiveScopeDependency) {
        if (this.effectiveScope >= effectiveScope) {
            return;
        }
        this.effectiveScope = effectiveScope;
        this.effectiveScopeDependency = effectiveScopeDependency;
        this.pushScopeToUsers();
    }

    void pushScopeToUsers() {
        for (Node user : this.users) {
            user.setEffectiveScope(this.effectiveScope, this);
        }
    }

    boolean isScopedCorrectly() {
        return this.appliedScope >= this.effectiveScope;
    }

    boolean isEffectiveScopeAppliedScope() {
        return this.appliedScope == this.effectiveScope;
    }

    Node effectiveScopeDependency() {
        return this.effectiveScopeDependency;
    }

    public void addUser(Node node) {
        if (this.users.isEmpty()) {
            this.users = $Sets.newHashSet();
        }
        this.users.add(node);
    }

    public String toString() {
        return this.appliedScopeAnnotation != null ? Errors.convert(this.key) + " in @" + this.appliedScopeAnnotation.getSimpleName() : Errors.convert(this.key).toString();
    }
}

