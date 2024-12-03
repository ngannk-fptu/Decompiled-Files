/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeStreamingValidator;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.StreamingValidatorImpl;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnionType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Vector;

public abstract class XSDatatypeImpl
implements XSDatatype {
    private final String namespaceUri;
    private final String typeName;
    public final WhiteSpaceProcessor whiteSpace;
    protected static final ValidationContext serializedValueChecker = new ValidationContext(){

        public boolean isNotation(String s) {
            return true;
        }

        public boolean isUnparsedEntity(String s) {
            return true;
        }

        public String resolveNamespacePrefix(String ns) {
            return "abc";
        }

        public String getBaseUri() {
            return null;
        }
    };
    public static final String ERR_INAPPROPRIATE_FOR_TYPE = "DataTypeErrorDiagnosis.InappropriateForType";
    public static final String ERR_TOO_MUCH_PRECISION = "DataTypeErrorDiagnosis.TooMuchPrecision";
    public static final String ERR_TOO_MUCH_SCALE = "DataTypeErrorDiagnosis.TooMuchScale";
    public static final String ERR_ENUMERATION = "DataTypeErrorDiagnosis.Enumeration";
    public static final String ERR_ENUMERATION_WITH_ARG = "DataTypeErrorDiagnosis.Enumeration.Arg";
    public static final String ERR_OUT_OF_RANGE = "DataTypeErrorDiagnosis.OutOfRange";
    public static final String ERR_LENGTH = "DataTypeErrorDiagnosis.Length";
    public static final String ERR_MINLENGTH = "DataTypeErrorDiagnosis.MinLength";
    public static final String ERR_MAXLENGTH = "DataTypeErrorDiagnosis.MaxLength";
    public static final String ERR_PATTERN_1 = "DataTypeErrorDiagnosis.Pattern.1";
    public static final String ERR_PATTERN_MANY = "DataTypeErrorDiagnosis.Pattern.Many";
    public static final String ERR_INVALID_ITEMTYPE = "BadTypeException.InvalidItemType";
    public static final String ERR_INVALID_MEMBER_TYPE = "BadTypeException.InvalidMemberType";
    public static final String ERR_INVALID_BASE_TYPE = "BadTypeException.InvalidBaseType";
    public static final String ERR_INVALID_WHITESPACE_VALUE = "WhiteSpaceProcessor.InvalidWhiteSpaceValue";
    public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
    public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE = "EnumerationFacet.InvalidValueForThisType";
    public static final String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER = "BadTypeException.FacetMustBeNonNegativeInteger";
    public static final String ERR_FACET_MUST_BE_POSITIVE_INTEGER = "BadTypeException.FacetMustBePositiveInteger";
    public static final String ERR_OVERRIDING_FIXED_FACET = "BadTypeException.OverridingFixedFacet";
    public static final String ERR_INCONSISTENT_FACETS_1 = "InconsistentFacets.1";
    public static final String ERR_INCONSISTENT_FACETS_2 = "InconsistentFacets.2";
    public static final String ERR_X_AND_Y_ARE_EXCLUSIVE = "XAndYAreExclusive";
    public static final String ERR_LOOSENED_FACET = "LoosenedFacet";
    public static final String ERR_SCALE_IS_GREATER_THAN_PRECISION = "PrecisionScaleFacet.ScaleIsGraterThanPrecision";
    public static final String ERR_DUPLICATE_FACET = "BadTypeException.DuplicateFacet";
    public static final String ERR_NOT_APPLICABLE_FACET = "BadTypeException.NotApplicableFacet";
    public static final String ERR_EMPTY_UNION = "BadTypeException.EmptyUnion";
    private static final long serialVersionUID = 1L;

    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public String getName() {
        return this.typeName;
    }

    protected XSDatatypeImpl(String uri, String typeName, WhiteSpaceProcessor whiteSpace) {
        this.namespaceUri = uri;
        this.typeName = typeName;
        this.whiteSpace = whiteSpace;
    }

    public final Object createValue(String lexicalValue, ValidationContext context) {
        return this._createValue(this.whiteSpace.process(lexicalValue), context);
    }

    protected abstract Object _createValue(String var1, ValidationContext var2);

    public final void checkValid(String content, ValidationContext context) throws DatatypeException {
        this._checkValid(this.whiteSpace.process(content), context);
    }

    protected abstract void _checkValid(String var1, ValidationContext var2) throws DatatypeException;

    public final Object createJavaObject(String literal, ValidationContext context) {
        return this._createJavaObject(this.whiteSpace.process(literal), context);
    }

    protected abstract Object _createJavaObject(String var1, ValidationContext var2);

    public final boolean isValid(String literal, ValidationContext context) {
        literal = this.whiteSpace.process(literal);
        if (this.needValueCheck()) {
            return this._createValue(literal, context) != null;
        }
        return this.checkFormat(literal, context);
    }

    public boolean isAlwaysValid() {
        return false;
    }

    public DatatypeStreamingValidator createStreamingValidator(ValidationContext context) {
        return new StreamingValidatorImpl(this, context);
    }

    protected abstract boolean checkFormat(String var1, ValidationContext var2);

    protected boolean needValueCheck() {
        return false;
    }

    public DataTypeWithFacet getFacetObject(String facetName) {
        XSDatatype dt = this.getBaseType();
        if (dt != null) {
            return dt.getFacetObject(facetName);
        }
        return null;
    }

    public String[] getApplicableFacetNames() {
        Vector<String> vec = new Vector<String>();
        String[] facetNames = new String[]{"enumeration", "fractionDigits", "totalDigits", "length", "minLength", "maxLength", "maxExclusive", "minExclusive", "maxInclusive", "minInclusive", "pattern", "whiteSpace"};
        for (int i = 0; i < facetNames.length; ++i) {
            if (this.isFacetApplicable(facetNames[i]) != 0) continue;
            vec.add(facetNames[i]);
        }
        return vec.toArray(new String[vec.size()]);
    }

    public abstract ConcreteType getConcreteType();

    public final boolean sameValue(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public final int valueHashCode(Object o) {
        return o.hashCode();
    }

    public final boolean isDerivedTypeOf(XSDatatype baseType, boolean restrictionAllowed) {
        return XSDatatypeImpl.isDerivedTypeOf(baseType, this, restrictionAllowed);
    }

    public static boolean isDerivedTypeOf(XSDatatype base, XSDatatype derived, boolean restrictionAllowed) {
        if (base == derived) {
            return true;
        }
        if (!restrictionAllowed) {
            return false;
        }
        if (base == SimpleURType.theInstance) {
            return true;
        }
        if (base.getVariety() == 3) {
            XSDatatype t = base;
            while (!(t instanceof UnionType)) {
                t = t.getBaseType();
            }
            XSDatatypeImpl[] memberTypes = ((UnionType)t).memberTypes;
            for (int i = 0; i < memberTypes.length; ++i) {
                if (!XSDatatypeImpl.isDerivedTypeOf(memberTypes[i], derived, restrictionAllowed)) continue;
                return true;
            }
        }
        while (derived != SimpleURType.theInstance) {
            if (base == derived) {
                return true;
            }
            derived = derived.getBaseType();
        }
        return false;
    }

    public XSDatatype getAncestorBuiltinType() {
        XSDatatype dt = this;
        while (!"http://www.w3.org/2001/XMLSchema".equals(dt.getNamespaceUri())) {
            dt = dt.getBaseType();
        }
        return dt;
    }

    public int getIdType() {
        return 0;
    }

    public boolean isContextDependent() {
        return false;
    }

    public static String localize(String prop, Object[] args) {
        return MessageFormat.format(ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.datatype.xsd.Messages").getString(prop), args);
    }

    public static String localize(String prop) {
        return XSDatatypeImpl.localize(prop, null);
    }

    public static String localize(String prop, Object arg1) {
        return XSDatatypeImpl.localize(prop, new Object[]{arg1});
    }

    public static String localize(String prop, Object arg1, Object arg2) {
        return XSDatatypeImpl.localize(prop, new Object[]{arg1, arg2});
    }

    public static String localize(String prop, Object arg1, Object arg2, Object arg3) {
        return XSDatatypeImpl.localize(prop, new Object[]{arg1, arg2, arg3});
    }
}

