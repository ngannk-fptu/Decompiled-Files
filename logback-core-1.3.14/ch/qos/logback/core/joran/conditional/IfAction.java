/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.conditional.IfModel;
import org.xml.sax.Attributes;

public class IfAction
extends BaseModelAction {
    public static final String CONDITION_ATTRIBUTE = "condition";

    @Override
    protected boolean validPreconditions(SaxEventInterpretationContext interpcont, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator(this, interpcont, name, attributes);
        pv.generic(CONDITION_ATTRIBUTE);
        return pv.isValid();
    }

    @Override
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        IfModel ifModel = new IfModel();
        String condition = attributes.getValue(CONDITION_ATTRIBUTE);
        ifModel.setCondition(condition);
        return ifModel;
    }
}

