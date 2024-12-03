/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.AnyAtomicDV;
import org.apache.xerces.impl.dv.xs.AnySimpleDV;
import org.apache.xerces.impl.dv.xs.AnyURIDV;
import org.apache.xerces.impl.dv.xs.Base64BinaryDV;
import org.apache.xerces.impl.dv.xs.BooleanDV;
import org.apache.xerces.impl.dv.xs.DateDV;
import org.apache.xerces.impl.dv.xs.DateTimeDV;
import org.apache.xerces.impl.dv.xs.DayDV;
import org.apache.xerces.impl.dv.xs.DayTimeDurationDV;
import org.apache.xerces.impl.dv.xs.DecimalDV;
import org.apache.xerces.impl.dv.xs.DoubleDV;
import org.apache.xerces.impl.dv.xs.DurationDV;
import org.apache.xerces.impl.dv.xs.EntityDV;
import org.apache.xerces.impl.dv.xs.FloatDV;
import org.apache.xerces.impl.dv.xs.HexBinaryDV;
import org.apache.xerces.impl.dv.xs.IDDV;
import org.apache.xerces.impl.dv.xs.IDREFDV;
import org.apache.xerces.impl.dv.xs.IntegerDV;
import org.apache.xerces.impl.dv.xs.ListDV;
import org.apache.xerces.impl.dv.xs.MonthDV;
import org.apache.xerces.impl.dv.xs.MonthDayDV;
import org.apache.xerces.impl.dv.xs.PrecisionDecimalDV;
import org.apache.xerces.impl.dv.xs.QNameDV;
import org.apache.xerces.impl.dv.xs.StringDV;
import org.apache.xerces.impl.dv.xs.TimeDV;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.impl.dv.xs.UnionDV;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDelegate;
import org.apache.xerces.impl.dv.xs.YearDV;
import org.apache.xerces.impl.dv.xs.YearMonthDV;
import org.apache.xerces.impl.dv.xs.YearMonthDurationDV;
import org.apache.xerces.impl.xpath.regex.RegularExpression;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.impl.xs.util.ShortListImpl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.w3c.dom.TypeInfo;

public class XSSimpleTypeDecl
implements XSSimpleType,
TypeInfo {
    protected static final short DV_STRING = 1;
    protected static final short DV_BOOLEAN = 2;
    protected static final short DV_DECIMAL = 3;
    protected static final short DV_FLOAT = 4;
    protected static final short DV_DOUBLE = 5;
    protected static final short DV_DURATION = 6;
    protected static final short DV_DATETIME = 7;
    protected static final short DV_TIME = 8;
    protected static final short DV_DATE = 9;
    protected static final short DV_GYEARMONTH = 10;
    protected static final short DV_GYEAR = 11;
    protected static final short DV_GMONTHDAY = 12;
    protected static final short DV_GDAY = 13;
    protected static final short DV_GMONTH = 14;
    protected static final short DV_HEXBINARY = 15;
    protected static final short DV_BASE64BINARY = 16;
    protected static final short DV_ANYURI = 17;
    protected static final short DV_QNAME = 18;
    protected static final short DV_PRECISIONDECIMAL = 19;
    protected static final short DV_NOTATION = 20;
    protected static final short DV_ANYSIMPLETYPE = 0;
    protected static final short DV_ID = 21;
    protected static final short DV_IDREF = 22;
    protected static final short DV_ENTITY = 23;
    protected static final short DV_INTEGER = 24;
    protected static final short DV_LIST = 25;
    protected static final short DV_UNION = 26;
    protected static final short DV_YEARMONTHDURATION = 27;
    protected static final short DV_DAYTIMEDURATION = 28;
    protected static final short DV_ANYATOMICTYPE = 29;
    private static final TypeValidator[] gDVs = new TypeValidator[]{new AnySimpleDV(), new StringDV(), new BooleanDV(), new DecimalDV(), new FloatDV(), new DoubleDV(), new DurationDV(), new DateTimeDV(), new TimeDV(), new DateDV(), new YearMonthDV(), new YearDV(), new MonthDayDV(), new DayDV(), new MonthDV(), new HexBinaryDV(), new Base64BinaryDV(), new AnyURIDV(), new QNameDV(), new PrecisionDecimalDV(), new QNameDV(), new IDDV(), new IDREFDV(), new EntityDV(), new IntegerDV(), new ListDV(), new UnionDV(), new YearMonthDurationDV(), new DayTimeDurationDV(), new AnyAtomicDV()};
    static final short NORMALIZE_NONE = 0;
    static final short NORMALIZE_TRIM = 1;
    static final short NORMALIZE_FULL = 2;
    static final short[] fDVNormalizeType = new short[]{0, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 0};
    static final short SPECIAL_PATTERN_NONE = 0;
    static final short SPECIAL_PATTERN_NMTOKEN = 1;
    static final short SPECIAL_PATTERN_NAME = 2;
    static final short SPECIAL_PATTERN_NCNAME = 3;
    static final String[] SPECIAL_PATTERN_STRING = new String[]{"NONE", "NMTOKEN", "Name", "NCName"};
    static final String[] WS_FACET_STRING = new String[]{"preserve", "replace", "collapse"};
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String ANY_TYPE = "anyType";
    public static final short YEARMONTHDURATION_DT = 46;
    public static final short DAYTIMEDURATION_DT = 47;
    public static final short PRECISIONDECIMAL_DT = 48;
    public static final short ANYATOMICTYPE_DT = 49;
    static final int DERIVATION_ANY = 0;
    static final int DERIVATION_RESTRICTION = 1;
    static final int DERIVATION_EXTENSION = 2;
    static final int DERIVATION_UNION = 4;
    static final int DERIVATION_LIST = 8;
    static final ValidationContext fEmptyContext = new ValidationContext(){

        @Override
        public boolean needFacetChecking() {
            return true;
        }

        @Override
        public boolean needExtraChecking() {
            return false;
        }

        @Override
        public boolean needToNormalize() {
            return true;
        }

        @Override
        public boolean useNamespaces() {
            return true;
        }

        @Override
        public boolean isEntityDeclared(String string) {
            return false;
        }

        @Override
        public boolean isEntityUnparsed(String string) {
            return false;
        }

        @Override
        public boolean isIdDeclared(String string) {
            return false;
        }

        @Override
        public void addId(String string) {
        }

        @Override
        public void addIdRef(String string) {
        }

        @Override
        public String getSymbol(String string) {
            return string.intern();
        }

        @Override
        public String getURI(String string) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }
    };
    private TypeValidator[] fDVs = gDVs;
    private boolean fIsImmutable = false;
    private XSSimpleTypeDecl fItemType;
    private XSSimpleTypeDecl[] fMemberTypes;
    private short fBuiltInKind;
    private String fTypeName;
    private String fTargetNamespace;
    private short fFinalSet = 0;
    private XSSimpleTypeDecl fBase;
    private short fVariety = (short)-1;
    private short fValidationDV = (short)-1;
    private short fFacetsDefined = 0;
    private short fFixedFacet = 0;
    private short fWhiteSpace = 0;
    private int fLength = -1;
    private int fMinLength = -1;
    private int fMaxLength = -1;
    private int fTotalDigits = -1;
    private int fFractionDigits = -1;
    private Vector fPattern;
    private Vector fPatternStr;
    private ValidatedInfo[] fEnumeration;
    private int fEnumerationSize;
    private ShortList fEnumerationTypeList;
    private ObjectList fEnumerationItemTypeList;
    private StringList fLexicalPattern;
    private StringList fLexicalEnumeration;
    private ObjectList fActualEnumeration;
    private Object fMaxInclusive;
    private Object fMaxExclusive;
    private Object fMinExclusive;
    private Object fMinInclusive;
    public XSAnnotation lengthAnnotation;
    public XSAnnotation minLengthAnnotation;
    public XSAnnotation maxLengthAnnotation;
    public XSAnnotation whiteSpaceAnnotation;
    public XSAnnotation totalDigitsAnnotation;
    public XSAnnotation fractionDigitsAnnotation;
    public XSObjectListImpl patternAnnotations;
    public XSObjectList enumerationAnnotations;
    public XSAnnotation maxInclusiveAnnotation;
    public XSAnnotation maxExclusiveAnnotation;
    public XSAnnotation minInclusiveAnnotation;
    public XSAnnotation minExclusiveAnnotation;
    private XSObjectListImpl fFacets;
    private XSObjectListImpl fMultiValueFacets;
    private XSObjectList fAnnotations = null;
    private short fPatternType = 0;
    private short fOrdered;
    private boolean fFinite;
    private boolean fBounded;
    private boolean fNumeric;
    private XSNamespaceItem fNamespaceItem = null;
    static final XSSimpleTypeDecl fAnySimpleType = new XSSimpleTypeDecl(null, "anySimpleType", 0, 0, false, true, false, true, 1);
    static final XSSimpleTypeDecl fAnyAtomicType = new XSSimpleTypeDecl(fAnySimpleType, "anyAtomicType", 29, 0, false, true, false, true, 49);
    static final ValidationContext fDummyContext = new ValidationContext(){

        @Override
        public boolean needFacetChecking() {
            return true;
        }

        @Override
        public boolean needExtraChecking() {
            return false;
        }

        @Override
        public boolean needToNormalize() {
            return false;
        }

        @Override
        public boolean useNamespaces() {
            return true;
        }

        @Override
        public boolean isEntityDeclared(String string) {
            return false;
        }

        @Override
        public boolean isEntityUnparsed(String string) {
            return false;
        }

        @Override
        public boolean isIdDeclared(String string) {
            return false;
        }

        @Override
        public void addId(String string) {
        }

        @Override
        public void addIdRef(String string) {
        }

        @Override
        public String getSymbol(String string) {
            return string.intern();
        }

        @Override
        public String getURI(String string) {
            return null;
        }

        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }
    };
    private boolean fAnonymous = false;

    protected static TypeValidator[] getGDVs() {
        return (TypeValidator[])gDVs.clone();
    }

    protected void setDVs(TypeValidator[] typeValidatorArray) {
        this.fDVs = typeValidatorArray;
    }

    public XSSimpleTypeDecl() {
    }

    protected XSSimpleTypeDecl(XSSimpleTypeDecl xSSimpleTypeDecl, String string, short s, short s2, boolean bl, boolean bl2, boolean bl3, boolean bl4, short s3) {
        this.fIsImmutable = bl4;
        this.fBase = xSSimpleTypeDecl;
        this.fTypeName = string;
        this.fTargetNamespace = URI_SCHEMAFORSCHEMA;
        this.fVariety = 1;
        this.fValidationDV = s;
        this.fFacetsDefined = (short)16;
        if (s == 0 || s == 29 || s == 1) {
            this.fWhiteSpace = 0;
        } else {
            this.fWhiteSpace = (short)2;
            this.fFixedFacet = (short)16;
        }
        this.fOrdered = s2;
        this.fBounded = bl;
        this.fFinite = bl2;
        this.fNumeric = bl3;
        this.fAnnotations = null;
        this.fBuiltInKind = s3;
    }

    protected XSSimpleTypeDecl(XSSimpleTypeDecl xSSimpleTypeDecl, String string, String string2, short s, boolean bl, XSObjectList xSObjectList, short s2) {
        this(xSSimpleTypeDecl, string, string2, s, bl, xSObjectList);
        this.fBuiltInKind = s2;
    }

    protected XSSimpleTypeDecl(XSSimpleTypeDecl xSSimpleTypeDecl, String string, String string2, short s, boolean bl, XSObjectList xSObjectList) {
        this.fBase = xSSimpleTypeDecl;
        this.fTypeName = string;
        this.fTargetNamespace = string2;
        this.fFinalSet = s;
        this.fAnnotations = xSObjectList;
        this.fVariety = this.fBase.fVariety;
        this.fValidationDV = this.fBase.fValidationDV;
        switch (this.fVariety) {
            case 1: {
                break;
            }
            case 2: {
                this.fItemType = this.fBase.fItemType;
                break;
            }
            case 3: {
                this.fMemberTypes = this.fBase.fMemberTypes;
            }
        }
        this.fLength = this.fBase.fLength;
        this.fMinLength = this.fBase.fMinLength;
        this.fMaxLength = this.fBase.fMaxLength;
        this.fPattern = this.fBase.fPattern;
        this.fPatternStr = this.fBase.fPatternStr;
        this.fEnumeration = this.fBase.fEnumeration;
        this.fEnumerationSize = this.fBase.fEnumerationSize;
        this.fWhiteSpace = this.fBase.fWhiteSpace;
        this.fMaxExclusive = this.fBase.fMaxExclusive;
        this.fMaxInclusive = this.fBase.fMaxInclusive;
        this.fMinExclusive = this.fBase.fMinExclusive;
        this.fMinInclusive = this.fBase.fMinInclusive;
        this.fTotalDigits = this.fBase.fTotalDigits;
        this.fFractionDigits = this.fBase.fFractionDigits;
        this.fPatternType = this.fBase.fPatternType;
        this.fFixedFacet = this.fBase.fFixedFacet;
        this.fFacetsDefined = this.fBase.fFacetsDefined;
        this.lengthAnnotation = this.fBase.lengthAnnotation;
        this.minLengthAnnotation = this.fBase.minLengthAnnotation;
        this.maxLengthAnnotation = this.fBase.maxLengthAnnotation;
        this.patternAnnotations = this.fBase.patternAnnotations;
        this.enumerationAnnotations = this.fBase.enumerationAnnotations;
        this.whiteSpaceAnnotation = this.fBase.whiteSpaceAnnotation;
        this.maxExclusiveAnnotation = this.fBase.maxExclusiveAnnotation;
        this.maxInclusiveAnnotation = this.fBase.maxInclusiveAnnotation;
        this.minExclusiveAnnotation = this.fBase.minExclusiveAnnotation;
        this.minInclusiveAnnotation = this.fBase.minInclusiveAnnotation;
        this.totalDigitsAnnotation = this.fBase.totalDigitsAnnotation;
        this.fractionDigitsAnnotation = this.fBase.fractionDigitsAnnotation;
        this.calcFundamentalFacets();
        this.fIsImmutable = bl;
        this.fBuiltInKind = xSSimpleTypeDecl.fBuiltInKind;
    }

    protected XSSimpleTypeDecl(String string, String string2, short s, XSSimpleTypeDecl xSSimpleTypeDecl, boolean bl, XSObjectList xSObjectList) {
        this.fBase = fAnySimpleType;
        this.fTypeName = string;
        this.fTargetNamespace = string2;
        this.fFinalSet = s;
        this.fAnnotations = xSObjectList;
        this.fVariety = (short)2;
        this.fItemType = xSSimpleTypeDecl;
        this.fValidationDV = (short)25;
        this.fFacetsDefined = (short)16;
        this.fFixedFacet = (short)16;
        this.fWhiteSpace = (short)2;
        this.calcFundamentalFacets();
        this.fIsImmutable = bl;
        this.fBuiltInKind = (short)44;
    }

    protected XSSimpleTypeDecl(String string, String string2, short s, XSSimpleTypeDecl[] xSSimpleTypeDeclArray, XSObjectList xSObjectList) {
        this.fBase = fAnySimpleType;
        this.fTypeName = string;
        this.fTargetNamespace = string2;
        this.fFinalSet = s;
        this.fAnnotations = xSObjectList;
        this.fVariety = (short)3;
        this.fMemberTypes = xSSimpleTypeDeclArray;
        this.fValidationDV = (short)26;
        this.fFacetsDefined = (short)16;
        this.fWhiteSpace = (short)2;
        this.calcFundamentalFacets();
        this.fIsImmutable = false;
        this.fBuiltInKind = (short)45;
    }

    protected XSSimpleTypeDecl setRestrictionValues(XSSimpleTypeDecl xSSimpleTypeDecl, String string, String string2, short s, XSObjectList xSObjectList) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = xSSimpleTypeDecl;
        this.fAnonymous = false;
        this.fTypeName = string;
        this.fTargetNamespace = string2;
        this.fFinalSet = s;
        this.fAnnotations = xSObjectList;
        this.fVariety = this.fBase.fVariety;
        this.fValidationDV = this.fBase.fValidationDV;
        switch (this.fVariety) {
            case 1: {
                break;
            }
            case 2: {
                this.fItemType = this.fBase.fItemType;
                break;
            }
            case 3: {
                this.fMemberTypes = this.fBase.fMemberTypes;
            }
        }
        this.fLength = this.fBase.fLength;
        this.fMinLength = this.fBase.fMinLength;
        this.fMaxLength = this.fBase.fMaxLength;
        this.fPattern = this.fBase.fPattern;
        this.fPatternStr = this.fBase.fPatternStr;
        this.fEnumeration = this.fBase.fEnumeration;
        this.fEnumerationSize = this.fBase.fEnumerationSize;
        this.fWhiteSpace = this.fBase.fWhiteSpace;
        this.fMaxExclusive = this.fBase.fMaxExclusive;
        this.fMaxInclusive = this.fBase.fMaxInclusive;
        this.fMinExclusive = this.fBase.fMinExclusive;
        this.fMinInclusive = this.fBase.fMinInclusive;
        this.fTotalDigits = this.fBase.fTotalDigits;
        this.fFractionDigits = this.fBase.fFractionDigits;
        this.fPatternType = this.fBase.fPatternType;
        this.fFixedFacet = this.fBase.fFixedFacet;
        this.fFacetsDefined = this.fBase.fFacetsDefined;
        this.calcFundamentalFacets();
        this.fBuiltInKind = xSSimpleTypeDecl.fBuiltInKind;
        return this;
    }

    protected XSSimpleTypeDecl setListValues(String string, String string2, short s, XSSimpleTypeDecl xSSimpleTypeDecl, XSObjectList xSObjectList) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = fAnySimpleType;
        this.fAnonymous = false;
        this.fTypeName = string;
        this.fTargetNamespace = string2;
        this.fFinalSet = s;
        this.fAnnotations = xSObjectList;
        this.fVariety = (short)2;
        this.fItemType = xSSimpleTypeDecl;
        this.fValidationDV = (short)25;
        this.fFacetsDefined = (short)16;
        this.fFixedFacet = (short)16;
        this.fWhiteSpace = (short)2;
        this.calcFundamentalFacets();
        this.fBuiltInKind = (short)44;
        return this;
    }

    protected XSSimpleTypeDecl setUnionValues(String string, String string2, short s, XSSimpleTypeDecl[] xSSimpleTypeDeclArray, XSObjectList xSObjectList) {
        if (this.fIsImmutable) {
            return null;
        }
        this.fBase = fAnySimpleType;
        this.fAnonymous = false;
        this.fTypeName = string;
        this.fTargetNamespace = string2;
        this.fFinalSet = s;
        this.fAnnotations = xSObjectList;
        this.fVariety = (short)3;
        this.fMemberTypes = xSSimpleTypeDeclArray;
        this.fValidationDV = (short)26;
        this.fFacetsDefined = (short)16;
        this.fWhiteSpace = (short)2;
        this.calcFundamentalFacets();
        this.fBuiltInKind = (short)45;
        return this;
    }

    @Override
    public short getType() {
        return 3;
    }

    @Override
    public short getTypeCategory() {
        return 16;
    }

    @Override
    public String getName() {
        return this.getAnonymous() ? null : this.fTypeName;
    }

    @Override
    public String getTypeName() {
        return this.fTypeName;
    }

    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }

    @Override
    public short getFinal() {
        return this.fFinalSet;
    }

    @Override
    public boolean isFinal(short s) {
        return (this.fFinalSet & s) != 0;
    }

    @Override
    public XSTypeDefinition getBaseType() {
        return this.fBase;
    }

    @Override
    public boolean getAnonymous() {
        return this.fAnonymous || this.fTypeName == null;
    }

    @Override
    public short getVariety() {
        return this.fValidationDV == 0 ? (short)0 : this.fVariety;
    }

    @Override
    public boolean isIDType() {
        switch (this.fVariety) {
            case 1: {
                return this.fValidationDV == 21;
            }
            case 2: {
                return this.fItemType.isIDType();
            }
            case 3: {
                for (int i = 0; i < this.fMemberTypes.length; ++i) {
                    if (!this.fMemberTypes[i].isIDType()) continue;
                    return true;
                }
                break;
            }
        }
        return false;
    }

    @Override
    public short getWhitespace() throws DatatypeException {
        if (this.fVariety == 3) {
            throw new DatatypeException("dt-whitespace", new Object[]{this.fTypeName});
        }
        return this.fWhiteSpace;
    }

    @Override
    public short getPrimitiveKind() {
        if (this.fVariety == 1 && this.fValidationDV != 0) {
            if (this.fValidationDV == 21 || this.fValidationDV == 22 || this.fValidationDV == 23) {
                return 1;
            }
            if (this.fValidationDV == 24) {
                return 3;
            }
            return this.fValidationDV;
        }
        return 0;
    }

    @Override
    public short getBuiltInKind() {
        return this.fBuiltInKind;
    }

    @Override
    public XSSimpleTypeDefinition getPrimitiveType() {
        if (this.fVariety == 1 && this.fValidationDV != 0) {
            XSSimpleTypeDecl xSSimpleTypeDecl = this;
            while (xSSimpleTypeDecl.fBase != fAnySimpleType) {
                xSSimpleTypeDecl = xSSimpleTypeDecl.fBase;
            }
            return xSSimpleTypeDecl;
        }
        return null;
    }

    @Override
    public XSSimpleTypeDefinition getItemType() {
        if (this.fVariety == 2) {
            return this.fItemType;
        }
        return null;
    }

    @Override
    public XSObjectList getMemberTypes() {
        if (this.fVariety == 3) {
            return new XSObjectListImpl(this.fMemberTypes, this.fMemberTypes.length);
        }
        return XSObjectListImpl.EMPTY_LIST;
    }

    @Override
    public void applyFacets(XSFacets xSFacets, short s, short s2, ValidationContext validationContext) throws InvalidDatatypeFacetException {
        if (validationContext == null) {
            validationContext = fEmptyContext;
        }
        this.applyFacets(xSFacets, s, s2, (short)0, validationContext);
    }

    void applyFacets1(XSFacets xSFacets, short s, short s2) {
        try {
            this.applyFacets(xSFacets, s, s2, (short)0, fDummyContext);
        }
        catch (InvalidDatatypeFacetException invalidDatatypeFacetException) {
            throw new RuntimeException("internal error");
        }
        this.fIsImmutable = true;
    }

    void applyFacets1(XSFacets xSFacets, short s, short s2, short s3) {
        try {
            this.applyFacets(xSFacets, s, s2, s3, fDummyContext);
        }
        catch (InvalidDatatypeFacetException invalidDatatypeFacetException) {
            throw new RuntimeException("internal error");
        }
        this.fIsImmutable = true;
    }

    void applyFacets(XSFacets xSFacets, short s, short s2, short s3, ValidationContext validationContext) throws InvalidDatatypeFacetException {
        Serializable serializable;
        if (this.fIsImmutable) {
            return;
        }
        ValidatedInfo validatedInfo = new ValidatedInfo();
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        int n = 0;
        short s4 = this.fDVs[this.fValidationDV].getAllowedFacets();
        if ((s & 1) != 0) {
            if ((s4 & 1) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"length", this.fTypeName});
            } else {
                this.fLength = xSFacets.length;
                this.lengthAnnotation = xSFacets.lengthAnnotation;
                this.fFacetsDefined = (short)(this.fFacetsDefined | 1);
                if ((s2 & 1) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 1);
                }
            }
        }
        if ((s & 2) != 0) {
            if ((s4 & 2) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"minLength", this.fTypeName});
            } else {
                this.fMinLength = xSFacets.minLength;
                this.minLengthAnnotation = xSFacets.minLengthAnnotation;
                this.fFacetsDefined = (short)(this.fFacetsDefined | 2);
                if ((s2 & 2) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 2);
                }
            }
        }
        if ((s & 4) != 0) {
            if ((s4 & 4) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"maxLength", this.fTypeName});
            } else {
                this.fMaxLength = xSFacets.maxLength;
                this.maxLengthAnnotation = xSFacets.maxLengthAnnotation;
                this.fFacetsDefined = (short)(this.fFacetsDefined | 4);
                if ((s2 & 4) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 4);
                }
            }
        }
        if ((s & 8) != 0) {
            if ((s4 & 8) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"pattern", this.fTypeName});
            } else {
                this.patternAnnotations = xSFacets.patternAnnotations;
                serializable = null;
                try {
                    serializable = new RegularExpression(xSFacets.pattern, "X", validationContext.getLocale());
                }
                catch (Exception exception) {
                    this.reportError("InvalidRegex", new Object[]{xSFacets.pattern, exception.getLocalizedMessage()});
                }
                if (serializable != null) {
                    this.fPattern = new Vector();
                    this.fPattern.addElement(serializable);
                    this.fPatternStr = new Vector();
                    this.fPatternStr.addElement(xSFacets.pattern);
                    this.fFacetsDefined = (short)(this.fFacetsDefined | 8);
                    if ((s2 & 8) != 0) {
                        this.fFixedFacet = (short)(this.fFixedFacet | 8);
                    }
                }
            }
        }
        if ((s & 0x10) != 0) {
            if ((s4 & 0x10) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"whiteSpace", this.fTypeName});
            } else {
                this.fWhiteSpace = xSFacets.whiteSpace;
                this.whiteSpaceAnnotation = xSFacets.whiteSpaceAnnotation;
                this.fFacetsDefined = (short)(this.fFacetsDefined | 0x10);
                if ((s2 & 0x10) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 0x10);
                }
            }
        }
        if ((s & 0x800) != 0) {
            if ((s4 & 0x800) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"enumeration", this.fTypeName});
            } else {
                serializable = xSFacets.enumeration;
                int n2 = ((Vector)serializable).size();
                this.fEnumeration = new ValidatedInfo[n2];
                Vector vector = xSFacets.enumNSDecls;
                ValidationContextImpl validationContextImpl = new ValidationContextImpl(validationContext);
                this.enumerationAnnotations = xSFacets.enumAnnotations;
                this.fEnumerationSize = 0;
                for (int i = 0; i < n2; ++i) {
                    if (vector != null) {
                        validationContextImpl.setNSContext((NamespaceContext)vector.elementAt(i));
                    }
                    try {
                        ValidatedInfo validatedInfo2 = this.getActualEnumValue((String)((Vector)serializable).elementAt(i), validationContextImpl, null);
                        this.fEnumeration[this.fEnumerationSize++] = validatedInfo2;
                        continue;
                    }
                    catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                        this.reportError("enumeration-valid-restriction", new Object[]{((Vector)serializable).elementAt(i), this.getBaseType().getName()});
                    }
                }
                this.fFacetsDefined = (short)(this.fFacetsDefined | 0x800);
                if ((s2 & 0x800) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 0x800);
                }
            }
        }
        if ((s & 0x20) != 0) {
            if ((s4 & 0x20) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"maxInclusive", this.fTypeName});
            } else {
                this.maxInclusiveAnnotation = xSFacets.maxInclusiveAnnotation;
                try {
                    this.fMaxInclusive = this.fBase.getActualValue(xSFacets.maxInclusive, validationContext, validatedInfo, true);
                    this.fFacetsDefined = (short)(this.fFacetsDefined | 0x20);
                    if ((s2 & 0x20) != 0) {
                        this.fFixedFacet = (short)(this.fFixedFacet | 0x20);
                    }
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.maxInclusive, "maxInclusive", this.fBase.getName()});
                }
                if ((this.fBase.fFacetsDefined & 0x20) != 0 && (this.fBase.fFixedFacet & 0x20) != 0 && this.fDVs[this.fValidationDV].compare(this.fMaxInclusive, this.fBase.fMaxInclusive) != 0) {
                    this.reportError("FixedFacetValue", new Object[]{"maxInclusive", this.fMaxInclusive, this.fBase.fMaxInclusive, this.fTypeName});
                }
                try {
                    this.fBase.validate(validationContext, validatedInfo);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.maxInclusive, "maxInclusive", this.fBase.getName()});
                }
            }
        }
        boolean bl = true;
        if ((s & 0x40) != 0) {
            if ((s4 & 0x40) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"maxExclusive", this.fTypeName});
            } else {
                this.maxExclusiveAnnotation = xSFacets.maxExclusiveAnnotation;
                try {
                    this.fMaxExclusive = this.fBase.getActualValue(xSFacets.maxExclusive, validationContext, validatedInfo, true);
                    this.fFacetsDefined = (short)(this.fFacetsDefined | 0x40);
                    if ((s2 & 0x40) != 0) {
                        this.fFixedFacet = (short)(this.fFixedFacet | 0x40);
                    }
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.maxExclusive, "maxExclusive", this.fBase.getName()});
                }
                if ((this.fBase.fFacetsDefined & 0x40) != 0) {
                    n = this.fDVs[this.fValidationDV].compare(this.fMaxExclusive, this.fBase.fMaxExclusive);
                    if ((this.fBase.fFixedFacet & 0x40) != 0 && n != 0) {
                        this.reportError("FixedFacetValue", new Object[]{"maxExclusive", xSFacets.maxExclusive, this.fBase.fMaxExclusive, this.fTypeName});
                    }
                    if (n == 0) {
                        bl = false;
                    }
                }
                if (bl) {
                    try {
                        this.fBase.validate(validationContext, validatedInfo);
                    }
                    catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                        this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                        this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.maxExclusive, "maxExclusive", this.fBase.getName()});
                    }
                } else if ((this.fBase.fFacetsDefined & 0x20) != 0 && this.fDVs[this.fValidationDV].compare(this.fMaxExclusive, this.fBase.fMaxInclusive) > 0) {
                    this.reportError("maxExclusive-valid-restriction.2", new Object[]{xSFacets.maxExclusive, this.fBase.fMaxInclusive});
                }
            }
        }
        bl = true;
        if ((s & 0x80) != 0) {
            if ((s4 & 0x80) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"minExclusive", this.fTypeName});
            } else {
                this.minExclusiveAnnotation = xSFacets.minExclusiveAnnotation;
                try {
                    this.fMinExclusive = this.fBase.getActualValue(xSFacets.minExclusive, validationContext, validatedInfo, true);
                    this.fFacetsDefined = (short)(this.fFacetsDefined | 0x80);
                    if ((s2 & 0x80) != 0) {
                        this.fFixedFacet = (short)(this.fFixedFacet | 0x80);
                    }
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.minExclusive, "minExclusive", this.fBase.getName()});
                }
                if ((this.fBase.fFacetsDefined & 0x80) != 0) {
                    n = this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fBase.fMinExclusive);
                    if ((this.fBase.fFixedFacet & 0x80) != 0 && n != 0) {
                        this.reportError("FixedFacetValue", new Object[]{"minExclusive", xSFacets.minExclusive, this.fBase.fMinExclusive, this.fTypeName});
                    }
                    if (n == 0) {
                        bl = false;
                    }
                }
                if (bl) {
                    try {
                        this.fBase.validate(validationContext, validatedInfo);
                    }
                    catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                        this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                        this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.minExclusive, "minExclusive", this.fBase.getName()});
                    }
                } else if ((this.fBase.fFacetsDefined & 0x100) != 0 && this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fBase.fMinInclusive) < 0) {
                    this.reportError("minExclusive-valid-restriction.3", new Object[]{xSFacets.minExclusive, this.fBase.fMinInclusive});
                }
            }
        }
        if ((s & 0x100) != 0) {
            if ((s4 & 0x100) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"minInclusive", this.fTypeName});
            } else {
                this.minInclusiveAnnotation = xSFacets.minInclusiveAnnotation;
                try {
                    this.fMinInclusive = this.fBase.getActualValue(xSFacets.minInclusive, validationContext, validatedInfo, true);
                    this.fFacetsDefined = (short)(this.fFacetsDefined | 0x100);
                    if ((s2 & 0x100) != 0) {
                        this.fFixedFacet = (short)(this.fFixedFacet | 0x100);
                    }
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.minInclusive, "minInclusive", this.fBase.getName()});
                }
                if ((this.fBase.fFacetsDefined & 0x100) != 0 && (this.fBase.fFixedFacet & 0x100) != 0 && this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fBase.fMinInclusive) != 0) {
                    this.reportError("FixedFacetValue", new Object[]{"minInclusive", xSFacets.minInclusive, this.fBase.fMinInclusive, this.fTypeName});
                }
                try {
                    this.fBase.validate(validationContext, validatedInfo);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportError("FacetValueFromBase", new Object[]{this.fTypeName, xSFacets.minInclusive, "minInclusive", this.fBase.getName()});
                }
            }
        }
        if ((s & 0x200) != 0) {
            if ((s4 & 0x200) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"totalDigits", this.fTypeName});
            } else {
                this.totalDigitsAnnotation = xSFacets.totalDigitsAnnotation;
                this.fTotalDigits = xSFacets.totalDigits;
                this.fFacetsDefined = (short)(this.fFacetsDefined | 0x200);
                if ((s2 & 0x200) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 0x200);
                }
            }
        }
        if ((s & 0x400) != 0) {
            if ((s4 & 0x400) == 0) {
                this.reportError("cos-applicable-facets", new Object[]{"fractionDigits", this.fTypeName});
            } else {
                this.fFractionDigits = xSFacets.fractionDigits;
                this.fractionDigitsAnnotation = xSFacets.fractionDigitsAnnotation;
                this.fFacetsDefined = (short)(this.fFacetsDefined | 0x400);
                if ((s2 & 0x400) != 0) {
                    this.fFixedFacet = (short)(this.fFixedFacet | 0x400);
                }
            }
        }
        if (s3 != 0) {
            this.fPatternType = s3;
        }
        if (this.fFacetsDefined != 0) {
            if ((this.fFacetsDefined & 2) != 0 && (this.fFacetsDefined & 4) != 0 && this.fMinLength > this.fMaxLength) {
                this.reportError("minLength-less-than-equal-to-maxLength", new Object[]{Integer.toString(this.fMinLength), Integer.toString(this.fMaxLength), this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x40) != 0 && (this.fFacetsDefined & 0x20) != 0) {
                this.reportError("maxInclusive-maxExclusive", new Object[]{this.fMaxInclusive, this.fMaxExclusive, this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x80) != 0 && (this.fFacetsDefined & 0x100) != 0) {
                this.reportError("minInclusive-minExclusive", new Object[]{this.fMinInclusive, this.fMinExclusive, this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x20) != 0 && (this.fFacetsDefined & 0x100) != 0 && (n = this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fMaxInclusive)) != -1 && n != 0) {
                this.reportError("minInclusive-less-than-equal-to-maxInclusive", new Object[]{this.fMinInclusive, this.fMaxInclusive, this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x40) != 0 && (this.fFacetsDefined & 0x80) != 0 && (n = this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fMaxExclusive)) != -1 && n != 0) {
                this.reportError("minExclusive-less-than-equal-to-maxExclusive", new Object[]{this.fMinExclusive, this.fMaxExclusive, this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x20) != 0 && (this.fFacetsDefined & 0x80) != 0 && this.fDVs[this.fValidationDV].compare(this.fMinExclusive, this.fMaxInclusive) != -1) {
                this.reportError("minExclusive-less-than-maxInclusive", new Object[]{this.fMinExclusive, this.fMaxInclusive, this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x40) != 0 && (this.fFacetsDefined & 0x100) != 0 && this.fDVs[this.fValidationDV].compare(this.fMinInclusive, this.fMaxExclusive) != -1) {
                this.reportError("minInclusive-less-than-maxExclusive", new Object[]{this.fMinInclusive, this.fMaxExclusive, this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x400) != 0 && (this.fFacetsDefined & 0x200) != 0 && this.fFractionDigits > this.fTotalDigits) {
                this.reportError("fractionDigits-totalDigits", new Object[]{Integer.toString(this.fFractionDigits), Integer.toString(this.fTotalDigits), this.fTypeName});
            }
            if ((this.fFacetsDefined & 1) != 0) {
                if ((this.fBase.fFacetsDefined & 2) != 0 && this.fLength < this.fBase.fMinLength) {
                    this.reportError("length-minLength-maxLength.1.1", new Object[]{this.fTypeName, Integer.toString(this.fLength), Integer.toString(this.fBase.fMinLength)});
                }
                if ((this.fBase.fFacetsDefined & 4) != 0 && this.fLength > this.fBase.fMaxLength) {
                    this.reportError("length-minLength-maxLength.2.1", new Object[]{this.fTypeName, Integer.toString(this.fLength), Integer.toString(this.fBase.fMaxLength)});
                }
                if ((this.fBase.fFacetsDefined & 1) != 0 && this.fLength != this.fBase.fLength) {
                    this.reportError("length-valid-restriction", new Object[]{Integer.toString(this.fLength), Integer.toString(this.fBase.fLength), this.fTypeName});
                }
            }
            if ((this.fBase.fFacetsDefined & 1) != 0 || (this.fFacetsDefined & 1) != 0) {
                if ((this.fFacetsDefined & 2) != 0) {
                    if ((this.fBase.fFacetsDefined & 1) != 0 && this.fBase.fLength < this.fMinLength) {
                        this.reportError("length-minLength-maxLength.1.1", new Object[]{this.fTypeName, Integer.toString(this.fBase.fLength), Integer.toString(this.fMinLength)});
                    }
                    if ((this.fFacetsDefined & 1) != 0 && this.fLength < this.fMinLength) {
                        this.reportError("length-minLength-maxLength.1.1", new Object[]{this.fTypeName, Integer.toString(this.fLength), Integer.toString(this.fMinLength)});
                    }
                    if ((this.fBase.fFacetsDefined & 2) == 0) {
                        this.reportError("length-minLength-maxLength.1.2.a", new Object[]{this.fTypeName});
                    }
                    if (this.fMinLength != this.fBase.fMinLength) {
                        this.reportError("length-minLength-maxLength.1.2.b", new Object[]{this.fTypeName, Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMinLength)});
                    }
                }
                if ((this.fFacetsDefined & 4) != 0) {
                    if ((this.fBase.fFacetsDefined & 1) != 0 && this.fBase.fLength > this.fMaxLength) {
                        this.reportError("length-minLength-maxLength.2.1", new Object[]{this.fTypeName, Integer.toString(this.fBase.fLength), Integer.toString(this.fMaxLength)});
                    }
                    if ((this.fFacetsDefined & 1) != 0 && this.fLength > this.fMaxLength) {
                        this.reportError("length-minLength-maxLength.2.1", new Object[]{this.fTypeName, Integer.toString(this.fLength), Integer.toString(this.fMaxLength)});
                    }
                    if ((this.fBase.fFacetsDefined & 4) == 0) {
                        this.reportError("length-minLength-maxLength.2.2.a", new Object[]{this.fTypeName});
                    }
                    if (this.fMaxLength != this.fBase.fMaxLength) {
                        this.reportError("length-minLength-maxLength.2.2.b", new Object[]{this.fTypeName, Integer.toString(this.fMaxLength), Integer.toString(this.fBase.fBase.fMaxLength)});
                    }
                }
            }
            if ((this.fFacetsDefined & 2) != 0) {
                if ((this.fBase.fFacetsDefined & 4) != 0) {
                    if (this.fMinLength > this.fBase.fMaxLength) {
                        this.reportError("minLength-less-than-equal-to-maxLength", new Object[]{Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMaxLength), this.fTypeName});
                    }
                } else if ((this.fBase.fFacetsDefined & 2) != 0) {
                    if ((this.fBase.fFixedFacet & 2) != 0 && this.fMinLength != this.fBase.fMinLength) {
                        this.reportError("FixedFacetValue", new Object[]{"minLength", Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMinLength), this.fTypeName});
                    }
                    if (this.fMinLength < this.fBase.fMinLength) {
                        this.reportError("minLength-valid-restriction", new Object[]{Integer.toString(this.fMinLength), Integer.toString(this.fBase.fMinLength), this.fTypeName});
                    }
                }
            }
            if ((this.fFacetsDefined & 4) != 0 && (this.fBase.fFacetsDefined & 2) != 0 && this.fMaxLength < this.fBase.fMinLength) {
                this.reportError("minLength-less-than-equal-to-maxLength", new Object[]{Integer.toString(this.fBase.fMinLength), Integer.toString(this.fMaxLength)});
            }
            if ((this.fFacetsDefined & 4) != 0 && (this.fBase.fFacetsDefined & 4) != 0) {
                if ((this.fBase.fFixedFacet & 4) != 0 && this.fMaxLength != this.fBase.fMaxLength) {
                    this.reportError("FixedFacetValue", new Object[]{"maxLength", Integer.toString(this.fMaxLength), Integer.toString(this.fBase.fMaxLength), this.fTypeName});
                }
                if (this.fMaxLength > this.fBase.fMaxLength) {
                    this.reportError("maxLength-valid-restriction", new Object[]{Integer.toString(this.fMaxLength), Integer.toString(this.fBase.fMaxLength), this.fTypeName});
                }
            }
            if ((this.fFacetsDefined & 0x200) != 0 && (this.fBase.fFacetsDefined & 0x200) != 0) {
                if ((this.fBase.fFixedFacet & 0x200) != 0 && this.fTotalDigits != this.fBase.fTotalDigits) {
                    this.reportError("FixedFacetValue", new Object[]{"totalDigits", Integer.toString(this.fTotalDigits), Integer.toString(this.fBase.fTotalDigits), this.fTypeName});
                }
                if (this.fTotalDigits > this.fBase.fTotalDigits) {
                    this.reportError("totalDigits-valid-restriction", new Object[]{Integer.toString(this.fTotalDigits), Integer.toString(this.fBase.fTotalDigits), this.fTypeName});
                }
            }
            if ((this.fFacetsDefined & 0x400) != 0 && (this.fBase.fFacetsDefined & 0x200) != 0 && this.fFractionDigits > this.fBase.fTotalDigits) {
                this.reportError("fractionDigits-totalDigits", new Object[]{Integer.toString(this.fFractionDigits), Integer.toString(this.fTotalDigits), this.fTypeName});
            }
            if ((this.fFacetsDefined & 0x400) != 0) {
                if ((this.fBase.fFacetsDefined & 0x400) != 0) {
                    if ((this.fBase.fFixedFacet & 0x400) != 0 && this.fFractionDigits != this.fBase.fFractionDigits || this.fValidationDV == 24 && this.fFractionDigits != 0) {
                        this.reportError("FixedFacetValue", new Object[]{"fractionDigits", Integer.toString(this.fFractionDigits), Integer.toString(this.fBase.fFractionDigits), this.fTypeName});
                    }
                    if (this.fFractionDigits > this.fBase.fFractionDigits) {
                        this.reportError("fractionDigits-valid-restriction", new Object[]{Integer.toString(this.fFractionDigits), Integer.toString(this.fBase.fFractionDigits), this.fTypeName});
                    }
                } else if (this.fValidationDV == 24 && this.fFractionDigits != 0) {
                    this.reportError("FixedFacetValue", new Object[]{"fractionDigits", Integer.toString(this.fFractionDigits), "0", this.fTypeName});
                }
            }
            if ((this.fFacetsDefined & 0x10) != 0 && (this.fBase.fFacetsDefined & 0x10) != 0) {
                if ((this.fBase.fFixedFacet & 0x10) != 0 && this.fWhiteSpace != this.fBase.fWhiteSpace) {
                    this.reportError("FixedFacetValue", new Object[]{"whiteSpace", this.whiteSpaceValue(this.fWhiteSpace), this.whiteSpaceValue(this.fBase.fWhiteSpace), this.fTypeName});
                }
                if (this.fWhiteSpace == 0 && this.fBase.fWhiteSpace == 2) {
                    this.reportError("whiteSpace-valid-restriction.1", new Object[]{this.fTypeName, "preserve"});
                }
                if (this.fWhiteSpace == 1 && this.fBase.fWhiteSpace == 2) {
                    this.reportError("whiteSpace-valid-restriction.1", new Object[]{this.fTypeName, "replace"});
                }
                if (this.fWhiteSpace == 0 && this.fBase.fWhiteSpace == 1) {
                    this.reportError("whiteSpace-valid-restriction.2", new Object[]{this.fTypeName});
                }
            }
        }
        if ((this.fFacetsDefined & 1) == 0 && (this.fBase.fFacetsDefined & 1) != 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 1);
            this.fLength = this.fBase.fLength;
            this.lengthAnnotation = this.fBase.lengthAnnotation;
        }
        if ((this.fFacetsDefined & 2) == 0 && (this.fBase.fFacetsDefined & 2) != 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 2);
            this.fMinLength = this.fBase.fMinLength;
            this.minLengthAnnotation = this.fBase.minLengthAnnotation;
        }
        if ((this.fFacetsDefined & 4) == 0 && (this.fBase.fFacetsDefined & 4) != 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 4);
            this.fMaxLength = this.fBase.fMaxLength;
            this.maxLengthAnnotation = this.fBase.maxLengthAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 8) != 0) {
            if ((this.fFacetsDefined & 8) == 0) {
                this.fFacetsDefined = (short)(this.fFacetsDefined | 8);
                this.fPattern = this.fBase.fPattern;
                this.fPatternStr = this.fBase.fPatternStr;
                this.patternAnnotations = this.fBase.patternAnnotations;
            } else {
                int n3;
                for (n3 = this.fBase.fPattern.size() - 1; n3 >= 0; --n3) {
                    this.fPattern.addElement(this.fBase.fPattern.elementAt(n3));
                    this.fPatternStr.addElement(this.fBase.fPatternStr.elementAt(n3));
                }
                if (this.fBase.patternAnnotations != null) {
                    if (this.patternAnnotations != null) {
                        for (n3 = this.fBase.patternAnnotations.getLength() - 1; n3 >= 0; --n3) {
                            this.patternAnnotations.addXSObject(this.fBase.patternAnnotations.item(n3));
                        }
                    } else {
                        this.patternAnnotations = this.fBase.patternAnnotations;
                    }
                }
            }
        }
        if ((this.fFacetsDefined & 0x10) == 0 && (this.fBase.fFacetsDefined & 0x10) != 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x10);
            this.fWhiteSpace = this.fBase.fWhiteSpace;
            this.whiteSpaceAnnotation = this.fBase.whiteSpaceAnnotation;
        }
        if ((this.fFacetsDefined & 0x800) == 0 && (this.fBase.fFacetsDefined & 0x800) != 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x800);
            this.fEnumeration = this.fBase.fEnumeration;
            this.fEnumerationSize = this.fBase.fEnumerationSize;
            this.enumerationAnnotations = this.fBase.enumerationAnnotations;
        }
        if ((this.fBase.fFacetsDefined & 0x40) != 0 && (this.fFacetsDefined & 0x40) == 0 && (this.fFacetsDefined & 0x20) == 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x40);
            this.fMaxExclusive = this.fBase.fMaxExclusive;
            this.maxExclusiveAnnotation = this.fBase.maxExclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x20) != 0 && (this.fFacetsDefined & 0x40) == 0 && (this.fFacetsDefined & 0x20) == 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x20);
            this.fMaxInclusive = this.fBase.fMaxInclusive;
            this.maxInclusiveAnnotation = this.fBase.maxInclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x80) != 0 && (this.fFacetsDefined & 0x80) == 0 && (this.fFacetsDefined & 0x100) == 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x80);
            this.fMinExclusive = this.fBase.fMinExclusive;
            this.minExclusiveAnnotation = this.fBase.minExclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x100) != 0 && (this.fFacetsDefined & 0x80) == 0 && (this.fFacetsDefined & 0x100) == 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x100);
            this.fMinInclusive = this.fBase.fMinInclusive;
            this.minInclusiveAnnotation = this.fBase.minInclusiveAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x200) != 0 && (this.fFacetsDefined & 0x200) == 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x200);
            this.fTotalDigits = this.fBase.fTotalDigits;
            this.totalDigitsAnnotation = this.fBase.totalDigitsAnnotation;
        }
        if ((this.fBase.fFacetsDefined & 0x400) != 0 && (this.fFacetsDefined & 0x400) == 0) {
            this.fFacetsDefined = (short)(this.fFacetsDefined | 0x400);
            this.fFractionDigits = this.fBase.fFractionDigits;
            this.fractionDigitsAnnotation = this.fBase.fractionDigitsAnnotation;
        }
        if (this.fPatternType == 0 && this.fBase.fPatternType != 0) {
            this.fPatternType = this.fBase.fPatternType;
        }
        this.fFixedFacet = (short)(this.fFixedFacet | this.fBase.fFixedFacet);
        this.calcFundamentalFacets();
    }

    @Override
    public Object validate(String string, ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (validationContext == null) {
            validationContext = fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        } else {
            validatedInfo.memberType = null;
        }
        boolean bl = validationContext == null || validationContext.needToNormalize();
        Object object = this.getActualValue(string, validationContext, validatedInfo, bl);
        this.validate(validationContext, validatedInfo);
        return object;
    }

    protected ValidatedInfo getActualEnumValue(String string, ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        return this.fBase.validateWithInfo(string, validationContext, validatedInfo);
    }

    public ValidatedInfo validateWithInfo(String string, ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (validationContext == null) {
            validationContext = fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        } else {
            validatedInfo.memberType = null;
        }
        boolean bl = validationContext == null || validationContext.needToNormalize();
        this.getActualValue(string, validationContext, validatedInfo, bl);
        this.validate(validationContext, validatedInfo);
        return validatedInfo;
    }

    @Override
    public Object validate(Object object, ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (validationContext == null) {
            validationContext = fEmptyContext;
        }
        if (validatedInfo == null) {
            validatedInfo = new ValidatedInfo();
        } else {
            validatedInfo.memberType = null;
        }
        boolean bl = validationContext == null || validationContext.needToNormalize();
        Object object2 = this.getActualValue(object, validationContext, validatedInfo, bl);
        this.validate(validationContext, validatedInfo);
        return object2;
    }

    @Override
    public void validate(ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        if (validationContext == null) {
            validationContext = fEmptyContext;
        }
        if (validationContext.needFacetChecking() && this.fFacetsDefined != 0 && this.fFacetsDefined != 16) {
            this.checkFacets(validatedInfo);
        }
        if (validationContext.needExtraChecking()) {
            this.checkExtraRules(validationContext, validatedInfo);
        }
    }

    private void checkFacets(ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        int n;
        Object object = validatedInfo.actualValue;
        String string = validatedInfo.normalizedValue;
        short s = validatedInfo.actualValueType;
        ShortList shortList = validatedInfo.itemValueTypes;
        if (this.fValidationDV != 18 && this.fValidationDV != 20) {
            n = this.fDVs[this.fValidationDV].getDataLength(object);
            if ((this.fFacetsDefined & 4) != 0 && n > this.fMaxLength) {
                throw new InvalidDatatypeValueException("cvc-maxLength-valid", new Object[]{string, Integer.toString(n), Integer.toString(this.fMaxLength), this.fTypeName});
            }
            if ((this.fFacetsDefined & 2) != 0 && n < this.fMinLength) {
                throw new InvalidDatatypeValueException("cvc-minLength-valid", new Object[]{string, Integer.toString(n), Integer.toString(this.fMinLength), this.fTypeName});
            }
            if ((this.fFacetsDefined & 1) != 0 && n != this.fLength) {
                throw new InvalidDatatypeValueException("cvc-length-valid", new Object[]{string, Integer.toString(n), Integer.toString(this.fLength), this.fTypeName});
            }
        }
        if ((this.fFacetsDefined & 0x800) != 0) {
            n = 0;
            int n2 = this.fEnumerationSize;
            short s2 = this.convertToPrimitiveKind(s);
            for (int i = 0; i < n2; ++i) {
                short s3 = this.convertToPrimitiveKind(this.fEnumeration[i].actualValueType);
                if (s2 != s3 && (s2 != 1 || s3 != 2) && (s2 != 2 || s3 != 1) || !this.fEnumeration[i].actualValue.equals(object)) continue;
                if (s2 == 44 || s2 == 43) {
                    short s4;
                    short s5;
                    int n3;
                    int n4;
                    ShortList shortList2 = this.fEnumeration[i].itemValueTypes;
                    int n5 = shortList != null ? shortList.getLength() : 0;
                    int n6 = n4 = shortList2 != null ? shortList2.getLength() : 0;
                    if (n5 != n4) continue;
                    for (n3 = 0; n3 < n5 && ((s5 = this.convertToPrimitiveKind(shortList.item(n3))) == (s4 = this.convertToPrimitiveKind(shortList2.item(n3))) || s5 == 1 && s4 == 2 || s5 == 2 && s4 == 1); ++n3) {
                    }
                    if (n3 != n5) continue;
                    n = 1;
                    break;
                }
                n = 1;
                break;
            }
            if (n == 0) {
                StringBuffer stringBuffer = new StringBuffer();
                this.appendEnumString(stringBuffer);
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string, stringBuffer.toString()});
            }
        }
        if ((this.fFacetsDefined & 0x400) != 0 && (n = this.fDVs[this.fValidationDV].getFractionDigits(object)) > this.fFractionDigits) {
            throw new InvalidDatatypeValueException("cvc-fractionDigits-valid", new Object[]{string, Integer.toString(n), Integer.toString(this.fFractionDigits)});
        }
        if ((this.fFacetsDefined & 0x200) != 0 && (n = this.fDVs[this.fValidationDV].getTotalDigits(object)) > this.fTotalDigits) {
            throw new InvalidDatatypeValueException("cvc-totalDigits-valid", new Object[]{string, Integer.toString(n), Integer.toString(this.fTotalDigits)});
        }
        if ((this.fFacetsDefined & 0x20) != 0 && (n = this.fDVs[this.fValidationDV].compare(object, this.fMaxInclusive)) != -1 && n != 0) {
            throw new InvalidDatatypeValueException("cvc-maxInclusive-valid", new Object[]{string, this.fMaxInclusive, this.fTypeName});
        }
        if ((this.fFacetsDefined & 0x40) != 0 && (n = this.fDVs[this.fValidationDV].compare(object, this.fMaxExclusive)) != -1) {
            throw new InvalidDatatypeValueException("cvc-maxExclusive-valid", new Object[]{string, this.fMaxExclusive, this.fTypeName});
        }
        if ((this.fFacetsDefined & 0x100) != 0 && (n = this.fDVs[this.fValidationDV].compare(object, this.fMinInclusive)) != 1 && n != 0) {
            throw new InvalidDatatypeValueException("cvc-minInclusive-valid", new Object[]{string, this.fMinInclusive, this.fTypeName});
        }
        if ((this.fFacetsDefined & 0x80) != 0 && (n = this.fDVs[this.fValidationDV].compare(object, this.fMinExclusive)) != 1) {
            throw new InvalidDatatypeValueException("cvc-minExclusive-valid", new Object[]{string, this.fMinExclusive, this.fTypeName});
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkExtraRules(ValidationContext validationContext, ValidatedInfo validatedInfo) throws InvalidDatatypeValueException {
        Object object = validatedInfo.actualValue;
        if (this.fVariety == 1) {
            this.fDVs[this.fValidationDV].checkExtraRules(object, validationContext);
        } else if (this.fVariety == 2) {
            ListDV.ListData listData = (ListDV.ListData)object;
            XSSimpleType xSSimpleType = validatedInfo.memberType;
            int n = listData.getLength();
            try {
                if (this.fItemType.fVariety == 3) {
                    XSSimpleTypeDecl[] xSSimpleTypeDeclArray = (XSSimpleTypeDecl[])validatedInfo.memberTypes;
                    for (int i = n - 1; i >= 0; --i) {
                        validatedInfo.actualValue = listData.item(i);
                        validatedInfo.memberType = xSSimpleTypeDeclArray[i];
                        this.fItemType.checkExtraRules(validationContext, validatedInfo);
                    }
                }
                for (int i = n - 1; i >= 0; --i) {
                    validatedInfo.actualValue = listData.item(i);
                    this.fItemType.checkExtraRules(validationContext, validatedInfo);
                }
            }
            finally {
                validatedInfo.actualValue = listData;
                validatedInfo.memberType = xSSimpleType;
            }
        } else {
            ((XSSimpleTypeDecl)validatedInfo.memberType).checkExtraRules(validationContext, validatedInfo);
        }
    }

    private Object getActualValue(Object object, ValidationContext validationContext, ValidatedInfo validatedInfo, boolean bl) throws InvalidDatatypeValueException {
        Object object2;
        int n;
        String string = bl ? this.normalize(object, this.fWhiteSpace) : object.toString();
        if ((this.fFacetsDefined & 8) != 0) {
            for (n = this.fPattern.size() - 1; n >= 0; --n) {
                object2 = (RegularExpression)this.fPattern.elementAt(n);
                if (((RegularExpression)object2).matches(string)) continue;
                throw new InvalidDatatypeValueException("cvc-pattern-valid", new Object[]{object, this.fPatternStr.elementAt(n), this.fTypeName});
            }
        }
        if (this.fVariety == 1) {
            Object object3;
            if (this.fPatternType != 0) {
                boolean bl2 = false;
                if (this.fPatternType == 1) {
                    bl2 = !XMLChar.isValidNmtoken(string);
                } else if (this.fPatternType == 2) {
                    bl2 = !XMLChar.isValidName(string);
                } else if (this.fPatternType == 3) {
                    boolean bl3 = bl2 = !XMLChar.isValidNCName(string);
                }
                if (bl2) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, SPECIAL_PATTERN_STRING[this.fPatternType]});
                }
            }
            validatedInfo.normalizedValue = string;
            validatedInfo.actualValue = object3 = this.fDVs[this.fValidationDV].getActualValue(string, validationContext);
            validatedInfo.actualValueType = this.fBuiltInKind;
            validatedInfo.actualType = this;
            return object3;
        }
        if (this.fVariety == 2) {
            object2 = new StringTokenizer(string, " ");
            n = ((StringTokenizer)object2).countTokens();
            Object[] objectArray = new Object[n];
            boolean bl4 = this.fItemType.getVariety() == 3;
            short[] sArray = new short[bl4 ? n : 1];
            if (!bl4) {
                sArray[0] = this.fItemType.fBuiltInKind;
            }
            XSSimpleTypeDecl[] xSSimpleTypeDeclArray = new XSSimpleTypeDecl[n];
            for (int i = 0; i < n; ++i) {
                objectArray[i] = this.fItemType.getActualValue(((StringTokenizer)object2).nextToken(), validationContext, validatedInfo, false);
                if (validationContext.needFacetChecking() && this.fItemType.fFacetsDefined != 0 && this.fItemType.fFacetsDefined != 16) {
                    this.fItemType.checkFacets(validatedInfo);
                }
                xSSimpleTypeDeclArray[i] = (XSSimpleTypeDecl)validatedInfo.memberType;
                if (!bl4) continue;
                sArray[i] = xSSimpleTypeDeclArray[i].fBuiltInKind;
            }
            ListDV.ListData listData = new ListDV.ListData(objectArray);
            validatedInfo.actualValue = listData;
            validatedInfo.actualValueType = (short)(bl4 ? 43 : 44);
            validatedInfo.memberType = null;
            validatedInfo.memberTypes = xSSimpleTypeDeclArray;
            validatedInfo.itemValueTypes = new ShortListImpl(sArray, sArray.length);
            validatedInfo.normalizedValue = string;
            validatedInfo.actualType = this;
            return listData;
        }
        object2 = this.fMemberTypes.length > 1 && object != null ? object.toString() : object;
        for (n = 0; n < this.fMemberTypes.length; ++n) {
            try {
                Object object4 = this.fMemberTypes[n].getActualValue(object2, validationContext, validatedInfo, true);
                if (validationContext.needFacetChecking() && this.fMemberTypes[n].fFacetsDefined != 0 && this.fMemberTypes[n].fFacetsDefined != 16) {
                    this.fMemberTypes[n].checkFacets(validatedInfo);
                }
                validatedInfo.memberType = this.fMemberTypes[n];
                validatedInfo.actualType = this;
                return object4;
            }
            catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                continue;
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < this.fMemberTypes.length; ++i) {
            if (i != 0) {
                stringBuffer.append(" | ");
            }
            XSSimpleTypeDecl xSSimpleTypeDecl = this.fMemberTypes[i];
            if (xSSimpleTypeDecl.fTargetNamespace != null) {
                stringBuffer.append('{');
                stringBuffer.append(xSSimpleTypeDecl.fTargetNamespace);
                stringBuffer.append('}');
            }
            stringBuffer.append(xSSimpleTypeDecl.fTypeName);
            if (xSSimpleTypeDecl.fEnumeration == null) continue;
            stringBuffer.append(" : ");
            xSSimpleTypeDecl.appendEnumString(stringBuffer);
        }
        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{object, this.fTypeName, stringBuffer.toString()});
    }

    @Override
    public boolean isEqual(Object object, Object object2) {
        if (object == null) {
            return false;
        }
        return object.equals(object2);
    }

    public boolean isIdentical(Object object, Object object2) {
        if (object == null) {
            return false;
        }
        return this.fDVs[this.fValidationDV].isIdentical(object, object2);
    }

    public static String normalize(String string, short s) {
        int n;
        int n2 = n = string == null ? 0 : string.length();
        if (n == 0 || s == 0) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (s == 1) {
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (c != '\t' && c != '\n' && c != '\r') {
                    stringBuffer.append(c);
                    continue;
                }
                stringBuffer.append(' ');
            }
        } else {
            boolean bl = true;
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (c != '\t' && c != '\n' && c != '\r' && c != ' ') {
                    stringBuffer.append(c);
                    bl = false;
                    continue;
                }
                while (i < n - 1 && ((c = string.charAt(i + 1)) == '\t' || c == '\n' || c == '\r' || c == ' ')) {
                    ++i;
                }
                if (i >= n - 1 || bl) continue;
                stringBuffer.append(' ');
            }
        }
        return stringBuffer.toString();
    }

    protected String normalize(Object object, short s) {
        if (object == null) {
            return null;
        }
        if ((this.fFacetsDefined & 8) == 0) {
            short s2 = fDVNormalizeType[this.fValidationDV];
            if (s2 == 0) {
                return object.toString();
            }
            if (s2 == 1) {
                return XMLChar.trim(object.toString());
            }
        }
        if (!(object instanceof StringBuffer)) {
            String string = object.toString();
            return XSSimpleTypeDecl.normalize(string, s);
        }
        StringBuffer stringBuffer = (StringBuffer)object;
        int n = stringBuffer.length();
        if (n == 0) {
            return "";
        }
        if (s == 0) {
            return stringBuffer.toString();
        }
        if (s == 1) {
            for (int i = 0; i < n; ++i) {
                char c = stringBuffer.charAt(i);
                if (c != '\t' && c != '\n' && c != '\r') continue;
                stringBuffer.setCharAt(i, ' ');
            }
        } else {
            int n2 = 0;
            boolean bl = true;
            for (int i = 0; i < n; ++i) {
                char c = stringBuffer.charAt(i);
                if (c != '\t' && c != '\n' && c != '\r' && c != ' ') {
                    stringBuffer.setCharAt(n2++, c);
                    bl = false;
                    continue;
                }
                while (i < n - 1 && ((c = stringBuffer.charAt(i + 1)) == '\t' || c == '\n' || c == '\r' || c == ' ')) {
                    ++i;
                }
                if (i >= n - 1 || bl) continue;
                stringBuffer.setCharAt(n2++, ' ');
            }
            stringBuffer.setLength(n2);
        }
        return stringBuffer.toString();
    }

    void reportError(String string, Object[] objectArray) throws InvalidDatatypeFacetException {
        throw new InvalidDatatypeFacetException(string, objectArray);
    }

    private String whiteSpaceValue(short s) {
        return WS_FACET_STRING[s];
    }

    @Override
    public short getOrdered() {
        return this.fOrdered;
    }

    @Override
    public boolean getBounded() {
        return this.fBounded;
    }

    @Override
    public boolean getFinite() {
        return this.fFinite;
    }

    @Override
    public boolean getNumeric() {
        return this.fNumeric;
    }

    @Override
    public boolean isDefinedFacet(short s) {
        if (this.fValidationDV == 0 || this.fValidationDV == 29) {
            return false;
        }
        if ((this.fFacetsDefined & s) != 0) {
            return true;
        }
        if (this.fPatternType != 0) {
            return s == 8;
        }
        if (this.fValidationDV == 24) {
            return s == 8 || s == 1024;
        }
        return false;
    }

    @Override
    public short getDefinedFacets() {
        if (this.fValidationDV == 0 || this.fValidationDV == 29) {
            return 0;
        }
        if (this.fPatternType != 0) {
            return (short)(this.fFacetsDefined | 8);
        }
        if (this.fValidationDV == 24) {
            return (short)(this.fFacetsDefined | 8 | 0x400);
        }
        return this.fFacetsDefined;
    }

    @Override
    public boolean isFixedFacet(short s) {
        if ((this.fFixedFacet & s) != 0) {
            return true;
        }
        if (this.fValidationDV == 24) {
            return s == 1024;
        }
        return false;
    }

    @Override
    public short getFixedFacets() {
        if (this.fValidationDV == 24) {
            return (short)(this.fFixedFacet | 0x400);
        }
        return this.fFixedFacet;
    }

    @Override
    public String getLexicalFacetValue(short s) {
        switch (s) {
            case 1: {
                return this.fLength == -1 ? null : Integer.toString(this.fLength);
            }
            case 2: {
                return this.fMinLength == -1 ? null : Integer.toString(this.fMinLength);
            }
            case 4: {
                return this.fMaxLength == -1 ? null : Integer.toString(this.fMaxLength);
            }
            case 16: {
                if (this.fValidationDV == 0 || this.fValidationDV == 29) {
                    return null;
                }
                return WS_FACET_STRING[this.fWhiteSpace];
            }
            case 32: {
                return this.fMaxInclusive == null ? null : this.fMaxInclusive.toString();
            }
            case 64: {
                return this.fMaxExclusive == null ? null : this.fMaxExclusive.toString();
            }
            case 128: {
                return this.fMinExclusive == null ? null : this.fMinExclusive.toString();
            }
            case 256: {
                return this.fMinInclusive == null ? null : this.fMinInclusive.toString();
            }
            case 512: {
                return this.fTotalDigits == -1 ? null : Integer.toString(this.fTotalDigits);
            }
            case 1024: {
                if (this.fValidationDV == 24) {
                    return "0";
                }
                return this.fFractionDigits == -1 ? null : Integer.toString(this.fFractionDigits);
            }
        }
        return null;
    }

    @Override
    public StringList getLexicalEnumeration() {
        if (this.fLexicalEnumeration == null) {
            if (this.fEnumeration == null) {
                return StringListImpl.EMPTY_LIST;
            }
            int n = this.fEnumerationSize;
            String[] stringArray = new String[n];
            for (int i = 0; i < n; ++i) {
                stringArray[i] = this.fEnumeration[i].normalizedValue;
            }
            this.fLexicalEnumeration = new StringListImpl(stringArray, n);
        }
        return this.fLexicalEnumeration;
    }

    public ObjectList getActualEnumeration() {
        if (this.fActualEnumeration == null) {
            this.fActualEnumeration = new AbstractObjectList(){

                @Override
                public int getLength() {
                    return XSSimpleTypeDecl.this.fEnumeration != null ? XSSimpleTypeDecl.this.fEnumerationSize : 0;
                }

                @Override
                public boolean contains(Object object) {
                    if (XSSimpleTypeDecl.this.fEnumeration == null) {
                        return false;
                    }
                    for (int i = 0; i < XSSimpleTypeDecl.this.fEnumerationSize; ++i) {
                        if (!XSSimpleTypeDecl.this.fEnumeration[i].getActualValue().equals(object)) continue;
                        return true;
                    }
                    return false;
                }

                @Override
                public Object item(int n) {
                    if (n < 0 || n >= this.getLength()) {
                        return null;
                    }
                    return XSSimpleTypeDecl.this.fEnumeration[n].getActualValue();
                }
            };
        }
        return this.fActualEnumeration;
    }

    public ObjectList getEnumerationItemTypeList() {
        if (this.fEnumerationItemTypeList == null) {
            if (this.fEnumeration == null) {
                return null;
            }
            this.fEnumerationItemTypeList = new AbstractObjectList(){

                @Override
                public int getLength() {
                    return XSSimpleTypeDecl.this.fEnumeration != null ? XSSimpleTypeDecl.this.fEnumerationSize : 0;
                }

                @Override
                public boolean contains(Object object) {
                    if (XSSimpleTypeDecl.this.fEnumeration == null || !(object instanceof ShortList)) {
                        return false;
                    }
                    for (int i = 0; i < XSSimpleTypeDecl.this.fEnumerationSize; ++i) {
                        if (((XSSimpleTypeDecl)XSSimpleTypeDecl.this).fEnumeration[i].itemValueTypes != object) continue;
                        return true;
                    }
                    return false;
                }

                @Override
                public Object item(int n) {
                    if (n < 0 || n >= this.getLength()) {
                        return null;
                    }
                    return ((XSSimpleTypeDecl)XSSimpleTypeDecl.this).fEnumeration[n].itemValueTypes;
                }
            };
        }
        return this.fEnumerationItemTypeList;
    }

    public ShortList getEnumerationTypeList() {
        if (this.fEnumerationTypeList == null) {
            if (this.fEnumeration == null) {
                return ShortListImpl.EMPTY_LIST;
            }
            short[] sArray = new short[this.fEnumerationSize];
            for (int i = 0; i < this.fEnumerationSize; ++i) {
                sArray[i] = this.fEnumeration[i].actualValueType;
            }
            this.fEnumerationTypeList = new ShortListImpl(sArray, this.fEnumerationSize);
        }
        return this.fEnumerationTypeList;
    }

    @Override
    public StringList getLexicalPattern() {
        if (this.fPatternType == 0 && this.fValidationDV != 24 && this.fPatternStr == null) {
            return StringListImpl.EMPTY_LIST;
        }
        if (this.fLexicalPattern == null) {
            String[] stringArray;
            int n;
            int n2 = n = this.fPatternStr == null ? 0 : this.fPatternStr.size();
            if (this.fPatternType == 1) {
                stringArray = new String[n + 1];
                stringArray[n] = "\\c+";
            } else if (this.fPatternType == 2) {
                stringArray = new String[n + 1];
                stringArray[n] = "\\i\\c*";
            } else if (this.fPatternType == 3) {
                stringArray = new String[n + 2];
                stringArray[n] = "\\i\\c*";
                stringArray[n + 1] = "[\\i-[:]][\\c-[:]]*";
            } else if (this.fValidationDV == 24) {
                stringArray = new String[n + 1];
                stringArray[n] = "[\\-+]?[0-9]+";
            } else {
                stringArray = new String[n];
            }
            for (int i = 0; i < n; ++i) {
                stringArray[i] = (String)this.fPatternStr.elementAt(i);
            }
            this.fLexicalPattern = new StringListImpl(stringArray, stringArray.length);
        }
        return this.fLexicalPattern;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    private void calcFundamentalFacets() {
        this.setOrdered();
        this.setNumeric();
        this.setBounded();
        this.setCardinality();
    }

    private void setOrdered() {
        if (this.fVariety == 1) {
            this.fOrdered = this.fBase.fOrdered;
        } else if (this.fVariety == 2) {
            this.fOrdered = 0;
        } else if (this.fVariety == 3) {
            int n = this.fMemberTypes.length;
            if (n == 0) {
                this.fOrdered = 1;
                return;
            }
            short s = this.getPrimitiveDV(this.fMemberTypes[0].fValidationDV);
            boolean bl = s != 0;
            boolean bl2 = this.fMemberTypes[0].fOrdered == 0;
            for (int i = 1; i < this.fMemberTypes.length && (bl || bl2); ++i) {
                if (bl) {
                    boolean bl3 = bl = s == this.getPrimitiveDV(this.fMemberTypes[i].fValidationDV);
                }
                if (!bl2) continue;
                bl2 = this.fMemberTypes[i].fOrdered == 0;
            }
            this.fOrdered = bl ? this.fMemberTypes[0].fOrdered : (bl2 ? (short)0 : 1);
        }
    }

    private void setNumeric() {
        if (this.fVariety == 1) {
            this.fNumeric = this.fBase.fNumeric;
        } else if (this.fVariety == 2) {
            this.fNumeric = false;
        } else if (this.fVariety == 3) {
            XSSimpleTypeDecl[] xSSimpleTypeDeclArray = this.fMemberTypes;
            for (int i = 0; i < xSSimpleTypeDeclArray.length; ++i) {
                if (xSSimpleTypeDeclArray[i].getNumeric()) continue;
                this.fNumeric = false;
                return;
            }
            this.fNumeric = true;
        }
    }

    private void setBounded() {
        if (this.fVariety == 1) {
            this.fBounded = !((this.fFacetsDefined & 0x100) == 0 && (this.fFacetsDefined & 0x80) == 0 || (this.fFacetsDefined & 0x20) == 0 && (this.fFacetsDefined & 0x40) == 0);
        } else if (this.fVariety == 2) {
            this.fBounded = (this.fFacetsDefined & 1) != 0 || (this.fFacetsDefined & 2) != 0 && (this.fFacetsDefined & 4) != 0;
        } else if (this.fVariety == 3) {
            XSSimpleTypeDecl[] xSSimpleTypeDeclArray = this.fMemberTypes;
            short s = 0;
            if (xSSimpleTypeDeclArray.length > 0) {
                s = this.getPrimitiveDV(xSSimpleTypeDeclArray[0].fValidationDV);
            }
            for (int i = 0; i < xSSimpleTypeDeclArray.length; ++i) {
                if (xSSimpleTypeDeclArray[i].getBounded() && s == this.getPrimitiveDV(xSSimpleTypeDeclArray[i].fValidationDV)) continue;
                this.fBounded = false;
                return;
            }
            this.fBounded = true;
        }
    }

    private boolean specialCardinalityCheck() {
        return this.fBase.fValidationDV == 9 || this.fBase.fValidationDV == 10 || this.fBase.fValidationDV == 11 || this.fBase.fValidationDV == 12 || this.fBase.fValidationDV == 13 || this.fBase.fValidationDV == 14;
    }

    private void setCardinality() {
        if (this.fVariety == 1) {
            this.fFinite = this.fBase.fFinite ? true : ((this.fFacetsDefined & 1) != 0 || (this.fFacetsDefined & 4) != 0 || (this.fFacetsDefined & 0x200) != 0 ? true : (!((this.fFacetsDefined & 0x100) == 0 && (this.fFacetsDefined & 0x80) == 0 || (this.fFacetsDefined & 0x20) == 0 && (this.fFacetsDefined & 0x40) == 0) ? (this.fFacetsDefined & 0x400) != 0 || this.specialCardinalityCheck() : false));
        } else if (this.fVariety == 2) {
            this.fFinite = (this.fFacetsDefined & 1) != 0 || (this.fFacetsDefined & 2) != 0 && (this.fFacetsDefined & 4) != 0;
        } else if (this.fVariety == 3) {
            XSSimpleTypeDecl[] xSSimpleTypeDeclArray = this.fMemberTypes;
            for (int i = 0; i < xSSimpleTypeDeclArray.length; ++i) {
                if (xSSimpleTypeDeclArray[i].getFinite()) continue;
                this.fFinite = false;
                return;
            }
            this.fFinite = true;
        }
    }

    private short getPrimitiveDV(short s) {
        if (s == 21 || s == 22 || s == 23) {
            return 1;
        }
        if (s == 24) {
            return 3;
        }
        return s;
    }

    @Override
    public boolean derivedFromType(XSTypeDefinition xSTypeDefinition, short s) {
        XSTypeDefinition xSTypeDefinition2;
        if (xSTypeDefinition == null) {
            return false;
        }
        while (xSTypeDefinition instanceof XSSimpleTypeDelegate) {
            xSTypeDefinition = ((XSSimpleTypeDelegate)xSTypeDefinition).type;
        }
        if (xSTypeDefinition.getBaseType() == xSTypeDefinition) {
            return true;
        }
        for (xSTypeDefinition2 = this; xSTypeDefinition2 != xSTypeDefinition && xSTypeDefinition2 != fAnySimpleType; xSTypeDefinition2 = xSTypeDefinition2.getBaseType()) {
        }
        return xSTypeDefinition2 == xSTypeDefinition;
    }

    @Override
    public boolean derivedFrom(String string, String string2, short s) {
        XSTypeDefinition xSTypeDefinition;
        if (string2 == null) {
            return false;
        }
        if (URI_SCHEMAFORSCHEMA.equals(string) && ANY_TYPE.equals(string2)) {
            return true;
        }
        for (xSTypeDefinition = this; !(string2.equals(xSTypeDefinition.getName()) && (string == null && xSTypeDefinition.getNamespace() == null || string != null && string.equals(xSTypeDefinition.getNamespace())) || xSTypeDefinition == fAnySimpleType); xSTypeDefinition = xSTypeDefinition.getBaseType()) {
        }
        return xSTypeDefinition != fAnySimpleType;
    }

    public boolean isDOMDerivedFrom(String string, String string2, int n) {
        if (string2 == null) {
            return false;
        }
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(string) && ANY_TYPE.equals(string2) && ((n & 1) != 0 || n == 0)) {
            return true;
        }
        if ((n & 1) != 0 && this.isDerivedByRestriction(string, string2, this)) {
            return true;
        }
        if ((n & 8) != 0 && this.isDerivedByList(string, string2, this)) {
            return true;
        }
        if ((n & 4) != 0 && this.isDerivedByUnion(string, string2, this)) {
            return true;
        }
        if ((n & 2) != 0 && (n & 1) == 0 && (n & 8) == 0 && (n & 4) == 0) {
            return false;
        }
        if ((n & 2) == 0 && (n & 1) == 0 && (n & 8) == 0 && (n & 4) == 0) {
            return this.isDerivedByAny(string, string2, this);
        }
        return false;
    }

    private boolean isDerivedByAny(String string, String string2, XSTypeDefinition xSTypeDefinition) {
        boolean bl = false;
        XSTypeDefinition xSTypeDefinition2 = null;
        while (xSTypeDefinition != null && xSTypeDefinition != xSTypeDefinition2) {
            if (string2.equals(xSTypeDefinition.getName()) && (string == null && xSTypeDefinition.getNamespace() == null || string != null && string.equals(xSTypeDefinition.getNamespace()))) {
                bl = true;
                break;
            }
            if (this.isDerivedByRestriction(string, string2, xSTypeDefinition)) {
                return true;
            }
            if (this.isDerivedByList(string, string2, xSTypeDefinition)) {
                return true;
            }
            if (this.isDerivedByUnion(string, string2, xSTypeDefinition)) {
                return true;
            }
            xSTypeDefinition2 = xSTypeDefinition;
            if (((XSSimpleTypeDecl)xSTypeDefinition).getVariety() == 0 || ((XSSimpleTypeDecl)xSTypeDefinition).getVariety() == 1) {
                xSTypeDefinition = xSTypeDefinition.getBaseType();
                continue;
            }
            if (((XSSimpleTypeDecl)xSTypeDefinition).getVariety() == 3) {
                int n = 0;
                if (n >= ((XSSimpleTypeDecl)xSTypeDefinition).getMemberTypes().getLength()) continue;
                return this.isDerivedByAny(string, string2, (XSTypeDefinition)((XSSimpleTypeDecl)xSTypeDefinition).getMemberTypes().item(n));
            }
            if (((XSSimpleTypeDecl)xSTypeDefinition).getVariety() != 2) continue;
            xSTypeDefinition = ((XSSimpleTypeDecl)xSTypeDefinition).getItemType();
        }
        return bl;
    }

    private boolean isDerivedByRestriction(String string, String string2, XSTypeDefinition xSTypeDefinition) {
        XSTypeDefinition xSTypeDefinition2 = null;
        while (xSTypeDefinition != null && xSTypeDefinition != xSTypeDefinition2) {
            if (string2.equals(xSTypeDefinition.getName()) && (string != null && string.equals(xSTypeDefinition.getNamespace()) || xSTypeDefinition.getNamespace() == null && string == null)) {
                return true;
            }
            xSTypeDefinition2 = xSTypeDefinition;
            xSTypeDefinition = xSTypeDefinition.getBaseType();
        }
        return false;
    }

    private boolean isDerivedByList(String string, String string2, XSTypeDefinition xSTypeDefinition) {
        XSSimpleTypeDefinition xSSimpleTypeDefinition;
        return xSTypeDefinition != null && ((XSSimpleTypeDefinition)xSTypeDefinition).getVariety() == 2 && (xSSimpleTypeDefinition = ((XSSimpleTypeDefinition)xSTypeDefinition).getItemType()) != null && this.isDerivedByRestriction(string, string2, xSSimpleTypeDefinition);
    }

    private boolean isDerivedByUnion(String string, String string2, XSTypeDefinition xSTypeDefinition) {
        if (xSTypeDefinition != null && ((XSSimpleTypeDefinition)xSTypeDefinition).getVariety() == 3) {
            XSObjectList xSObjectList = ((XSSimpleTypeDefinition)xSTypeDefinition).getMemberTypes();
            for (int i = 0; i < xSObjectList.getLength(); ++i) {
                if (xSObjectList.item(i) == null || !this.isDerivedByRestriction(string, string2, (XSSimpleTypeDefinition)xSObjectList.item(i))) continue;
                return true;
            }
        }
        return false;
    }

    public void reset() {
        if (this.fIsImmutable) {
            return;
        }
        this.fItemType = null;
        this.fMemberTypes = null;
        this.fTypeName = null;
        this.fTargetNamespace = null;
        this.fFinalSet = 0;
        this.fBase = null;
        this.fVariety = (short)-1;
        this.fValidationDV = (short)-1;
        this.fFacetsDefined = 0;
        this.fFixedFacet = 0;
        this.fWhiteSpace = 0;
        this.fLength = -1;
        this.fMinLength = -1;
        this.fMaxLength = -1;
        this.fTotalDigits = -1;
        this.fFractionDigits = -1;
        this.fPattern = null;
        this.fPatternStr = null;
        this.fEnumeration = null;
        this.fLexicalPattern = null;
        this.fLexicalEnumeration = null;
        this.fActualEnumeration = null;
        this.fEnumerationTypeList = null;
        this.fEnumerationItemTypeList = null;
        this.fMaxInclusive = null;
        this.fMaxExclusive = null;
        this.fMinExclusive = null;
        this.fMinInclusive = null;
        this.lengthAnnotation = null;
        this.minLengthAnnotation = null;
        this.maxLengthAnnotation = null;
        this.whiteSpaceAnnotation = null;
        this.totalDigitsAnnotation = null;
        this.fractionDigitsAnnotation = null;
        this.patternAnnotations = null;
        this.enumerationAnnotations = null;
        this.maxInclusiveAnnotation = null;
        this.maxExclusiveAnnotation = null;
        this.minInclusiveAnnotation = null;
        this.minExclusiveAnnotation = null;
        this.fPatternType = 0;
        this.fAnnotations = null;
        this.fFacets = null;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }

    public void setNamespaceItem(XSNamespaceItem xSNamespaceItem) {
        this.fNamespaceItem = xSNamespaceItem;
    }

    public String toString() {
        return this.fTargetNamespace + "," + this.fTypeName;
    }

    @Override
    public XSObjectList getFacets() {
        if (this.fFacets == null && (this.fFacetsDefined != 0 || this.fValidationDV == 24)) {
            XSObject[] xSObjectArray = new XSFacetImpl[10];
            int n = 0;
            if ((this.fFacetsDefined & 0x10) != 0 && this.fValidationDV != 0 && this.fValidationDV != 29) {
                xSObjectArray[n] = new XSFacetImpl(16, WS_FACET_STRING[this.fWhiteSpace], 0, null, (this.fFixedFacet & 0x10) != 0, this.whiteSpaceAnnotation);
                ++n;
            }
            if (this.fLength != -1) {
                xSObjectArray[n] = new XSFacetImpl(1, Integer.toString(this.fLength), this.fLength, null, (this.fFixedFacet & 1) != 0, this.lengthAnnotation);
                ++n;
            }
            if (this.fMinLength != -1) {
                xSObjectArray[n] = new XSFacetImpl(2, Integer.toString(this.fMinLength), this.fMinLength, null, (this.fFixedFacet & 2) != 0, this.minLengthAnnotation);
                ++n;
            }
            if (this.fMaxLength != -1) {
                xSObjectArray[n] = new XSFacetImpl(4, Integer.toString(this.fMaxLength), this.fMaxLength, null, (this.fFixedFacet & 4) != 0, this.maxLengthAnnotation);
                ++n;
            }
            if (this.fTotalDigits != -1) {
                xSObjectArray[n] = new XSFacetImpl(512, Integer.toString(this.fTotalDigits), this.fTotalDigits, null, (this.fFixedFacet & 0x200) != 0, this.totalDigitsAnnotation);
                ++n;
            }
            if (this.fValidationDV == 24) {
                xSObjectArray[n] = new XSFacetImpl(1024, "0", 0, null, true, this.fractionDigitsAnnotation);
                ++n;
            } else if (this.fFractionDigits != -1) {
                xSObjectArray[n] = new XSFacetImpl(1024, Integer.toString(this.fFractionDigits), this.fFractionDigits, null, (this.fFixedFacet & 0x400) != 0, this.fractionDigitsAnnotation);
                ++n;
            }
            if (this.fMaxInclusive != null) {
                xSObjectArray[n] = new XSFacetImpl(32, this.fMaxInclusive.toString(), 0, this.fMaxInclusive, (this.fFixedFacet & 0x20) != 0, this.maxInclusiveAnnotation);
                ++n;
            }
            if (this.fMaxExclusive != null) {
                xSObjectArray[n] = new XSFacetImpl(64, this.fMaxExclusive.toString(), 0, this.fMaxExclusive, (this.fFixedFacet & 0x40) != 0, this.maxExclusiveAnnotation);
                ++n;
            }
            if (this.fMinExclusive != null) {
                xSObjectArray[n] = new XSFacetImpl(128, this.fMinExclusive.toString(), 0, this.fMinExclusive, (this.fFixedFacet & 0x80) != 0, this.minExclusiveAnnotation);
                ++n;
            }
            if (this.fMinInclusive != null) {
                xSObjectArray[n] = new XSFacetImpl(256, this.fMinInclusive.toString(), 0, this.fMinInclusive, (this.fFixedFacet & 0x100) != 0, this.minInclusiveAnnotation);
                ++n;
            }
            this.fFacets = n > 0 ? new XSObjectListImpl(xSObjectArray, n) : XSObjectListImpl.EMPTY_LIST;
        }
        return this.fFacets != null ? this.fFacets : XSObjectListImpl.EMPTY_LIST;
    }

    @Override
    public XSObject getFacet(int n) {
        if (n == 2048 || n == 8) {
            XSObjectList xSObjectList = this.getMultiValueFacets();
            for (int i = 0; i < xSObjectList.getLength(); ++i) {
                XSMultiValueFacet xSMultiValueFacet = (XSMultiValueFacet)xSObjectList.item(i);
                if (xSMultiValueFacet.getFacetKind() != n) continue;
                return xSMultiValueFacet;
            }
        } else {
            XSObjectList xSObjectList = this.getFacets();
            for (int i = 0; i < xSObjectList.getLength(); ++i) {
                XSFacet xSFacet = (XSFacet)xSObjectList.item(i);
                if (xSFacet.getFacetKind() != n) continue;
                return xSFacet;
            }
        }
        return null;
    }

    @Override
    public XSObjectList getMultiValueFacets() {
        if (this.fMultiValueFacets == null && ((this.fFacetsDefined & 0x800) != 0 || (this.fFacetsDefined & 8) != 0 || this.fPatternType != 0 || this.fValidationDV == 24)) {
            XSObject[] xSObjectArray = new XSMVFacetImpl[2];
            int n = 0;
            if ((this.fFacetsDefined & 8) != 0 || this.fPatternType != 0 || this.fValidationDV == 24) {
                xSObjectArray[n] = new XSMVFacetImpl(8, this.getLexicalPattern(), null, this.patternAnnotations);
                ++n;
            }
            if (this.fEnumeration != null) {
                xSObjectArray[n] = new XSMVFacetImpl(2048, this.getLexicalEnumeration(), new ObjectListImpl(this.fEnumeration, this.fEnumerationSize), this.enumerationAnnotations);
                ++n;
            }
            this.fMultiValueFacets = new XSObjectListImpl(xSObjectArray, n);
        }
        return this.fMultiValueFacets != null ? this.fMultiValueFacets : XSObjectListImpl.EMPTY_LIST;
    }

    public Object getMinInclusiveValue() {
        return this.fMinInclusive;
    }

    public Object getMinExclusiveValue() {
        return this.fMinExclusive;
    }

    public Object getMaxInclusiveValue() {
        return this.fMaxInclusive;
    }

    public Object getMaxExclusiveValue() {
        return this.fMaxExclusive;
    }

    public void setAnonymous(boolean bl) {
        this.fAnonymous = bl;
    }

    @Override
    public String getTypeNamespace() {
        return this.getNamespace();
    }

    @Override
    public boolean isDerivedFrom(String string, String string2, int n) {
        return this.isDOMDerivedFrom(string, string2, n);
    }

    private short convertToPrimitiveKind(short s) {
        if (s <= 20) {
            return s;
        }
        if (s <= 29) {
            return 2;
        }
        if (s <= 42) {
            return 4;
        }
        return s;
    }

    private void appendEnumString(StringBuffer stringBuffer) {
        stringBuffer.append('[');
        for (int i = 0; i < this.fEnumerationSize; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(this.fEnumeration[i].actualValue);
        }
        stringBuffer.append(']');
    }

    private static abstract class AbstractObjectList
    extends AbstractList
    implements ObjectList {
        private AbstractObjectList() {
        }

        public Object get(int n) {
            if (n >= 0 && n < this.getLength()) {
                return this.item(n);
            }
            throw new IndexOutOfBoundsException("Index: " + n);
        }

        @Override
        public int size() {
            return this.getLength();
        }
    }

    private static final class XSMVFacetImpl
    implements XSMultiValueFacet {
        final short kind;
        final XSObjectList annotations;
        final StringList svalues;
        final ObjectList avalues;

        public XSMVFacetImpl(short s, StringList stringList, ObjectList objectList, XSObjectList xSObjectList) {
            this.kind = s;
            this.svalues = stringList;
            this.avalues = objectList;
            this.annotations = xSObjectList != null ? xSObjectList : XSObjectListImpl.EMPTY_LIST;
        }

        @Override
        public short getFacetKind() {
            return this.kind;
        }

        @Override
        public XSObjectList getAnnotations() {
            return this.annotations;
        }

        @Override
        public StringList getLexicalFacetValues() {
            return this.svalues;
        }

        @Override
        public ObjectList getEnumerationValues() {
            return this.avalues;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getNamespace() {
            return null;
        }

        @Override
        public XSNamespaceItem getNamespaceItem() {
            return null;
        }

        @Override
        public short getType() {
            return 14;
        }
    }

    private static final class XSFacetImpl
    implements XSFacet {
        final short kind;
        final String svalue;
        final int ivalue;
        Object avalue;
        final boolean fixed;
        final XSObjectList annotations;

        public XSFacetImpl(short s, String string, int n, Object object, boolean bl, XSAnnotation xSAnnotation) {
            this.kind = s;
            this.svalue = string;
            this.ivalue = n;
            this.avalue = object;
            this.fixed = bl;
            if (xSAnnotation != null) {
                this.annotations = new XSObjectListImpl();
                ((XSObjectListImpl)this.annotations).addXSObject(xSAnnotation);
            } else {
                this.annotations = XSObjectListImpl.EMPTY_LIST;
            }
        }

        @Override
        public XSAnnotation getAnnotation() {
            return (XSAnnotation)this.annotations.item(0);
        }

        @Override
        public XSObjectList getAnnotations() {
            return this.annotations;
        }

        @Override
        public short getFacetKind() {
            return this.kind;
        }

        @Override
        public String getLexicalFacetValue() {
            return this.svalue;
        }

        @Override
        public Object getActualFacetValue() {
            if (this.avalue == null) {
                this.avalue = this.kind == 16 ? this.svalue : BigInteger.valueOf(this.ivalue);
            }
            return this.avalue;
        }

        @Override
        public int getIntFacetValue() {
            return this.ivalue;
        }

        @Override
        public boolean getFixed() {
            return this.fixed;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getNamespace() {
            return null;
        }

        @Override
        public XSNamespaceItem getNamespaceItem() {
            return null;
        }

        @Override
        public short getType() {
            return 13;
        }
    }

    static final class ValidationContextImpl
    implements ValidationContext {
        final ValidationContext fExternal;
        NamespaceContext fNSContext;

        ValidationContextImpl(ValidationContext validationContext) {
            this.fExternal = validationContext;
        }

        void setNSContext(NamespaceContext namespaceContext) {
            this.fNSContext = namespaceContext;
        }

        @Override
        public boolean needFacetChecking() {
            return this.fExternal.needFacetChecking();
        }

        @Override
        public boolean needExtraChecking() {
            return this.fExternal.needExtraChecking();
        }

        @Override
        public boolean needToNormalize() {
            return this.fExternal.needToNormalize();
        }

        @Override
        public boolean useNamespaces() {
            return true;
        }

        @Override
        public boolean isEntityDeclared(String string) {
            return this.fExternal.isEntityDeclared(string);
        }

        @Override
        public boolean isEntityUnparsed(String string) {
            return this.fExternal.isEntityUnparsed(string);
        }

        @Override
        public boolean isIdDeclared(String string) {
            return this.fExternal.isIdDeclared(string);
        }

        @Override
        public void addId(String string) {
            this.fExternal.addId(string);
        }

        @Override
        public void addIdRef(String string) {
            this.fExternal.addIdRef(string);
        }

        @Override
        public String getSymbol(String string) {
            return this.fExternal.getSymbol(string);
        }

        @Override
        public String getURI(String string) {
            if (this.fNSContext == null) {
                return this.fExternal.getURI(string);
            }
            return this.fNSContext.getURI(string);
        }

        @Override
        public Locale getLocale() {
            return this.fExternal.getLocale();
        }
    }
}

