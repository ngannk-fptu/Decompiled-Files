/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Document
 */
package com.sun.syndication.io;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.jdom.Document;

public class SyndFeedOutput {
    private WireFeedOutput _feedOutput = new WireFeedOutput();

    public String outputString(SyndFeed feed) throws FeedException {
        return this._feedOutput.outputString(feed.createWireFeed());
    }

    public String outputString(SyndFeed feed, boolean prettyPrint) throws FeedException {
        return this._feedOutput.outputString(feed.createWireFeed(), prettyPrint);
    }

    public void output(SyndFeed feed, File file) throws IOException, FeedException {
        this._feedOutput.output(feed.createWireFeed(), file);
    }

    public void output(SyndFeed feed, File file, boolean prettyPrint) throws IOException, FeedException {
        this._feedOutput.output(feed.createWireFeed(), file, prettyPrint);
    }

    public void output(SyndFeed feed, Writer writer) throws IOException, FeedException {
        this._feedOutput.output(feed.createWireFeed(), writer);
    }

    public void output(SyndFeed feed, Writer writer, boolean prettyPrint) throws IOException, FeedException {
        this._feedOutput.output(feed.createWireFeed(), writer, prettyPrint);
    }

    public org.w3c.dom.Document outputW3CDom(SyndFeed feed) throws FeedException {
        return this._feedOutput.outputW3CDom(feed.createWireFeed());
    }

    public Document outputJDom(SyndFeed feed) throws FeedException {
        return this._feedOutput.outputJDom(feed.createWireFeed());
    }
}

