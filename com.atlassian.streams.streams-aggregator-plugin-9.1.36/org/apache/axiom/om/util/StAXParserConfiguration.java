/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.util.stax.dialect.StAXDialect;

public interface StAXParserConfiguration {
    public static final StAXParserConfiguration DEFAULT = new StAXParserConfiguration(){

        public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
            return factory;
        }

        public String toString() {
            return "DEFAULT";
        }
    };
    public static final StAXParserConfiguration STANDALONE = new StAXParserConfiguration(){

        public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
            factory.setXMLResolver(new XMLResolver(){

                public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
                    return new ByteArrayInputStream(new byte[0]);
                }
            });
            return factory;
        }

        public String toString() {
            return "STANDALONE";
        }
    };
    public static final StAXParserConfiguration NON_COALESCING = new StAXParserConfiguration(){

        public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
            factory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
            return factory;
        }

        public String toString() {
            return "NON_COALESCING";
        }
    };
    public static final StAXParserConfiguration PRESERVE_CDATA_SECTIONS = new StAXParserConfiguration(){

        public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
            return dialect.enableCDataReporting(factory);
        }

        public String toString() {
            return "PRESERVE_CDATA_SECTIONS";
        }
    };
    public static final StAXParserConfiguration SOAP = new StAXParserConfiguration(){

        public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
            return dialect.disallowDoctypeDecl(factory);
        }

        public String toString() {
            return "SOAP";
        }
    };

    public XMLInputFactory configure(XMLInputFactory var1, StAXDialect var2);
}

