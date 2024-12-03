/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.apache.axis.utils.StringUtils;
import org.apache.axis.utils.XMLUtils;

public class tcpmon
extends JFrame {
    private JTabbedPane notebook = new JTabbedPane();
    private static final int STATE_COLUMN = 0;
    private static final int TIME_COLUMN = 1;
    private static final int INHOST_COLUMN = 2;
    private static final int OUTHOST_COLUMN = 3;
    private static final int REQ_COLUMN = 4;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8080;
    private static ResourceBundle messages = null;

    public tcpmon(int listenPort, String targetHost, int targetPort, boolean embedded) {
        super(tcpmon.getMessage("tcpmon00", "TCPMonitor"));
        this.getContentPane().add(this.notebook);
        new AdminPage(this.notebook, tcpmon.getMessage("admin00", "Admin"));
        if (listenPort != 0) {
            Listener l = null;
            l = targetHost == null ? new Listener(this.notebook, null, listenPort, targetHost, targetPort, true, null) : new Listener(this.notebook, null, listenPort, targetHost, targetPort, false, null);
            this.notebook.setSelectedIndex(1);
            l.HTTPProxyHost = System.getProperty("http.proxyHost");
            if (l.HTTPProxyHost != null && l.HTTPProxyHost.equals("")) {
                l.HTTPProxyHost = null;
            }
            if (l.HTTPProxyHost != null) {
                String tmp = System.getProperty("http.proxyPort");
                if (tmp != null && tmp.equals("")) {
                    tmp = null;
                }
                l.HTTPProxyPort = tmp == null ? 80 : Integer.parseInt(tmp);
            }
        }
        if (!embedded) {
            this.setDefaultCloseOperation(3);
        }
        this.pack();
        this.setSize(600, 600);
        this.setVisible(true);
    }

    public tcpmon(int listenPort, String targetHost, int targetPort) {
        this(listenPort, targetHost, targetPort, false);
    }

    private static void setupLookAndFeel(boolean nativeLookAndFeel) throws Exception {
        String lafProperty;
        String classname = UIManager.getCrossPlatformLookAndFeelClassName();
        if (nativeLookAndFeel) {
            classname = UIManager.getSystemLookAndFeelClassName();
        }
        if ((lafProperty = System.getProperty("tcpmon.laf", "")).length() > 0) {
            classname = lafProperty;
        }
        try {
            UIManager.setLookAndFeel(classname);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            tcpmon.setupLookAndFeel(true);
            if (args.length == 3) {
                int p1 = Integer.parseInt(args[0]);
                int p2 = Integer.parseInt(args[2]);
                new tcpmon(p1, args[1], p2);
            } else if (args.length == 1) {
                int p1 = Integer.parseInt(args[0]);
                new tcpmon(p1, null, 0);
            } else if (args.length != 0) {
                System.err.println(tcpmon.getMessage("usage00", "Usage:") + " tcpmon [listenPort targetHost targetPort]\n");
            } else {
                new tcpmon(0, null, 0);
            }
        }
        catch (Throwable exp) {
            exp.printStackTrace();
        }
    }

    public static String getMessage(String key, String defaultMsg) {
        try {
            if (messages == null) {
                tcpmon.initializeMessages();
            }
            return messages.getString(key);
        }
        catch (Throwable t) {
            return defaultMsg;
        }
    }

    private static void initializeMessages() {
        messages = ResourceBundle.getBundle("org.apache.axis.utils.tcpmon");
    }

    static class HostnameField
    extends RestrictedTextField {
        private static final String VALID_TEXT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZYZ-.";

        public HostnameField(int columns) {
            super(columns, VALID_TEXT);
        }

        public HostnameField() {
            super(VALID_TEXT);
        }
    }

    static class NumberField
    extends RestrictedTextField {
        private static final String VALID_TEXT = "0123456789";

        public NumberField() {
            super(VALID_TEXT);
        }

        public NumberField(int columns) {
            super(columns, VALID_TEXT);
        }

        public int getValue(int def) {
            int result = def;
            String text = this.getText();
            if (text != null && text.length() != 0) {
                try {
                    result = Integer.parseInt(text);
                }
                catch (NumberFormatException e) {
                    // empty catch block
                }
            }
            return result;
        }

        public void setValue(int value) {
            this.setText(Integer.toString(value));
        }
    }

    static class RestrictedTextField
    extends JTextField {
        protected String validText;

        public RestrictedTextField(String validText) {
            this.setValidText(validText);
        }

        public RestrictedTextField(int columns, String validText) {
            super(columns);
            this.setValidText(validText);
        }

        public RestrictedTextField(String text, String validText) {
            super(text);
            this.setValidText(validText);
        }

        public RestrictedTextField(String text, int columns, String validText) {
            super(text, columns);
            this.setValidText(validText);
        }

        private void setValidText(String validText) {
            this.validText = validText;
        }

        public Document createDefaultModel() {
            return new RestrictedDocument();
        }

        class RestrictedDocument
        extends PlainDocument {
            public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {
                if (string == null) {
                    return;
                }
                int len = string.length();
                StringBuffer buffer = new StringBuffer(string.length());
                for (int i = 0; i < len; ++i) {
                    char ch = string.charAt(i);
                    if (RestrictedTextField.this.validText.indexOf(ch) < 0) continue;
                    buffer.append(ch);
                }
                super.insertString(offset, new String(buffer), attributes);
            }
        }
    }

    class Listener
    extends JPanel {
        public Socket inputSocket = null;
        public Socket outputSocket = null;
        public JTextField portField = null;
        public JTextField hostField = null;
        public JTextField tPortField = null;
        public JCheckBox isProxyBox = null;
        public JButton stopButton = null;
        public JButton removeButton = null;
        public JButton removeAllButton = null;
        public JCheckBox xmlFormatBox = null;
        public JCheckBox numericBox = null;
        public JButton saveButton = null;
        public JButton resendButton = null;
        public JButton switchButton = null;
        public JButton closeButton = null;
        public JTable connectionTable = null;
        public DefaultTableModel tableModel = null;
        public JSplitPane outPane = null;
        public ServerSocket sSocket = null;
        public SocketWaiter sw = null;
        public JPanel leftPanel = null;
        public JPanel rightPanel = null;
        public JTabbedPane notebook = null;
        public String HTTPProxyHost = null;
        public int HTTPProxyPort = 80;
        public int delayBytes = 0;
        public int delayTime = 0;
        public SlowLinkSimulator slowLink;
        public final Vector connections = new Vector();

        public Listener(JTabbedPane _notebook, String name, int listenPort, String host, int targetPort, boolean isProxy, SlowLinkSimulator slowLink) {
            this.notebook = _notebook;
            if (name == null) {
                name = tcpmon.getMessage("port01", "Port") + " " + listenPort;
            }
            this.slowLink = slowLink != null ? slowLink : new SlowLinkSimulator(0, 0);
            this.setLayout(new BorderLayout());
            JPanel top = new JPanel();
            top.setLayout(new BoxLayout(top, 0));
            top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            String start = tcpmon.getMessage("start00", "Start");
            this.stopButton = new JButton(start);
            top.add(this.stopButton);
            top.add(Box.createRigidArea(new Dimension(5, 0)));
            top.add(new JLabel("  " + tcpmon.getMessage("listenPort01", "Listen Port:") + " ", 4));
            this.portField = new JTextField("" + listenPort, 4);
            top.add(this.portField);
            top.add(new JLabel("  " + tcpmon.getMessage("host00", "Host:"), 4));
            this.hostField = new JTextField(host, 30);
            top.add(this.hostField);
            top.add(new JLabel("  " + tcpmon.getMessage("port02", "Port:") + " ", 4));
            this.tPortField = new JTextField("" + targetPort, 4);
            top.add(this.tPortField);
            top.add(Box.createRigidArea(new Dimension(5, 0)));
            this.isProxyBox = new JCheckBox(tcpmon.getMessage("proxy00", "Proxy"));
            top.add(this.isProxyBox);
            this.isProxyBox.addChangeListener(new BasicButtonListener(this, this.isProxyBox){
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                }

                public void stateChanged(ChangeEvent event) {
                    JCheckBox box = (JCheckBox)event.getSource();
                    boolean state = box.isSelected();
                    this.this$1.tPortField.setEnabled(!state);
                    this.this$1.hostField.setEnabled(!state);
                }
            });
            this.isProxyBox.setSelected(isProxy);
            this.portField.setEditable(false);
            this.portField.setMaximumSize(new Dimension(50, Short.MAX_VALUE));
            this.hostField.setEditable(false);
            this.hostField.setMaximumSize(new Dimension(85, Short.MAX_VALUE));
            this.tPortField.setEditable(false);
            this.tPortField.setMaximumSize(new Dimension(50, Short.MAX_VALUE));
            this.stopButton.addActionListener(new ActionListener(this, start){
                private final /* synthetic */ String val$start;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$start = val$start;
                }

                public void actionPerformed(ActionEvent event) {
                    if (tcpmon.getMessage("stop00", "Stop").equals(event.getActionCommand())) {
                        this.this$1.stop();
                    }
                    if (this.val$start.equals(event.getActionCommand())) {
                        this.this$1.start();
                    }
                }
            });
            this.add((Component)top, "North");
            this.tableModel = new DefaultTableModel(new String[]{tcpmon.getMessage("state00", "State"), tcpmon.getMessage("time00", "Time"), tcpmon.getMessage("requestHost00", "Request Host"), tcpmon.getMessage("targetHost", "Target Host"), tcpmon.getMessage("request00", "Request...")}, 0);
            this.tableModel.addRow(new Object[]{"---", tcpmon.getMessage("mostRecent00", "Most Recent"), "---", "---", "---"});
            this.connectionTable = new JTable(1, 2);
            this.connectionTable.setModel(this.tableModel);
            this.connectionTable.setSelectionMode(2);
            TableColumn col = this.connectionTable.getColumnModel().getColumn(0);
            col.setMaxWidth(col.getPreferredWidth() / 2);
            col = this.connectionTable.getColumnModel().getColumn(4);
            col.setPreferredWidth(col.getPreferredWidth() * 2);
            ListSelectionModel sel = this.connectionTable.getSelectionModel();
            sel.addListSelectionListener(new ListSelectionListener(this){
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                }

                public void valueChanged(ListSelectionEvent event) {
                    if (event.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel m = (ListSelectionModel)event.getSource();
                    int divLoc = this.this$1.outPane.getDividerLocation();
                    if (m.isSelectionEmpty()) {
                        this.this$1.setLeft(new JLabel(" " + tcpmon.getMessage("wait00", "Waiting for Connection...")));
                        this.this$1.setRight(new JLabel(""));
                        this.this$1.removeButton.setEnabled(false);
                        this.this$1.removeAllButton.setEnabled(false);
                        this.this$1.saveButton.setEnabled(false);
                        this.this$1.resendButton.setEnabled(false);
                    } else {
                        int row = m.getLeadSelectionIndex();
                        if (row == 0) {
                            if (this.this$1.connections.size() == 0) {
                                this.this$1.setLeft(new JLabel(" " + tcpmon.getMessage("wait00", "Waiting for connection...")));
                                this.this$1.setRight(new JLabel(""));
                                this.this$1.removeButton.setEnabled(false);
                                this.this$1.removeAllButton.setEnabled(false);
                                this.this$1.saveButton.setEnabled(false);
                                this.this$1.resendButton.setEnabled(false);
                            } else {
                                Connection conn = (Connection)this.this$1.connections.lastElement();
                                this.this$1.setLeft(conn.inputScroll);
                                this.this$1.setRight(conn.outputScroll);
                                this.this$1.removeButton.setEnabled(false);
                                this.this$1.removeAllButton.setEnabled(true);
                                this.this$1.saveButton.setEnabled(true);
                                this.this$1.resendButton.setEnabled(true);
                            }
                        } else {
                            Connection conn = (Connection)this.this$1.connections.get(row - 1);
                            this.this$1.setLeft(conn.inputScroll);
                            this.this$1.setRight(conn.outputScroll);
                            this.this$1.removeButton.setEnabled(true);
                            this.this$1.removeAllButton.setEnabled(true);
                            this.this$1.saveButton.setEnabled(true);
                            this.this$1.resendButton.setEnabled(true);
                        }
                    }
                    this.this$1.outPane.setDividerLocation(divLoc);
                }
            });
            JPanel tablePane = new JPanel();
            tablePane.setLayout(new BorderLayout());
            JScrollPane tableScrollPane = new JScrollPane(this.connectionTable);
            tablePane.add((Component)tableScrollPane, "Center");
            JPanel buttons = new JPanel();
            buttons.setLayout(new BoxLayout(buttons, 0));
            buttons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            String removeSelected = tcpmon.getMessage("removeSelected00", "Remove Selected");
            this.removeButton = new JButton(removeSelected);
            buttons.add(this.removeButton);
            buttons.add(Box.createRigidArea(new Dimension(5, 0)));
            String removeAll = tcpmon.getMessage("removeAll00", "Remove All");
            this.removeAllButton = new JButton(removeAll);
            buttons.add(this.removeAllButton);
            tablePane.add((Component)buttons, "South");
            this.removeButton.setEnabled(false);
            this.removeButton.addActionListener(new ActionListener(this, removeSelected){
                private final /* synthetic */ String val$removeSelected;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$removeSelected = val$removeSelected;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$removeSelected.equals(event.getActionCommand())) {
                        this.this$1.remove();
                    }
                }
            });
            this.removeAllButton.setEnabled(false);
            this.removeAllButton.addActionListener(new ActionListener(this, removeAll){
                private final /* synthetic */ String val$removeAll;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$removeAll = val$removeAll;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$removeAll.equals(event.getActionCommand())) {
                        this.this$1.removeAll();
                    }
                }
            });
            JPanel pane2 = new JPanel();
            pane2.setLayout(new BorderLayout());
            this.leftPanel = new JPanel();
            this.leftPanel.setAlignmentX(0.0f);
            this.leftPanel.setLayout(new BoxLayout(this.leftPanel, 1));
            this.leftPanel.add(new JLabel("  " + tcpmon.getMessage("request01", "Request")));
            this.leftPanel.add(new JLabel(" " + tcpmon.getMessage("wait01", "Waiting for connection")));
            this.rightPanel = new JPanel();
            this.rightPanel.setLayout(new BoxLayout(this.rightPanel, 1));
            this.rightPanel.add(new JLabel("  " + tcpmon.getMessage("response00", "Response")));
            this.rightPanel.add(new JLabel(""));
            this.outPane = new JSplitPane(0, this.leftPanel, this.rightPanel);
            this.outPane.setDividerSize(4);
            pane2.add((Component)this.outPane, "Center");
            JPanel bottomButtons = new JPanel();
            bottomButtons.setLayout(new BoxLayout(bottomButtons, 0));
            bottomButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            this.xmlFormatBox = new JCheckBox(tcpmon.getMessage("xmlFormat00", "XML Format"));
            bottomButtons.add(this.xmlFormatBox);
            this.numericBox = new JCheckBox(tcpmon.getMessage("numericEnc00", "Numeric"));
            bottomButtons.add(this.numericBox);
            bottomButtons.add(Box.createRigidArea(new Dimension(5, 0)));
            String save = tcpmon.getMessage("save00", "Save");
            this.saveButton = new JButton(save);
            bottomButtons.add(this.saveButton);
            bottomButtons.add(Box.createRigidArea(new Dimension(5, 0)));
            String resend = tcpmon.getMessage("resend00", "Resend");
            this.resendButton = new JButton(resend);
            bottomButtons.add(this.resendButton);
            bottomButtons.add(Box.createRigidArea(new Dimension(5, 0)));
            String switchStr = tcpmon.getMessage("switch00", "Switch Layout");
            this.switchButton = new JButton(switchStr);
            bottomButtons.add(this.switchButton);
            bottomButtons.add(Box.createHorizontalGlue());
            String close = tcpmon.getMessage("close00", "Close");
            this.closeButton = new JButton(close);
            bottomButtons.add(this.closeButton);
            pane2.add((Component)bottomButtons, "South");
            this.saveButton.setEnabled(false);
            this.saveButton.addActionListener(new ActionListener(this, save){
                private final /* synthetic */ String val$save;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$save = val$save;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$save.equals(event.getActionCommand())) {
                        this.this$1.save();
                    }
                }
            });
            this.resendButton.setEnabled(false);
            this.resendButton.addActionListener(new ActionListener(this, resend){
                private final /* synthetic */ String val$resend;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$resend = val$resend;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$resend.equals(event.getActionCommand())) {
                        this.this$1.resend();
                    }
                }
            });
            this.switchButton.addActionListener(new ActionListener(this, switchStr){
                private final /* synthetic */ String val$switchStr;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$switchStr = val$switchStr;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$switchStr.equals(event.getActionCommand())) {
                        int v = this.this$1.outPane.getOrientation();
                        if (v == 0) {
                            this.this$1.outPane.setOrientation(1);
                        } else {
                            this.this$1.outPane.setOrientation(0);
                        }
                        this.this$1.outPane.setDividerLocation(0.5);
                    }
                }
            });
            this.closeButton.addActionListener(new ActionListener(this, close){
                private final /* synthetic */ String val$close;
                private final /* synthetic */ Listener this$1;
                {
                    this.this$1 = this$1;
                    this.val$close = val$close;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$close.equals(event.getActionCommand())) {
                        this.this$1.close();
                    }
                }
            });
            JSplitPane pane1 = new JSplitPane(0);
            pane1.setDividerSize(4);
            pane1.setTopComponent(tablePane);
            pane1.setBottomComponent(pane2);
            pane1.setDividerLocation(150);
            this.add((Component)pane1, "Center");
            sel.setSelectionInterval(0, 0);
            this.outPane.setDividerLocation(150);
            this.notebook.addTab(name, this);
            this.start();
        }

        public void setLeft(Component left) {
            this.leftPanel.removeAll();
            this.leftPanel.add(left);
        }

        public void setRight(Component right) {
            this.rightPanel.removeAll();
            this.rightPanel.add(right);
        }

        public void start() {
            int port = Integer.parseInt(this.portField.getText());
            this.portField.setText("" + port);
            int i = this.notebook.indexOfComponent(this);
            this.notebook.setTitleAt(i, tcpmon.getMessage("port01", "Port") + " " + port);
            int tmp = Integer.parseInt(this.tPortField.getText());
            this.tPortField.setText("" + tmp);
            this.sw = new SocketWaiter(this, port);
            this.stopButton.setText(tcpmon.getMessage("stop00", "Stop"));
            this.portField.setEditable(false);
            this.hostField.setEditable(false);
            this.tPortField.setEditable(false);
            this.isProxyBox.setEnabled(false);
        }

        public void close() {
            this.stop();
            this.notebook.remove(this);
        }

        public void stop() {
            try {
                for (int i = 0; i < this.connections.size(); ++i) {
                    Connection conn = (Connection)this.connections.get(i);
                    conn.halt();
                }
                this.sw.halt();
                this.stopButton.setText(tcpmon.getMessage("start00", "Start"));
                this.portField.setEditable(true);
                this.hostField.setEditable(true);
                this.tPortField.setEditable(true);
                this.isProxyBox.setEnabled(true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void remove() {
            int top;
            ListSelectionModel lsm = this.connectionTable.getSelectionModel();
            int bot = lsm.getMinSelectionIndex();
            for (int i = top = lsm.getMaxSelectionIndex(); i >= bot; --i) {
                ((Connection)this.connections.get(i - 1)).remove();
            }
            if (bot > this.connections.size()) {
                bot = this.connections.size();
            }
            lsm.setSelectionInterval(bot, bot);
        }

        public void removeAll() {
            ListSelectionModel lsm = this.connectionTable.getSelectionModel();
            lsm.clearSelection();
            while (this.connections.size() > 0) {
                ((Connection)this.connections.get(0)).remove();
            }
            lsm.setSelectionInterval(0, 0);
        }

        public void save() {
            JFileChooser dialog = new JFileChooser(".");
            int rc = dialog.showSaveDialog(this);
            if (rc == 0) {
                try {
                    File file = dialog.getSelectedFile();
                    FileOutputStream out = new FileOutputStream(file);
                    ListSelectionModel lsm = this.connectionTable.getSelectionModel();
                    rc = lsm.getLeadSelectionIndex();
                    int n = 0;
                    Iterator i = this.connections.iterator();
                    while (i.hasNext()) {
                        Connection conn = (Connection)i.next();
                        if (lsm.isSelectedIndex(n + 1) || !i.hasNext() && lsm.getLeadSelectionIndex() == 0) {
                            rc = Integer.parseInt(this.portField.getText());
                            out.write("\n==============\n".getBytes());
                            out.write((tcpmon.getMessage("listenPort01", "Listen Port:") + " " + rc + "\n").getBytes());
                            out.write((tcpmon.getMessage("targetHost01", "Target Host:") + " " + this.hostField.getText() + "\n").getBytes());
                            rc = Integer.parseInt(this.tPortField.getText());
                            out.write((tcpmon.getMessage("targetPort01", "Target Port:") + " " + rc + "\n").getBytes());
                            out.write(("==== " + tcpmon.getMessage("request01", "Request") + " ====\n").getBytes());
                            out.write(conn.inputText.getText().getBytes());
                            out.write(("==== " + tcpmon.getMessage("response00", "Response") + " ====\n").getBytes());
                            out.write(conn.outputText.getText().getBytes());
                            out.write("\n==============\n".getBytes());
                        }
                        ++n;
                    }
                    out.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void resend() {
            try {
                ListSelectionModel lsm = this.connectionTable.getSelectionModel();
                int rc = lsm.getLeadSelectionIndex();
                if (rc == 0) {
                    rc = this.connections.size();
                }
                Connection conn = (Connection)this.connections.get(rc - 1);
                if (rc > 0) {
                    lsm.clearSelection();
                    lsm.setSelectionInterval(0, 0);
                }
                ByteArrayInputStream in = null;
                String text = conn.inputText.getText();
                if (text.startsWith("POST ") || text.startsWith("GET ")) {
                    String headers;
                    int pos1;
                    int pos3 = text.indexOf("\n\n");
                    if (pos3 == -1) {
                        pos3 = text.indexOf("\r\n\r\n");
                        if (pos3 != -1) {
                            pos3 += 4;
                        }
                    } else {
                        pos3 += 2;
                    }
                    if ((pos1 = (headers = text.substring(0, pos3)).indexOf("Content-Length:")) != -1) {
                        int newLen = text.length() - pos3;
                        int pos2 = headers.indexOf("\n", pos1);
                        System.err.println("CL: " + newLen);
                        System.err.println("Hdrs: '" + headers + "'");
                        System.err.println("subTEXT: '" + text.substring(pos3, pos3 + newLen) + "'");
                        text = headers.substring(0, pos1) + "Content-Length: " + newLen + "\n" + headers.substring(pos2 + 1) + text.substring(pos3);
                        System.err.println("\nTEXT: '" + text + "'");
                    }
                }
                in = new ByteArrayInputStream(text.getBytes());
                new Connection(this, in);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Connection
    extends Thread {
        Listener listener;
        boolean active;
        String fromHost;
        String time;
        JTextArea inputText = null;
        JScrollPane inputScroll = null;
        JTextArea outputText = null;
        JScrollPane outputScroll = null;
        Socket inSocket = null;
        Socket outSocket = null;
        Thread clientThread = null;
        Thread serverThread = null;
        SocketRR rr1 = null;
        SocketRR rr2 = null;
        InputStream inputStream = null;
        String HTTPProxyHost = null;
        int HTTPProxyPort = 80;
        private SlowLinkSimulator slowLink;

        public Connection(Listener l) {
            this.listener = l;
            this.HTTPProxyHost = l.HTTPProxyHost;
            this.HTTPProxyPort = l.HTTPProxyPort;
            this.slowLink = l.slowLink;
        }

        public Connection(Listener l, Socket s) {
            this(l);
            this.inSocket = s;
            this.start();
        }

        public Connection(Listener l, InputStream in) {
            this(l);
            this.inputStream = in;
            this.start();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            try {
                byte[] b;
                this.active = true;
                this.HTTPProxyHost = System.getProperty("http.proxyHost");
                if (this.HTTPProxyHost != null && this.HTTPProxyHost.equals("")) {
                    this.HTTPProxyHost = null;
                }
                if (this.HTTPProxyHost != null) {
                    String tmp = System.getProperty("http.proxyPort");
                    if (tmp != null && tmp.equals("")) {
                        tmp = null;
                    }
                    this.HTTPProxyPort = tmp == null ? 80 : Integer.parseInt(tmp);
                }
                this.fromHost = this.inSocket != null ? this.inSocket.getInetAddress().getHostName() : "resend";
                String dateformat = tcpmon.getMessage("dateformat00", "yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat df = new SimpleDateFormat(dateformat);
                this.time = df.format(new Date());
                int count = this.listener.connections.size();
                this.listener.tableModel.insertRow(count + 1, new Object[]{tcpmon.getMessage("active00", "Active"), this.time, this.fromHost, this.listener.hostField.getText(), ""});
                this.listener.connections.add(this);
                this.inputText = new JTextArea(null, null, 20, 80);
                this.inputScroll = new JScrollPane(this.inputText);
                this.outputText = new JTextArea(null, null, 20, 80);
                this.outputScroll = new JScrollPane(this.outputText);
                ListSelectionModel lsm = this.listener.connectionTable.getSelectionModel();
                if (count == 0 || lsm.getLeadSelectionIndex() == 0) {
                    this.listener.outPane.setVisible(false);
                    int divLoc = this.listener.outPane.getDividerLocation();
                    this.listener.setLeft(this.inputScroll);
                    this.listener.setRight(this.outputScroll);
                    this.listener.removeButton.setEnabled(false);
                    this.listener.removeAllButton.setEnabled(true);
                    this.listener.saveButton.setEnabled(true);
                    this.listener.resendButton.setEnabled(true);
                    this.listener.outPane.setDividerLocation(divLoc);
                    this.listener.outPane.setVisible(true);
                }
                String targetHost = this.listener.hostField.getText();
                int targetPort = Integer.parseInt(this.listener.tPortField.getText());
                int listenPort = Integer.parseInt(this.listener.portField.getText());
                InputStream tmpIn1 = this.inputStream;
                OutputStream tmpOut1 = null;
                InputStream tmpIn2 = null;
                OutputStream tmpOut2 = null;
                if (tmpIn1 == null) {
                    tmpIn1 = this.inSocket.getInputStream();
                }
                if (this.inSocket != null) {
                    tmpOut1 = this.inSocket.getOutputStream();
                }
                String bufferedData = null;
                StringBuffer buf = null;
                int index = this.listener.connections.indexOf(this);
                if (this.listener.isProxyBox.isSelected() || this.HTTPProxyHost != null) {
                    int len;
                    b = new byte[1];
                    buf = new StringBuffer();
                    while ((len = tmpIn1.read(b, 0, 1)) != -1) {
                        String s = new String(b);
                        buf.append(s);
                        if (b[0] != 10) continue;
                    }
                    bufferedData = buf.toString();
                    this.inputText.append(bufferedData);
                    if (bufferedData.startsWith("GET ") || bufferedData.startsWith("POST ") || bufferedData.startsWith("PUT ") || bufferedData.startsWith("DELETE ")) {
                        URL url;
                        int start = bufferedData.indexOf(32) + 1;
                        while (bufferedData.charAt(start) == ' ') {
                            ++start;
                        }
                        int end = bufferedData.indexOf(32, start);
                        String urlString = bufferedData.substring(start, end);
                        if (urlString.charAt(0) == '/') {
                            urlString = urlString.substring(1);
                        }
                        if (this.listener.isProxyBox.isSelected()) {
                            url = new URL(urlString);
                            targetHost = url.getHost();
                            targetPort = url.getPort();
                            if (targetPort == -1) {
                                targetPort = 80;
                            }
                            this.listener.tableModel.setValueAt(targetHost, index + 1, 3);
                            bufferedData = bufferedData.substring(0, start) + url.getFile() + bufferedData.substring(end);
                        } else {
                            url = new URL("http://" + targetHost + ":" + targetPort + "/" + urlString);
                            this.listener.tableModel.setValueAt(targetHost, index + 1, 3);
                            bufferedData = bufferedData.substring(0, start) + url.toExternalForm() + bufferedData.substring(end);
                            targetHost = this.HTTPProxyHost;
                            targetPort = this.HTTPProxyPort;
                        }
                    }
                } else {
                    String s1;
                    int len;
                    byte[] b1 = new byte[1];
                    buf = new StringBuffer();
                    String lastLine = null;
                    while ((len = tmpIn1.read(b1, 0, 1)) != -1) {
                        s1 = new String(b1);
                        buf.append(s1);
                        if (b1[0] != 10) continue;
                        String line = buf.toString();
                        buf.setLength(0);
                        if (line.startsWith("Host: ")) {
                            String newHost = "Host: " + targetHost + ":" + listenPort + "\r\n";
                            bufferedData = bufferedData.concat(newHost);
                            break;
                        }
                        bufferedData = bufferedData == null ? line : bufferedData.concat(line);
                        if (line.equals("\r\n") || "\n".equals(lastLine) && line.equals("\n")) break;
                        lastLine = line;
                    }
                    if (bufferedData != null) {
                        this.inputText.append(bufferedData);
                        int idx = bufferedData.length() < 50 ? bufferedData.length() : 50;
                        s1 = bufferedData.substring(0, idx);
                        int i = s1.indexOf(10);
                        if (i > 0) {
                            s1 = s1.substring(0, i - 1);
                        }
                        s1 = s1 + "                           " + "                       ";
                        s1 = s1.substring(0, 51);
                        this.listener.tableModel.setValueAt(s1, index + 1, 4);
                    }
                }
                if (targetPort == -1) {
                    targetPort = 80;
                }
                this.outSocket = new Socket(targetHost, targetPort);
                tmpIn2 = this.outSocket.getInputStream();
                tmpOut2 = this.outSocket.getOutputStream();
                if (bufferedData != null) {
                    b = bufferedData.getBytes();
                    tmpOut2.write(b);
                    this.slowLink.pump(b.length);
                }
                boolean format = this.listener.xmlFormatBox.isSelected();
                boolean numeric = this.listener.numericBox.isSelected();
                this.rr1 = new SocketRR(this, this.inSocket, tmpIn1, this.outSocket, tmpOut2, this.inputText, format, numeric, this.listener.tableModel, index + 1, "request:", this.slowLink);
                SlowLinkSimulator responseLink = new SlowLinkSimulator(this.slowLink);
                this.rr2 = new SocketRR(this, this.outSocket, tmpIn2, this.inSocket, tmpOut1, this.outputText, format, numeric, null, 0, "response:", responseLink);
                while (this.rr1 != null || this.rr2 != null) {
                    if (null != this.rr1 && this.rr1.isDone()) {
                        if (index >= 0 && this.rr2 != null) {
                            this.listener.tableModel.setValueAt(tcpmon.getMessage("resp00", "Resp"), 1 + index, 0);
                        }
                        this.rr1 = null;
                    }
                    if (null != this.rr2 && this.rr2.isDone()) {
                        if (index >= 0 && this.rr1 != null) {
                            this.listener.tableModel.setValueAt(tcpmon.getMessage("req00", "Req"), 1 + index, 0);
                        }
                        this.rr2 = null;
                    }
                    Connection connection = this;
                    synchronized (connection) {
                        this.wait(1000L);
                    }
                }
                this.active = false;
                if (index >= 0) {
                    this.listener.tableModel.setValueAt(tcpmon.getMessage("done00", "Done"), 1 + index, 0);
                }
            }
            catch (Exception e) {
                StringWriter st = new StringWriter();
                PrintWriter wr = new PrintWriter(st);
                int index = this.listener.connections.indexOf(this);
                if (index >= 0) {
                    this.listener.tableModel.setValueAt(tcpmon.getMessage("error00", "Error"), 1 + index, 0);
                }
                e.printStackTrace(wr);
                wr.close();
                if (this.outputText != null) {
                    this.outputText.append(st.toString());
                } else {
                    System.out.println(st.toString());
                }
                this.halt();
            }
        }

        synchronized void wakeUp() {
            this.notifyAll();
        }

        public void halt() {
            try {
                if (this.rr1 != null) {
                    this.rr1.halt();
                }
                if (this.rr2 != null) {
                    this.rr2.halt();
                }
                if (this.inSocket != null) {
                    this.inSocket.close();
                }
                this.inSocket = null;
                if (this.outSocket != null) {
                    this.outSocket.close();
                }
                this.outSocket = null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void remove() {
            int index = -1;
            try {
                this.halt();
                index = this.listener.connections.indexOf(this);
                this.listener.tableModel.removeRow(index + 1);
                this.listener.connections.remove(index);
            }
            catch (Exception e) {
                System.err.println("index:=" + index + this);
                e.printStackTrace();
            }
        }
    }

    class SocketRR
    extends Thread {
        Socket inSocket = null;
        Socket outSocket = null;
        JTextArea textArea;
        InputStream in = null;
        OutputStream out = null;
        boolean xmlFormat;
        boolean numericEnc;
        volatile boolean done = false;
        TableModel tmodel = null;
        int tableIndex = 0;
        String type = null;
        Connection myConnection = null;
        SlowLinkSimulator slowLink;

        public SocketRR(Connection c, Socket inputSocket, InputStream inputStream, Socket outputSocket, OutputStream outputStream, JTextArea _textArea, boolean format, boolean numeric, TableModel tModel, int index, String type, SlowLinkSimulator slowLink) {
            this.inSocket = inputSocket;
            this.in = inputStream;
            this.outSocket = outputSocket;
            this.out = outputStream;
            this.textArea = _textArea;
            this.xmlFormat = format;
            this.numericEnc = numeric;
            this.tmodel = tModel;
            this.tableIndex = index;
            this.type = type;
            this.myConnection = c;
            this.slowLink = slowLink;
            this.start();
        }

        public boolean isDone() {
            return this.done;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Loose catch block
         */
        public void run() {
            block59: {
                String tmpStr;
                byte[] buffer = new byte[4096];
                byte[] tmpbuffer = new byte[8192];
                String message = null;
                int saved = 0;
                int reqSaved = 0;
                int tabWidth = 3;
                boolean atMargin = true;
                int thisIndent = -1;
                int nextIndent = -1;
                int previousIndent = -1;
                if (this.tmodel != null && !"".equals(tmpStr = (String)this.tmodel.getValueAt(this.tableIndex, 4))) {
                    reqSaved = tmpStr.length();
                }
                block14: while (!this.done) {
                    int i;
                    int len = buffer.length;
                    if (len == 0) {
                        len = buffer.length;
                    }
                    if (saved + len > buffer.length) {
                        len = buffer.length - saved;
                    }
                    int len1 = 0;
                    while (len1 == 0) {
                        try {
                            len1 = this.in.read(buffer, saved, len);
                        }
                        catch (Exception ex) {
                            if (this.done && saved == 0) break block14;
                            len1 = -1;
                            break;
                        }
                    }
                    if ((len = len1) == -1 && saved == 0) break;
                    if (len == -1) {
                        this.done = true;
                    }
                    if (this.out != null && len > 0) {
                        this.slowLink.pump(len);
                        this.out.write(buffer, saved, len);
                    }
                    if (this.tmodel != null && reqSaved < 50) {
                        String old = (String)this.tmodel.getValueAt(this.tableIndex, 4);
                        if ((old = old + new String(buffer, saved, len)).length() > 50) {
                            old = old.substring(0, 50);
                        }
                        reqSaved = old.length();
                        i = old.indexOf(10);
                        if (i > 0) {
                            old = old.substring(0, i - 1);
                            reqSaved = 50;
                        }
                        this.tmodel.setValueAt(old, this.tableIndex, 4);
                    }
                    if (this.xmlFormat) {
                        boolean inXML = false;
                        int bufferLen = saved;
                        if (len != -1) {
                            bufferLen += len;
                        }
                        int i2 = 0;
                        saved = 0;
                        for (int i1 = 0; i1 < bufferLen; ++i1) {
                            if (len != -1 && i1 + 1 == bufferLen) {
                                saved = 1;
                                break;
                            }
                            thisIndent = -1;
                            if (buffer[i1] == 60 && buffer[i1 + 1] != 47) {
                                previousIndent = nextIndent++;
                                thisIndent = nextIndent;
                                inXML = true;
                            }
                            if (buffer[i1] == 60 && buffer[i1 + 1] == 47) {
                                if (previousIndent > nextIndent) {
                                    thisIndent = nextIndent;
                                }
                                --nextIndent;
                                inXML = true;
                            }
                            if (buffer[i1] == 47 && buffer[i1 + 1] == 62) {
                                --nextIndent;
                                inXML = true;
                            }
                            if (thisIndent != -1) {
                                if (thisIndent > 0) {
                                    tmpbuffer[i2++] = 10;
                                }
                                for (i = tabWidth * thisIndent; i > 0; --i) {
                                    tmpbuffer[i2++] = 32;
                                }
                            }
                            boolean bl = atMargin = buffer[i1] == 10 || buffer[i1] == 13;
                            if (inXML && atMargin) continue;
                            tmpbuffer[i2++] = buffer[i1];
                        }
                        message = new String(tmpbuffer, 0, i2, this.getEncoding());
                        if (this.numericEnc) {
                            this.textArea.append(StringUtils.escapeNumericChar(message));
                        } else {
                            this.textArea.append(StringUtils.unescapeNumericChar(message));
                        }
                        for (i = 0; i < saved; ++i) {
                            buffer[i] = buffer[bufferLen - saved + i];
                        }
                        continue;
                    }
                    message = new String(buffer, 0, len, this.getEncoding());
                    if (this.numericEnc) {
                        this.textArea.append(StringUtils.escapeNumericChar(message));
                        continue;
                    }
                    this.textArea.append(StringUtils.unescapeNumericChar(message));
                }
                Object var19_22 = null;
                this.done = true;
                try {
                    if (this.out != null) {
                        this.out.flush();
                        if (null != this.outSocket) {
                            this.outSocket.shutdownOutput();
                        } else {
                            this.out.close();
                        }
                        this.out = null;
                    }
                }
                catch (Exception e) {
                    // empty catch block
                }
                try {
                    if (this.in != null) {
                        if (this.inSocket != null) {
                            this.inSocket.shutdownInput();
                        } else {
                            this.in.close();
                        }
                        this.in = null;
                    }
                }
                catch (Exception e) {
                    // empty catch block
                }
                this.myConnection.wakeUp();
                {
                    break block59;
                    catch (Throwable t) {
                        t.printStackTrace();
                        Object var19_23 = null;
                        this.done = true;
                        try {
                            if (this.out != null) {
                                this.out.flush();
                                if (null != this.outSocket) {
                                    this.outSocket.shutdownOutput();
                                } else {
                                    this.out.close();
                                }
                                this.out = null;
                            }
                        }
                        catch (Exception e) {
                            // empty catch block
                        }
                        try {
                            if (this.in != null) {
                                if (this.inSocket != null) {
                                    this.inSocket.shutdownInput();
                                } else {
                                    this.in.close();
                                }
                                this.in = null;
                            }
                        }
                        catch (Exception e) {
                            // empty catch block
                        }
                        this.myConnection.wakeUp();
                    }
                }
                catch (Throwable throwable) {
                    Object var19_24 = null;
                    this.done = true;
                    try {
                        if (this.out != null) {
                            this.out.flush();
                            if (null != this.outSocket) {
                                this.outSocket.shutdownOutput();
                            } else {
                                this.out.close();
                            }
                            this.out = null;
                        }
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                    try {
                        if (this.in != null) {
                            if (this.inSocket != null) {
                                this.inSocket.shutdownInput();
                            } else {
                                this.in.close();
                            }
                            this.in = null;
                        }
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                    this.myConnection.wakeUp();
                    throw throwable;
                }
            }
        }

        private String getEncoding() {
            try {
                return XMLUtils.getEncoding();
            }
            catch (Throwable t) {
                return "UTF-8";
            }
        }

        public void halt() {
            try {
                if (this.inSocket != null) {
                    this.inSocket.close();
                }
                if (this.outSocket != null) {
                    this.outSocket.close();
                }
                this.inSocket = null;
                this.outSocket = null;
                if (this.in != null) {
                    this.in.close();
                }
                if (this.out != null) {
                    this.out.close();
                }
                this.in = null;
                this.out = null;
                this.done = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class SlowLinkSimulator {
        private int delayBytes;
        private int delayTime;
        private int currentBytes;
        private int totalBytes;

        public SlowLinkSimulator(int delayBytes, int delayTime) {
            this.delayBytes = delayBytes;
            this.delayTime = delayTime;
        }

        public SlowLinkSimulator(SlowLinkSimulator that) {
            this.delayBytes = that.delayBytes;
            this.delayTime = that.delayTime;
        }

        public int getTotalBytes() {
            return this.totalBytes;
        }

        public void pump(int bytes) {
            this.totalBytes += bytes;
            if (this.delayBytes == 0) {
                return;
            }
            this.currentBytes += bytes;
            if (this.currentBytes > this.delayBytes) {
                int delaysize = this.currentBytes / this.delayBytes;
                long delay = (long)delaysize * (long)this.delayTime;
                this.currentBytes %= this.delayBytes;
                try {
                    Thread.sleep(delay);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
            }
        }

        public int getCurrentBytes() {
            return this.currentBytes;
        }

        public void setCurrentBytes(int currentBytes) {
            this.currentBytes = currentBytes;
        }
    }

    class SocketWaiter
    extends Thread {
        ServerSocket sSocket = null;
        Listener listener;
        int port;
        boolean pleaseStop = false;

        public SocketWaiter(Listener l, int p) {
            this.listener = l;
            this.port = p;
            this.start();
        }

        public void run() {
            block4: {
                try {
                    this.listener.setLeft(new JLabel(tcpmon.getMessage("wait00", " Waiting for Connection...")));
                    this.listener.repaint();
                    this.sSocket = new ServerSocket(this.port);
                    while (true) {
                        Socket inSocket = this.sSocket.accept();
                        if (!this.pleaseStop) {
                            new Connection(this.listener, inSocket);
                            inSocket = null;
                            continue;
                        }
                        break;
                    }
                }
                catch (Exception exp) {
                    if ("socket closed".equals(exp.getMessage())) break block4;
                    JLabel tmp = new JLabel(exp.toString());
                    tmp.setForeground(Color.red);
                    this.listener.setLeft(tmp);
                    this.listener.setRight(new JLabel(""));
                    this.listener.stop();
                }
            }
        }

        public void halt() {
            try {
                this.pleaseStop = true;
                new Socket(tcpmon.DEFAULT_HOST, this.port);
                if (this.sSocket != null) {
                    this.sSocket.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AdminPage
    extends JPanel {
        public JRadioButton listenerButton;
        public JRadioButton proxyButton;
        public JLabel hostLabel;
        public JLabel tportLabel;
        public NumberField port;
        public HostnameField host;
        public NumberField tport;
        public JTabbedPane noteb;
        public JCheckBox HTTPProxyBox;
        public HostnameField HTTPProxyHost;
        public NumberField HTTPProxyPort;
        public JLabel HTTPProxyHostLabel;
        public JLabel HTTPProxyPortLabel;
        public JLabel delayTimeLabel;
        public JLabel delayBytesLabel;
        public NumberField delayTime;
        public NumberField delayBytes;
        public JCheckBox delayBox;

        public AdminPage(JTabbedPane notebook, String name) {
            JPanel mainPane = null;
            JButton addButton = null;
            this.setLayout(new BorderLayout());
            this.noteb = notebook;
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            mainPane = new JPanel(layout);
            c.anchor = 17;
            c.gridwidth = 0;
            mainPane.add((Component)new JLabel(tcpmon.getMessage("newTCP00", "Create a new TCP/IP Monitor...") + " "), c);
            mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);
            JPanel tmpPanel = new JPanel(new GridBagLayout());
            c.anchor = 17;
            c.gridwidth = 1;
            tmpPanel.add((Component)new JLabel(tcpmon.getMessage("listenPort00", "Listen Port #") + " "), c);
            c.anchor = 17;
            c.gridwidth = 0;
            this.port = new NumberField(4);
            tmpPanel.add((Component)this.port, c);
            mainPane.add((Component)tmpPanel, c);
            mainPane.add(Box.createRigidArea(new Dimension(1, 5)), c);
            ButtonGroup btns = new ButtonGroup();
            c.anchor = 17;
            c.gridwidth = 0;
            mainPane.add((Component)new JLabel(tcpmon.getMessage("actAs00", "Act as a...")), c);
            c.anchor = 17;
            c.gridwidth = 0;
            String listener = tcpmon.getMessage("listener00", "Listener");
            this.listenerButton = new JRadioButton(listener);
            mainPane.add((Component)this.listenerButton, c);
            btns.add(this.listenerButton);
            this.listenerButton.setSelected(true);
            this.listenerButton.addActionListener(new ActionListener(this, listener){
                private final /* synthetic */ String val$listener;
                private final /* synthetic */ AdminPage this$1;
                {
                    this.this$1 = this$1;
                    this.val$listener = val$listener;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$listener.equals(event.getActionCommand())) {
                        boolean state = this.this$1.listenerButton.isSelected();
                        this.this$1.tport.setEnabled(state);
                        this.this$1.host.setEnabled(state);
                        this.this$1.hostLabel.setForeground(state ? Color.black : Color.gray);
                        this.this$1.tportLabel.setForeground(state ? Color.black : Color.gray);
                    }
                }
            });
            c.anchor = 17;
            c.gridwidth = 1;
            mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
            this.hostLabel = new JLabel(tcpmon.getMessage("targetHostname00", "Target Hostname") + " ");
            mainPane.add((Component)this.hostLabel, c);
            c.anchor = 17;
            c.gridwidth = 0;
            this.host = new HostnameField(30);
            mainPane.add((Component)this.host, c);
            this.host.setText(tcpmon.DEFAULT_HOST);
            c.anchor = 17;
            c.gridwidth = 1;
            mainPane.add(Box.createRigidArea(new Dimension(25, 0)));
            this.tportLabel = new JLabel(tcpmon.getMessage("targetPort00", "Target Port #") + " ");
            mainPane.add((Component)this.tportLabel, c);
            c.anchor = 17;
            c.gridwidth = 0;
            this.tport = new NumberField(4);
            mainPane.add((Component)this.tport, c);
            this.tport.setValue(8080);
            c.anchor = 17;
            c.gridwidth = 0;
            String proxy = tcpmon.getMessage("proxy00", "Proxy");
            this.proxyButton = new JRadioButton(proxy);
            mainPane.add((Component)this.proxyButton, c);
            btns.add(this.proxyButton);
            this.proxyButton.addActionListener(new ActionListener(this, proxy){
                private final /* synthetic */ String val$proxy;
                private final /* synthetic */ AdminPage this$1;
                {
                    this.this$1 = this$1;
                    this.val$proxy = val$proxy;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$proxy.equals(event.getActionCommand())) {
                        boolean state = this.this$1.proxyButton.isSelected();
                        this.this$1.tport.setEnabled(!state);
                        this.this$1.host.setEnabled(!state);
                        this.this$1.hostLabel.setForeground(state ? Color.gray : Color.black);
                        this.this$1.tportLabel.setForeground(state ? Color.gray : Color.black);
                    }
                }
            });
            c.anchor = 17;
            c.gridwidth = 0;
            mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);
            JPanel opts = new JPanel(new GridBagLayout());
            opts.setBorder(new TitledBorder(tcpmon.getMessage("options00", "Options")));
            c.anchor = 17;
            c.gridwidth = 0;
            mainPane.add((Component)opts, c);
            c.anchor = 17;
            c.gridwidth = 0;
            String proxySupport = tcpmon.getMessage("proxySupport00", "HTTP Proxy Support");
            this.HTTPProxyBox = new JCheckBox(proxySupport);
            opts.add((Component)this.HTTPProxyBox, c);
            c.anchor = 17;
            c.gridwidth = 1;
            this.HTTPProxyHostLabel = new JLabel(tcpmon.getMessage("hostname00", "Hostname") + " ");
            opts.add((Component)this.HTTPProxyHostLabel, c);
            this.HTTPProxyHostLabel.setForeground(Color.gray);
            c.anchor = 17;
            c.gridwidth = 0;
            this.HTTPProxyHost = new HostnameField(30);
            opts.add((Component)this.HTTPProxyHost, c);
            this.HTTPProxyHost.setEnabled(false);
            c.anchor = 17;
            c.gridwidth = 1;
            this.HTTPProxyPortLabel = new JLabel(tcpmon.getMessage("port00", "Port #") + " ");
            opts.add((Component)this.HTTPProxyPortLabel, c);
            this.HTTPProxyPortLabel.setForeground(Color.gray);
            c.anchor = 17;
            c.gridwidth = 0;
            this.HTTPProxyPort = new NumberField(4);
            opts.add((Component)this.HTTPProxyPort, c);
            this.HTTPProxyPort.setEnabled(false);
            this.HTTPProxyBox.addActionListener(new ActionListener(this, proxySupport){
                private final /* synthetic */ String val$proxySupport;
                private final /* synthetic */ AdminPage this$1;
                {
                    this.this$1 = this$1;
                    this.val$proxySupport = val$proxySupport;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$proxySupport.equals(event.getActionCommand())) {
                        boolean b = this.this$1.HTTPProxyBox.isSelected();
                        Color color = b ? Color.black : Color.gray;
                        this.this$1.HTTPProxyHost.setEnabled(b);
                        this.this$1.HTTPProxyPort.setEnabled(b);
                        this.this$1.HTTPProxyHostLabel.setForeground(color);
                        this.this$1.HTTPProxyPortLabel.setForeground(color);
                    }
                }
            });
            String tmp = System.getProperty("http.proxyHost");
            if (tmp != null && tmp.equals("")) {
                tmp = null;
            }
            this.HTTPProxyBox.setSelected(tmp != null);
            this.HTTPProxyHost.setEnabled(tmp != null);
            this.HTTPProxyPort.setEnabled(tmp != null);
            this.HTTPProxyHostLabel.setForeground(tmp != null ? Color.black : Color.gray);
            this.HTTPProxyPortLabel.setForeground(tmp != null ? Color.black : Color.gray);
            if (tmp != null) {
                this.HTTPProxyBox.setSelected(true);
                this.HTTPProxyHost.setText(tmp);
                tmp = System.getProperty("http.proxyPort");
                if (tmp != null && tmp.equals("")) {
                    tmp = null;
                }
                if (tmp == null) {
                    tmp = "80";
                }
                this.HTTPProxyPort.setText(tmp);
            }
            opts.add(Box.createRigidArea(new Dimension(1, 10)), c);
            c.anchor = 17;
            c.gridwidth = 0;
            String delaySupport = tcpmon.getMessage("delay00", "Simulate Slow Connection");
            this.delayBox = new JCheckBox(delaySupport);
            opts.add((Component)this.delayBox, c);
            c.anchor = 17;
            c.gridwidth = 1;
            this.delayBytesLabel = new JLabel(tcpmon.getMessage("delay01", "Bytes per Pause"));
            opts.add((Component)this.delayBytesLabel, c);
            this.delayBytesLabel.setForeground(Color.gray);
            c.anchor = 17;
            c.gridwidth = 0;
            this.delayBytes = new NumberField(6);
            opts.add((Component)this.delayBytes, c);
            this.delayBytes.setEnabled(false);
            c.anchor = 17;
            c.gridwidth = 1;
            this.delayTimeLabel = new JLabel(tcpmon.getMessage("delay02", "Delay in Milliseconds"));
            opts.add((Component)this.delayTimeLabel, c);
            this.delayTimeLabel.setForeground(Color.gray);
            c.anchor = 17;
            c.gridwidth = 0;
            this.delayTime = new NumberField(6);
            opts.add((Component)this.delayTime, c);
            this.delayTime.setEnabled(false);
            this.delayBox.addActionListener(new ActionListener(this, delaySupport){
                private final /* synthetic */ String val$delaySupport;
                private final /* synthetic */ AdminPage this$1;
                {
                    this.this$1 = this$1;
                    this.val$delaySupport = val$delaySupport;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$delaySupport.equals(event.getActionCommand())) {
                        boolean b = this.this$1.delayBox.isSelected();
                        Color color = b ? Color.black : Color.gray;
                        this.this$1.delayBytes.setEnabled(b);
                        this.this$1.delayTime.setEnabled(b);
                        this.this$1.delayBytesLabel.setForeground(color);
                        this.this$1.delayTimeLabel.setForeground(color);
                    }
                }
            });
            mainPane.add(Box.createRigidArea(new Dimension(1, 10)), c);
            c.anchor = 17;
            c.gridwidth = 0;
            String add = tcpmon.getMessage("add00", "Add");
            addButton = new JButton(add);
            mainPane.add((Component)addButton, c);
            this.add((Component)new JScrollPane(mainPane), "Center");
            addButton.addActionListener(new ActionListener(this, add, tcpmon.this){
                private final /* synthetic */ String val$add;
                private final /* synthetic */ tcpmon val$this$0;
                private final /* synthetic */ AdminPage this$1;
                {
                    this.this$1 = this$1;
                    this.val$add = val$add;
                    this.val$this$0 = val$this$0;
                }

                public void actionPerformed(ActionEvent event) {
                    if (this.val$add.equals(event.getActionCommand())) {
                        Listener l = null;
                        int lPort = this.this$1.port.getValue(0);
                        if (lPort == 0) {
                            return;
                        }
                        String tHost = this.this$1.host.getText();
                        int tPort = 0;
                        tPort = this.this$1.tport.getValue(0);
                        SlowLinkSimulator slowLink = null;
                        if (this.this$1.delayBox.isSelected()) {
                            int bytes = this.this$1.delayBytes.getValue(0);
                            int time = this.this$1.delayTime.getValue(0);
                            slowLink = new SlowLinkSimulator(bytes, time);
                        }
                        try {
                            l = AdminPage.access$000(this.this$1).new Listener(this.this$1.noteb, null, lPort, tHost, tPort, this.this$1.proxyButton.isSelected(), slowLink);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        String text = this.this$1.HTTPProxyHost.getText();
                        if ("".equals(text)) {
                            text = null;
                        }
                        l.HTTPProxyHost = text;
                        text = this.this$1.HTTPProxyPort.getText();
                        int proxyPort = this.this$1.HTTPProxyPort.getValue(-1);
                        if (proxyPort != -1) {
                            l.HTTPProxyPort = Integer.parseInt(text);
                        }
                        this.this$1.port.setText(null);
                    }
                }
            });
            notebook.addTab(name, this);
            notebook.repaint();
            notebook.setSelectedIndex(notebook.getTabCount() - 1);
        }

        static /* synthetic */ tcpmon access$000(AdminPage x0) {
            return x0.tcpmon.this;
        }
    }
}

