/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.date.SerialDate;

public class SerialDateChooserPanel
extends JPanel
implements ActionListener {
    public static final Color DEFAULT_DATE_BUTTON_COLOR = Color.red;
    public static final Color DEFAULT_MONTH_BUTTON_COLOR = Color.lightGray;
    private SerialDate date;
    private Color dateButtonColor;
    private Color monthButtonColor;
    private Color chosenOtherButtonColor = Color.darkGray;
    private int firstDayOfWeek = 1;
    private int yearSelectionRange = 20;
    private Font dateFont = new Font("SansSerif", 0, 10);
    private JComboBox monthSelector = null;
    private JComboBox yearSelector = null;
    private JButton todayButton = null;
    private JButton[] buttons = null;
    private boolean refreshing = false;

    public SerialDateChooserPanel() {
        this(SerialDate.createInstance(new Date()), false, DEFAULT_DATE_BUTTON_COLOR, DEFAULT_MONTH_BUTTON_COLOR);
    }

    public SerialDateChooserPanel(SerialDate date, boolean controlPanel) {
        this(date, controlPanel, DEFAULT_DATE_BUTTON_COLOR, DEFAULT_MONTH_BUTTON_COLOR);
    }

    public SerialDateChooserPanel(SerialDate date, boolean controlPanel, Color dateButtonColor, Color monthButtonColor) {
        super(new BorderLayout());
        this.date = date;
        this.dateButtonColor = dateButtonColor;
        this.monthButtonColor = monthButtonColor;
        this.add((Component)this.constructSelectionPanel(), "North");
        this.add((Component)this.getCalendarPanel(), "Center");
        if (controlPanel) {
            this.add((Component)this.constructControlPanel(), "South");
        }
    }

    public void setDate(SerialDate date) {
        this.date = date;
        this.monthSelector.setSelectedIndex(date.getMonth() - 1);
        this.refreshYearSelector();
        this.refreshButtons();
    }

    public SerialDate getDate() {
        return this.date;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("monthSelectionChanged")) {
            JComboBox c = (JComboBox)e.getSource();
            this.date = SerialDate.createInstance(this.date.getDayOfMonth(), c.getSelectedIndex() + 1, this.date.getYYYY());
            this.refreshButtons();
        } else if (e.getActionCommand().equals("yearSelectionChanged")) {
            if (!this.refreshing) {
                JComboBox c = (JComboBox)e.getSource();
                Integer y = (Integer)c.getSelectedItem();
                this.date = SerialDate.createInstance(this.date.getDayOfMonth(), this.date.getMonth(), y);
                this.refreshYearSelector();
                this.refreshButtons();
            }
        } else if (e.getActionCommand().equals("todayButtonClicked")) {
            this.setDate(SerialDate.createInstance(new Date()));
        } else if (e.getActionCommand().equals("dateButtonClicked")) {
            JButton b = (JButton)e.getSource();
            int i = Integer.parseInt(b.getName());
            SerialDate first = this.getFirstVisibleDate();
            SerialDate selected = SerialDate.addDays(i, first);
            this.setDate(selected);
        }
    }

    private JPanel getCalendarPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 7));
        panel.add(new JLabel("Sun", 0));
        panel.add(new JLabel("Mon", 0));
        panel.add(new JLabel("Tue", 0));
        panel.add(new JLabel("Wed", 0));
        panel.add(new JLabel("Thu", 0));
        panel.add(new JLabel("Fri", 0));
        panel.add(new JLabel("Sat", 0));
        this.buttons = new JButton[42];
        for (int i = 0; i < 42; ++i) {
            JButton button = new JButton("");
            button.setMargin(new Insets(1, 1, 1, 1));
            button.setName(Integer.toString(i));
            button.setFont(this.dateFont);
            button.setFocusPainted(false);
            button.setActionCommand("dateButtonClicked");
            button.addActionListener(this);
            this.buttons[i] = button;
            panel.add(button);
        }
        return panel;
    }

    protected Color getButtonColor(SerialDate targetDate) {
        if (this.date.equals(this.date)) {
            return this.dateButtonColor;
        }
        if (targetDate.getMonth() == this.date.getMonth()) {
            return this.monthButtonColor;
        }
        return this.chosenOtherButtonColor;
    }

    protected SerialDate getFirstVisibleDate() {
        SerialDate result = SerialDate.createInstance(1, this.date.getMonth(), this.date.getYYYY());
        result = SerialDate.addDays(-1, result);
        while (result.getDayOfWeek() != this.getFirstDayOfWeek()) {
            result = SerialDate.addDays(-1, result);
        }
        return result;
    }

    private int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    protected void refreshButtons() {
        SerialDate current = this.getFirstVisibleDate();
        for (int i = 0; i < 42; ++i) {
            JButton button = this.buttons[i];
            button.setText(String.valueOf(current.getDayOfWeek()));
            button.setBackground(this.getButtonColor(current));
            current = SerialDate.addDays(1, current);
        }
    }

    private void refreshYearSelector() {
        if (!this.refreshing) {
            this.refreshing = true;
            this.yearSelector.removeAllItems();
            Vector v = this.getYears(this.date.getYYYY());
            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                this.yearSelector.addItem(e.nextElement());
            }
            this.yearSelector.setSelectedItem(new Integer(this.date.getYYYY()));
            this.refreshing = false;
        }
    }

    private Vector getYears(int chosenYear) {
        Vector<Integer> v = new Vector<Integer>();
        for (int i = chosenYear - this.yearSelectionRange; i <= chosenYear + this.yearSelectionRange; ++i) {
            v.addElement(new Integer(i));
        }
        return v;
    }

    private JPanel constructSelectionPanel() {
        JPanel p = new JPanel();
        this.monthSelector = new JComboBox<String>(SerialDate.getMonths());
        this.monthSelector.addActionListener(this);
        this.monthSelector.setActionCommand("monthSelectionChanged");
        p.add(this.monthSelector);
        this.yearSelector = new JComboBox(this.getYears(0));
        this.yearSelector.addActionListener(this);
        this.yearSelector.setActionCommand("yearSelectionChanged");
        p.add(this.yearSelector);
        return p;
    }

    private JPanel constructControlPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        this.todayButton = new JButton("Today");
        this.todayButton.addActionListener(this);
        this.todayButton.setActionCommand("todayButtonClicked");
        p.add(this.todayButton);
        return p;
    }
}

