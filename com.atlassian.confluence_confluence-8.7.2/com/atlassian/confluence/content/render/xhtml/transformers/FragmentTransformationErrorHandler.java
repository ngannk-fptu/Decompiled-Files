/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import javax.xml.stream.XMLEventReader;

public interface FragmentTransformationErrorHandler {
    public String handle(XMLEventReader var1, Exception var2);
}

