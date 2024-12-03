/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.jfree.ui.NumberCellRenderer;

public class RefineryUtilities {
    static /* synthetic */ Class class$java$awt$GraphicsEnvironment;
    static /* synthetic */ Class class$java$lang$Number;

    private RefineryUtilities() {
    }

    public static Point getCenterPoint() {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            Method method = (class$java$awt$GraphicsEnvironment == null ? (class$java$awt$GraphicsEnvironment = RefineryUtilities.class$("java.awt.GraphicsEnvironment")) : class$java$awt$GraphicsEnvironment).getMethod("getCenterPoint", null);
            return (Point)method.invoke((Object)localGraphicsEnvironment, (Object[])null);
        }
        catch (Exception e) {
            Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
            return new Point(s.width / 2, s.height / 2);
        }
    }

    public static Rectangle getMaximumWindowBounds() {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            Method method = (class$java$awt$GraphicsEnvironment == null ? (class$java$awt$GraphicsEnvironment = RefineryUtilities.class$("java.awt.GraphicsEnvironment")) : class$java$awt$GraphicsEnvironment).getMethod("getMaximumWindowBounds", null);
            return (Rectangle)method.invoke((Object)localGraphicsEnvironment, (Object[])null);
        }
        catch (Exception e) {
            Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
            return new Rectangle(0, 0, s.width, s.height);
        }
    }

    public static void centerFrameOnScreen(Window frame) {
        RefineryUtilities.positionFrameOnScreen(frame, 0.5, 0.5);
    }

    public static void positionFrameOnScreen(Window frame, double horizontalPercent, double verticalPercent) {
        Rectangle s = RefineryUtilities.getMaximumWindowBounds();
        Dimension f = frame.getSize();
        int w = Math.max(s.width - f.width, 0);
        int h = Math.max(s.height - f.height, 0);
        int x = (int)(horizontalPercent * (double)w) + s.x;
        int y = (int)(verticalPercent * (double)h) + s.y;
        frame.setBounds(x, y, f.width, f.height);
    }

    public static void positionFrameRandomly(Window frame) {
        RefineryUtilities.positionFrameOnScreen(frame, Math.random(), Math.random());
    }

    public static void centerDialogInParent(Dialog dialog) {
        RefineryUtilities.positionDialogRelativeToParent(dialog, 0.5, 0.5);
    }

    public static void positionDialogRelativeToParent(Dialog dialog, double horizontalPercent, double verticalPercent) {
        Dimension d = dialog.getSize();
        Container parent = dialog.getParent();
        Dimension p = parent.getSize();
        int baseX = parent.getX() - d.width;
        int baseY = parent.getY() - d.height;
        int w = d.width + p.width;
        int h = d.height + p.height;
        int x = baseX + (int)(horizontalPercent * (double)w);
        int y = baseY + (int)(verticalPercent * (double)h);
        Rectangle s = RefineryUtilities.getMaximumWindowBounds();
        x = Math.min(x, s.width - d.width);
        x = Math.max(x, 0);
        y = Math.min(y, s.height - d.height);
        y = Math.max(y, 0);
        dialog.setBounds(x + s.x, y + s.y, d.width, d.height);
    }

    public static JPanel createTablePanel(TableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(model);
        for (int columnIndex = 0; columnIndex < model.getColumnCount(); ++columnIndex) {
            TableColumn column = table.getColumnModel().getColumn(columnIndex);
            Class<?> c = model.getColumnClass(columnIndex);
            if (!c.equals(class$java$lang$Number == null ? RefineryUtilities.class$("java.lang.Number") : class$java$lang$Number)) continue;
            column.setCellRenderer(new NumberCellRenderer());
        }
        panel.add(new JScrollPane(table));
        return panel;
    }

    public static JLabel createJLabel(String text, Font font) {
        JLabel result = new JLabel(text);
        result.setFont(font);
        return result;
    }

    public static JLabel createJLabel(String text, Font font, Color color) {
        JLabel result = new JLabel(text);
        result.setFont(font);
        result.setForeground(color);
        return result;
    }

    public static JButton createJButton(String label, Font font) {
        JButton result = new JButton(label);
        result.setFont(font);
        return result;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

