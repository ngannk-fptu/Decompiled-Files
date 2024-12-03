/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceProvider
 */
package com.ctc.wstx.msv;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.msv.BaseSchemaFactory;
import com.ctc.wstx.msv.W3CSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.InputSource;

@ServiceProvider(value=XMLValidationSchemaFactory.class)
public class W3CSchemaFactory
extends BaseSchemaFactory {
    protected final GrammarReaderController mDummyController = new IgnoreController();

    public W3CSchemaFactory() {
        super("http://www.w3.org/2001/XMLSchema");
    }

    @Override
    protected XMLValidationSchema loadSchema(InputSource src, Object sysRef) throws XMLStreamException {
        BaseSchemaFactory.MyGrammarController ctrl;
        SAXParserFactory saxFactory = W3CSchemaFactory.getSaxFactory();
        XMLSchemaGrammar grammar = XMLSchemaReader.parse(src, saxFactory, (GrammarReaderController)(ctrl = new BaseSchemaFactory.MyGrammarController()));
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

