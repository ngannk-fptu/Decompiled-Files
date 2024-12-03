/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import java.util.Objects;

public class Modification {
    private final String fileName;
    private final String relativePath;

    public Modification(String fileName, String relativePath) {
        this.fileName = Objects.requireNonNull(fileName);
        this.relativePath = Objects.requireNonNull(relativePath);
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Modification that = (Modification)o;
        return Objects.equals(this.getFileName(), that.getFileName()) && Objects.equals(this.getRelativePath(), that.getRelativePath());
    }

    public int hashCode() {
        return Objects.hash(this.getFileName(), this.getRelativePath());
    }

    public String toString() {
        return String.format("Modification{fileName='%s', relativePath='%s'}", this.fileName, this.relativePath);
    }
}

