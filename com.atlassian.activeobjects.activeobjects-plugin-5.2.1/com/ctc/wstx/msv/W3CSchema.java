/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.msv.grammar.xmlschema.XMLSchemaGrammar
 *  com.sun.msv.verifier.DocumentDeclaration
 *  com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.msv.GenericMsvValidator;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;
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
        return new GenericMsvValidator(this, ctxt, (DocumentDeclaration)dd);
    }
}

