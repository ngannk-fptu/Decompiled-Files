/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.authorization;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import org.jetbrains.annotations.NotNull;

public interface PrivilegeCollection {
    public Privilege[] getPrivileges() throws RepositoryException;

    public boolean includes(String ... var1) throws RepositoryException;

    public static class Default
    implements PrivilegeCollection {
        private final Privilege[] privileges;
        private final AccessControlManager accessControlManager;

        public Default(@NotNull Privilege[] privileges, @NotNull AccessControlManager accessControlManager) {
            this.privileges = privileges;
            this.accessControlManager = accessControlManager;
        }

        @Override
        public Privilege[] getPrivileges() {
            return this.privileges;
        }

        @Override
        public boolean includes(String ... privilegeNames) throws RepositoryException {
            if (privilegeNames.length == 0) {
                return true;
            }
            if (this.privileges.length == 0) {
                return false;
            }
            HashSet<Privilege> toTest = new HashSet<Privilege>(privilegeNames.length);
            for (String pName : privilegeNames) {
                toTest.add(this.accessControlManager.privilegeFromName(pName));
            }
            HashSet<Privilege> privilegeSet = new HashSet<Privilege>(Arrays.asList(this.privileges));
            if (privilegeSet.containsAll(toTest)) {
                return true;
            }
            Stream.of(this.privileges).filter(Privilege::isAggregate).forEach(privilege -> Collections.addAll(privilegeSet, privilege.getAggregatePrivileges()));
            return privilegeSet.containsAll(toTest);
        }
    }
}

