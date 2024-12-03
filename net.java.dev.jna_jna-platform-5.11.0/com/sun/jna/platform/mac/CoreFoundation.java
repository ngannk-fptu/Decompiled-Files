/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.NativeLong
 *  com.sun.jna.Pointer
 *  com.sun.jna.PointerType
 *  com.sun.jna.ptr.ByReference
 *  com.sun.jna.ptr.ByteByReference
 *  com.sun.jna.ptr.DoubleByReference
 *  com.sun.jna.ptr.FloatByReference
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.LongByReference
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.ptr.ShortByReference
 */
package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public interface CoreFoundation
extends Library {
    public static final CoreFoundation INSTANCE = (CoreFoundation)Native.load((String)"CoreFoundation", CoreFoundation.class);
    public static final int kCFNotFound = -1;
    public static final int kCFStringEncodingASCII = 1536;
    public static final int kCFStringEncodingUTF8 = 0x8000100;
    public static final CFTypeID ARRAY_TYPE_ID = INSTANCE.CFArrayGetTypeID();
    public static final CFTypeID BOOLEAN_TYPE_ID = INSTANCE.CFBooleanGetTypeID();
    public static final CFTypeID DATA_TYPE_ID = INSTANCE.CFDataGetTypeID();
    public static final CFTypeID DATE_TYPE_ID = INSTANCE.CFDateGetTypeID();
    public static final CFTypeID DICTIONARY_TYPE_ID = INSTANCE.CFDictionaryGetTypeID();
    public static final CFTypeID NUMBER_TYPE_ID = INSTANCE.CFNumberGetTypeID();
    public static final CFTypeID STRING_TYPE_ID = INSTANCE.CFStringGetTypeID();

    public CFStringRef CFStringCreateWithCharacters(CFAllocatorRef var1, char[] var2, CFIndex var3);

    public CFNumberRef CFNumberCreate(CFAllocatorRef var1, CFIndex var2, ByReference var3);

    public CFArrayRef CFArrayCreate(CFAllocatorRef var1, Pointer var2, CFIndex var3, Pointer var4);

    public CFDataRef CFDataCreate(CFAllocatorRef var1, Pointer var2, CFIndex var3);

    public CFMutableDictionaryRef CFDictionaryCreateMutable(CFAllocatorRef var1, CFIndex var2, Pointer var3, Pointer var4);

    public CFStringRef CFCopyDescription(CFTypeRef var1);

    public void CFRelease(CFTypeRef var1);

    public CFTypeRef CFRetain(CFTypeRef var1);

    public CFIndex CFGetRetainCount(CFTypeRef var1);

    public CFIndex CFDictionaryGetCount(CFDictionaryRef var1);

    public Pointer CFDictionaryGetValue(CFDictionaryRef var1, PointerType var2);

    public byte CFDictionaryGetValueIfPresent(CFDictionaryRef var1, PointerType var2, PointerByReference var3);

    public void CFDictionarySetValue(CFMutableDictionaryRef var1, PointerType var2, PointerType var3);

    public byte CFStringGetCString(CFStringRef var1, Pointer var2, CFIndex var3, int var4);

    public byte CFBooleanGetValue(CFBooleanRef var1);

    public CFIndex CFArrayGetCount(CFArrayRef var1);

    public Pointer CFArrayGetValueAtIndex(CFArrayRef var1, CFIndex var2);

    public CFIndex CFNumberGetType(CFNumberRef var1);

    public byte CFNumberGetValue(CFNumberRef var1, CFIndex var2, ByReference var3);

    public CFIndex CFStringGetLength(CFStringRef var1);

    public CFIndex CFStringGetMaximumSizeForEncoding(CFIndex var1, int var2);

    public CFAllocatorRef CFAllocatorGetDefault();

    public CFIndex CFDataGetLength(CFDataRef var1);

    public Pointer CFDataGetBytePtr(CFDataRef var1);

    public CFTypeID CFGetTypeID(CFTypeRef var1);

    public CFTypeID CFArrayGetTypeID();

    public CFTypeID CFBooleanGetTypeID();

    public CFTypeID CFDateGetTypeID();

    public CFTypeID CFDataGetTypeID();

    public CFTypeID CFDictionaryGetTypeID();

    public CFTypeID CFNumberGetTypeID();

    public CFTypeID CFStringGetTypeID();

    public static class CFTypeID
    extends NativeLong {
        private static final long serialVersionUID = 1L;

        public CFTypeID() {
        }

        public CFTypeID(long value) {
            super(value);
        }

        public String toString() {
            if (this.equals((Object)ARRAY_TYPE_ID)) {
                return "CFArray";
            }
            if (this.equals((Object)BOOLEAN_TYPE_ID)) {
                return "CFBoolean";
            }
            if (this.equals((Object)DATA_TYPE_ID)) {
                return "CFData";
            }
            if (this.equals((Object)DATE_TYPE_ID)) {
                return "CFDate";
            }
            if (this.equals((Object)DICTIONARY_TYPE_ID)) {
                return "CFDictionary";
            }
            if (this.equals((Object)NUMBER_TYPE_ID)) {
                return "CFNumber";
            }
            if (this.equals((Object)STRING_TYPE_ID)) {
                return "CFString";
            }
            return super.toString();
        }
    }

    public static class CFIndex
    extends NativeLong {
        private static final long serialVersionUID = 1L;

        public CFIndex() {
        }

        public CFIndex(long value) {
            super(value);
        }
    }

    public static class CFStringRef
    extends CFTypeRef {
        public CFStringRef() {
        }

        public CFStringRef(Pointer p) {
            super(p);
            if (!this.isTypeID(STRING_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFString. Type ID: " + (Object)((Object)this.getTypeID()));
            }
        }

        public static CFStringRef createCFString(String s) {
            char[] chars = s.toCharArray();
            return INSTANCE.CFStringCreateWithCharacters(null, chars, new CFIndex(chars.length));
        }

        public String stringValue() {
            CFIndex length = INSTANCE.CFStringGetLength(this);
            if (length.longValue() == 0L) {
                return "";
            }
            CFIndex maxSize = INSTANCE.CFStringGetMaximumSizeForEncoding(length, 0x8000100);
            if (maxSize.intValue() == -1) {
                throw new StringIndexOutOfBoundsException("CFString maximum number of bytes exceeds LONG_MAX.");
            }
            maxSize.setValue(maxSize.longValue() + 1L);
            Memory buf = new Memory(maxSize.longValue());
            if (0 != INSTANCE.CFStringGetCString(this, (Pointer)buf, maxSize, 0x8000100)) {
                return buf.getString(0L, "UTF8");
            }
            throw new IllegalArgumentException("CFString conversion fails or the provided buffer is too small.");
        }
    }

    public static class CFMutableDictionaryRef
    extends CFDictionaryRef {
        public CFMutableDictionaryRef() {
        }

        public CFMutableDictionaryRef(Pointer p) {
            super(p);
        }

        public void setValue(PointerType key, PointerType value) {
            INSTANCE.CFDictionarySetValue(this, key, value);
        }
    }

    public static class CFDictionaryRef
    extends CFTypeRef {
        public CFDictionaryRef() {
        }

        public CFDictionaryRef(Pointer p) {
            super(p);
            if (!this.isTypeID(DICTIONARY_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFDictionary. Type ID: " + (Object)((Object)this.getTypeID()));
            }
        }

        public Pointer getValue(PointerType key) {
            return INSTANCE.CFDictionaryGetValue(this, key);
        }

        public long getCount() {
            return INSTANCE.CFDictionaryGetCount(this).longValue();
        }

        public boolean getValueIfPresent(PointerType key, PointerByReference value) {
            return INSTANCE.CFDictionaryGetValueIfPresent(this, key, value) > 0;
        }
    }

    public static class CFDataRef
    extends CFTypeRef {
        public CFDataRef() {
        }

        public CFDataRef(Pointer p) {
            super(p);
            if (!this.isTypeID(DATA_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFData. Type ID: " + (Object)((Object)this.getTypeID()));
            }
        }

        public int getLength() {
            return INSTANCE.CFDataGetLength(this).intValue();
        }

        public Pointer getBytePtr() {
            return INSTANCE.CFDataGetBytePtr(this);
        }
    }

    public static class CFArrayRef
    extends CFTypeRef {
        public CFArrayRef() {
        }

        public CFArrayRef(Pointer p) {
            super(p);
            if (!this.isTypeID(ARRAY_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFArray. Type ID: " + (Object)((Object)this.getTypeID()));
            }
        }

        public int getCount() {
            return INSTANCE.CFArrayGetCount(this).intValue();
        }

        public Pointer getValueAtIndex(int idx) {
            return INSTANCE.CFArrayGetValueAtIndex(this, new CFIndex(idx));
        }
    }

    public static class CFBooleanRef
    extends CFTypeRef {
        public CFBooleanRef() {
        }

        public CFBooleanRef(Pointer p) {
            super(p);
            if (!this.isTypeID(BOOLEAN_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFBoolean. Type ID: " + (Object)((Object)this.getTypeID()));
            }
        }

        public boolean booleanValue() {
            return 0 != INSTANCE.CFBooleanGetValue(this);
        }
    }

    public static enum CFNumberType {
        unusedZero,
        kCFNumberSInt8Type,
        kCFNumberSInt16Type,
        kCFNumberSInt32Type,
        kCFNumberSInt64Type,
        kCFNumberFloat32Type,
        kCFNumberFloat64Type,
        kCFNumberCharType,
        kCFNumberShortType,
        kCFNumberIntType,
        kCFNumberLongType,
        kCFNumberLongLongType,
        kCFNumberFloatType,
        kCFNumberDoubleType,
        kCFNumberCFIndexType,
        kCFNumberNSIntegerType,
        kCFNumberCGFloatType,
        kCFNumberMaxType;


        public CFIndex typeIndex() {
            return new CFIndex(this.ordinal());
        }
    }

    public static class CFNumberRef
    extends CFTypeRef {
        public CFNumberRef() {
        }

        public CFNumberRef(Pointer p) {
            super(p);
            if (!this.isTypeID(NUMBER_TYPE_ID)) {
                throw new ClassCastException("Unable to cast to CFNumber. Type ID: " + (Object)((Object)this.getTypeID()));
            }
        }

        public long longValue() {
            LongByReference lbr = new LongByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberLongLongType.typeIndex(), (ByReference)lbr);
            return lbr.getValue();
        }

        public int intValue() {
            IntByReference ibr = new IntByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberIntType.typeIndex(), (ByReference)ibr);
            return ibr.getValue();
        }

        public short shortValue() {
            ShortByReference sbr = new ShortByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberShortType.typeIndex(), (ByReference)sbr);
            return sbr.getValue();
        }

        public byte byteValue() {
            ByteByReference bbr = new ByteByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberCharType.typeIndex(), (ByReference)bbr);
            return bbr.getValue();
        }

        public double doubleValue() {
            DoubleByReference dbr = new DoubleByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberDoubleType.typeIndex(), (ByReference)dbr);
            return dbr.getValue();
        }

        public float floatValue() {
            FloatByReference fbr = new FloatByReference();
            INSTANCE.CFNumberGetValue(this, CFNumberType.kCFNumberFloatType.typeIndex(), (ByReference)fbr);
            return fbr.getValue();
        }
    }

    public static class CFAllocatorRef
    extends CFTypeRef {
    }

    public static class CFTypeRef
    extends PointerType {
        public CFTypeRef() {
        }

        public CFTypeRef(Pointer p) {
            super(p);
        }

        public CFTypeID getTypeID() {
            if (this.getPointer() == null) {
                return new CFTypeID(0L);
            }
            return INSTANCE.CFGetTypeID(this);
        }

        public boolean isTypeID(CFTypeID typeID) {
            return this.getTypeID().equals((Object)typeID);
        }

        public void retain() {
            INSTANCE.CFRetain(this);
        }

        public void release() {
            INSTANCE.CFRelease(this);
        }
    }
}

