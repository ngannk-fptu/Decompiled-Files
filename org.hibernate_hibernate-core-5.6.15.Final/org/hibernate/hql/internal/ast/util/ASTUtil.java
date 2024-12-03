/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.collections.AST
 *  antlr.collections.impl.ASTArray
 */
package org.hibernate.hql.internal.ast.util;

import antlr.ASTFactory;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateError;
import org.hibernate.hql.internal.ast.util.NodeTraverser;

public final class ASTUtil {
    @Deprecated
    private ASTUtil() {
    }

    @Deprecated
    public static AST create(ASTFactory astFactory, int type, String text) {
        return astFactory.create(type, text);
    }

    public static AST createSibling(ASTFactory astFactory, int type, String text, AST prevSibling) {
        AST node = astFactory.create(type, text);
        return ASTUtil.insertSibling(node, prevSibling);
    }

    public static AST insertSibling(AST node, AST prevSibling) {
        node.setNextSibling(prevSibling.getNextSibling());
        prevSibling.setNextSibling(node);
        return node;
    }

    public static AST createBinarySubtree(ASTFactory factory, int parentType, String parentText, AST child1, AST child2) {
        ASTArray array = ASTUtil.createAstArray(factory, 3, parentType, parentText, child1);
        array.add(child2);
        return factory.make(array);
    }

    public static AST createParent(ASTFactory factory, int parentType, String parentText, AST child) {
        ASTArray array = ASTUtil.createAstArray(factory, 2, parentType, parentText, child);
        return factory.make(array);
    }

    public static AST createTree(ASTFactory factory, AST[] nestedChildren) {
        int limit;
        AST[] array = new AST[2];
        for (int i = limit = nestedChildren.length - 1; i >= 0; --i) {
            if (i == limit) continue;
            array[1] = nestedChildren[i + 1];
            array[0] = nestedChildren[i];
            factory.make(array);
        }
        return array[0];
    }

    public static boolean isSubtreeChild(AST fixture, AST test) {
        for (AST n = fixture.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n == test) {
                return true;
            }
            if (n.getFirstChild() == null || !ASTUtil.isSubtreeChild(n, test)) continue;
            return true;
        }
        return false;
    }

    public static AST findTypeInChildren(AST parent, int type) {
        AST n;
        for (n = parent.getFirstChild(); n != null && n.getType() != type; n = n.getNextSibling()) {
        }
        return n;
    }

    public static AST getLastChild(AST n) {
        return ASTUtil.getLastSibling(n.getFirstChild());
    }

    private static AST getLastSibling(AST a) {
        AST last = null;
        while (a != null) {
            last = a;
            a = a.getNextSibling();
        }
        return last;
    }

    public static String getDebugString(AST n) {
        StringBuilder buf = new StringBuilder();
        buf.append("[ ");
        buf.append(n == null ? "{null}" : n.toStringTree());
        buf.append(" ]");
        return buf.toString();
    }

    public static AST findPreviousSibling(AST parent, AST child) {
        AST prev = null;
        for (AST n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n == child) {
                return prev;
            }
            prev = n;
        }
        throw new IllegalArgumentException("Child not found in parent!");
    }

    public static void makeSiblingOfParent(AST parent, AST child) {
        AST prev = ASTUtil.findPreviousSibling(parent, child);
        if (prev != null) {
            prev.setNextSibling(child.getNextSibling());
        } else {
            parent.setFirstChild(child.getNextSibling());
        }
        child.setNextSibling(parent.getNextSibling());
        parent.setNextSibling(child);
    }

    public static String getPathText(AST n) {
        StringBuilder buf = new StringBuilder();
        ASTUtil.getPathText(buf, n);
        return buf.toString();
    }

    private static void getPathText(StringBuilder buf, AST n) {
        AST firstChild = n.getFirstChild();
        if (firstChild != null) {
            ASTUtil.getPathText(buf, firstChild);
        }
        buf.append(n.getText());
        if (firstChild != null && firstChild.getNextSibling() != null) {
            ASTUtil.getPathText(buf, firstChild.getNextSibling());
        }
    }

    public static boolean hasExactlyOneChild(AST n) {
        return n != null && n.getFirstChild() != null && n.getFirstChild().getNextSibling() == null;
    }

    public static void appendSibling(AST n, AST s) {
        while (n.getNextSibling() != null) {
            n = n.getNextSibling();
        }
        n.setNextSibling(s);
    }

    public static void insertChild(AST parent, AST child) {
        if (parent.getFirstChild() == null) {
            parent.setFirstChild(child);
        } else {
            AST n = parent.getFirstChild();
            parent.setFirstChild(child);
            child.setNextSibling(n);
        }
    }

    public static void appendChild(AST parent, AST child) {
        if (parent.getFirstChild() == null) {
            parent.setFirstChild(child);
        } else {
            ASTUtil.getLastChild(parent).setNextSibling(child);
        }
    }

    private static ASTArray createAstArray(ASTFactory factory, int size, int parentType, String parentText, AST child1) {
        ASTArray array = new ASTArray(size);
        array.add(factory.create(parentType, parentText));
        array.add(child1);
        return array;
    }

    public static List collectChildren(AST root, FilterPredicate predicate) {
        return new CollectingNodeVisitor(predicate).collect(root);
    }

    public static String[] generateTokenNameCache(Class tokenTypeInterface) {
        Field[] fields = tokenTypeInterface.getFields();
        String[] names = new String[fields.length + 2];
        for (Field field : fields) {
            String fieldName;
            if (!Modifier.isStatic(field.getModifiers())) continue;
            int idx = 0;
            try {
                idx = field.getInt(null);
            }
            catch (IllegalAccessException e) {
                throw new HibernateError("Initialization error", e);
            }
            names[idx] = fieldName = field.getName();
        }
        return names;
    }

    @Deprecated
    public static String getConstantName(Class owner, int value) {
        return ASTUtil.getTokenTypeName(owner, value);
    }

    public static String getTokenTypeName(Class tokenTypeInterface, int tokenType) {
        String tokenTypeName = Integer.toString(tokenType);
        if (tokenTypeInterface != null) {
            Field[] fields;
            for (Field field : fields = tokenTypeInterface.getFields()) {
                Integer fieldValue = ASTUtil.extractIntegerValue(field);
                if (fieldValue == null || fieldValue != tokenType) continue;
                tokenTypeName = field.getName();
                break;
            }
        }
        return tokenTypeName;
    }

    private static Integer extractIntegerValue(Field field) {
        Integer rtn = null;
        try {
            Object value = field.get(null);
            if (value instanceof Integer) {
                rtn = (Integer)value;
            } else if (value instanceof Short) {
                rtn = ((Short)value).intValue();
            } else if (value instanceof Long && (Long)value <= Integer.MAX_VALUE) {
                rtn = ((Long)value).intValue();
            }
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        return rtn;
    }

    private static class CollectingNodeVisitor
    implements NodeTraverser.VisitationStrategy {
        private final FilterPredicate predicate;
        private final List collectedNodes = new ArrayList();

        public CollectingNodeVisitor(FilterPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public void visit(AST node) {
            if (this.predicate == null || !this.predicate.exclude(node)) {
                this.collectedNodes.add(node);
            }
        }

        public List getCollectedNodes() {
            return this.collectedNodes;
        }

        public List collect(AST root) {
            NodeTraverser traverser = new NodeTraverser(this);
            traverser.traverseDepthFirst(root);
            return this.collectedNodes;
        }
    }

    public static abstract class IncludePredicate
    implements FilterPredicate {
        @Override
        public final boolean exclude(AST node) {
            return !this.include(node);
        }

        public abstract boolean include(AST var1);
    }

    public static interface FilterPredicate {
        public boolean exclude(AST var1);
    }
}

