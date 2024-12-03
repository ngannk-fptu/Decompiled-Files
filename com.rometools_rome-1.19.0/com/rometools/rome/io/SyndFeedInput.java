/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;
import org.jdom2.Document;
import org.xml.sax.InputSource;

public class SyndFeedInput {
    private final WireFeedInput feedInput;
    private boolean preserveWireFeed = false;

    public SyndFeedInput() {
        this(false, Locale.US);
    }

    public SyndFeedInput(boolean validate, Locale locale) {
        this.feedInput = new WireFeedInput(validate, locale);
    }

    public void setXmlHealerOn(boolean heals) {
        this.feedInput.setXmlHealerOn(heals);
    }

    public boolean getXmlHealerOn() {
        return this.feedInput.getXmlHealerOn();
    }

    public boolean isAllowDoctypes() {
        return this.feedInput.isAllowDoctypes();
    }

    public void setAllowDoctypes(boolean allowDoctypes) {
        this.feedInput.setAllowDoctypes(allowDoctypes);
    }

    public SyndFeed build(File file) throws FileNotFoundException, IOException, IllegalArgumentException, FeedException {
        return new SyndFeedImpl(this.feedInput.build(file), this.preserveWireFeed);
    }

    public SyndFeed build(Reader reader) throws IllegalArgumentException, FeedException {
        return new SyndFeedImpl(this.feedInput.build(reader), this.preserveWireFeed);
    }

    public SyndFeed build(InputSource is) throws IllegalArgumentException, FeedException {
        return new SyndFeedImpl(this.feedInput.build(is), this.preserveWireFeed);
    }

    public SyndFeed build(org.w3c.dom.Document document) throws IllegalArgumentException, FeedException {
        return new SyndFeedImpl(this.feedInput.build(document), this.preserveWireFeed);
    }

    public SyndFeed build(Document document) throws IllegalArgumentException, FeedException {
        return new SyndFeedImpl(this.feedInput.build(document), this.preserveWireFeed);
    }

    public boolean isPreserveWireFeed() {
        return this.preserveWireFeed;
    }

    public void setPreserveWireFeed(boolean preserveWireFeed) {
        this.preserveWireFeed = preserveWireFeed;
    }
}

