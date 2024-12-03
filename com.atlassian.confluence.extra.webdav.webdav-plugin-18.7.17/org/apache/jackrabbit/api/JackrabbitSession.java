/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JackrabbitSession
extends Session {
    public static final String ACTION_ADD_PROPERTY = "add_property";
    public static final String ACTION_MODIFY_PROPERTY = "modify_property";
    public static final String ACTION_REMOVE_PROPERTY = "remove_property";
    public static final String ACTION_REMOVE_NODE = "remove_node";
    public static final String ACTION_NODE_TYPE_MANAGEMENT = "node_type_management";
    public static final String ACTION_VERSIONING = "versioning";
    public static final String ACTION_LOCKING = "locking";
    public static final String ACTION_READ_ACCESS_CONTROL = "read_access_control";
    public static final String ACTION_MODIFY_ACCESS_CONTROL = "modify_access_control";
    public static final String ACTION_USER_MANAGEMENT = "user_management";

    public boolean hasPermission(@NotNull String var1, String ... var2) throws RepositoryException;

    public PrincipalManager getPrincipalManager() throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public UserManager getUserManager() throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException;

    public Item getItemOrNull(String var1) throws RepositoryException;

    public Property getPropertyOrNull(String var1) throws RepositoryException;

    public Node getNodeOrNull(String var1) throws RepositoryException;

    @Nullable
    default public Node getParentOrNull(@NotNull Item item) throws RepositoryException {
        try {
            return item.getParent();
        }
        catch (AccessDeniedException | ItemNotFoundException e) {
            return null;
        }
    }
}

