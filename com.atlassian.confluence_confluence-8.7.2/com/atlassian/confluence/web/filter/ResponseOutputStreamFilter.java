/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.core.filters.AbstractHttpFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ResponseOutputStreamFilter
extends AbstractHttpFilter {
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        NoopAfterCloseResponse wrappedResponse = new NoopAfterCloseResponse(response);
        filterChain.doFilter((ServletRequest)request, (ServletResponse)wrappedResponse);
    }

    private static class NoopAfterCloseOutputStream
    extends ServletOutputStream {
        private ServletOutputStream out;
        private volatile boolean closed = false;

        private NoopAfterCloseOutputStream(ServletOutputStream out) {
            this.out = out;
        }

        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            try {
                this.out.close();
            }
            finally {
                this.out = null;
            }
        }

        public void write(int b) throws IOException {
            if (this.closed) {
                return;
            }
            this.out.write(b);
        }

        public void write(@NonNull byte[] b) throws IOException {
            if (this.closed) {
                return;
            }
            this.out.write(b);
        }

        public void write(@NonNull byte[] b, int off, int len) throws IOException {
            if (this.closed) {
                return;
            }
            this.out.write(b, off, len);
        }

        public void flush() throws IOException {
            if (this.closed) {
                return;
            }
            this.out.flush();
        }

        public boolean isReady() {
            if (this.closed) {
                return true;
            }
            return this.out.isReady();
        }

        public void setWriteListener(@NonNull WriteListener writeListener) {
            if (this.closed) {
                return;
            }
            this.out.setWriteListener(writeListener);
        }
    }

    private static class NoopAfterCloseResponse
    extends HttpServletResponseWrapper {
        private ServletOutputStream wrappedOutputStream;

        private NoopAfterCloseResponse(HttpServletResponse response) throws IOException {
            super(response);
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (this.wrappedOutputStream == null) {
                this.wrappedOutputStream = new NoopAfterCloseOutputStream(super.getOutputStream());
            }
            return this.wrappedOutputStream;
        }
    }
}

