/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.util.HashMap;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;
import org.apache.xerces.jaxp.validation.ValidatorHandlerImpl;
import org.apache.xerces.jaxp.validation.ValidatorImpl;
import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;

abstract class AbstractXMLSchema
extends Schema
implements XSGrammarPoolContainer {
    private final HashMap fFeatures = new HashMap();

    @Override
    public final Validator newValidator() {
        return new ValidatorImpl(this);
    }

    @Override
    public final ValidatorHandler newValidatorHandler() {
        return new ValidatorHandlerImpl(this);
    }

    @Override
    public final Boolean getFeature(String string) {
        return (Boolean)this.fFeatures.get(string);
    }

    final void setFeature(String string, boolean bl) {
        this.fFeatures.put(string, bl ? Boolean.TRUE : Boolean.FALSE);
    }
}

