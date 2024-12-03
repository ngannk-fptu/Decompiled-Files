/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;

@ExperimentalApi
public class UserStatus
extends BaseApiEnum {
    public static final UserStatus CURRENT = new UserStatus("current");
    public static final UserStatus DEACTIVATED = new UserStatus("deactivated");
    public static final UserStatus EXTERNALLY_DELETED = new UserStatus("externallyDeleted");
    public static final UserStatus UNLICENSED = new UserStatus("unlicensed");
    private static final List<UserStatus> BUILT_IN = Collections.unmodifiableList(Arrays.asList(CURRENT, DEACTIVATED, EXTERNALLY_DELETED, UNLICENSED));

    @JsonCreator
    public static UserStatus valueOf(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        for (UserStatus status : BUILT_IN) {
            if (!status.getValue().equals(str)) continue;
            return status;
        }
        return new UserStatus(str);
    }

    private UserStatus(String value) {
        super(value);
    }
}

