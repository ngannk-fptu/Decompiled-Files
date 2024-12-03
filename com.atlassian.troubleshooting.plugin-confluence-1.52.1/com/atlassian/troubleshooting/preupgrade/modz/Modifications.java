/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.preupgrade.modz.Modification;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Modifications {
    private final List<Modification> modifiedFiles;
    private final List<Modification> deletedFiles;

    public Modifications(List<Modification> modifiedFiles, List<Modification> deletedFiles) {
        this.modifiedFiles = ImmutableList.copyOf((Collection)Objects.requireNonNull(modifiedFiles));
        this.deletedFiles = ImmutableList.copyOf((Collection)Objects.requireNonNull(deletedFiles));
    }

    public boolean hasModifications() {
        return !this.modifiedFiles.isEmpty();
    }

    public List<String> getNamesOfModifiedFiles() {
        return this.modifiedFiles.stream().map(Modification::getFileName).collect(Collectors.toList());
    }

    public List<String> getNamesOfDeletedFiles() {
        return this.deletedFiles.stream().map(Modification::getFileName).collect(Collectors.toList());
    }

    public List<Modification> getModifiedFiles() {
        return this.modifiedFiles;
    }

    public List<Modification> getDeletedFiles() {
        return this.deletedFiles;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Modifications that = (Modifications)o;
        return new EqualsBuilder().append(this.modifiedFiles, that.modifiedFiles).append(this.deletedFiles, that.deletedFiles).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.modifiedFiles).append(this.deletedFiles).toHashCode();
    }

    public String toString() {
        return String.format("Modifications{modifiedFiles=%s, deletedFiles=%s}", this.modifiedFiles, this.deletedFiles);
    }
}

