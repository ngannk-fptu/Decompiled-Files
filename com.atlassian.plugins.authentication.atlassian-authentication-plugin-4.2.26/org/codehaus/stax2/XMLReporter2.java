/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2;

import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public interface XMLReporter2
extends XMLReporter {
    public void report(XMLValidationProblem var1) throws XMLStreamException;
}

