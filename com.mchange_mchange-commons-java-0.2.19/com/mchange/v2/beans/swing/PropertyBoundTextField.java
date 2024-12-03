/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.beans.swing.HostBindingInterface;
import com.mchange.v2.beans.swing.PropertyComponentBindingUtility;
import com.mchange.v2.beans.swing.TestBean;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class PropertyBoundTextField
extends JTextField {
    PropertyComponentBindingUtility pcbu;
    HostBindingInterface myHbi = new MyHbi();

    public PropertyBoundTextField(Object object, String string, int n) throws IntrospectionException {
        super(n);
        this.pcbu = new PropertyComponentBindingUtility(this.myHbi, object, string, true);
        this.pcbu.resync();
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
            PropertyBoundTextField propertyBoundTextField = new PropertyBoundTextField(testBean, "theString", 20);
            PropertyBoundTextField propertyBoundTextField2 = new PropertyBoundTextField(testBean, "theInt", 5);
            PropertyBoundTextField propertyBoundTextField3 = new PropertyBoundTextField(testBean, "theFloat", 5);
            JFrame jFrame = new JFrame();
            BoxLayout boxLayout = new BoxLayout(jFrame.getContentPane(), 1);
            jFrame.getContentPane().setLayout(boxLayout);
            jFrame.getContentPane().add(propertyBoundTextField);
            jFrame.getContentPane().add(propertyBoundTextField2);
            jFrame.getContentPane().add(propertyBoundTextField3);
            jFrame.pack();
            jFrame.show();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    class WeChangedListener
    implements ActionListener,
    FocusListener {
        WeChangedListener() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            PropertyBoundTextField.this.pcbu.userModification();
        }

        @Override
        public void focusGained(FocusEvent focusEvent) {
        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            PropertyBoundTextField.this.pcbu.userModification();
        }
    }

    class MyHbi
    implements HostBindingInterface {
        MyHbi() {
        }

        @Override
        public void syncToValue(PropertyEditor propertyEditor, Object object) {
            if (object == null) {
                PropertyBoundTextField.this.setText("");
            } else {
                propertyEditor.setValue(object);
                String string = propertyEditor.getAsText();
                PropertyBoundTextField.this.setText(string);
            }
        }

        @Override
        public void addUserModificationListeners() {
            WeChangedListener weChangedListener = new WeChangedListener();
            PropertyBoundTextField.this.addActionListener(weChangedListener);
            PropertyBoundTextField.this.addFocusListener(weChangedListener);
        }

        @Override
        public Object fetchUserModification(PropertyEditor propertyEditor, Object object) {
            String string = PropertyBoundTextField.this.getText().trim();
            if ("".equals(string)) {
                return null;
            }
            propertyEditor.setAsText(string);
            return propertyEditor.getValue();
        }

        @Override
        public void alertErroneousInput() {
            PropertyBoundTextField.this.getToolkit().beep();
        }
    }
}

