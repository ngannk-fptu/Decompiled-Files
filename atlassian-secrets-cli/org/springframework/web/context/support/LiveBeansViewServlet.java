/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.context.support;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.support.LiveBeansView;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.support.ServletContextLiveBeansView;

public class LiveBeansViewServlet
extends HttpServlet {
    @Nullable
    private LiveBeansView liveBeansView;

    public void init() throws ServletException {
        this.liveBeansView = this.buildLiveBeansView();
    }

    protected LiveBeansView buildLiveBeansView() {
        return new ServletContextLiveBeansView(this.getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Assert.state(this.liveBeansView != null, "No LiveBeansView available");
        String content = this.liveBeansView.getSnapshotAsJson();
        response.setContentType("application/json");
        response.setContentLength(content.length());
        response.getWriter().write(content);
    }
}

