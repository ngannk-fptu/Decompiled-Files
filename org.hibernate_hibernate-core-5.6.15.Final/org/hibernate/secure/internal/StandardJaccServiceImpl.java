/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.security.jacc.EJBMethodPermission
 *  javax.security.jacc.PolicyConfiguration
 *  javax.security.jacc.PolicyConfigurationFactory
 *  javax.security.jacc.PolicyContext
 *  javax.security.jacc.PolicyContextException
 *  org.jboss.logging.Logger
 */
package org.hibernate.secure.internal;

import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.jacc.EJBMethodPermission;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import org.hibernate.HibernateException;
import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.IntegrationException;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.secure.spi.PermissibleAction;
import org.hibernate.secure.spi.PermissionCheckEntityInformation;
import org.hibernate.service.spi.Configurable;
import org.jboss.logging.Logger;

@Deprecated
public class StandardJaccServiceImpl
implements JaccService,
Configurable {
    private static final Logger log = Logger.getLogger(StandardJaccServiceImpl.class);
    private String contextId;
    private PolicyConfiguration policyConfiguration;

    @Override
    public void configure(Map configurationValues) {
        this.contextId = (String)configurationValues.get("hibernate.jacc_context_id");
    }

    @Override
    public String getContextId() {
        return this.contextId;
    }

    @Override
    public void addPermission(GrantedPermission permissionDeclaration) {
        if (this.policyConfiguration == null) {
            this.policyConfiguration = this.locatePolicyConfiguration(this.contextId);
        }
        for (String grantedAction : permissionDeclaration.getPermissibleAction().getImpliedActions()) {
            EJBMethodPermission permission = new EJBMethodPermission(permissionDeclaration.getEntityName(), grantedAction, null, null);
            log.debugf("Adding permission [%s] to role [%s]", (Object)grantedAction, (Object)permissionDeclaration.getRole());
            try {
                this.policyConfiguration.addToRole(permissionDeclaration.getRole(), (Permission)permission);
            }
            catch (PolicyContextException pce) {
                throw new HibernateException("policy context exception occurred", pce);
            }
        }
    }

    private PolicyConfiguration locatePolicyConfiguration(String contextId) {
        try {
            return PolicyConfigurationFactory.getPolicyConfigurationFactory().getPolicyConfiguration(contextId, false);
        }
        catch (Exception e) {
            throw new IntegrationException("Unable to access JACC PolicyConfiguration");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void checkPermission(PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
        if (action == PermissibleAction.ANY) {
            throw new HibernateException("ANY action (*) is not legal for permission check, only for configuration");
        }
        String originalContextId = AccessController.doPrivileged(new ContextIdSetAction(this.contextId));
        try {
            this.doPermissionCheckInContext(entityInformation, action);
        }
        finally {
            AccessController.doPrivileged(new ContextIdSetAction(originalContextId));
        }
    }

    private void doPermissionCheckInContext(PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
        EJBMethodPermission jaccPermission;
        Policy policy = Policy.getPolicy();
        Principal[] principals = this.getCallerPrincipals();
        CodeSource codeSource = entityInformation.getEntity().getClass().getProtectionDomain().getCodeSource();
        ProtectionDomain pd = new ProtectionDomain(codeSource, null, null, principals);
        if (!policy.implies(pd, (Permission)(jaccPermission = new EJBMethodPermission(entityInformation.getEntityName(), action.getImpliedActions()[0], null, null)))) {
            throw new SecurityException(String.format("JACC denied permission to [%s.%s] for [%s]", entityInformation.getEntityName(), action.getImpliedActions()[0], this.join(principals)));
        }
    }

    private String join(Principal[] principals) {
        String separator = "";
        StringBuilder buffer = new StringBuilder();
        for (Principal principal : principals) {
            buffer.append(separator).append(principal.getName());
            separator = ", ";
        }
        return buffer.toString();
    }

    protected Principal[] getCallerPrincipals() {
        Subject caller = this.getContextSubjectAccess().getContextSubject();
        if (caller == null) {
            return new Principal[0];
        }
        Set<Principal> principalsSet = caller.getPrincipals();
        return principalsSet.toArray(new Principal[principalsSet.size()]);
    }

    private ContextSubjectAccess getContextSubjectAccess() {
        return System.getSecurityManager() == null ? NonPrivilegedContextSubjectAccess.INSTANCE : PrivilegedContextSubjectAccess.INSTANCE;
    }

    protected static class NonPrivilegedContextSubjectAccess
    implements ContextSubjectAccess {
        public static final NonPrivilegedContextSubjectAccess INSTANCE = new NonPrivilegedContextSubjectAccess();

        protected NonPrivilegedContextSubjectAccess() {
        }

        @Override
        public Subject getContextSubject() {
            try {
                return (Subject)PolicyContext.getContext((String)"javax.security.auth.Subject.container");
            }
            catch (PolicyContextException e) {
                throw new HibernateException("Unable to access JACC PolicyContext in order to locate calling Subject", e);
            }
        }
    }

    protected static class PrivilegedContextSubjectAccess
    implements ContextSubjectAccess {
        public static final PrivilegedContextSubjectAccess INSTANCE = new PrivilegedContextSubjectAccess();
        private final PrivilegedAction<Subject> privilegedAction = new PrivilegedAction<Subject>(){

            @Override
            public Subject run() {
                return NonPrivilegedContextSubjectAccess.INSTANCE.getContextSubject();
            }
        };

        protected PrivilegedContextSubjectAccess() {
        }

        @Override
        public Subject getContextSubject() {
            return AccessController.doPrivileged(this.privilegedAction);
        }
    }

    protected static interface ContextSubjectAccess {
        public static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";

        public Subject getContextSubject();
    }

    private static class ContextIdSetAction
    implements PrivilegedAction<String> {
        private final String contextId;

        private ContextIdSetAction(String contextId) {
            this.contextId = contextId;
        }

        @Override
        public String run() {
            String previousID = PolicyContext.getContextID();
            PolicyContext.setContextID((String)this.contextId);
            return previousID;
        }
    }
}

