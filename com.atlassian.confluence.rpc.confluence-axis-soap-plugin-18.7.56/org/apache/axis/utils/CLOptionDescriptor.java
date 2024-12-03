/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

public final class CLOptionDescriptor {
    public static final int ARGUMENT_REQUIRED = 2;
    public static final int ARGUMENT_OPTIONAL = 4;
    public static final int ARGUMENT_DISALLOWED = 8;
    public static final int ARGUMENTS_REQUIRED_2 = 16;
    public static final int DUPLICATES_ALLOWED = 32;
    private final int m_id;
    private final int m_flags;
    private final String m_name;
    private final String m_description;
    private final int[] m_incompatible;

    public CLOptionDescriptor(String name, int flags, int id, String description) {
        int[] nArray;
        if ((flags & 0x20) > 0) {
            nArray = new int[]{};
        } else {
            int[] nArray2 = new int[1];
            nArray = nArray2;
            nArray2[0] = id;
        }
        this(name, flags, id, description, nArray);
    }

    public CLOptionDescriptor(String name, int flags, int id, String description, int[] incompatable) {
        this.m_id = id;
        this.m_name = name;
        this.m_flags = flags;
        this.m_description = description;
        this.m_incompatible = incompatable;
    }

    protected final int[] getIncompatble() {
        return this.getIncompatible();
    }

    protected final int[] getIncompatible() {
        return this.m_incompatible;
    }

    public final String getDescription() {
        return this.m_description;
    }

    public final int getFlags() {
        return this.m_flags;
    }

    public final int getId() {
        return this.m_id;
    }

    public final String getName() {
        return this.m_name;
    }

    public final String toString() {
        return "[OptionDescriptor " + this.m_name + ", " + this.m_id + ", " + this.m_flags + ", " + this.m_description + " ]";
    }
}

