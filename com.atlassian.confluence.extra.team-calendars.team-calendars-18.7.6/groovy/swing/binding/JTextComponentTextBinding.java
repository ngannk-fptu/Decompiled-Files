/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.swing.binding.AbstractSyntheticBinding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;

class JTextComponentTextBinding
extends AbstractSyntheticBinding
implements PropertyChangeListener,
DocumentListener {
    JTextComponent boundTextComponent;

    public JTextComponentTextBinding(PropertyBinding source, TargetBinding target) {
        super(source, target, JTextComponent.class, "text");
        source.setNonChangeCheck(true);
    }

    @Override
    public synchronized void syntheticBind() {
        this.boundTextComponent = (JTextComponent)((PropertyBinding)this.sourceBinding).getBean();
        this.boundTextComponent.addPropertyChangeListener("document", this);
        this.boundTextComponent.getDocument().addDocumentListener(this);
    }

    @Override
    public synchronized void syntheticUnbind() {
        this.boundTextComponent.removePropertyChangeListener("document", this);
        this.boundTextComponent.getDocument().removeDocumentListener(this);
        this.boundTextComponent = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        this.update();
        ((Document)event.getOldValue()).removeDocumentListener(this);
        ((Document)event.getNewValue()).addDocumentListener(this);
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.update();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.update();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.update();
    }
}

