/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.jfree.util.Log;

public class ActionMenuItem
extends JMenuItem {
    private Action action;
    private ActionEnablePropertyChangeHandler propertyChangeHandler;

    public ActionMenuItem() {
    }

    public ActionMenuItem(Icon icon) {
        super(icon);
    }

    public ActionMenuItem(String text) {
        super(text);
    }

    public ActionMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    public ActionMenuItem(String text, int i) {
        super(text, i);
    }

    public ActionMenuItem(Action action) {
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
        Action oldAction = this.getAction();
        if (oldAction != null) {
            this.removeActionListener(oldAction);
            oldAction.removePropertyChangeListener(this.getPropertyChangeHandler());
            this.setAccelerator(null);
        }
        this.action = newAction;
        if (this.action != null) {
            this.addActionListener(newAction);
            newAction.addPropertyChangeListener(this.getPropertyChangeHandler());
            this.setText((String)newAction.getValue("Name"));
            this.setToolTipText((String)newAction.getValue("ShortDescription"));
            this.setIcon((Icon)newAction.getValue("SmallIcon"));
            this.setEnabled(this.action.isEnabled());
            Object o = newAction.getValue("MnemonicKey");
            if (o != null) {
                if (o instanceof Character) {
                    Character c = (Character)o;
                    this.setMnemonic(c.charValue());
                } else if (o instanceof Integer) {
                    Integer c = (Integer)o;
                    this.setMnemonic(c);
                }
            } else {
                this.setMnemonic(0);
            }
            o = newAction.getValue("AcceleratorKey");
            if (o instanceof KeyStroke) {
                this.setAccelerator((KeyStroke)o);
            }
        }
    }

    private class ActionEnablePropertyChangeHandler
    implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            try {
                if (event.getPropertyName().equals("enabled")) {
                    ActionMenuItem.this.setEnabled(ActionMenuItem.this.getAction().isEnabled());
                } else if (event.getPropertyName().equals("SmallIcon")) {
                    ActionMenuItem.this.setIcon((Icon)ActionMenuItem.this.getAction().getValue("SmallIcon"));
                } else if (event.getPropertyName().equals("Name")) {
                    ActionMenuItem.this.setText((String)ActionMenuItem.this.getAction().getValue("Name"));
                } else if (event.getPropertyName().equals("ShortDescription")) {
                    ActionMenuItem.this.setToolTipText((String)ActionMenuItem.this.getAction().getValue("ShortDescription"));
                }
                Action ac = ActionMenuItem.this.getAction();
                if (event.getPropertyName().equals("AcceleratorKey")) {
                    ActionMenuItem.this.setAccelerator((KeyStroke)ac.getValue("AcceleratorKey"));
                } else if (event.getPropertyName().equals("MnemonicKey")) {
                    Object o = ac.getValue("MnemonicKey");
                    if (o != null) {
                        if (o instanceof Character) {
                            Character c = (Character)o;
                            ActionMenuItem.this.setMnemonic(c.charValue());
                        } else if (o instanceof Integer) {
                            Integer c = (Integer)o;
                            ActionMenuItem.this.setMnemonic(c);
                        }
                    } else {
                        ActionMenuItem.this.setMnemonic(0);
                    }
                }
            }
            catch (Exception e) {
                Log.warn("Error on PropertyChange in ActionButton: ", e);
            }
        }
    }
}

