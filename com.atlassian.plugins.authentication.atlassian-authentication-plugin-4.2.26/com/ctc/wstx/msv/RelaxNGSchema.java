/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.GenericMsvValidator;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class RelaxNGSchema
implements XMLValidationSchema {
    protected final TREXGrammar mGrammar;

    public RelaxNGSchema(TREXGrammar grammar) {
        this.mGrammar = grammar;
    }

    @Override
    public String getSchemaType() {
        return "http://relaxng.org/ns/structure/0.9";
    }

    @Override
    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        REDocumentDeclaration dd = new REDocumentDeclaration(this.mGrammar);
        return new GenericMsvValidator(this, ctxt, dd);
    }
}

