/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.msv.grammar.xmlschema.XMLSchemaGrammar
 *  com.sun.msv.reader.GrammarReaderController
 *  com.sun.msv.reader.util.IgnoreController
 *  com.sun.msv.reader.xmlschema.XMLSchemaReader
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.BaseSchemaFactory;
import com.ctc.wstx.msv.W3CSchema;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.xml.sax.InputSource;

public class W3CSchemaFactory
extends BaseSchemaFactory {
    protected final GrammarReaderController mDummyController = new IgnoreController();

    public W3CSchemaFactory() {
        super("http://relaxng.org/ns/structure/0.9");
    }

    protected XMLValidationSchema loadSchema(InputSource src, Object sysRef) throws XMLStreamException {
        BaseSchemaFactory.MyGrammarController ctrl;
        SAXParserFactory saxFactory = W3CSchemaFactory.getSaxFactory();
        XMLSchemaGrammar grammar = XMLSchemaReader.parse((InputSource)src, (SAXParserFactory)saxFactory, (GrammarReaderController)(ctrl = new BaseSchemaFactory.MyGrammarController()));
        if (grammar == null) {
            String msg = "Failed to load W3C Schema from '" + sysRef + "'";
            String emsg = ctrl.mErrorMsg;
            if (emsg != null) {
                msg = msg + ": " + emsg;
            }
            throw new XMLStreamException(msg);
        }
        return new W3CSchema(grammar);
    }
}

