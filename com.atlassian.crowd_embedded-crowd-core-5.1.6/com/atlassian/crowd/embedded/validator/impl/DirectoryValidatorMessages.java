/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.validator.impl;

public interface DirectoryValidatorMessages {

    public static interface CROWD_DIRECTORY {
        public static final String INVALID_APPLICATION_NAME = "directorycrowd.applicationname.invalid";
        public static final String INVALID_PASSWORD = "directorycrowd.applicationpassword.invalid";
        public static final String INVALID_HTTP_MAX_CONNECTIONS = "directorycrowd.http.maxconnections.invalid";
        public static final String INVALID_HTTP_PROXY_PORT = "directorycrowd.proxy.port.invalid";
        public static final String INVALID_CROWD_SERVER_URL = "directorycrowd.url.invalid";
        public static final String CONNECTION_TEST_SUCCESS = "directorycrowd.testconnection.success";
        public static final String CONNECTION_TEST_FAILURE = "directorycrowd.testconnection.invalid";
    }

    public static interface SYNCHRONISATION_CONFIGURATION {
        public static final String INVALID_POLLING_INTERVAL = "directory.polling.interval.invalid";
        public static final String INVALID_CRON_EXPRESSION = "directory.polling.cron.invalid";
    }

    public static interface USER_CONFIGURATION {
        public static final String INVALID_USER_FIRST_NAME = "directoryconnector.userfirstnameattribute.invalid";
        public static final String INVALID_USER_GROUP = "directoryconnector.usermemberofattribute.invalid";
        public static final String INVALID_USER_LAST_NAME = "directoryconnector.userlastnameattribute.invalid";
        public static final String INVALID_USER_DISPLAY_NAME = "directoryconnector.userdisplaynameattribute.invalid";
        public static final String INVALID_USER_MAIL = "directoryconnector.usermailattribute.invalid";
        public static final String INVALID_USERNAME = "directoryconnector.usernameattribute.invalid";
        public static final String INVALID_USERNAME_RDN = "directoryconnector.usernamerdnattribute.invalid";
        public static final String INVALID_USER_OBJECT_FILTER = "directoryconnector.userobjectfilter.invalid";
        public static final String INVALID_USER_OBJECT_CLASS = "directoryconnector.userobjectclass.invalid";
        public static final String INVALID_USER_PASSWORD = "directoryconnector.userpassword.invalid";
    }

    public static interface GROUP_CONFIGURATION {
        public static final String INVALID_GROUP_NAME = "directoryconnector.groupname.invalid";
        public static final String INVALID_GROUP_DESCRIPTION = "directoryconnector.groupdescription.invalid";
        public static final String INVALID_GROUP_MEMBER = "directoryconnector.groupmember.invalid";
        public static final String INVALID_GROUP_OBJECT_CLASS = "directoryconnector.groupobjectclass.invalid";
        public static final String INVALID_GROUP_OBJECT_FILTER = "directoryconnector.groupobjectfilter.invalid";
    }

    public static interface LDAP_CONNECTION {
        public static final String INVALID_CONNECTOR_URL = "directoryconnector.url.invalid";
        public static final String EMPTY_BASE_DN = "directoryconnector.basedn.invalid.blank";
        public static final String INVALID_BASE_DN = "directoryconnector.basedn.invalid";
        public static final String INVALID_PAGE_SIZE = "directoryconnector.pagedresultscontrolsize.invalid";
        public static final String LOCAL_USER_STATUS_WITHOUT_CACHE = "directoryconnector.localuserstatus.withoutcache.message";
        public static final String LOCAL_GROUPS_ENABLED_WITHOUT_CACHE = "directoryconnector.localgroups.withoutcache.message";
    }

    public static interface AZURE_AD {
        public static final String INVALID_GRAPH_API = "directory.azure.ad.graph.api.invalid";
        public static final String INVALID_AUTHORITY_API = "directory.azure.ad.authority.api.invalid";
        public static final String INVALID_WEBAPP_ID = "directory.azure.ad.web.app.id.invalid";
        public static final String INVALID_WEBAPP_SECRET = "directory.azure.ad.web.app.secret.invalid";
        public static final String INVALID_NATIVE_APP_ID = "directory.azure.ad.native.app.id.invalid";
        public static final String INVALID_TENANT_ID = "directory.azure.ad.tenant.id.invalid";
        public static final String INVALID_READ_TIMEOUT = "directory.azure.ad.read.timeout.invalid";
        public static final String INVALID_CONNECTION_TIMEOUT = "directory.azure.ad.connection.timeout.invalid";
    }

    public static interface INTERNAL_DIRECTORY {
        public static final String INVALID_PASSWORD_REGEX_PATTERN = "directoryinternal.passwordregex.invalid";
        public static final String INVALID_PASSWORD_MAX_CHANGE_TIME = "directoryinternal.passwordmaxchangetime.invalid";
        public static final String INVALID_PASSWORD_MAX_AUTHENTICATE_ATTEMPTS = "directoryinternal.passwordmaxattempts.invalid";
        public static final String INVALID_PASSWORD_HISTORY_COUNTS = "directoryinternal.passwordhistorycount.invalid";
        public static final String INVALID_USER_ENCRYPTION_METHOD = "directoryinternal.userencryptionmethod.invalid";
        public static final String INVALID_REMIND_PERIODS_FORMAT = "directoryinternal.remindperiods.invalid.format";
        public static final String INVALID_REMIND_PERIODS_VALUES = "directoryinternal.remindperiods.invalid.values";
        public static final String MAX_PASSWORD_CHANGE_TIME_TOO_LOW = "directoryinternal.passwordmaxchangetime.too.low";
    }

    public static interface CUSTOM_DIRECTORY {
        public static final String INVALID_IMPL_CLASS = "directorycustom.implementationclass.invalid";
    }

    public static interface DIRECTORY {
        public static final String INVALID_NAME = "directoryinternal.name.invalid";
        public static final String NAME_ALREADY_EXISTS = "invalid.namealreadyexist.directory";
    }
}

