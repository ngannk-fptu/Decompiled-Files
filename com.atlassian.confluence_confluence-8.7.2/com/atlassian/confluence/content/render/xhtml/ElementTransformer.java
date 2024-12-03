/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

public interface ElementTransformer {
    public Set<QName> getHandledElementNames();

    public StartElement transform(StartElement var1);

    public EndElement transform(EndElement var1);
}

