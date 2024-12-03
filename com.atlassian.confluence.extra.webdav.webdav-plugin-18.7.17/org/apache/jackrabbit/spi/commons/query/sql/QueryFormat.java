/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
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
import org.apache.jackrabbit.util.ISO8601;

class QueryFormat
implements QueryNodeVisitor,
QueryConstants {
    private final NameResolver resolver;
    private final String statement;
    private final List exceptions = new ArrayList();
    private final List nodeTypes = new ArrayList();

    private QueryFormat(QueryRootNode root, NameResolver resolver) throws RepositoryException {
        this.resolver = resolver;
        this.statement = root.accept(this, new StringBuffer()).toString();
        if (this.exceptions.size() > 0) {
            Exception e = (Exception)this.exceptions.get(0);
            throw new InvalidQueryException(e.getMessage(), e);
        }
    }

    public static String toString(QueryRootNode root, NameResolver resolver) throws InvalidQueryException {
        try {
            return new QueryFormat(root, resolver).toString();
        }
        catch (RepositoryException e) {
            throw new InvalidQueryException(e);
        }
    }

    public String toString() {
        return this.statement;
    }

    @Override
    public Object visit(QueryRootNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        try {
            sb.append("SELECT");
            Name[] selectProps = node.getSelectProperties();
            if (selectProps.length == 0) {
                sb.append(" *");
            } else {
                String comma = "";
                for (int i = 0; i < selectProps.length; ++i) {
                    sb.append(comma).append(" ");
                    QueryFormat.appendName(selectProps[i], this.resolver, sb);
                    comma = ",";
                }
            }
            sb.append(" FROM");
            StringBuffer tmp = new StringBuffer();
            LocationStepQueryNode[] steps = node.getLocationNode().getPathSteps();
            QueryNode[] predicates = steps[steps.length - 1].getPredicates();
            for (int i = 0; i < predicates.length; ++i) {
                if (predicates[i].getType() == 6) continue;
                tmp.append(" WHERE ");
            }
            String and = "";
            for (int i = 0; i < predicates.length; ++i) {
                if (predicates[i].getType() != 6) {
                    tmp.append(and);
                    and = " AND ";
                }
                predicates[i].accept(this, tmp);
            }
            String comma = "";
            int ntCount = 0;
            for (Name nt : this.nodeTypes) {
                sb.append(comma).append(" ");
                QueryFormat.appendName(nt, this.resolver, sb);
                comma = ",";
                ++ntCount;
            }
            if (ntCount == 0) {
                sb.append(" ");
                sb.append(this.resolver.getJCRName(NameConstants.NT_BASE));
            }
            sb.append(tmp.toString());
            if (!(steps.length == 2 && steps[1].getIncludeDescendants() && steps[1].getNameTest() == null || steps.length == 1 && steps[0].getIncludeDescendants() && steps[0].getNameTest() == null)) {
                if (predicates.length > 0) {
                    sb.append(" AND ");
                } else {
                    sb.append(" WHERE ");
                }
                node.getLocationNode().accept(this, sb);
            }
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        if (node.getOrderNode() != null) {
            node.getOrderNode().accept(this, sb);
        }
        return sb;
    }

    @Override
    public Object visit(OrQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        boolean bracket = false;
        if (node.getParent() instanceof LocationStepQueryNode || node.getParent() instanceof AndQueryNode || node.getParent() instanceof NotQueryNode) {
            bracket = true;
        }
        if (bracket) {
            sb.append("(");
        }
        String or = "";
        QueryNode[] operands = node.getOperands();
        for (int i = 0; i < operands.length; ++i) {
            sb.append(or);
            operands[i].accept(this, sb);
            or = " OR ";
        }
        if (bracket) {
            sb.append(")");
        }
        return sb;
    }

    @Override
    public Object visit(AndQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        boolean bracket = false;
        if (node.getParent() instanceof NotQueryNode) {
            bracket = true;
        }
        if (bracket) {
            sb.append("(");
        }
        String and = "";
        QueryNode[] operands = node.getOperands();
        for (int i = 0; i < operands.length; ++i) {
            sb.append(and);
            int len = sb.length();
            operands[i].accept(this, sb);
            and = sb.length() - len > 0 ? " AND " : "";
        }
        if (bracket) {
            sb.append(")");
        }
        return sb;
    }

    @Override
    public Object visit(NotQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        QueryNode[] operands = node.getOperands();
        if (operands.length > 0) {
            sb.append("NOT ");
            operands[0].accept(this, sb);
        }
        return sb;
    }

    @Override
    public Object visit(ExactQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        try {
            QueryFormat.appendName(node.getPropertyName(), this.resolver, sb);
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        sb.append("='").append(node.getValue()).append("'");
        return sb;
    }

    @Override
    public Object visit(NodeTypeQueryNode node, Object data) {
        this.nodeTypes.add(node.getValue());
        return data;
    }

    @Override
    public Object visit(TextsearchQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        String query = node.getQuery().replaceAll("'", "''");
        sb.append("CONTAINS(");
        if (node.getRelativePath() == null) {
            sb.append("*");
        } else if (node.getRelativePath().getLength() > 1 || !node.getReferencesProperty()) {
            this.exceptions.add(new InvalidQueryException("Child axis not supported in SQL"));
        } else {
            try {
                QueryFormat.appendName(node.getRelativePath().getName(), this.resolver, sb);
            }
            catch (NamespaceException e) {
                this.exceptions.add(e);
            }
        }
        sb.append(", '");
        sb.append(query).append("')");
        return sb;
    }

    @Override
    public Object visit(PathQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        try {
            if (QueryFormat.containsDescendantOrSelf(node)) {
                int i;
                sb.append("(");
                sb.append(this.resolver.getJCRName(NameConstants.JCR_PATH));
                sb.append(" LIKE '");
                LocationStepQueryNode[] steps = node.getPathSteps();
                for (i = 0; i < steps.length; ++i) {
                    if (steps[i].getNameTest() == null || steps[i].getNameTest().getLocalName().length() > 0) {
                        sb.append('/');
                    }
                    if (steps[i].getIncludeDescendants()) {
                        sb.append("%/");
                    }
                    steps[i].accept(this, sb);
                }
                sb.append('\'');
                sb.append(" OR ");
                sb.append(this.resolver.getJCRName(NameConstants.JCR_PATH));
                sb.append(" LIKE '");
                for (i = 0; i < steps.length; ++i) {
                    if (steps[i].getNameTest() == null || steps[i].getNameTest().getLocalName().length() > 0) {
                        sb.append('/');
                    }
                    if (steps[i].getNameTest() == null) continue;
                    steps[i].accept(this, sb);
                }
                sb.append("')");
            } else if (QueryFormat.containsAllChildrenMatch(node)) {
                sb.append(this.resolver.getJCRName(NameConstants.JCR_PATH));
                sb.append(" LIKE '");
                StringBuffer path = new StringBuffer();
                LocationStepQueryNode[] steps = node.getPathSteps();
                for (int i = 0; i < steps.length; ++i) {
                    if (steps[i].getNameTest() == null || steps[i].getNameTest().getLocalName().length() > 0) {
                        path.append('/');
                    }
                    steps[i].accept(this, path);
                }
                sb.append(path);
                sb.append('\'');
                sb.append(" AND NOT ");
                sb.append(this.resolver.getJCRName(NameConstants.JCR_PATH));
                sb.append(" LIKE '");
                sb.append(path).append("/%").append('\'');
            } else {
                sb.append(this.resolver.getJCRName(NameConstants.JCR_PATH));
                sb.append(" LIKE '");
                LocationStepQueryNode[] steps = node.getPathSteps();
                for (int i = 0; i < steps.length; ++i) {
                    if (steps[i].getNameTest() == null || steps[i].getNameTest().getLocalName().length() > 0) {
                        sb.append('/');
                    }
                    steps[i].accept(this, sb);
                }
                sb.append('\'');
            }
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        return sb;
    }

    @Override
    public Object visit(LocationStepQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        if (node.getNameTest() == null) {
            sb.append("%");
        } else if (node.getNameTest().getLocalName().length() > 0) {
            try {
                sb.append(this.resolver.getJCRName(node.getNameTest()));
            }
            catch (NamespaceException e) {
                this.exceptions.add(e);
            }
            if (node.getIndex() == -2147483647) {
                sb.append("[%]");
            } else if (node.getIndex() != 1) {
                sb.append('[').append(node.getIndex()).append(']');
            }
        }
        return sb;
    }

    @Override
    public Object visit(DerefQueryNode node, Object data) {
        this.exceptions.add(new InvalidQueryException("jcr:deref() function not supported in SQL"));
        return data;
    }

    @Override
    public Object visit(RelationQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        try {
            StringBuffer propName = new StringBuffer();
            PathQueryNode relPath = node.getRelativePath();
            if (relPath == null) {
                propName.append(".");
            } else {
                if (relPath.getPathSteps().length > 1) {
                    this.exceptions.add(new InvalidQueryException("Child axis not supported in SQL"));
                    return data;
                }
                this.visit(relPath, data);
            }
            node.acceptOperands(this, propName);
            if (node.getOperation() == 11 || node.getOperation() == 12) {
                sb.append(propName);
                sb.append(" = ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 19 || node.getOperation() == 20) {
                sb.append(propName);
                sb.append(" >= ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 17 || node.getOperation() == 18) {
                sb.append(propName);
                sb.append(" > ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 21 || node.getOperation() == 22) {
                sb.append(propName);
                sb.append(" <= ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 23) {
                sb.append(propName);
                sb.append(" LIKE ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 15 || node.getOperation() == 16) {
                sb.append(propName);
                sb.append(" < ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 13 || node.getOperation() == 14) {
                sb.append(propName);
                sb.append(" <> ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 26) {
                sb.append(propName);
                sb.append(" IS NULL");
            } else if (node.getOperation() == 27) {
                sb.append(propName);
                sb.append(" IS NOT NULL");
            } else if (node.getOperation() == 28) {
                sb.append("SIMILAR(");
                sb.append(propName);
                sb.append(", ");
                this.appendValue(node, sb);
                sb.append(")");
            } else if (node.getOperation() == 29) {
                sb.append("SPELLCHECK(");
                this.appendValue(node, sb);
                sb.append(")");
            } else {
                this.exceptions.add(new InvalidQueryException("Invalid operation: " + node.getOperation()));
            }
            if (node.getOperation() == 23 && node.getStringValue().indexOf(92) > -1) {
                sb.append(" ESCAPE '\\'");
            }
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        return sb;
    }

    @Override
    public Object visit(OrderQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        sb.append(" ORDER BY");
        OrderQueryNode.OrderSpec[] specs = node.getOrderSpecs();
        if (specs.length > 0) {
            try {
                String comma = "";
                for (int i = 0; i < specs.length; ++i) {
                    sb.append(comma).append(" ");
                    Path propPath = specs[i].getPropertyPath();
                    if (propPath.getLength() > 1) {
                        this.exceptions.add(new InvalidQueryException("SQL does not support relative paths in order by clause"));
                        return sb;
                    }
                    QueryFormat.appendName(propPath.getName(), this.resolver, sb);
                    if (!specs[i].isAscending()) {
                        sb.append(" DESC");
                    }
                    comma = ",";
                }
            }
            catch (NamespaceException e) {
                this.exceptions.add(e);
            }
        } else {
            sb.append(" SCORE");
        }
        return sb;
    }

    @Override
    public Object visit(PropertyFunctionQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        String functionName = node.getFunctionName();
        if (functionName.equals("lower-case")) {
            sb.insert(0, "LOWER(").append(")");
        } else if (functionName.equals("upper-case")) {
            sb.insert(0, "UPPER(").append(")");
        } else {
            this.exceptions.add(new InvalidQueryException("Unsupported function: " + functionName));
        }
        return sb;
    }

    private static void appendName(Name name, NameResolver resolver, StringBuffer b) throws NamespaceException {
        boolean quote;
        boolean bl = quote = name.getLocalName().indexOf(32) > -1;
        if (quote) {
            b.append('\"');
        }
        b.append(resolver.getJCRName(name));
        if (quote) {
            b.append('\"');
        }
    }

    private void appendValue(RelationQueryNode node, StringBuffer b) {
        if (node.getValueType() == 1) {
            b.append(node.getLongValue());
        } else if (node.getValueType() == 2) {
            b.append(node.getDoubleValue());
        } else if (node.getValueType() == 3) {
            b.append("'").append(node.getStringValue().replaceAll("'", "''")).append("'");
        } else if (node.getValueType() == 4 || node.getValueType() == 5) {
            b.append("TIMESTAMP '").append(ISO8601.format(node.getDateValue())).append("'");
        } else {
            this.exceptions.add(new InvalidQueryException("Invalid type: " + node.getValueType()));
        }
    }

    private static boolean containsDescendantOrSelf(PathQueryNode path) {
        LocationStepQueryNode[] steps = path.getPathSteps();
        int count = 0;
        for (int i = 0; i < steps.length; ++i) {
            if (steps[i].getNameTest() == null || !steps[i].getIncludeDescendants()) continue;
            ++count;
        }
        return count == 1;
    }

    private static boolean containsAllChildrenMatch(PathQueryNode path) {
        LocationStepQueryNode[] steps = path.getPathSteps();
        int count = 0;
        for (int i = 0; i < steps.length; ++i) {
            if (steps[i].getNameTest() != null || steps[i].getIncludeDescendants()) continue;
            if (i == steps.length - 1 && count == 0) {
                return true;
            }
            ++count;
        }
        return false;
    }
}

