/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.emailgateway.events;

import com.atlassian.confluence.plugins.emailgateway.events.EmailHandlingExceptionPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SimpleEmailHandlingExceptionPayload
implements EmailHandlingExceptionPayload {
    private final String emailAddress;
    private final String emailSubject;
    private final boolean createPageError;
    private final boolean attachmentError;
    private final boolean readOnlyModeError;

    @JsonCreator
    public SimpleEmailHandlingExceptionPayload(@JsonProperty(value="emailAddress") String emailAddress, @JsonProperty(value="emailSubject") String emailSubject, @JsonProperty(value="createPageError") boolean createPageError, @JsonProperty(value="attachmentError") boolean attachmentError, @JsonProperty(value="readOnlyModeError") boolean readOnlyModeError) {
        this.emailAddress = emailAddress;
        this.emailSubject = emailSubject;
        this.createPageError = createPageError;
        this.attachmentError = attachmentError;
        this.readOnlyModeError = readOnlyModeError;
    }

    public Maybe<String> getOriginatingUserKey() {
        return Option.none();
    }

    public Optional<UserKey> getOriginatorUserKey() {
        return Optional.empty();
    }

    @Override
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @Override
    public String getEmailSubject() {
        return this.emailSubject;
    }

    @Override
    public boolean isAttachmentError() {
        return this.attachmentError;
    }

    @Override
    public boolean isCreatePageError() {
        return this.createPageError;
    }

    @Override
    public boolean isReadOnlyModeError() {
        return this.readOnlyModeError;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleEmailHandlingExceptionPayload{");
        sb.append("emailAddress='").append(this.emailAddress).append('\'');
        sb.append(", emailSubject='").append(this.emailSubject).append('\'');
        sb.append(", createPageError='").append(this.createPageError).append('\'');
        sb.append(", attachmentError='").append(this.attachmentError).append('\'');
        sb.append(", readOnlyModeError='").append(this.readOnlyModeError).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

