/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class FSScrollPane
extends JScrollPane {
    private static final long serialVersionUID = 2L;
    public static final String PAGE_DOWN = "page-down";
    public static final String PAGE_UP = "page-up";
    public static final String LINE_DOWN = "down";
    public static final String LINE_UP = "up";
    public static final String PAGE_END = "page-end";
    public static final String PAGE_START = "page-start";

    public FSScrollPane() {
        this(null);
    }

    public FSScrollPane(JPanel aview) {
        super(aview, 22, 32);
        this.getVerticalScrollBar().setUnitIncrement(15);
    }

    @Override
    public void setViewportView(Component view) {
        this.setPreferredSize(new Dimension((int)view.getSize().getWidth(), (int)view.getSize().getHeight()));
        if (view instanceof JComponent) {
            this.setDefaultInputMap((JComponent)view);
            this.setDefaultActionMap((JComponent)view);
        }
        this.addResizeListener(view);
        super.setViewportView(view);
    }

    private void setDefaultInputMap(JComponent view) {
        view.getInputMap(2).put(KeyStroke.getKeyStroke(34, 0), PAGE_DOWN);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(33, 0), PAGE_UP);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(40, 0), LINE_DOWN);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(38, 0), LINE_UP);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(35, 2), PAGE_END);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(35, 0), PAGE_END);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(36, 2), PAGE_START);
        view.getInputMap(2).put(KeyStroke.getKeyStroke(36, 0), PAGE_START);
    }

    private void setDefaultActionMap(JComponent view) {
        view.getActionMap().put(PAGE_DOWN, new AbstractAction(){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                JScrollBar sb = FSScrollPane.this.getVerticalScrollBar();
                sb.getModel().setValue(sb.getModel().getValue() + sb.getBlockIncrement(1));
            }
        });
        view.getActionMap().put(PAGE_END, new AbstractAction(){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                JScrollBar sb = FSScrollPane.this.getVerticalScrollBar();
                sb.getModel().setValue(sb.getModel().getMaximum());
            }
        });
        view.getActionMap().put(PAGE_UP, new AbstractAction(){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                JScrollBar sb = FSScrollPane.this.getVerticalScrollBar();
                sb.getModel().setValue(sb.getModel().getValue() - sb.getBlockIncrement(-1));
            }
        });
        view.getActionMap().put(PAGE_START, new AbstractAction(){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                JScrollBar sb = FSScrollPane.this.getVerticalScrollBar();
                sb.getModel().setValue(0);
            }
        });
        view.getActionMap().put(LINE_DOWN, new AbstractAction(){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                JScrollBar sb = FSScrollPane.this.getVerticalScrollBar();
                sb.getModel().setValue(sb.getModel().getValue() + sb.getUnitIncrement(1));
            }
        });
        view.getActionMap().put(LINE_UP, new AbstractAction(){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                JScrollBar sb = FSScrollPane.this.getVerticalScrollBar();
                sb.getModel().setValue(sb.getModel().getValue() - sb.getUnitIncrement(-1));
            }
        });
    }

    private void addResizeListener(Component view) {
        view.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent e) {
                JScrollBar bar = FSScrollPane.this.getVerticalScrollBar();
                int incr = (int)(FSScrollPane.this.getSize().getHeight() - (double)(bar.getUnitIncrement(1) * 3));
                FSScrollPane.this.getVerticalScrollBar().setBlockIncrement(incr);
            }
        });
    }
}

