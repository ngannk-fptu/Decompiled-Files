/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.embedded.admin.dto;

import com.atlassian.crowd.embedded.admin.dto.PageLink;
import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewUserDto;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserSyncPreviewResult {
    @XmlElement
    private String usersCountSubtitle;
    @XmlElement
    private int totalUsersCount;
    @XmlElement
    private List<UserSyncPreviewUserDto> users;
    @XmlElement
    private List<PageLink> pageLinks;

    public String getUsersCountSubtitle() {
        return this.usersCountSubtitle;
    }

    public void setUsersCountSubtitle(String usersCountSubtitle) {
        this.usersCountSubtitle = usersCountSubtitle;
    }

    public int getTotalUsersCount() {
        return this.totalUsersCount;
    }

    public void setTotalUsersCount(int totalUsersCount) {
        this.totalUsersCount = totalUsersCount;
    }

    public List<UserSyncPreviewUserDto> getUsers() {
        return this.users;
    }

    public void setUsers(List<UserSyncPreviewUserDto> users) {
        this.users = users;
    }

    public List<PageLink> getPageLinks() {
        return this.pageLinks;
    }

    public void setPageLinks(List<PageLink> pageLinks) {
        this.pageLinks = pageLinks;
    }
}

