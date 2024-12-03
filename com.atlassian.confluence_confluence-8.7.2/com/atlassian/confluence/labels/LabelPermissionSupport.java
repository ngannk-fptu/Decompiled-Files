/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.List;

@Deprecated
public class LabelPermissionSupport {
    private static final LabelPermissionEnforcer labelPermissionEnforcer = (LabelPermissionEnforcer)ContainerManager.getComponent((String)"labelPermissionEnforcer");

    public static boolean isLabelableByUser(Labelable object, PermissionManager permissionManager) {
        return labelPermissionEnforcer.isLabelableByUser(object);
    }

    public static boolean userCanEditLabel(ParsedLabelName ref, Labelable object, PermissionManager permissionManager) {
        return labelPermissionEnforcer.userCanEditLabel(ref, object);
    }

    public static boolean userCanEditLabel(Label label, Labelable object, PermissionManager permissionManager) {
        return labelPermissionEnforcer.userCanEditLabel(label, object);
    }

    public static boolean userCanEditLabelOrIsSpaceAdmin(Label label, SpaceContentEntityObject object, PermissionManager permissionManager) {
        return labelPermissionEnforcer.userCanEditLabelOrIsSpaceAdmin(label, object);
    }

    public static boolean userCanViewObject(Labelable object, PermissionManager permissionManager) {
        return labelPermissionEnforcer.userCanViewObject(object);
    }

    public static List filterVisibleLabels(List labelList, User user, boolean hideSpecialLabels) {
        return labelPermissionEnforcer.filterVisibleLabels(labelList, user, hideSpecialLabels);
    }

    public static List filterLabelsByNamespace(List labelList, User user, Namespace namespace) {
        return labelPermissionEnforcer.filterLabelsByNamespace(labelList, user, namespace);
    }
}

