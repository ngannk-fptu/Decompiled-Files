/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import org.codehaus.stax2.validation.XMLValidationException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public interface ValidationProblemHandler {
    public void reportProblem(XMLValidationProblem var1) throws XMLValidationException;
}

