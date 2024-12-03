/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.ProjectHelper;

public final class DefaultDefinitions {
    private static final String IF_NAMESPACE = "ant:if";
    private static final String UNLESS_NAMESPACE = "ant:unless";
    private final ComponentHelper componentHelper;

    public DefaultDefinitions(ComponentHelper componentHelper) {
        this.componentHelper = componentHelper;
    }

    public void execute() {
        this.attributeNamespaceDef(IF_NAMESPACE);
        this.attributeNamespaceDef(UNLESS_NAMESPACE);
        this.ifUnlessDef("true", "IfTrueAttribute");
        this.ifUnlessDef("set", "IfSetAttribute");
        this.ifUnlessDef("blank", "IfBlankAttribute");
    }

    private void attributeNamespaceDef(String ns) {
        AntTypeDefinition def = new AntTypeDefinition();
        def.setName(ProjectHelper.nsToComponentName(ns));
        def.setClassName("org.apache.tools.ant.attribute.AttributeNamespace");
        def.setClassLoader(this.getClass().getClassLoader());
        def.setRestrict(true);
        this.componentHelper.addDataTypeDefinition(def);
    }

    private void ifUnlessDef(String name, String base) {
        String classname = "org.apache.tools.ant.attribute." + base;
        this.componentDef(IF_NAMESPACE, name, classname);
        this.componentDef(UNLESS_NAMESPACE, name, classname + "$Unless");
    }

    private void componentDef(String ns, String name, String classname) {
        AntTypeDefinition def = new AntTypeDefinition();
        def.setName(ProjectHelper.genComponentName(ns, name));
        def.setClassName(classname);
        def.setClassLoader(this.getClass().getClassLoader());
        def.setRestrict(true);
        this.componentHelper.addDataTypeDefinition(def);
    }
}

