/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.action.BaseModelAction
 *  ch.qos.logback.core.joran.action.PreconditionValidator
 *  ch.qos.logback.core.joran.spi.SaxEventInterpretationContext
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.spi.ContextAware
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.ReceiverModel;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAware;
import org.xml.sax.Attributes;

public class ReceiverAction
extends BaseModelAction {
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ReceiverModel rm = new ReceiverModel();
        rm.setClassName(attributes.getValue("class"));
        return rm;
    }

    protected boolean validPreconditions(SaxEventInterpretationContext seic, String name, Attributes attributes) {
        PreconditionValidator validator = new PreconditionValidator((ContextAware)this, seic, name, attributes);
        validator.validateClassAttribute();
        return validator.isValid();
    }
}

