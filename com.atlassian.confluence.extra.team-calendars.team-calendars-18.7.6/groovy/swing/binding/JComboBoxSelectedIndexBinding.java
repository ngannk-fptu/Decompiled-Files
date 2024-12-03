/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractSyntheticBinding;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComboBox;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;

class JComboBoxSelectedIndexBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
ItemListener {
    JComboBox boundComboBox;

    public JComboBoxSelectedIndexBinding(PropertyBinding source, TargetBinding target) {
        super(source, target, JComboBox.class, "selectedIndex");
    }

    @Override
    public synchronized void syntheticBind() {
        this.boundComboBox = (JComboBox)((PropertyBinding)this.sourceBinding).getBean();
        this.boundComboBox.addPropertyChangeListener("model", this);
        this.boundComboBox.addItemListener(this);
    }

    @Override
    public synchronized void syntheticUnbind() {
        this.boundComboBox.removePropertyChangeListener("model", this);
        this.boundComboBox.removeItemListener(this);
        this.boundComboBox = null;
    }

    @Override
    public void setTargetBinding(TargetBinding target) {
        super.setTargetBinding(target);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        this.update();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        this.update();
    }
}

