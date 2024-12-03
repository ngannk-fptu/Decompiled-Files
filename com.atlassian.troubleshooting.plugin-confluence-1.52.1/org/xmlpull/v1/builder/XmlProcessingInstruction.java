/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlNotation;

public interface XmlProcessingInstruction {
    public String getTarget();

    public String getContent();

    public String getBaseUri();

    public XmlNotation getNotation();

    public XmlContainer getParent();
}

