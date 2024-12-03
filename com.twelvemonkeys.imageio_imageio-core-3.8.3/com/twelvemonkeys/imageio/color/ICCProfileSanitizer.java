/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.SystemUtil
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.color.ColorSpaces;
import com.twelvemonkeys.imageio.color.KCMSSanitizerStrategy;
import com.twelvemonkeys.imageio.color.LCMSSanitizerStrategy;
import com.twelvemonkeys.lang.SystemUtil;
import java.awt.color.ICC_Profile;

interface ICCProfileSanitizer {
    public void fixProfile(ICC_Profile var1);

    public boolean validationAltersProfileHeader();

    public static class Factory {
        static ICCProfileSanitizer get() {
            String string = System.getProperty("sun.java2d.cmm");
            ICCProfileSanitizer iCCProfileSanitizer = "sun.java2d.cmm.kcms.KcmsServiceProvider".equals(string) && SystemUtil.isClassAvailable((String)"sun.java2d.cmm.kcms.CMM") ? new KCMSSanitizerStrategy() : ("sun.java2d.cmm.lcms.LcmsServiceProvider".equals(string) && SystemUtil.isClassAvailable((String)"sun.java2d.cmm.lcms.LCMS") ? new LCMSSanitizerStrategy() : (SystemUtil.isClassAvailable((String)"java.util.stream.Stream") || SystemUtil.isClassAvailable((String)"java.lang.invoke.CallSite") && !SystemUtil.isClassAvailable((String)"sun.java2d.cmm.kcms.CMM") ? new LCMSSanitizerStrategy() : new KCMSSanitizerStrategy()));
            if (ColorSpaces.DEBUG) {
                System.out.println("ICC ProfileCleaner instance: " + iCCProfileSanitizer.getClass().getName());
            }
            return iCCProfileSanitizer;
        }
    }
}

