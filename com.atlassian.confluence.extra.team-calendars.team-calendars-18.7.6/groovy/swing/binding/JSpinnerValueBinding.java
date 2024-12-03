/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractSyntheticBinding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;

class JSpinnerValueBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
ChangeListener {
    JSpinner boundSlider;

    public JSpinnerValueBinding(PropertyBinding source, TargetBinding target) {
        super(source, target, JSpinner.class, "value");
    }

    @Override
    public synchronized void syntheticBind() {
        this.boundSlider = (JSpinner)((PropertyBinding)this.sourceBinding).getBean();
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
        ((SpinnerModel)event.getOldValue()).removeChangeListener(this);
        ((SpinnerModel)event.getNewValue()).addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.update();
    }
}

