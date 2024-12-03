/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.VerifierAppFrame;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.verifier.VerifierFactoryListModel;

public class GraphicalVerifier {
    private static final boolean packFrame = false;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        new GraphicalVerifier();
    }

    public GraphicalVerifier() {
        VerifierAppFrame frame = new VerifierAppFrame();
        frame.validate();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
        frame.getClassNamesJList().setModel(new VerifierFactoryListModel());
        VerifierFactory.getVerifier(Type.OBJECT.getClassName());
        frame.getClassNamesJList().setSelectedIndex(0);
    }
}

