/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.model.licensing;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.licensing.DirectoryInfo;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class LicensedUser {
    private Long id;
    private String username;
    private Date lastActive;
    private String fullName;
    private String email;
    private DirectoryInfo directory;
    private String lowerUsername;
    private String lowerFullName;
    private String lowerEmail;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
        this.lowerUsername = IdentifierUtils.toLowerCase((String)username);
    }

    public Date getLastActive() {
        return this.lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.lowerFullName = StringUtils.lowerCase((String)fullName);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.lowerEmail = StringUtils.lowerCase((String)email);
    }

    public DirectoryInfo getDirectory() {
        return this.directory;
    }

    public void setDirectory(DirectoryInfo directory) {
        this.directory = directory;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LicensedUser user = (LicensedUser)o;
        return Objects.equals(this.id, user.id) && Objects.equals(this.username, user.username) && Objects.equals(this.lastActive, user.lastActive) && Objects.equals(this.fullName, user.fullName) && Objects.equals(this.email, user.email) && Objects.equals(this.directory, user.directory);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.username, this.lastActive, this.fullName, this.email, this.directory);
    }
}

