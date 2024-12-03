/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;

@DefaultKey(value="mark")
public class MarkupTool
extends SafeConfig {
    public static final String DEFAULT_TAB = "  ";
    public static final String DEFAULT_DELIMITER = " ";
    private String tab = "  ";
    private String delim = " ";

    public void setTab(String tab) {
        if (!this.isConfigLocked()) {
            this.tab = tab;
        }
    }

    public String getTab() {
        return this.tab;
    }

    public Tag get(String tag) {
        return this.tag(tag);
    }

    public Tag tag(String definition) {
        String[] tags = this.split(definition);
        Tag last = null;
        for (int i = 0; i < tags.length; ++i) {
            Tag tag = this.parse(tags[i]);
            if (last != null) {
                last.append(tag);
            }
            last = tag;
        }
        return last;
    }

    protected String[] split(String me) {
        return me.split(this.delim);
    }

    protected Tag parse(String definition) {
        StringBuilder store = new StringBuilder();
        Tag tag = new Tag(this);
        Mode mode = Mode.NAME;
        for (int i = 0; i < definition.length(); ++i) {
            char c = definition.charAt(i);
            if (c == '#') {
                store = this.clear(mode, tag, store, true);
                mode = Mode.ID;
                continue;
            }
            if (c == '.') {
                store = this.clear(mode, tag, store, true);
                mode = Mode.CLASS;
                continue;
            }
            if (c == '[') {
                store = this.clear(mode, tag, store, true);
                mode = Mode.ATTR;
                continue;
            }
            if (c == ']') {
                store = this.clear(mode, tag, store, true);
                mode = Mode.NAME;
                continue;
            }
            store.append(c);
        }
        this.clear(mode, tag, store, false);
        return tag;
    }

    private StringBuilder clear(Mode mode, Tag tag, StringBuilder val, boolean emptyStore) {
        if (val.length() > 0) {
            String s = val.toString();
            switch (mode) {
                case NAME: {
                    tag.name(s);
                    break;
                }
                case ID: {
                    tag.id(s);
                    break;
                }
                case CLASS: {
                    tag.addClass(s);
                    break;
                }
                case ATTR: {
                    if (s.indexOf(61) > 0) {
                        String[] kv = s.split("=");
                        tag.attr(kv[0], kv[1]);
                        break;
                    }
                    tag.attr(s, null);
                }
            }
            if (emptyStore) {
                return new StringBuilder();
            }
            return val;
        }
        return val;
    }

    public static class Tag {
        private MarkupTool tool;
        private Tag parent;
        private Object name;
        private Object id;
        private List<Object> classes;
        private Map<Object, Object> attributes;
        private List<Object> children;

        public Tag(MarkupTool tool) {
            this.tool = tool;
        }

        public Tag name(Object name) {
            this.name = name;
            return this;
        }

        public Tag id(Object id) {
            this.id = id;
            return this;
        }

        public Tag addClass(Object c) {
            if (c == null) {
                return null;
            }
            if (this.classes == null) {
                this.classes = new ArrayList<Object>();
            }
            this.classes.add(c);
            return this;
        }

        public Tag attr(Object k, Object v) {
            if (k == null) {
                return null;
            }
            if (this.attributes == null) {
                this.attributes = new HashMap<Object, Object>();
            }
            this.attributes.put(k, v);
            return this;
        }

        public Tag body(Object o) {
            if (this.children == null) {
                this.children = new ArrayList<Object>();
            } else {
                this.children.clear();
            }
            this.children.add(o);
            return this;
        }

        public Tag append(Object o) {
            if (this.children == null) {
                this.children = new ArrayList<Object>();
            }
            this.children.add(o);
            if (o instanceof Tag) {
                ((Tag)o).parent(this);
            }
            return this;
        }

        public Tag prepend(Object o) {
            if (this.children == null) {
                this.children = new ArrayList<Object>();
                this.children.add(o);
            } else {
                this.children.add(0, o);
            }
            if (o instanceof Tag) {
                ((Tag)o).parent(this);
            }
            return this;
        }

        public Tag wrap(String tag) {
            Tag prnt = this.tool.tag(tag);
            prnt.root().parent(this.parent());
            this.parent(prnt);
            return this;
        }

        public Tag orphan() {
            return this.parent(null);
        }

        public Tag parent(Tag parent) {
            this.parent = parent;
            return this;
        }

        public Tag parent() {
            return this.parent;
        }

        public Tag root() {
            if (this.isOrphan()) {
                return this;
            }
            return this.parent.root();
        }

        public List<Object> children() {
            return this.children;
        }

        public boolean isOrphan() {
            return this.parent == null;
        }

        public boolean isEmpty() {
            return this.children == null || this.children().isEmpty();
        }

        public boolean matches(Tag tag) {
            return !this.missed(this.name, tag.name) && !this.missed(this.id, tag.id) && !this.missed(this.classes, tag.classes);
        }

        protected boolean missed(Object target, Object arrow) {
            if (arrow == null) {
                return false;
            }
            return !arrow.equals(target);
        }

        protected boolean missed(List<Object> targets, List<Object> arrows) {
            if (arrows == null) {
                return false;
            }
            if (targets == null) {
                return true;
            }
            for (Object o : arrows) {
                if (targets.contains(o)) continue;
                return true;
            }
            return false;
        }

        protected void render(String indent, StringBuilder s) {
            if (this.render_start(indent, s)) {
                this.render_body(indent, s);
                this.render_end(indent, s);
            }
        }

        protected boolean render_start(String indent, StringBuilder s) {
            if (indent != null) {
                s.append(indent);
            }
            s.append('<');
            this.render_name(s);
            this.render_id(s);
            this.render_classes(s);
            this.render_attributes(s);
            if (this.isEmpty()) {
                s.append("/>");
                return false;
            }
            s.append('>');
            return true;
        }

        protected void render_name(StringBuilder s) {
            s.append(this.name == null ? "div" : this.name);
        }

        protected void render_id(StringBuilder s) {
            if (this.id != null) {
                s.append(" id=\"").append(this.id).append('\"');
            }
        }

        protected void render_classes(StringBuilder s) {
            if (this.classes != null) {
                s.append(" class=\"");
                for (int i = 0; i < this.classes.size(); ++i) {
                    s.append(this.classes.get(i));
                    if (i + 1 == this.classes.size()) continue;
                    s.append(' ');
                }
                s.append('\"');
            }
        }

        protected void render_attributes(StringBuilder s) {
            if (this.attributes != null) {
                for (Map.Entry<Object, Object> entry : this.attributes.entrySet()) {
                    s.append(' ').append(entry.getKey()).append("=\"");
                    if (entry.getValue() != null) {
                        s.append(entry.getValue());
                    }
                    s.append('\"');
                }
            }
        }

        protected void render_body(String indent, StringBuilder s) {
            String kidIndent = indent + this.tool.getTab();
            for (Object o : this.children) {
                if (o instanceof Tag) {
                    ((Tag)o).render(kidIndent, s);
                    continue;
                }
                s.append(kidIndent);
                s.append(o);
            }
        }

        protected void render_end(String indent, StringBuilder s) {
            if (indent != null) {
                s.append(indent);
            }
            s.append("</").append(this.name).append('>');
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            this.root().render("\n", s);
            return s.toString();
        }
    }

    private static enum Mode {
        NAME,
        ID,
        CLASS,
        ATTR;

    }
}

