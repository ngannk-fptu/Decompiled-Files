/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.sal.api.user.UserKey;
import java.text.ParseException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class PermissionsFormHandler {
    public static final String FORM_PARAMETER_PREFIX = "confluence";
    public static final String FORM_PARAMETER_SEPARATOR = "_";
    public static final String FULL_FORM_PARAMETER_PREFIX = "confluence_";
    private final ConfluenceUserResolver userResolver;

    public PermissionsFormHandler(ConfluenceUserResolver userResolver) {
        this.userResolver = userResolver;
    }

    public SpacePermission fromFormParameterName(String formParameterName, Space space, String parameterType) throws ParseException {
        String[] parameterComponents = PermissionsFormHandler.parseFormParameter(formParameterName, parameterType);
        String permissionType = PermissionsFormHandler.extractPermissionType(formParameterName, parameterComponents[2]);
        String entityType = parameterComponents[3];
        if ("anonymous".equals(entityType)) {
            return SpacePermission.createAnonymousSpacePermission(permissionType, space);
        }
        if ("authenticatedusers".equals(entityType)) {
            return SpacePermission.createAuthenticatedUsersSpacePermission(permissionType, space);
        }
        String entityName = PermissionsFormHandler.getEntityName(formParameterName, parameterComponents);
        if ("user".equals(entityType)) {
            return SpacePermission.createUserSpacePermission(permissionType, space, this.lookupUser(formParameterName, entityName));
        }
        if ("group".equals(entityType)) {
            return SpacePermission.createGroupSpacePermission(permissionType, space, entityName);
        }
        throw new ParseException("Unrecognised entity type: " + entityType + "in form parameter: " + formParameterName, 0);
    }

    private ConfluenceUser lookupUser(String formParameterName, String entityName) throws ParseException {
        ConfluenceUser userByKey = this.userResolver.getUserByKey(new UserKey(entityName));
        if (userByKey != null) {
            return userByKey;
        }
        ConfluenceUser userByName = this.userResolver.getUserByName(entityName);
        if (userByName != null) {
            return userByName;
        }
        throw new ParseException("Unrecognised user '" + entityName + "' in form parameter: " + formParameterName, 0);
    }

    private static String extractPermissionType(String formParameterName, String parameterComponent) throws ParseException {
        String permissionType = parameterComponent.toUpperCase(Locale.ENGLISH);
        if (!SpacePermission.PERMISSION_TYPES.contains(permissionType)) {
            throw new ParseException("Unrecognised permission type: " + permissionType + " in parameter " + formParameterName, 0);
        }
        return permissionType;
    }

    private static String[] parseFormParameter(String formParameterName, String parameterType) throws ParseException {
        String[] splitUpCheckboxName = formParameterName.split(FORM_PARAMETER_SEPARATOR, 5);
        if (splitUpCheckboxName.length < 4) {
            throw new ParseException("Insufficient information in form parameter name: " + formParameterName, 0);
        }
        if (!FORM_PARAMETER_PREFIX.equals(splitUpCheckboxName[0])) {
            throw new ParseException("Wrong prefix in form parameter name: " + formParameterName, 0);
        }
        if (!parameterType.equals(splitUpCheckboxName[1])) {
            throw new ParseException("Wrong parameterType in form parameter name: " + formParameterName, 0);
        }
        return splitUpCheckboxName;
    }

    private static String getEntityName(String formParameterName, String[] splitUpCheckboxName) throws ParseException {
        try {
            String entityName = splitUpCheckboxName[4];
            if (StringUtils.isBlank((CharSequence)entityName)) {
                throw new ParseException("No user or group name in parameter " + formParameterName, 0);
            }
            return entityName;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Insufficient information in form parameter name: " + formParameterName, 0);
        }
    }
}

