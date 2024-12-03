/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.Tag
 *  org.springframework.util.Assert
 */
package org.springframework.web.util;

import javax.servlet.jsp.tagext.Tag;
import org.springframework.util.Assert;

public abstract class TagUtils {
    public static final String SCOPE_PAGE = "page";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";

    public static int getScope(String scope) {
        Assert.notNull((Object)scope, (String)"Scope to search for cannot be null");
        if (scope.equals(SCOPE_REQUEST)) {
            return 2;
        }
        if (scope.equals(SCOPE_SESSION)) {
            return 3;
        }
        if (scope.equals(SCOPE_APPLICATION)) {
            return 4;
        }
        return 1;
    }

    public static boolean hasAncestorOfType(Tag tag, Class<?> ancestorTagClass) {
        Assert.notNull((Object)tag, (String)"Tag cannot be null");
        Assert.notNull(ancestorTagClass, (String)"Ancestor tag class cannot be null");
        if (!Tag.class.isAssignableFrom(ancestorTagClass)) {
            throw new IllegalArgumentException("Class '" + ancestorTagClass.getName() + "' is not a valid Tag type");
        }
        for (Tag ancestor = tag.getParent(); ancestor != null; ancestor = ancestor.getParent()) {
            if (!ancestorTagClass.isAssignableFrom(ancestor.getClass())) continue;
            return true;
        }
        return false;
    }

    public static void assertHasAncestorOfType(Tag tag, Class<?> ancestorTagClass, String tagName, String ancestorTagName) {
        Assert.hasText((String)tagName, (String)"'tagName' must not be empty");
        Assert.hasText((String)ancestorTagName, (String)"'ancestorTagName' must not be empty");
        if (!TagUtils.hasAncestorOfType(tag, ancestorTagClass)) {
            throw new IllegalStateException("The '" + tagName + "' tag can only be used inside a valid '" + ancestorTagName + "' tag.");
        }
    }
}

