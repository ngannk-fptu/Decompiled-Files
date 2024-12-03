/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.script;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.DefBase;
import org.apache.tools.ant.taskdefs.optional.script.ScriptDefBase;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.ScriptManager;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public class ScriptDef
extends DefBase {
    private ScriptRunnerHelper helper = new ScriptRunnerHelper();
    private String name;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private List<NestedElement> nestedElements = new ArrayList<NestedElement>();
    private Set<String> attributeSet;
    private Map<String, NestedElement> nestedElementMap;

    public ScriptDef() {
        this.helper.setSetBeans(false);
    }

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAttributeSupported(String attributeName) {
        return this.attributeSet.contains(attributeName);
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void addElement(NestedElement nestedElement) {
        this.nestedElements.add(nestedElement);
    }

    @Override
    public void execute() {
        if (this.name == null) {
            throw new BuildException("scriptdef requires a name attribute to name the script");
        }
        if (this.helper.getLanguage() == null) {
            throw new BuildException("scriptdef requires a language attribute to specify the script language");
        }
        if (this.helper.getSrc() == null && this.helper.getEncoding() != null) {
            throw new BuildException("scriptdef requires a src attribute if the encoding is set");
        }
        if (this.getAntlibClassLoader() != null || this.hasCpDelegate()) {
            this.helper.setClassLoader(this.createLoader());
        }
        this.attributeSet = new HashSet<String>();
        for (Attribute attribute : this.attributes) {
            if (attribute.name == null) {
                throw new BuildException("scriptdef <attribute> elements must specify an attribute name");
            }
            if (this.attributeSet.contains(attribute.name)) {
                throw new BuildException("scriptdef <%s> declares the %s attribute more than once", this.name, attribute.name);
            }
            this.attributeSet.add(attribute.name);
        }
        this.nestedElementMap = new HashMap<String, NestedElement>();
        for (NestedElement nestedElement : this.nestedElements) {
            if (nestedElement.name == null) {
                throw new BuildException("scriptdef <element> elements must specify an element name");
            }
            if (this.nestedElementMap.containsKey(nestedElement.name)) {
                throw new BuildException("scriptdef <%s> declares the %s nested element more than once", this.name, nestedElement.name);
            }
            if (nestedElement.className == null && nestedElement.type == null) {
                throw new BuildException("scriptdef <element> elements must specify either a classname or type attribute");
            }
            if (nestedElement.className != null && nestedElement.type != null) {
                throw new BuildException("scriptdef <element> elements must specify only one of the classname and type attributes");
            }
            this.nestedElementMap.put(nestedElement.name, nestedElement);
        }
        Map<String, ScriptDef> scriptRepository = this.lookupScriptRepository();
        this.name = ProjectHelper.genComponentName(this.getURI(), this.name);
        scriptRepository.put(this.name, this);
        AntTypeDefinition def = new AntTypeDefinition();
        def.setName(this.name);
        def.setClass(ScriptDefBase.class);
        ComponentHelper.getComponentHelper(this.getProject()).addDataTypeDefinition(def);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, ScriptDef> lookupScriptRepository() {
        HashMap<String, ScriptDef> scriptRepository;
        Project p;
        Project project = p = this.getProject();
        synchronized (project) {
            scriptRepository = (HashMap<String, ScriptDef>)p.getReference("org.apache.ant.scriptrepo");
            if (scriptRepository == null) {
                scriptRepository = new HashMap<String, ScriptDef>();
                p.addReference("org.apache.ant.scriptrepo", scriptRepository);
            }
        }
        return scriptRepository;
    }

    public Object createNestedElement(String elementName) {
        Object instance;
        NestedElement definition = this.nestedElementMap.get(elementName);
        if (definition == null) {
            throw new BuildException("<%s> does not support the <%s> nested element", this.name, elementName);
        }
        String classname = definition.className;
        if (classname == null) {
            instance = this.getProject().createTask(definition.type);
            if (instance == null) {
                instance = this.getProject().createDataType(definition.type);
            }
        } else {
            ClassLoader loader = this.createLoader();
            try {
                instance = ClasspathUtils.newInstance(classname, loader);
            }
            catch (BuildException e) {
                instance = ClasspathUtils.newInstance(classname, ScriptDef.class.getClassLoader());
            }
            this.getProject().setProjectReference(instance);
        }
        if (instance == null) {
            throw new BuildException("<%s> is unable to create the <%s> nested element", this.name, elementName);
        }
        return instance;
    }

    @Deprecated
    public void executeScript(Map<String, String> attributes, Map<String, List<Object>> elements) {
        this.executeScript(attributes, elements, null);
    }

    public void executeScript(Map<String, String> attributes, Map<String, List<Object>> elements, ScriptDefBase instance) {
        ScriptRunnerBase runner = this.helper.getScriptRunner();
        runner.addBean("attributes", this.withDefault(attributes));
        runner.addBean("elements", elements);
        runner.addBean("project", this.getProject());
        if (instance != null) {
            runner.addBean("self", instance);
        }
        runner.executeScript("scriptdef_" + this.name);
    }

    @Deprecated
    public void setManager(String manager) {
        this.helper.setManager(manager);
    }

    public void setManager(ScriptManager manager) {
        this.helper.setManager(manager);
    }

    public void setLanguage(String language) {
        this.helper.setLanguage(language);
    }

    public void setCompiled(boolean compiled) {
        this.helper.setCompiled(compiled);
    }

    public void setSrc(File file) {
        this.helper.setSrc(file);
    }

    public void setEncoding(String encoding) {
        this.helper.setEncoding(encoding);
    }

    public void setSetBeans(boolean setBeans) {
        this.helper.setSetBeans(setBeans);
    }

    public void addText(String text) {
        this.helper.addText(text);
    }

    public void add(ResourceCollection resource) {
        this.helper.add(resource);
    }

    private Map<String, String> withDefault(Map<String, String> attributes) {
        Set unsupported = this.attributeSet.stream().filter(a -> !this.isAttributeSupported((String)a)).map(s -> '@' + s).collect(Collectors.toSet());
        if (!unsupported.isEmpty()) {
            throw new BuildException("Found unsupported attributes " + unsupported);
        }
        if (this.attributes.isEmpty()) {
            return attributes;
        }
        Map result = this.attributes.stream().filter(Attribute::hasDefault).collect(Collectors.toMap(Attribute::getName, Attribute::getDefault, (l, r) -> r, LinkedHashMap::new));
        result.putAll(attributes);
        return result;
    }

    public static class Attribute {
        private String name;
        private String defaultValue;

        public void setName(String name) {
            this.name = name.toLowerCase(Locale.ENGLISH);
        }

        String getName() {
            return this.name;
        }

        public void setDefault(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        String getDefault() {
            return this.defaultValue;
        }

        boolean hasDefault() {
            return this.defaultValue != null;
        }
    }

    public static class NestedElement {
        private String name;
        private String type;
        private String className;

        public void setName(String name) {
            this.name = name.toLowerCase(Locale.ENGLISH);
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}

