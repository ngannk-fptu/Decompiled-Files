/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.validation.ValidationContext
 *  org.codehaus.stax2.validation.XMLValidationSchema
 *  org.codehaus.stax2.validation.XMLValidator
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.GenericMsvValidator;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.XSREDocDecl;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class W3CSchema
implements XMLValidationSchema {
    protected final XMLSchemaGrammar mGrammar;

    public W3CSchema(XMLSchemaGrammar grammar) {
        this.mGrammar = grammar;
    }

    public String getSchemaType() {
        return "http://www.w3.org/2001/XMLSchema";
    }

    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        XSREDocDecl dd = new XSREDocDecl(this.mGrammar);
        return new GenericMsvValidator(this, ctxt, dd);
    }
}

