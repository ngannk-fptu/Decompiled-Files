/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.plugins.rest.common.Link;
import com.google.common.base.Preconditions;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

public class LinkUriHelper {
    private static final String USERNAME_QUERY_PARAM = "username";
    private static final String KEY_QUERY_PARAM = "key";
    private static final String GROUPNAME_QUERY_PARAM = "groupname";
    private static final String PARENT_GROUPNAME_QUERY_PARAM = "parent-groupname";
    private static final String CHILD_GROUPNAME_QUERY_PARAM = "child-groupname";
    private static final String ATTRIBUTENAME_QUERY_PARAM = "attributename";

    private LinkUriHelper() {
    }

    public static Link buildUserLink(URI baseUri, String username) {
        URI userUri = LinkUriHelper.buildUserUri(baseUri, username);
        return Link.self((URI)userUri);
    }

    public static URI buildUserUri(URI baseUri, String username) {
        Preconditions.checkNotNull((Object)baseUri);
        Preconditions.checkNotNull((Object)username);
        UriBuilder builder = UriBuilder.fromUri((URI)baseUri);
        return builder.path("user").queryParam(USERNAME_QUERY_PARAM, new Object[]{"{username}"}).build(new Object[]{username});
    }

    public static URI buildDirectUserGroupUri(URI baseURI, String groupName, String username) {
        Preconditions.checkNotNull((Object)baseURI);
        Preconditions.checkNotNull((Object)groupName);
        UriBuilder builder = UriBuilder.fromUri((URI)baseURI);
        return builder.path("group").path("user").path("direct").queryParam(GROUPNAME_QUERY_PARAM, new Object[]{"{groupName}"}).queryParam(USERNAME_QUERY_PARAM, new Object[]{"{username}"}).build(new Object[]{groupName, username});
    }

    public static Link updateUserLink(Link userLink, String username) {
        URI updatedUri = LinkUriHelper.updateUserUri(userLink.getHref(), username);
        return Link.link((URI)updatedUri, (String)userLink.getRel());
    }

    public static URI updateUserUri(URI userUri, String username) {
        UriBuilder builder = UriBuilder.fromUri((URI)userUri);
        return builder.replaceQueryParam(USERNAME_QUERY_PARAM, new Object[]{"{username}"}).build(new Object[]{username});
    }

    public static Link buildGroupLink(URI baseUri, String groupName) {
        URI groupUri = LinkUriHelper.buildGroupUri(baseUri, groupName);
        return Link.self((URI)groupUri);
    }

    public static URI buildGroupUri(URI baseURI, String groupName) {
        Preconditions.checkNotNull((Object)baseURI);
        Preconditions.checkNotNull((Object)groupName);
        UriBuilder builder = UriBuilder.fromUri((URI)baseURI);
        return builder.path("group").queryParam(GROUPNAME_QUERY_PARAM, new Object[]{"{groupName}"}).build(new Object[]{groupName});
    }

    public static URI buildDirectChildGroupUri(URI baseURI, String groupName, String childGroupName) {
        Preconditions.checkNotNull((Object)baseURI);
        Preconditions.checkNotNull((Object)groupName);
        UriBuilder builder = UriBuilder.fromUri((URI)baseURI);
        return builder.path("group").path("child-group").path("direct").queryParam(GROUPNAME_QUERY_PARAM, new Object[]{"{groupName}"}).queryParam(CHILD_GROUPNAME_QUERY_PARAM, new Object[]{"{childGroupName}"}).build(new Object[]{groupName, childGroupName});
    }

    public static URI buildDirectParentGroupUri(URI baseURI, String groupName, String parentGroupName) {
        Preconditions.checkNotNull((Object)baseURI);
        Preconditions.checkNotNull((Object)groupName);
        UriBuilder builder = UriBuilder.fromUri((URI)baseURI);
        return builder.path("group").path("parent-group").path("direct").queryParam(GROUPNAME_QUERY_PARAM, new Object[]{"{groupName}"}).queryParam(PARENT_GROUPNAME_QUERY_PARAM, new Object[]{"{parentGroupName}"}).build(new Object[]{groupName, parentGroupName});
    }

    public static URI buildDirectParentGroupOfUserUri(URI baseURI, String userName, String parentGroupName) {
        Preconditions.checkNotNull((Object)baseURI);
        Preconditions.checkNotNull((Object)userName);
        UriBuilder builder = UriBuilder.fromUri((URI)baseURI);
        return builder.path("user").path("group").path("direct").queryParam(USERNAME_QUERY_PARAM, new Object[]{"{userName}"}).queryParam(GROUPNAME_QUERY_PARAM, new Object[]{"{groupName}"}).build(new Object[]{userName, parentGroupName});
    }

    public static Link updateGroupLink(Link groupLink, String groupName) {
        URI updatedUri = LinkUriHelper.updateGroupUri(groupLink.getHref(), groupName);
        return Link.link((URI)updatedUri, (String)groupLink.getRel());
    }

    public static URI updateGroupUri(URI groupUri, String groupName) {
        UriBuilder builder = UriBuilder.fromUri((URI)groupUri);
        return builder.replaceQueryParam(GROUPNAME_QUERY_PARAM, new Object[]{"{groupname}"}).build(new Object[]{groupName});
    }

    public static URI buildEntityAttributeListUri(URI entityUri) {
        Preconditions.checkNotNull((Object)entityUri);
        return UriBuilder.fromUri((URI)entityUri).path("attribute").build(new Object[0]);
    }

    public static URI buildEntityAttributeUri(URI attributesUri, String attributeName) {
        Preconditions.checkNotNull((Object)attributesUri);
        Preconditions.checkNotNull((Object)attributeName);
        return UriBuilder.fromUri((URI)attributesUri).queryParam(ATTRIBUTENAME_QUERY_PARAM, new Object[]{"{attributeName}"}).build(new Object[]{attributeName});
    }

    public static URI buildUserPasswordUri(URI userUri) {
        Preconditions.checkNotNull((Object)userUri);
        return UriBuilder.fromUri((URI)userUri).path("password").build(new Object[0]);
    }

    public static URI buildSessionUri(URI baseUri, String token) {
        Preconditions.checkNotNull((Object)baseUri);
        Preconditions.checkNotNull((Object)token);
        return UriBuilder.fromUri((URI)baseUri).path("session/{token}").build(new Object[]{token});
    }

    public static Link buildSessionLink(URI baseUri, String token) {
        return Link.self((URI)LinkUriHelper.buildSessionUri(baseUri, token));
    }
}

