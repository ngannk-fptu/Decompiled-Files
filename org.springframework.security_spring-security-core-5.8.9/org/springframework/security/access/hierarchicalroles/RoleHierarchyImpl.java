/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 */
package org.springframework.security.access.hierarchicalroles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.hierarchicalroles.CycleInRoleHierarchyException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class RoleHierarchyImpl
implements RoleHierarchy {
    private static final Log logger = LogFactory.getLog(RoleHierarchyImpl.class);
    private String roleHierarchyStringRepresentation = null;
    private Map<String, Set<GrantedAuthority>> rolesReachableInOneStepMap = null;
    private Map<String, Set<GrantedAuthority>> rolesReachableInOneOrMoreStepsMap = null;

    public void setHierarchy(String roleHierarchyStringRepresentation) {
        this.roleHierarchyStringRepresentation = roleHierarchyStringRepresentation;
        logger.debug((Object)LogMessage.format((String)"setHierarchy() - The following role hierarchy was set: %s", (Object)roleHierarchyStringRepresentation));
        this.buildRolesReachableInOneStepMap();
        this.buildRolesReachableInOneOrMoreStepsMap();
    }

    public Collection<GrantedAuthority> getReachableGrantedAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        HashSet<GrantedAuthority> reachableRoles = new HashSet<GrantedAuthority>();
        HashSet<String> processedNames = new HashSet<String>();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority() == null) {
                reachableRoles.add(grantedAuthority);
                continue;
            }
            if (!processedNames.add(grantedAuthority.getAuthority())) continue;
            reachableRoles.add(grantedAuthority);
            Set<GrantedAuthority> lowerRoles = this.rolesReachableInOneOrMoreStepsMap.get(grantedAuthority.getAuthority());
            if (lowerRoles == null) continue;
            for (GrantedAuthority role : lowerRoles) {
                if (!processedNames.add(role.getAuthority())) continue;
                reachableRoles.add(role);
            }
        }
        logger.debug((Object)LogMessage.format((String)"getReachableGrantedAuthorities() - From the roles %s one can reach %s in zero or more steps.", authorities, reachableRoles));
        return new ArrayList<GrantedAuthority>(reachableRoles);
    }

    private void buildRolesReachableInOneStepMap() {
        this.rolesReachableInOneStepMap = new HashMap<String, Set<GrantedAuthority>>();
        for (String line : this.roleHierarchyStringRepresentation.split("\n")) {
            String[] roles = line.trim().split("\\s+>\\s+");
            for (int i = 1; i < roles.length; ++i) {
                Set<Object> rolesReachableInOneStepSet;
                String higherRole = roles[i - 1];
                SimpleGrantedAuthority lowerRole = new SimpleGrantedAuthority(roles[i]);
                if (!this.rolesReachableInOneStepMap.containsKey(higherRole)) {
                    rolesReachableInOneStepSet = new HashSet();
                    this.rolesReachableInOneStepMap.put(higherRole, rolesReachableInOneStepSet);
                } else {
                    rolesReachableInOneStepSet = this.rolesReachableInOneStepMap.get(higherRole);
                }
                rolesReachableInOneStepSet.add(lowerRole);
                logger.debug((Object)LogMessage.format((String)"buildRolesReachableInOneStepMap() - From role %s one can reach role %s in one step.", (Object)higherRole, (Object)lowerRole));
            }
        }
    }

    private void buildRolesReachableInOneOrMoreStepsMap() {
        this.rolesReachableInOneOrMoreStepsMap = new HashMap<String, Set<GrantedAuthority>>();
        for (String roleName : this.rolesReachableInOneStepMap.keySet()) {
            HashSet rolesToVisitSet = new HashSet(this.rolesReachableInOneStepMap.get(roleName));
            HashSet<GrantedAuthority> visitedRolesSet = new HashSet<GrantedAuthority>();
            while (!rolesToVisitSet.isEmpty()) {
                GrantedAuthority lowerRole = (GrantedAuthority)rolesToVisitSet.iterator().next();
                rolesToVisitSet.remove(lowerRole);
                if (!visitedRolesSet.add(lowerRole) || !this.rolesReachableInOneStepMap.containsKey(lowerRole.getAuthority())) continue;
                if (roleName.equals(lowerRole.getAuthority())) {
                    throw new CycleInRoleHierarchyException();
                }
                rolesToVisitSet.addAll(this.rolesReachableInOneStepMap.get(lowerRole.getAuthority()));
            }
            this.rolesReachableInOneOrMoreStepsMap.put(roleName, visitedRolesSet);
            logger.debug((Object)LogMessage.format((String)"buildRolesReachableInOneOrMoreStepsMap() - From role %s one can reach %s in one or more steps.", (Object)roleName, visitedRolesSet));
        }
    }
}

