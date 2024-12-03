/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.xmlparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class TreeNode {
    protected HashMap attributes = null;
    protected String body = null;
    protected ArrayList children = null;
    protected String name = null;
    protected TreeNode parent = null;

    public TreeNode(String name) {
        this(name, null);
    }

    public TreeNode(String name, TreeNode parent) {
        this.name = name;
        this.parent = parent;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }

    public void addAttribute(String name, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        this.attributes.put(name, value);
    }

    public void addChild(TreeNode node) {
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(node);
    }

    public String findAttribute(String name) {
        if (this.attributes == null) {
            return null;
        }
        return (String)this.attributes.get(name);
    }

    public Iterator findAttributes() {
        if (this.attributes == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return this.attributes.keySet().iterator();
    }

    public TreeNode findChild(String name) {
        if (this.children == null) {
            return null;
        }
        for (TreeNode item : this.children) {
            if (!name.equals(item.getName())) continue;
            return item;
        }
        return null;
    }

    public Iterator findChildren() {
        if (this.children == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return this.children.iterator();
    }

    public Iterator findChildren(String name) {
        if (this.children == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        ArrayList<TreeNode> results = new ArrayList<TreeNode>();
        for (TreeNode item : this.children) {
            if (!name.equals(item.getName())) continue;
            results.add(item);
        }
        return results.iterator();
    }

    public String getBody() {
        return this.body;
    }

    public String getName() {
        return this.name;
    }

    public void removeAttribute(String name) {
        if (this.attributes != null) {
            this.attributes.remove(name);
        }
    }

    public void removeNode(TreeNode node) {
        if (this.children != null) {
            this.children.remove(node);
        }
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        this.toString(sb, 0, this);
        return sb.toString();
    }

    protected void toString(StringBuffer sb, int indent, TreeNode node) {
        int indent2 = indent + 2;
        for (int i = 0; i < indent; ++i) {
            sb.append(' ');
        }
        sb.append('<');
        sb.append(node.getName());
        Iterator names = node.findAttributes();
        while (names.hasNext()) {
            sb.append(' ');
            String name = (String)names.next();
            sb.append(name);
            sb.append("=\"");
            String value = node.findAttribute(name);
            sb.append(value);
            sb.append("\"");
        }
        sb.append(">\n");
        String body = node.getBody();
        if (body != null && body.length() > 0) {
            for (int i = 0; i < indent2; ++i) {
                sb.append(' ');
            }
            sb.append(body);
            sb.append("\n");
        }
        Iterator children = node.findChildren();
        while (children.hasNext()) {
            TreeNode child = (TreeNode)children.next();
            this.toString(sb, indent2, child);
        }
        for (int i = 0; i < indent; ++i) {
            sb.append(' ');
        }
        sb.append("</");
        sb.append(node.getName());
        sb.append(">\n");
    }
}

