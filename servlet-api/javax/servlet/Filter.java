/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface Filter {
    default public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;

    default public void destroy() {
    }
}

