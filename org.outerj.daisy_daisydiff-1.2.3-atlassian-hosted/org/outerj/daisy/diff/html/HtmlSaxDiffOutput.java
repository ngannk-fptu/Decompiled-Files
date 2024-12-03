/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html;

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

public class HtmlSaxDiffOutput
implements DiffOutput {
    private ContentHandler handler;
    private String prefix;

    public HtmlSaxDiffOutput(ContentHandler handler, String name) {
        this.handler = handler;
        this.prefix = name;
    }

    @Override
    public void generateOutput(TagNode node) throws SAXException {
        if (!node.getQName().equalsIgnoreCase("img") && !node.getQName().equalsIgnoreCase("body")) {
            this.handler.startElement("", node.getQName(), node.getQName(), node.getAttributes());
        }
        boolean newStarted = false;
        boolean remStarted = false;
        boolean changeStarted = false;
        boolean conflictStarted = false;
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
                } else if (conflictStarted) {
                    this.handler.endElement("", "span", "span");
                    conflictStarted = false;
                }
                this.generateOutput((TagNode)child);
                continue;
            }
            if (!(child instanceof TextNode)) continue;
            TextNode textChild = (TextNode)child;
            Modification mod = textChild.getModification();
            if (newStarted && (mod.getOutputType() != ModificationType.ADDED || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                newStarted = false;
            } else if (changeStarted && (mod.getOutputType() != ModificationType.CHANGED || !mod.getChanges().equals(changeTXT) || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                changeStarted = false;
            } else if (remStarted && (mod.getOutputType() != ModificationType.REMOVED || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                remStarted = false;
            } else if (conflictStarted && (mod.getOutputType() != ModificationType.CONFLICT || mod.isFirstOfID())) {
                this.handler.endElement("", "span", "span");
                conflictStarted = false;
            }
            if (!newStarted && mod.getOutputType() == ModificationType.ADDED) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-added");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", (Object)((Object)mod.getOutputType()) + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs);
                this.handler.startElement("", "span", "span", attrs);
                newStarted = true;
            } else if (!changeStarted && mod.getOutputType() == ModificationType.CHANGED) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-changed");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", (Object)((Object)mod.getOutputType()) + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs);
                this.handler.startElement("", "span", "span", attrs);
                changeStarted = true;
                changeTXT = mod.getChanges();
            } else if (!remStarted && mod.getOutputType() == ModificationType.REMOVED) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-removed");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", (Object)((Object)mod.getOutputType()) + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs);
                this.handler.startElement("", "span", "span", attrs);
                remStarted = true;
            } else if (!conflictStarted && mod.getOutputType() == ModificationType.CONFLICT) {
                attrs = new AttributesImpl();
                attrs.addAttribute("", "class", "class", "CDATA", "diff-html-conflict");
                if (mod.isFirstOfID()) {
                    attrs.addAttribute("", "id", "id", "CDATA", (Object)((Object)mod.getOutputType()) + "-" + this.prefix + "-" + mod.getID());
                }
                this.addAttributes(mod, attrs);
                this.handler.startElement("", "span", "span", attrs);
                conflictStarted = true;
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
        } else if (conflictStarted) {
            this.handler.endElement("", "span", "span");
            conflictStarted = false;
        }
        if (!node.getQName().equalsIgnoreCase("img") && !node.getQName().equalsIgnoreCase("body")) {
            this.handler.endElement("", node.getQName(), node.getQName());
        }
    }

    private void writeImage(ImageNode imgNode) throws SAXException {
        AttributesImpl attrs = imgNode.getAttributes();
        if (imgNode.getModification().getOutputType() == ModificationType.REMOVED) {
            attrs.addAttribute("", "changeType", "changeType", "CDATA", "diff-removed-image");
        } else if (imgNode.getModification().getOutputType() == ModificationType.ADDED) {
            attrs.addAttribute("", "changeType", "changeType", "CDATA", "diff-added-image");
        } else if (imgNode.getModification().getOutputType() == ModificationType.CONFLICT) {
            attrs.addAttribute("", "changeType", "changeType", "CDATA", "diff-conflict-image");
        }
        this.handler.startElement("", "img", "img", attrs);
        this.handler.endElement("", "img", "img");
    }

    private void addAttributes(Modification mod, AttributesImpl attrs) {
        if (mod.getOutputType() == ModificationType.CHANGED) {
            String changes = mod.getChanges();
            attrs.addAttribute("", "changes", "changes", "CDATA", changes);
        }
        String previous = mod.getPrevious() == null ? "first-" + this.prefix : (Object)((Object)mod.getPrevious().getOutputType()) + "-" + this.prefix + "-" + mod.getPrevious().getID();
        attrs.addAttribute("", "previous", "previous", "CDATA", previous);
        String changeId = (Object)((Object)mod.getOutputType()) + "-" + this.prefix + "-" + mod.getID();
        attrs.addAttribute("", "changeId", "changeId", "CDATA", changeId);
        String next = mod.getNext() == null ? "last-" + this.prefix : (Object)((Object)mod.getNext().getOutputType()) + "-" + this.prefix + "-" + mod.getNext().getID();
        attrs.addAttribute("", "next", "next", "CDATA", next);
    }
}

