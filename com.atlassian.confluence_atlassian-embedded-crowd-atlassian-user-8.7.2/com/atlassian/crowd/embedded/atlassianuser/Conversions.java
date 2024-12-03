/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Group
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdGroup;
import com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser;
import com.google.common.base.Function;

@Deprecated
abstract class Conversions {
    static final Function<Group, com.atlassian.user.Group> TO_ATLASSIAN_GROUP = new Function<Group, com.atlassian.user.Group>(){

        public com.atlassian.user.Group apply(Group from) {
            return from == null ? null : new EmbeddedCrowdGroup(from);
        }
    };
    static final Function<User, com.atlassian.user.User> TO_ATLASSIAN_USER = new Function<User, com.atlassian.user.User>(){

        public com.atlassian.user.User apply(User from) {
            return from == null ? null : new EmbeddedCrowdUser(from);
        }
    };

    Conversions() {
    }
}

