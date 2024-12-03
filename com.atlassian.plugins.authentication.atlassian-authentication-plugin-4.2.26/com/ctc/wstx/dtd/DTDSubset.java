/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.dtd.DTDElement;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.PrefixedName;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class DTDSubset
implements DTDValidationSchema {
    protected DTDSubset() {
    }

    public abstract DTDSubset combineWithExternalSubset(InputProblemReporter var1, DTDSubset var2) throws XMLStreamException;

    @Override
    public abstract XMLValidator createValidator(ValidationContext var1) throws XMLStreamException;

    @Override
    public String getSchemaType() {
        return "http://www.w3.org/XML/1998/namespace";
    }

    @Override
    public abstract int getEntityCount();

    @Override
    public abstract int getNotationCount();

    public abstract boolean isCachable();

    public abstract boolean isReusableWith(DTDSubset var1);

    public abstract HashMap<String, EntityDecl> getGeneralEntityMap();

    public abstract List<EntityDecl> getGeneralEntityList();

    public abstract HashMap<String, EntityDecl> getParameterEntityMap();

    public abstract HashMap<String, NotationDeclaration> getNotationMap();

    public abstract List<NotationDeclaration> getNotationList();

    public abstract HashMap<PrefixedName, DTDElement> getElementMap();
}

