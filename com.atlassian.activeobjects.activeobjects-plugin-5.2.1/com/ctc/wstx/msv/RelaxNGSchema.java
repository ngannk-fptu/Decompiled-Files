/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.msv.grammar.Grammar
 *  com.sun.msv.grammar.trex.TREXGrammar
 *  com.sun.msv.verifier.DocumentDeclaration
 *  com.sun.msv.verifier.regexp.REDocumentDeclaration
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.GenericMsvValidator;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
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

    public String getSchemaType() {
        return "http://relaxng.org/ns/structure/0.9";
    }

    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        REDocumentDeclaration dd = new REDocumentDeclaration((Grammar)this.mGrammar);
        return new GenericMsvValidator(this, ctxt, (DocumentDeclaration)dd);
    }
}

