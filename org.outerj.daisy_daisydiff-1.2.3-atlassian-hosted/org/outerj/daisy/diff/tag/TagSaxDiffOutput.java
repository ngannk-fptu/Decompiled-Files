/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import org.outerj.daisy.diff.output.TextDiffOutput;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TagSaxDiffOutput
implements TextDiffOutput {
    private ContentHandler consumer;
    private boolean insideTag = false;
    private int removedID = 1;
    private int addedID = 1;

    public TagSaxDiffOutput(ContentHandler consumer) throws SAXException {
        this.consumer = consumer;
    }

    @Override
    public void addClearPart(String text) throws Exception {
        this.addBasicText(text);
    }

    private void addBasicText(String text) throws SAXException {
        char[] c = text.toCharArray();
        AttributesImpl noattrs = new AttributesImpl();
        block5: for (int i = 0; i < c.length; ++i) {
            switch (c[i]) {
                case '\n': {
                    this.consumer.startElement("", "br", "br", noattrs);
                    this.consumer.endElement("", "br", "br");
                    this.consumer.characters("\n".toCharArray(), 0, "\n".length());
                    continue block5;
                }
                case '<': {
                    if (!this.insideTag) {
                        AttributesImpl attrs = new AttributesImpl();
                        attrs.addAttribute("", "class", "class", "CDATA", "diff-tag-html");
                        this.consumer.startElement("", "span", "span", attrs);
                        this.insideTag = true;
                    } else {
                        this.consumer.endElement("", "span", "span");
                        this.insideTag = false;
                    }
                    this.consumer.characters("<".toCharArray(), 0, "<".length());
                    continue block5;
                }
                case '>': {
                    this.consumer.characters(">".toCharArray(), 0, ">".length());
                    if (!this.insideTag) continue block5;
                    this.consumer.endElement("", "span", "span");
                    this.insideTag = false;
                    continue block5;
                }
                default: {
                    this.consumer.characters(c, i, 1);
                }
            }
        }
    }

    @Override
    public void addRemovedPart(String text) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "class", "class", "CDATA", "diff-tag-removed");
        attrs.addAttribute("", "id", "id", "CDATA", "removed" + this.removedID);
        attrs.addAttribute("", "title", "title", "CDATA", "#removed" + this.removedID);
        ++this.removedID;
        this.consumer.startElement("", "span", "span", attrs);
        this.addBasicText(text);
        this.consumer.endElement("", "span", "span");
    }

    @Override
    public void addAddedPart(String text) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "class", "class", "CDATA", "diff-tag-added");
        attrs.addAttribute("", "id", "id", "CDATA", "added" + this.addedID);
        attrs.addAttribute("", "title", "title", "CDATA", "#added" + this.addedID);
        ++this.addedID;
        this.consumer.startElement("", "span", "span", attrs);
        this.addBasicText(text);
        this.consumer.endElement("", "span", "span");
    }
}

