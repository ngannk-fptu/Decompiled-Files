/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.client.AdminClient;
import org.apache.axis.utils.Options;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SOAPMonitor
extends JFrame
implements ActionListener,
ChangeListener {
    private JPanel main_panel = null;
    private JTabbedPane tabbed_pane = null;
    private JTabbedPane top_pane = null;
    private int port = 5001;
    private String axisHost = "localhost";
    private int axisPort = 8080;
    private String axisURL = null;
    private Vector pages = null;
    private final String titleStr = "SOAP Monitor Administration";
    private JPanel set_panel = null;
    private JLabel titleLabel = null;
    private JButton add_btn = null;
    private JButton del_btn = null;
    private JButton save_btn = null;
    private JButton login_btn = null;
    private DefaultListModel model1 = null;
    private DefaultListModel model2 = null;
    private JList list1 = null;
    private JList list2 = null;
    private HashMap serviceMap = null;
    private Document originalDoc = null;
    private static String axisUser = null;
    private static String axisPass = null;
    private AdminClient adminClient = new AdminClient();

    public static void main(String[] args) throws Exception {
        SOAPMonitor soapMonitor = null;
        Options opts = new Options(args);
        if (opts.isFlagSet('?') > 0) {
            System.out.println("Usage: SOAPMonitor [-l<url>] [-u<user>] [-w<password>] [-?]");
            System.exit(0);
        }
        soapMonitor = new SOAPMonitor();
        soapMonitor.axisURL = opts.getURL();
        URL url = new URL(soapMonitor.axisURL);
        soapMonitor.axisHost = url.getHost();
        axisUser = opts.getUser();
        axisPass = opts.getPassword();
        soapMonitor.doLogin();
    }

    public SOAPMonitor() {
        this.setTitle("SOAP Monitor Application");
        Dimension d = this.getToolkit().getScreenSize();
        this.setSize(640, 480);
        this.setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        this.setDefaultCloseOperation(2);
        this.addWindowListener(new MyWindowAdapter());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            // empty catch block
        }
        this.main_panel = new JPanel();
        this.main_panel.setBackground(Color.white);
        this.main_panel.setLayout(new BorderLayout());
        this.top_pane = new JTabbedPane();
        this.set_panel = new JPanel();
        this.titleLabel = new JLabel("SOAP Monitor Administration");
        this.titleLabel.setFont(new Font("Serif", 1, 18));
        this.model1 = new DefaultListModel();
        this.list1 = new JList(this.model1);
        this.list1.setFixedCellWidth(250);
        JScrollPane scroll1 = new JScrollPane(this.list1);
        this.model2 = new DefaultListModel();
        this.list2 = new JList(this.model2);
        this.list2.setFixedCellWidth(250);
        JScrollPane scroll2 = new JScrollPane(this.list2);
        this.add_btn = new JButton("Turn On [ >> ]");
        this.del_btn = new JButton("[ << ] Turn Off");
        JPanel center_panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        center_panel.setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(this.add_btn, c);
        center_panel.add(this.add_btn);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(this.del_btn, c);
        center_panel.add(this.del_btn);
        this.save_btn = new JButton("Save changes");
        this.login_btn = new JButton("Change server");
        JPanel south_panel = new JPanel();
        layout = new GridBagLayout();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(this.save_btn, c);
        south_panel.add(this.save_btn);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(this.login_btn, c);
        south_panel.add(this.login_btn);
        this.set_panel.setLayout(new BorderLayout(5, 5));
        this.set_panel.add((Component)this.titleLabel, "North");
        this.set_panel.add((Component)south_panel, "South");
        this.set_panel.add((Component)scroll1, "West");
        this.set_panel.add((Component)scroll2, "East");
        this.set_panel.add((Component)center_panel, "Center");
        this.add_btn.addActionListener(this);
        this.del_btn.addActionListener(this);
        this.save_btn.addActionListener(this);
        this.login_btn.addActionListener(this);
        this.add_btn.setEnabled(false);
        this.del_btn.setEnabled(false);
        this.save_btn.setEnabled(false);
        this.login_btn.setEnabled(false);
        this.top_pane.add("Setting", this.set_panel);
        this.top_pane.add("Monitoring", this.main_panel);
        this.getContentPane().add(this.top_pane);
        this.tabbed_pane = new JTabbedPane(1);
        this.main_panel.add((Component)this.tabbed_pane, "Center");
        this.top_pane.addChangeListener(this);
        this.top_pane.setEnabled(false);
        this.setVisible(true);
    }

    private boolean doLogin() {
        Dimension d = null;
        LoginDlg login = new LoginDlg();
        login.show();
        if (!login.isLogin()) {
            this.login_btn.setEnabled(true);
            return false;
        }
        login.dispose();
        this.save_btn.setEnabled(false);
        this.login_btn.setEnabled(false);
        String url_str = login.getURL();
        try {
            URL url = new URL(url_str);
            this.axisHost = url.getHost();
            this.axisPort = url.getPort();
            if (this.axisPort == -1) {
                this.axisPort = 8080;
            }
            String axisPath = url.getPath();
            this.axisURL = "http://" + this.axisHost + ":" + this.axisPort + axisPath;
        }
        catch (MalformedURLException e) {
            JOptionPane pane = new JOptionPane();
            String msg = e.toString();
            pane.setMessageType(2);
            pane.setMessage(msg);
            pane.setOptions(new String[]{"OK"});
            JDialog dlg = pane.createDialog(null, "Login status");
            dlg.setVisible(true);
            this.login_btn.setEnabled(true);
            return false;
        }
        this.titleLabel.setText("SOAP Monitor Administration for [" + this.axisHost + ":" + this.axisPort + "]");
        JProgressBar progressBar = new JProgressBar(0, 100);
        BarThread stepper = new BarThread(progressBar);
        stepper.start();
        JFrame progress = new JFrame();
        d = new Dimension(250, 50);
        progress.setSize(d);
        d = this.getToolkit().getScreenSize();
        progress.getContentPane().add(progressBar);
        progress.setTitle("Now loading data ...");
        progress.setLocation((d.width - progress.getWidth()) / 2, (d.height - progress.getHeight()) / 2);
        progress.show();
        this.pages = new Vector();
        this.addPage(new SOAPMonitorPage(this.axisHost));
        this.serviceMap = new HashMap();
        this.originalDoc = this.getServerWSDD();
        this.model1.clear();
        this.model2.clear();
        if (this.originalDoc != null) {
            String ret = null;
            NodeList nl = this.originalDoc.getElementsByTagName("service");
            for (int i = 0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                NamedNodeMap map = node.getAttributes();
                ret = map.getNamedItem("name").getNodeValue();
                this.serviceMap.put(ret, node);
                if (!this.isMonitored(node)) {
                    this.model1.addElement(ret);
                    continue;
                }
                this.model2.addElement(ret);
            }
            if (this.model1.size() > 0) {
                this.add_btn.setEnabled(true);
            }
            if (this.model2.size() > 0) {
                this.del_btn.setEnabled(true);
            }
            progress.dispose();
            this.save_btn.setEnabled(true);
            this.login_btn.setEnabled(true);
            this.top_pane.setEnabled(true);
            return true;
        }
        progress.dispose();
        this.login_btn.setEnabled(true);
        return false;
    }

    private Document getServerWSDD() {
        Document doc = null;
        try {
            String[] param = new String[]{"-u" + axisUser, "-w" + axisPass, "-l " + this.axisURL, "list"};
            String ret = this.adminClient.process(param);
            doc = XMLUtils.newDocument(new ByteArrayInputStream(ret.getBytes()));
        }
        catch (Exception e) {
            JOptionPane pane = new JOptionPane();
            String msg = e.toString();
            pane.setMessageType(2);
            pane.setMessage(msg);
            pane.setOptions(new String[]{"OK"});
            JDialog dlg = pane.createDialog(null, "Login status");
            dlg.setVisible(true);
        }
        return doc;
    }

    private boolean doDeploy(Document wsdd) {
        String deploy = null;
        Options opt = null;
        deploy = XMLUtils.DocumentToString(wsdd);
        try {
            String[] param = new String[]{"-u" + axisUser, "-w" + axisPass, "-l " + this.axisURL, ""};
            opt = new Options(param);
            this.adminClient.process(opt, new ByteArrayInputStream(deploy.getBytes()));
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private Document getNewDocumentAsNode(Node target) {
        Document doc = null;
        Node node = null;
        try {
            doc = XMLUtils.newDocument();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        node = doc.importNode(target, true);
        doc.appendChild(node);
        return doc;
    }

    private Node addMonitor(Node target) {
        Document doc = null;
        Node node = null;
        Element newNode = null;
        Object ret = null;
        NodeList nl = null;
        String reqFlow = "requestFlow";
        String resFlow = "responseFlow";
        String monitor = "soapmonitor";
        String handler = "handler";
        String type = "type";
        doc = this.getNewDocumentAsNode(target);
        nl = doc.getElementsByTagName("responseFlow");
        if (nl.getLength() == 0) {
            node = doc.getDocumentElement().getFirstChild();
            newNode = doc.createElement("responseFlow");
            doc.getDocumentElement().insertBefore(newNode, node);
        }
        if ((nl = doc.getElementsByTagName("requestFlow")).getLength() == 0) {
            node = doc.getDocumentElement().getFirstChild();
            newNode = doc.createElement("requestFlow");
            doc.getDocumentElement().insertBefore(newNode, node);
        }
        nl = doc.getElementsByTagName("requestFlow");
        node = nl.item(0).getFirstChild();
        newNode = doc.createElement("handler");
        newNode.setAttribute("type", "soapmonitor");
        nl.item(0).insertBefore(newNode, node);
        nl = doc.getElementsByTagName("responseFlow");
        node = nl.item(0).getFirstChild();
        newNode = doc.createElement("handler");
        newNode.setAttribute("type", "soapmonitor");
        nl.item(0).insertBefore(newNode, node);
        return doc.getDocumentElement();
    }

    private Node delMonitor(Node target) {
        int i;
        Document doc = null;
        Node node = null;
        Node newNode = null;
        String ret = null;
        NodeList nl = null;
        String reqFlow = "requestFlow";
        String resFlow = "responseFlow";
        String monitor = "soapmonitor";
        String handler = "handler";
        String type = "type";
        doc = this.getNewDocumentAsNode(target);
        nl = doc.getElementsByTagName("handler");
        int size = nl.getLength();
        Node[] removeNode = new Node[size];
        if (size > 0) {
            newNode = nl.item(0).getParentNode();
        }
        for (i = 0; i < size; ++i) {
            node = nl.item(i);
            NamedNodeMap map = node.getAttributes();
            ret = map.getNamedItem("type").getNodeValue();
            if (!ret.equals("soapmonitor")) continue;
            removeNode[i] = node;
        }
        for (i = 0; i < size; ++i) {
            Node child = removeNode[i];
            if (child == null) continue;
            child.getParentNode().removeChild(child);
        }
        return doc.getDocumentElement();
    }

    private boolean isMonitored(Node target) {
        Document doc = null;
        Node node = null;
        String ret = null;
        NodeList nl = null;
        String monitor = "soapmonitor";
        String handler = "handler";
        String type = "type";
        int i = 0;
        doc = this.getNewDocumentAsNode(target);
        nl = doc.getElementsByTagName("handler");
        if (i < nl.getLength()) {
            node = nl.item(i);
            NamedNodeMap map = node.getAttributes();
            ret = map.getNamedItem("type").getNodeValue();
            return ret.equals("soapmonitor");
        }
        return false;
    }

    private Node addAuthenticate(Node target) {
        NamedNodeMap map;
        int i;
        Document doc = null;
        Node node = null;
        Element newNode = null;
        String ret = null;
        NodeList nl = null;
        String reqFlow = "requestFlow";
        String handler = "handler";
        String type = "type";
        String authentication = "java:org.apache.axis.handlers.SimpleAuthenticationHandler";
        String authorization = "java:org.apache.axis.handlers.SimpleAuthorizationHandler";
        String param = "parameter";
        String name = "name";
        String role = "allowedRoles";
        String value = "value";
        String admin = "admin";
        boolean authNode = false;
        boolean roleNode = false;
        doc = this.getNewDocumentAsNode(target);
        nl = doc.getElementsByTagName("requestFlow");
        if (nl.getLength() == 0) {
            node = doc.getDocumentElement().getFirstChild();
            newNode = doc.createElement("requestFlow");
            doc.getDocumentElement().insertBefore(newNode, node);
        }
        nl = doc.getElementsByTagName("handler");
        for (i = 0; i < nl.getLength(); ++i) {
            node = nl.item(i);
            map = node.getAttributes();
            ret = map.getNamedItem("type").getNodeValue();
            if (!ret.equals("java:org.apache.axis.handlers.SimpleAuthorizationHandler")) continue;
            authNode = true;
            break;
        }
        if (!authNode) {
            nl = doc.getElementsByTagName("requestFlow");
            node = nl.item(0).getFirstChild();
            newNode = doc.createElement("handler");
            newNode.setAttribute("type", "java:org.apache.axis.handlers.SimpleAuthorizationHandler");
            nl.item(0).insertBefore(newNode, node);
        }
        authNode = false;
        nl = doc.getElementsByTagName("handler");
        for (i = 0; i < nl.getLength(); ++i) {
            node = nl.item(i);
            map = node.getAttributes();
            ret = map.getNamedItem("type").getNodeValue();
            if (!ret.equals("java:org.apache.axis.handlers.SimpleAuthenticationHandler")) continue;
            authNode = true;
            break;
        }
        if (!authNode) {
            nl = doc.getElementsByTagName("requestFlow");
            node = nl.item(0).getFirstChild();
            newNode = doc.createElement("handler");
            newNode.setAttribute("type", "java:org.apache.axis.handlers.SimpleAuthenticationHandler");
            nl.item(0).insertBefore(newNode, node);
        }
        nl = doc.getElementsByTagName("parameter");
        for (i = 0; i < nl.getLength(); ++i) {
            node = nl.item(i);
            map = node.getAttributes();
            if ((node = map.getNamedItem("name")) == null || !(ret = node.getNodeValue()).equals("allowedRoles")) continue;
            roleNode = true;
            break;
        }
        if (!roleNode) {
            nl = doc.getElementsByTagName("parameter");
            newNode = doc.createElement("parameter");
            newNode.setAttribute("name", "allowedRoles");
            newNode.setAttribute("value", "admin");
            doc.getDocumentElement().insertBefore(newNode, nl.item(0));
        }
        return doc.getDocumentElement();
    }

    private void addPage(SOAPMonitorPage pg) {
        this.tabbed_pane.addTab("  " + pg.getHost() + "  ", pg);
        this.pages.addElement(pg);
    }

    private void delPage() {
        this.tabbed_pane.removeAll();
        this.pages.removeAllElements();
    }

    public void start() {
        Enumeration e = this.pages.elements();
        while (e.hasMoreElements()) {
            SOAPMonitorPage pg = (SOAPMonitorPage)e.nextElement();
            if (pg == null) continue;
            pg.start();
        }
    }

    public void stop() {
        Enumeration e = this.pages.elements();
        while (e.hasMoreElements()) {
            SOAPMonitorPage pg = (SOAPMonitorPage)e.nextElement();
            if (pg == null) continue;
            pg.stop();
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == this.add_btn) {
            int len;
            int[] selected = this.list1.getSelectedIndices();
            for (int i = len = selected.length - 1; i >= 0; --i) {
                this.model2.addElement(this.model1.getElementAt(selected[i]));
                this.model1.remove(selected[i]);
            }
            if (this.model1.size() == 0) {
                this.add_btn.setEnabled(false);
            }
            if (this.model2.size() > 0) {
                this.del_btn.setEnabled(true);
            }
        } else if (obj == this.del_btn) {
            int len;
            int[] selected = this.list2.getSelectedIndices();
            for (int i = len = selected.length - 1; i >= 0; --i) {
                this.model1.addElement(this.model2.getElementAt(selected[i]));
                this.model2.remove(selected[i]);
            }
            if (this.model2.size() == 0) {
                this.del_btn.setEnabled(false);
            }
            if (this.model1.size() > 0) {
                this.add_btn.setEnabled(true);
            }
        } else if (obj == this.login_btn) {
            if (this.doLogin()) {
                this.delPage();
                this.addPage(new SOAPMonitorPage(this.axisHost));
                this.start();
            } else {
                this.add_btn.setEnabled(false);
                this.del_btn.setEnabled(false);
            }
        } else if (obj == this.save_btn) {
            String service = null;
            Node node = null;
            Node impNode = null;
            Document wsdd = null;
            JOptionPane pane = null;
            JDialog dlg = null;
            String msg = null;
            String title = "Deployment status";
            String deploy = "<deployment name=\"SOAPMonitor\" xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\n <handler name=\"soapmonitor\" type=\"java:org.apache.axis.handlers.SOAPMonitorHandler\" />\n </deployment>";
            try {
                wsdd = XMLUtils.newDocument(new ByteArrayInputStream("<deployment name=\"SOAPMonitor\" xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\n <handler name=\"soapmonitor\" type=\"java:org.apache.axis.handlers.SOAPMonitorHandler\" />\n </deployment>".getBytes()));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            Set col = this.serviceMap.keySet();
            Iterator ite = col.iterator();
            while (ite.hasNext()) {
                service = (String)ite.next();
                node = (Node)this.serviceMap.get(service);
                impNode = this.model2.contains(service) ? (this.isMonitored(node) ? wsdd.importNode(node, true) : wsdd.importNode(this.addMonitor(node), true)) : (this.isMonitored(node) ? wsdd.importNode(this.delMonitor(node), true) : wsdd.importNode(node, true));
                if (service.equals("AdminService")) {
                    impNode = wsdd.importNode(this.addAuthenticate(impNode), true);
                }
                wsdd.getDocumentElement().appendChild(impNode);
            }
            pane = new JOptionPane();
            if (this.doDeploy(wsdd)) {
                msg = "The deploy was successful.";
                pane.setMessageType(1);
            } else {
                msg = "The deploy was NOT successful.";
                pane.setMessageType(2);
            }
            pane.setOptions(new String[]{"OK"});
            pane.setMessage(msg);
            dlg = pane.createDialog(null, "Deployment status");
            dlg.setVisible(true);
        }
    }

    public void stateChanged(ChangeEvent e) {
        JTabbedPane tab = (JTabbedPane)e.getSource();
        int item = tab.getSelectedIndex();
        if (item == 1) {
            this.start();
        } else {
            this.stop();
        }
    }

    class SOAPMonitorTextArea
    extends JTextArea {
        private boolean format = false;
        private String original = "";
        private String formatted = null;

        public void setText(String text) {
            this.original = text;
            this.formatted = null;
            if (this.format) {
                this.doFormat();
                super.setText(this.formatted);
            } else {
                super.setText(this.original);
            }
        }

        public void setReflowXML(boolean reflow) {
            this.format = reflow;
            if (this.format) {
                if (this.formatted == null) {
                    this.doFormat();
                }
                super.setText(this.formatted);
            } else {
                super.setText(this.original);
            }
        }

        public void doFormat() {
            int index;
            Vector<String> parts = new Vector<String>();
            char[] chars = this.original.toCharArray();
            int first = 0;
            String part = null;
            for (index = 0; index < chars.length; ++index) {
                if (chars[index] == '<') {
                    if (first < index) {
                        part = new String(chars, first, index - first);
                        if ((part = part.trim()).length() > 0) {
                            parts.addElement(part);
                        }
                    }
                    first = index;
                }
                if (chars[index] == '>') {
                    part = new String(chars, first, index - first + 1);
                    parts.addElement(part);
                    first = index + 1;
                }
                if (chars[index] != '\n' && chars[index] != '\r') continue;
                if (first < index) {
                    part = new String(chars, first, index - first);
                    if ((part = part.trim()).length() > 0) {
                        parts.addElement(part);
                    }
                }
                first = index + 1;
            }
            StringBuffer buf = new StringBuffer();
            Object[] list = parts.toArray();
            int indent = 0;
            int pad = 0;
            for (index = 0; index < list.length; ++index) {
                part = (String)list[index];
                if (buf.length() == 0) {
                    buf.append(part);
                    continue;
                }
                buf.append('\n');
                if (part.startsWith("</")) {
                    --indent;
                }
                for (pad = 0; pad < indent; ++pad) {
                    buf.append("  ");
                }
                buf.append(part);
                if (!part.startsWith("<") || part.startsWith("</") || part.endsWith("/>")) continue;
                ++indent;
                if (index + 2 >= list.length || !(part = (String)list[index + 2]).startsWith("</") || (part = (String)list[index + 1]).startsWith("<")) continue;
                buf.append(part);
                part = (String)list[index + 2];
                buf.append(part);
                index += 2;
                --indent;
            }
            this.formatted = new String(buf);
        }
    }

    class SOAPMonitorFilter
    implements ActionListener {
        private JDialog dialog = null;
        private JPanel panel = null;
        private JPanel buttons = null;
        private JButton ok_button = null;
        private JButton cancel_button = null;
        private ServiceFilterPanel include_panel = null;
        private ServiceFilterPanel exclude_panel = null;
        private JPanel status_panel = null;
        private JCheckBox status_box = null;
        private EmptyBorder empty_border = null;
        private EmptyBorder indent_border = null;
        private JPanel status_options = null;
        private ButtonGroup status_group = null;
        private JRadioButton status_active = null;
        private JRadioButton status_complete = null;
        private Vector filter_include_list = null;
        private Vector filter_exclude_list = new Vector();
        private boolean filter_active = false;
        private boolean filter_complete = false;
        private boolean ok_pressed = false;

        public SOAPMonitorFilter() {
            this.filter_exclude_list.addElement("NotificationService");
            this.filter_exclude_list.addElement("EventViewerService");
        }

        public Vector getFilterIncludeList() {
            return this.filter_include_list;
        }

        public Vector getFilterExcludeList() {
            return this.filter_exclude_list;
        }

        public boolean getFilterActive() {
            return this.filter_active;
        }

        public boolean getFilterComplete() {
            return this.filter_complete;
        }

        public void showDialog() {
            this.empty_border = new EmptyBorder(5, 5, 0, 5);
            this.indent_border = new EmptyBorder(5, 25, 5, 5);
            this.include_panel = new ServiceFilterPanel("Include messages based on target service:", this.filter_include_list);
            this.exclude_panel = new ServiceFilterPanel("Exclude messages based on target service:", this.filter_exclude_list);
            this.status_box = new JCheckBox("Filter messages based on status:");
            this.status_box.addActionListener(this);
            this.status_active = new JRadioButton("Active messages only");
            this.status_active.setSelected(true);
            this.status_active.setEnabled(false);
            this.status_complete = new JRadioButton("Complete messages only");
            this.status_complete.setEnabled(false);
            this.status_group = new ButtonGroup();
            this.status_group.add(this.status_active);
            this.status_group.add(this.status_complete);
            if (this.filter_active || this.filter_complete) {
                this.status_box.setSelected(true);
                this.status_active.setEnabled(true);
                this.status_complete.setEnabled(true);
                if (this.filter_complete) {
                    this.status_complete.setSelected(true);
                }
            }
            this.status_options = new JPanel();
            this.status_options.setLayout(new BoxLayout(this.status_options, 1));
            this.status_options.add(this.status_active);
            this.status_options.add(this.status_complete);
            this.status_options.setBorder(this.indent_border);
            this.status_panel = new JPanel();
            this.status_panel.setLayout(new BorderLayout());
            this.status_panel.add((Component)this.status_box, "North");
            this.status_panel.add((Component)this.status_options, "Center");
            this.status_panel.setBorder(this.empty_border);
            this.ok_button = new JButton("Ok");
            this.ok_button.addActionListener(this);
            this.cancel_button = new JButton("Cancel");
            this.cancel_button.addActionListener(this);
            this.buttons = new JPanel();
            this.buttons.setLayout(new FlowLayout());
            this.buttons.add(this.ok_button);
            this.buttons.add(this.cancel_button);
            this.panel = new JPanel();
            this.panel.setLayout(new BoxLayout(this.panel, 1));
            this.panel.add(this.include_panel);
            this.panel.add(this.exclude_panel);
            this.panel.add(this.status_panel);
            this.panel.add(this.buttons);
            this.dialog = new JDialog();
            this.dialog.setTitle("SOAP Monitor Filter");
            this.dialog.setContentPane(this.panel);
            this.dialog.setDefaultCloseOperation(2);
            this.dialog.setModal(true);
            this.dialog.pack();
            Dimension d = this.dialog.getToolkit().getScreenSize();
            this.dialog.setLocation((d.width - this.dialog.getWidth()) / 2, (d.height - this.dialog.getHeight()) / 2);
            this.ok_pressed = false;
            this.dialog.show();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.ok_button) {
                this.filter_include_list = this.include_panel.getServiceList();
                this.filter_exclude_list = this.exclude_panel.getServiceList();
                if (this.status_box.isSelected()) {
                    this.filter_active = this.status_active.isSelected();
                    this.filter_complete = this.status_complete.isSelected();
                } else {
                    this.filter_active = false;
                    this.filter_complete = false;
                }
                this.ok_pressed = true;
                this.dialog.dispose();
            }
            if (e.getSource() == this.cancel_button) {
                this.dialog.dispose();
            }
            if (e.getSource() == this.status_box) {
                this.status_active.setEnabled(this.status_box.isSelected());
                this.status_complete.setEnabled(this.status_box.isSelected());
            }
        }

        public boolean okPressed() {
            return this.ok_pressed;
        }
    }

    class ServiceFilterPanel
    extends JPanel
    implements ActionListener,
    ListSelectionListener,
    DocumentListener {
        private JCheckBox service_box = null;
        private Vector filter_list = null;
        private Vector service_data = null;
        private JList service_list = null;
        private JScrollPane service_scroll = null;
        private JButton remove_service_button = null;
        private JPanel remove_service_panel = null;
        private EmptyBorder indent_border = null;
        private EmptyBorder empty_border = new EmptyBorder(5, 5, 0, 5);
        private JPanel service_area = null;
        private JPanel add_service_area = null;
        private JTextField add_service_field = null;
        private JButton add_service_button = null;
        private JPanel add_service_panel = null;

        public ServiceFilterPanel(String text, Vector list) {
            this.indent_border = new EmptyBorder(5, 25, 5, 5);
            this.service_box = new JCheckBox(text);
            this.service_box.addActionListener(this);
            this.service_data = new Vector();
            if (list != null) {
                this.service_box.setSelected(true);
                this.service_data = (Vector)list.clone();
            }
            this.service_list = new JList(this.service_data);
            this.service_list.setBorder(new EtchedBorder());
            this.service_list.setVisibleRowCount(5);
            this.service_list.addListSelectionListener(this);
            this.service_list.setEnabled(this.service_box.isSelected());
            this.service_scroll = new JScrollPane(this.service_list);
            this.service_scroll.setBorder(new EtchedBorder());
            this.remove_service_button = new JButton("Remove");
            this.remove_service_button.addActionListener(this);
            this.remove_service_button.setEnabled(false);
            this.remove_service_panel = new JPanel();
            this.remove_service_panel.setLayout(new FlowLayout());
            this.remove_service_panel.add(this.remove_service_button);
            this.service_area = new JPanel();
            this.service_area.setLayout(new BorderLayout());
            this.service_area.add((Component)this.service_scroll, "Center");
            this.service_area.add((Component)this.remove_service_panel, "East");
            this.service_area.setBorder(this.indent_border);
            this.add_service_field = new JTextField();
            this.add_service_field.addActionListener(this);
            this.add_service_field.getDocument().addDocumentListener(this);
            this.add_service_field.setEnabled(this.service_box.isSelected());
            this.add_service_button = new JButton("Add");
            this.add_service_button.addActionListener(this);
            this.add_service_button.setEnabled(false);
            this.add_service_panel = new JPanel();
            this.add_service_panel.setLayout(new BorderLayout());
            JPanel dummy = new JPanel();
            dummy.setBorder(this.empty_border);
            this.add_service_panel.add((Component)dummy, "West");
            this.add_service_panel.add((Component)this.add_service_button, "East");
            this.add_service_area = new JPanel();
            this.add_service_area.setLayout(new BorderLayout());
            this.add_service_area.add((Component)this.add_service_field, "Center");
            this.add_service_area.add((Component)this.add_service_panel, "East");
            this.add_service_area.setBorder(this.indent_border);
            this.setLayout(new BorderLayout());
            this.add((Component)this.service_box, "North");
            this.add((Component)this.service_area, "Center");
            this.add((Component)this.add_service_area, "South");
            this.setBorder(this.empty_border);
        }

        public Vector getServiceList() {
            Vector list = null;
            if (this.service_box.isSelected()) {
                list = this.service_data;
            }
            return list;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.service_box) {
                this.service_list.setEnabled(this.service_box.isSelected());
                this.service_list.clearSelection();
                this.remove_service_button.setEnabled(false);
                this.add_service_field.setEnabled(this.service_box.isSelected());
                this.add_service_field.setText("");
                this.add_service_button.setEnabled(false);
            }
            if (e.getSource() == this.add_service_button || e.getSource() == this.add_service_field) {
                String text = this.add_service_field.getText();
                if (text != null && text.length() > 0) {
                    this.service_data.addElement(text);
                    this.service_list.setListData(this.service_data);
                }
                this.add_service_field.setText("");
                this.add_service_field.requestFocus();
            }
            if (e.getSource() == this.remove_service_button) {
                Object[] sels = this.service_list.getSelectedValues();
                for (int i = 0; i < sels.length; ++i) {
                    this.service_data.removeElement(sels[i]);
                }
                this.service_list.setListData(this.service_data);
                this.service_list.clearSelection();
            }
        }

        public void changedUpdate(DocumentEvent e) {
            String text = this.add_service_field.getText();
            if (text != null && text.length() > 0) {
                this.add_service_button.setEnabled(true);
            } else {
                this.add_service_button.setEnabled(false);
            }
        }

        public void insertUpdate(DocumentEvent e) {
            this.changedUpdate(e);
        }

        public void removeUpdate(DocumentEvent e) {
            this.changedUpdate(e);
        }

        public void valueChanged(ListSelectionEvent e) {
            if (this.service_list.getSelectedIndex() == -1) {
                this.remove_service_button.setEnabled(false);
            } else {
                this.remove_service_button.setEnabled(true);
            }
        }
    }

    class SOAPMonitorTableModel
    extends AbstractTableModel {
        private final String[] column_names = new String[]{"Time", "Target Service", "Status"};
        private Vector data = new Vector();
        private Vector filter_include;
        private Vector filter_exclude;
        private boolean filter_active;
        private boolean filter_complete;
        private Vector filter_data;

        public SOAPMonitorTableModel() {
            SOAPMonitorData soap = new SOAPMonitorData(null, null, null);
            this.data.addElement(soap);
            this.filter_include = null;
            this.filter_exclude = null;
            this.filter_active = false;
            this.filter_complete = false;
            this.filter_data = null;
            this.filter_exclude = new Vector();
            this.filter_exclude.addElement("NotificationService");
            this.filter_exclude.addElement("EventViewerService");
            this.filter_data = new Vector();
            this.filter_data.addElement(soap);
        }

        public int getColumnCount() {
            return this.column_names.length;
        }

        public int getRowCount() {
            int count = this.data.size();
            if (this.filter_data != null) {
                count = this.filter_data.size();
            }
            return count;
        }

        public String getColumnName(int col) {
            return this.column_names[col];
        }

        public Object getValueAt(int row, int col) {
            String value = null;
            SOAPMonitorData soap = (SOAPMonitorData)this.data.elementAt(row);
            if (this.filter_data != null) {
                soap = (SOAPMonitorData)this.filter_data.elementAt(row);
            }
            switch (col) {
                case 0: {
                    value = soap.getTime();
                    break;
                }
                case 1: {
                    value = soap.getTargetService();
                    break;
                }
                case 2: {
                    value = soap.getStatus();
                }
            }
            return value;
        }

        public boolean filterMatch(SOAPMonitorData soap) {
            String service;
            Enumeration e;
            boolean match = true;
            if (this.filter_include != null) {
                e = this.filter_include.elements();
                match = false;
                while (e.hasMoreElements() && !match) {
                    service = (String)e.nextElement();
                    if (!service.equals(soap.getTargetService())) continue;
                    match = true;
                }
            }
            if (this.filter_exclude != null) {
                e = this.filter_exclude.elements();
                while (e.hasMoreElements() && match) {
                    service = (String)e.nextElement();
                    if (!service.equals(soap.getTargetService())) continue;
                    match = false;
                }
            }
            if (this.filter_active && soap.getSOAPResponse() != null) {
                match = false;
            }
            if (this.filter_complete && soap.getSOAPResponse() == null) {
                match = false;
            }
            if (soap.getId() == null) {
                match = true;
            }
            return match;
        }

        public void addData(SOAPMonitorData soap) {
            int row = this.data.size();
            this.data.addElement(soap);
            if (this.filter_data != null) {
                if (this.filterMatch(soap)) {
                    row = this.filter_data.size();
                    this.filter_data.addElement(soap);
                    this.fireTableRowsInserted(row, row);
                }
            } else {
                this.fireTableRowsInserted(row, row);
            }
        }

        public SOAPMonitorData findData(Long id) {
            SOAPMonitorData soap = null;
            for (int row = this.data.size(); row > 0 && soap == null; --row) {
                soap = (SOAPMonitorData)this.data.elementAt(row - 1);
                if (soap.getId().longValue() == id.longValue()) continue;
                soap = null;
            }
            return soap;
        }

        public int findRow(SOAPMonitorData soap) {
            int row = -1;
            row = this.filter_data != null ? this.filter_data.indexOf(soap) : this.data.indexOf(soap);
            return row;
        }

        public void clearAll() {
            int last_row = this.data.size() - 1;
            if (last_row > 0) {
                this.data.removeAllElements();
                SOAPMonitorData soap = new SOAPMonitorData(null, null, null);
                this.data.addElement(soap);
                if (this.filter_data != null) {
                    this.filter_data.removeAllElements();
                    this.filter_data.addElement(soap);
                }
                this.fireTableDataChanged();
            }
        }

        public void removeRow(int row) {
            SOAPMonitorData soap = null;
            if (this.filter_data == null) {
                soap = (SOAPMonitorData)this.data.elementAt(row);
                this.data.remove(soap);
            } else {
                soap = (SOAPMonitorData)this.filter_data.elementAt(row);
                this.filter_data.remove(soap);
                this.data.remove(soap);
            }
            this.fireTableRowsDeleted(row, row);
        }

        public void setFilter(SOAPMonitorFilter filter) {
            this.filter_include = filter.getFilterIncludeList();
            this.filter_exclude = filter.getFilterExcludeList();
            this.filter_active = filter.getFilterActive();
            this.filter_complete = filter.getFilterComplete();
            this.applyFilter();
        }

        public void applyFilter() {
            this.filter_data = null;
            if (this.filter_include != null || this.filter_exclude != null || this.filter_active || this.filter_complete) {
                this.filter_data = new Vector();
                Enumeration e = this.data.elements();
                while (e.hasMoreElements()) {
                    SOAPMonitorData soap = (SOAPMonitorData)e.nextElement();
                    if (!this.filterMatch(soap)) continue;
                    this.filter_data.addElement(soap);
                }
            }
            this.fireTableDataChanged();
        }

        public SOAPMonitorData getData(int row) {
            SOAPMonitorData soap = null;
            soap = this.filter_data == null ? (SOAPMonitorData)this.data.elementAt(row) : (SOAPMonitorData)this.filter_data.elementAt(row);
            return soap;
        }

        public void updateData(SOAPMonitorData soap) {
            if (this.filter_data == null) {
                int row = this.data.indexOf(soap);
                if (row != -1) {
                    this.fireTableRowsUpdated(row, row);
                }
            } else {
                int row = this.filter_data.indexOf(soap);
                if (row == -1) {
                    if (this.filterMatch(soap)) {
                        int index = -1;
                        for (row = this.data.indexOf(soap) + 1; row < this.data.size() && index == -1; ++row) {
                            index = this.filter_data.indexOf(this.data.elementAt(row));
                            if (index == -1) continue;
                            this.filter_data.add(index, soap);
                        }
                        if (index == -1) {
                            index = this.filter_data.size();
                            this.filter_data.addElement(soap);
                        }
                        this.fireTableRowsInserted(index, index);
                    }
                } else if (this.filterMatch(soap)) {
                    this.fireTableRowsUpdated(row, row);
                } else {
                    this.filter_data.remove(soap);
                    this.fireTableRowsDeleted(row, row);
                }
            }
        }
    }

    class SOAPMonitorData {
        private Long id;
        private String time;
        private String target;
        private String soap_request;
        private String soap_response;

        public SOAPMonitorData(Long id, String target, String soap_request) {
            this.id = id;
            if (id == null) {
                this.time = "Most Recent";
                this.target = "---";
                this.soap_request = null;
                this.soap_response = null;
            } else {
                this.time = DateFormat.getTimeInstance().format(new Date());
                this.target = target;
                this.soap_request = soap_request;
                this.soap_response = null;
            }
        }

        public Long getId() {
            return this.id;
        }

        public String getTime() {
            return this.time;
        }

        public String getTargetService() {
            return this.target;
        }

        public String getStatus() {
            String status = "---";
            if (this.id != null) {
                status = "Complete";
                if (this.soap_response == null) {
                    status = "Active";
                }
            }
            return status;
        }

        public String getSOAPRequest() {
            return this.soap_request;
        }

        public void setSOAPResponse(String response) {
            this.soap_response = response;
        }

        public String getSOAPResponse() {
            return this.soap_response;
        }
    }

    class SOAPMonitorPage
    extends JPanel
    implements Runnable,
    ListSelectionListener,
    ActionListener {
        private final String STATUS_ACTIVE = "The SOAP Monitor is started.";
        private final String STATUS_STOPPED = "The SOAP Monitor is stopped.";
        private final String STATUS_CLOSED = "The server communication has been terminated.";
        private final String STATUS_NOCONNECT = "The SOAP Monitor is unable to communcate with the server.";
        private String host = null;
        private Socket socket = null;
        private ObjectInputStream in = null;
        private ObjectOutputStream out = null;
        private SOAPMonitorTableModel model = null;
        private JTable table = null;
        private JScrollPane scroll = null;
        private JPanel list_panel = null;
        private JPanel list_buttons = null;
        private JButton remove_button = null;
        private JButton remove_all_button = null;
        private JButton filter_button = null;
        private JPanel details_panel = null;
        private JPanel details_header = null;
        private JSplitPane details_soap = null;
        private JPanel details_buttons = null;
        private JLabel details_time = null;
        private JLabel details_target = null;
        private JLabel details_status = null;
        private JLabel details_time_value = null;
        private JLabel details_target_value = null;
        private JLabel details_status_value = null;
        private EmptyBorder empty_border = null;
        private EtchedBorder etched_border = null;
        private JPanel request_panel = null;
        private JPanel response_panel = null;
        private JLabel request_label = null;
        private JLabel response_label = null;
        private SOAPMonitorTextArea request_text = null;
        private SOAPMonitorTextArea response_text = null;
        private JScrollPane request_scroll = null;
        private JScrollPane response_scroll = null;
        private JButton layout_button = null;
        private JSplitPane split = null;
        private JPanel status_area = null;
        private JPanel status_buttons = null;
        private JButton start_button = null;
        private JButton stop_button = null;
        private JLabel status_text = null;
        private JPanel status_text_panel = null;
        private SOAPMonitorFilter filter = null;
        private GridBagLayout details_header_layout = null;
        private GridBagConstraints details_header_constraints = null;
        private JCheckBox reflow_xml = null;

        public SOAPMonitorPage(String host_name) {
            this.host = host_name;
            this.filter = new SOAPMonitorFilter();
            this.etched_border = new EtchedBorder();
            this.model = new SOAPMonitorTableModel();
            this.table = new JTable(this.model);
            this.table.setSelectionMode(0);
            this.table.setRowSelectionInterval(0, 0);
            this.table.setPreferredScrollableViewportSize(new Dimension(600, 96));
            this.table.getSelectionModel().addListSelectionListener(this);
            this.scroll = new JScrollPane(this.table);
            this.remove_button = new JButton("Remove");
            this.remove_button.addActionListener(this);
            this.remove_button.setEnabled(false);
            this.remove_all_button = new JButton("Remove All");
            this.remove_all_button.addActionListener(this);
            this.filter_button = new JButton("Filter ...");
            this.filter_button.addActionListener(this);
            this.list_buttons = new JPanel();
            this.list_buttons.setLayout(new FlowLayout());
            this.list_buttons.add(this.remove_button);
            this.list_buttons.add(this.remove_all_button);
            this.list_buttons.add(this.filter_button);
            this.list_panel = new JPanel();
            this.list_panel.setLayout(new BorderLayout());
            this.list_panel.add((Component)this.scroll, "Center");
            this.list_panel.add((Component)this.list_buttons, "South");
            this.list_panel.setBorder(this.empty_border);
            this.details_time = new JLabel("Time: ", 4);
            this.details_target = new JLabel("Target Service: ", 4);
            this.details_status = new JLabel("Status: ", 4);
            this.details_time_value = new JLabel();
            this.details_target_value = new JLabel();
            this.details_status_value = new JLabel();
            Dimension preferred_size = this.details_time.getPreferredSize();
            preferred_size.width = 1;
            this.details_time.setPreferredSize(preferred_size);
            this.details_target.setPreferredSize(preferred_size);
            this.details_status.setPreferredSize(preferred_size);
            this.details_time_value.setPreferredSize(preferred_size);
            this.details_target_value.setPreferredSize(preferred_size);
            this.details_status_value.setPreferredSize(preferred_size);
            this.details_header = new JPanel();
            this.details_header_layout = new GridBagLayout();
            this.details_header.setLayout(this.details_header_layout);
            this.details_header_constraints = new GridBagConstraints();
            this.details_header_constraints.fill = 1;
            this.details_header_constraints.weightx = 0.5;
            this.details_header_layout.setConstraints(this.details_time, this.details_header_constraints);
            this.details_header.add(this.details_time);
            this.details_header_layout.setConstraints(this.details_time_value, this.details_header_constraints);
            this.details_header.add(this.details_time_value);
            this.details_header_layout.setConstraints(this.details_target, this.details_header_constraints);
            this.details_header.add(this.details_target);
            this.details_header_constraints.weightx = 1.0;
            this.details_header_layout.setConstraints(this.details_target_value, this.details_header_constraints);
            this.details_header.add(this.details_target_value);
            this.details_header_constraints.weightx = 0.5;
            this.details_header_layout.setConstraints(this.details_status, this.details_header_constraints);
            this.details_header.add(this.details_status);
            this.details_header_layout.setConstraints(this.details_status_value, this.details_header_constraints);
            this.details_header.add(this.details_status_value);
            this.details_header.setBorder(this.etched_border);
            this.request_label = new JLabel("SOAP Request", 0);
            this.request_text = new SOAPMonitorTextArea();
            this.request_text.setEditable(false);
            this.request_scroll = new JScrollPane(this.request_text);
            this.request_panel = new JPanel();
            this.request_panel.setLayout(new BorderLayout());
            this.request_panel.add((Component)this.request_label, "North");
            this.request_panel.add((Component)this.request_scroll, "Center");
            this.response_label = new JLabel("SOAP Response", 0);
            this.response_text = new SOAPMonitorTextArea();
            this.response_text.setEditable(false);
            this.response_scroll = new JScrollPane(this.response_text);
            this.response_panel = new JPanel();
            this.response_panel.setLayout(new BorderLayout());
            this.response_panel.add((Component)this.response_label, "North");
            this.response_panel.add((Component)this.response_scroll, "Center");
            this.details_soap = new JSplitPane(1);
            this.details_soap.setTopComponent(this.request_panel);
            this.details_soap.setRightComponent(this.response_panel);
            this.details_soap.setResizeWeight(0.5);
            this.details_panel = new JPanel();
            this.layout_button = new JButton("Switch Layout");
            this.layout_button.addActionListener(this);
            this.reflow_xml = new JCheckBox("Reflow XML text");
            this.reflow_xml.addActionListener(this);
            this.details_buttons = new JPanel();
            this.details_buttons.setLayout(new FlowLayout());
            this.details_buttons.add(this.reflow_xml);
            this.details_buttons.add(this.layout_button);
            this.details_panel.setLayout(new BorderLayout());
            this.details_panel.add((Component)this.details_header, "North");
            this.details_panel.add((Component)this.details_soap, "Center");
            this.details_panel.add((Component)this.details_buttons, "South");
            this.details_panel.setBorder(this.empty_border);
            this.split = new JSplitPane(0);
            this.split.setTopComponent(this.list_panel);
            this.split.setRightComponent(this.details_panel);
            this.start_button = new JButton("Start");
            this.start_button.addActionListener(this);
            this.stop_button = new JButton("Stop");
            this.stop_button.addActionListener(this);
            this.status_buttons = new JPanel();
            this.status_buttons.setLayout(new FlowLayout());
            this.status_buttons.add(this.start_button);
            this.status_buttons.add(this.stop_button);
            this.status_text = new JLabel();
            this.status_text.setBorder(new BevelBorder(1));
            this.status_text_panel = new JPanel();
            this.status_text_panel.setLayout(new BorderLayout());
            this.status_text_panel.add((Component)this.status_text, "Center");
            this.status_text_panel.setBorder(this.empty_border);
            this.status_area = new JPanel();
            this.status_area.setLayout(new BorderLayout());
            this.status_area.add((Component)this.status_buttons, "West");
            this.status_area.add((Component)this.status_text_panel, "Center");
            this.status_area.setBorder(this.etched_border);
            this.setLayout(new BorderLayout());
            this.add((Component)this.split, "Center");
            this.add((Component)this.status_area, "South");
        }

        public String getHost() {
            return this.host;
        }

        public void setStatus(String txt) {
            this.status_text.setForeground(Color.black);
            this.status_text.setText("  " + txt);
        }

        public void setErrorStatus(String txt) {
            this.status_text.setForeground(Color.red);
            this.status_text.setText("  " + txt);
        }

        public void start() {
            String codehost = SOAPMonitor.this.axisHost;
            if (this.socket == null) {
                try {
                    this.socket = new Socket(codehost, SOAPMonitor.this.port);
                    this.out = new ObjectOutputStream(this.socket.getOutputStream());
                    this.out.flush();
                    this.in = new ObjectInputStream(this.socket.getInputStream());
                    new Thread(this).start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.setErrorStatus("The SOAP Monitor is unable to communcate with the server.");
                    this.socket = null;
                }
            }
            if (this.socket != null) {
                this.start_button.setEnabled(false);
                this.stop_button.setEnabled(true);
                this.setStatus("The SOAP Monitor is started.");
            }
        }

        public void stop() {
            if (this.socket != null) {
                if (this.out != null) {
                    try {
                        this.out.close();
                    }
                    catch (IOException ioe) {
                        // empty catch block
                    }
                    this.out = null;
                }
                if (this.in != null) {
                    try {
                        this.in.close();
                    }
                    catch (IOException ioe) {
                        // empty catch block
                    }
                    this.in = null;
                }
                if (this.socket != null) {
                    try {
                        this.socket.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    this.socket = null;
                }
            }
            this.start_button.setEnabled(true);
            this.stop_button.setEnabled(false);
            this.setStatus("The SOAP Monitor is stopped.");
        }

        public void run() {
            while (this.socket != null) {
                try {
                    Integer message_type = (Integer)this.in.readObject();
                    switch (message_type) {
                        case 0: {
                            Long id = (Long)this.in.readObject();
                            String target = (String)this.in.readObject();
                            String soap = (String)this.in.readObject();
                            SOAPMonitorData data = new SOAPMonitorData(id, target, soap);
                            this.model.addData(data);
                            int selected = this.table.getSelectedRow();
                            if (selected != 0 || !this.model.filterMatch(data)) break;
                            this.valueChanged(null);
                            break;
                        }
                        case 1: {
                            int row;
                            Long id = (Long)this.in.readObject();
                            String soap = (String)this.in.readObject();
                            SOAPMonitorData data = this.model.findData(id);
                            if (data == null) break;
                            boolean update_needed = false;
                            int selected = this.table.getSelectedRow();
                            if (selected == 0) {
                                update_needed = true;
                            }
                            if ((row = this.model.findRow(data)) != -1 && row == selected) {
                                update_needed = true;
                            }
                            data.setSOAPResponse(soap);
                            this.model.updateData(data);
                            if (!update_needed) break;
                            this.valueChanged(null);
                        }
                    }
                }
                catch (Exception e) {
                    if (!this.stop_button.isEnabled()) continue;
                    this.stop();
                    this.setErrorStatus("The server communication has been terminated.");
                }
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            int row = this.table.getSelectedRow();
            if (row > 0) {
                this.remove_button.setEnabled(true);
            } else {
                this.remove_button.setEnabled(false);
            }
            if (row == 0 && (row = this.model.getRowCount() - 1) == 0) {
                row = -1;
            }
            if (row == -1) {
                this.details_time_value.setText("");
                this.details_target_value.setText("");
                this.details_status_value.setText("");
                this.request_text.setText("");
                this.response_text.setText("");
            } else {
                SOAPMonitorData soap = this.model.getData(row);
                this.details_time_value.setText(soap.getTime());
                this.details_target_value.setText(soap.getTargetService());
                this.details_status_value.setText(soap.getStatus());
                if (soap.getSOAPRequest() == null) {
                    this.request_text.setText("");
                } else {
                    this.request_text.setText(soap.getSOAPRequest());
                    this.request_text.setCaretPosition(0);
                }
                if (soap.getSOAPResponse() == null) {
                    this.response_text.setText("");
                } else {
                    this.response_text.setText(soap.getSOAPResponse());
                    this.response_text.setCaretPosition(0);
                }
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.remove_button) {
                int row = this.table.getSelectedRow();
                this.model.removeRow(row);
                this.table.clearSelection();
                this.table.repaint();
                this.valueChanged(null);
            }
            if (e.getSource() == this.remove_all_button) {
                this.model.clearAll();
                this.table.setRowSelectionInterval(0, 0);
                this.table.repaint();
                this.valueChanged(null);
            }
            if (e.getSource() == this.filter_button) {
                this.filter.showDialog();
                if (this.filter.okPressed()) {
                    this.model.setFilter(this.filter);
                    this.table.repaint();
                }
            }
            if (e.getSource() == this.start_button) {
                this.start();
            }
            if (e.getSource() == this.stop_button) {
                this.stop();
            }
            if (e.getSource() == this.layout_button) {
                this.details_panel.remove(this.details_soap);
                this.details_soap.removeAll();
                this.details_soap = this.details_soap.getOrientation() == 1 ? new JSplitPane(0) : new JSplitPane(1);
                this.details_soap.setTopComponent(this.request_panel);
                this.details_soap.setRightComponent(this.response_panel);
                this.details_soap.setResizeWeight(0.5);
                this.details_panel.add((Component)this.details_soap, "Center");
                this.details_panel.validate();
                this.details_panel.repaint();
            }
            if (e.getSource() == this.reflow_xml) {
                this.request_text.setReflowXML(this.reflow_xml.isSelected());
                this.response_text.setReflowXML(this.reflow_xml.isSelected());
            }
        }
    }

    class LoginDlg
    extends JDialog
    implements ActionListener {
        private JButton ok_button = null;
        private JButton cancel_button = null;
        private JTextField user = new JTextField(20);
        private JPasswordField pass = new JPasswordField(20);
        private JTextField url = new JTextField(20);
        private boolean loginState = false;

        public LoginDlg() {
            this.setTitle("SOAP Monitor Login");
            UIManager.put("Label.font", new Font("Dialog", 1, 12));
            JPanel panel = new JPanel();
            this.ok_button = new JButton("OK");
            this.ok_button.addActionListener(this);
            this.cancel_button = new JButton("Cancel");
            this.cancel_button.addActionListener(this);
            this.url.setText(SOAPMonitor.this.axisURL);
            JLabel userLabel = new JLabel("User:");
            JLabel passLabel = new JLabel("Password:");
            JLabel urlLabel = new JLabel("Axis URL:");
            userLabel.setHorizontalAlignment(4);
            passLabel.setHorizontalAlignment(4);
            urlLabel.setHorizontalAlignment(4);
            panel.add(userLabel);
            panel.add(this.user);
            panel.add(passLabel);
            panel.add(this.pass);
            panel.add(urlLabel);
            panel.add(this.url);
            panel.add(this.ok_button);
            panel.add(this.cancel_button);
            this.setContentPane(panel);
            this.user.setText(axisUser);
            this.pass.setText(axisPass);
            GridLayout layout = new GridLayout(4, 2);
            layout.setHgap(15);
            layout.setVgap(5);
            panel.setLayout(layout);
            this.setDefaultCloseOperation(2);
            this.setModal(true);
            this.pack();
            Dimension d = this.getToolkit().getScreenSize();
            this.setLocation((d.width - this.getWidth()) / 2, (d.height - this.getHeight()) / 2);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.ok_button) {
                this.loginState = true;
                axisUser = this.user.getText();
                axisPass = new String(this.pass.getPassword());
                this.hide();
            } else if (e.getSource() == this.cancel_button) {
                this.dispose();
            }
        }

        public String getURL() {
            return this.url.getText();
        }

        public boolean isLogin() {
            return this.loginState;
        }
    }

    class MyWindowAdapter
    extends WindowAdapter {
        MyWindowAdapter() {
        }

        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    class BarThread
    extends Thread {
        private int wait = 100;
        JProgressBar progressBar = null;

        public BarThread(JProgressBar bar) {
            this.progressBar = bar;
        }

        public void run() {
            int min = this.progressBar.getMinimum();
            int max = this.progressBar.getMaximum();
            Runnable runner = new Runnable(this){
                private final /* synthetic */ BarThread this$1;
                {
                    this.this$1 = this$1;
                }

                public void run() {
                    int val = this.this$1.progressBar.getValue();
                    this.this$1.progressBar.setValue(val + 1);
                }
            };
            for (int i = min; i < max; ++i) {
                try {
                    SwingUtilities.invokeAndWait(runner);
                    Thread.sleep(this.wait);
                    continue;
                }
                catch (InterruptedException ignoredException) {
                    continue;
                }
                catch (InvocationTargetException ignoredException) {
                    // empty catch block
                }
            }
        }
    }
}

