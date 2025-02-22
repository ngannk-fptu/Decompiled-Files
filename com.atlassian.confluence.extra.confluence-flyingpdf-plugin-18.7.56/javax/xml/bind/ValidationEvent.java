/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.bind;

import javax.xml.bind.ValidationEventLocator;

public interface ValidationEvent {
    public static final int WARNING = 0;
    public static final int ERROR = 1;
    public static final int FATAL_ERROR = 2;

    public int getSeverity();

    public String getMessage();

    public Throwable getLinkedException();

    public ValidationEventLocator getLocator();
}

