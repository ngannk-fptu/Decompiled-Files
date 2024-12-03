/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.tuckey.web.filters.urlrewrite.sample;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.tuckey.web.filters.urlrewrite.extend.RewriteMatch;

class SampleRewriteMatch
extends RewriteMatch {
    private int id;

    SampleRewriteMatch(int i) {
        this.id = i;
    }

    int getId() {
        return this.id;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("sampleRewriteMatch", (Object)this);
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/some-view.jsp");
        rd.forward((ServletRequest)request, (ServletResponse)response);
        return true;
    }
}

