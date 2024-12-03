/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.AnyURIType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Base64BinaryType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BooleanType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ByteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateTimeType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DateType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DoubleType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DurationType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.EntityType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ErrorType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FloatType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GDayType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GMonthDayType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GMonthType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GYearMonthType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.GYearType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.HexBinaryType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDREFType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LanguageType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ListType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LongType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NameType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NcnameType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NegativeIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NmtokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NonNegativeIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NonPositiveIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NormalizedStringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NumberType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.PositiveIntegerType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Proxy;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.QnameType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ShortType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TimeType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnionType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedByteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedIntType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedLongType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnsignedShortType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DatatypeFactory {
    private static final Map builtinType = DatatypeFactory.createInitialBuiltinTypesMap();

    private DatatypeFactory() {
    }

    public static XSDatatype deriveByList(String nsUri, String newTypeName, XSDatatype itemType) throws DatatypeException {
        if (itemType instanceof ErrorType) {
            return itemType;
        }
        return new ListType(nsUri, newTypeName, (XSDatatypeImpl)itemType);
    }

    public static XSDatatype deriveByList(String newTypeName, XSDatatype itemType) throws DatatypeException {
        return DatatypeFactory.deriveByList("", newTypeName, itemType);
    }

    public static XSDatatype deriveByUnion(String nsUri, String newTypeName, XSDatatype[] memberTypes) throws DatatypeException {
        for (int i = 0; i < memberTypes.length; ++i) {
            if (!(memberTypes[i] instanceof ErrorType)) continue;
            return memberTypes[i];
        }
        return new UnionType(nsUri, newTypeName, memberTypes);
    }

    public static XSDatatype deriveByUnion(String newTypeName, XSDatatype[] memberTypes) throws DatatypeException {
        return DatatypeFactory.deriveByUnion("", newTypeName, memberTypes);
    }

    public static XSDatatype deriveByUnion(String newTypeName, Collection memberTypes) throws DatatypeException {
        return DatatypeFactory.deriveByUnion("", newTypeName, memberTypes);
    }

    public static XSDatatype deriveByUnion(String nsUri, String newTypeName, Collection memberTypes) throws DatatypeException {
        XSDatatype[] m = new XSDatatypeImpl[memberTypes.size()];
        int n = 0;
        Iterator itr = memberTypes.iterator();
        while (itr.hasNext()) {
            for (int i = 0; i < m.length; ++i) {
                m[i] = (XSDatatypeImpl)itr.next();
            }
            ++n;
        }
        return DatatypeFactory.deriveByUnion(nsUri, newTypeName, m);
    }

    private static void add(Map m, XSDatatypeImpl type) {
        String name = type.getName();
        if (name == null) {
            throw new IllegalArgumentException("anonymous type");
        }
        if (m.containsKey(name)) {
            throw new IllegalArgumentException("multiple definition");
        }
        m.put(name, type);
    }

    public static synchronized XSDatatype getTypeByName(String dataTypeName) throws DatatypeException {
        XSDatatype dt = (XSDatatype)builtinType.get(dataTypeName);
        if (dt != null) {
            return dt;
        }
        try {
            if (dataTypeName.equals("float")) {
                DatatypeFactory.add(builtinType, FloatType.theInstance);
            } else if (dataTypeName.equals("double")) {
                DatatypeFactory.add(builtinType, DoubleType.theInstance);
            } else if (dataTypeName.equals("duration")) {
                DatatypeFactory.add(builtinType, DurationType.theInstance);
            } else if (dataTypeName.equals("dateTime")) {
                DatatypeFactory.add(builtinType, DateTimeType.theInstance);
            } else if (dataTypeName.equals("time")) {
                DatatypeFactory.add(builtinType, TimeType.theInstance);
            } else if (dataTypeName.equals("date")) {
                DatatypeFactory.add(builtinType, DateType.theInstance);
            } else if (dataTypeName.equals("gYearMonth")) {
                DatatypeFactory.add(builtinType, GYearMonthType.theInstance);
            } else if (dataTypeName.equals("gYear")) {
                DatatypeFactory.add(builtinType, GYearType.theInstance);
            } else if (dataTypeName.equals("gMonthDay")) {
                DatatypeFactory.add(builtinType, GMonthDayType.theInstance);
            } else if (dataTypeName.equals("gDay")) {
                DatatypeFactory.add(builtinType, GDayType.theInstance);
            } else if (dataTypeName.equals("gMonth")) {
                DatatypeFactory.add(builtinType, GMonthType.theInstance);
            } else if (dataTypeName.equals("hexBinary")) {
                DatatypeFactory.add(builtinType, HexBinaryType.theInstance);
            } else if (dataTypeName.equals("base64Binary")) {
                DatatypeFactory.add(builtinType, Base64BinaryType.theInstance);
            } else if (dataTypeName.equals("anyURI")) {
                DatatypeFactory.add(builtinType, AnyURIType.theInstance);
            } else if (dataTypeName.equals("ENTITY")) {
                DatatypeFactory.add(builtinType, EntityType.theInstance);
            } else if (dataTypeName.equals("language")) {
                DatatypeFactory.add(builtinType, LanguageType.theInstance);
            } else if (dataTypeName.equals("ID")) {
                DatatypeFactory.add(builtinType, IDType.theInstance);
            } else if (dataTypeName.equals("IDREF")) {
                DatatypeFactory.add(builtinType, IDREFType.theInstance);
            } else if (dataTypeName.equals("IDREFS")) {
                DatatypeFactory.add(builtinType, DatatypeFactory.createBuiltinList("IDREFS", IDREFType.theInstance));
            } else if (dataTypeName.equals("ENTITIES")) {
                DatatypeFactory.add(builtinType, DatatypeFactory.createBuiltinList("ENTITIES", EntityType.theInstance));
            } else if (dataTypeName.equals("NMTOKENS")) {
                DatatypeFactory.add(builtinType, DatatypeFactory.createBuiltinList("NMTOKENS", NmtokenType.theInstance));
            } else if (dataTypeName.equals("NOTATION")) {
                DatatypeFactory.add(builtinType, new StringType("NOTATION", WhiteSpaceProcessor.theCollapse, false));
            } else if (dataTypeName.equals("nonPositiveInteger")) {
                DatatypeFactory.add(builtinType, NonPositiveIntegerType.theInstance);
            } else if (dataTypeName.equals("unsignedLong")) {
                DatatypeFactory.add(builtinType, UnsignedLongType.theInstance);
            } else if (dataTypeName.equals("unsignedInt")) {
                DatatypeFactory.add(builtinType, UnsignedIntType.theInstance);
            } else if (dataTypeName.equals("unsignedShort")) {
                DatatypeFactory.add(builtinType, UnsignedShortType.theInstance);
            } else if (dataTypeName.equals("unsignedByte")) {
                DatatypeFactory.add(builtinType, UnsignedByteType.theInstance);
            } else if (dataTypeName.equals("anySimpleType")) {
                DatatypeFactory.add(builtinType, SimpleURType.theInstance);
            }
        }
        catch (DatatypeException dte) {
            throw new Error();
        }
        dt = (XSDatatype)builtinType.get(dataTypeName);
        if (dt != null) {
            return dt;
        }
        throw new DatatypeException("undefined type name:" + dataTypeName);
    }

    private static XSDatatypeImpl createBuiltinList(String name, XSDatatypeImpl item) throws DatatypeException {
        TypeIncubator ti = new TypeIncubator(new ListType(null, null, item));
        ti.addFacet("minLength", "1", false, null);
        return new Proxy("http://www.w3.org/2001/XMLSchema", name, ti.derive(null, null)){

            private Object readResolve() {
                try {
                    return DatatypeFactory.getTypeByName(this.getName());
                }
                catch (DatatypeException e) {
                    e.printStackTrace();
                    throw new InternalError(e.getMessage());
                }
            }
        };
    }

    private static Map createInitialBuiltinTypesMap() {
        HashMap m = new HashMap();
        DatatypeFactory.add(m, StringType.theInstance);
        DatatypeFactory.add(m, BooleanType.theInstance);
        DatatypeFactory.add(m, NumberType.theInstance);
        DatatypeFactory.add(m, QnameType.theInstance);
        DatatypeFactory.add(m, NormalizedStringType.theInstance);
        DatatypeFactory.add(m, TokenType.theInstance);
        DatatypeFactory.add(m, NmtokenType.theInstance);
        DatatypeFactory.add(m, NameType.theInstance);
        DatatypeFactory.add(m, NcnameType.theInstance);
        DatatypeFactory.add(m, IntegerType.theInstance);
        DatatypeFactory.add(m, NegativeIntegerType.theInstance);
        DatatypeFactory.add(m, LongType.theInstance);
        DatatypeFactory.add(m, IntType.theInstance);
        DatatypeFactory.add(m, ShortType.theInstance);
        DatatypeFactory.add(m, ByteType.theInstance);
        DatatypeFactory.add(m, NonNegativeIntegerType.theInstance);
        DatatypeFactory.add(m, PositiveIntegerType.theInstance);
        return m;
    }
}

