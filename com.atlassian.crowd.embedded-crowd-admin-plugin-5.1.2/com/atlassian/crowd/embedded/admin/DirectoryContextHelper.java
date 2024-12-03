/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import javax.servlet.http.HttpServletRequest;

public class DirectoryContextHelper {
    public static final String DIRECTORY_ID_PARAM = "directoryId";
    private CrowdDirectoryService crowdDirectoryService;
    private CrowdService crowdService;
    private UserManager userManager;

    public boolean hasDirectoryId(HttpServletRequest request) {
        return request.getParameter(DIRECTORY_ID_PARAM) != null && !request.getParameter(DIRECTORY_ID_PARAM).equals("0");
    }

    public Directory getDirectory(long directoryId) throws DirectoryNotFoundException {
        try {
            Directory directoryById = this.crowdDirectoryService.findDirectoryById(directoryId);
            if (directoryById == null) {
                throw new DirectoryNotFoundException(Long.valueOf(directoryId));
            }
            return directoryById;
        }
        catch (RuntimeException runtimeException) {
            Throwable cause = runtimeException.getCause();
            if (cause instanceof DirectoryNotFoundException) {
                throw (DirectoryNotFoundException)cause;
            }
            throw runtimeException;
        }
    }

    public Directory getDirectory(HttpServletRequest request) throws DirectoryNotFoundException {
        try {
            long directoryId = Long.parseLong(request.getParameter(DIRECTORY_ID_PARAM));
            Directory directory = this.getDirectory(directoryId);
            if (directory == null) {
                throw new DirectoryNotFoundException(Long.valueOf(directoryId));
            }
            return directory;
        }
        catch (NumberFormatException e) {
            throw new DirectoryNotFoundException((Throwable)e);
        }
    }

    public boolean isContextUserFromDirectory(HttpServletRequest request) throws DirectoryNotFoundException {
        return this.isContextUserFromDirectory(this.getDirectory(request), request);
    }

    public boolean isContextUserFromDirectory(Directory directory, HttpServletRequest request) {
        UserProfile remoteUser = this.userManager.getRemoteUser(request);
        if (remoteUser == null) {
            return false;
        }
        User currentUser = this.crowdService.getUser(remoteUser.getUsername());
        return currentUser != null && currentUser.getDirectoryId() == directory.getId().longValue();
    }

    public void setCrowdDirectoryService(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public void setCrowdService(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}

