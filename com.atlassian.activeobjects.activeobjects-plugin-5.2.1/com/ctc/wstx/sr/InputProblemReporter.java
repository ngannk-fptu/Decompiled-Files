/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public interface InputProblemReporter {
    public void throwParseError(String var1) throws XMLStreamException;

    public void throwParseError(String var1, Object var2, Object var3) throws XMLStreamException;

    public void reportValidationProblem(XMLValidationProblem var1) throws XMLStreamException;

    public void reportValidationProblem(String var1) throws XMLStreamException;

    public void reportValidationProblem(String var1, Object var2, Object var3) throws XMLStreamException;

    public void reportProblem(Location var1, String var2, String var3, Object var4, Object var5) throws XMLStreamException;

    public Location getLocation();
}

