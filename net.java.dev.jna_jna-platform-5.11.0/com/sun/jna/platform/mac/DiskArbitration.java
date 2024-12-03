/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 */
package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;

public interface DiskArbitration
extends Library {
    public static final DiskArbitration INSTANCE = (DiskArbitration)Native.load((String)"DiskArbitration", DiskArbitration.class);

    public DASessionRef DASessionCreate(CoreFoundation.CFAllocatorRef var1);

    public DADiskRef DADiskCreateFromBSDName(CoreFoundation.CFAllocatorRef var1, DASessionRef var2, String var3);

    public DADiskRef DADiskCreateFromIOMedia(CoreFoundation.CFAllocatorRef var1, DASessionRef var2, IOKit.IOObject var3);

    public CoreFoundation.CFDictionaryRef DADiskCopyDescription(DADiskRef var1);

    public String DADiskGetBSDName(DADiskRef var1);

    public static class DADiskRef
    extends CoreFoundation.CFTypeRef {
    }

    public static class DASessionRef
    extends CoreFoundation.CFTypeRef {
    }
}

