/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceProvider
 */
package com.ctc.wstx.msv;

import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.msv.BaseSchemaFactory;
import com.ctc.wstx.msv.RelaxNGSchema;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.InputSource;

@ServiceProvider(value=XMLValidationSchemaFactory.class)
public class RelaxNGSchemaFactory
extends BaseSchemaFactory {
    protected final GrammarReaderController mDummyController = new IgnoreController();

    public RelaxNGSchemaFactory() {
        super("http://relaxng.org/ns/structure/0.9");
    }

    @Override
    protected XMLValidationSchema loadSchema(InputSource src, Object sysRef) throws XMLStreamException {
        BaseSchemaFactory.MyGrammarController ctrl;
        SAXParserFactory saxFactory = RelaxNGSchemaFactory.getSaxFactory();
        TREXGrammar grammar = RELAXNGReader.parse(src, saxFactory, (GrammarReaderController)(ctrl = new BaseSchemaFactory.MyGrammarController()));
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

