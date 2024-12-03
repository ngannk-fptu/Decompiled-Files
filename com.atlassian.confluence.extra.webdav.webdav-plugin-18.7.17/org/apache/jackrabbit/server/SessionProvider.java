/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.server;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public interface SessionProvider {
    public Session getSession(HttpServletRequest var1, Repository var2, String var3) throws LoginException, ServletException, RepositoryException;

    public void releaseSession(Session var1);
}

