/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.service.multitype;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.sound.SoundCaptchaService;
import com.octo.captcha.service.text.TextCaptchaService;

public interface MultiTypeCaptchaService
extends ImageCaptchaService,
SoundCaptchaService,
TextCaptchaService {
}

