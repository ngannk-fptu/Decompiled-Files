/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.web.Icon
 */
package com.atlassian.confluence.plugins.restapi.examples;

import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.plugins.restapi.enrich.StaticEnricherFilter;
import java.util.Arrays;

public class UserExamples {
    public static final Object GROUP_LIST_EXAMPLE = StaticEnricherFilter.enrichResponse(UserExamples.createPageResponse(UserExamples.makeGroupExample(), UserExamples.makeGroupExample2()));
    public static final Object GROUP_RESPONSE_EXAMPLE = StaticEnricherFilter.enrichResponse(UserExamples.makeGroupExample());
    public static final Object USER_RESPONSE_EXAMPLE = StaticEnricherFilter.enrichResponse(UserExamples.makeUserExample());
    public static final Object ANONYMOUS_EXAMPLE = StaticEnricherFilter.enrichRequest(UserExamples.makeAnonymousExample());
    public static final Object USER_LIST_EXAMPLE = StaticEnricherFilter.enrichRequest(UserExamples.createPageResponse(UserExamples.makeUserExample()));

    private static Object makeAnonymousExample() {
        return new Anonymous(UserExamples.makeIcon(), "Anonymous");
    }

    private static User makeUserExample() {
        return KnownUser.builder().displayName("Joe Smith").username("jsmith").userKey("402880824ff933a4014ff9345d7c0002").profilePicture(UserExamples.makeIcon()).build();
    }

    private static Group makeGroupExample() {
        return new Group("somegroup");
    }

    private static Group makeGroupExample2() {
        return new Group("anothergroup");
    }

    private static Icon makeIcon() {
        return new Icon("/wiki/relative/avatar.png", 48, 48, true);
    }

    private static PageResponse createPageResponse(Object ... item) {
        return PageResponseImpl.from(Arrays.asList(item), (boolean)false).build();
    }
}

