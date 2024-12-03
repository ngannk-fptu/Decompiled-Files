/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.PointerType
 *  com.sun.jna.platform.mac.CoreFoundation$CFArrayRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFBooleanRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFDictionaryRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFNumberRef
 *  com.sun.jna.platform.mac.CoreFoundation$CFStringRef
 */
package oshi.driver.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.CoreGraphics;
import oshi.software.os.OSDesktopWindow;
import oshi.util.FormatUtil;
import oshi.util.platform.mac.CFUtil;

@ThreadSafe
public final class WindowInfo {
    private WindowInfo() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<OSDesktopWindow> queryDesktopWindows(boolean visibleOnly) {
        CoreFoundation.CFArrayRef windowInfo = CoreGraphics.INSTANCE.CGWindowListCopyWindowInfo(visibleOnly ? 17 : 0, 0);
        int numWindows = windowInfo.getCount();
        ArrayList<OSDesktopWindow> windowList = new ArrayList<OSDesktopWindow>();
        CoreFoundation.CFStringRef kCGWindowIsOnscreen = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowIsOnscreen");
        CoreFoundation.CFStringRef kCGWindowNumber = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowNumber");
        CoreFoundation.CFStringRef kCGWindowOwnerPID = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowOwnerPID");
        CoreFoundation.CFStringRef kCGWindowLayer = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowLayer");
        CoreFoundation.CFStringRef kCGWindowBounds = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowBounds");
        CoreFoundation.CFStringRef kCGWindowName = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowName");
        CoreFoundation.CFStringRef kCGWindowOwnerName = CoreFoundation.CFStringRef.createCFString((String)"kCGWindowOwnerName");
        try {
            for (int i = 0; i < numWindows; ++i) {
                boolean visible;
                Pointer result = windowInfo.getValueAtIndex(i);
                CoreFoundation.CFDictionaryRef windowRef = new CoreFoundation.CFDictionaryRef(result);
                boolean bl = visible = (result = windowRef.getValue((PointerType)kCGWindowIsOnscreen)) == null || new CoreFoundation.CFBooleanRef(result).booleanValue();
                if (visibleOnly && !visible) continue;
                result = windowRef.getValue((PointerType)kCGWindowNumber);
                long windowNumber = new CoreFoundation.CFNumberRef(result).longValue();
                result = windowRef.getValue((PointerType)kCGWindowOwnerPID);
                long windowOwnerPID = new CoreFoundation.CFNumberRef(result).longValue();
                result = windowRef.getValue((PointerType)kCGWindowLayer);
                int windowLayer = new CoreFoundation.CFNumberRef(result).intValue();
                result = windowRef.getValue((PointerType)kCGWindowBounds);
                CoreGraphics.CGRect rect = new CoreGraphics.CGRect();
                CoreGraphics.INSTANCE.CGRectMakeWithDictionaryRepresentation(new CoreFoundation.CFDictionaryRef(result), rect);
                Rectangle windowBounds = new Rectangle(FormatUtil.roundToInt(rect.origin.x), FormatUtil.roundToInt(rect.origin.y), FormatUtil.roundToInt(rect.size.width), FormatUtil.roundToInt(rect.size.height));
                result = windowRef.getValue((PointerType)kCGWindowName);
                String windowName = CFUtil.cfPointerToString(result, false);
                result = windowRef.getValue((PointerType)kCGWindowOwnerName);
                String windowOwnerName = CFUtil.cfPointerToString(result, false);
                windowName = windowName.isEmpty() ? windowOwnerName : windowName + "(" + windowOwnerName + ")";
                windowList.add(new OSDesktopWindow(windowNumber, windowName, windowOwnerName, windowBounds, windowOwnerPID, windowLayer, visible));
            }
        }
        finally {
            kCGWindowIsOnscreen.release();
            kCGWindowNumber.release();
            kCGWindowOwnerPID.release();
            kCGWindowLayer.release();
            kCGWindowBounds.release();
            kCGWindowName.release();
            kCGWindowOwnerName.release();
            windowInfo.release();
        }
        return windowList;
    }
}

