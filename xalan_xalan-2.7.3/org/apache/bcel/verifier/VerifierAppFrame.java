/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.verifier;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.commons.lang3.ArrayUtils;

public class VerifierAppFrame
extends JFrame {
    private static final long serialVersionUID = -542458133073307640L;
    private JPanel contentPane;
    private final JSplitPane jSplitPane1 = new JSplitPane();
    private final JPanel jPanel1 = new JPanel();
    private final JPanel jPanel2 = new JPanel();
    private final JSplitPane jSplitPane2 = new JSplitPane();
    private final JPanel jPanel3 = new JPanel();
    private final JList<String> classNamesJList = new JList();
    private final GridLayout gridLayout1 = new GridLayout();
    private final JPanel messagesPanel = new JPanel();
    private final GridLayout gridLayout2 = new GridLayout();
    private final JMenuBar jMenuBar1 = new JMenuBar();
    private final JMenu jMenu1 = new JMenu();
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JScrollPane messagesScrollPane = new JScrollPane();
    private final JScrollPane jScrollPane3 = new JScrollPane();
    private final GridLayout gridLayout4 = new GridLayout();
    private final JScrollPane jScrollPane4 = new JScrollPane();
    private final CardLayout cardLayout1 = new CardLayout();
    private String currentClass;
    private final GridLayout gridLayout3 = new GridLayout();
    private final JTextPane pass1TextPane = new JTextPane();
    private final JTextPane pass2TextPane = new JTextPane();
    private final JTextPane messagesTextPane = new JTextPane();
    private final JMenuItem newFileMenuItem = new JMenuItem();
    private final JSplitPane jSplitPane3 = new JSplitPane();
    private final JSplitPane jSplitPane4 = new JSplitPane();
    private final JScrollPane jScrollPane2 = new JScrollPane();
    private final JScrollPane jScrollPane5 = new JScrollPane();
    private final JScrollPane jScrollPane6 = new JScrollPane();
    private final JScrollPane jScrollPane7 = new JScrollPane();
    private final JList<String> pass3aJList = new JList();
    private final JList<String> pass3bJList = new JList();
    private final JTextPane pass3aTextPane = new JTextPane();
    private final JTextPane pass3bTextPane = new JTextPane();
    private final JMenu jMenu2 = new JMenu();
    private final JMenuItem whatisMenuItem = new JMenuItem();
    private final JMenuItem aboutMenuItem = new JMenuItem();

    public VerifierAppFrame() {
        this.enableEvents(64L);
        try {
            this.jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void aboutMenuItem_actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Apache Commons BCEL\nhttps://commons.apache.org/bcel\n", "Apache Commons BCEL", 1);
    }

    synchronized void classNamesJList_valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        this.currentClass = this.classNamesJList.getSelectedValue();
        try {
            this.verify();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        this.classNamesJList.setSelectedValue(this.currentClass, true);
    }

    JList<String> getClassNamesJList() {
        return this.classNamesJList;
    }

    private void jbInit() {
        this.contentPane = (JPanel)this.getContentPane();
        this.contentPane.setLayout(this.cardLayout1);
        this.setJMenuBar(this.jMenuBar1);
        this.setSize(new Dimension(708, 451));
        this.setTitle("JustIce");
        this.jPanel1.setMinimumSize(new Dimension(100, 100));
        this.jPanel1.setPreferredSize(new Dimension(100, 100));
        this.jPanel1.setLayout(this.gridLayout1);
        this.jSplitPane2.setOrientation(0);
        this.jPanel2.setLayout(this.gridLayout2);
        this.jPanel3.setMinimumSize(new Dimension(200, 100));
        this.jPanel3.setPreferredSize(new Dimension(400, 400));
        this.jPanel3.setLayout(this.gridLayout4);
        this.messagesPanel.setMinimumSize(new Dimension(100, 100));
        this.messagesPanel.setLayout(this.gridLayout3);
        this.jPanel2.setMinimumSize(new Dimension(200, 100));
        this.jMenu1.setText("File");
        this.jScrollPane1.getViewport().setBackground(Color.red);
        this.messagesScrollPane.getViewport().setBackground(Color.red);
        this.messagesScrollPane.setPreferredSize(new Dimension(10, 10));
        this.classNamesJList.addListSelectionListener(this::classNamesJList_valueChanged);
        this.classNamesJList.setSelectionMode(0);
        this.jScrollPane3.setBorder(BorderFactory.createLineBorder(Color.black));
        this.jScrollPane3.setPreferredSize(new Dimension(100, 100));
        this.gridLayout4.setRows(4);
        this.gridLayout4.setColumns(1);
        this.gridLayout4.setHgap(1);
        this.jScrollPane4.setBorder(BorderFactory.createLineBorder(Color.black));
        this.jScrollPane4.setPreferredSize(new Dimension(100, 100));
        this.pass1TextPane.setBorder(BorderFactory.createRaisedBevelBorder());
        this.pass1TextPane.setToolTipText("");
        this.pass1TextPane.setEditable(false);
        this.pass2TextPane.setBorder(BorderFactory.createRaisedBevelBorder());
        this.pass2TextPane.setEditable(false);
        this.messagesTextPane.setBorder(BorderFactory.createRaisedBevelBorder());
        this.messagesTextPane.setEditable(false);
        this.newFileMenuItem.setText("New...");
        this.newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, 2, true));
        this.newFileMenuItem.addActionListener(this::newFileMenuItem_actionPerformed);
        this.pass3aTextPane.setEditable(false);
        this.pass3bTextPane.setEditable(false);
        this.pass3aJList.addListSelectionListener(this::pass3aJList_valueChanged);
        this.pass3bJList.addListSelectionListener(this::pass3bJList_valueChanged);
        this.jMenu2.setText("Help");
        this.whatisMenuItem.setText("What is...");
        this.whatisMenuItem.addActionListener(this::whatisMenuItem_actionPerformed);
        this.aboutMenuItem.setText("About");
        this.aboutMenuItem.addActionListener(this::aboutMenuItem_actionPerformed);
        this.jSplitPane2.add((Component)this.messagesPanel, "bottom");
        this.messagesPanel.add((Component)this.messagesScrollPane, null);
        this.messagesScrollPane.getViewport().add((Component)this.messagesTextPane, null);
        this.jSplitPane2.add((Component)this.jPanel3, "top");
        this.jPanel3.add((Component)this.jScrollPane3, null);
        this.jScrollPane3.getViewport().add((Component)this.pass1TextPane, null);
        this.jPanel3.add((Component)this.jScrollPane4, null);
        this.jPanel3.add((Component)this.jSplitPane3, null);
        this.jSplitPane3.add((Component)this.jScrollPane2, "left");
        this.jScrollPane2.getViewport().add(this.pass3aJList, null);
        this.jSplitPane3.add((Component)this.jScrollPane5, "right");
        this.jScrollPane5.getViewport().add((Component)this.pass3aTextPane, null);
        this.jPanel3.add((Component)this.jSplitPane4, null);
        this.jSplitPane4.add((Component)this.jScrollPane6, "left");
        this.jScrollPane6.getViewport().add(this.pass3bJList, null);
        this.jSplitPane4.add((Component)this.jScrollPane7, "right");
        this.jScrollPane7.getViewport().add((Component)this.pass3bTextPane, null);
        this.jScrollPane4.getViewport().add((Component)this.pass2TextPane, null);
        this.jSplitPane1.add((Component)this.jPanel2, "top");
        this.jPanel2.add((Component)this.jScrollPane1, null);
        this.jSplitPane1.add((Component)this.jPanel1, "bottom");
        this.jPanel1.add((Component)this.jSplitPane2, null);
        this.jScrollPane1.getViewport().add(this.classNamesJList, null);
        this.jMenuBar1.add(this.jMenu1);
        this.jMenuBar1.add(this.jMenu2);
        this.contentPane.add((Component)this.jSplitPane1, "jSplitPane1");
        this.jMenu1.add(this.newFileMenuItem);
        this.jMenu2.add(this.whatisMenuItem);
        this.jMenu2.add(this.aboutMenuItem);
        this.jSplitPane2.setDividerLocation(300);
        this.jSplitPane3.setDividerLocation(150);
        this.jSplitPane4.setDividerLocation(150);
    }

    void newFileMenuItem_actionPerformed(ActionEvent e) {
        String className = JOptionPane.showInputDialog("Please enter the fully qualified name of a class or interface to verify:");
        if (className == null || className.isEmpty()) {
            return;
        }
        VerifierFactory.getVerifier(className);
        this.classNamesJList.setSelectedValue(className, true);
    }

    synchronized void pass3aJList_valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        Verifier v = VerifierFactory.getVerifier(this.currentClass);
        StringBuilder all3amsg = new StringBuilder();
        boolean all3aok = true;
        boolean rejected = false;
        for (int i = 0; i < this.pass3aJList.getModel().getSize(); ++i) {
            if (!this.pass3aJList.isSelectedIndex(i)) continue;
            VerificationResult vr = v.doPass3a(i);
            if (vr.getStatus() == 2) {
                all3aok = false;
                rejected = true;
            }
            JavaClass jc = null;
            try {
                jc = Repository.lookupClass(v.getClassName());
                all3amsg.append("Method '").append(jc.getMethods()[i]).append("': ").append(vr.getMessage().replace('\n', ' ')).append("\n\n");
                continue;
            }
            catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        this.pass3aTextPane.setText(all3amsg.toString());
        this.pass3aTextPane.setBackground(all3aok ? Color.green : (rejected ? Color.red : Color.yellow));
    }

    synchronized void pass3bJList_valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        Verifier v = VerifierFactory.getVerifier(this.currentClass);
        StringBuilder all3bmsg = new StringBuilder();
        boolean all3bok = true;
        boolean rejected = false;
        for (int i = 0; i < this.pass3bJList.getModel().getSize(); ++i) {
            if (!this.pass3bJList.isSelectedIndex(i)) continue;
            VerificationResult vr = v.doPass3b(i);
            if (vr.getStatus() == 2) {
                all3bok = false;
                rejected = true;
            }
            JavaClass jc = null;
            try {
                jc = Repository.lookupClass(v.getClassName());
                all3bmsg.append("Method '").append(jc.getMethods()[i]).append("': ").append(vr.getMessage().replace('\n', ' ')).append("\n\n");
                continue;
            }
            catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        this.pass3bTextPane.setText(all3bmsg.toString());
        this.pass3bTextPane.setBackground(all3bok ? Color.green : (rejected ? Color.red : Color.yellow));
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == 201) {
            System.exit(0);
        }
    }

    private void verify() throws ClassNotFoundException {
        this.setTitle("PLEASE WAIT");
        Verifier v = VerifierFactory.getVerifier(this.currentClass);
        v.flush();
        VerificationResult vr = v.doPass1();
        if (vr.getStatus() == 2) {
            this.pass1TextPane.setText(vr.getMessage());
            this.pass1TextPane.setBackground(Color.red);
            this.pass2TextPane.setText("");
            this.pass2TextPane.setBackground(Color.yellow);
            this.pass3aTextPane.setText("");
            this.pass3aJList.setListData((String[])ArrayUtils.EMPTY_STRING_ARRAY);
            this.pass3aTextPane.setBackground(Color.yellow);
            this.pass3bTextPane.setText("");
            this.pass3bJList.setListData((String[])ArrayUtils.EMPTY_STRING_ARRAY);
            this.pass3bTextPane.setBackground(Color.yellow);
        } else {
            this.pass1TextPane.setBackground(Color.green);
            this.pass1TextPane.setText(vr.getMessage());
            vr = v.doPass2();
            if (vr.getStatus() == 2) {
                this.pass2TextPane.setText(vr.getMessage());
                this.pass2TextPane.setBackground(Color.red);
                this.pass3aTextPane.setText("");
                this.pass3aTextPane.setBackground(Color.yellow);
                this.pass3aJList.setListData((String[])ArrayUtils.EMPTY_STRING_ARRAY);
                this.pass3bTextPane.setText("");
                this.pass3bTextPane.setBackground(Color.yellow);
                this.pass3bJList.setListData((String[])ArrayUtils.EMPTY_STRING_ARRAY);
            } else {
                this.pass2TextPane.setText(vr.getMessage());
                this.pass2TextPane.setBackground(Color.green);
                JavaClass jc = Repository.lookupClass(this.currentClass);
                String[] methodNames = new String[jc.getMethods().length];
                Arrays.setAll(methodNames, i -> jc.getMethods()[i].toString().replace('\n', ' ').replace('\t', ' '));
                this.pass3aJList.setListData((String[])methodNames);
                this.pass3aJList.setSelectionInterval(0, jc.getMethods().length - 1);
                this.pass3bJList.setListData((String[])methodNames);
                this.pass3bJList.setSelectionInterval(0, jc.getMethods().length - 1);
            }
        }
        String[] msgs = v.getMessages();
        this.messagesTextPane.setBackground(msgs.length == 0 ? Color.green : Color.yellow);
        StringBuilder allmsgs = new StringBuilder();
        for (int i2 = 0; i2 < msgs.length; ++i2) {
            msgs[i2] = msgs[i2].replace('\n', ' ');
            allmsgs.append(msgs[i2]).append("\n\n");
        }
        this.messagesTextPane.setText(allmsgs.toString());
        this.setTitle(this.currentClass + " - " + "Apache Commons BCEL");
    }

    void whatisMenuItem_actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "The upper four boxes to the right reflect verification passes according to The Java Virtual Machine Specification.\nThese are (in that order): Pass one, Pass two, Pass three (before data flow analysis), Pass three (data flow analysis).\nThe bottom box to the right shows (warning) messages; warnings do not cause a class to be rejected.", "Apache Commons BCEL", 1);
    }
}

