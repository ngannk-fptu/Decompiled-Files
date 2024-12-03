/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.Document
 *  org.jdom2.JDOMException
 *  org.jdom2.ProcessingInstruction
 *  org.jdom2.output.DOMOutputter
 *  org.jdom2.output.Format
 *  org.jdom2.output.XMLOutputter
 */
package com.rometools.rome.io;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.impl.ConfigurableClassLoader;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedGenerator;
import com.rometools.rome.io.impl.FeedGenerators;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.output.DOMOutputter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class WireFeedOutput {
    private static Map<ClassLoader, FeedGenerators> clMap = new WeakHashMap<ClassLoader, FeedGenerators>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static FeedGenerators getFeedGenerators() {
        Class<WireFeedOutput> clazz = WireFeedOutput.class;
        synchronized (WireFeedOutput.class) {
            ClassLoader classLoader = ConfigurableClassLoader.INSTANCE.getClassLoader();
            FeedGenerators generators = clMap.get(classLoader);
            if (generators == null) {
                generators = new FeedGenerators();
                clMap.put(classLoader, generators);
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return generators;
        }
    }

    public static List<String> getSupportedFeedTypes() {
        return WireFeedOutput.getFeedGenerators().getSupportedFeedTypes();
    }

    public String outputString(WireFeed feed) throws IllegalArgumentException, FeedException {
        return this.outputString(feed, true);
    }

    public String outputString(WireFeed feed, boolean prettyPrint) throws IllegalArgumentException, FeedException {
        Document doc = this.outputJDom(feed);
        String encoding = feed.getEncoding();
        Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
        if (encoding != null) {
            format.setEncoding(encoding);
        }
        XMLOutputter outputter = new XMLOutputter(format);
        return outputter.outputString(doc);
    }

    public void output(WireFeed feed, File file) throws IllegalArgumentException, IOException, FeedException {
        this.output(feed, file, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void output(WireFeed feed, File file, boolean prettyPrint) throws IllegalArgumentException, IOException, FeedException {
        FileWriter writer = new FileWriter(file);
        try {
            this.output(feed, writer, prettyPrint);
        }
        finally {
            ((Writer)writer).close();
        }
    }

    public void output(WireFeed feed, Writer writer) throws IllegalArgumentException, IOException, FeedException {
        this.output(feed, writer, true);
    }

    public void output(WireFeed feed, Writer writer, boolean prettyPrint) throws IllegalArgumentException, IOException, FeedException {
        Document doc = this.outputJDom(feed);
        String encoding = feed.getEncoding();
        Format format = prettyPrint ? Format.getPrettyFormat() : Format.getCompactFormat();
        if (encoding != null) {
            format.setEncoding(encoding);
        }
        XMLOutputter outputter = new XMLOutputter(format);
        outputter.output(doc, writer);
    }

    public org.w3c.dom.Document outputW3CDom(WireFeed feed) throws IllegalArgumentException, FeedException {
        Document doc = this.outputJDom(feed);
        DOMOutputter outputter = new DOMOutputter();
        try {
            return outputter.output(doc);
        }
        catch (JDOMException jdomEx) {
            throw new FeedException("Could not create DOM", jdomEx);
        }
    }

    public Document outputJDom(WireFeed feed) throws IllegalArgumentException, FeedException {
        String type = feed.getFeedType();
        WireFeedGenerator generator = WireFeedOutput.getFeedGenerators().getGenerator(type);
        if (generator == null) {
            throw new IllegalArgumentException("Invalid feed type [" + type + "]");
        }
        if (!generator.getType().equals(type)) {
            throw new IllegalArgumentException("WireFeedOutput type[" + type + "] and WireFeed type [" + type + "] don't match");
        }
        Document doc = generator.generate(feed);
        String styleSheet = feed.getStyleSheet();
        if (styleSheet != null) {
            LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
            data.put("type", "text/xsl");
            data.put("href", styleSheet);
            doc.addContent(0, (Content)new ProcessingInstruction("xml-stylesheet", data));
        }
        return doc;
    }
}

