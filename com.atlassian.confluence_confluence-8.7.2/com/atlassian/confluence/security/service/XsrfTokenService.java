/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Pair
 *  io.atlassian.fugue.Pair
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.security.service;

import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Pair;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public interface XsrfTokenService {
    @Deprecated
    default public Pair<String, String> generate(HttpServletRequest request) {
        return FugueConversionUtil.toComPair(this.generateToken(request));
    }

    public io.atlassian.fugue.Pair<String, String> generateToken(HttpServletRequest var1);

    @Deprecated
    default public Maybe<Message> validate(HttpServletRequest request) {
        return FugueConversionUtil.toComMaybe(this.validateToken(request));
    }

    public Optional<Message> validateToken(HttpServletRequest var1);
}

