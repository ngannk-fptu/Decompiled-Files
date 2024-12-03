/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.server;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public interface CredentialsProvider {
    public Credentials getCredentials(HttpServletRequest var1) throws LoginException, ServletException;
}

