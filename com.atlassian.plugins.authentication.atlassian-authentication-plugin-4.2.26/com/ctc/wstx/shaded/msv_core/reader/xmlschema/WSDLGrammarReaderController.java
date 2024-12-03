/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.DOMLSInputImpl;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.EmbeddedSchema;
import java.util.Map;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WSDLGrammarReaderController
implements GrammarReaderController2,
LSResourceResolver {
    private GrammarReaderController2 nextController;
    private Map<String, EmbeddedSchema> schemas;
    private String baseURI;

    public WSDLGrammarReaderController(GrammarReaderController2 nextController, String baseURI, Map<String, EmbeddedSchema> sources) {
        this.nextController = nextController;
        this.baseURI = baseURI;
        this.schemas = sources;
    }

    @Override
    public void error(Locator[] locs, String msg, Exception nestedException) {
        if (this.nextController != null) {
            this.nextController.error(locs, msg, nestedException);
        }
    }

    @Override
    public void warning(Locator[] locs, String errorMessage) {
        if (this.nextController != null) {
            this.nextController.warning(locs, errorMessage);
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        return null;
    }

    @Override
    public LSResourceResolver getLSResourceResolver() {
        return this;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        EmbeddedSchema schema = this.schemas.get(namespaceURI);
        if (schema != null) {
            return new DOMLSInputImpl(this.baseURI, schema.getSystemId(), schema.getSchemaElement());
        }
        return null;
    }
}

