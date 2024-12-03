/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import groovyjarjarantlr.CharScanner;
import groovyjarjarantlr.Token;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import org.codehaus.groovy.antlr.HScrollableTextPane;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

public class LexerFrame
extends JFrame
implements ActionListener {
    JSplitPane jSplitPane1 = new JSplitPane();
    JScrollPane jScrollPane1 = new JScrollPane();
    JScrollPane jScrollPane2 = new JScrollPane();
    JTextPane tokenPane = new HScrollableTextPane();
    JButton jbutton = new JButton("open");
    JPanel mainPanel = new JPanel(new BorderLayout());
    JTextArea scriptPane = new JTextArea();
    Border border1;
    Border border2;
    Class lexerClass;
    Hashtable tokens = new Hashtable();
    private Action loadFileAction = new AbstractAction("Open File..."){

        @Override
        public void actionPerformed(ActionEvent ae) {
            JFileChooser jfc = new JFileChooser();
            int response = jfc.showOpenDialog(LexerFrame.this);
            if (response != 0) {
                return;
            }
            try {
                LexerFrame.this.scanScript(jfc.getSelectedFile());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public LexerFrame(Class lexerClass, Class tokenTypesClass) {
        super("Token Steam Viewer");
        this.lexerClass = lexerClass;
        try {
            this.jbInit();
            this.setSize(500, 500);
            this.listTokens(tokenTypesClass);
            final JPopupMenu popup = new JPopupMenu();
            popup.add(this.loadFileAction);
            this.jbutton.setSize(30, 30);
            this.jbutton.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseReleased(MouseEvent e) {
                    popup.show(LexerFrame.this.scriptPane, e.getX(), e.getY());
                }
            });
            this.setDefaultCloseOperation(3);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listTokens(Class tokenTypes) throws Exception {
        Field[] field = tokenTypes.getDeclaredFields();
        for (int i = 0; i < field.length; ++i) {
            this.tokens.put(field[i].get(null), field[i].getName());
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Token token = (Token)((JComponent)ae.getSource()).getClientProperty("token");
        if (token.getType() == 1) {
            this.scriptPane.select(0, 0);
            return;
        }
        try {
            int start = this.scriptPane.getLineStartOffset(token.getLine() - 1) + token.getColumn() - 1;
            this.scriptPane.select(start, start + token.getText().length());
            this.scriptPane.requestFocus();
        }
        catch (BadLocationException badLocationException) {
            // empty catch block
        }
    }

    private void scanScript(File file) throws Exception {
        this.scriptPane.read(new FileReader(file), null);
        Constructor constructor = this.lexerClass.getConstructor(InputStream.class);
        CharScanner lexer = (CharScanner)constructor.newInstance(new FileInputStream(file));
        this.tokenPane.setEditable(true);
        this.tokenPane.setText("");
        int line = 1;
        ButtonGroup bg = new ButtonGroup();
        Token token = null;
        do {
            token = lexer.nextToken();
            JToggleButton tokenButton = new JToggleButton((String)this.tokens.get(token.getType()));
            bg.add(tokenButton);
            tokenButton.addActionListener(this);
            tokenButton.setToolTipText(token.getText());
            tokenButton.putClientProperty("token", token);
            tokenButton.setMargin(new Insets(0, 1, 0, 1));
            tokenButton.setFocusPainted(false);
            if (token.getLine() > line) {
                this.tokenPane.getDocument().insertString(this.tokenPane.getDocument().getLength(), "\n", null);
                line = token.getLine();
            }
            this.insertComponent(tokenButton);
        } while (token.getType() != 1);
        this.tokenPane.setEditable(false);
        this.tokenPane.setCaretPosition(0);
    }

    private void insertComponent(JComponent comp) {
        try {
            this.tokenPane.getDocument().insertString(this.tokenPane.getDocument().getLength(), " ", null);
        }
        catch (BadLocationException badLocationException) {
            // empty catch block
        }
        try {
            this.tokenPane.setCaretPosition(this.tokenPane.getDocument().getLength() - 1);
        }
        catch (Exception ex) {
            this.tokenPane.setCaretPosition(0);
        }
        this.tokenPane.insertComponent(comp);
    }

    private void jbInit() throws Exception {
        this.border1 = BorderFactory.createEmptyBorder();
        this.border2 = BorderFactory.createEmptyBorder();
        this.jSplitPane1.setOrientation(0);
        this.tokenPane.setEditable(false);
        this.tokenPane.setText("");
        this.scriptPane.setFont(new Font("DialogInput", 0, 12));
        this.scriptPane.setEditable(false);
        this.scriptPane.setMargin(new Insets(5, 5, 5, 5));
        this.scriptPane.setText("");
        this.jScrollPane1.setBorder(this.border1);
        this.jScrollPane2.setBorder(this.border1);
        this.jSplitPane1.setMinimumSize(new Dimension(800, 600));
        this.mainPanel.add((Component)this.jSplitPane1, "Center");
        this.mainPanel.add((Component)this.jbutton, "North");
        this.getContentPane().add(this.mainPanel);
        this.jSplitPane1.add((Component)this.jScrollPane1, "left");
        this.jScrollPane1.getViewport().add((Component)this.tokenPane, null);
        this.jSplitPane1.add((Component)this.jScrollPane2, "right");
        this.jScrollPane2.getViewport().add((Component)this.scriptPane, null);
        this.jScrollPane1.setColumnHeaderView(new JLabel(" Token Stream:"));
        this.jScrollPane2.setColumnHeaderView(new JLabel(" Input Script:"));
        this.jSplitPane1.setResizeWeight(0.5);
    }

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        new LexerFrame(GroovyLexer.class, GroovyTokenTypes.class).setVisible(true);
    }
}

