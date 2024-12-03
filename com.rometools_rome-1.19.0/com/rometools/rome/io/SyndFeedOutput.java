/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.jdom2.Document;

public class SyndFeedOutput {
    private final WireFeedOutput feedOutput = new WireFeedOutput();

    public String outputString(SyndFeed feed) throws FeedException {
        return this.feedOutput.outputString(feed.createWireFeed());
    }

    public String outputString(SyndFeed feed, boolean prettyPrint) throws FeedException {
        return this.feedOutput.outputString(feed.createWireFeed(), prettyPrint);
    }

    public void output(SyndFeed feed, File file) throws IOException, FeedException {
        this.feedOutput.output(feed.createWireFeed(), file);
    }

    public void output(SyndFeed feed, File file, boolean prettyPrint) throws IOException, FeedException {
        this.feedOutput.output(feed.createWireFeed(), file, prettyPrint);
    }

    public void output(SyndFeed feed, Writer writer) throws IOException, FeedException {
        this.feedOutput.output(feed.createWireFeed(), writer);
    }

    public void output(SyndFeed feed, Writer writer, boolean prettyPrint) throws IOException, FeedException {
        this.feedOutput.output(feed.createWireFeed(), writer, prettyPrint);
    }

    public org.w3c.dom.Document outputW3CDom(SyndFeed feed) throws FeedException {
        return this.feedOutput.outputW3CDom(feed.createWireFeed());
    }

    public Document outputJDom(SyndFeed feed) throws FeedException {
        return this.feedOutput.outputJDom(feed.createWireFeed());
    }
}

