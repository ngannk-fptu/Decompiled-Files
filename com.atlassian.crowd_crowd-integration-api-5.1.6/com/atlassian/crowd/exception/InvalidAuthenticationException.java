/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.CrowdException
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.CrowdException;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringEscapeUtils;

public class InvalidAuthenticationException
extends CrowdException {
    private String username;
    private Directory directory;

    public InvalidAuthenticationException(String username, Directory directory, Throwable cause) {
        super(String.format("Could not authenticate user %s in directory %s", username, directory.getName()), cause);
        this.username = username;
        this.directory = directory;
    }

    public InvalidAuthenticationException(String msg) {
        super(msg);
    }

    public InvalidAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidAuthenticationException(Throwable cause) {
        super(cause);
    }

    public static InvalidAuthenticationException newInstanceWithName(String name) {
        return new InvalidAuthenticationException("Account with name <" + name + "> failed to authenticate");
    }

    public static InvalidAuthenticationException newInstanceWithName(String name, Throwable cause) {
        return new InvalidAuthenticationException("Account with name <" + name + "> failed to authenticate", cause);
    }

    public static InvalidAuthenticationException newInstanceWithNameAndDescriptionFromCause(String name, Throwable cause) {
        return new InvalidAuthenticationException("Account with name <" + name + "> failed to authenticate: " + StringEscapeUtils.escapeJava((String)cause.getMessage()));
    }

    @Nullable
    public String getUsername() {
        return this.username;
    }

    @Nullable
    public Directory getDirectory() {
        return this.directory;
    }
}

