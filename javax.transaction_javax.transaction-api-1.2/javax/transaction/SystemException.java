/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction;

public class SystemException
extends Exception {
    public int errorCode;

    public SystemException() {
    }

    public SystemException(String s) {
        super(s);
    }

    public SystemException(int errcode) {
        this.errorCode = errcode;
    }
}

