/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class IssueId
implements Comparable<IssueId> {
    private static final Comparator<IssueId> COMPARATOR = Comparator.comparing(IssueId::toString);
    private final int code;
    private final String componentId;
    private final String idString;

    IssueId(String componentId, int code) {
        this.code = code;
        this.componentId = StringUtils.upperCase((String)componentId, (Locale)Locale.ROOT);
        this.idString = this.componentId + "-" + StringUtils.leftPad((String)Integer.toString(code), (int)4, (char)'0');
    }

    @Override
    public int compareTo(@Nonnull IssueId other) {
        return COMPARATOR.compare(this, other);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IssueId issueId = (IssueId)o;
        return this.code == issueId.code && Objects.equals(this.componentId, issueId.componentId);
    }

    public int hashCode() {
        return Objects.hash(this.code, this.componentId);
    }

    public String toString() {
        return this.idString;
    }

    @Nonnull
    public static IssueId valueOf(@Nonnull String id) {
        Objects.requireNonNull(id, "id");
        int separator = id.lastIndexOf(45);
        if (separator != -1 && separator < id.length() - 1) {
            String componentId = id.substring(0, separator);
            try {
                return new IssueId(componentId, Integer.parseInt(id.substring(separator + 1)));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        throw new IllegalArgumentException("Invalid issue ID: " + id);
    }

    int getCode() {
        return this.code;
    }

    @Nonnull
    String getComponentId() {
        return this.componentId;
    }
}

