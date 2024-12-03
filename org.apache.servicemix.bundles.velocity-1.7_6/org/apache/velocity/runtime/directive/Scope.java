/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.StopCommand;

public class Scope
extends AbstractMap {
    private Map storage;
    private Object replaced;
    private Scope parent;
    private Info info;
    protected final Object owner;

    public Scope(Object owner, Object previous) {
        this.owner = owner;
        if (previous != null) {
            try {
                this.parent = (Scope)previous;
            }
            catch (ClassCastException cce) {
                this.replaced = previous;
            }
        }
    }

    private Map getStorage() {
        if (this.storage == null) {
            this.storage = new HashMap();
        }
        return this.storage;
    }

    public Set entrySet() {
        return this.getStorage().entrySet();
    }

    public Object get(Object key) {
        Object o = super.get(key);
        if (o == null && this.parent != null && !this.containsKey(key)) {
            return this.parent.get(key);
        }
        return o;
    }

    public Object put(Object key, Object value) {
        return this.getStorage().put(key, value);
    }

    protected void stop() {
        throw new StopCommand(this.owner);
    }

    protected int getDepth() {
        if (this.parent == null) {
            return 1;
        }
        return this.parent.getDepth() + 1;
    }

    public Scope getTopmost() {
        if (this.parent == null) {
            return this;
        }
        return this.parent.getTopmost();
    }

    public Scope getParent() {
        return this.parent;
    }

    public Object getReplaced() {
        if (this.replaced == null && this.parent != null) {
            return this.parent.getReplaced();
        }
        return this.replaced;
    }

    public Info getInfo() {
        if (this.info == null) {
            this.info = new Info(this, this.owner);
        }
        return this.info;
    }

    public static class Info {
        private Scope scope;
        private Directive directive;
        private Template template;

        public Info(Scope scope, Object owner) {
            if (owner instanceof Directive) {
                this.directive = (Directive)owner;
            }
            if (owner instanceof Template) {
                this.template = (Template)owner;
            }
            this.scope = scope;
        }

        public String getName() {
            if (this.directive != null) {
                return this.directive.getName();
            }
            if (this.template != null) {
                return this.template.getName();
            }
            return null;
        }

        public String getType() {
            if (this.directive != null) {
                switch (this.directive.getType()) {
                    case 1: {
                        return "block";
                    }
                    case 2: {
                        return "line";
                    }
                }
            }
            if (this.template != null) {
                return this.template.getEncoding();
            }
            return null;
        }

        public int getDepth() {
            return this.scope.getDepth();
        }

        public String getTemplate() {
            if (this.directive != null) {
                return this.directive.getTemplateName();
            }
            if (this.template != null) {
                return this.template.getName();
            }
            return null;
        }

        public int getLine() {
            if (this.directive != null) {
                return this.directive.getLine();
            }
            return 0;
        }

        public int getColumn() {
            if (this.directive != null) {
                return this.directive.getColumn();
            }
            return 0;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            if (this.directive != null) {
                sb.append('#');
            }
            sb.append(this.getName());
            sb.append("[type:").append(this.getType());
            int depth = this.getDepth();
            if (depth > 1) {
                sb.append(" depth:").append(depth);
            }
            if (this.template == null) {
                String vtl = this.getTemplate();
                sb.append(" template:");
                if (vtl.indexOf(" ") < 0) {
                    sb.append(vtl);
                } else {
                    sb.append('\"').append(vtl).append('\"');
                }
                sb.append(" line:").append(this.getLine());
                sb.append(" column:").append(this.getColumn());
            }
            sb.append(']');
            return sb.toString();
        }
    }
}

