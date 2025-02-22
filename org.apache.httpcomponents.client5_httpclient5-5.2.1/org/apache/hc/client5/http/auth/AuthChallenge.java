/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.NameValuePair
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.hc.client5.http.auth.ChallengeType;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class AuthChallenge {
    private final ChallengeType challengeType;
    private final String schemeName;
    private final String value;
    private final List<NameValuePair> params;

    public AuthChallenge(ChallengeType challengeType, String schemeName, String value, List<? extends NameValuePair> params) {
        this.challengeType = (ChallengeType)((Object)Args.notNull((Object)((Object)challengeType), (String)"Challenge type"));
        this.schemeName = (String)Args.notNull((Object)schemeName, (String)"schemeName");
        this.value = value;
        this.params = params != null ? Collections.unmodifiableList(new ArrayList<NameValuePair>(params)) : null;
    }

    public AuthChallenge(ChallengeType challengeType, String schemeName, NameValuePair ... params) {
        this(challengeType, schemeName, null, Arrays.asList(params));
    }

    public ChallengeType getChallengeType() {
        return this.challengeType;
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    public String getValue() {
        return this.value;
    }

    public List<NameValuePair> getParams() {
        return this.params;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.schemeName).append(" ");
        if (this.value != null) {
            buffer.append(this.value);
        } else if (this.params != null) {
            buffer.append(this.params);
        }
        return buffer.toString();
    }
}

