/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import java.io.Serializable;

public interface XSDatatype
extends Serializable,
DatabindableDatatype {
    public static final int VARIETY_ATOMIC = 1;
    public static final int VARIETY_LIST = 2;
    public static final int VARIETY_UNION = 3;
    public static final int DERIVATION_BY_RESTRICTION = 1;
    public static final int DERIVATION_BY_LIST = 2;
    public static final int DERIVATION_BY_UNION = 4;
    public static final int APPLICABLE = 0;
    public static final int FIXED = -1;
    public static final int NOT_ALLOWED = -2;
    public static final String FACET_LENGTH = "length";
    public static final String FACET_MINLENGTH = "minLength";
    public static final String FACET_MAXLENGTH = "maxLength";
    public static final String FACET_PATTERN = "pattern";
    public static final String FACET_ENUMERATION = "enumeration";
    public static final String FACET_TOTALDIGITS = "totalDigits";
    public static final String FACET_FRACTIONDIGITS = "fractionDigits";
    public static final String FACET_MININCLUSIVE = "minInclusive";
    public static final String FACET_MAXINCLUSIVE = "maxInclusive";
    public static final String FACET_MINEXCLUSIVE = "minExclusive";
    public static final String FACET_MAXEXCLUSIVE = "maxExclusive";
    public static final String FACET_WHITESPACE = "whiteSpace";
    public static final String XMLSCHEMA_NSURI = "http://www.w3.org/2001/XMLSchema";

    public String getName();

    public String getNamespaceUri();

    public String displayName();

    public String convertToLexicalValue(Object var1, SerializationContext var2) throws IllegalArgumentException;

    public int getVariety();

    public boolean isFinal(int var1);

    public int isFacetApplicable(String var1);

    public String[] getApplicableFacetNames();

    public DataTypeWithFacet getFacetObject(String var1);

    public XSDatatype getBaseType();

    public XSDatatype getAncestorBuiltinType();

    public boolean isDerivedTypeOf(XSDatatype var1, boolean var2);

    public boolean isAlwaysValid();
}

