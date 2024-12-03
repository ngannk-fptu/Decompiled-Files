/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.beans.swing.HostBindingInterface;
import com.mchange.v2.beans.swing.PropertyBoundTextField;
import com.mchange.v2.beans.swing.PropertyComponentBindingUtility;
import com.mchange.v2.beans.swing.TestBean;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class PropertyBoundComboBox
extends JComboBox {
    PropertyComponentBindingUtility pcbu;
    MyHbi myHbi = new MyHbi();
    Object itemsSrc = null;
    Object nullObject = null;

    public PropertyBoundComboBox(Object object, String string, Object object2, Object object3) throws IntrospectionException {
        this.pcbu = new PropertyComponentBindingUtility(this.myHbi, object, string, false);
        this.nullObject = object3;
        this.setItemsSrc(object2);
    }

    public Object getItemsSrc() {
        return this.itemsSrc;
    }

    public void setItemsSrc(Object object) {
        this.myHbi.suspendNotifications();
        this.removeAllItems();
        if (object instanceof Object[]) {
            Object[] objectArray = (Object[])object;
            int n = objectArray.length;
            for (int i = 0; i < n; ++i) {
                this.addItem(objectArray[i]);
            }
        } else if (object instanceof Collection) {
            Collection collection = (Collection)object;
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                this.addItem(iterator.next());
            }
        } else if (object instanceof ComboBoxModel) {
            this.setModel((ComboBoxModel)object);
        } else {
            throw new IllegalArgumentException("itemsSrc must be an Object[], a Collection, or a ComboBoxModel");
        }
        this.itemsSrc = object;
        this.pcbu.resync();
        this.myHbi.resumeNotifications();
    }

    public void setNullObject(Object object) {
        this.nullObject = null;
        this.pcbu.resync();
    }

    public Object getNullObject() {
        return this.nullObject;
    }

    public static void main(String[] stringArray) {
        try {
            TestBean testBean = new TestBean();
            PropertyChangeListener propertyChangeListener = new PropertyChangeListener(){

                @Override
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    BeansUtils.debugShowPropertyChange(propertyChangeEvent);
                }
            };
            testBean.addPropertyChangeListener(propertyChangeListener);
            PropertyBoundComboBox propertyBoundComboBox = new PropertyBoundComboBox(testBean, "theString", new String[]{"SELECT", "Frog", "Fish", "Puppy"}, "SELECT");
            PropertyBoundTextField propertyBoundTextField = new PropertyBoundTextField(testBean, "theInt", 5);
            PropertyBoundTextField propertyBoundTextField2 = new PropertyBoundTextField(testBean, "theFloat", 5);
            JFrame jFrame = new JFrame();
            BoxLayout boxLayout = new BoxLayout(jFrame.getContentPane(), 1);
            jFrame.getContentPane().setLayout(boxLayout);
            jFrame.getContentPane().add(propertyBoundComboBox);
            jFrame.getContentPane().add(propertyBoundTextField);
            jFrame.getContentPane().add(propertyBoundTextField2);
            jFrame.pack();
            jFrame.show();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    class MyHbi
    implements HostBindingInterface {
        boolean suspend_notice = false;

        MyHbi() {
        }

        public void suspendNotifications() {
            this.suspend_notice = true;
        }

        public void resumeNotifications() {
            this.suspend_notice = false;
        }

        @Override
        public void syncToValue(PropertyEditor propertyEditor, Object object) {
            if (object == null) {
                PropertyBoundComboBox.this.setSelectedItem(PropertyBoundComboBox.this.nullObject);
            } else {
                PropertyBoundComboBox.this.setSelectedItem(object);
            }
        }

        @Override
        public void addUserModificationListeners() {
            ItemListener itemListener = new ItemListener(){

                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    if (!MyHbi.this.suspend_notice) {
                        PropertyBoundComboBox.this.pcbu.userModification();
                    }
                }
            };
            PropertyBoundComboBox.this.addItemListener(itemListener);
        }

        @Override
        public Object fetchUserModification(PropertyEditor propertyEditor, Object object) {
            Object object2 = PropertyBoundComboBox.this.getSelectedItem();
            if (PropertyBoundComboBox.this.nullObject != null && PropertyBoundComboBox.this.nullObject.equals(object2)) {
                object2 = null;
            }
            return object2;
        }

        @Override
        public void alertErroneousInput() {
            PropertyBoundComboBox.this.getToolkit().beep();
        }
    }
}

