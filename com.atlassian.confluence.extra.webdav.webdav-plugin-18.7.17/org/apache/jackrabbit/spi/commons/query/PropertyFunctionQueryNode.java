/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class PropertyFunctionQueryNode
extends QueryNode {
    public static final String UPPER_CASE = "upper-case";
    public static final String LOWER_CASE = "lower-case";
    private static final Set SUPPORTED_FUNCTION_NAMES;
    private final String functionName;

    protected PropertyFunctionQueryNode(QueryNode parent, String functionName) throws IllegalArgumentException {
        super(parent);
        if (!SUPPORTED_FUNCTION_NAMES.contains(functionName)) {
            throw new IllegalArgumentException("unknown function name");
        }
        this.functionName = functionName;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PropertyFunctionQueryNode) {
            PropertyFunctionQueryNode other = (PropertyFunctionQueryNode)obj;
            return this.functionName.equals(other.functionName);
        }
        return false;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public boolean needsSystemTree() {
        return false;
    }

    static {
        HashSet<String> tmp = new HashSet<String>();
        tmp.add(UPPER_CASE);
        tmp.add(LOWER_CASE);
        SUPPORTED_FUNCTION_NAMES = Collections.unmodifiableSet(tmp);
    }
}

