/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlValue
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import javax.xml.bind.annotation.XmlValue;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonValue;

public enum ResourceErrorType {
    UNKNOWN(Component.UNKNOWN),
    RENDERING_CONTENT(Component.RENDERING, Component.CONTENT),
    RENDERING_MACRO(Component.RENDERING, Component.MACRO),
    PARAMETER_MISSING(Component.PARAMETER, Component.MISSING),
    PARAMETER_TOO_MANY(Component.PARAMETER, Component.TOO_MANY),
    PARAMETER_INVALID(Component.PARAMETER, Component.INVALID),
    PERMISSION_ANONYMOUS_CREATE(Component.PERMISSION, Component.ANONYMOUS, Component.CREATE),
    PERMISSION_ANONYMOUS_CREATE_PAGE(Component.PERMISSION, Component.ANONYMOUS, Component.CREATE, Component.PAGE),
    PERMISSION_ANONYMOUS_CREATE_BLOGPOST(Component.PERMISSION, Component.ANONYMOUS, Component.CREATE, Component.BLOGPOST),
    PERMISSION_ANONYMOUS_CREATE_SPACE(Component.PERMISSION, Component.ANONYMOUS, Component.CREATE, Component.SPACE),
    PERMISSION_ANONYMOUS_CREATE_PERSONAL_SPACE(Component.PERMISSION, Component.ANONYMOUS, Component.CREATE, Component.PERSONAL_SPACE),
    PERMISSION_UNKNOWN_USER_CREATE_PAGE(Component.PERMISSION, Component.UNKNOWN_USER, Component.CREATE, Component.PAGE),
    PERMISSION_UNKNOWN_USER_CREATE_SPACE(Component.PERMISSION, Component.UNKNOWN_USER, Component.CREATE, Component.SPACE),
    PERMISSION_UNKNOWN_USER_CREATE_PERSONAL_SPACE(Component.PERMISSION, Component.UNKNOWN_USER, Component.CREATE, Component.PERSONAL_SPACE),
    PERMISSION_USER_VIEW_PAGE(Component.PERMISSION, Component.USER, Component.VIEW, Component.PAGE),
    PERMISSION_USER_CREATE(Component.PERMISSION, Component.USER, Component.CREATE),
    PERMISSION_USER_CREATE_PAGE(Component.PERMISSION, Component.USER, Component.CREATE, Component.PAGE),
    PERMISSION_USER_CREATE_BLOGPOST(Component.PERMISSION, Component.USER, Component.CREATE, Component.BLOGPOST),
    PERMISSION_USER_CREATE_SPACE(Component.PERMISSION, Component.USER, Component.CREATE, Component.SPACE),
    PERMISSION_USER_CREATE_PERSONAL_SPACE(Component.PERMISSION, Component.USER, Component.CREATE, Component.PERSONAL_SPACE),
    PERMISSION_USER_ADMIN_SPACE(Component.PERMISSION, Component.USER, Component.ADMIN, Component.SPACE),
    PERMISSION_USER_ADMIN(Component.PERMISSION, Component.USER, Component.ADMIN),
    NOT_FOUND_PAGE_TEMPLATE(Component.NOT_FOUND, Component.PAGE_TEMPLATE),
    NOT_FOUND_CONTENT_TEMPLATE(Component.NOT_FOUND, Component.CONTENT_TEMPLATE),
    NOT_FOUND_CONTENT_TEMPLATE_REF(Component.NOT_FOUND, Component.CONTENT_TEMPLATE_REF),
    NOT_FOUND_SPACE(Component.NOT_FOUND, Component.SPACE),
    NOT_FOUND_BLUEPRINT(Component.NOT_FOUND, Component.BLUEPRINT),
    DUPLICATED_TITLE(Component.DUPLICATED, Component.TITLE),
    DUPLICATED_SPACE(Component.DUPLICATED, Component.SPACE),
    DUPLICATED_PERSONAL_SPACE(Component.DUPLICATED, Component.PERSONAL_SPACE),
    DUPLICATED_TITLE_INDEX(Component.DUPLICATED, Component.TITLE_INDEX),
    INVALID_SPACE_KEY(Component.INVALID, Component.SPACE_KEY),
    INVALID_BLUEPRINT(Component.INVALID, Component.BLUEPRINT),
    INVALID_MODULE(Component.INVALID, Component.MODULE),
    INVALID_ENTITY(Component.INVALID, Component.ENTITY),
    INVALID_INDEX_PAGE(Component.INVALID, Component.INDEX, Component.PAGE);

    private static final char SEPARATOR = ':';
    private final String value;

    private ResourceErrorType(Component ... values) {
        this.value = StringUtils.join((Object[])values, (char)':');
    }

    @JsonValue
    @XmlValue
    public String getValue() {
        return this.value;
    }

    private static enum Component {
        UNKNOWN,
        PARAMETER,
        PERMISSION,
        NOT_FOUND,
        DUPLICATED,
        INVALID,
        RENDERING,
        MISSING,
        TOO_MANY,
        ANONYMOUS,
        UNKNOWN_USER,
        USER,
        CREATE,
        VIEW,
        UPDATE,
        DELETE,
        ADMIN,
        ID,
        CREATE_RESULT,
        INDEX,
        MACRO,
        CONTENT,
        PAGE,
        BLOGPOST,
        BLUEPRINT,
        SPACE,
        PERSONAL_SPACE,
        PAGE_TEMPLATE,
        CONTENT_TEMPLATE,
        CONTENT_TEMPLATE_REF,
        SPACE_KEY,
        MODULE,
        ENTITY,
        TITLE,
        TITLE_INDEX;

    }
}

