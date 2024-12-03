/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.ValidationEvent
 *  javax.xml.bind.ValidationEventHandler
 *  javax.xml.bind.ValidationEventLocator
 */
package org.hibernate.boot.jaxb.internal;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

class ContextProvidingValidationEventHandler
implements ValidationEventHandler {
    private int lineNumber;
    private int columnNumber;
    private String message;

    ContextProvidingValidationEventHandler() {
    }

    public boolean handleEvent(ValidationEvent validationEvent) {
        ValidationEventLocator locator = validationEvent.getLocator();
        this.lineNumber = locator.getLineNumber();
        this.columnNumber = locator.getColumnNumber();
        this.message = validationEvent.getMessage();
        return false;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public String getMessage() {
        return this.message;
    }
}

