/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.mac.CoreFoundation$CFDataRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFStringRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFTypeRef
 *  com.sun.jna.platform.mac.IOKit$IOIterator
 *  com.sun.jna.platform.mac.IOKit$IORegistryEntry
 *  com.sun.jna.platform.mac.IOKitUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
final class MacDisplay
extends AbstractDisplay {
    private static final Logger LOG = LoggerFactory.getLogger(MacDisplay.class);

    MacDisplay(byte[] edid) {
        super(edid);
        LOG.debug("Initialized MacDisplay");
    }

    public static List<Display> getDisplays() {
        ArrayList<Display> displays = new ArrayList<Display>();
        IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices((String)"IODisplayConnect");
        if (serviceIterator != null) {
            CoreFoundation.CFStringRef cfEdid = CoreFoundation.CFStringRef.createCFString((String)"IODisplayEDID");
            IOKit.IORegistryEntry sdService = serviceIterator.next();
            while (sdService != null) {
                IOKit.IORegistryEntry properties = sdService.getChildEntry("IOService");
                if (properties != null) {
                    CoreFoundation.CFTypeRef edidRaw = properties.createCFProperty(cfEdid);
                    if (edidRaw != null) {
                        CoreFoundation.CFDataRef edid = new CoreFoundation.CFDataRef(edidRaw.getPointer());
                        int length = edid.getLength();
                        Pointer p = edid.getBytePtr();
                        displays.add(new MacDisplay(p.getByteArray(0L, length)));
                        edid.release();
                    }
                    properties.release();
                }
                sdService.release();
                sdService = serviceIterator.next();
            }
            serviceIterator.release();
            cfEdid.release();
        }
        return displays;
    }
}

