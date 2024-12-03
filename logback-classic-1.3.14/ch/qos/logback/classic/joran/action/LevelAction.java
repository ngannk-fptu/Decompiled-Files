/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.action.Action
 *  ch.qos.logback.core.joran.action.BaseModelAction
 *  ch.qos.logback.core.joran.action.PreconditionValidator
 *  ch.qos.logback.core.joran.spi.SaxEventInterpretationContext
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.spi.ContextAware
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.LevelModel;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAware;
import org.xml.sax.Attributes;

public class LevelAction
extends BaseModelAction {
    protected boolean validPreconditions(SaxEventInterpretationContext interpcont, String name, Attributes attributes) {
        PreconditionValidator pv = new PreconditionValidator((ContextAware)this, interpcont, name, attributes);
        pv.validateValueAttribute();
        this.addWarn("<level> element is deprecated. Near [" + name + "] on line " + Action.getLineNumber((SaxEventInterpretationContext)interpcont));
        this.addWarn("Please use \"level\" attribute within <logger> or <root> elements instead.");
        return pv.isValid();
    }

    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        LevelModel lm = new LevelModel();
        String value = attributes.getValue("value");
        lm.setValue(value);
        return lm;
    }
}

