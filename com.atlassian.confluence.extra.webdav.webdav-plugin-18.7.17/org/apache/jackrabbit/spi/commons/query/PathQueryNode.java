/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.Collection;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.NodeTypeQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class PathQueryNode
extends NAryQueryNode<LocationStepQueryNode> {
    private boolean absolute = false;
    private final Collection<Name> validJcrSystemNodeTypeNames;
    private static final LocationStepQueryNode[] EMPTY = new LocationStepQueryNode[0];

    protected PathQueryNode(QueryNode parent, Collection<Name> validJcrSystemNodeTypeNames) {
        super(parent);
        this.validJcrSystemNodeTypeNames = validJcrSystemNodeTypeNames;
    }

    public Collection<Name> getValidJcrSystemNodeTypeNames() {
        return this.validJcrSystemNodeTypeNames;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 11;
    }

    public void addPathStep(LocationStepQueryNode step) {
        this.addOperand(step);
    }

    public LocationStepQueryNode[] getPathSteps() {
        if (this.operands == null) {
            return EMPTY;
        }
        return this.operands.toArray(new LocationStepQueryNode[this.operands.size()]);
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PathQueryNode) {
            PathQueryNode other = (PathQueryNode)obj;
            return super.equals(obj) && this.absolute == other.absolute;
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        LocationStepQueryNode[] pathSteps = this.getPathSteps();
        if (pathSteps == null || pathSteps.length == 0) {
            return true;
        }
        Name firstPathStepName = pathSteps[0].getNameTest();
        if (firstPathStepName == null) {
            NodeTypeQueryNode nodeTypeQueryNode;
            QueryNode[] pathStepOperands = pathSteps[0].getOperands();
            return pathStepOperands.length <= 0 || !(pathStepOperands[0] instanceof NodeTypeQueryNode) || this.validJcrSystemNodeTypeNames.contains((nodeTypeQueryNode = (NodeTypeQueryNode)pathStepOperands[0]).getValue());
        }
        LocationStepQueryNode firstWorkspaceRelativeStep = pathSteps[0];
        if (firstPathStepName.equals(NameConstants.ROOT) && pathSteps.length > 1) {
            firstWorkspaceRelativeStep = pathSteps[1];
        }
        if (firstWorkspaceRelativeStep.getIncludeDescendants()) {
            return true;
        }
        Name firstWorkspaceRelativeName = firstWorkspaceRelativeStep.getNameTest();
        if (firstWorkspaceRelativeName == null || firstWorkspaceRelativeName.equals(NameConstants.JCR_SYSTEM)) {
            return true;
        }
        return super.needsSystemTree();
    }
}

