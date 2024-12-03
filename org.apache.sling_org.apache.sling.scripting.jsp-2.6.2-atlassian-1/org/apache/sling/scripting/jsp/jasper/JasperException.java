/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 */
package org.apache.sling.scripting.jsp.jasper;

import javax.servlet.ServletException;

public class JasperException
extends ServletException {
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

