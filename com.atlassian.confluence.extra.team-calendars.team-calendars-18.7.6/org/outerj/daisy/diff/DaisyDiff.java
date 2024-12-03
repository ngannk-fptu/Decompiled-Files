/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.outerj.daisy.diff.tag.TagComparator;
import org.outerj.daisy.diff.tag.TagDiffer;
import org.outerj.daisy.diff.tag.TagSaxDiffOutput;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class DaisyDiff {
    public static void diffHTML(InputSource oldSource, InputSource newSource, ContentHandler consumer, String prefix, Locale locale) throws SAXException, IOException {
        DomTreeBuilder oldHandler = new DomTreeBuilder();
        XMLReader xr1 = XMLReaderFactory.createXMLReader();
        xr1.setContentHandler(oldHandler);
        xr1.parse(oldSource);
        TextNodeComparator leftComparator = new TextNodeComparator(oldHandler, locale);
        DomTreeBuilder newHandler = new DomTreeBuilder();
        XMLReader xr2 = XMLReaderFactory.createXMLReader();
        xr2.setContentHandler(newHandler);
        xr2.parse(newSource);
        TextNodeComparator rightComparator = new TextNodeComparator(newHandler, locale);
        HtmlSaxDiffOutput output = new HtmlSaxDiffOutput(consumer, prefix);
        HTMLDiffer differ = new HTMLDiffer(output);
        differ.diff(leftComparator, rightComparator);
    }

    public static void diffTag(String oldText, String newText, ContentHandler consumer) throws Exception {
        consumer.startDocument();
        TagComparator oldComp = new TagComparator(oldText);
        TagComparator newComp = new TagComparator(newText);
        TagSaxDiffOutput output = new TagSaxDiffOutput(consumer);
        TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);
        consumer.endDocument();
    }

    public static void diffTag(BufferedReader oldText, BufferedReader newText, ContentHandler consumer) throws Exception {
        TagComparator oldComp = new TagComparator(oldText);
        TagComparator newComp = new TagComparator(newText);
        TagSaxDiffOutput output = new TagSaxDiffOutput(consumer);
        TagDiffer differ = new TagDiffer(output);
        differ.diff(oldComp, newComp);
    }
}

