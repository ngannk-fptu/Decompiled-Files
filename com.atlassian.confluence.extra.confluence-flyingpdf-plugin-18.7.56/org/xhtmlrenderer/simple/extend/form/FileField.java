/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend.form;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.InputField;

class FileField
extends InputField
implements ActionListener {
    private JTextField _pathTextField;
    private JButton _browseButton;

    public FileField(Element e, XhtmlForm form, LayoutContext context, BlockBox box) {
        super(e, form, context, box);
    }

    @Override
    public JComponent create() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        this._pathTextField = new JTextField();
        this._pathTextField.setColumns(15);
        this._browseButton = new JButton("Browse...");
        this._browseButton.addActionListener(this);
        GridBagConstraints pathConstraints = new GridBagConstraints();
        pathConstraints.fill = 2;
        pathConstraints.gridx = 0;
        pathConstraints.gridy = 0;
        pathConstraints.weightx = 1.0;
        pathConstraints.anchor = 13;
        pathConstraints.insets = new Insets(0, 0, 0, 0);
        panel.add((Component)this._pathTextField, pathConstraints);
        GridBagConstraints browseConstraints = new GridBagConstraints();
        browseConstraints.fill = 2;
        browseConstraints.gridx = 1;
        browseConstraints.gridy = 0;
        browseConstraints.weightx = 0.0;
        browseConstraints.anchor = 13;
        browseConstraints.insets = new Insets(0, 5, 0, 0);
        panel.add((Component)this._browseButton, browseConstraints);
        return panel;
    }

    @Override
    protected void applyOriginalState() {
        this._pathTextField.setText("");
    }

    @Override
    protected String[] getFieldValues() {
        return new String[]{this._pathTextField.getText()};
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser;
        int result;
        if (e.getSource() == this._browseButton && (result = (chooser = new JFileChooser()).showOpenDialog(this._browseButton)) == 0) {
            this._pathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
            this._pathTextField.setCaretPosition(0);
            this._browseButton.requestFocus();
        }
    }
}

