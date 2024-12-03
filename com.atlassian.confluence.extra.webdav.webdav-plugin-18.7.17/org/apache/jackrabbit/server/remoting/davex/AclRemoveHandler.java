/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.remoting.davex;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import org.apache.jackrabbit.server.remoting.davex.ProtectedItemRemoveHandler;
import org.apache.jackrabbit.util.Text;

public class AclRemoveHandler
implements ProtectedItemRemoveHandler {
    private static final String NT_REP_ACL = "rep:ACL";

    @Override
    public boolean remove(Session session, String itemPath) throws RepositoryException {
        if (this.canHandle(session, itemPath)) {
            AccessControlPolicy[] policies;
            String controlledPath = Text.getRelativeParent(itemPath, 1);
            AccessControlManager acMgr = session.getAccessControlManager();
            for (AccessControlPolicy policy : policies = acMgr.getPolicies(controlledPath)) {
                acMgr.removePolicy(controlledPath, policy);
            }
            return true;
        }
        return false;
    }

    private boolean canHandle(Session session, String itemPath) throws RepositoryException {
        Item aclItem = session.getItem(itemPath);
        return aclItem.isNode() && itemPath.startsWith("/") && this.isJackrabbitAclNodeType((Node)aclItem);
    }

    private boolean isJackrabbitAclNodeType(Node aclNode) throws RepositoryException {
        String ntName = aclNode.getPrimaryNodeType().getName();
        return ntName.equals(NT_REP_ACL);
    }
}

