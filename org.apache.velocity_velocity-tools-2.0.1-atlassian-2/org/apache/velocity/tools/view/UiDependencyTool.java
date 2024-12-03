/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.Rule
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@DefaultKey(value="depends")
@ValidScope(value={"request"})
public class UiDependencyTool {
    public static final String GROUPS_KEY_SPACE = UiDependencyTool.class.getName() + ":";
    public static final String TYPES_KEY_SPACE = UiDependencyTool.class.getName() + ":types:";
    public static final String SOURCE_FILE_KEY = "file";
    public static final String DEFAULT_SOURCE_FILE = "ui.xml";
    private static final List<Type> DEFAULT_TYPES;
    private Map<String, Group> groups = null;
    private List<Type> types = DEFAULT_TYPES;
    private Map<String, List<String>> dependencies;
    private Log LOG;
    private String context = "";

    private void debug(String msg, Object ... args) {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug((Object)String.format("UiDependencyTool: " + msg, args));
        }
    }

    protected static final void trace(Log log, String msg, Object ... args) {
        if (log.isTraceEnabled()) {
            log.trace((Object)String.format("UiDependencyTool: " + msg, args));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void configure(Map params) {
        ServletContext app = (ServletContext)params.get("servletContext");
        this.LOG = (Log)params.get("log");
        HttpServletRequest request = (HttpServletRequest)params.get("request");
        this.context = request.getContextPath();
        String file = (String)params.get(SOURCE_FILE_KEY);
        if (file == null) {
            file = DEFAULT_SOURCE_FILE;
        } else {
            this.debug("Loading file: %s", file);
        }
        ServletContext servletContext = app;
        synchronized (servletContext) {
            this.groups = (Map)app.getAttribute(GROUPS_KEY_SPACE + file);
            if (this.groups == null) {
                this.groups = new LinkedHashMap<String, Group>();
                this.read(file, file != DEFAULT_SOURCE_FILE);
                app.setAttribute(GROUPS_KEY_SPACE + file, this.groups);
                if (this.types != DEFAULT_TYPES) {
                    app.setAttribute(TYPES_KEY_SPACE + file, this.types);
                }
            } else {
                List alt = (List)app.getAttribute(TYPES_KEY_SPACE + file);
                if (alt != null) {
                    this.types = alt;
                }
            }
        }
    }

    public UiDependencyTool on(String name) {
        Map<String, List<String>> groupDeps = this.getGroupDependencies(name);
        if (groupDeps == null) {
            return null;
        }
        this.addDependencies(groupDeps);
        return this;
    }

    public UiDependencyTool on(String type, String file) {
        if (type == null || file == null) {
            return null;
        }
        this.addFile(type, file);
        return this;
    }

    public String print() {
        return this.printAll("\n");
    }

    public String print(String typeOrDelim) {
        if (this.getType(typeOrDelim) == null) {
            return this.printAll(typeOrDelim);
        }
        return this.print(typeOrDelim, "\n");
    }

    public String print(String type, String delim) {
        List<String> files = this.getDependencies(type);
        if (files == null) {
            return null;
        }
        String format = this.getFormat(type);
        StringBuilder out = new StringBuilder();
        for (String file : files) {
            out.append(this.format(format, file));
            out.append(delim);
        }
        return out.toString();
    }

    public String printAll(String delim) {
        if (this.dependencies == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (Type type : this.types) {
            List<String> files;
            if (out.length() > 0) {
                out.append(delim);
            }
            if ((files = this.dependencies.get(type.name)) == null) continue;
            for (int i = 0; i < files.size(); ++i) {
                if (i > 0) {
                    out.append(delim);
                }
                out.append(this.format(type.format, files.get(i)));
            }
        }
        return out.toString();
    }

    public UiDependencyTool context(String path) {
        this.context = path;
        return this;
    }

    public String getFormat(String type) {
        Type t = this.getType(type);
        if (t == null) {
            return null;
        }
        return t.format;
    }

    public void setFormat(String type, String format) {
        Type t;
        if (format == null || type == null) {
            throw new NullPointerException("Type name and format must not be null");
        }
        if (this.types == DEFAULT_TYPES) {
            this.types = new ArrayList<Type>();
            for (Type t2 : DEFAULT_TYPES) {
                this.types.add(new Type(t2.name, t2.format));
            }
        }
        if ((t = this.getType(type)) == null) {
            this.types.add(new Type(type, format));
        } else {
            t.format = format;
        }
    }

    public Map<String, List<String>> getDependencies() {
        return this.dependencies;
    }

    public List<String> getDependencies(String type) {
        if (this.dependencies == null) {
            return null;
        }
        return this.dependencies.get(type);
    }

    public Map<String, List<String>> getGroupDependencies(String name) {
        Group group = this.getGroup(name);
        if (group == null) {
            return null;
        }
        return group.getDependencies(this);
    }

    public String toString() {
        return "";
    }

    protected void read(String file, boolean required) {
        this.debug("UiDependencyTool: Reading file from %s", file);
        URL url = this.toURL(file);
        if (url == null) {
            String msg = "UiDependencyTool: Could not read file from '" + file + "'";
            if (required) {
                this.LOG.error((Object)msg);
                throw new IllegalArgumentException(msg);
            }
            this.LOG.debug((Object)msg);
        } else {
            Digester digester = this.createDigester();
            try {
                digester.parse(url.openStream());
            }
            catch (SAXException saxe) {
                this.LOG.error((Object)("UiDependencyTool: Failed to parse '" + file + "'"), (Throwable)saxe);
                throw new RuntimeException("While parsing the InputStream", saxe);
            }
            catch (IOException ioe) {
                this.LOG.error((Object)("UiDependencyTool: Failed to read '" + file + "'"), (Throwable)ioe);
                throw new RuntimeException("While handling the InputStream", ioe);
            }
        }
    }

    protected Digester createDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setUseContextClassLoader(true);
        digester.addRule("ui/type", (Rule)new TypeRule());
        digester.addRule("ui/group", (Rule)new GroupRule());
        digester.addRule("ui/group/file", (Rule)new FileRule());
        digester.addRule("ui/group/needs", (Rule)new NeedsRule());
        digester.push((Object)this);
        return digester;
    }

    protected String format(String format, String value) {
        if (format == null) {
            return value;
        }
        return format.replace("{file}", value).replace("{context}", this.context);
    }

    protected Group getGroup(String name) {
        if (this.groups == null) {
            return null;
        }
        return this.groups.get(name);
    }

    protected Group makeGroup(String name) {
        UiDependencyTool.trace(this.LOG, "Creating group '%s'", name);
        Group group = new Group(name, this.LOG);
        this.groups.put(name, group);
        return group;
    }

    protected void addDependencies(Map<String, List<String>> fbt) {
        if (this.dependencies == null) {
            this.dependencies = new LinkedHashMap<String, List<String>>(fbt.size());
        }
        for (Map.Entry<String, List<String>> entry : fbt.entrySet()) {
            List<String> existing;
            String type = entry.getKey();
            if (this.getType(type) == null) {
                this.LOG.error((Object)("UiDependencyTool: Type '" + type + "' is unknown and will not be printed unless defined."));
            }
            if ((existing = this.dependencies.get(type)) == null) {
                existing = new ArrayList<String>(entry.getValue().size());
                this.dependencies.put(type, existing);
            }
            for (String file : entry.getValue()) {
                if (existing.contains(file)) continue;
                UiDependencyTool.trace(this.LOG, "Adding %s: %s", type, file);
                existing.add(file);
            }
        }
    }

    protected void addFile(String type, String file) {
        List<String> files = null;
        if (this.dependencies == null) {
            this.dependencies = new LinkedHashMap<String, List<String>>(this.types.size());
        } else {
            files = this.dependencies.get(type);
        }
        if (files == null) {
            files = new ArrayList<String>();
            this.dependencies.put(type, files);
        }
        if (!files.contains(file)) {
            UiDependencyTool.trace(this.LOG, "Adding %s: %s", type, file);
            files.add(file);
        }
    }

    private Type getType(String type) {
        for (Type t : this.types) {
            if (!t.name.equals(type)) continue;
            return t;
        }
        return null;
    }

    private URL toURL(String file) {
        try {
            return ClassUtils.getResource(file, this);
        }
        catch (Exception e) {
            return null;
        }
    }

    static {
        ArrayList<Type> types = new ArrayList<Type>();
        types.add(new Type("style", "<link rel=\"stylesheet\" type=\"text/css\" href=\"{context}/css/{file}\"/>"));
        types.add(new Type("script", "<script type=\"text/javascript\" src=\"{context}/js/{file}\"></script>"));
        DEFAULT_TYPES = Collections.unmodifiableList(types);
    }

    private static final class Type {
        protected String name;
        protected String format;

        Type(String n, String f) {
            this.name = n;
            this.format = f;
        }
    }

    protected static class NeedsRule
    extends Rule {
        protected NeedsRule() {
        }

        public void body(String ns, String el, String otherGroup) throws Exception {
            Group group = (Group)this.digester.peek();
            group.addGroup(otherGroup);
        }
    }

    protected static class FileRule
    extends Rule {
        protected FileRule() {
        }

        public void begin(String ns, String el, Attributes attributes) throws Exception {
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getLocalName(i);
                if ("".equals(name)) {
                    name = attributes.getQName(i);
                }
                if (!"type".equals(name)) continue;
                this.digester.push((Object)attributes.getValue(i));
            }
        }

        public void body(String ns, String el, String value) throws Exception {
            String type = (String)this.digester.pop();
            Group group = (Group)this.digester.peek();
            group.addFile(type, value);
        }
    }

    protected static class GroupRule
    extends Rule {
        private UiDependencyTool parent;

        protected GroupRule() {
        }

        public void begin(String ns, String el, Attributes attributes) throws Exception {
            this.parent = (UiDependencyTool)this.digester.peek();
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getLocalName(i);
                if ("".equals(name)) {
                    name = attributes.getQName(i);
                }
                if (!"name".equals(name)) continue;
                this.digester.push((Object)this.parent.makeGroup(attributes.getValue(i)));
            }
        }

        public void end(String ns, String el) throws Exception {
            this.digester.pop();
        }
    }

    protected static class TypeRule
    extends Rule {
        private UiDependencyTool parent;

        protected TypeRule() {
        }

        public void begin(String ns, String el, Attributes attributes) throws Exception {
            this.parent = (UiDependencyTool)this.digester.peek();
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getLocalName(i);
                if ("".equals(name)) {
                    name = attributes.getQName(i);
                }
                if (!"name".equals(name)) continue;
                this.digester.push((Object)attributes.getValue(i));
            }
        }

        public void body(String ns, String el, String typeFormat) throws Exception {
            String typeName = (String)this.digester.pop();
            this.parent.setFormat(typeName, typeFormat);
        }
    }

    protected static class Group {
        private volatile boolean resolved = true;
        private String name;
        private Map<String, Integer> typeCounts = new LinkedHashMap<String, Integer>();
        private Map<String, List<String>> dependencies = new LinkedHashMap<String, List<String>>();
        private List<String> groups;
        private Log LOG;

        public Group(String name, Log log) {
            this.name = name;
            this.LOG = log;
        }

        private void trace(String msg, Object ... args) {
            if (this.LOG.isTraceEnabled()) {
                UiDependencyTool.trace(this.LOG, "Group " + this.name + ": " + msg, args);
            }
        }

        public void addFile(String type, String value) {
            List<String> files = this.dependencies.get(type);
            if (files == null) {
                files = new ArrayList<String>();
                this.dependencies.put(type, files);
            }
            if (!files.contains(value)) {
                this.trace("Adding %s: %s", type, value);
                files.add(value);
            }
        }

        public void addGroup(String group) {
            if (this.groups == null) {
                this.resolved = false;
                this.groups = new ArrayList<String>();
            }
            if (!this.groups.contains(group)) {
                this.trace("Adding group %s", group, this.name);
                this.groups.add(group);
            }
        }

        public Map<String, List<String>> getDependencies(UiDependencyTool parent) {
            this.resolve(parent);
            return this.dependencies;
        }

        protected void resolve(UiDependencyTool parent) {
            if (!this.resolved) {
                this.resolved = true;
                this.trace("resolving...", new Object[0]);
                for (String name : this.groups) {
                    Group group = parent.getGroup(name);
                    if (group == null) {
                        throw new NullPointerException("No group named '" + name + "'");
                    }
                    Map<String, List<String>> dependencies = group.getDependencies(parent);
                    for (Map.Entry<String, List<String>> type : dependencies.entrySet()) {
                        for (String value : type.getValue()) {
                            this.addFileFromGroup(type.getKey(), value);
                        }
                    }
                }
                this.trace(" is resolved.", new Object[0]);
            }
        }

        private void addFileFromGroup(String type, String value) {
            List<String> files = this.dependencies.get(type);
            if (files == null) {
                files = new ArrayList<String>();
                files.add(value);
                this.trace("adding %s '%s' first", type, value);
                this.dependencies.put(type, files);
                this.typeCounts.put(type, 1);
            } else if (!files.contains(value)) {
                Integer count = this.typeCounts.get(type);
                if (count == null) {
                    count = 0;
                }
                files.add(count, value);
                this.trace("adding %s '%s' at %s", type, value, count);
                count = count + 1;
                this.typeCounts.put(type, count);
            }
        }
    }
}

