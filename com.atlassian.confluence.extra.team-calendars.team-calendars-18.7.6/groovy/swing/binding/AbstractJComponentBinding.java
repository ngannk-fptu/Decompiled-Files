/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractSyntheticBinding;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;

abstract class AbstractJComponentBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
ComponentListener {
    JComponent boundComponent;
    String propertyName;

    public AbstractJComponentBinding(PropertyBinding source, TargetBinding target, String propertyName) {
        super(source, target, JComponent.class, propertyName);
        source.setNonChangeCheck(true);
    }

    @Override
    public synchronized void syntheticBind() {
        this.boundComponent = (JComponent)((PropertyBinding)this.sourceBinding).getBean();
        this.boundComponent.addPropertyChangeListener(this.propertyName, this);
        this.boundComponent.addComponentListener(this);
    }

    @Override
    public synchronized void syntheticUnbind() {
        this.boundComponent.removePropertyChangeListener(this.propertyName, this);
        this.boundComponent.removeComponentListener(this);
        this.boundComponent = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        this.update();
        ((JComponent)event.getOldValue()).removeComponentListener(this);
        ((JComponent)event.getNewValue()).addComponentListener(this);
    }

    @Override
    public void componentHidden(ComponentEvent event) {
    }

    @Override
    public void componentShown(ComponentEvent event) {
    }

    @Override
    public void componentMoved(ComponentEvent event) {
    }

    @Override
    public void componentResized(ComponentEvent event) {
    }
}

