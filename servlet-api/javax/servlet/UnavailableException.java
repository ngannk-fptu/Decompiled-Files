/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

public class UnavailableException
extends ServletException {
    private static final long serialVersionUID = 1L;
    private final Servlet servlet;
    private final boolean permanent;
    private final int seconds;

    @Deprecated
    public UnavailableException(Servlet servlet, String msg) {
        super(msg);
        this.servlet = servlet;
        this.permanent = true;
        this.seconds = 0;
    }

    @Deprecated
    public UnavailableException(int seconds, Servlet servlet, String msg) {
        super(msg);
        this.servlet = servlet;
        this.seconds = seconds <= 0 ? -1 : seconds;
        this.permanent = false;
    }

    public UnavailableException(String msg) {
        super(msg);
        this.seconds = 0;
        this.servlet = null;
        this.permanent = true;
    }

    public UnavailableException(String msg, int seconds) {
        super(msg);
        this.seconds = seconds <= 0 ? -1 : seconds;
        this.servlet = null;
        this.permanent = false;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    @Deprecated
    public Servlet getServlet() {
        return this.servlet;
    }

    public int getUnavailableSeconds() {
        return this.permanent ? -1 : this.seconds;
    }
}

