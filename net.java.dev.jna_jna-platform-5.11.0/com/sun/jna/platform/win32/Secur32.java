/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Sspi;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Secur32
extends StdCallLibrary {
    public static final Secur32 INSTANCE = (Secur32)Native.load((String)"Secur32", Secur32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

    public boolean GetUserNameEx(int var1, char[] var2, IntByReference var3);

    public int AcquireCredentialsHandle(String var1, String var2, int var3, WinNT.LUID var4, Pointer var5, Pointer var6, Pointer var7, Sspi.CredHandle var8, Sspi.TimeStamp var9);

    public int InitializeSecurityContext(Sspi.CredHandle var1, Sspi.CtxtHandle var2, String var3, int var4, int var5, int var6, Sspi.SecBufferDesc var7, int var8, Sspi.CtxtHandle var9, Sspi.SecBufferDesc var10, IntByReference var11, Sspi.TimeStamp var12);

    public int DeleteSecurityContext(Sspi.CtxtHandle var1);

    public int FreeCredentialsHandle(Sspi.CredHandle var1);

    public int AcceptSecurityContext(Sspi.CredHandle var1, Sspi.CtxtHandle var2, Sspi.SecBufferDesc var3, int var4, int var5, Sspi.CtxtHandle var6, Sspi.SecBufferDesc var7, IntByReference var8, Sspi.TimeStamp var9);

    public int CompleteAuthToken(Sspi.CtxtHandle var1, Sspi.SecBufferDesc var2);

    public int EnumerateSecurityPackages(IntByReference var1, Sspi.PSecPkgInfo var2);

    public int FreeContextBuffer(Pointer var1);

    public int QuerySecurityContextToken(Sspi.CtxtHandle var1, WinNT.HANDLEByReference var2);

    public int ImpersonateSecurityContext(Sspi.CtxtHandle var1);

    public int RevertSecurityContext(Sspi.CtxtHandle var1);

    public int QueryContextAttributes(Sspi.CtxtHandle var1, int var2, Structure var3);

    public int QueryCredentialsAttributes(Sspi.CredHandle var1, int var2, Structure var3);

    public int QuerySecurityPackageInfo(String var1, Sspi.PSecPkgInfo var2);

    public int EncryptMessage(Sspi.CtxtHandle var1, int var2, Sspi.SecBufferDesc var3, int var4);

    public int VerifySignature(Sspi.CtxtHandle var1, Sspi.SecBufferDesc var2, int var3, IntByReference var4);

    public int MakeSignature(Sspi.CtxtHandle var1, int var2, Sspi.SecBufferDesc var3, int var4);

    public int DecryptMessage(Sspi.CtxtHandle var1, Sspi.SecBufferDesc var2, int var3, IntByReference var4);

    public static abstract class EXTENDED_NAME_FORMAT {
        public static final int NameUnknown = 0;
        public static final int NameFullyQualifiedDN = 1;
        public static final int NameSamCompatible = 2;
        public static final int NameDisplay = 3;
        public static final int NameUniqueId = 6;
        public static final int NameCanonical = 7;
        public static final int NameUserPrincipal = 8;
        public static final int NameCanonicalEx = 9;
        public static final int NameServicePrincipal = 10;
        public static final int NameDnsDomain = 12;
    }
}

