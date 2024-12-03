/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.admin.support;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DirectoryTroubleshooter {
    private static final String TEST_GET_MEMBERS_SUCCESS_MESSAGE = Test.GET_MEMBERS.getKey() + ".success";
    private static final String TEST_GET_MEMBERSHIPS_SUCCESS_MESSAGE = Test.GET_MEMBERSHIPS.getKey() + ".success";
    private static final String FAILED_KEY = "embedded.crowd.directory.test.fail";
    private static final String SUCCESS_KEY = "embedded.crowd.directory.test.success";
    private static final String SKIPPED_KEY = "embedded.crowd.directory.test.not.performed";
    private static final String EMPTY_EXTERNAL_ID_HINT = "embedded.crowd.directory.test.empty.externalid.hint";
    private static final String INVALID_EXTERNAL_ID_HINT = "embedded.crowd.directory.test.invalid.externalid.hint";
    private DirectoryManager directoryManager;
    private I18nResolver i18nResolver;

    public Iterable<TestResult> troubleshootDirectory(RemoteDirectory directory, String username, String password) {
        ArrayList<TestResult> testResults = new ArrayList<TestResult>();
        boolean connectionOk = this.testConnection(directory, testResults);
        User user = null;
        if (connectionOk) {
            user = this.checkAndGetUserIfExists(directory, username, testResults);
        }
        this.checkIfUserCanBeRenamed(user, directory, testResults);
        this.checkMembership(user, directory, testResults);
        this.checkAuthentication(user, directory, password, testResults);
        return testResults;
    }

    protected User checkAndGetUserIfExists(RemoteDirectory remoteDirectory, String username, List<TestResult> testResults) {
        User user = null;
        if (!StringUtils.isBlank((CharSequence)username)) {
            user = this.testGetUser(remoteDirectory, username.trim(), testResults);
        } else {
            this.skipTestGetUser(testResults);
        }
        return user;
    }

    protected void checkIfUserCanBeRenamed(User user, RemoteDirectory remoteDirectory, List<TestResult> testResults) {
        if (user != null) {
            this.testRename(remoteDirectory.getDirectoryId(), user.getExternalId(), testResults);
        } else {
            this.skipTestRename(testResults);
        }
    }

    protected void checkMembership(User user, RemoteDirectory remoteDirectory, List<TestResult> testResults) {
        if (user != null) {
            this.testMemberships(remoteDirectory, user.getName(), testResults);
        } else {
            this.skipTestMemberships(testResults);
        }
    }

    protected void checkAuthentication(User user, RemoteDirectory remoteDirectory, String password, List<TestResult> testResults) {
        if (user != null && !StringUtils.isEmpty((CharSequence)user.getName()) && !StringUtils.isEmpty((CharSequence)password)) {
            this.testAuthenticate(remoteDirectory, user.getName(), password, testResults);
        } else {
            this.skipTestAuthenticate(testResults);
        }
    }

    private boolean testConnection(RemoteDirectory directory, List<TestResult> testResults) {
        try {
            directory.testConnection();
            testResults.add(this.createSuccessfulTestResult(Test.CONNECT));
            return true;
        }
        catch (OperationFailedException e) {
            testResults.add(this.createFailedTestResult(Test.CONNECT, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
            return false;
        }
    }

    private User testGetUser(RemoteDirectory directory, String username, List<TestResult> testResults) {
        User user = null;
        try {
            user = directory.findUserByName(username);
            testResults.add(this.createSuccessfulTestResult(Test.GET_USER));
        }
        catch (Exception e) {
            testResults.add(this.createFailedTestResult(Test.GET_USER, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
        }
        return user;
    }

    private void skipTestGetUser(List<TestResult> testResults) {
        testResults.add(this.createSkippedTestResult(Test.GET_USER));
    }

    private void testRename(long directoryId, String externalId, List<TestResult> testResults) {
        try {
            Map directoryAttributes = this.directoryManager.findDirectoryById(directoryId).getAttributes();
            String externalIdAttribute = (String)directoryAttributes.get("ldap.external.id");
            if (StringUtils.isBlank((CharSequence)externalIdAttribute)) {
                testResults.add(this.createSkippedTestResultWithMessage(Test.GET_EXTERNAL_ID, EMPTY_EXTERNAL_ID_HINT));
            } else if (StringUtils.isNotBlank((CharSequence)externalId)) {
                testResults.add(this.createSuccessfulTestResult(Test.GET_EXTERNAL_ID));
            } else {
                testResults.add(this.createFailedTestResult(Test.GET_EXTERNAL_ID, (ImmutableList<String>)ImmutableList.of((Object)this.i18nResolver.getText(INVALID_EXTERNAL_ID_HINT))));
            }
        }
        catch (DirectoryNotFoundException e) {
            testResults.add(this.createFailedTestResult(Test.GET_EXTERNAL_ID, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
        }
    }

    private void testMemberships(RemoteDirectory directory, String username, List<TestResult> testResults) {
        block12: {
            List groups = null;
            try {
                MembershipQuery query = QueryBuilder.createMembershipQuery((int)-1, (int)0, (boolean)false, (EntityDescriptor)EntityDescriptor.group(), Group.class, (EntityDescriptor)EntityDescriptor.user(), (String)username);
                groups = directory.searchGroupRelationships(query);
                if (groups.isEmpty()) {
                    testResults.add(this.createFailedTestResult(Test.GET_MEMBERSHIPS));
                } else {
                    testResults.add(this.createSuccessfulTestResultWithMessage(Test.GET_MEMBERSHIPS, this.i18nResolver.getText(TEST_GET_MEMBERSHIPS_SUCCESS_MESSAGE, new Serializable[]{Integer.valueOf(groups.size())})));
                }
            }
            catch (Exception e) {
                testResults.add(this.createFailedTestResult(Test.GET_MEMBERSHIPS, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
                testResults.add(this.createSkippedTestResult(Test.GET_GROUP));
                testResults.add(this.createFailedTestResult(Test.GET_MEMBERS));
            }
            if (groups != null && groups.size() > 0) {
                Group group = null;
                try {
                    group = directory.findGroupByName(((Group)groups.get(0)).getName());
                    testResults.add(this.createSuccessfulTestResult(Test.GET_GROUP));
                }
                catch (Exception e) {
                    testResults.add(this.createFailedTestResult(Test.GET_GROUP, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
                    testResults.add(this.createFailedTestResult(Test.GET_MEMBERS));
                }
                if (group != null) {
                    try {
                        MembershipQuery query = QueryBuilder.createMembershipQuery((int)-1, (int)0, (boolean)true, (EntityDescriptor)EntityDescriptor.user(), User.class, (EntityDescriptor)EntityDescriptor.group(), (String)group.getName());
                        List members = directory.searchGroupRelationships(query);
                        if (members.isEmpty()) {
                            testResults.add(this.createFailedTestResult(Test.GET_MEMBERS));
                            break block12;
                        }
                        testResults.add(this.createSuccessfulTestResultWithMessage(Test.GET_MEMBERS, this.i18nResolver.getText(TEST_GET_MEMBERS_SUCCESS_MESSAGE, new Serializable[]{Integer.valueOf(members.size())})));
                    }
                    catch (Exception e) {
                        testResults.add(this.createFailedTestResult(Test.GET_MEMBERS, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
                    }
                }
            } else {
                testResults.add(this.createSkippedTestResult(Test.GET_GROUP));
                testResults.add(this.createSkippedTestResult(Test.GET_MEMBERS));
            }
        }
    }

    private void skipTestRename(List<TestResult> testResults) {
        testResults.add(this.createSkippedTestResult(Test.GET_EXTERNAL_ID));
    }

    private void skipTestMemberships(List<TestResult> testResults) {
        testResults.add(this.createSkippedTestResult(Test.GET_MEMBERSHIPS));
        testResults.add(this.createSkippedTestResult(Test.GET_GROUP));
        testResults.add(this.createSkippedTestResult(Test.GET_MEMBERS));
    }

    private User testAuthenticate(RemoteDirectory directory, String username, String password, List<TestResult> testResults) {
        User user = null;
        try {
            this.directoryManager.authenticateUser(directory.getDirectoryId(), username, new PasswordCredential(password));
            user = directory.authenticate(username, new PasswordCredential(password));
            testResults.add(this.createSuccessfulTestResult(Test.AUTHENTICATE));
        }
        catch (Exception e) {
            testResults.add(this.createFailedTestResult(Test.AUTHENTICATE, (ImmutableList<String>)ImmutableList.of((Object)e.getMessage())));
        }
        return user;
    }

    private void skipTestAuthenticate(List<TestResult> testResults) {
        testResults.add(this.createSkippedTestResult(Test.AUTHENTICATE));
    }

    public void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public TestResult createSkippedTestResult(Test test) {
        return new TestResult(test, this.i18nResolver.getText(SKIPPED_KEY), false, false);
    }

    public TestResult createSkippedTestResultWithMessage(Test test, String messageKey) {
        return new TestResult(test, this.i18nResolver.getText(messageKey), false, false);
    }

    public TestResult createSuccessfulTestResult(Test test) {
        return new TestResult(test, this.i18nResolver.getText(SUCCESS_KEY), true, true);
    }

    public TestResult createSuccessfulTestResultWithMessage(Test test, String successMessage) {
        return new TestResult(test, successMessage, true, true);
    }

    private TestResult createFailedTestResult(Test test) {
        return this.createFailedTestResult(test, (ImmutableList<String>)ImmutableList.of());
    }

    private TestResult createFailedTestResult(Test test, ImmutableList<String> errorMessages) {
        return new TestResult(test, this.i18nResolver.getText(FAILED_KEY), true, false, (List)errorMessages);
    }

    public static class TestResult {
        private final Test test;
        private final String message;
        private final boolean performed;
        private final boolean successful;
        private final Iterable<String> errors;

        private TestResult(Test test, String message, boolean performed, boolean successful) {
            this(test, message, performed, successful, (List<String>)ImmutableList.of());
        }

        private TestResult(Test test, String message, boolean performed, boolean successful, List<String> errors) {
            this.test = test;
            this.message = message;
            this.performed = performed;
            this.successful = successful;
            this.errors = ImmutableList.copyOf(errors);
        }

        public String getTestNameKey() {
            return this.test.getKey();
        }

        public String getMessage() {
            return this.message;
        }

        public boolean isSuccessful() {
            return this.performed && this.successful;
        }

        public boolean isPerformed() {
            return this.performed;
        }

        public Iterable<String> getErrors() {
            return this.errors;
        }

        public boolean isAddErrorCodeLink() {
            for (String error : this.errors) {
                if (!error.toLowerCase().contains("error code")) continue;
                return true;
            }
            return false;
        }
    }

    public static enum Test {
        CONNECT("embedded.crowd.directory.test.connect"),
        AUTHENTICATE("embedded.crowd.directory.test.authenticate"),
        GET_USER("embedded.crowd.directory.test.get.user"),
        GET_EXTERNAL_ID("embedded.crowd.directory.test.get.externalid"),
        GET_GROUP("embedded.crowd.directory.test.get.group"),
        GET_MEMBERS("embedded.crowd.directory.test.get.members"),
        GET_MEMBERSHIPS("embedded.crowd.directory.test.get.memberships");

        private final String nameKey;

        private Test(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getKey() {
            return this.nameKey;
        }
    }
}

