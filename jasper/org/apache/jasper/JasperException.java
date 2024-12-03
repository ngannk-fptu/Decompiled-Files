/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.apache.jasper;

import javax.servlet.ServletException;

public class JasperException
extends ServletException {
    private static final long serialVersionUID = 1L;

    public JasperException(String reason) {
        super(reason);
    }

    public JasperException(String reason, Throwable exception) {
        super(reason, exception);
    }

    public JasperException(Throwable exception) {
        super(exception);
    }
}

