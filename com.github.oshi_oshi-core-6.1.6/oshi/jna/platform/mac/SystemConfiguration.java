/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.mac.CoreFoundation$CFArrayRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFStringRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFTypeRef
 */
package oshi.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;

public interface SystemConfiguration
extends Library {
    public static final SystemConfiguration INSTANCE = (SystemConfiguration)Native.load((String)"SystemConfiguration", SystemConfiguration.class);

    public CoreFoundation.CFArrayRef SCNetworkInterfaceCopyAll();

    public CoreFoundation.CFStringRef SCNetworkInterfaceGetBSDName(SCNetworkInterfaceRef var1);

    public CoreFoundation.CFStringRef SCNetworkInterfaceGetLocalizedDisplayName(SCNetworkInterfaceRef var1);

    public static class SCNetworkInterfaceRef
    extends CoreFoundation.CFTypeRef {
        public SCNetworkInterfaceRef() {
        }

        public SCNetworkInterfaceRef(Pointer p) {
            super(p);
        }
    }
}

