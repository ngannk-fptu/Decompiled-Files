/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.WireFeed
 *  com.rometools.rome.io.WireFeedOutput
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.WireFeedOutput;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractFeedView<T extends WireFeed>
extends AbstractView {
    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        T wireFeed = this.newFeed();
        this.buildFeedMetadata(model, wireFeed, request);
        this.buildFeedEntries(model, wireFeed, request, response);
        this.setResponseContentType(request, response);
        if (!StringUtils.hasText(wireFeed.getEncoding())) {
            wireFeed.setEncoding("UTF-8");
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        ServletOutputStream out = response.getOutputStream();
        feedOutput.output(wireFeed, (Writer)new OutputStreamWriter((OutputStream)out, wireFeed.getEncoding()));
        out.flush();
    }

    protected abstract T newFeed();

    protected void buildFeedMetadata(Map<String, Object> model, T feed, HttpServletRequest request) {
    }

    protected abstract void buildFeedEntries(Map<String, Object> var1, T var2, HttpServletRequest var3, HttpServletResponse var4) throws Exception;
}

