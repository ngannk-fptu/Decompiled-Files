/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.web.filter.CachingHeaders
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.TwitterEmoticonService;
import com.atlassian.confluence.web.filter.CachingHeaders;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;

public class TwitterEmojiRedirectorServlet
extends HttpServlet {
    TwitterEmoticonService twitterEmoticonService;

    public TwitterEmojiRedirectorServlet(@Qualifier(value="twitterEmoticonService") TwitterEmoticonService twitterEmoticonService) {
        this.twitterEmoticonService = twitterEmoticonService;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        CachingHeaders.PUBLIC_LONG_TERM.apply(resp);
        String id = req.getParameter("id");
        this.doGetCache(id, resp);
    }

    public void doGetCache(String id, HttpServletResponse resp) throws IOException {
        AtlaskitEmoticonModel model = this.twitterEmoticonService.findById(id);
        if (model == null) {
            resp.setStatus(404);
            return;
        }
        resp.setContentType("image/svg+xml");
        resp.getWriter().write(this.twitterEmoticonService.getImageFileContent(model));
    }
}

