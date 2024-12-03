/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DependencyDefinition;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

public class ModelInterpretationContext
extends ContextAwareBase
implements PropertyContainer {
    Stack<Object> objectStack;
    Stack<Model> modelStack;
    Map<String, Object> objectMap;
    protected Map<String, String> propertiesMap;
    protected Map<String, String> importMap;
    private final BeanDescriptionCache beanDescriptionCache;
    final DefaultNestedComponentRegistry defaultNestedComponentRegistry = new DefaultNestedComponentRegistry();
    List<DependencyDefinition> dependencyDefinitionList = new ArrayList<DependencyDefinition>();
    final List<String> startedDependees = new ArrayList<String>();
    Object configuratorHint;
    Model topModel;

    public ModelInterpretationContext(Context context) {
        this(context, null);
    }

    public ModelInterpretationContext(Context context, Object configuratorHint) {
        this.context = context;
        this.configuratorHint = configuratorHint;
        this.objectStack = new Stack();
        this.modelStack = new Stack();
        this.beanDescriptionCache = new BeanDescriptionCache(context);
        this.objectMap = new HashMap<String, Object>(5);
        this.propertiesMap = new HashMap<String, String>(5);
        this.importMap = new HashMap<String, String>(5);
    }

    public ModelInterpretationContext(ModelInterpretationContext otherMic) {
        this(otherMic.context, otherMic.configuratorHint);
        this.importMap = new HashMap<String, String>(otherMic.importMap);
        this.propertiesMap = new HashMap<String, String>(otherMic.propertiesMap);
        this.defaultNestedComponentRegistry.duplicate(otherMic.getDefaultNestedComponentRegistry());
        this.createAppenderBags();
    }

    public Map<String, Object> getObjectMap() {
        return this.objectMap;
    }

    public void createAppenderBags() {
        this.objectMap.put("APPENDER_BAG", new HashMap());
        this.objectMap.put("APPENDER_REF_BAG", new HashMap());
    }

    public Model getTopModel() {
        return this.topModel;
    }

    public void setTopModel(Model topModel) {
        this.topModel = topModel;
    }

    public void pushModel(Model m) {
        this.modelStack.push(m);
    }

    public Model peekModel() {
        return this.modelStack.peek();
    }

    public boolean isModelStackEmpty() {
        return this.modelStack.isEmpty();
    }

    public Model popModel() {
        return this.modelStack.pop();
    }

    public Stack<Object> getObjectStack() {
        return this.objectStack;
    }

    public boolean isObjectStackEmpty() {
        return this.objectStack.isEmpty();
    }

    public Object peekObject() {
        return this.objectStack.peek();
    }

    public void pushObject(Object o) {
        this.objectStack.push(o);
    }

    public Object popObject() {
        return this.objectStack.pop();
    }

    public Object getObject(int i) {
        return this.objectStack.get(i);
    }

    public Object getConfiguratorHint() {
        return this.configuratorHint;
    }

    public void setConfiguratorHint(Object configuratorHint) {
        this.configuratorHint = configuratorHint;
    }

    public BeanDescriptionCache getBeanDescriptionCache() {
        return this.beanDescriptionCache;
    }

    public String subst(String ref) {
        if (ref == null) {
            return null;
        }
        try {
            return OptionHelper.substVars(ref, this, this.context);
        }
        catch (ScanException | IllegalArgumentException e) {
            this.addError("Problem while parsing [" + ref + "]", e);
            return ref;
        }
    }

    public void addSubstitutionProperty(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        value = value.trim();
        this.propertiesMap.put(key, value);
    }

    public void addSubstitutionProperties(Properties props) {
        if (props == null) {
            return;
        }
        for (Object keyObject : props.keySet()) {
            String key = (String)keyObject;
            String val = props.getProperty(key);
            this.addSubstitutionProperty(key, val);
        }
    }

    public DefaultNestedComponentRegistry getDefaultNestedComponentRegistry() {
        return this.defaultNestedComponentRegistry;
    }

    public void addDependencyDefinition(DependencyDefinition dd) {
        this.dependencyDefinitionList.add(dd);
    }

    public List<DependencyDefinition> getDependencyDefinitions() {
        return Collections.unmodifiableList(this.dependencyDefinitionList);
    }

    public List<String> getDependeeNamesForModel(Model model) {
        ArrayList<String> dependencyList = new ArrayList<String>();
        for (DependencyDefinition dd : this.dependencyDefinitionList) {
            if (dd.getDepender() != model) continue;
            dependencyList.add(dd.getDependee());
        }
        return dependencyList;
    }

    public boolean hasDependers(String dependeeName) {
        if (dependeeName == null || dependeeName.trim().length() == 0) {
            new IllegalArgumentException("Empty dependeeName name not allowed here");
        }
        for (DependencyDefinition dd : this.dependencyDefinitionList) {
            if (!dd.dependee.equals(dependeeName)) continue;
            return true;
        }
        return false;
    }

    public void markStartOfNamedDependee(String name) {
        this.startedDependees.add(name);
    }

    public boolean isNamedDependeeStarted(String name) {
        return this.startedDependees.contains(name);
    }

    @Override
    public String getProperty(String key) {
        String v = this.propertiesMap.get(key);
        if (v != null) {
            return v;
        }
        return this.context.getProperty(key);
    }

    @Override
    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap<String, String>(this.propertiesMap);
    }

    public void addImport(String stem, String fqcn) {
        this.importMap.put(stem, fqcn);
    }

    public Map<String, String> getImportMapCopy() {
        return new HashMap<String, String>(this.importMap);
    }

    public String getImport(String stem) {
        if (stem == null) {
            return null;
        }
        String result = this.importMap.get(stem);
        if (result == null) {
            return stem;
        }
        return result;
    }
}

