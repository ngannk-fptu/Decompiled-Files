/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

public interface Group
extends Comparable<Group> {
    public String getName();

    public boolean equals(Object var1);

    public int hashCode();

    @Override
    public int compareTo(Group var1);
}

