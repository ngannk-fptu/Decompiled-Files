/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.xpath;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.DefaultQueryNodeVisitor;
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
import org.apache.jackrabbit.spi.commons.query.xpath.XPathQueryBuilder;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.util.ISO9075;

class QueryFormat
implements QueryNodeVisitor,
QueryConstants {
    private final NameResolver resolver;
    private final String statement;
    private final List exceptions = new ArrayList();

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
        node.getLocationNode().accept(this, data);
        Name[] selectProps = node.getSelectProperties();
        if (selectProps.length > 0) {
            boolean union;
            sb.append('/');
            boolean bl = union = selectProps.length > 1;
            if (union) {
                sb.append('(');
            }
            String pipe = "";
            for (int i = 0; i < selectProps.length; ++i) {
                try {
                    sb.append(pipe);
                    sb.append('@');
                    sb.append(this.resolver.getJCRName(QueryFormat.encode(selectProps[i])));
                    pipe = "|";
                    continue;
                }
                catch (NamespaceException e) {
                    this.exceptions.add(e);
                }
            }
            if (union) {
                sb.append(')');
            }
        }
        if (node.getOrderNode() != null) {
            node.getOrderNode().accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(OrQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        boolean bracket = false;
        if (node.getParent() instanceof AndQueryNode) {
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
            or = " or ";
        }
        if (bracket) {
            sb.append(")");
        }
        return sb;
    }

    @Override
    public Object visit(AndQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        String and = "";
        QueryNode[] operands = node.getOperands();
        for (int i = 0; i < operands.length; ++i) {
            sb.append(and);
            operands[i].accept(this, sb);
            and = " and ";
        }
        return sb;
    }

    @Override
    public Object visit(NotQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        QueryNode[] operands = node.getOperands();
        if (operands.length > 0) {
            try {
                sb.append(this.resolver.getJCRName(XPathQueryBuilder.FN_NOT_10));
                sb.append("(");
                operands[0].accept(this, sb);
                sb.append(")");
            }
            catch (NamespaceException e) {
                this.exceptions.add(e);
            }
        }
        return sb;
    }

    @Override
    public Object visit(ExactQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        sb.append("@");
        try {
            Name name = QueryFormat.encode(node.getPropertyName());
            sb.append(this.resolver.getJCRName(name));
            sb.append("='");
            sb.append(this.resolver.getJCRName(node.getValue()));
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        sb.append("'");
        return sb;
    }

    @Override
    public Object visit(NodeTypeQueryNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(TextsearchQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        try {
            sb.append(this.resolver.getJCRName(XPathQueryBuilder.JCR_CONTAINS));
            sb.append("(");
            Path relPath = node.getRelativePath();
            if (relPath == null) {
                sb.append(".");
            } else {
                Path.Element[] elements = relPath.getElements();
                String slash = "";
                for (int i = 0; i < elements.length; ++i) {
                    sb.append(slash);
                    slash = "/";
                    if (node.getReferencesProperty() && i == elements.length - 1) {
                        sb.append("@");
                    }
                    if (elements[i].getName().equals(RelationQueryNode.STAR_NAME_TEST)) {
                        sb.append("*");
                    } else {
                        Name n = QueryFormat.encode(elements[i].getName());
                        sb.append(this.resolver.getJCRName(n));
                    }
                    if (elements[i].getIndex() == 0) continue;
                    sb.append("[").append(elements[i].getIndex()).append("]");
                }
            }
            sb.append(", '");
            sb.append(node.getQuery().replaceAll("'", "''"));
            sb.append("')");
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        return sb;
    }

    @Override
    public Object visit(PathQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        if (node.isAbsolute()) {
            sb.append("/");
        }
        LocationStepQueryNode[] steps = node.getPathSteps();
        String slash = "";
        for (int i = 0; i < steps.length; ++i) {
            sb.append(slash);
            steps[i].accept(this, sb);
            slash = "/";
        }
        return sb;
    }

    @Override
    public Object visit(LocationStepQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        if (node.getIncludeDescendants()) {
            sb.append('/');
        }
        final Name[] nodeType = new Name[1];
        node.acceptOperands(new DefaultQueryNodeVisitor(){

            @Override
            public Object visit(NodeTypeQueryNode node, Object data) {
                nodeType[0] = node.getValue();
                return data;
            }
        }, null);
        if (nodeType[0] != null) {
            sb.append("element(");
        }
        if (node.getNameTest() == null) {
            sb.append("*");
        } else {
            try {
                if (node.getNameTest().getLocalName().length() == 0) {
                    sb.append(this.resolver.getJCRName(XPathQueryBuilder.JCR_ROOT));
                } else {
                    sb.append(this.resolver.getJCRName(QueryFormat.encode(node.getNameTest())));
                }
            }
            catch (NamespaceException e) {
                this.exceptions.add(e);
            }
        }
        if (nodeType[0] != null) {
            sb.append(", ");
            try {
                sb.append(this.resolver.getJCRName(QueryFormat.encode(nodeType[0])));
            }
            catch (NamespaceException e) {
                this.exceptions.add(e);
            }
            sb.append(")");
        }
        if (node.getIndex() != -2147483647) {
            sb.append('[').append(node.getIndex()).append(']');
        }
        QueryNode[] predicates = node.getPredicates();
        for (int i = 0; i < predicates.length; ++i) {
            if (predicates[i].getType() == 6) continue;
            sb.append('[');
            predicates[i].accept(this, sb);
            sb.append(']');
        }
        return sb;
    }

    @Override
    public Object visit(DerefQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        try {
            sb.append(this.resolver.getJCRName(XPathQueryBuilder.JCR_DEREF));
            sb.append("(@");
            sb.append(this.resolver.getJCRName(QueryFormat.encode(node.getRefProperty())));
            sb.append(", '");
            if (node.getNameTest() == null) {
                sb.append("*");
            } else {
                sb.append(this.resolver.getJCRName(QueryFormat.encode(node.getNameTest())));
            }
            sb.append("')");
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        return sb;
    }

    @Override
    public Object visit(RelationQueryNode node, Object data) throws RepositoryException {
        StringBuffer sb = (StringBuffer)data;
        try {
            StringBuffer propPath = new StringBuffer();
            PathQueryNode relPath = node.getRelativePath();
            if (relPath == null) {
                propPath.append(".");
            } else if (relPath.getNumOperands() > 0 && XPathQueryBuilder.FN_POSITION_FULL.equals(relPath.getPathSteps()[0].getNameTest())) {
                propPath.append(this.resolver.getJCRName(XPathQueryBuilder.FN_POSITION_FULL));
            } else {
                LocationStepQueryNode[] steps = relPath.getPathSteps();
                String slash = "";
                for (int i = 0; i < steps.length; ++i) {
                    propPath.append(slash);
                    slash = "/";
                    if (i == steps.length - 1 && node.getOperation() != 28) {
                        propPath.append("@");
                    }
                    this.visit(steps[i], (Object)propPath);
                }
            }
            node.acceptOperands(this, propPath);
            if (node.getOperation() == 11) {
                sb.append(propPath).append(" eq ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 12) {
                sb.append(propPath).append(" = ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 20) {
                sb.append(propPath).append(" >= ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 19) {
                sb.append(propPath).append(" ge ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 18) {
                sb.append(propPath).append(" > ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 17) {
                sb.append(propPath).append(" gt ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 22) {
                sb.append(propPath).append(" <= ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 21) {
                sb.append(propPath).append(" le ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 23) {
                sb.append(this.resolver.getJCRName(XPathQueryBuilder.JCR_LIKE));
                sb.append("(").append(propPath).append(", ");
                this.appendValue(node, sb);
                sb.append(")");
            } else if (node.getOperation() == 16) {
                sb.append(propPath).append(" < ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 15) {
                sb.append(propPath).append(" lt ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 14) {
                sb.append(propPath).append(" != ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 13) {
                sb.append(propPath).append(" ne ");
                this.appendValue(node, sb);
            } else if (node.getOperation() == 26) {
                sb.append(this.resolver.getJCRName(XPathQueryBuilder.FN_NOT));
                sb.append("(").append(propPath).append(")");
            } else if (node.getOperation() == 27) {
                sb.append(propPath);
            } else if (node.getOperation() == 28) {
                sb.append(this.resolver.getJCRName(XPathQueryBuilder.REP_SIMILAR));
                sb.append("(").append(propPath).append(", ");
                this.appendValue(node, sb);
                sb.append(")");
            } else if (node.getOperation() == 29) {
                sb.append(this.resolver.getJCRName(XPathQueryBuilder.REP_SPELLCHECK));
                sb.append("(");
                this.appendValue(node, sb);
                sb.append(")");
            } else {
                this.exceptions.add(new InvalidQueryException("Invalid operation: " + node.getOperation()));
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
        sb.append(" order by");
        OrderQueryNode.OrderSpec[] specs = node.getOrderSpecs();
        String comma = "";
        try {
            for (int i = 0; i < specs.length; ++i) {
                sb.append(comma);
                Path propPath = specs[i].getPropertyPath();
                Path.Element[] elements = propPath.getElements();
                sb.append(" ");
                String slash = "";
                for (int j = 0; j < elements.length; ++j) {
                    sb.append(slash);
                    slash = "/";
                    Path.Element element = elements[j];
                    Name name = QueryFormat.encode(element.getName());
                    if (j == elements.length - 1) {
                        sb.append("@");
                    }
                    sb.append(this.resolver.getJCRName(name));
                }
                if (!specs[i].isAscending()) {
                    sb.append(" descending");
                }
                comma = ",";
            }
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        return data;
    }

    @Override
    public Object visit(PropertyFunctionQueryNode node, Object data) {
        StringBuffer sb = (StringBuffer)data;
        String functionName = node.getFunctionName();
        try {
            if (functionName.equals("lower-case")) {
                sb.insert(0, this.resolver.getJCRName(XPathQueryBuilder.FN_LOWER_CASE) + "(");
                sb.append(")");
            } else if (functionName.equals("upper-case")) {
                sb.insert(0, this.resolver.getJCRName(XPathQueryBuilder.FN_UPPER_CASE) + "(");
                sb.append(")");
            } else {
                this.exceptions.add(new InvalidQueryException("Unsupported function: " + functionName));
            }
        }
        catch (NamespaceException e) {
            this.exceptions.add(e);
        }
        return sb;
    }

    private void appendValue(RelationQueryNode node, StringBuffer b) throws NamespaceException {
        if (node.getValueType() == 1) {
            b.append(node.getLongValue());
        } else if (node.getValueType() == 2) {
            b.append(node.getDoubleValue());
        } else if (node.getValueType() == 3) {
            b.append("'").append(node.getStringValue().replaceAll("'", "''")).append("'");
        } else if (node.getValueType() == 4 || node.getValueType() == 5) {
            b.append(this.resolver.getJCRName(XPathQueryBuilder.XS_DATETIME));
            b.append("('").append(ISO8601.format(node.getDateValue())).append("')");
        } else if (node.getValueType() == 6) {
            if (node.getPositionValue() == Integer.MIN_VALUE) {
                b.append("last()");
            } else {
                b.append(node.getPositionValue());
            }
        } else {
            this.exceptions.add(new InvalidQueryException("Invalid type: " + node.getValueType()));
        }
    }

    private static Name encode(Name name) {
        String encoded = ISO9075.encode(name.getLocalName());
        if (encoded.equals(name.getLocalName())) {
            return name;
        }
        return NameFactoryImpl.getInstance().create(name.getNamespaceURI(), encoded);
    }
}

