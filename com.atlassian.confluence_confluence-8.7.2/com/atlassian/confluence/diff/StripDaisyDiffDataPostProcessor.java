/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.filter.ContentFilter
 *  org.jdom.filter.Filter
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.DiffPostProcessor;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.Filter;

public class StripDaisyDiffDataPostProcessor
implements DiffPostProcessor {
    @Override
    public Document process(Document document) {
        Iterator descendants = document.getRootElement().getDescendants((Filter)new ContentFilter(1));
        while (descendants.hasNext()) {
            Element descendant = (Element)descendants.next();
            for (Attribute attr : new ArrayList(descendant.getAttributes())) {
                if (!attr.getName().startsWith("data-daisydiff")) continue;
                descendant.removeAttribute(attr);
            }
        }
        return document;
    }
}

