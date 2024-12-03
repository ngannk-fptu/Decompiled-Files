/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.EnumerationFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ErrorType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FinalComponent;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FractionDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MaxExclusiveFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MaxInclusiveFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MaxLengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MinExclusiveFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MinInclusiveFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MinLengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.PatternFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.RangeFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TotalDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class TypeIncubator {
    private final Map impl = new HashMap();
    private final XSDatatypeImpl baseType;
    private static final String[][] exclusiveFacetPairs = new String[][]{{"length", "minLength"}, {"length", "maxLength"}, {"maxInclusive", "maxExclusive"}, {"minInclusive", "minExclusive"}};

    public TypeIncubator(XSDatatype baseType) {
        this.baseType = (XSDatatypeImpl)baseType;
        if (baseType == null) {
            throw new IllegalArgumentException();
        }
    }

    public void add(String name, String strValue, boolean fixed, ValidationContext context) throws DatatypeException {
        this.addFacet(name, strValue, fixed, context);
    }

    public void addFacet(String name, String strValue, boolean fixed, ValidationContext context) throws DatatypeException {
        Object value;
        if (this.baseType instanceof ErrorType) {
            return;
        }
        switch (this.baseType.isFacetApplicable(name)) {
            case 0: {
                break;
            }
            case -1: {
                return;
            }
            case -2: {
                throw new DatatypeException(XSDatatypeImpl.localize("BadTypeException.NotApplicableFacet", name));
            }
            default: {
                throw new Error();
            }
        }
        if (TypeIncubator.isValueFacet(name)) {
            value = this.baseType.createValue(strValue, context);
            if (value == null) {
                throw new DatatypeException(XSDatatypeImpl.localize("EnumerationFacet.InvalidValueForThisType", strValue, this.baseType.displayName()));
            }
        } else {
            value = strValue;
        }
        if (TypeIncubator.isRepeatable(name)) {
            FacetInfo fi;
            if (this.impl.containsKey(name)) {
                fi = (FacetInfo)this.impl.get(name);
            } else {
                fi = new FacetInfo(new Vector(), fixed);
                this.impl.put(name, fi);
            }
            ((Vector)fi.value).add(value);
            fi.fixed |= fixed;
        } else {
            if (this.impl.containsKey(name)) {
                throw new DatatypeException(XSDatatypeImpl.localize("BadTypeException.DuplicateFacet", name));
            }
            this.impl.put(name, new FacetInfo(value, fixed));
        }
    }

    public XSDatatypeImpl derive(String newName) throws DatatypeException {
        return this.derive("", newName);
    }

    public XSDatatypeImpl derive(String newNameUri, String newLocalName) throws DatatypeException {
        if (this.baseType instanceof ErrorType) {
            return this.baseType;
        }
        if (this.baseType.isFinal(1)) {
            throw new DatatypeException(XSDatatypeImpl.localize("BadTypeException.InvalidBaseType", this.baseType.displayName()));
        }
        if (this.isEmpty()) {
            if (newNameUri == null && newLocalName == null) {
                return this.baseType;
            }
            return new FinalComponent(newNameUri, newLocalName, this.baseType, 0);
        }
        XSDatatypeImpl r = this.baseType;
        for (int i = 0; i < exclusiveFacetPairs.length; ++i) {
            if (!this.contains(exclusiveFacetPairs[i][0]) || !this.contains(exclusiveFacetPairs[i][1])) continue;
            throw new DatatypeException(XSDatatypeImpl.localize("XAndYAreExclusive", exclusiveFacetPairs[i][0], exclusiveFacetPairs[i][1]));
        }
        if (this.contains("totalDigits")) {
            r = new TotalDigitsFacet(newNameUri, newLocalName, r, this.getPositiveInteger("totalDigits"), this.isFixed("totalDigits"));
        }
        if (this.contains("fractionDigits")) {
            r = new FractionDigitsFacet(newNameUri, newLocalName, r, this.getNonNegativeInteger("fractionDigits"), this.isFixed("fractionDigits"));
        }
        if (this.contains("minInclusive")) {
            r = new MinInclusiveFacet(newNameUri, newLocalName, r, this.getFacet("minInclusive"), this.isFixed("minInclusive"));
        }
        if (this.contains("maxInclusive")) {
            r = new MaxInclusiveFacet(newNameUri, newLocalName, r, this.getFacet("maxInclusive"), this.isFixed("maxInclusive"));
        }
        if (this.contains("minExclusive")) {
            r = new MinExclusiveFacet(newNameUri, newLocalName, r, this.getFacet("minExclusive"), this.isFixed("minExclusive"));
        }
        if (this.contains("maxExclusive")) {
            r = new MaxExclusiveFacet(newNameUri, newLocalName, r, this.getFacet("maxExclusive"), this.isFixed("maxExclusive"));
        }
        if (this.contains("length")) {
            r = new LengthFacet(newNameUri, newLocalName, r, this);
        }
        if (this.contains("minLength")) {
            r = new MinLengthFacet(newNameUri, newLocalName, r, this);
        }
        if (this.contains("maxLength")) {
            r = new MaxLengthFacet(newNameUri, newLocalName, r, this);
        }
        if (this.contains("whiteSpace")) {
            r = new WhiteSpaceFacet(newNameUri, newLocalName, r, this);
        }
        if (this.contains("pattern")) {
            r = new PatternFacet(newNameUri, newLocalName, r, this);
        }
        if (this.contains("enumeration")) {
            r = new EnumerationFacet(newNameUri, newLocalName, r, this.getVector("enumeration"), this.isFixed("enumeration"));
        }
        DataTypeWithFacet o1 = r.getFacetObject("maxLength");
        DataTypeWithFacet o2 = r.getFacetObject("minLength");
        if (o1 != null && o2 != null && ((MaxLengthFacet)o1).maxLength < ((MinLengthFacet)o2).minLength) {
            throw TypeIncubator.reportFacetInconsistency(newLocalName, o1, "maxLength", o2, "minLength");
        }
        o1 = r.getFacetObject("fractionDigits");
        o2 = r.getFacetObject("totalDigits");
        if (o1 != null && o2 != null && ((FractionDigitsFacet)o1).scale > ((TotalDigitsFacet)o2).precision) {
            throw TypeIncubator.reportFacetInconsistency(newLocalName, o1, "fractionDigits", o2, "totalDigits");
        }
        TypeIncubator.checkRangeConsistency(r, "minInclusive", "maxInclusive");
        TypeIncubator.checkRangeConsistency(r, "minExclusive", "maxExclusive");
        TypeIncubator.checkRangeConsistency(r, "minInclusive", "maxExclusive");
        TypeIncubator.checkRangeConsistency(r, "minExclusive", "maxInclusive");
        return r;
    }

    private static void checkRangeConsistency(XSDatatypeImpl newType, String facetName1, String facetName2) throws DatatypeException {
        int c;
        DataTypeWithFacet o1 = newType.getFacetObject(facetName1);
        DataTypeWithFacet o2 = newType.getFacetObject(facetName2);
        if (o1 != null && o2 != null && (c = ((Comparator)((Object)o1.getConcreteType())).compare(((RangeFacet)o1).limitValue, ((RangeFacet)o2).limitValue)) == 1) {
            throw TypeIncubator.reportFacetInconsistency(newType.displayName(), o1, facetName1, o2, facetName2);
        }
    }

    private static DatatypeException reportFacetInconsistency(String newName, DataTypeWithFacet o1, String facetName1, DataTypeWithFacet o2, String facetName2) {
        String o2typeName;
        String o1typeName = o1.getName();
        if (o1typeName.equals(o2typeName = o2.getName())) {
            return new DatatypeException(XSDatatypeImpl.localize("InconsistentFacets.1", facetName1, facetName2));
        }
        if (o1typeName.equals(newName)) {
            return new DatatypeException(XSDatatypeImpl.localize("InconsistentFacets.2", facetName1, o2.displayName(), facetName2));
        }
        if (o2typeName.equals(newName)) {
            return new DatatypeException(XSDatatypeImpl.localize("InconsistentFacets.2", facetName2, o1.displayName(), facetName1));
        }
        throw new IllegalStateException();
    }

    private static boolean isValueFacet(String facetName) {
        return facetName.equals("enumeration") || facetName.equals("maxExclusive") || facetName.equals("minExclusive") || facetName.equals("maxInclusive") || facetName.equals("minInclusive");
    }

    private static boolean isRepeatable(String facetName) {
        return facetName.equals("enumeration") || facetName.equals("pattern");
    }

    public boolean isFixed(String facetName) {
        return ((FacetInfo)this.impl.get((Object)facetName)).fixed;
    }

    public Object getFacet(String facetName) {
        return ((FacetInfo)this.impl.get((Object)facetName)).value;
    }

    public Vector getVector(String facetName) {
        return (Vector)((FacetInfo)this.impl.get((Object)facetName)).value;
    }

    public int getPositiveInteger(String facetName) throws DatatypeException {
        try {
            int value = Integer.parseInt((String)this.getFacet(facetName));
            if (value > 0) {
                return value;
            }
        }
        catch (NumberFormatException e) {
            try {
                if (new BigInteger((String)this.getFacet(facetName)).signum() > 0) {
                    return Integer.MAX_VALUE;
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        throw new DatatypeException(XSDatatypeImpl.localize("BadTypeException.FacetMustBePositiveInteger", facetName));
    }

    public int getNonNegativeInteger(String facetName) throws DatatypeException {
        try {
            int value = Integer.parseInt((String)this.getFacet(facetName));
            if (value >= 0) {
                return value;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        throw new DatatypeException(XSDatatypeImpl.localize("BadTypeException.FacetMustBeNonNegativeInteger", facetName));
    }

    private boolean contains(String facetName) {
        return this.impl.containsKey(facetName);
    }

    public boolean isEmpty() {
        return this.impl.isEmpty();
    }

    public void dump(PrintStream out) {
        for (String facetName : this.impl.keySet()) {
            FacetInfo fi = (FacetInfo)this.impl.get(facetName);
            if (fi.value instanceof Vector) {
                out.println(facetName + " :");
                Vector v = (Vector)fi.value;
                for (int i = 0; i < v.size(); ++i) {
                    out.println("  " + v.elementAt(i));
                }
                continue;
            }
            out.println(facetName + " : " + fi.value);
        }
    }

    public String getFacetNames() {
        String r = "";
        Iterator itr = this.impl.keySet().iterator();
        while (itr.hasNext()) {
            if (r.length() != 0) {
                r = r + ", ";
            }
            r = r + (String)itr.next();
        }
        return r;
    }

    private static class FacetInfo {
        public Object value;
        public boolean fixed;

        public FacetInfo(Object value, boolean fixed) {
            this.value = value;
            this.fixed = fixed;
        }
    }
}

