/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class LinkContentHandler
extends DefaultHandler {
    private final LinkedList<LinkBuilder> builderStack = new LinkedList();
    private final List<Link> links = new ArrayList<Link>();
    private boolean collapseWhitespaceInAnchor;

    public LinkContentHandler() {
        this(false);
    }

    public LinkContentHandler(boolean collapseWhitespaceInAnchor) {
        this.collapseWhitespaceInAnchor = collapseWhitespaceInAnchor;
    }

    public List<Link> getLinks() {
        return this.links;
    }

    @Override
    public void startElement(String uri, String local, String name, Attributes attributes) {
        if ("http://www.w3.org/1999/xhtml".equals(uri)) {
            if ("a".equals(local)) {
                LinkBuilder builder = new LinkBuilder("a");
                builder.setURI(attributes.getValue("", "href"));
                builder.setTitle(attributes.getValue("", "title"));
                builder.setRel(attributes.getValue("", "rel"));
                this.builderStack.addFirst(builder);
            } else if ("link".equals(local)) {
                LinkBuilder builder = new LinkBuilder("link");
                builder.setURI(attributes.getValue("", "href"));
                builder.setRel(attributes.getValue("", "rel"));
                this.builderStack.addFirst(builder);
            } else if ("script".equals(local)) {
                if (attributes.getValue("", "src") != null) {
                    LinkBuilder builder = new LinkBuilder("script");
                    builder.setURI(attributes.getValue("", "src"));
                    this.builderStack.addFirst(builder);
                }
            } else if ("iframe".equals(local)) {
                LinkBuilder builder = new LinkBuilder("iframe");
                builder.setURI(attributes.getValue("", "src"));
                this.builderStack.addFirst(builder);
            } else if ("img".equals(local)) {
                LinkBuilder builder = new LinkBuilder("img");
                builder.setURI(attributes.getValue("", "src"));
                builder.setTitle(attributes.getValue("", "title"));
                builder.setRel(attributes.getValue("", "rel"));
                this.builderStack.addFirst(builder);
                String alt = attributes.getValue("", "alt");
                if (alt != null) {
                    char[] ch = alt.toCharArray();
                    this.characters(ch, 0, ch.length);
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        for (LinkBuilder builder : this.builderStack) {
            builder.characters(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
        this.characters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String local, String name) {
        if (!this.builderStack.isEmpty() && "http://www.w3.org/1999/xhtml".equals(uri) && ("a".equals(local) || "img".equals(local) || "link".equals(local) || "script".equals(local) || "iframe".equals(local)) && this.builderStack.getFirst().getType().equals(local)) {
            LinkBuilder builder = this.builderStack.removeFirst();
            this.links.add(builder.getLink(this.collapseWhitespaceInAnchor));
        }
    }
}

