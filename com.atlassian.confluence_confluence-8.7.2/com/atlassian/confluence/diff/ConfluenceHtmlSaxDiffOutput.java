/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.WordUtils
 *  org.outerj.daisy.diff.html.dom.ImageNode
 *  org.outerj.daisy.diff.html.dom.Node
 *  org.outerj.daisy.diff.html.dom.TagNode
 *  org.outerj.daisy.diff.html.dom.TextNode
 *  org.outerj.daisy.diff.html.modification.Modification
 *  org.outerj.daisy.diff.html.modification.ModificationType
 *  org.outerj.daisy.diff.output.DiffOutput
 */
package com.atlassian.confluence.diff;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.outerj.daisy.diff.html.dom.ImageNode;
import org.outerj.daisy.diff.html.dom.Node;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.dom.TextNode;
import org.outerj.daisy.diff.html.modification.Modification;
import org.outerj.daisy.diff.html.modification.ModificationType;
import org.outerj.daisy.diff.output.DiffOutput;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ConfluenceHtmlSaxDiffOutput
implements DiffOutput {
    private ContentHandler handler;
    private String prefix;

    public ConfluenceHtmlSaxDiffOutput(ContentHandler handler, String name) {
        this.handler = handler;
        this.prefix = name;
    }

    public void generateOutput(TagNode node) throws SAXException {
        if (!node.getQName().equalsIgnoreCase("img")) {
            this.handler.startElement("", node.getQName(), node.getQName(), node.getAttributes());
        }
        boolean newStarted = false;
        boolean remStarted = false;
        boolean changeStarted = false;
        String changeTXT = "";
        for (Node child : node) {
            AttributesImpl attrs;
            if (child instanceof TagNode) {
                if (newStarted) {
                    this.handler.endElement("", "span", "span");
                    newStarted = false;
                } else if (changeStarted) {
                    this.handler.endElement("", "span", "span");
                    changeStarted = false;
                } else if (remStarted) {
                    this.handler.endElement("", "span", "span");
                    remStarted = false;
                }
                this.generateOutput((TagNode)child);
                continue;
            }
            if (!(child instanceof TextNode)) continue;
            TextNode textChild = (TextNode)child;
            Modification mod = textChild.getModification();
            if (newStarted && (mod.getType() != ModificationType.ADDED || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                newStarted = false;
            } else if (changeStarted && (mod.getType() != ModificationType.CHANGED || !mod.getChanges().equals(changeTXT) || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                changeStarted = false;
            } else if (remStarted && (mod.getType() != ModificationType.REMOVED || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                remStarted = false;
            }
            if (!newStarted && mod.getType() == ModificationType.ADDED) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-added");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", mod.getType() + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs, node);
                this.handler.startElement("", "span", "span", attrs);
                newStarted = true;
            } else if (!changeStarted && mod.getType() == ModificationType.CHANGED) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-changed");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", mod.getType() + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs, node);
                this.handler.startElement("", "span", "span", attrs);
                changeStarted = true;
                changeTXT = mod.getChanges();
            } else if (!remStarted && mod.getType() == ModificationType.REMOVED) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-removed");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", mod.getType() + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs, node);
                this.handler.startElement("", "span", "span", attrs);
                remStarted = true;
            }
            char[] chars = textChild.getText().toCharArray();
            if (textChild instanceof ImageNode) {
                this.writeImage((ImageNode)textChild);
                continue;
            }
            this.handler.characters(chars, 0, chars.length);
        }
        if (newStarted) {
            this.handler.endElement("", "span", "span");
            newStarted = false;
        } else if (changeStarted) {
            this.handler.endElement("", "span", "span");
            changeStarted = false;
        } else if (remStarted) {
            this.handler.endElement("", "span", "span");
            remStarted = false;
        }
        if (!node.getQName().equalsIgnoreCase("img") && !node.getQName().equalsIgnoreCase("body")) {
            this.handler.endElement("", node.getQName(), node.getQName());
        }
    }

    private void writeImage(ImageNode imgNode) throws SAXException {
        AttributesImpl attrs = imgNode.getAttributes();
        if (imgNode.getModification().getType() != ModificationType.NONE) {
            attrs.addAttribute("", "data-daisydiff-change-type", "data-daisydiff-change-type", "CDATA", imgNode.getModification().getType().toString());
        }
        this.handler.startElement("", "img", "img", attrs);
        this.handler.endElement("", "img", "img");
        if (this.isEmoticon(imgNode)) {
            return;
        }
        if (imgNode.getModification().getType() != ModificationType.NONE) {
            AttributesImpl overlayAttrs = new AttributesImpl();
            String changeType = attrs.getValue("data-daisydiff-change-type").toLowerCase();
            if ("changed".equals(changeType)) {
                changeType = "modified";
            }
            String imageStatus = WordUtils.capitalize((String)("Image " + changeType));
            Object overlayClasses = "";
            overlayClasses = (String)overlayClasses + "diff-image-overlay";
            overlayClasses = (String)overlayClasses + " diff-" + changeType + "-image";
            overlayAttrs.addAttribute("", "class", "class", "CDATA", (String)overlayClasses);
            this.handler.startElement("", "span", "span", overlayAttrs);
            this.handler.characters(imageStatus.toCharArray(), 0, imageStatus.length());
            this.handler.endElement("", "span", "span");
        }
    }

    private void addAttributes(Modification mod, AttributesImpl attrs, TagNode node) {
        if (mod.getType() == ModificationType.CHANGED) {
            String changes = mod.getChanges();
            attrs.addAttribute("", "data-daisydiff-changes", "data-daisydiff-changes", "CDATA", changes);
        }
        String previous = mod.getPrevious() == null ? "first-" + this.prefix : mod.getPrevious().getType() + "-" + this.prefix + "-" + mod.getPrevious().getID();
        attrs.addAttribute("", "data-daisydiff-previous", "data-daisydiff-previous", "CDATA", previous);
        String changeId = mod.getType() + "-" + this.prefix + "-" + mod.getID();
        attrs.addAttribute("", "data-daisydiff-change-id", "data-daisydiff-change-id", "CDATA", changeId);
        String next = mod.getNext() == null ? "last-" + this.prefix : mod.getNext().getType() + "-" + this.prefix + "-" + mod.getNext().getID();
        attrs.addAttribute("", "data-daisydiff-next", "data-daisydiff-next", "CDATA", next);
        if (null != node && node.getQName().equalsIgnoreCase("img") && !this.isEmoticon(node)) {
            attrs.setValue(attrs.getIndex("class"), attrs.getValue("class") + " diff-html-image-container");
        }
    }

    private boolean isEmoticon(TagNode node) {
        return StringUtils.contains((CharSequence)node.getAttributes().getValue("class"), (CharSequence)"emoticon");
    }

    private boolean isEmoticon(ImageNode node) {
        return StringUtils.contains((CharSequence)node.getAttributes().getValue("class"), (CharSequence)"emoticon");
    }
}

