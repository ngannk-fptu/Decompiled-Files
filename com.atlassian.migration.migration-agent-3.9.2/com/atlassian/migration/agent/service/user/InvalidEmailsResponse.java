/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.user.InvalidEmail;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InvalidEmailsResponse {
    private final List<InvalidEmail> invalidEmails;
    private final Map<Integer, String> errorsMapper;

    @JsonCreator
    public InvalidEmailsResponse(@JsonProperty(value="invalidEmails") List<InvalidEmail> invalidEmails, @JsonProperty(value="errorMapper") Map<Integer, String> errorsMapper) {
        this.invalidEmails = invalidEmails;
        this.errorsMapper = errorsMapper;
    }

    @Generated
    public List<InvalidEmail> getInvalidEmails() {
        return this.invalidEmails;
    }

    @Generated
    public Map<Integer, String> getErrorsMapper() {
        return this.errorsMapper;
    }
}

