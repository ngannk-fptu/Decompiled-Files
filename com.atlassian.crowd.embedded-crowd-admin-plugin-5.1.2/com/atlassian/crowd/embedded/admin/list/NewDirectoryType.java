/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.crowd.embedded.admin.list;

import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum NewDirectoryType {
    ACTIVE_DIRECTORY("embedded.crowd.directory.type.microsoft.active.directory", "/configure/activedirectory/"),
    LDAP("embedded.crowd.directory.type.ldap", "/configure/ldap/"),
    DELEGATING_LDAP("embedded.crowd.directory.type.delegating.ldap", "/configure/delegatingldap/"),
    CROWD("embedded.crowd.directory.type.crowd", "/configure/crowd/"),
    JIRA("embedded.crowd.directory.type.jira", "/configure/jira/"),
    JIRAJDBC("embedded.crowd.directory.type.jirajdbc", "/configure/jirajdbc");

    private static ApplicationType currentApplicationType;
    private static List<NewDirectoryType> validNewDirectoryTypes;
    private final Message label;
    private final String formUrl;

    private NewDirectoryType(String labelKey, String formUrl) {
        this.label = SimpleMessage.instance(labelKey, new Serializable[0]);
        this.formUrl = formUrl;
    }

    public Message getLabel() {
        return this.label;
    }

    public String getFormUrl() {
        return this.formUrl;
    }

    public static List<NewDirectoryType> getValidNewDirectoryTypes(ApplicationType applicationType) {
        if (validNewDirectoryTypes == null || currentApplicationType != applicationType) {
            ArrayList<NewDirectoryType> values = new ArrayList<NewDirectoryType>(Arrays.asList(NewDirectoryType.values()));
            if (!applicationType.equals((Object)ApplicationType.CONFLUENCE)) {
                values.remove((Object)JIRAJDBC);
            }
            currentApplicationType = applicationType;
            validNewDirectoryTypes = Collections.unmodifiableList(values);
        }
        return validNewDirectoryTypes;
    }
}

