/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class DTDSubset
implements DTDValidationSchema {
    protected DTDSubset() {
    }

    public abstract DTDSubset combineWithExternalSubset(InputProblemReporter var1, DTDSubset var2) throws XMLStreamException;

    public abstract XMLValidator createValidator(ValidationContext var1) throws XMLStreamException;

    public String getSchemaType() {
        return "http://www.w3.org/XML/1998/namespace";
    }

    public abstract int getEntityCount();

    public abstract int getNotationCount();

    public abstract boolean isCachable();

    public abstract boolean isReusableWith(DTDSubset var1);

    public abstract HashMap getGeneralEntityMap();

    public abstract List getGeneralEntityList();

    public abstract HashMap getParameterEntityMap();

    public abstract HashMap getNotationMap();

    public abstract List getNotationList();

    public abstract HashMap getElementMap();
}

