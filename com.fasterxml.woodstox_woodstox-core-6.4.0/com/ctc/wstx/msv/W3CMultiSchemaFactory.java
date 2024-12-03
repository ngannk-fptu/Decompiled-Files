/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.validation.XMLValidationSchema
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.W3CSchema;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.EmbeddedSchema;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.MultiSchemaReader;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.WSDLGrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

public class W3CMultiSchemaFactory {
    private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();

    public W3CMultiSchemaFactory() {
        this.parserFactory.setNamespaceAware(true);
    }

    public XMLValidationSchema createSchema(String baseURI, Map<String, Source> schemaSources) throws XMLStreamException {
        HashMap<String, EmbeddedSchema> embeddedSources = new HashMap<String, EmbeddedSchema>();
        for (Map.Entry<String, Source> source : schemaSources.entrySet()) {
            if (!(source.getValue() instanceof DOMSource)) continue;
            Node nd = ((DOMSource)source.getValue()).getNode();
            Object el = null;
            if (nd instanceof Element) {
                el = (Element)nd;
            } else if (nd instanceof Document) {
                el = ((Document)nd).getDocumentElement();
            }
            embeddedSources.put(source.getKey(), new EmbeddedSchema(source.getValue().getSystemId(), (Element)el));
        }
        WSDLGrammarReaderController ctrl = new WSDLGrammarReaderController(null, baseURI, embeddedSources);
        RecursiveAllowedXMLSchemaReader xmlSchemaReader = new RecursiveAllowedXMLSchemaReader(ctrl, this.parserFactory);
        MultiSchemaReader multiSchemaReader = new MultiSchemaReader(xmlSchemaReader);
        for (Source source : schemaSources.values()) {
            multiSchemaReader.parse(source);
        }
        XMLSchemaGrammar grammar = multiSchemaReader.getResult();
        if (grammar == null) {
            throw new XMLStreamException("Failed to load schemas");
        }
        return new W3CSchema(grammar);
    }

    static class RecursiveAllowedXMLSchemaReader
    extends XMLSchemaReader {
        Set<String> sysIds = new TreeSet<String>();

        RecursiveAllowedXMLSchemaReader(GrammarReaderController controller, SAXParserFactory parserFactory) {
            super(controller, parserFactory, new XMLSchemaReader.StateFactory(){

                @Override
                public State schemaHead(String expectedNamespace) {
                    return new SchemaState(expectedNamespace){
                        private XMLSchemaSchema old;

                        @Override
                        protected void endSelf() {
                            super.endSelf();
                            RecursiveAllowedXMLSchemaReader r = (RecursiveAllowedXMLSchemaReader)this.reader;
                            r.currentSchema = this.old;
                        }

                        @Override
                        protected void onTargetNamespaceResolved(String targetNs, boolean ignoreContents) {
                            RecursiveAllowedXMLSchemaReader r = (RecursiveAllowedXMLSchemaReader)this.reader;
                            this.old = r.currentSchema;
                            r.currentSchema = r.getOrCreateSchema(targetNs);
                            if (ignoreContents) {
                                return;
                            }
                            if (!r.isSchemaDefined(r.currentSchema)) {
                                r.markSchemaAsDefined(r.currentSchema);
                            }
                        }
                    };
                }
            }, new ExpressionPool());
        }

        @Override
        public void setLocator(Locator locator) {
            if (locator == null && this.getLocator() != null && this.getLocator().getSystemId() != null) {
                this.sysIds.add(this.getLocator().getSystemId());
            }
            super.setLocator(locator);
        }

        @Override
        public void switchSource(Source source, State newState) {
            String url = source.getSystemId();
            if (url != null && this.sysIds.contains(url)) {
                return;
            }
            super.switchSource(source, newState);
        }
    }
}

