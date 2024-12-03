/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.model.user.User
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;

public class UserTemplateWithCredentialAndAttributes
extends UserTemplateWithAttributes {
    private final PasswordCredential credential;
    private final List<PasswordCredential> credentialHistory = new ArrayList<PasswordCredential>();
    private Date createdDate = null;
    private Date updatedDate = null;

    public UserTemplateWithCredentialAndAttributes(String username, long directoryId, PasswordCredential credential) {
        super(username, directoryId);
        Validate.notNull((Object)credential, (String)"argument credential cannot be null", (Object[])new Object[0]);
        this.credential = credential;
    }

    public UserTemplateWithCredentialAndAttributes(User user, PasswordCredential credential) {
        super(user);
        Validate.notNull((Object)credential, (String)"argument credential cannot be null", (Object[])new Object[0]);
        this.credential = credential;
    }

    public UserTemplateWithCredentialAndAttributes(User user, Map<String, Set<String>> attributes, PasswordCredential credential) {
        this(user, credential);
        if (attributes != null) {
            for (Map.Entry<String, Set<String>> attributeEntry : attributes.entrySet()) {
                this.setAttribute(attributeEntry.getKey(), attributeEntry.getValue());
            }
        }
    }

    public PasswordCredential getCredential() {
        return this.credential;
    }

    public List<PasswordCredential> getCredentialHistory() {
        return this.credentialHistory;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}

