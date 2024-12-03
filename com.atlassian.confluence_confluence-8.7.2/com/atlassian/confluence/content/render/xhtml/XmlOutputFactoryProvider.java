/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;

public interface XmlOutputFactoryProvider {
    public XmlOutputFactory getXmlOutputFactory();

    public XmlOutputFactory getXmlFragmentOutputFactory();
}

