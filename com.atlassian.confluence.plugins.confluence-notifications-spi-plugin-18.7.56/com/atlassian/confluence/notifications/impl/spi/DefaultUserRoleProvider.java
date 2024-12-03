/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.notifications.spi.UserRolesProvider
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.confluence.notifications.SystemUserRole;
import com.atlassian.confluence.notifications.impl.NotificationDescriptorLocator;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.UserRolesProvider;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class DefaultUserRoleProvider
implements UserRolesProvider {
    private static final Function<AbstractParticipantDescriptor<RecipientsProvider>, Iterable<UserRole>> TO_USER_ROLES = descriptor -> ((RecipientsProvider)descriptor.getModule()).getUserRoles();
    private final NotificationDescriptorLocator descriptorLocator;

    public DefaultUserRoleProvider(NotificationDescriptorLocator descriptorLocator) {
        this.descriptorLocator = descriptorLocator;
    }

    public UserRole getRole(String key) {
        for (UserRole userRole : this.getRoles()) {
            if (!userRole.getID().equals(key)) continue;
            return userRole;
        }
        return null;
    }

    public Iterable<UserRole> getRoles() {
        return ImmutableSet.copyOf(this.getUserRoles());
    }

    private Iterable<UserRole> getUserRoles() {
        Iterable<AbstractParticipantDescriptor<RecipientsProvider>> RecipientsProviders = this.descriptorLocator.findParticipantDescriptors(RecipientsProvider.class);
        Iterable allUserRoles = Iterables.concat((Iterable)Iterables.transform(RecipientsProviders, TO_USER_ROLES));
        return Iterables.filter((Iterable)allUserRoles, (Predicate)Predicates.not((Predicate)Predicates.instanceOf(SystemUserRole.class)));
    }
}

