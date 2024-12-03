/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.groovy.binding.AbstractFullBinding;
import org.codehaus.groovy.binding.BindPath;

public class PropertyPathFullBinding
extends AbstractFullBinding
implements PropertyChangeListener {
    Set updateObjects = new HashSet();
    BindPath[] bindPaths;
    boolean bound;

    @Override
    public void bind() {
        this.updateObjects.clear();
        for (BindPath bp : this.bindPaths) {
            bp.addAllListeners(this, bp.currentObject, this.updateObjects);
        }
        this.bound = true;
    }

    @Override
    public void unbind() {
        this.updateObjects.clear();
        for (BindPath path : this.bindPaths) {
            path.removeListeners();
        }
        this.bound = false;
    }

    @Override
    public void rebind() {
        if (this.bound) {
            this.bind();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.updateObjects.contains(evt.getSource())) {
            for (BindPath bp : this.bindPaths) {
                HashSet newUpdates = new HashSet();
                bp.updatePath(this, bp.currentObject, newUpdates);
                this.updateObjects = newUpdates;
            }
        }
        this.update();
    }
}

