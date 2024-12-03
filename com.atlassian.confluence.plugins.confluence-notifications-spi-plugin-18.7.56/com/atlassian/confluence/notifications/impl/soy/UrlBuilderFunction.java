/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.notifications.impl.soy;

import com.atlassian.confluence.notifications.JwtTokenGenerator;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.util.Map;
import java.util.Set;

public class UrlBuilderFunction
implements SoyServerFunction<String> {
    private static final String STOP_WATCHING_ACTION = "stop-watching";
    private static final String LIKE_ACTION = "like";
    private static final ImmutableSet<Integer> ARG_SIZE = ImmutableSet.of((Object)4, (Object)5);
    private static final int BASE_URL_ARG = 0;
    private static final int PARAM_MAP_ARG = 1;
    private static final int ACTION_ARG = 2;
    private static final int USER_KEY_ARG = 3;
    private static final int TOKEN_ARG = 4;
    private final JwtTokenGenerator jwtTokenGenerator;

    public UrlBuilderFunction(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = (JwtTokenGenerator)Preconditions.checkNotNull((Object)jwtTokenGenerator);
    }

    public String apply(Object ... args) {
        Option userKey;
        UrlBuilder urlBuilder = new UrlBuilder(args[0].toString());
        Map parameterMap = (Map)args[1];
        for (Map.Entry parameter : parameterMap.entrySet()) {
            urlBuilder.add((String)parameter.getKey(), (String)parameter.getValue());
        }
        Option action = (Option)args[2];
        if (this.isAddedJwtToken((Option<String>)action) && (userKey = (Option)args[3]).isDefined()) {
            Option<String> jwtToken;
            Option<String> option = jwtToken = args.length > 4 ? Option.option((Object)((String)args[4])) : this.jwtTokenGenerator.generate("GET", URI.create(urlBuilder.toUrl()), ((UserKey)userKey.get()).getStringValue());
            if (jwtToken.isDefined()) {
                urlBuilder.add("jwt", (String)jwtToken.get());
            }
        }
        return urlBuilder.toUrl();
    }

    public String getName() {
        return "urlBuilder";
    }

    public Set<Integer> validArgSizes() {
        return ARG_SIZE;
    }

    private boolean isAddedJwtToken(Option<String> action) {
        return action.isDefined() && (((String)action.get()).equals(STOP_WATCHING_ACTION) || ((String)action.get()).equals(LIKE_ACTION));
    }
}

