/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.serviceprovider.internal.servlet;

import com.atlassian.oauth.serviceprovider.internal.servlet.TransactionException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class TransactionalServlet
extends HttpServlet {
    private final TransactionTemplate transactionTemplate;
    private final ServeInTransaction GET = this::doGetInTransaction;
    private final ServeInTransaction POST = this::doPostInTransaction;

    public TransactionalServlet(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
    }

    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.serve(this.GET, request, response);
    }

    protected void doGetInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.serve(this.POST, request, response);
    }

    protected void doPostInTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    private void serve(ServeInTransaction inTransaction, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            this.transactionTemplate.execute(() -> {
                try {
                    inTransaction.serve(request, response);
                }
                catch (IOException | ServletException e) {
                    throw new TransactionException((Exception)e);
                }
                return null;
            });
        }
        catch (TransactionException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw (ServletException)e.getCause();
        }
    }

    private static interface ServeInTransaction {
        public void serve(HttpServletRequest var1, HttpServletResponse var2) throws IOException, ServletException;
    }
}

