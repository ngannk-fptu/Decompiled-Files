/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.Evaluation;

public class OgnlException
extends Exception {
    private Evaluation _evaluation;

    public OgnlException() {
        this(null, null);
    }

    public OgnlException(String msg) {
        this(msg, null);
    }

    public OgnlException(String msg, Throwable reason) {
        super(msg, reason, true, false);
    }

    protected OgnlException(String message, Throwable reason, boolean enableSuppression, boolean writableStackTrace) {
        super(message, reason, enableSuppression, writableStackTrace);
    }

    public Throwable getReason() {
        return this.getCause();
    }

    public Evaluation getEvaluation() {
        return this._evaluation;
    }

    public void setEvaluation(Evaluation value) {
        this._evaluation = value;
    }

    @Override
    public String toString() {
        if (this.getCause() == null) {
            return super.toString();
        }
        return super.toString() + " [" + this.getCause() + "]";
    }
}

