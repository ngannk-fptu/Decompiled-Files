/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
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
        Assert.notEmpty((Object[])propertyAccessExceptions, (String)"At least 1 PropertyAccessException required");
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
            if (!ObjectUtils.nullSafeEquals((Object)propertyName, (Object)pae.getPropertyName())) continue;
            return pae;
        }
        return null;
    }

    public String getMessage() {
        StringJoiner stringJoiner = new StringJoiner("; ", "Failed properties: ", "");
        for (PropertyAccessException exception : this.propertyAccessExceptions) {
            stringJoiner.add(exception.getMessage());
        }
        return stringJoiner.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(((Object)((Object)this)).getClass().getName()).append("; nested PropertyAccessExceptions (");
        sb.append(this.getExceptionCount()).append(") are:");
        for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
            sb.append('\n').append("PropertyAccessException ").append(i + 1).append(": ");
            sb.append((Object)this.propertyAccessExceptions[i]);
        }
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream ps) {
        PrintStream printStream = ps;
        synchronized (printStream) {
            ps.println(((Object)((Object)this)).getClass().getName() + "; nested PropertyAccessException details (" + this.getExceptionCount() + ") are:");
            for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
                ps.println("PropertyAccessException " + (i + 1) + ":");
                this.propertyAccessExceptions[i].printStackTrace(ps);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter pw) {
        PrintWriter printWriter = pw;
        synchronized (printWriter) {
            pw.println(((Object)((Object)this)).getClass().getName() + "; nested PropertyAccessException details (" + this.getExceptionCount() + ") are:");
            for (int i = 0; i < this.propertyAccessExceptions.length; ++i) {
                pw.println("PropertyAccessException " + (i + 1) + ":");
                this.propertyAccessExceptions[i].printStackTrace(pw);
            }
        }
    }

    public boolean contains(@Nullable Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance((Object)this)) {
            return true;
        }
        for (PropertyAccessException pae : this.propertyAccessExceptions) {
            if (!pae.contains(exType)) continue;
            return true;
        }
        return false;
    }
}

