/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class PreconditionValidator
extends ContextAwareBase {
    boolean valid = true;
    SaxEventInterpretationContext intercon;
    Attributes attributes;
    String tag;

    public PreconditionValidator(ContextAware origin, SaxEventInterpretationContext intercon, String name, Attributes attributes) {
        super(origin);
        this.setContext(origin.getContext());
        this.intercon = intercon;
        this.tag = name;
        this.attributes = attributes;
    }

    public PreconditionValidator validateZeroAttributes() {
        if (this.attributes == null) {
            return this;
        }
        if (this.attributes.getLength() != 0) {
            this.addError("Element [" + this.tag + "] should have no attributes, near line " + Action.getLineNumber(this.intercon));
            this.valid = false;
        }
        return this;
    }

    public PreconditionValidator validateClassAttribute() {
        return this.generic("class");
    }

    public PreconditionValidator validateNameAttribute() {
        return this.generic("name");
    }

    public PreconditionValidator validateValueAttribute() {
        return this.generic("value");
    }

    public PreconditionValidator validateRefAttribute() {
        return this.generic("ref");
    }

    public PreconditionValidator generic(String attributeName) {
        String attributeValue = this.attributes.getValue(attributeName);
        if (OptionHelper.isNullOrEmpty(attributeValue)) {
            this.addError("Missing attribute [" + attributeName + "] in element [" + this.tag + "] near line " + Action.getLineNumber(this.intercon));
            this.valid = false;
        }
        return this;
    }

    public boolean isValid() {
        return this.valid;
    }
}

