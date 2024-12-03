/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import org.xmlpull.v1.builder.XmlNotation;

public interface XmlUnparsedEntity {
    public String getName();

    public String getSystemIdentifier();

    public String getPublicIdentifier();

    public String getDeclarationBaseUri();

    public String getNotationName();

    public XmlNotation getNotation();
}

