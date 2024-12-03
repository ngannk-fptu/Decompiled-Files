/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.filter.AbstractFilter
 *  org.jdom.filter.Filter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.DiffPostProcessor;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.AbstractFilter;
import org.jdom.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StripEmptySpansDiffPostProcessor
implements DiffPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(StripEmptySpansDiffPostProcessor.class);

    @Override
    public Document process(Document document) {
        Iterator descendants = document.getRootElement().getDescendants((Filter)new EmptySpanFilter());
        ImmutableList spansToRemove = ImmutableList.copyOf((Iterator)descendants);
        for (Element element : spansToRemove) {
            Element parentElement = element.getParentElement();
            parentElement.removeContent((Content)element);
            document.removeContent((Content)element);
        }
        return document;
    }

    private static class EmptySpanFilter
    extends AbstractFilter {
        private EmptySpanFilter() {
        }

        public boolean matches(Object o) {
            return o instanceof Element && this.isEmptySpan((Element)o);
        }

        private boolean isEmptySpan(Element element) {
            return element.getContentSize() == 0 && "span".equals(element.getName());
        }
    }
}

