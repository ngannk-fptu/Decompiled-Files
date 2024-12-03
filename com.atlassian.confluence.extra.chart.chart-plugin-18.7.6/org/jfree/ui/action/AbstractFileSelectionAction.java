/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.action;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import org.jfree.ui.ExtensionFileFilter;
import org.jfree.ui.action.AbstractActionDowngrade;
import org.jfree.util.StringUtils;

public abstract class AbstractFileSelectionAction
extends AbstractActionDowngrade {
    private JFileChooser fileChooser;
    private Component parent;

    public AbstractFileSelectionAction(Component parent) {
        this.parent = parent;
    }

    protected abstract String getFileExtension();

    protected abstract String getFileDescription();

    protected File getCurrentDirectory() {
        return new File(".");
    }

    protected File performSelectFile(File selectedFile, int dialogType, boolean appendExtension) {
        if (this.fileChooser == null) {
            this.fileChooser = this.createFileChooser();
        }
        this.fileChooser.setSelectedFile(selectedFile);
        this.fileChooser.setDialogType(dialogType);
        int option = this.fileChooser.showDialog(this.parent, null);
        if (option == 0) {
            File selFile = this.fileChooser.getSelectedFile();
            String selFileName = selFile.getAbsolutePath();
            if (!StringUtils.endsWithIgnoreCase(selFileName, this.getFileExtension())) {
                selFileName = selFileName + this.getFileExtension();
            }
            return new File(selFileName);
        }
        return null;
    }

    protected JFileChooser createFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ExtensionFileFilter(this.getFileDescription(), this.getFileExtension()));
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(this.getCurrentDirectory());
        return fc;
    }
}

