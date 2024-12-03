/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.StringJoiner;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class PropertyBatchUpdateException
extends BeansException {
    private final PropertyAccessException[] propertyAccessExceptions;

    public PropertyBatchUpdateException(PropertyAccessException[] propertyAccessExceptions) {
        super(null, null);
        Assert.notEmpty((Object[])propertyAccessExceptions, "At least 1 PropertyAccessException required");
        this.propertyAccessExceptions = propertyAccessExceptions;
    }

    public final int getExceptionCount() {
        return this.propertyAccessExceptions.length;
    }

    public final PropertyAccessException[] getPropertyAccessExceptions() {
        return this.propertyAccessExceptions;
    }

    @Nullable
    public PropertyAccessException getPropertyAccessException(String propertyName) {
        for (PropertyAccessException pae : this.propertyAccessExceptions) {
            if (!ObjectUtils.nullSafeEquals(propertyName, pae.getPropertyName())) continue;
            return pae;
        }
        return null;
    }

    @Override
    public String getMessage() {
        StringJoiner stringJoiner = new StringJoiner("; ", "Failed properties: ", "");
        for (PropertyAccessException exception : this.propertyAccessExceptions) {
            stringJoiner.add(exception.getMessage());
        }
        return stringJoiner.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append("; nested PropertyAccessExceptions (");
        sb.append(this.getExceptionCount()).append(") are:");
        for (int i2 = 0; i2 < this.propertyAccessExceptions.length; ++i2) {
            sb.append('\n').append("PropertyAccessException ").append(i2 + 1).append(": ");
            sb.append(this.propertyAccessExceptions[i2]);
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintStream ps) {
        PrintStream printStream = ps;
        synchronized (printStream) {
            ps.println(this.getClass().getName() + "; nested PropertyAccessException details (" + this.getExceptionCount() + ") are:");
            for (int i2 = 0; i2 < this.propertyAccessExceptions.length; ++i2) {
                ps.println("PropertyAccessException " + (i2 + 1) + ":");
                this.propertyAccessExceptions[i2].printStackTrace(ps);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintWriter pw) {
        PrintWriter printWriter = pw;
        synchronized (printWriter) {
            pw.println(this.getClass().getName() + "; nested PropertyAccessException details (" + this.getExceptionCount() + ") are:");
            for (int i2 = 0; i2 < this.propertyAccessExceptions.length; ++i2) {
                pw.println("PropertyAccessException " + (i2 + 1) + ":");
                this.propertyAccessExceptions[i2].printStackTrace(pw);
            }
        }
    }

    @Override
    public boolean contains(@Nullable Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        for (PropertyAccessException pae : this.propertyAccessExceptions) {
            if (!pae.contains(exType)) continue;
            return true;
        }
        return false;
    }
}

