/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.plugins.roadmap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.lang3.ArrayUtils;

public class FontUtils {
    private static final int MINIMUM_CHARACTER_IN_BOX = 3;
    private static final String ELLIPSIS = "\u2026";

    public static void main(String[] args) {
        String str = "Angela versus Isabelle. Height, advantage Isabelle. Birthing hips, advantage Isabelle. Remaining childbearing years, advantage Isabelle. Legal obligation, advantage Angela.";
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(3);
        frame.setSize(200, 200);
        frame.setVisible(true);
        frame.getContentPane().add(new JPanel(){

            @Override
            public void paint(Graphics g) {
                g.setFont(g.getFont().deriveFont(64.0f));
                FontMetrics fontMetrics = this.getFontMetrics(g.getFont());
                int width = this.getWidth();
                String[] lines = FontUtils.wrap("Angela versus Isabelle. Height, advantage Isabelle. Birthing hips, advantage Isabelle. Remaining childbearing years, advantage Isabelle. Legal obligation, advantage Angela.", width, fontMetrics);
                int y = fontMetrics.getAscent();
                g.setColor(Color.black);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                for (String line : lines) {
                    g.setColor(Color.white);
                    g.drawString(line, 0, y);
                    y += fontMetrics.getHeight();
                }
            }
        });
    }

    public static String[] wrap(String str, int width, FontMetrics fm) {
        ArrayList<Object> lines = new ArrayList<Object>();
        StringBuilder currentBlock = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            int x = fm.stringWidth(currentLine.toString() + currentBlock.toString());
            currentBlock.append(c);
            if (c == ' ') {
                currentLine.append(currentBlock.toString());
                currentBlock.setLength(0);
                continue;
            }
            if (i != str.length() - 1 && x <= width) continue;
            if (currentLine.length() == 0) {
                String s = currentBlock.toString();
                currentBlock.setLength(0);
                while (fm.stringWidth(s) > width) {
                    currentBlock.append(s.charAt(s.length() - 1));
                    s = s.substring(0, s.length() - 1);
                }
                lines.add(s);
                continue;
            }
            lines.add(currentLine.toString());
            if (i == str.length() - 1 && currentBlock.length() > 0) {
                if (x > width) {
                    lines.add(currentBlock.toString());
                } else {
                    lines.remove(lines.size() - 1);
                    lines.add(currentLine.toString() + currentBlock.toString());
                }
            }
            currentLine.setLength(0);
        }
        return lines.toArray(new String[lines.size()]);
    }

    public static String[] wrap(String str, int width, FontMetrics fm, int numberOfLine) {
        Object[] wrapText = FontUtils.wrap(str, width, fm);
        if (wrapText.length <= numberOfLine) {
            return wrapText;
        }
        String[] wrapTextLimitLine = (String[])ArrayUtils.subarray((Object[])wrapText, (int)0, (int)numberOfLine);
        String lastStringItem = wrapTextLimitLine[numberOfLine - 1];
        wrapTextLimitLine[numberOfLine - 1] = lastStringItem.substring(0, lastStringItem.length() - 2) + ELLIPSIS;
        return wrapTextLimitLine;
    }

    public static String cutTextInBox(String str, Rectangle rc, Font font, Graphics g, int margin, boolean isVertical) {
        int lengthOfBox;
        int charPerLine;
        FontMetrics fontMetrics = g.getFontMetrics(font);
        int strWidth = fontMetrics.stringWidth(str);
        int strLength = str.length();
        if (strLength <= (charPerLine = (int)((double)(strLength * ((lengthOfBox = isVertical ? rc.height : rc.width) - margin * 2)) / (double)strWidth)) || strLength < 3) {
            return str;
        }
        if (charPerLine > 3) {
            return str.substring(0, charPerLine - 1) + ELLIPSIS;
        }
        return ELLIPSIS;
    }
}

