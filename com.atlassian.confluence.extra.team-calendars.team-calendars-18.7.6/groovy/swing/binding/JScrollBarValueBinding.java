/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractSyntheticBinding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;

class JScrollBarValueBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
ChangeListener {
    JScrollBar boundScrollBar;

    public JScrollBarValueBinding(PropertyBinding source, TargetBinding target) {
        super(source, target, JScrollBar.class, "value");
    }

    @Override
    public synchronized void syntheticBind() {
        this.boundScrollBar = (JScrollBar)((PropertyBinding)this.sourceBinding).getBean();
        this.boundScrollBar.addPropertyChangeListener("model", this);
        this.boundScrollBar.getModel().addChangeListener(this);
    }

    @Override
    public synchronized void syntheticUnbind() {
        this.boundScrollBar.removePropertyChangeListener("model", this);
        this.boundScrollBar.getModel().removeChangeListener(this);
        this.boundScrollBar = null;
    }

    @Override
    public void setTargetBinding(TargetBinding target) {
        super.setTargetBinding(target);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        this.update();
        ((BoundedRangeModel)event.getOldValue()).removeChangeListener(this);
        ((BoundedRangeModel)event.getNewValue()).addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.update();
    }
}

