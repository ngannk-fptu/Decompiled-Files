/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.MappedResource;
import org.apache.tools.ant.types.resources.PropertyResource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;

public class PropertySet
extends DataType
implements ResourceCollection {
    private boolean dynamic = true;
    private boolean negate = false;
    private Set<String> cachedNames;
    private List<PropertyRef> ptyRefs = new ArrayList<PropertyRef>();
    private List<PropertySet> setRefs = new ArrayList<PropertySet>();
    private Mapper mapper;
    private boolean noAttributeSet = true;

    public void appendName(String name) {
        PropertyRef r = new PropertyRef();
        r.setName(name);
        this.addPropertyref(r);
    }

    public void appendRegex(String regex) {
        PropertyRef r = new PropertyRef();
        r.setRegex(regex);
        this.addPropertyref(r);
    }

    public void appendPrefix(String prefix) {
        PropertyRef r = new PropertyRef();
        r.setPrefix(prefix);
        this.addPropertyref(r);
    }

    public void appendBuiltin(BuiltinPropertySetName b) {
        PropertyRef r = new PropertyRef();
        r.setBuiltin(b);
        this.addPropertyref(r);
    }

    public void setMapper(String type, String from, String to) {
        Mapper m = this.createMapper();
        Mapper.MapperType mapperType = new Mapper.MapperType();
        mapperType.setValue(type);
        m.setType(mapperType);
        m.setFrom(from);
        m.setTo(to);
    }

    public void addPropertyref(PropertyRef ref) {
        this.assertNotReference();
        this.setChecked(false);
        this.ptyRefs.add(ref);
    }

    public void addPropertyset(PropertySet ref) {
        this.assertNotReference();
        this.setChecked(false);
        this.setRefs.add(ref);
    }

    public Mapper createMapper() {
        this.assertNotReference();
        if (this.mapper != null) {
            throw new BuildException("Too many <mapper>s!");
        }
        this.mapper = new Mapper(this.getProject());
        this.setChecked(false);
        return this.mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }

    public void setDynamic(boolean dynamic) {
        this.assertNotReference();
        this.dynamic = dynamic;
    }

    public void setNegate(boolean negate) {
        this.assertNotReference();
        this.negate = negate;
    }

    public boolean getDynamic() {
        if (this.isReference()) {
            return this.getRef().dynamic;
        }
        this.dieOnCircularReference();
        return this.dynamic;
    }

    public Mapper getMapper() {
        if (this.isReference()) {
            return this.getRef().mapper;
        }
        this.dieOnCircularReference();
        return this.mapper;
    }

    private Map<String, Object> getAllSystemProperties() {
        return System.getProperties().stringPropertyNames().stream().collect(Collectors.toMap(name -> name, name -> System.getProperties().getProperty((String)name), (a, b) -> b));
    }

    public Properties getProperties() {
        Properties result = new Properties();
        result.putAll(this.getPropertyMap());
        return result;
    }

    private Map<String, Object> getPropertyMap() {
        if (this.isReference()) {
            return this.getRef().getPropertyMap();
        }
        this.dieOnCircularReference();
        Mapper myMapper = this.getMapper();
        FileNameMapper m = myMapper == null ? null : myMapper.getImplementation();
        Map<String, Object> effectiveProperties = this.getEffectiveProperties();
        Set<String> propertyNames = this.getPropertyNames(effectiveProperties);
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (String name : propertyNames) {
            String[] newname;
            Object value = effectiveProperties.get(name);
            if (value == null) continue;
            if (m != null && (newname = m.mapFileName(name)) != null) {
                name = newname[0];
            }
            result.put(name, value);
        }
        return result;
    }

    private Map<String, Object> getEffectiveProperties() {
        Map<String, Object> result;
        Project prj = this.getProject();
        if (prj == null) {
            result = this.getAllSystemProperties();
        } else {
            PropertyHelper ph = PropertyHelper.getPropertyHelper(prj);
            result = prj.getPropertyNames().stream().map(n -> new AbstractMap.SimpleImmutableEntry<String, Object>((String)n, ph.getProperty((String)n))).filter(kv -> kv.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        for (PropertySet set : this.setRefs) {
            result.putAll(set.getPropertyMap());
        }
        return result;
    }

    private Set<String> getPropertyNames(Map<String, Object> props) {
        HashSet<String> names;
        if (this.getDynamic() || this.cachedNames == null) {
            names = new HashSet();
            this.addPropertyNames(names, props);
            for (PropertySet set : this.setRefs) {
                names.addAll(set.getPropertyMap().keySet());
            }
            if (this.negate) {
                HashSet<String> complement = new HashSet<String>(props.keySet());
                complement.removeAll(names);
                names = complement;
            }
            if (!this.getDynamic()) {
                this.cachedNames = names;
            }
        } else {
            names = this.cachedNames;
        }
        return names;
    }

    private void addPropertyNames(Set<String> names, Map<String, Object> props) {
        if (this.isReference()) {
            this.getRef().addPropertyNames(names, props);
        }
        this.dieOnCircularReference();
        for (PropertyRef r : this.ptyRefs) {
            if (r.name != null) {
                if (props.get(r.name) == null) continue;
                names.add(r.name);
                continue;
            }
            if (r.prefix != null) {
                for (String name : props.keySet()) {
                    if (!name.startsWith(r.prefix)) continue;
                    names.add(name);
                }
                continue;
            }
            if (r.regex != null) {
                RegexpMatcherFactory matchMaker = new RegexpMatcherFactory();
                RegexpMatcher matcher = matchMaker.newRegexpMatcher();
                matcher.setPattern(r.regex);
                for (String name : props.keySet()) {
                    if (!matcher.matches(name)) continue;
                    names.add(name);
                }
                continue;
            }
            if (r.builtin != null) {
                switch (r.builtin) {
                    case "all": {
                        names.addAll(props.keySet());
                        break;
                    }
                    case "system": {
                        names.addAll(this.getAllSystemProperties().keySet());
                        break;
                    }
                    case "commandline": {
                        names.addAll(this.getProject().getUserProperties().keySet());
                        break;
                    }
                    default: {
                        throw new BuildException("Impossible: Invalid builtin attribute!");
                    }
                }
                continue;
            }
            throw new BuildException("Impossible: Invalid PropertyRef!");
        }
    }

    protected PropertySet getRef() {
        return this.getCheckedRef(PropertySet.class);
    }

    @Override
    public final void setRefid(Reference r) {
        if (!this.noAttributeSet) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    protected final void assertNotReference() {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.noAttributeSet = false;
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        this.dieOnCircularReference();
        return new TreeMap<String, Object>(this.getPropertyMap()).entrySet().stream().map(e -> (String)e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", "));
    }

    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.dieOnCircularReference();
        Stream<Resource> result = this.getPropertyNames(this.getEffectiveProperties()).stream().map(name -> new PropertyResource(this.getProject(), (String)name));
        Optional<FileNameMapper> m = Optional.ofNullable(this.getMapper()).map(Mapper::getImplementation);
        if (m.isPresent()) {
            result = result.map(p -> new MappedResource((Resource)p, (FileNameMapper)m.get()));
        }
        return result.iterator();
    }

    @Override
    public int size() {
        return this.isReference() ? this.getRef().size() : this.getProperties().size();
    }

    @Override
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return false;
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.mapper != null) {
                PropertySet.pushAndInvokeCircularReferenceCheck(this.mapper, stk, p);
            }
            for (PropertySet propertySet : this.setRefs) {
                PropertySet.pushAndInvokeCircularReferenceCheck(propertySet, stk, p);
            }
            this.setChecked(true);
        }
    }

    public static class PropertyRef {
        private int count;
        private String name;
        private String regex;
        private String prefix;
        private String builtin;

        public void setName(String name) {
            this.assertValid("name", name);
            this.name = name;
        }

        public void setRegex(String regex) {
            this.assertValid("regex", regex);
            this.regex = regex;
        }

        public void setPrefix(String prefix) {
            this.assertValid("prefix", prefix);
            this.prefix = prefix;
        }

        public void setBuiltin(BuiltinPropertySetName b) {
            String pBuiltIn = b.getValue();
            this.assertValid("builtin", pBuiltIn);
            this.builtin = pBuiltIn;
        }

        private void assertValid(String attr, String value) {
            if (value == null || value.length() < 1) {
                throw new BuildException("Invalid attribute: " + attr);
            }
            if (++this.count != 1) {
                throw new BuildException("Attributes name, regex, and prefix are mutually exclusive");
            }
        }

        public String toString() {
            return "name=" + this.name + ", regex=" + this.regex + ", prefix=" + this.prefix + ", builtin=" + this.builtin;
        }
    }

    public static class BuiltinPropertySetName
    extends EnumeratedAttribute {
        static final String ALL = "all";
        static final String SYSTEM = "system";
        static final String COMMANDLINE = "commandline";

        @Override
        public String[] getValues() {
            return new String[]{ALL, SYSTEM, COMMANDLINE};
        }
    }
}

