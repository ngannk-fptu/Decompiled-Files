/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.CaptchaServiceException
 */
package com.octo.captcha.module.jmx;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.ManageableCaptchaService;

public class JMXRegistrationHelper {
    public static void registerToMBeanServer(ManageableCaptchaService service, String name) throws CaptchaServiceException {
    }

    public static void unregisterFromMBeanServer(String name) {
    }
}

