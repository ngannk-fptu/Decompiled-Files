/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import java.util.Iterator;
import org.xmlpull.v1.builder.XmlContainer;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlProcessingInstruction;

public interface XmlDoctype
extends XmlContainer {
    public String getSystemIdentifier();

    public String getPublicIdentifier();

    public Iterator children();

    public XmlDocument getParent();

    public XmlProcessingInstruction addProcessingInstruction(String var1, String var2);

    public void removeAllProcessingInstructions();
}

