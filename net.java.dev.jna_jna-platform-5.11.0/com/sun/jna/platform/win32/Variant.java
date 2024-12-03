/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$ByReference
 *  com.sun.jna.Structure$ByValue
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.Union
 *  com.sun.jna.ptr.ByteByReference
 *  com.sun.jna.ptr.DoubleByReference
 *  com.sun.jna.ptr.FloatByReference
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.ShortByReference
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import java.util.Date;

public interface Variant {
    public static final int VT_EMPTY = 0;
    public static final int VT_NULL = 1;
    public static final int VT_I2 = 2;
    public static final int VT_I4 = 3;
    public static final int VT_R4 = 4;
    public static final int VT_R8 = 5;
    public static final int VT_CY = 6;
    public static final int VT_DATE = 7;
    public static final int VT_BSTR = 8;
    public static final int VT_DISPATCH = 9;
    public static final int VT_ERROR = 10;
    public static final int VT_BOOL = 11;
    public static final int VT_VARIANT = 12;
    public static final int VT_UNKNOWN = 13;
    public static final int VT_DECIMAL = 14;
    public static final int VT_I1 = 16;
    public static final int VT_UI1 = 17;
    public static final int VT_UI2 = 18;
    public static final int VT_UI4 = 19;
    public static final int VT_I8 = 20;
    public static final int VT_UI8 = 21;
    public static final int VT_INT = 22;
    public static final int VT_UINT = 23;
    public static final int VT_VOID = 24;
    public static final int VT_HRESULT = 25;
    public static final int VT_PTR = 26;
    public static final int VT_SAFEARRAY = 27;
    public static final int VT_CARRAY = 28;
    public static final int VT_USERDEFINED = 29;
    public static final int VT_LPSTR = 30;
    public static final int VT_LPWSTR = 31;
    public static final int VT_RECORD = 36;
    public static final int VT_INT_PTR = 37;
    public static final int VT_UINT_PTR = 38;
    public static final int VT_FILETIME = 64;
    public static final int VT_BLOB = 65;
    public static final int VT_STREAM = 66;
    public static final int VT_STORAGE = 67;
    public static final int VT_STREAMED_OBJECT = 68;
    public static final int VT_STORED_OBJECT = 69;
    public static final int VT_BLOB_OBJECT = 70;
    public static final int VT_CF = 71;
    public static final int VT_CLSID = 72;
    public static final int VT_VERSIONED_STREAM = 73;
    public static final int VT_BSTR_BLOB = 4095;
    public static final int VT_VECTOR = 4096;
    public static final int VT_ARRAY = 8192;
    public static final int VT_BYREF = 16384;
    public static final int VT_RESERVED = 32768;
    public static final int VT_ILLEGAL = 65535;
    public static final int VT_ILLEGALMASKED = 4095;
    public static final int VT_TYPEMASK = 4095;
    public static final OaIdl.VARIANT_BOOL VARIANT_TRUE = new OaIdl.VARIANT_BOOL(65535L);
    public static final OaIdl.VARIANT_BOOL VARIANT_FALSE = new OaIdl.VARIANT_BOOL(0L);

    @Structure.FieldOrder(value={"variantArg"})
    public static class VariantArg
    extends Structure {
        public VARIANT[] variantArg;

        public VariantArg() {
            this.variantArg = new VARIANT[1];
        }

        public VariantArg(Pointer pointer) {
            super(pointer);
            this.variantArg = new VARIANT[1];
        }

        public VariantArg(VARIANT[] variantArg) {
            this.variantArg = variantArg;
        }

        public void setArraySize(int size) {
            this.variantArg = new VARIANT[size];
            this.read();
        }

        public static class ByReference
        extends VariantArg
        implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(VARIANT[] variantArg) {
                super(variantArg);
            }
        }
    }

    public static class VARIANT
    extends Union {
        public static final VARIANT VARIANT_MISSING = new VARIANT();
        public _VARIANT _variant;
        public OaIdl.DECIMAL decVal;

        public VARIANT() {
            this.setType("_variant");
            this.read();
        }

        public VARIANT(Pointer pointer) {
            super(pointer);
            this.setType("_variant");
            this.read();
        }

        public VARIANT(WTypes.BSTR value) {
            this();
            this.setValue(8, (Object)value);
        }

        public VARIANT(WTypes.BSTRByReference value) {
            this();
            this.setValue(16392, (Object)value);
        }

        public VARIANT(OaIdl.VARIANT_BOOL value) {
            this();
            this.setValue(11, (Object)value);
        }

        public VARIANT(WinDef.BOOL value) {
            this(value.booleanValue());
        }

        public VARIANT(WinDef.LONG value) {
            this();
            this.setValue(3, (Object)value);
        }

        public VARIANT(WinDef.SHORT value) {
            this();
            this.setValue(2, (Object)value);
        }

        public VARIANT(OaIdl.DATE value) {
            this();
            this.setValue(7, (Object)value);
        }

        public VARIANT(byte value) {
            this(new WinDef.BYTE(value));
        }

        public VARIANT(WinDef.BYTE value) {
            this();
            this.setValue(17, (Object)value);
        }

        public VARIANT(char value) {
            this();
            this.setValue(18, (Object)new WinDef.USHORT(value));
        }

        public VARIANT(WinDef.CHAR value) {
            this();
            this.setValue(16, (Object)value);
        }

        public VARIANT(short value) {
            this();
            this.setValue(2, (Object)new WinDef.SHORT(value));
        }

        public VARIANT(int value) {
            this();
            this.setValue(3, (Object)new WinDef.LONG(value));
        }

        public VARIANT(IntByReference value) {
            this();
            this.setValue(16406, (Object)value);
        }

        public VARIANT(long value) {
            this();
            this.setValue(20, (Object)new WinDef.LONGLONG(value));
        }

        public VARIANT(float value) {
            this();
            this.setValue(4, (Object)Float.valueOf(value));
        }

        public VARIANT(double value) {
            this();
            this.setValue(5, (Object)value);
        }

        public VARIANT(String value) {
            this();
            WTypes.BSTR bstrValue = OleAuto.INSTANCE.SysAllocString(value);
            this.setValue(8, (Object)bstrValue);
        }

        public VARIANT(boolean value) {
            this();
            this.setValue(11, (Object)new OaIdl.VARIANT_BOOL(value));
        }

        @Deprecated
        public VARIANT(IDispatch value) {
            this();
            this.setValue(9, (Object)value);
        }

        public VARIANT(Dispatch value) {
            this();
            this.setValue(9, (Object)value);
        }

        public VARIANT(Date value) {
            this();
            this.setValue(7, (Object)new OaIdl.DATE(value));
        }

        public VARIANT(OaIdl.SAFEARRAY array) {
            this();
            this.setValue(array);
        }

        public VARIANT(OaIdl.SAFEARRAYByReference array) {
            this();
            this.setValue(array);
        }

        public WTypes.VARTYPE getVarType() {
            this.read();
            return this._variant.vt;
        }

        public void setVarType(short vt) {
            this._variant.vt = new WTypes.VARTYPE(vt);
        }

        public void setValue(int vt, Object value) {
            this.setValue(new WTypes.VARTYPE(vt), value);
        }

        public void setValue(OaIdl.SAFEARRAY array) {
            this.setValue(array.getVarType().intValue() | 0x2000, (Object)array);
        }

        public void setValue(OaIdl.SAFEARRAYByReference array) {
            this.setValue(array.pSAFEARRAY.getVarType().intValue() | 0x2000 | 0x4000, (Object)array);
        }

        public void setValue(WTypes.VARTYPE vt, Object value) {
            int varType = vt.intValue();
            switch (varType) {
                case 17: {
                    this._variant.__variant.writeField("bVal", value);
                    break;
                }
                case 2: {
                    this._variant.__variant.writeField("iVal", value);
                    break;
                }
                case 3: {
                    this._variant.__variant.writeField("lVal", value);
                    break;
                }
                case 20: {
                    this._variant.__variant.writeField("llVal", value);
                    break;
                }
                case 4: {
                    this._variant.__variant.writeField("fltVal", value);
                    break;
                }
                case 5: {
                    this._variant.__variant.writeField("dblVal", value);
                    break;
                }
                case 11: {
                    this._variant.__variant.writeField("boolVal", value);
                    break;
                }
                case 10: {
                    this._variant.__variant.writeField("scode", value);
                    break;
                }
                case 6: {
                    this._variant.__variant.writeField("cyVal", value);
                    break;
                }
                case 7: {
                    this._variant.__variant.writeField("date", value);
                    break;
                }
                case 8: {
                    this._variant.__variant.writeField("bstrVal", value);
                    break;
                }
                case 13: {
                    this._variant.__variant.writeField("punkVal", value);
                    break;
                }
                case 9: {
                    this._variant.__variant.writeField("pdispVal", value);
                    break;
                }
                case 16401: {
                    this._variant.__variant.writeField("pbVal", value);
                    break;
                }
                case 16386: {
                    this._variant.__variant.writeField("piVal", value);
                    break;
                }
                case 16387: {
                    this._variant.__variant.writeField("plVal", value);
                    break;
                }
                case 16404: {
                    this._variant.__variant.writeField("pllVal", value);
                    break;
                }
                case 16388: {
                    this._variant.__variant.writeField("pfltVal", value);
                    break;
                }
                case 16389: {
                    this._variant.__variant.writeField("pdblVal", value);
                    break;
                }
                case 16395: {
                    this._variant.__variant.writeField("pboolVal", value);
                    break;
                }
                case 16394: {
                    this._variant.__variant.writeField("pscode", value);
                    break;
                }
                case 16390: {
                    this._variant.__variant.writeField("pcyVal", value);
                    break;
                }
                case 16391: {
                    this._variant.__variant.writeField("pdate", value);
                    break;
                }
                case 16392: {
                    this._variant.__variant.writeField("pbstrVal", value);
                    break;
                }
                case 16397: {
                    this._variant.__variant.writeField("ppunkVal", value);
                    break;
                }
                case 16393: {
                    this._variant.__variant.writeField("ppdispVal", value);
                    break;
                }
                case 16396: {
                    this._variant.__variant.writeField("pvarVal", value);
                    break;
                }
                case 16384: {
                    this._variant.__variant.writeField("byref", value);
                    break;
                }
                case 16: {
                    this._variant.__variant.writeField("cVal", value);
                    break;
                }
                case 18: {
                    this._variant.__variant.writeField("uiVal", value);
                    break;
                }
                case 19: {
                    this._variant.__variant.writeField("ulVal", value);
                    break;
                }
                case 21: {
                    this._variant.__variant.writeField("ullVal", value);
                    break;
                }
                case 22: {
                    this._variant.__variant.writeField("intVal", value);
                    break;
                }
                case 23: {
                    this._variant.__variant.writeField("uintVal", value);
                    break;
                }
                case 16398: {
                    this._variant.__variant.writeField("pdecVal", value);
                    break;
                }
                case 16400: {
                    this._variant.__variant.writeField("pcVal", value);
                    break;
                }
                case 16402: {
                    this._variant.__variant.writeField("puiVal", value);
                    break;
                }
                case 16403: {
                    this._variant.__variant.writeField("pulVal", value);
                    break;
                }
                case 16405: {
                    this._variant.__variant.writeField("pullVal", value);
                    break;
                }
                case 16406: {
                    this._variant.__variant.writeField("pintVal", value);
                    break;
                }
                case 16407: {
                    this._variant.__variant.writeField("puintVal", value);
                    break;
                }
                case 36: {
                    this._variant.__variant.writeField("pvRecord", value);
                    break;
                }
                default: {
                    if ((varType & 0x2000) <= 0) break;
                    if ((varType & 0x4000) > 0) {
                        this._variant.__variant.writeField("pparray", value);
                        break;
                    }
                    this._variant.__variant.writeField("parray", value);
                }
            }
            this._variant.writeField("vt", vt);
            this.write();
        }

        public Object getValue() {
            int varType = this.getVarType().intValue();
            switch (varType) {
                case 17: {
                    return this._variant.__variant.readField("bVal");
                }
                case 2: {
                    return this._variant.__variant.readField("iVal");
                }
                case 3: {
                    return this._variant.__variant.readField("lVal");
                }
                case 20: {
                    return this._variant.__variant.readField("llVal");
                }
                case 4: {
                    return this._variant.__variant.readField("fltVal");
                }
                case 5: {
                    return this._variant.__variant.readField("dblVal");
                }
                case 11: {
                    return this._variant.__variant.readField("boolVal");
                }
                case 10: {
                    return this._variant.__variant.readField("scode");
                }
                case 6: {
                    return this._variant.__variant.readField("cyVal");
                }
                case 7: {
                    return this._variant.__variant.readField("date");
                }
                case 8: {
                    return this._variant.__variant.readField("bstrVal");
                }
                case 13: {
                    return this._variant.__variant.readField("punkVal");
                }
                case 9: {
                    return this._variant.__variant.readField("pdispVal");
                }
                case 16401: {
                    return this._variant.__variant.readField("pbVal");
                }
                case 16386: {
                    return this._variant.__variant.readField("piVal");
                }
                case 16387: {
                    return this._variant.__variant.readField("plVal");
                }
                case 16404: {
                    return this._variant.__variant.readField("pllVal");
                }
                case 16388: {
                    return this._variant.__variant.readField("pfltVal");
                }
                case 16389: {
                    return this._variant.__variant.readField("pdblVal");
                }
                case 16395: {
                    return this._variant.__variant.readField("pboolVal");
                }
                case 16394: {
                    return this._variant.__variant.readField("pscode");
                }
                case 16390: {
                    return this._variant.__variant.readField("pcyVal");
                }
                case 16391: {
                    return this._variant.__variant.readField("pdate");
                }
                case 16392: {
                    return this._variant.__variant.readField("pbstrVal");
                }
                case 16397: {
                    return this._variant.__variant.readField("ppunkVal");
                }
                case 16393: {
                    return this._variant.__variant.readField("ppdispVal");
                }
                case 16396: {
                    return this._variant.__variant.readField("pvarVal");
                }
                case 16384: {
                    return this._variant.__variant.readField("byref");
                }
                case 16: {
                    return this._variant.__variant.readField("cVal");
                }
                case 18: {
                    return this._variant.__variant.readField("uiVal");
                }
                case 19: {
                    return this._variant.__variant.readField("ulVal");
                }
                case 21: {
                    return this._variant.__variant.readField("ullVal");
                }
                case 22: {
                    return this._variant.__variant.readField("intVal");
                }
                case 23: {
                    return this._variant.__variant.readField("uintVal");
                }
                case 16398: {
                    return this._variant.__variant.readField("pdecVal");
                }
                case 16400: {
                    return this._variant.__variant.readField("pcVal");
                }
                case 16402: {
                    return this._variant.__variant.readField("puiVal");
                }
                case 16403: {
                    return this._variant.__variant.readField("pulVal");
                }
                case 16405: {
                    return this._variant.__variant.readField("pullVal");
                }
                case 16406: {
                    return this._variant.__variant.readField("pintVal");
                }
                case 16407: {
                    return this._variant.__variant.readField("puintVal");
                }
                case 36: {
                    return this._variant.__variant.readField("pvRecord");
                }
            }
            if ((varType & 0x2000) > 0) {
                if ((varType & 0x4000) > 0) {
                    return this._variant.__variant.readField("pparray");
                }
                return this._variant.__variant.readField("parray");
            }
            return null;
        }

        public byte byteValue() {
            return ((Number)this.getValue()).byteValue();
        }

        public short shortValue() {
            return ((Number)this.getValue()).shortValue();
        }

        public int intValue() {
            return ((Number)this.getValue()).intValue();
        }

        public long longValue() {
            return ((Number)this.getValue()).longValue();
        }

        public float floatValue() {
            return ((Number)this.getValue()).floatValue();
        }

        public double doubleValue() {
            return ((Number)this.getValue()).doubleValue();
        }

        public String stringValue() {
            WTypes.BSTR bstr = (WTypes.BSTR)((Object)this.getValue());
            if (bstr == null) {
                return null;
            }
            return bstr.getValue();
        }

        public boolean booleanValue() {
            return ((OaIdl.VARIANT_BOOL)((Object)this.getValue())).booleanValue();
        }

        public Date dateValue() {
            OaIdl.DATE varDate = (OaIdl.DATE)((Object)this.getValue());
            if (varDate == null) {
                return null;
            }
            return varDate.getAsJavaDate();
        }

        static {
            VARIANT_MISSING.setValue(10, (Object)new WinDef.SCODE(-2147352572L));
        }

        @Structure.FieldOrder(value={"vt", "wReserved1", "wReserved2", "wReserved3", "__variant"})
        public static class _VARIANT
        extends Structure {
            public WTypes.VARTYPE vt;
            public short wReserved1;
            public short wReserved2;
            public short wReserved3;
            public __VARIANT __variant;

            public _VARIANT() {
            }

            public _VARIANT(Pointer pointer) {
                super(pointer);
                this.read();
            }

            public static class __VARIANT
            extends Union {
                public WinDef.LONGLONG llVal;
                public WinDef.LONG lVal;
                public WinDef.BYTE bVal;
                public WinDef.SHORT iVal;
                public Float fltVal;
                public Double dblVal;
                public OaIdl.VARIANT_BOOL boolVal;
                public WinDef.SCODE scode;
                public OaIdl.CURRENCY cyVal;
                public OaIdl.DATE date;
                public WTypes.BSTR bstrVal;
                public Unknown punkVal;
                public Dispatch pdispVal;
                public OaIdl.SAFEARRAY.ByReference parray;
                public ByteByReference pbVal;
                public ShortByReference piVal;
                public WinDef.LONGByReference plVal;
                public WinDef.LONGLONGByReference pllVal;
                public FloatByReference pfltVal;
                public DoubleByReference pdblVal;
                public OaIdl.VARIANT_BOOLByReference pboolVal;
                public OaIdl._VARIANT_BOOLByReference pbool;
                public WinDef.SCODEByReference pscode;
                public OaIdl.CURRENCY.ByReference pcyVal;
                public OaIdl.DATE.ByReference pdate;
                public WTypes.BSTRByReference pbstrVal;
                public Unknown.ByReference ppunkVal;
                public Dispatch.ByReference ppdispVal;
                public OaIdl.SAFEARRAYByReference pparray;
                public ByReference pvarVal;
                public WinDef.PVOID byref;
                public WinDef.CHAR cVal;
                public WinDef.USHORT uiVal;
                public WinDef.ULONG ulVal;
                public WinDef.ULONGLONG ullVal;
                public Integer intVal;
                public WinDef.UINT uintVal;
                public OaIdl.DECIMAL.ByReference pdecVal;
                public WinDef.CHARByReference pcVal;
                public WinDef.USHORTByReference puiVal;
                public WinDef.ULONGByReference pulVal;
                public WinDef.ULONGLONGByReference pullVal;
                public IntByReference pintVal;
                public WinDef.UINTByReference puintVal;
                public BRECORD pvRecord;

                public __VARIANT() {
                    this.read();
                }

                public __VARIANT(Pointer pointer) {
                    super(pointer);
                    this.read();
                }

                @Structure.FieldOrder(value={"pvRecord", "pRecInfo"})
                public static class BRECORD
                extends Structure {
                    public WinDef.PVOID pvRecord;
                    public Pointer pRecInfo;

                    public BRECORD() {
                    }

                    public BRECORD(Pointer pointer) {
                        super(pointer);
                    }

                    public static class ByReference
                    extends BRECORD
                    implements Structure.ByReference {
                    }
                }
            }
        }

        public static class ByValue
        extends VARIANT
        implements Structure.ByValue {
            public ByValue(VARIANT variant) {
                this.setValue(variant.getVarType(), variant.getValue());
            }

            public ByValue(Pointer variant) {
                super(variant);
            }

            public ByValue() {
            }
        }

        public static class ByReference
        extends VARIANT
        implements Structure.ByReference {
            public ByReference(VARIANT variant) {
                this.setValue(variant.getVarType(), variant.getValue());
            }

            public ByReference(Pointer variant) {
                super(variant);
            }

            public ByReference() {
            }
        }
    }
}

