/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

public class BundleException
extends Exception {
    static final long serialVersionUID = 3571095144220455665L;
    private final int type;
    public static final int UNSPECIFIED = 0;
    public static final int UNSUPPORTED_OPERATION = 1;
    public static final int INVALID_OPERATION = 2;
    public static final int MANIFEST_ERROR = 3;
    public static final int RESOLVE_ERROR = 4;
    public static final int ACTIVATOR_ERROR = 5;
    public static final int SECURITY_ERROR = 6;
    public static final int STATECHANGE_ERROR = 7;
    public static final int NATIVECODE_ERROR = 8;
    public static final int DUPLICATE_BUNDLE_ERROR = 9;
    public static final int START_TRANSIENT_ERROR = 10;
    public static final int READ_ERROR = 11;
    public static final int REJECTED_BY_HOOK = 12;

    public BundleException(String msg, Throwable cause) {
        this(msg, 0, cause);
    }

    public BundleException(String msg) {
        this(msg, 0);
    }

    public BundleException(String msg, int type, Throwable cause) {
        super(msg, cause);
        this.type = type;
    }

    public BundleException(String msg, int type) {
        super(msg);
        this.type = type;
    }

    public Throwable getNestedException() {
        return this.getCause();
    }

    @Override
    public Throwable getCause() {
        return super.getCause();
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return super.initCause(cause);
    }

    public int getType() {
        return this.type;
    }
}

