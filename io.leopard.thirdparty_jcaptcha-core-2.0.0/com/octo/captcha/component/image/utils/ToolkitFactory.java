/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.utils;

import com.octo.captcha.CaptchaException;
import java.awt.Toolkit;

public class ToolkitFactory {
    public static String TOOLKIT_IMPL = "toolkit.implementation";

    public static Toolkit getToolkit() {
        Toolkit defaultToolkit = null;
        try {
            String tempToolkitClass = System.getProperty(TOOLKIT_IMPL);
            defaultToolkit = tempToolkitClass != null ? (Toolkit)Class.forName(tempToolkitClass).newInstance() : ToolkitFactory.getDefaultToolkit();
        }
        catch (Throwable e) {
            throw new CaptchaException("toolkit has not been abble to be initialized", e);
        }
        return defaultToolkit;
    }

    private static Toolkit getDefaultToolkit() {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit;
    }
}

