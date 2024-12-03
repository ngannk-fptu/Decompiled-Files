/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.FormParam
 */
package com.atlassian.confluence.extra.calendar3.rest.param;

import com.atlassian.confluence.extra.calendar3.rest.param.SpaceContextParam;
import java.util.List;
import javax.ws.rs.FormParam;

public class RestrictSubCalendarParam
extends SpaceContextParam {
    @FormParam(value="subCalendarId")
    String subCalendarId;
    @FormParam(value="spaceKey")
    String spaceKey;
    @FormParam(value="include")
    List<String> subCalendarIncludes;
    @FormParam(value="updateUsersPermittedToView")
    boolean updateUsersPermittedToView;
    @FormParam(value="usersPermittedToView")
    List<String> userIdsPermittedToView;
    @FormParam(value="updateGroupsPermittedToView")
    boolean updateGroupsPermittedToView;
    @FormParam(value="groupsPermittedToView")
    List<String> groupsPermittedToView;
    @FormParam(value="updateUsersPermittedToEdit")
    boolean updateUsersPermittedToEdit;
    @FormParam(value="usersPermittedToEdit")
    List<String> userIdsPermittedToEdit;
    @FormParam(value="updateGroupsPermittedToEdit")
    boolean updateGroupsPermittedToEdit;
    @FormParam(value="groupsPermittedToEdit")
    List<String> groupsPermittedToEdit;

    public List<String> getGroupsPermittedToEdit() {
        return this.groupsPermittedToEdit;
    }

    public void setGroupsPermittedToEdit(List<String> groupsPermittedToEdit) {
        this.groupsPermittedToEdit = groupsPermittedToEdit;
    }

    public List<String> getGroupsPermittedToView() {
        return this.groupsPermittedToView;
    }

    public void setGroupsPermittedToView(List<String> groupsPermittedToView) {
        this.groupsPermittedToView = groupsPermittedToView;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSubCalendarId() {
        return this.subCalendarId;
    }

    public void setSubCalendarId(String subCalendarId) {
        this.subCalendarId = subCalendarId;
    }

    public List<String> getSubCalendarIncludes() {
        return this.subCalendarIncludes;
    }

    public void setSubCalendarIncludes(List<String> subCalendarIncludes) {
        this.subCalendarIncludes = subCalendarIncludes;
    }

    public boolean isUpdateGroupsPermittedToEdit() {
        return this.updateGroupsPermittedToEdit;
    }

    public void setUpdateGroupsPermittedToEdit(boolean updateGroupsPermittedToEdit) {
        this.updateGroupsPermittedToEdit = updateGroupsPermittedToEdit;
    }

    public boolean isUpdateGroupsPermittedToView() {
        return this.updateGroupsPermittedToView;
    }

    public void setUpdateGroupsPermittedToView(boolean updateGroupsPermittedToView) {
        this.updateGroupsPermittedToView = updateGroupsPermittedToView;
    }

    public boolean isUpdateUsersPermittedToEdit() {
        return this.updateUsersPermittedToEdit;
    }

    public void setUpdateUsersPermittedToEdit(boolean updateUsersPermittedToEdit) {
        this.updateUsersPermittedToEdit = updateUsersPermittedToEdit;
    }

    public boolean isUpdateUsersPermittedToView() {
        return this.updateUsersPermittedToView;
    }

    public void setUpdateUsersPermittedToView(boolean updateUsersPermittedToView) {
        this.updateUsersPermittedToView = updateUsersPermittedToView;
    }

    public List<String> getUserIdsPermittedToEdit() {
        return this.userIdsPermittedToEdit;
    }

    public void setUserIdsPermittedToEdit(List<String> userIdsPermittedToEdit) {
        this.userIdsPermittedToEdit = userIdsPermittedToEdit;
    }

    public List<String> getUserIdsPermittedToView() {
        return this.userIdsPermittedToView;
    }

    public void setUserIdsPermittedToView(List<String> userIdsPermittedToView) {
        this.userIdsPermittedToView = userIdsPermittedToView;
    }
}

