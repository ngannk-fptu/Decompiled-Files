/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.util.stax.dialect.StAXDialect;

abstract class AbstractStAXDialect
implements StAXDialect {
    AbstractStAXDialect() {
    }

    public abstract XMLStreamReader normalize(XMLStreamReader var1);

    public abstract XMLStreamWriter normalize(XMLStreamWriter var1);
}

