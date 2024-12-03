/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import com.mchange.v2.beans.swing.HostBindingInterface;
import com.mchange.v2.beans.swing.PropertyComponentBindingUtility;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;

class PropertyBoundButtonGroup
extends ButtonGroup {
    PropertyComponentBindingUtility pcbu;
    HostBindingInterface myHbi;
    WeChangedListener wcl = new WeChangedListener();
    Map buttonsModelsToValues = new HashMap();
    Map valuesToButtonModels = new HashMap();
    JButton fakeButton = new JButton();

    public PropertyBoundButtonGroup(Object object, String string) throws IntrospectionException {
        this.myHbi = new MyHbi();
        this.pcbu = new PropertyComponentBindingUtility(this.myHbi, object, string, false);
        this.add(this.fakeButton, null);
        this.pcbu.resync();
    }

    public void add(AbstractButton abstractButton, Object object) {
        super.add(abstractButton);
        this.buttonsModelsToValues.put(abstractButton.getModel(), object);
        this.valuesToButtonModels.put(object, abstractButton.getModel());
        abstractButton.addActionListener(this.wcl);
        this.pcbu.resync();
    }

    @Override
    public void add(AbstractButton abstractButton) {
        System.err.println(this + "Warning: The button '" + abstractButton + "' has been implicitly associated with a null value!");
        System.err.println("To avoid this warning, please use public void add(AbstractButton button, Object associatedValue)");
        System.err.println("instead of the single-argument add method.");
        super.add(abstractButton);
        abstractButton.addActionListener(this.wcl);
        this.pcbu.resync();
    }

    @Override
    public void remove(AbstractButton abstractButton) {
        abstractButton.removeActionListener(this.wcl);
        super.remove(abstractButton);
    }

    class WeChangedListener
    implements ActionListener {
        WeChangedListener() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            PropertyBoundButtonGroup.this.pcbu.userModification();
        }
    }

    class MyHbi
    implements HostBindingInterface {
        MyHbi() {
        }

        @Override
        public void syncToValue(PropertyEditor propertyEditor, Object object) {
            ButtonModel buttonModel = (ButtonModel)PropertyBoundButtonGroup.this.valuesToButtonModels.get(object);
            if (buttonModel != null) {
                PropertyBoundButtonGroup.this.setSelected(buttonModel, true);
            } else {
                PropertyBoundButtonGroup.this.setSelected(PropertyBoundButtonGroup.this.fakeButton.getModel(), true);
            }
        }

        @Override
        public void addUserModificationListeners() {
        }

        @Override
        public Object fetchUserModification(PropertyEditor propertyEditor, Object object) {
            ButtonModel buttonModel = PropertyBoundButtonGroup.this.getSelection();
            return PropertyBoundButtonGroup.this.buttonsModelsToValues.get(buttonModel);
        }

        @Override
        public void alertErroneousInput() {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}

