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
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jfree.date.SerialDate;

public class DateChooserPanel
extends JPanel
implements ActionListener {
    private Calendar chosenDate;
    private Color chosenDateButtonColor;
    private Color chosenMonthButtonColor;
    private Color chosenOtherButtonColor;
    private int firstDayOfWeek;
    private int yearSelectionRange = 20;
    private Font dateFont = new Font("SansSerif", 0, 10);
    private JComboBox monthSelector;
    private JComboBox yearSelector;
    private JButton todayButton;
    private JButton[] buttons;
    private boolean refreshing = false;
    private int[] WEEK_DAYS;

    public DateChooserPanel() {
        this(Calendar.getInstance(), false);
    }

    public DateChooserPanel(Calendar calendar, boolean controlPanel) {
        super(new BorderLayout());
        this.chosenDateButtonColor = UIManager.getColor("textHighlight");
        this.chosenMonthButtonColor = UIManager.getColor("control");
        this.chosenOtherButtonColor = UIManager.getColor("controlShadow");
        this.chosenDate = calendar;
        this.firstDayOfWeek = calendar.getFirstDayOfWeek();
        this.WEEK_DAYS = new int[7];
        for (int i = 0; i < 7; ++i) {
            this.WEEK_DAYS[i] = (this.firstDayOfWeek + i - 1) % 7 + 1;
        }
        this.add((Component)this.constructSelectionPanel(), "North");
        this.add((Component)this.getCalendarPanel(), "Center");
        if (controlPanel) {
            this.add((Component)this.constructControlPanel(), "South");
        }
        this.setDate(calendar.getTime());
    }

    public void setDate(Date theDate) {
        this.chosenDate.setTime(theDate);
        this.monthSelector.setSelectedIndex(this.chosenDate.get(2));
        this.refreshYearSelector();
        this.refreshButtons();
    }

    public Date getDate() {
        return this.chosenDate.getTime();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("monthSelectionChanged")) {
            JComboBox c = (JComboBox)e.getSource();
            int dayOfMonth = this.chosenDate.get(5);
            this.chosenDate.set(5, 1);
            this.chosenDate.set(2, c.getSelectedIndex());
            int maxDayOfMonth = this.chosenDate.getActualMaximum(5);
            this.chosenDate.set(5, Math.min(dayOfMonth, maxDayOfMonth));
            this.refreshButtons();
        } else if (e.getActionCommand().equals("yearSelectionChanged")) {
            if (!this.refreshing) {
                JComboBox c = (JComboBox)e.getSource();
                Integer y = (Integer)c.getSelectedItem();
                int dayOfMonth = this.chosenDate.get(5);
                this.chosenDate.set(5, 1);
                this.chosenDate.set(1, y);
                int maxDayOfMonth = this.chosenDate.getActualMaximum(5);
                this.chosenDate.set(5, Math.min(dayOfMonth, maxDayOfMonth));
                this.refreshYearSelector();
                this.refreshButtons();
            }
        } else if (e.getActionCommand().equals("todayButtonClicked")) {
            this.setDate(new Date());
        } else if (e.getActionCommand().equals("dateButtonClicked")) {
            JButton b = (JButton)e.getSource();
            int i = Integer.parseInt(b.getName());
            Calendar cal = this.getFirstVisibleDate();
            cal.add(5, i);
            this.setDate(cal.getTime());
        }
    }

    private JPanel getCalendarPanel() {
        int i;
        JPanel p = new JPanel(new GridLayout(7, 7));
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        String[] weekDays = dateFormatSymbols.getShortWeekdays();
        for (i = 0; i < this.WEEK_DAYS.length; ++i) {
            p.add(new JLabel(weekDays[this.WEEK_DAYS[i]], 0));
        }
        this.buttons = new JButton[42];
        for (i = 0; i < 42; ++i) {
            JButton b = new JButton("");
            b.setMargin(new Insets(1, 1, 1, 1));
            b.setName(Integer.toString(i));
            b.setFont(this.dateFont);
            b.setFocusPainted(false);
            b.setActionCommand("dateButtonClicked");
            b.addActionListener(this);
            this.buttons[i] = b;
            p.add(b);
        }
        return p;
    }

    private Color getButtonColor(Calendar theDate) {
        if (this.equalDates(theDate, this.chosenDate)) {
            return this.chosenDateButtonColor;
        }
        if (theDate.get(2) == this.chosenDate.get(2)) {
            return this.chosenMonthButtonColor;
        }
        return this.chosenOtherButtonColor;
    }

    private boolean equalDates(Calendar c1, Calendar c2) {
        return c1.get(5) == c2.get(5) && c1.get(2) == c2.get(2) && c1.get(1) == c2.get(1);
    }

    private Calendar getFirstVisibleDate() {
        Calendar c = Calendar.getInstance();
        c.set(this.chosenDate.get(1), this.chosenDate.get(2), 1);
        c.add(5, -1);
        while (c.get(7) != this.getFirstDayOfWeek()) {
            c.add(5, -1);
        }
        return c;
    }

    private int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    private void refreshButtons() {
        Calendar c = this.getFirstVisibleDate();
        for (int i = 0; i < 42; ++i) {
            JButton b = this.buttons[i];
            b.setText(Integer.toString(c.get(5)));
            b.setBackground(this.getButtonColor(c));
            c.add(5, 1);
        }
    }

    private void refreshYearSelector() {
        if (!this.refreshing) {
            this.refreshing = true;
            this.yearSelector.removeAllItems();
            Integer[] years = this.getYears(this.chosenDate.get(1));
            for (int i = 0; i < years.length; ++i) {
                this.yearSelector.addItem(years[i]);
            }
            this.yearSelector.setSelectedItem(new Integer(this.chosenDate.get(1)));
            this.refreshing = false;
        }
    }

    private Integer[] getYears(int chosenYear) {
        int size = this.yearSelectionRange * 2 + 1;
        int start = chosenYear - this.yearSelectionRange;
        Integer[] years = new Integer[size];
        for (int i = 0; i < size; ++i) {
            years[i] = new Integer(i + start);
        }
        return years;
    }

    private JPanel constructSelectionPanel() {
        JPanel p = new JPanel();
        int minMonth = this.chosenDate.getMinimum(2);
        int maxMonth = this.chosenDate.getMaximum(2);
        String[] months = new String[maxMonth - minMonth + 1];
        System.arraycopy(SerialDate.getMonths(), minMonth, months, 0, months.length);
        this.monthSelector = new JComboBox<String>(months);
        this.monthSelector.addActionListener(this);
        this.monthSelector.setActionCommand("monthSelectionChanged");
        p.add(this.monthSelector);
        this.yearSelector = new JComboBox<Integer>(this.getYears(0));
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

    public Color getChosenDateButtonColor() {
        return this.chosenDateButtonColor;
    }

    public void setChosenDateButtonColor(Color chosenDateButtonColor) {
        if (chosenDateButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        Color oldValue = this.chosenDateButtonColor;
        this.chosenDateButtonColor = chosenDateButtonColor;
        this.refreshButtons();
        this.firePropertyChange("chosenDateButtonColor", oldValue, chosenDateButtonColor);
    }

    public Color getChosenMonthButtonColor() {
        return this.chosenMonthButtonColor;
    }

    public void setChosenMonthButtonColor(Color chosenMonthButtonColor) {
        if (chosenMonthButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        Color oldValue = this.chosenMonthButtonColor;
        this.chosenMonthButtonColor = chosenMonthButtonColor;
        this.refreshButtons();
        this.firePropertyChange("chosenMonthButtonColor", oldValue, chosenMonthButtonColor);
    }

    public Color getChosenOtherButtonColor() {
        return this.chosenOtherButtonColor;
    }

    public void setChosenOtherButtonColor(Color chosenOtherButtonColor) {
        if (chosenOtherButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        Color oldValue = this.chosenOtherButtonColor;
        this.chosenOtherButtonColor = chosenOtherButtonColor;
        this.refreshButtons();
        this.firePropertyChange("chosenOtherButtonColor", oldValue, chosenOtherButtonColor);
    }

    public int getYearSelectionRange() {
        return this.yearSelectionRange;
    }

    public void setYearSelectionRange(int yearSelectionRange) {
        int oldYearSelectionRange = this.yearSelectionRange;
        this.yearSelectionRange = yearSelectionRange;
        this.refreshYearSelector();
        this.firePropertyChange("yearSelectionRange", oldYearSelectionRange, yearSelectionRange);
    }
}

