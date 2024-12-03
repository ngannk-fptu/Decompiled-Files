/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.catalina;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Request;

public interface Authenticator {
    public boolean authenticate(Request var1, HttpServletResponse var2) throws IOException;

    public void login(String var1, String var2, Request var3) throws ServletException;

    public void logout(Request var1);
}

