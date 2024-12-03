/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics;

import java.util.LinkedList;
import java.util.List;
import org.terracotta.context.TreeNode;

public class Constants {
    public static final String NAME_PROP = "name";
    public static final String PROPERTIES_PROP = "properties";

    public static String[] formStringPathsFromContext(TreeNode tn) {
        LinkedList<String> results = new LinkedList<String>();
        for (List<? extends TreeNode> path : tn.getPaths()) {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            for (TreeNode treeNode : path) {
                String name = null;
                if (name == null) {
                    name = (String)treeNode.getContext().attributes().get(NAME_PROP);
                }
                if (name == null) {
                    name = treeNode.getContext().identifier().getSimpleName();
                }
                if (!first) {
                    sb.append("/");
                } else {
                    first = false;
                }
                sb.append(name);
            }
            results.add(sb.toString());
        }
        return results.toArray(new String[0]);
    }
}

