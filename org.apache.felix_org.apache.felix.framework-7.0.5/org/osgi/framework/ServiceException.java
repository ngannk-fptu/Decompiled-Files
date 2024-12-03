/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

public class ServiceException
extends RuntimeException {
    static final long serialVersionUID = 3038963223712959631L;
    private final int type;
    public static final int UNSPECIFIED = 0;
    public static final int UNREGISTERED = 1;
    public static final int FACTORY_ERROR = 2;
    public static final int FACTORY_EXCEPTION = 3;
    public static final int SUBCLASSED = 4;
    public static final int REMOTE = 5;
    public static final int FACTORY_RECURSION = 6;
    public static final int ASYNC_ERROR = 7;

    public ServiceException(String msg, Throwable cause) {
        this(msg, 0, cause);
    }

    public ServiceException(String msg) {
        this(msg, 0);
    }

    public ServiceException(String msg, int type, Throwable cause) {
        super(msg, cause);
        this.type = type;
    }

    public ServiceException(String msg, int type) {
        super(msg);
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}

