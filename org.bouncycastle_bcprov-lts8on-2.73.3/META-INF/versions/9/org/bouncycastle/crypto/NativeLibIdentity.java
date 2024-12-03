/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class NativeLibIdentity {
    NativeLibIdentity() {
    }

    static String getLibraryIdent() {
        try {
            return NativeLibIdentity.getLibIdent();
        }
        catch (UnsatisfiedLinkError ule) {
            return "java";
        }
    }

    private static native String getLibIdent();

    static String getNativeBuiltTimeStamp() {
        try {
            return NativeLibIdentity.getBuiltTimeStamp();
        }
        catch (UnsatisfiedLinkError ule) {
            return "None";
        }
    }

    private static native String getBuiltTimeStamp();
}

