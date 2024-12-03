/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.action.BaseModelAction
 *  ch.qos.logback.core.joran.spi.SaxEventInterpretationContext
 *  ch.qos.logback.core.model.Model
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.model.ContextNameModel;
import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.Model;
import org.xml.sax.Attributes;

public class ContextNameAction
extends BaseModelAction {
    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        ContextNameModel contextNameModel = new ContextNameModel();
        return contextNameModel;
    }
}

