/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.Arrays;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.DerefQueryNode;
import org.apache.jackrabbit.spi.commons.query.ExactQueryNode;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NodeTypeQueryNode;
import org.apache.jackrabbit.spi.commons.query.NotQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.PropertyFunctionQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryConstants;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;

public class QueryTreeDump
implements QueryNodeVisitor {
    private int indent;
    private static char[] PADDING = new char[255];
    private static final char PADDING_CHAR = ' ';

    private QueryTreeDump(QueryNode node, StringBuffer buffer) throws RepositoryException {
        node.accept(this, buffer);
    }

    public static void dump(QueryNode node, StringBuffer buffer) throws RepositoryException {
        new QueryTreeDump(node, buffer);
    }

    @Override
    public Object visit(QueryRootNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append("+ Root node");
        buffer.append("\n");
        Name[] select = node.getSelectProperties();
        buffer.append("+ Select properties: ");
        if (select.length == 0) {
            buffer.append("*");
        } else {
            String comma = "";
            for (int i = 0; i < select.length; ++i) {
                buffer.append(comma);
                buffer.append(select[i].toString());
                comma = ", ";
            }
        }
        buffer.append("\n");
        this.traverse(new QueryNode[]{node.getLocationNode()}, buffer);
        OrderQueryNode order = node.getOrderNode();
        if (order != null) {
            this.traverse(new QueryNode[]{order}, buffer);
        }
        return buffer;
    }

    @Override
    public Object visit(OrQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ OrQueryNode");
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(AndQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ AndQueryNode");
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(NotQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ NotQueryNode");
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(ExactQueryNode node, Object data) {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ ExactQueryNode: ");
        buffer.append(" Prop=").append(node.getPropertyName());
        buffer.append(" Value=").append(node.getValue());
        buffer.append("\n");
        return buffer;
    }

    @Override
    public Object visit(NodeTypeQueryNode node, Object data) {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ NodeTypeQueryNode: ");
        buffer.append(" Prop=").append(node.getPropertyName());
        buffer.append(" Value=").append(node.getValue());
        buffer.append("\n");
        return buffer;
    }

    @Override
    public Object visit(TextsearchQueryNode node, Object data) {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ TextsearchQueryNode: ");
        buffer.append(" Path=");
        Path relPath = node.getRelativePath();
        if (relPath == null) {
            buffer.append(".");
        } else {
            Path.Element[] elements = relPath.getElements();
            String slash = "";
            for (int i = 0; i < elements.length; ++i) {
                buffer.append(slash);
                slash = "/";
                if (node.getReferencesProperty() && i == elements.length - 1) {
                    buffer.append("@");
                }
                buffer.append(elements[i]);
            }
        }
        buffer.append(" Query=").append(node.getQuery());
        buffer.append("\n");
        return buffer;
    }

    @Override
    public Object visit(PathQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ PathQueryNode");
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(LocationStepQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ LocationStepQueryNode: ");
        buffer.append(" NodeTest=");
        if (node.getNameTest() == null) {
            buffer.append("*");
        } else {
            buffer.append(node.getNameTest());
        }
        buffer.append(" Descendants=").append(node.getIncludeDescendants());
        buffer.append(" Index=");
        if (node.getIndex() == -2147483647) {
            buffer.append("NONE");
        } else if (node.getIndex() == Integer.MIN_VALUE) {
            buffer.append("last()");
        } else {
            buffer.append(node.getIndex());
        }
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(RelationQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ RelationQueryNode: Op: ");
        buffer.append(QueryConstants.OPERATION_NAMES.getName(node.getOperation()));
        buffer.append(" Prop=[");
        PathQueryNode relPath = node.getRelativePath();
        if (relPath == null) {
            buffer.append(relPath);
        } else {
            this.visit(relPath, (Object)buffer);
        }
        buffer.append("] Type=").append(QueryConstants.TYPE_NAMES.getName(node.getValueType()));
        if (node.getValueType() == 4) {
            buffer.append(" Value=").append(node.getDateValue());
        } else if (node.getValueType() == 2) {
            buffer.append(" Value=").append(node.getDoubleValue());
        } else if (node.getValueType() == 1) {
            buffer.append(" Value=").append(node.getLongValue());
        } else if (node.getValueType() == 6) {
            buffer.append(" Value=").append(node.getPositionValue());
        } else if (node.getValueType() == 3) {
            buffer.append(" Value=").append(node.getStringValue());
        } else if (node.getValueType() == 5) {
            buffer.append(" Value=").append(node.getDateValue());
        }
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(OrderQueryNode node, Object data) {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ OrderQueryNode");
        buffer.append("\n");
        OrderQueryNode.OrderSpec[] specs = node.getOrderSpecs();
        for (int i = 0; i < specs.length; ++i) {
            buffer.append(PADDING, 0, this.indent);
            buffer.append("  ");
            QueryTreeDump.appendPath(specs[i].getPropertyPath(), buffer);
            buffer.append(" asc=").append(specs[i].isAscending());
            buffer.append("\n");
        }
        return buffer;
    }

    @Override
    public Object visit(DerefQueryNode node, Object data) throws RepositoryException {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ DerefQueryNode: ");
        buffer.append(" NodeTest=");
        if (node.getNameTest() == null) {
            buffer.append("*");
        } else {
            buffer.append(node.getNameTest());
        }
        buffer.append(" Descendants=").append(node.getIncludeDescendants());
        buffer.append(" Index=");
        if (node.getIndex() == -2147483647) {
            buffer.append("NONE");
        } else if (node.getIndex() == Integer.MIN_VALUE) {
            buffer.append("last()");
        } else {
            buffer.append(node.getIndex());
        }
        buffer.append("\n");
        this.traverse(node.getOperands(), buffer);
        return buffer;
    }

    @Override
    public Object visit(PropertyFunctionQueryNode node, Object data) {
        StringBuffer buffer = (StringBuffer)data;
        buffer.append(PADDING, 0, this.indent);
        buffer.append("+ PropertyFunctionQueryNode: ");
        buffer.append(node.getFunctionName());
        buffer.append("()\n");
        return buffer;
    }

    private void traverse(QueryNode[] node, StringBuffer buffer) throws RepositoryException {
        this.indent += 2;
        if (this.indent > PADDING.length) {
            char[] tmp = new char[this.indent * 2];
            Arrays.fill(tmp, ' ');
            PADDING = tmp;
        }
        for (int i = 0; i < node.length; ++i) {
            node[i].accept(this, buffer);
        }
        this.indent -= 2;
    }

    private static void appendPath(Path relPath, StringBuffer buffer) {
        Path.Element[] elements = relPath.getElements();
        String slash = "";
        for (int i = 0; i < elements.length; ++i) {
            buffer.append(slash);
            slash = "/";
            buffer.append(elements[i]);
        }
    }

    static {
        Arrays.fill(PADDING, ' ');
    }
}

