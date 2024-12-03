/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.impl.Path;

final class SubstitutionExpression {
    private final Path path;
    private final boolean optional;

    SubstitutionExpression(Path path, boolean optional) {
        this.path = path;
        this.optional = optional;
    }

    Path path() {
        return this.path;
    }

    boolean optional() {
        return this.optional;
    }

    SubstitutionExpression changePath(Path newPath) {
        if (newPath == this.path) {
            return this;
        }
        return new SubstitutionExpression(newPath, this.optional);
    }

    public String toString() {
        return "${" + (this.optional ? "?" : "") + this.path.render() + "}";
    }

    public boolean equals(Object other) {
        if (other instanceof SubstitutionExpression) {
            SubstitutionExpression otherExp = (SubstitutionExpression)other;
            return otherExp.path.equals(this.path) && otherExp.optional == this.optional;
        }
        return false;
    }

    public int hashCode() {
        int h = 41 * (41 + this.path.hashCode());
        h = 41 * (h + (this.optional ? 1 : 0));
        return h;
    }
}

