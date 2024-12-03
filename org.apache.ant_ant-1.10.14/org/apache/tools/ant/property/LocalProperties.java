/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.property;

import java.util.Set;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.property.LocalPropertyStack;

public class LocalProperties
extends InheritableThreadLocal<LocalPropertyStack>
implements PropertyHelper.PropertyEvaluator,
PropertyHelper.PropertySetter,
PropertyHelper.PropertyEnumerator {
    public static synchronized LocalProperties get(Project project) {
        LocalProperties l = (LocalProperties)project.getReference("ant.LocalProperties");
        if (l == null) {
            l = new LocalProperties();
            project.addReference("ant.LocalProperties", l);
            PropertyHelper.getPropertyHelper(project).add(l);
        }
        return l;
    }

    private LocalProperties() {
    }

    @Override
    protected synchronized LocalPropertyStack initialValue() {
        return new LocalPropertyStack();
    }

    public void addLocal(String property) {
        ((LocalPropertyStack)this.get()).addLocal(property);
    }

    public void enterScope() {
        ((LocalPropertyStack)this.get()).enterScope();
    }

    public void exitScope() {
        ((LocalPropertyStack)this.get()).exitScope();
    }

    public void copy() {
        this.set(((LocalPropertyStack)this.get()).copy());
    }

    @Override
    public Object evaluate(String property, PropertyHelper helper) {
        return ((LocalPropertyStack)this.get()).evaluate(property, helper);
    }

    @Override
    public boolean setNew(String property, Object value, PropertyHelper propertyHelper) {
        return ((LocalPropertyStack)this.get()).setNew(property, value, propertyHelper);
    }

    @Override
    public boolean set(String property, Object value, PropertyHelper propertyHelper) {
        return ((LocalPropertyStack)this.get()).set(property, value, propertyHelper);
    }

    @Override
    public Set<String> getPropertyNames() {
        return ((LocalPropertyStack)this.get()).getPropertyNames();
    }
}

