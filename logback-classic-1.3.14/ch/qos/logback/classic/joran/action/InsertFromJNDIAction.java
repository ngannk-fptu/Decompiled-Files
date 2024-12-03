/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.action.BaseModelAction
 *  ch.qos.logback.core.joran.action.PreconditionValidator
 *  ch.qos.logback.core.joran.spi.SaxEventInterpretationContext
 *  ch.qos.logback.core.model.InsertFromJNDIModel
 *  ch.qos.logback.core.model.Model
 *  ch.qos.logback.core.spi.ContextAware
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.core.joran.action.BaseModelAction;
import ch.qos.logback.core.joran.action.PreconditionValidator;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.model.InsertFromJNDIModel;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.ContextAware;
import org.xml.sax.Attributes;

public class InsertFromJNDIAction
extends BaseModelAction {
    public static final String ENV_ENTRY_NAME_ATTR = "env-entry-name";
    public static final String AS_ATTR = "as";

    protected Model buildCurrentModel(SaxEventInterpretationContext interpretationContext, String name, Attributes attributes) {
        InsertFromJNDIModel ifjm = new InsertFromJNDIModel();
        ifjm.setEnvEntryName(attributes.getValue(ENV_ENTRY_NAME_ATTR));
        ifjm.setAs(attributes.getValue(AS_ATTR));
        ifjm.setScopeStr(attributes.getValue("scope"));
        return ifjm;
    }

    protected boolean validPreconditions(SaxEventInterpretationContext seic, String name, Attributes attributes) {
        PreconditionValidator validator = new PreconditionValidator((ContextAware)this, seic, name, attributes);
        validator.generic(ENV_ENTRY_NAME_ATTR);
        validator.generic(AS_ATTR);
        return validator.isValid();
    }
}

