/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.filter.ContentFilter
 *  org.jdom.filter.Filter
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.DiffPostProcessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.Filter;

public class StripToContextDiffPostProcessor
implements DiffPostProcessor {
    private static final String CONTEXT_PLACEHOLDER_NAME = "contextplaceholderelement";
    private final String diffTargetBlockClass;
    private final String diffContextBlockClass;

    public StripToContextDiffPostProcessor(String diffTargetBlockClass, String diffContextBlockClass) {
        this.diffTargetBlockClass = diffTargetBlockClass;
        this.diffContextBlockClass = diffContextBlockClass;
    }

    @Override
    public Document process(Document document) {
        ArrayList<Element> contextBlocks = new ArrayList<Element>();
        Iterator descendants = document.getRootElement().getDescendants((Filter)new ContentFilter(1));
        while (descendants.hasNext()) {
            Element descendant = (Element)descendants.next();
            if (this.isKnownDescendant(contextBlocks, descendant)) continue;
            if (this.hasClass(descendant, this.diffTargetBlockClass) || this.hasClass(descendant, this.diffContextBlockClass)) {
                contextBlocks.add(descendant);
                continue;
            }
            if (!contextBlocks.isEmpty() && ((Element)contextBlocks.get(contextBlocks.size() - 1)).getName().equals(CONTEXT_PLACEHOLDER_NAME)) continue;
            contextBlocks.add(new Element(CONTEXT_PLACEHOLDER_NAME));
        }
        if (!this.hasDiffContent(contextBlocks)) {
            return document;
        }
        Element rootElement = new Element(document.getRootElement().getName(), document.getRootElement().getNamespace());
        Document strippedDoc = new Document(rootElement);
        for (Element context : contextBlocks) {
            if (context.getName().equals(CONTEXT_PLACEHOLDER_NAME)) {
                Element placeholderParagraph = new Element("p");
                placeholderParagraph.setAttribute("class", "diff-context-placeholder");
                placeholderParagraph.setText("...");
                rootElement.addContent((Content)placeholderParagraph);
                continue;
            }
            context.detach();
            rootElement.addContent((Content)context);
        }
        return strippedDoc;
    }

    private boolean hasClass(Element element, String clazz) {
        String classValues = element.getAttributeValue("class");
        if (classValues == null) {
            return false;
        }
        return classValues.equals(clazz) || classValues.startsWith(clazz) || classValues.endsWith(clazz) || classValues.contains(" " + clazz + " ");
    }

    private boolean isKnownDescendant(List<Element> knownBlocks, Element descendant) {
        for (Element el : knownBlocks) {
            if (!el.isAncestor(descendant)) continue;
            return true;
        }
        return false;
    }

    private boolean hasDiffContent(List<Element> contextBlocks) {
        if (contextBlocks.isEmpty()) {
            return false;
        }
        for (Element el : contextBlocks) {
            if (el.getName().equals(CONTEXT_PLACEHOLDER_NAME)) continue;
            return true;
        }
        return false;
    }
}

