/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.ui.RefineryUtilities;

public class AboutPanel
extends JPanel {
    public AboutPanel(String application, String version, String copyright, String info) {
        this(application, version, copyright, info, null);
    }

    public AboutPanel(String application, String version, String copyright, String info, Image logo) {
        this.setLayout(new BorderLayout());
        JPanel textPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        JPanel appPanel = new JPanel();
        Font f1 = new Font("Dialog", 1, 14);
        JLabel appLabel = RefineryUtilities.createJLabel(application, f1, Color.black);
        appLabel.setHorizontalTextPosition(0);
        appPanel.add(appLabel);
        JPanel verPanel = new JPanel();
        Font f2 = new Font("Dialog", 0, 12);
        JLabel verLabel = RefineryUtilities.createJLabel(version, f2, Color.black);
        verLabel.setHorizontalTextPosition(0);
        verPanel.add(verLabel);
        JPanel copyrightPanel = new JPanel();
        JLabel copyrightLabel = RefineryUtilities.createJLabel(copyright, f2, Color.black);
        copyrightLabel.setHorizontalTextPosition(0);
        copyrightPanel.add(copyrightLabel);
        JPanel infoPanel = new JPanel();
        JLabel infoLabel = RefineryUtilities.createJLabel(info, f2, Color.black);
        infoLabel.setHorizontalTextPosition(0);
        infoPanel.add(infoLabel);
        textPanel.add(appPanel);
        textPanel.add(verPanel);
        textPanel.add(copyrightPanel);
        textPanel.add(infoPanel);
        this.add(textPanel);
        if (logo != null) {
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.add(new JLabel(new ImageIcon(logo)));
            imagePanel.setBorder(BorderFactory.createLineBorder(Color.black));
            JPanel imageContainer = new JPanel(new BorderLayout());
            imageContainer.add((Component)imagePanel, "North");
            this.add((Component)imageContainer, "West");
        }
    }
}

