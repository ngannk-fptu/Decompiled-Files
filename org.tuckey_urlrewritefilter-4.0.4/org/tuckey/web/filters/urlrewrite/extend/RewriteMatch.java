/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.extend;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RewriteMatch {
    public String getMatchingUrl() {
        return null;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return true;
    }
}

