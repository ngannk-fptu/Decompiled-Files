/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import com.mchange.v2.beans.swing.HostBindingInterface;
import com.mchange.v2.beans.swing.PropertyComponentBindingUtility;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.event.ChangeListener;

class SetPropertyElementBoundButtonModel
implements ButtonModel {
    Object putativeElement;
    ButtonModel inner;
    PropertyComponentBindingUtility pcbu;

    public static void bind(AbstractButton[] abstractButtonArray, Object[] objectArray, Object object, String string) throws IntrospectionException {
        int n = abstractButtonArray.length;
        for (int i = 0; i < n; ++i) {
            AbstractButton abstractButton = abstractButtonArray[i];
            abstractButton.setModel(new SetPropertyElementBoundButtonModel(abstractButton.getModel(), object, string, objectArray[i]));
        }
    }

    public SetPropertyElementBoundButtonModel(ButtonModel buttonModel, Object object, String string, Object object2) throws IntrospectionException {
        this.inner = buttonModel;
        this.putativeElement = object2;
        this.pcbu = new PropertyComponentBindingUtility(new MyHbi(), object, string, false);
        this.pcbu.resync();
    }

    @Override
    public boolean isArmed() {
        return this.inner.isArmed();
    }

    @Override
    public boolean isSelected() {
        return this.inner.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return this.inner.isEnabled();
    }

    @Override
    public boolean isPressed() {
        return this.inner.isPressed();
    }

    @Override
    public boolean isRollover() {
        return this.inner.isRollover();
    }

    @Override
    public void setArmed(boolean bl) {
        this.inner.setArmed(bl);
    }

    @Override
    public void setSelected(boolean bl) {
        this.inner.setSelected(bl);
    }

    @Override
    public void setEnabled(boolean bl) {
        this.inner.setEnabled(bl);
    }

    @Override
    public void setPressed(boolean bl) {
        this.inner.setPressed(bl);
    }

    @Override
    public void setRollover(boolean bl) {
        this.inner.setRollover(bl);
    }

    @Override
    public void setMnemonic(int n) {
        this.inner.setMnemonic(n);
    }

    @Override
    public int getMnemonic() {
        return this.inner.getMnemonic();
    }

    @Override
    public void setActionCommand(String string) {
        this.inner.setActionCommand(string);
    }

    @Override
    public String getActionCommand() {
        return this.inner.getActionCommand();
    }

    @Override
    public void setGroup(ButtonGroup buttonGroup) {
        this.inner.setGroup(buttonGroup);
    }

    @Override
    public Object[] getSelectedObjects() {
        return this.inner.getSelectedObjects();
    }

    @Override
    public void addActionListener(ActionListener actionListener) {
        this.inner.addActionListener(actionListener);
    }

    @Override
    public void removeActionListener(ActionListener actionListener) {
        this.inner.removeActionListener(actionListener);
    }

    @Override
    public void addItemListener(ItemListener itemListener) {
        this.inner.addItemListener(itemListener);
    }

    @Override
    public void removeItemListener(ItemListener itemListener) {
        this.inner.removeItemListener(itemListener);
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        this.inner.addChangeListener(changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        this.inner.removeChangeListener(changeListener);
    }

    class MyHbi
    implements HostBindingInterface {
        MyHbi() {
        }

        @Override
        public void syncToValue(PropertyEditor propertyEditor, Object object) {
            if (object == null) {
                SetPropertyElementBoundButtonModel.this.setSelected(false);
            } else {
                SetPropertyElementBoundButtonModel.this.setSelected(((Set)object).contains(SetPropertyElementBoundButtonModel.this.putativeElement));
            }
        }

        @Override
        public void addUserModificationListeners() {
            ActionListener actionListener = new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    SetPropertyElementBoundButtonModel.this.pcbu.userModification();
                }
            };
            SetPropertyElementBoundButtonModel.this.addActionListener(actionListener);
        }

        @Override
        public Object fetchUserModification(PropertyEditor propertyEditor, Object object) {
            HashSet<Object> hashSet;
            if (object == null) {
                if (!SetPropertyElementBoundButtonModel.this.isSelected()) {
                    return null;
                }
                hashSet = new HashSet();
            } else {
                hashSet = new HashSet<Object>((Set)object);
            }
            if (SetPropertyElementBoundButtonModel.this.isSelected()) {
                hashSet.add(SetPropertyElementBoundButtonModel.this.putativeElement);
            } else {
                hashSet.remove(SetPropertyElementBoundButtonModel.this.putativeElement);
            }
            return hashSet;
        }

        @Override
        public void alertErroneousInput() {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}

