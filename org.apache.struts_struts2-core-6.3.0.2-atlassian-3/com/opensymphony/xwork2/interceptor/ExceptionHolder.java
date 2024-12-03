/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class ExceptionHolder
implements Serializable {
    private static final long serialVersionUID = 1L;
    private Exception exception;

    public ExceptionHolder(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return this.exception;
    }

    public String getExceptionStack() {
        String exceptionStack = null;
        if (this.getException() != null) {
            try (StringWriter sw = new StringWriter();
                 PrintWriter pw = new PrintWriter(sw);){
                this.getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return exceptionStack;
    }
}

