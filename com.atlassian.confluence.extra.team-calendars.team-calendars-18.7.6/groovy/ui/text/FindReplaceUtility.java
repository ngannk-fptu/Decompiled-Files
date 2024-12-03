/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.EventListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;

public final class FindReplaceUtility {
    public static final String FIND_ACTION_COMMAND = "Find";
    public static final String REPLACE_ACTION_COMMAND = "Replace";
    public static final String REPLACE_ALL_ACTION_COMMAND = "Replace All";
    public static final String CLOSE_ACTION_COMMAND = "Close";
    public static final Action FIND_ACTION = new FindAction();
    private static final JDialog FIND_REPLACE_DIALOG = new JDialog();
    private static final JPanel TEXT_FIELD_PANEL = new JPanel(new GridLayout(2, 1));
    private static final JPanel ENTRY_PANEL = new JPanel();
    private static final JPanel FIND_PANEL = new JPanel();
    private static final JLabel FIND_LABEL = new JLabel("Find What:    ");
    private static final JComboBox FIND_FIELD = new JComboBox();
    private static final JPanel REPLACE_PANEL = new JPanel();
    private static final JLabel REPLACE_LABEL = new JLabel("Replace With:");
    private static final JComboBox REPLACE_FIELD = new JComboBox();
    private static final JPanel BUTTON_PANEL = new JPanel();
    private static final JButton FIND_BUTTON = new JButton();
    private static final JButton REPLACE_BUTTON = new JButton();
    private static final JButton REPLACE_ALL_BUTTON = new JButton();
    private static final JButton CLOSE_BUTTON = new JButton();
    private static final Action CLOSE_ACTION = new CloseAction();
    private static final Action REPLACE_ACTION = new ReplaceAction();
    private static final JPanel CHECK_BOX_PANEL = new JPanel(new GridLayout(3, 1));
    private static final JCheckBox MATCH_CASE_CHECKBOX = new JCheckBox("Match Case      ");
    private static final JCheckBox IS_BACKWARDS_CHECKBOX = new JCheckBox("Search Backwards");
    private static final JCheckBox WRAP_SEARCH_CHECKBOX = new JCheckBox("Wrap Search     ");
    private static JTextComponent textComponent;
    private static AttributeSet attributeSet;
    private static int findReplaceCount;
    private static String lastAction;
    private static final EventListenerList EVENT_LISTENER_LIST;
    private static final Segment SEGMENT;
    private static final FocusAdapter TEXT_FOCUS_LISTENER;

    private FindReplaceUtility() {
    }

    public static void addTextListener(TextListener tl) {
        EVENT_LISTENER_LIST.add(TextListener.class, tl);
    }

    private static void fireTextEvent() {
        EventListener[] lstrs = EVENT_LISTENER_LIST.getListeners(TextListener.class);
        if (lstrs != null && lstrs.length > 0) {
            TextEvent te = new TextEvent(FIND_REPLACE_DIALOG, 900);
            for (int i = 0; i < lstrs.length; ++i) {
                ((TextListener)lstrs[i]).textValueChanged(te);
            }
        }
    }

    public static String getLastAction() {
        return lastAction;
    }

    public static int getReplacementCount() {
        return findReplaceCount;
    }

    public static String getSearchText() {
        return (String)FIND_FIELD.getSelectedItem();
    }

    public static void registerTextComponent(JTextComponent textComponent) {
        textComponent.addFocusListener(TEXT_FOCUS_LISTENER);
    }

    public static void removeTextListener(TextListener tl) {
        EVENT_LISTENER_LIST.remove(TextListener.class, tl);
    }

    private static int findNext(boolean reverse, int pos) {
        boolean backwards = IS_BACKWARDS_CHECKBOX.isSelected();
        backwards = backwards ? !reverse : reverse;
        String pattern = (String)FIND_FIELD.getSelectedItem();
        if (pattern != null && pattern.length() > 0) {
            try {
                Document doc = textComponent.getDocument();
                doc.getText(0, doc.getLength(), SEGMENT);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            char first = backwards ? pattern.charAt(pattern.length() - 1) : pattern.charAt(0);
            char oppFirst = Character.isUpperCase(first) ? Character.toLowerCase(first) : Character.toUpperCase(first);
            int start = pos += textComponent.getSelectedText() == null ? (backwards ? -1 : 1) : 0;
            boolean wrapped = WRAP_SEARCH_CHECKBOX.isSelected();
            int end = backwards ? 0 : SEGMENT.getEndIndex();
            int n = backwards ? -1 : 1;
            int length = textComponent.getDocument().getLength();
            if ((pos += n) > length) {
                pos = wrapped ? 0 : length;
            }
            boolean found = false;
            while (!found && (backwards ? pos > end : pos < end)) {
                boolean bl = found = !MATCH_CASE_CHECKBOX.isSelected() && FindReplaceUtility.SEGMENT.array[pos] == oppFirst;
                boolean bl2 = found ? found : (found = FindReplaceUtility.SEGMENT.array[pos] == first);
                if (found) {
                    pos += backwards ? -(pattern.length() - 1) : 0;
                    for (int i = 0; found && i < pattern.length(); ++i) {
                        char c = pattern.charAt(i);
                        boolean bl3 = found = FindReplaceUtility.SEGMENT.array[pos + i] == c;
                        if (MATCH_CASE_CHECKBOX.isSelected() || found) continue;
                        c = Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c);
                        found = FindReplaceUtility.SEGMENT.array[pos + i] == c;
                    }
                }
                if (found || (pos += backwards ? -1 : 1) != end || !wrapped) continue;
                pos = backwards ? SEGMENT.getEndIndex() : 0;
                end = start;
                wrapped = false;
            }
            pos = found ? pos : -1;
        }
        return pos;
    }

    private static void setListStrings() {
        String replaceObject;
        Object findObject = FIND_FIELD.getSelectedItem();
        String string = replaceObject = REPLACE_FIELD.isShowing() ? (String)REPLACE_FIELD.getSelectedItem() : "";
        if (findObject != null && replaceObject != null) {
            int i;
            boolean found = false;
            for (i = 0; !found && i < FIND_FIELD.getItemCount(); ++i) {
                found = FIND_FIELD.getItemAt(i).equals(findObject);
            }
            if (!found) {
                FIND_FIELD.insertItemAt(findObject, 0);
                if (FIND_FIELD.getItemCount() > 7) {
                    FIND_FIELD.removeItemAt(7);
                }
            }
            if (REPLACE_FIELD.isShowing()) {
                found = false;
                for (i = 0; !found && i < REPLACE_FIELD.getItemCount(); ++i) {
                    found = REPLACE_FIELD.getItemAt(i).equals(findObject);
                }
                if (!found) {
                    REPLACE_FIELD.insertItemAt(replaceObject, 0);
                    if (REPLACE_FIELD.getItemCount() > 7) {
                        REPLACE_FIELD.removeItemAt(7);
                    }
                }
            }
        }
    }

    public static void showDialog() {
        FindReplaceUtility.showDialog(false);
    }

    public static void showDialog(boolean isReplace) {
        String title = isReplace ? REPLACE_ACTION_COMMAND : FIND_ACTION_COMMAND;
        FIND_REPLACE_DIALOG.setTitle(title);
        String text = textComponent.getSelectedText();
        if (text == null) {
            text = "";
        }
        FIND_FIELD.getEditor().setItem(text);
        FIND_FIELD.getEditor().selectAll();
        REPLACE_PANEL.setVisible(isReplace);
        REPLACE_ALL_BUTTON.setVisible(isReplace);
        CLOSE_BUTTON.setVisible(isReplace);
        Action action = isReplace ? REPLACE_ACTION : CLOSE_ACTION;
        REPLACE_BUTTON.setAction(action);
        REPLACE_BUTTON.setPreferredSize(null);
        Dimension d = isReplace ? REPLACE_ALL_BUTTON.getPreferredSize() : REPLACE_BUTTON.getPreferredSize();
        FIND_BUTTON.setPreferredSize(d);
        REPLACE_BUTTON.setPreferredSize(d);
        CLOSE_BUTTON.setPreferredSize(d);
        FIND_REPLACE_DIALOG.invalidate();
        FIND_REPLACE_DIALOG.repaint();
        FIND_REPLACE_DIALOG.pack();
        Frame[] frames = Frame.getFrames();
        for (int i = 0; i < frames.length; ++i) {
            if (!frames[i].isFocused()) continue;
            FIND_REPLACE_DIALOG.setLocationRelativeTo(frames[i]);
        }
        FIND_REPLACE_DIALOG.setVisible(true);
        FIND_FIELD.requestFocusInWindow();
    }

    public static void unregisterTextComponent(JTextComponent textComponent) {
        textComponent.removeFocusListener(TEXT_FOCUS_LISTENER);
    }

    public static void dispose() {
        FIND_REPLACE_DIALOG.dispose();
    }

    static {
        EVENT_LISTENER_LIST = new EventListenerList();
        SEGMENT = new Segment();
        TEXT_FOCUS_LISTENER = new FocusAdapter(){

            @Override
            public void focusGained(FocusEvent fe) {
                textComponent = (JTextComponent)fe.getSource();
                attributeSet = textComponent.getDocument().getDefaultRootElement().getAttributes();
            }
        };
        FIND_REPLACE_DIALOG.setResizable(false);
        FIND_REPLACE_DIALOG.setDefaultCloseOperation(2);
        KeyStroke.getKeyStroke("enter");
        KeyAdapter keyAdapter = new KeyAdapter(){

            @Override
            public void keyTyped(KeyEvent ke) {
                if (ke.getKeyChar() == '\n') {
                    FIND_BUTTON.doClick();
                }
            }
        };
        FIND_PANEL.setLayout(new FlowLayout(2));
        FIND_PANEL.add(FIND_LABEL);
        FIND_PANEL.add(FIND_FIELD);
        FIND_FIELD.addItem("");
        FIND_FIELD.setEditable(true);
        FIND_FIELD.getEditor().getEditorComponent().addKeyListener(keyAdapter);
        Dimension d = FIND_FIELD.getPreferredSize();
        d.width = 225;
        FIND_FIELD.setPreferredSize(d);
        REPLACE_PANEL.add(REPLACE_LABEL);
        REPLACE_PANEL.add(REPLACE_FIELD);
        REPLACE_FIELD.setEditable(true);
        REPLACE_FIELD.getEditor().getEditorComponent().addKeyListener(keyAdapter);
        REPLACE_FIELD.setPreferredSize(d);
        TEXT_FIELD_PANEL.setLayout(new BoxLayout(TEXT_FIELD_PANEL, 1));
        TEXT_FIELD_PANEL.add(FIND_PANEL);
        TEXT_FIELD_PANEL.add(REPLACE_PANEL);
        ENTRY_PANEL.add(TEXT_FIELD_PANEL);
        FIND_REPLACE_DIALOG.getContentPane().add((Component)ENTRY_PANEL, "West");
        CHECK_BOX_PANEL.add(MATCH_CASE_CHECKBOX);
        CHECK_BOX_PANEL.add(IS_BACKWARDS_CHECKBOX);
        CHECK_BOX_PANEL.add(WRAP_SEARCH_CHECKBOX);
        ENTRY_PANEL.add(CHECK_BOX_PANEL);
        ENTRY_PANEL.setLayout(new BoxLayout(ENTRY_PANEL, 1));
        REPLACE_ALL_BUTTON.setAction(new ReplaceAllAction());
        REPLACE_ALL_BUTTON.setHorizontalAlignment(0);
        d = REPLACE_ALL_BUTTON.getPreferredSize();
        BUTTON_PANEL.setLayout(new BoxLayout(BUTTON_PANEL, 1));
        FIND_BUTTON.setAction(FIND_ACTION);
        FIND_BUTTON.setPreferredSize(d);
        FIND_BUTTON.setHorizontalAlignment(0);
        JPanel panel = new JPanel();
        panel.add(FIND_BUTTON);
        BUTTON_PANEL.add(panel);
        FIND_REPLACE_DIALOG.getRootPane().setDefaultButton(FIND_BUTTON);
        REPLACE_BUTTON.setAction(REPLACE_ACTION);
        REPLACE_BUTTON.setPreferredSize(d);
        REPLACE_BUTTON.setHorizontalAlignment(0);
        panel = new JPanel();
        panel.add(REPLACE_BUTTON);
        BUTTON_PANEL.add(panel);
        panel = new JPanel();
        panel.add(REPLACE_ALL_BUTTON);
        BUTTON_PANEL.add(panel);
        CLOSE_BUTTON.setAction(CLOSE_ACTION);
        CLOSE_BUTTON.setPreferredSize(d);
        CLOSE_BUTTON.setHorizontalAlignment(0);
        panel = new JPanel();
        panel.add(CLOSE_BUTTON);
        BUTTON_PANEL.add(panel);
        FIND_REPLACE_DIALOG.getContentPane().add(BUTTON_PANEL);
        KeyStroke stroke = (KeyStroke)CLOSE_ACTION.getValue("AcceleratorKey");
        JRootPane rPane = FIND_REPLACE_DIALOG.getRootPane();
        rPane.getInputMap(2).put(stroke, "exit");
        rPane.getActionMap().put("exit", CLOSE_ACTION);
    }

    private static class CloseAction
    extends AbstractAction {
        public CloseAction() {
            this.putValue("Name", FindReplaceUtility.CLOSE_ACTION_COMMAND);
            this.putValue("ActionCommandKey", FindReplaceUtility.CLOSE_ACTION_COMMAND);
            this.putValue("MnemonicKey", 67);
            this.putValue("AcceleratorKey", KeyStroke.getKeyStroke("ESCAPE"));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            FIND_REPLACE_DIALOG.dispose();
        }
    }

    private static class ReplaceAllAction
    extends AbstractAction {
        public ReplaceAllAction() {
            this.putValue("Name", FindReplaceUtility.REPLACE_ALL_ACTION_COMMAND);
            this.putValue("ActionCommandKey", FindReplaceUtility.REPLACE_ALL_ACTION_COMMAND);
            this.putValue("MnemonicKey", 65);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            lastAction = ae.getActionCommand();
            findReplaceCount = 0;
            int last = textComponent.getSelectedText() == null ? textComponent.getCaretPosition() : textComponent.getSelectionStart();
            int pos = FindReplaceUtility.findNext(false, last - 1);
            String find = (String)FIND_FIELD.getSelectedItem();
            String replace = (String)REPLACE_FIELD.getSelectedItem();
            String string = replace = replace == null ? "" : replace;
            while (pos > -1) {
                Document doc = textComponent.getDocument();
                try {
                    doc.remove(pos, find.length());
                    doc.insertString(pos, replace, attributeSet);
                    last = pos;
                    pos = FindReplaceUtility.findNext(false, pos);
                }
                catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
                findReplaceCount++;
            }
            if (pos > -1) {
                textComponent.select(pos, pos + find.length());
            } else {
                textComponent.setCaretPosition(last + replace.length());
            }
            FindReplaceUtility.setListStrings();
            FindReplaceUtility.fireTextEvent();
        }
    }

    private static class ReplaceAction
    extends AbstractAction {
        public ReplaceAction() {
            this.putValue("Name", FindReplaceUtility.REPLACE_ACTION_COMMAND);
            this.putValue("ActionCommandKey", FindReplaceUtility.REPLACE_ACTION_COMMAND);
            this.putValue("MnemonicKey", 82);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            lastAction = ae.getActionCommand();
            findReplaceCount = 0;
            int pos = textComponent.getSelectedText() == null ? textComponent.getCaretPosition() : textComponent.getSelectionStart();
            pos = FindReplaceUtility.findNext(false, pos - 1);
            if (pos > -1) {
                String find = (String)FIND_FIELD.getSelectedItem();
                String replace = (String)REPLACE_FIELD.getSelectedItem();
                replace = replace == null ? "" : replace;
                Document doc = textComponent.getDocument();
                try {
                    doc.remove(pos, find.length());
                    doc.insertString(pos, replace, attributeSet);
                    int last = pos;
                    pos = FindReplaceUtility.findNext(false, pos);
                    if (pos > -1) {
                        textComponent.select(pos, pos + find.length());
                    } else {
                        textComponent.setCaretPosition(last + replace.length());
                    }
                }
                catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
                findReplaceCount = 1;
            }
            FindReplaceUtility.setListStrings();
            FindReplaceUtility.fireTextEvent();
        }
    }

    private static class FindAction
    extends AbstractAction {
        public FindAction() {
            this.putValue("Name", FindReplaceUtility.FIND_ACTION_COMMAND);
            this.putValue("ActionCommandKey", FindReplaceUtility.FIND_ACTION_COMMAND);
            this.putValue("MnemonicKey", 70);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            lastAction = FindReplaceUtility.FIND_ACTION_COMMAND;
            findReplaceCount = 0;
            if (!FIND_REPLACE_DIALOG.isVisible() || FIND_REPLACE_DIALOG.getTitle().equals(FindReplaceUtility.FIND_ACTION_COMMAND)) {
                // empty if block
            }
            int pos = textComponent.getSelectedText() == null ? textComponent.getCaretPosition() : textComponent.getSelectionStart();
            boolean reverse = (ae.getModifiers() & 1) != 0;
            pos = FindReplaceUtility.findNext(reverse, pos);
            if (pos > -1) {
                String pattern = (String)FIND_FIELD.getSelectedItem();
                textComponent.select(pos, pos + pattern.length());
                findReplaceCount = 1;
            }
            FindReplaceUtility.setListStrings();
            FindReplaceUtility.fireTextEvent();
        }
    }
}

