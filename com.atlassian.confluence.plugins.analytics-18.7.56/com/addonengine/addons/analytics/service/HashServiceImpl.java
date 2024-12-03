/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.HashService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import java.security.SecureRandom;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

@ExportAsDevService(value={HashService.class})
@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\u0004H\u0016J\u0018\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0004H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/service/HashServiceImpl;", "Lcom/addonengine/addons/analytics/service/HashService;", "()V", "appSalt", "", "saltSizeBytes", "", "generateInstanceSalt", "hashString", "data", "instanceSalt", "analytics"})
public final class HashServiceImpl
implements HashService {
    @NotNull
    private final String appSalt;
    private final int saltSizeBytes;

    public HashServiceImpl() {
        this.appSalt = "GhkwaoBY)1tsRRqGc.nTAMtlj5VFNNTqvPTDfo";
        this.saltSizeBytes = 32;
    }

    @Override
    @NotNull
    public String hashString(@NotNull String data, @NotNull String instanceSalt) {
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        Intrinsics.checkNotNullParameter((Object)instanceSalt, (String)"instanceSalt");
        String saltedValue = data + '-' + this.appSalt + '-' + instanceSalt;
        String string = DigestUtils.sha1Hex((String)saltedValue);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"sha1Hex(...)");
        return string;
    }

    @Override
    @NotNull
    public String generateInstanceSalt() {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[this.saltSizeBytes];
        sr.nextBytes(salt);
        String string = Base64.encodeBase64String((byte[])salt);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"encodeBase64String(...)");
        return string;
    }
}

