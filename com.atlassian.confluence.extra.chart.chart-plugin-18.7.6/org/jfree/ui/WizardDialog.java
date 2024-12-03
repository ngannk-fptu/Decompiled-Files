/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.ui.L1R3ButtonPanel;
import org.jfree.ui.WizardPanel;

public class WizardDialog
extends JDialog
implements ActionListener {
    private Object result = null;
    private int step;
    private WizardPanel currentPanel;
    private List panels;
    private JButton previousButton;
    private JButton nextButton;
    private JButton finishButton;
    private JButton helpButton;

    public WizardDialog(JDialog owner, boolean modal, String title, WizardPanel firstPanel) {
        super(owner, title + " : step 1", modal);
        this.currentPanel = firstPanel;
        this.step = 0;
        this.panels = new ArrayList();
        this.panels.add(firstPanel);
        this.setContentPane(this.createContent());
    }

    public WizardDialog(JFrame owner, boolean modal, String title, WizardPanel firstPanel) {
        super(owner, title + " : step 1", modal);
        this.currentPanel = firstPanel;
        this.step = 0;
        this.panels = new ArrayList();
        this.panels.add(firstPanel);
        this.setContentPane(this.createContent());
    }

    public Object getResult() {
        return this.result;
    }

    public int getStepCount() {
        return 0;
    }

    public boolean canDoPreviousPanel() {
        return this.step > 0;
    }

    public boolean canDoNextPanel() {
        return this.currentPanel.hasNextPanel();
    }

    public boolean canFinish() {
        return this.currentPanel.canFinish();
    }

    public WizardPanel getWizardPanel(int step) {
        if (step < this.panels.size()) {
            return (WizardPanel)this.panels.get(step);
        }
        return null;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("nextButton")) {
            this.next();
        } else if (command.equals("previousButton")) {
            this.previous();
        } else if (command.equals("finishButton")) {
            this.finish();
        }
    }

    public void previous() {
        if (this.step > 0) {
            WizardPanel previousPanel = this.getWizardPanel(this.step - 1);
            previousPanel.returnFromLaterStep();
            Container content = this.getContentPane();
            content.remove(this.currentPanel);
            content.add(previousPanel);
            --this.step;
            this.currentPanel = previousPanel;
            this.setTitle("Step " + (this.step + 1));
            this.enableButtons();
            this.pack();
        }
    }

    public void next() {
        WizardPanel nextPanel = this.getWizardPanel(this.step + 1);
        if (nextPanel != null) {
            if (!this.currentPanel.canRedisplayNextPanel()) {
                nextPanel = this.currentPanel.getNextPanel();
            }
        } else {
            nextPanel = this.currentPanel.getNextPanel();
        }
        ++this.step;
        if (this.step < this.panels.size()) {
            this.panels.set(this.step, nextPanel);
        } else {
            this.panels.add(nextPanel);
        }
        Container content = this.getContentPane();
        content.remove(this.currentPanel);
        content.add(nextPanel);
        this.currentPanel = nextPanel;
        this.setTitle("Step " + (this.step + 1));
        this.enableButtons();
        this.pack();
    }

    public void finish() {
        this.result = this.currentPanel.getResult();
        this.setVisible(false);
    }

    private void enableButtons() {
        this.previousButton.setEnabled(this.step > 0);
        this.nextButton.setEnabled(this.canDoNextPanel());
        this.finishButton.setEnabled(this.canFinish());
        this.helpButton.setEnabled(false);
    }

    public boolean isCancelled() {
        return false;
    }

    public JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        content.add((JPanel)this.panels.get(0));
        L1R3ButtonPanel buttons = new L1R3ButtonPanel("Help", "Previous", "Next", "Finish");
        this.helpButton = buttons.getLeftButton();
        this.helpButton.setEnabled(false);
        this.previousButton = buttons.getRightButton1();
        this.previousButton.setActionCommand("previousButton");
        this.previousButton.addActionListener(this);
        this.previousButton.setEnabled(false);
        this.nextButton = buttons.getRightButton2();
        this.nextButton.setActionCommand("nextButton");
        this.nextButton.addActionListener(this);
        this.nextButton.setEnabled(true);
        this.finishButton = buttons.getRightButton3();
        this.finishButton.setActionCommand("finishButton");
        this.finishButton.addActionListener(this);
        this.finishButton.setEnabled(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add((Component)buttons, "South");
        return content;
    }
}

