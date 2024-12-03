/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.JDOMException
 *  org.jdom.Text
 *  org.jdom.filter.ContentFilter
 *  org.jdom.filter.Filter
 *  org.jdom.xpath.XPath
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.DiffPostProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.Filter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextBlockMarkingDiffPostProcessor
implements DiffPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(ContextBlockMarkingDiffPostProcessor.class);
    private static final Set<String> BLOCK_ELEMENT_NAMES = new HashSet<String>(Arrays.asList("address", "blockquote", "center", "dir", "div", "dl", "fieldset", "form", "h1", "h2", "h3", "h4", "h5", "h6", "ol", "p", "pre", "table", "ul", "dd", "dl", "inline-task-list-ul"));
    private static final int PREFERRED_CONTEXT_SIZE = 25;
    private final String diffTargetBlockClass;
    private final String diffContextBlockClass;

    public ContextBlockMarkingDiffPostProcessor(String diffTargetBlockClass, String diffContextBlockClass) {
        this.diffTargetBlockClass = diffTargetBlockClass;
        this.diffContextBlockClass = diffContextBlockClass;
    }

    @Override
    public Document process(Document document) {
        ArrayList<Element> diffTargetBlocks = new ArrayList<Element>();
        ArrayList<Element> diffContextBlocks = new ArrayList<Element>();
        try {
            Iterator diffSpans = this.getDiffSpans(document);
            while (diffSpans.hasNext()) {
                Element span = (Element)diffSpans.next();
                Element closestBlock = this.getHighestBlock(span);
                if (closestBlock == null) continue;
                diffTargetBlocks.add(closestBlock);
                ContextCount contextCount = this.getContextCount(closestBlock, span);
                Element previousBlock = this.getPreviousSiblingBlock(closestBlock);
                for (int beforeCount = contextCount.getBeforeCount(); beforeCount < 25 && previousBlock != null; beforeCount += this.getFullTextSize(previousBlock)) {
                    diffContextBlocks.add(previousBlock);
                    previousBlock = this.getPreviousSiblingBlock(previousBlock);
                }
                Element nextBlock = this.getNextSiblingBlock(closestBlock);
                for (int afterCount = contextCount.getAfterCount(); afterCount < 25 && nextBlock != null; afterCount += this.getFullTextSize(nextBlock)) {
                    diffContextBlocks.add(nextBlock);
                    nextBlock = this.getNextSiblingBlock(nextBlock);
                }
            }
        }
        catch (JDOMException ex) {
            log.warn("There was an exception while calculating context for displaying the diff.", (Throwable)ex);
            return document;
        }
        for (Element target : diffTargetBlocks) {
            this.appendClassValue(target, this.diffTargetBlockClass);
        }
        for (Element context : diffContextBlocks) {
            this.appendClassValue(context, this.diffContextBlockClass);
        }
        return document;
    }

    private Iterator getDiffSpans(Document document) throws JDOMException {
        XPath diffSpans = XPath.newInstance((String)"//span[@data-daisydiff-change-id]");
        List selectedNodes = diffSpans.selectNodes((Object)document);
        if (selectedNodes != null) {
            return selectedNodes.iterator();
        }
        return Collections.emptyIterator();
    }

    private Element getHighestBlock(Element node) {
        Element block = null;
        Element nextBlock = this.getClosestBlock(node);
        while (nextBlock != null) {
            block = nextBlock;
            nextBlock = this.getClosestBlock(block);
        }
        return block;
    }

    private Element getClosestBlock(Element node) {
        Element parentElement;
        for (parentElement = node.getParentElement(); parentElement != null && !BLOCK_ELEMENT_NAMES.contains(parentElement.getName()); parentElement = parentElement.getParentElement()) {
        }
        return parentElement;
    }

    private void appendClassValue(Element element, String clazz) {
        String classValues = element.getAttributeValue("class");
        if (classValues == null) {
            element.setAttribute("class", clazz);
        } else if (!(classValues.equals(clazz) || classValues.startsWith(clazz) || classValues.endsWith(clazz) || classValues.contains(" " + clazz + " "))) {
            element.setAttribute("class", classValues + " " + clazz);
        }
    }

    private ContextCount getContextCount(Element ancestor, Element diffSpan) {
        int beforeCount = 0;
        int afterCount = 0;
        boolean diffSpanReached = false;
        Iterator contents = ancestor.getDescendants();
        while (contents.hasNext()) {
            Content content = (Content)contents.next();
            if (content == diffSpan) {
                diffSpanReached = true;
                continue;
            }
            if (diffSpanReached && diffSpan.getContent().contains(content) || !(content instanceof Text)) continue;
            int size = ((Text)content).getTextNormalize().length();
            if (diffSpanReached) {
                afterCount += size;
                continue;
            }
            beforeCount += size;
        }
        return new ContextCount(beforeCount, afterCount);
    }

    private int getFullTextSize(Element element) {
        int size = 0;
        Iterator descendants = element.getDescendants((Filter)new ContentFilter(4));
        while (descendants.hasNext()) {
            Text textNode = (Text)descendants.next();
            size += StringUtils.trim((String)textNode.getTextNormalize()).length();
        }
        return size;
    }

    private Element getPreviousSiblingBlock(Element node) {
        Element parent = node.getParentElement();
        int myIndex = parent.indexOf((Content)node);
        List contents = parent.getContent();
        Content sibling = null;
        Element previousSiblingBlock = null;
        while (myIndex > 0) {
            if (!((sibling = (Content)contents.get(--myIndex)) instanceof Element) || !BLOCK_ELEMENT_NAMES.contains(((Element)sibling).getName())) continue;
            previousSiblingBlock = (Element)sibling;
            break;
        }
        return previousSiblingBlock;
    }

    private Element getNextSiblingBlock(Element node) {
        Element parent = node.getParentElement();
        int myIndex = parent.indexOf((Content)node);
        List contents = parent.getContent();
        Content sibling = null;
        Element nextSiblingBlock = null;
        while (myIndex < contents.size() - 1) {
            if (!((sibling = (Content)contents.get(++myIndex)) instanceof Element) || !BLOCK_ELEMENT_NAMES.contains(((Element)sibling).getName())) continue;
            nextSiblingBlock = (Element)sibling;
            break;
        }
        return nextSiblingBlock;
    }

    private static class ContextCount {
        private int beforeCount;
        private int afterCount;

        public ContextCount(int beforeCount, int afterCount) {
            this.beforeCount = beforeCount;
            this.afterCount = afterCount;
        }

        public int getBeforeCount() {
            return this.beforeCount;
        }

        public int getAfterCount() {
            return this.afterCount;
        }
    }
}

