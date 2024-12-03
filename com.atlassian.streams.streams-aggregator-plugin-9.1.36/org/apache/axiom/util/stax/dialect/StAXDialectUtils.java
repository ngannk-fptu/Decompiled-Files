/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import org.apache.axiom.util.stax.dialect.DisallowDoctypeDeclInputFactoryWrapper;

class StAXDialectUtils {
    StAXDialectUtils() {
    }

    public static XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        return new DisallowDoctypeDeclInputFactoryWrapper(factory);
    }
}

