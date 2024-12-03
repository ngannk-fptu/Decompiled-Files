/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.msv.grammar.trex.TREXGrammar
 *  com.sun.msv.reader.GrammarReaderController
 *  com.sun.msv.reader.trex.ng.RELAXNGReader
 *  com.sun.msv.reader.util.IgnoreController
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.BaseSchemaFactory;
import com.ctc.wstx.msv.RelaxNGSchema;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.util.IgnoreController;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.xml.sax.InputSource;

public class RelaxNGSchemaFactory
extends BaseSchemaFactory {
    protected final GrammarReaderController mDummyController = new IgnoreController();

    public RelaxNGSchemaFactory() {
        super("http://relaxng.org/ns/structure/0.9");
    }

    protected XMLValidationSchema loadSchema(InputSource src, Object sysRef) throws XMLStreamException {
        BaseSchemaFactory.MyGrammarController ctrl;
        SAXParserFactory saxFactory = RelaxNGSchemaFactory.getSaxFactory();
        TREXGrammar grammar = RELAXNGReader.parse((InputSource)src, (SAXParserFactory)saxFactory, (GrammarReaderController)(ctrl = new BaseSchemaFactory.MyGrammarController()));
        if (grammar == null) {
            String msg = "Failed to load RelaxNG schema from '" + sysRef + "'";
            String emsg = ctrl.mErrorMsg;
            if (emsg != null) {
                msg = msg + ": " + emsg;
            }
            throw new XMLStreamException(msg);
        }
        return new RelaxNGSchema(grammar);
    }
}

