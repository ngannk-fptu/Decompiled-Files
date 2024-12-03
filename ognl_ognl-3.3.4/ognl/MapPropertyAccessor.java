/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import ognl.ASTProperty;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

public class MapPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        Map map = (Map)target;
        Node currentNode = ((OgnlContext)context).getCurrentNode().jjtGetParent();
        boolean indexedAccess = false;
        if (currentNode == null) {
            throw new OgnlException("node is null for '" + name + "'");
        }
        if (!(currentNode instanceof ASTProperty)) {
            currentNode = currentNode.jjtGetParent();
        }
        if (currentNode instanceof ASTProperty) {
            indexedAccess = ((ASTProperty)currentNode).isIndexedAccess();
        }
        Object result = name instanceof String && !indexedAccess ? (name.equals("size") ? new Integer(map.size()) : (name.equals("keys") || name.equals("keySet") ? map.keySet() : (name.equals("values") ? map.values() : (name.equals("isEmpty") ? (map.isEmpty() ? Boolean.TRUE : Boolean.FALSE) : map.get(name))))) : map.get(name);
        return result;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        Map map = (Map)target;
        map.put(name, value);
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        Node currentNode = context.getCurrentNode().jjtGetParent();
        boolean indexedAccess = false;
        if (currentNode == null) {
            throw new RuntimeException("node is null for '" + index + "'");
        }
        if (!(currentNode instanceof ASTProperty)) {
            currentNode = currentNode.jjtGetParent();
        }
        if (currentNode instanceof ASTProperty) {
            indexedAccess = ((ASTProperty)currentNode).isIndexedAccess();
        }
        String indexStr = index.toString();
        context.setCurrentAccessor(Map.class);
        context.setCurrentType(Object.class);
        if (String.class.isInstance(index) && !indexedAccess) {
            String key;
            String string = key = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
            if (key.equals("size")) {
                context.setCurrentType(Integer.TYPE);
                return ".size()";
            }
            if (key.equals("keys") || key.equals("keySet")) {
                context.setCurrentType(Set.class);
                return ".keySet()";
            }
            if (key.equals("values")) {
                context.setCurrentType(Collection.class);
                return ".values()";
            }
            if (key.equals("isEmpty")) {
                context.setCurrentType(Boolean.TYPE);
                return ".isEmpty()";
            }
        }
        return ".get(" + indexStr + ")";
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        context.setCurrentAccessor(Map.class);
        context.setCurrentType(Object.class);
        String indexStr = index.toString();
        if (String.class.isInstance(index)) {
            String key;
            String string = key = indexStr.indexOf(34) >= 0 ? indexStr.replaceAll("\"", "") : indexStr;
            if (key.equals("size")) {
                return "";
            }
            if (key.equals("keys") || key.equals("keySet")) {
                return "";
            }
            if (key.equals("values")) {
                return "";
            }
            if (key.equals("isEmpty")) {
                return "";
            }
        }
        return ".put(" + indexStr + ", $3)";
    }
}

