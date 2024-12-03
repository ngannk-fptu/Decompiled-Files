/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public interface Validatable {
    public XMLValidator validateAgainst(XMLValidationSchema var1) throws XMLStreamException;

    public XMLValidator stopValidatingAgainst(XMLValidationSchema var1) throws XMLStreamException;

    public XMLValidator stopValidatingAgainst(XMLValidator var1) throws XMLStreamException;

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler var1);
}

