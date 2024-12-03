/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.examples.viewer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import org.jpedal.jbig2.JBIG2Decoder;
import org.jpedal.jbig2.examples.viewer.JBIG2Viewer;

public class NavigationToolbar
extends JToolBar {
    protected static final int FIRSTPAGE = 0;
    protected static final int FBACKPAGE = 1;
    protected static final int BACKPAGE = 2;
    protected static final int FORWARDPAGE = 3;
    protected static final int FFORWARDPAGE = 4;
    protected static final int LASTPAGE = 5;
    protected static final int SETPAGE = 6;
    protected JTextField currentPageBox = new JTextField(4);
    private JLabel totalNoOfPages = new JLabel();
    private JBIG2Viewer viewer;

    public NavigationToolbar(JBIG2Viewer jBIG2Viewer) {
        this.viewer = jBIG2Viewer;
        this.totalNoOfPages.setText("of 1");
        this.currentPageBox.setText("1");
        this.add(Box.createHorizontalGlue());
        this.addButton("Rewind To Start", "/org/jpedal/jbig2/examples/viewer/res/start.gif", 0);
        this.addButton("Back 10 Pages", "/org/jpedal/jbig2/examples/viewer/res/fback.gif", 1);
        this.addButton("Back", "/org/jpedal/jbig2/examples/viewer/res/back.gif", 2);
        this.add(new JLabel("Page"));
        this.currentPageBox.setMaximumSize(new Dimension(5, 50));
        this.currentPageBox.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                NavigationToolbar.this.executeCommand(6);
            }
        });
        this.add(this.currentPageBox);
        this.add(this.totalNoOfPages);
        this.addButton("Forward", "/org/jpedal/jbig2/examples/viewer/res/forward.gif", 3);
        this.addButton("Forward 10 Pages", "/org/jpedal/jbig2/examples/viewer/res/fforward.gif", 4);
        this.addButton("Fast Forward To End", "/org/jpedal/jbig2/examples/viewer/res/end.gif", 5);
        this.add(Box.createHorizontalGlue());
    }

    public void setTotalNoOfPages(int n) {
        this.totalNoOfPages.setText("of " + n);
    }

    public void setCurrentPage(int n) {
        this.currentPageBox.setText(n + "");
    }

    private void addButton(String string, String string2, final int n) {
        JButton jButton = new JButton();
        jButton.setIcon(new ImageIcon(this.getClass().getResource(string2)));
        jButton.setToolTipText(string);
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                NavigationToolbar.this.executeCommand(n);
            }
        });
        this.add(jButton);
    }

    public void executeCommand(int n) {
        JBIG2Decoder jBIG2Decoder = this.viewer.getDecoder();
        switch (n) {
            case 0: {
                this.viewer.displayPage(1);
                break;
            }
            case 1: {
                this.viewer.displayPage(this.viewer.getCurrentPage() - 10);
                break;
            }
            case 2: {
                this.viewer.displayPage(this.viewer.getCurrentPage() - 1);
                break;
            }
            case 3: {
                this.viewer.displayPage(this.viewer.getCurrentPage() + 1);
                break;
            }
            case 4: {
                this.viewer.displayPage(this.viewer.getCurrentPage() + 10);
                break;
            }
            case 5: {
                this.viewer.displayPage(jBIG2Decoder.getNumberOfPages());
                break;
            }
            case 6: {
                int n2 = -1;
                try {
                    n2 = Integer.parseInt(this.currentPageBox.getText());
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
                if (n2 >= 1 && n2 <= jBIG2Decoder.getNumberOfPages()) {
                    this.viewer.displayPage(n2);
                    break;
                }
                this.currentPageBox.setText(this.viewer.getCurrentPage() + "");
            }
        }
    }
}

