/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.rometools.rome.feed.WireFeed
 *  com.rometools.rome.feed.synd.SyndFeed
 *  com.rometools.rome.io.WireFeedOutput
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.result.StrutsResultSupport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.xwork.results;

import com.opensymphony.xwork2.ActionInvocation;
import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.result.StrutsResultSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RssResult
extends StrutsResultSupport {
    private static final Logger log = LoggerFactory.getLogger(RssResult.class);
    private static final String DEFAULT_DEFAULT_ENCODING = "UTF-8";
    public static final String RSS = "rss";
    public static final String RSS1 = "rss1";
    public static final String RSS2 = "rss2";
    public static final String ATOM = "atom";

    protected void doExecute(String finalDestination, ActionInvocation actionInvocation) throws Exception {
        block7: {
            ServletActionContext.getRequest().getSession(false);
            HttpServletResponse response = ServletActionContext.getResponse();
            if (finalDestination.startsWith(RSS)) {
                response.setContentType("application/rss+xml; charset=UTF-8");
            } else if (finalDestination.startsWith(ATOM)) {
                response.setContentType("application/atom+xml; charset=UTF-8");
            } else {
                response.setContentType("text/xml; charset=UTF-8");
            }
            SyndFeed feed = (SyndFeed)actionInvocation.getStack().findValue("syndFeed");
            if (feed == null) {
                throw new ServletException("Unable to find feed for this action");
            }
            WireFeed outFeed = feed.createWireFeed(finalDestination);
            outFeed.setEncoding(DEFAULT_DEFAULT_ENCODING);
            new WireFeedOutput().output(outFeed, (Writer)response.getWriter());
            try {
                response.flushBuffer();
            }
            catch (IOException e) {
                log.info("Client aborted (closed the connection) before the rss feed could be returned.");
                if (!log.isDebugEnabled()) break block7;
                log.debug("Error sending rss result to client", (Throwable)e);
            }
        }
    }
}

