/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractSyntheticBinding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;

class JSliderValueBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
ChangeListener {
    JSlider boundSlider;

    public JSliderValueBinding(PropertyBinding source, TargetBinding target) {
        super(source, target, JSlider.class, "value");
    }

    @Override
    public synchronized void syntheticBind() {
        this.boundSlider = (JSlider)((PropertyBinding)this.sourceBinding).getBean();
        this.boundSlider.addPropertyChangeListener("model", this);
        this.boundSlider.getModel().addChangeListener(this);
    }

    @Override
    public synchronized void syntheticUnbind() {
        this.boundSlider.removePropertyChangeListener("model", this);
        this.boundSlider.getModel().removeChangeListener(this);
        this.boundSlider = null;
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

