/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.util.Arrays;

public final class CLOption {
    public static final int TEXT_ARGUMENT = 0;
    private final int m_id;
    private String[] m_arguments;

    public final String getArgument() {
        return this.getArgument(0);
    }

    public final String getArgument(int index) {
        if (null == this.m_arguments || index < 0 || index >= this.m_arguments.length) {
            return null;
        }
        return this.m_arguments[index];
    }

    public final int getId() {
        return this.m_id;
    }

    public CLOption(int id) {
        this.m_id = id;
    }

    public CLOption(String argument) {
        this(0);
        this.addArgument(argument);
    }

    public final void addArgument(String argument) {
        if (null == this.m_arguments) {
            this.m_arguments = new String[]{argument};
        } else {
            String[] arguments = new String[this.m_arguments.length + 1];
            System.arraycopy(this.m_arguments, 0, arguments, 0, this.m_arguments.length);
            arguments[this.m_arguments.length] = argument;
            this.m_arguments = arguments;
        }
    }

    public final int getArgumentCount() {
        if (null == this.m_arguments) {
            return 0;
        }
        return this.m_arguments.length;
    }

    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[Option ");
        sb.append((char)this.m_id);
        if (null != this.m_arguments) {
            sb.append(", ");
            sb.append(Arrays.asList(this.m_arguments));
        }
        sb.append(" ]");
        return sb.toString();
    }
}

