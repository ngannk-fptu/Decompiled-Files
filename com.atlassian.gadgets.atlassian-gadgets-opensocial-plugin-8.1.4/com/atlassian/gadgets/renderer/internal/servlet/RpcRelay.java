/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RpcRelay
extends HttpServlet {
    private static final String SCRIPT = "<script>\nvar u = location.href, h = u.substr(u.indexOf('#') + 1).split('&'), t, r;\ntry {\nt = h[0] === '..' ? parent.parent : parent.frames[h[0]];\nr = t.gadgets.rpc.receive;\n} catch (e) {\n}\nr && r(h);\n</script>";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().write(SCRIPT);
    }
}

