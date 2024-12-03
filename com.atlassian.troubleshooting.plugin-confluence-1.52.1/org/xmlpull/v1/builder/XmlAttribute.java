/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;

public interface XmlAttribute
extends Cloneable {
    public Object clone() throws CloneNotSupportedException;

    public XmlElement getOwner();

    public String getNamespaceName();

    public XmlNamespace getNamespace();

    public String getName();

    public String getValue();

    public String getType();

    public boolean isSpecified();
}

