/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import javax.xml.stream.XMLOutputFactory;
import org.apache.axiom.util.stax.dialect.StAXDialect;

public interface StAXWriterConfiguration {
    public static final StAXWriterConfiguration DEFAULT = new StAXWriterConfiguration(){

        public XMLOutputFactory configure(XMLOutputFactory factory, StAXDialect dialect) {
            return factory;
        }

        public String toString() {
            return "DEFAULT";
        }
    };

    public XMLOutputFactory configure(XMLOutputFactory var1, StAXDialect var2);
}

