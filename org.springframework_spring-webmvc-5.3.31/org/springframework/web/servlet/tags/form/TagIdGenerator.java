/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.PageContext
 */
package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.PageContext;

abstract class TagIdGenerator {
    private static final String PAGE_CONTEXT_ATTRIBUTE_PREFIX = TagIdGenerator.class.getName() + ".";

    TagIdGenerator() {
    }

    public static String nextId(String name, PageContext pageContext) {
        String attributeName = PAGE_CONTEXT_ATTRIBUTE_PREFIX + name;
        Integer currentCount = (Integer)pageContext.getAttribute(attributeName);
        currentCount = currentCount != null ? currentCount + 1 : 1;
        pageContext.setAttribute(attributeName, (Object)currentCount);
        return name + currentCount;
    }
}

