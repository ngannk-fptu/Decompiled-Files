/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import org.jfree.util.Log;

public class ActionButton
extends JButton {
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;

    public ActionButton() {
    }

    public ActionButton(String text) {
        super(text);
    }

    public ActionButton(String text, Icon icon) {
        super(text, icon);
    }

    public ActionButton(Icon icon) {
        super(icon);
    }

    public ActionButton(Action action) {
        this.setAction(action);
    }

    public Action getAction() {
        return this.action;
    }

    private ActionEnablePropertyChangeHandler getPropertyChangeHandler() {
        if (this.propertyChangeHandler == null) {
            this.propertyChangeHandler = new ActionEnablePropertyChangeHandler();
        }
        return this.propertyChangeHandler;
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (this.getAction() != null) {
            this.getAction().setEnabled(b);
        }
    }

    public void setAction(Action newAction) {
        KeyStroke k;
        Object o;
        Action oldAction = this.getAction();
        if (oldAction != null) {
            this.removeActionListener(oldAction);
            oldAction.removePropertyChangeListener(this.getPropertyChangeHandler());
            o = oldAction.getValue("AcceleratorKey");
            if (o instanceof KeyStroke) {
                k = (KeyStroke)o;
                this.unregisterKeyboardAction(k);
            }
        }
        this.action = newAction;
        if (this.action != null) {
            this.addActionListener(newAction);
            newAction.addPropertyChangeListener(this.getPropertyChangeHandler());
            this.setText((String)newAction.getValue("Name"));
            this.setToolTipText((String)newAction.getValue("ShortDescription"));
            this.setIcon((Icon)newAction.getValue("SmallIcon"));
            this.setEnabled(this.action.isEnabled());
            o = newAction.getValue("MnemonicKey");
            if (o != null) {
                Comparable<Character> c;
                if (o instanceof Character) {
                    c = (Character)o;
                    this.setMnemonic(((Character)c).charValue());
                } else if (o instanceof Integer) {
                    c = (Integer)o;
                    this.setMnemonic((Integer)c);
                }
            }
            if ((o = newAction.getValue("AcceleratorKey")) instanceof KeyStroke) {
                k = (KeyStroke)o;
                this.registerKeyboardAction(newAction, k, 2);
            }
        }
    }

    private class ActionEnablePropertyChangeHandler
    implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            try {
                Object o;
                if (event.getPropertyName().equals("enabled")) {
                    ActionButton.this.setEnabled(ActionButton.this.getAction().isEnabled());
                } else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionButton.this.setIcon((Icon)ActionButton.this.getAction().getValue("SmallIcon"));
                } else if (event.getPropertyName().equals("Name")) {
                    ActionButton.this.setText((String)ActionButton.this.getAction().getValue("Name"));
                } else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionButton.this.setToolTipText((String)ActionButton.this.getAction().getValue("ShortDescription"));
                }
                Action ac = ActionButton.this.getAction();
                if (event.getPropertyName().equals("AcceleratorKey")) {
                    Object o2;
                    KeyStroke oldVal = (KeyStroke)event.getOldValue();
                    if (oldVal != null) {
                        ActionButton.this.unregisterKeyboardAction(oldVal);
                    }
                    if ((o2 = ac.getValue("AcceleratorKey")) instanceof KeyStroke) {
                        KeyStroke k = (KeyStroke)o2;
                        ActionButton.this.registerKeyboardAction(ac, k, 2);
                    }
                } else if (event.getPropertyName().equals("MnemonicKey") && (o = ac.getValue("MnemonicKey")) != null) {
                    if (o instanceof Character) {
                        Character c = (Character)o;
                        ActionButton.this.setMnemonic(c.charValue());
                    } else if (o instanceof Integer) {
                        Integer c = (Integer)o;
                        ActionButton.this.setMnemonic(c);
                    }
                }
            }
            catch (Exception e) {
                Log.warn("Error on PropertyChange in ActionButton: ", e);
            }
        }
    }
}

