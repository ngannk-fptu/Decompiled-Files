/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public interface StAXDialect {
    public String getName();

    public XMLInputFactory enableCDataReporting(XMLInputFactory var1);

    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory var1);

    public XMLInputFactory makeThreadSafe(XMLInputFactory var1);

    public XMLOutputFactory makeThreadSafe(XMLOutputFactory var1);

    public XMLInputFactory normalize(XMLInputFactory var1);

    public XMLOutputFactory normalize(XMLOutputFactory var1);
}

