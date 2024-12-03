/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class FormatLayout
implements LayoutManager,
Serializable {
    private static final long serialVersionUID = 2866692886323930722L;
    public static final int C = 1;
    public static final int LC = 2;
    public static final int LCB = 3;
    public static final int LCLC = 4;
    public static final int LCLCB = 5;
    public static final int LCBLC = 6;
    public static final int LCBLCB = 7;
    private int[] rowFormats;
    private int rowGap;
    private int[] columnGaps;
    private int[] rowHeights;
    private int totalHeight;
    private int[] columnWidths;
    private int totalWidth;
    private int columns1and2Width;
    private int columns4and5Width;
    private int columns1to4Width;
    private int columns1to5Width;
    private int columns0to5Width;

    public FormatLayout(int rowCount, int[] rowFormats) {
        this.rowFormats = rowFormats;
        this.rowGap = 2;
        this.columnGaps = new int[5];
        this.columnGaps[0] = 10;
        this.columnGaps[1] = 5;
        this.columnGaps[2] = 5;
        this.columnGaps[3] = 10;
        this.columnGaps[4] = 5;
        this.rowHeights = new int[rowCount];
        this.columnWidths = new int[6];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension preferredLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets insets = parent.getInsets();
            int componentIndex = 0;
            int rowCount = this.rowHeights.length;
            for (int i = 0; i < this.columnWidths.length; ++i) {
                this.columnWidths[i] = 0;
            }
            this.columns1and2Width = 0;
            this.columns4and5Width = 0;
            this.columns1to4Width = 0;
            this.columns1to5Width = 0;
            this.columns0to5Width = 0;
            this.totalHeight = 0;
            block13: for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
                int format = this.rowFormats[rowIndex % this.rowFormats.length];
                switch (format) {
                    case 1: {
                        Component c0 = parent.getComponent(componentIndex);
                        this.updateC(rowIndex, c0.getPreferredSize());
                        ++componentIndex;
                        continue block13;
                    }
                    case 2: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        this.updateLC(rowIndex, c0.getPreferredSize(), c1.getPreferredSize());
                        componentIndex += 2;
                        continue block13;
                    }
                    case 3: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        this.updateLCB(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize());
                        componentIndex += 3;
                        continue block13;
                    }
                    case 4: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        this.updateLCLC(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize());
                        componentIndex += 4;
                        continue block13;
                    }
                    case 6: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        Component c4 = parent.getComponent(componentIndex + 4);
                        this.updateLCBLC(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize(), c4.getPreferredSize());
                        componentIndex += 5;
                        continue block13;
                    }
                    case 5: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        Component c4 = parent.getComponent(componentIndex + 4);
                        this.updateLCLCB(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize(), c4.getPreferredSize());
                        componentIndex += 5;
                        continue block13;
                    }
                    case 7: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        Component c4 = parent.getComponent(componentIndex + 4);
                        Component c5 = parent.getComponent(componentIndex + 5);
                        this.updateLCBLCB(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize(), c4.getPreferredSize(), c5.getPreferredSize());
                        componentIndex += 6;
                    }
                }
            }
            this.complete();
            return new Dimension(this.totalWidth + insets.left + insets.right, this.totalHeight + (rowCount - 1) * this.rowGap + insets.top + insets.bottom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Dimension minimumLayoutSize(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            Insets insets = parent.getInsets();
            int componentIndex = 0;
            int rowCount = this.rowHeights.length;
            for (int i = 0; i < this.columnWidths.length; ++i) {
                this.columnWidths[i] = 0;
            }
            this.columns1and2Width = 0;
            this.columns4and5Width = 0;
            this.columns1to4Width = 0;
            this.columns1to5Width = 0;
            this.columns0to5Width = 0;
            boolean totalHeight = false;
            block13: for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
                int format = this.rowFormats[rowIndex % this.rowFormats.length];
                switch (format) {
                    case 1: {
                        Component c0 = parent.getComponent(componentIndex);
                        this.columns0to5Width = Math.max(this.columns0to5Width, c0.getMinimumSize().width);
                        ++componentIndex;
                        continue block13;
                    }
                    case 2: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        this.updateLC(rowIndex, c0.getMinimumSize(), c1.getMinimumSize());
                        componentIndex += 2;
                        continue block13;
                    }
                    case 3: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        this.updateLCB(rowIndex, c0.getMinimumSize(), c1.getMinimumSize(), c2.getMinimumSize());
                        componentIndex += 3;
                        continue block13;
                    }
                    case 4: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        this.updateLCLC(rowIndex, c0.getMinimumSize(), c1.getMinimumSize(), c2.getMinimumSize(), c3.getMinimumSize());
                        componentIndex += 3;
                        continue block13;
                    }
                    case 6: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        Component c4 = parent.getComponent(componentIndex + 4);
                        this.updateLCBLC(rowIndex, c0.getMinimumSize(), c1.getMinimumSize(), c2.getMinimumSize(), c3.getMinimumSize(), c4.getMinimumSize());
                        componentIndex += 4;
                        continue block13;
                    }
                    case 5: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        Component c4 = parent.getComponent(componentIndex + 4);
                        this.updateLCLCB(rowIndex, c0.getMinimumSize(), c1.getMinimumSize(), c2.getMinimumSize(), c3.getMinimumSize(), c4.getMinimumSize());
                        componentIndex += 4;
                        continue block13;
                    }
                    case 7: {
                        Component c0 = parent.getComponent(componentIndex);
                        Component c1 = parent.getComponent(componentIndex + 1);
                        Component c2 = parent.getComponent(componentIndex + 2);
                        Component c3 = parent.getComponent(componentIndex + 3);
                        Component c4 = parent.getComponent(componentIndex + 4);
                        Component c5 = parent.getComponent(componentIndex + 5);
                        this.updateLCBLCB(rowIndex, c0.getMinimumSize(), c1.getMinimumSize(), c2.getMinimumSize(), c3.getMinimumSize(), c4.getMinimumSize(), c5.getMinimumSize());
                        componentIndex += 5;
                    }
                }
            }
            this.complete();
            return new Dimension(this.totalWidth + insets.left + insets.right, 0 + (rowCount - 1) * this.rowGap + insets.top + insets.bottom);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void layoutContainer(Container parent) {
        Object object = parent.getTreeLock();
        synchronized (object) {
            int[] rowX;
            Component c5;
            Component c4;
            Component c3;
            Component c2;
            Component c1;
            Component c0;
            Insets insets = parent.getInsets();
            int componentIndex = 0;
            int rowCount = this.rowHeights.length;
            for (int i = 0; i < this.columnWidths.length; ++i) {
                this.columnWidths[i] = 0;
            }
            this.columns1and2Width = 0;
            this.columns4and5Width = 0;
            this.columns1to4Width = 0;
            this.columns1to5Width = 0;
            this.columns0to5Width = parent.getBounds().width - insets.left - insets.right;
            this.totalHeight = 0;
            block22: for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
                int format = this.rowFormats[rowIndex % this.rowFormats.length];
                switch (format) {
                    case 1: {
                        c0 = parent.getComponent(componentIndex);
                        this.updateC(rowIndex, c0.getPreferredSize());
                        ++componentIndex;
                        continue block22;
                    }
                    case 2: {
                        c0 = parent.getComponent(componentIndex);
                        c1 = parent.getComponent(componentIndex + 1);
                        this.updateLC(rowIndex, c0.getPreferredSize(), c1.getPreferredSize());
                        componentIndex += 2;
                        continue block22;
                    }
                    case 3: {
                        c0 = parent.getComponent(componentIndex);
                        c1 = parent.getComponent(componentIndex + 1);
                        c2 = parent.getComponent(componentIndex + 2);
                        this.updateLCB(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize());
                        componentIndex += 3;
                        continue block22;
                    }
                    case 4: {
                        c0 = parent.getComponent(componentIndex);
                        c1 = parent.getComponent(componentIndex + 1);
                        c2 = parent.getComponent(componentIndex + 2);
                        c3 = parent.getComponent(componentIndex + 3);
                        this.updateLCLC(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize());
                        componentIndex += 4;
                        continue block22;
                    }
                    case 6: {
                        c0 = parent.getComponent(componentIndex);
                        c1 = parent.getComponent(componentIndex + 1);
                        c2 = parent.getComponent(componentIndex + 2);
                        c3 = parent.getComponent(componentIndex + 3);
                        c4 = parent.getComponent(componentIndex + 4);
                        this.updateLCBLC(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize(), c4.getPreferredSize());
                        componentIndex += 5;
                        continue block22;
                    }
                    case 5: {
                        c0 = parent.getComponent(componentIndex);
                        c1 = parent.getComponent(componentIndex + 1);
                        c2 = parent.getComponent(componentIndex + 2);
                        c3 = parent.getComponent(componentIndex + 3);
                        c4 = parent.getComponent(componentIndex + 4);
                        this.updateLCLCB(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize(), c4.getPreferredSize());
                        componentIndex += 5;
                        continue block22;
                    }
                    case 7: {
                        c0 = parent.getComponent(componentIndex);
                        c1 = parent.getComponent(componentIndex + 1);
                        c2 = parent.getComponent(componentIndex + 2);
                        c3 = parent.getComponent(componentIndex + 3);
                        c4 = parent.getComponent(componentIndex + 4);
                        c5 = parent.getComponent(componentIndex + 5);
                        this.updateLCBLCB(rowIndex, c0.getPreferredSize(), c1.getPreferredSize(), c2.getPreferredSize(), c3.getPreferredSize(), c4.getPreferredSize(), c5.getPreferredSize());
                        componentIndex += 6;
                    }
                }
            }
            this.complete();
            componentIndex = 0;
            int rowY = insets.top;
            rowX = new int[]{insets.left, rowX[0] + this.columnWidths[0] + this.columnGaps[0], rowX[1] + this.columnWidths[1] + this.columnGaps[1], rowX[2] + this.columnWidths[2] + this.columnGaps[2], rowX[3] + this.columnWidths[3] + this.columnGaps[3], rowX[4] + this.columnWidths[4] + this.columnGaps[4]};
            int w1to2 = this.columnWidths[1] + this.columnGaps[1] + this.columnWidths[2];
            int w4to5 = this.columnWidths[4] + this.columnGaps[4] + this.columnWidths[5];
            int w1to4 = w1to2 + this.columnGaps[2] + this.columnWidths[3] + this.columnGaps[3] + this.columnWidths[4];
            int w1to5 = w1to4 + this.columnGaps[4] + this.columnWidths[5];
            int w0to5 = w1to5 + this.columnWidths[0] + this.columnGaps[0];
            for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
                int format = this.rowFormats[rowIndex % this.rowFormats.length];
                switch (format) {
                    case 1: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY, w0to5, c0.getPreferredSize().height);
                        ++componentIndex;
                        break;
                    }
                    case 2: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY + (this.rowHeights[rowIndex] - c0.getPreferredSize().height) / 2, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + 1);
                        c1.setBounds(rowX[1], rowY + (this.rowHeights[rowIndex] - c1.getPreferredSize().height) / 2, w1to5, c1.getPreferredSize().height);
                        componentIndex += 2;
                        break;
                    }
                    case 3: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY + (this.rowHeights[rowIndex] - c0.getPreferredSize().height) / 2, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + 1);
                        c1.setBounds(rowX[1], rowY + (this.rowHeights[rowIndex] - c1.getPreferredSize().height) / 2, w1to4, c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + 2);
                        c2.setBounds(rowX[5], rowY + (this.rowHeights[rowIndex] - c2.getPreferredSize().height) / 2, this.columnWidths[5], c2.getPreferredSize().height);
                        componentIndex += 3;
                        break;
                    }
                    case 4: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY + (this.rowHeights[rowIndex] - c0.getPreferredSize().height) / 2, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + 1);
                        c1.setBounds(rowX[1], rowY + (this.rowHeights[rowIndex] - c1.getPreferredSize().height) / 2, w1to2, c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + 2);
                        c2.setBounds(rowX[3], rowY + (this.rowHeights[rowIndex] - c2.getPreferredSize().height) / 2, this.columnWidths[3], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + 3);
                        c3.setBounds(rowX[4], rowY + (this.rowHeights[rowIndex] - c3.getPreferredSize().height) / 2, w4to5, c3.getPreferredSize().height);
                        componentIndex += 4;
                        break;
                    }
                    case 6: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY + (this.rowHeights[rowIndex] - c0.getPreferredSize().height) / 2, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + 1);
                        c1.setBounds(rowX[1], rowY + (this.rowHeights[rowIndex] - c1.getPreferredSize().height) / 2, this.columnWidths[1], c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + 2);
                        c2.setBounds(rowX[2], rowY + (this.rowHeights[rowIndex] - c2.getPreferredSize().height) / 2, this.columnWidths[2], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + 3);
                        c3.setBounds(rowX[3], rowY + (this.rowHeights[rowIndex] - c3.getPreferredSize().height) / 2, this.columnWidths[3], c3.getPreferredSize().height);
                        c4 = parent.getComponent(componentIndex + 4);
                        c4.setBounds(rowX[4], rowY + (this.rowHeights[rowIndex] - c4.getPreferredSize().height) / 2, w4to5, c4.getPreferredSize().height);
                        componentIndex += 5;
                        break;
                    }
                    case 5: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY + (this.rowHeights[rowIndex] - c0.getPreferredSize().height) / 2, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + 1);
                        c1.setBounds(rowX[1], rowY + (this.rowHeights[rowIndex] - c1.getPreferredSize().height) / 2, w1to2, c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + 2);
                        c2.setBounds(rowX[3], rowY + (this.rowHeights[rowIndex] - c2.getPreferredSize().height) / 2, this.columnWidths[3], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + 3);
                        c3.setBounds(rowX[4], rowY + (this.rowHeights[rowIndex] - c3.getPreferredSize().height) / 2, this.columnWidths[4], c3.getPreferredSize().height);
                        c4 = parent.getComponent(componentIndex + 4);
                        c4.setBounds(rowX[5], rowY + (this.rowHeights[rowIndex] - c4.getPreferredSize().height) / 2, this.columnWidths[5], c4.getPreferredSize().height);
                        componentIndex += 5;
                        break;
                    }
                    case 7: {
                        c0 = parent.getComponent(componentIndex);
                        c0.setBounds(rowX[0], rowY + (this.rowHeights[rowIndex] - c0.getPreferredSize().height) / 2, this.columnWidths[0], c0.getPreferredSize().height);
                        c1 = parent.getComponent(componentIndex + 1);
                        c1.setBounds(rowX[1], rowY + (this.rowHeights[rowIndex] - c1.getPreferredSize().height) / 2, this.columnWidths[1], c1.getPreferredSize().height);
                        c2 = parent.getComponent(componentIndex + 2);
                        c2.setBounds(rowX[2], rowY + (this.rowHeights[rowIndex] - c2.getPreferredSize().height) / 2, this.columnWidths[2], c2.getPreferredSize().height);
                        c3 = parent.getComponent(componentIndex + 3);
                        c3.setBounds(rowX[3], rowY + (this.rowHeights[rowIndex] - c3.getPreferredSize().height) / 2, this.columnWidths[3], c3.getPreferredSize().height);
                        c4 = parent.getComponent(componentIndex + 4);
                        c4.setBounds(rowX[4], rowY + (this.rowHeights[rowIndex] - c4.getPreferredSize().height) / 2, this.columnWidths[4], c4.getPreferredSize().height);
                        c5 = parent.getComponent(componentIndex + 5);
                        c5.setBounds(rowX[5], rowY + (this.rowHeights[rowIndex] - c5.getPreferredSize().height) / 2, this.columnWidths[5], c5.getPreferredSize().height);
                        componentIndex += 6;
                    }
                }
                rowY = rowY + this.rowHeights[rowIndex] + this.rowGap;
            }
        }
    }

    protected void updateC(int rowIndex, Dimension d0) {
        this.rowHeights[rowIndex] = d0.height;
        this.totalHeight += this.rowHeights[rowIndex];
        this.columns0to5Width = Math.max(this.columns0to5Width, d0.width);
    }

    protected void updateLC(int rowIndex, Dimension d0, Dimension d1) {
        this.rowHeights[rowIndex] = Math.max(d0.height, d1.height);
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1to5Width = Math.max(this.columns1to5Width, d1.width);
    }

    protected void updateLCB(int rowIndex, Dimension d0, Dimension d1, Dimension d2) {
        this.rowHeights[rowIndex] = Math.max(d0.height, Math.max(d1.height, d2.height));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1to4Width = Math.max(this.columns1to4Width, d1.width);
        this.columnWidths[5] = Math.max(this.columnWidths[5], d2.width);
    }

    protected void updateLCLC(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3) {
        this.rowHeights[rowIndex] = Math.max(Math.max(d0.height, d1.height), Math.max(d2.height, d3.height));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1and2Width = Math.max(this.columns1and2Width, d1.width);
        this.columnWidths[3] = Math.max(this.columnWidths[3], d2.width);
        this.columns4and5Width = Math.max(this.columns4and5Width, d3.width);
    }

    protected void updateLCBLC(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3, Dimension d4) {
        this.rowHeights[rowIndex] = Math.max(d0.height, Math.max(Math.max(d1.height, d2.height), Math.max(d3.height, d4.height)));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columnWidths[1] = Math.max(this.columnWidths[1], d1.width);
        this.columnWidths[2] = Math.max(this.columnWidths[2], d2.width);
        this.columnWidths[3] = Math.max(this.columnWidths[3], d3.width);
        this.columns4and5Width = Math.max(this.columns4and5Width, d4.width);
    }

    protected void updateLCLCB(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3, Dimension d4) {
        this.rowHeights[rowIndex] = Math.max(d0.height, Math.max(Math.max(d1.height, d2.height), Math.max(d3.height, d4.height)));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columns1and2Width = Math.max(this.columns1and2Width, d1.width);
        this.columnWidths[3] = Math.max(this.columnWidths[3], d2.width);
        this.columnWidths[4] = Math.max(this.columnWidths[4], d3.width);
        this.columnWidths[5] = Math.max(this.columnWidths[5], d4.width);
    }

    protected void updateLCBLCB(int rowIndex, Dimension d0, Dimension d1, Dimension d2, Dimension d3, Dimension d4, Dimension d5) {
        this.rowHeights[rowIndex] = Math.max(Math.max(d0.height, d1.height), Math.max(Math.max(d2.height, d3.height), Math.max(d4.height, d5.height)));
        this.totalHeight += this.rowHeights[rowIndex];
        this.columnWidths[0] = Math.max(this.columnWidths[0], d0.width);
        this.columnWidths[1] = Math.max(this.columnWidths[1], d1.width);
        this.columnWidths[2] = Math.max(this.columnWidths[2], d2.width);
        this.columnWidths[3] = Math.max(this.columnWidths[3], d3.width);
        this.columnWidths[4] = Math.max(this.columnWidths[4], d4.width);
        this.columnWidths[5] = Math.max(this.columnWidths[5], d5.width);
    }

    public void complete() {
        this.columnWidths[1] = Math.max(this.columnWidths[1], this.columns1and2Width - this.columnGaps[1] - this.columnWidths[2]);
        this.columnWidths[4] = Math.max(this.columnWidths[4], Math.max(this.columns4and5Width - this.columnGaps[4] - this.columnWidths[5], Math.max(this.columns1to4Width - this.columnGaps[1] - this.columnGaps[2] - this.columnGaps[3] - this.columnWidths[1] - this.columnWidths[2] - this.columnWidths[3], this.columns1to5Width - this.columnGaps[1] - this.columnGaps[2] - this.columnGaps[3] - this.columnWidths[1] - this.columnWidths[2] - this.columnWidths[3] - this.columnGaps[4])));
        int leftWidth = this.columnWidths[0] + this.columnGaps[0] + this.columnWidths[1] + this.columnGaps[1] + this.columnWidths[2];
        int rightWidth = this.columnWidths[3] + this.columnGaps[3] + this.columnWidths[4] + this.columnGaps[4] + this.columnWidths[5];
        if (this.splitLayout()) {
            int mismatch;
            if (leftWidth > rightWidth) {
                mismatch = leftWidth - rightWidth;
                this.columnWidths[4] = this.columnWidths[4] + mismatch;
                rightWidth += mismatch;
            } else {
                mismatch = rightWidth - leftWidth;
                this.columnWidths[1] = this.columnWidths[1] + mismatch;
                leftWidth += mismatch;
            }
        }
        this.totalWidth = leftWidth + this.columnGaps[2] + rightWidth;
        if (this.columns0to5Width > this.totalWidth) {
            int spaceToAdd = this.columns0to5Width - this.totalWidth;
            if (this.splitLayout()) {
                int halfSpaceToAdd = spaceToAdd / 2;
                this.columnWidths[1] = this.columnWidths[1] + halfSpaceToAdd;
                this.columnWidths[4] = this.columnWidths[4] + spaceToAdd - halfSpaceToAdd;
                this.totalWidth += spaceToAdd;
            } else {
                this.columnWidths[1] = this.columnWidths[1] + spaceToAdd;
                this.totalWidth += spaceToAdd;
            }
        }
    }

    private boolean splitLayout() {
        for (int i = 0; i < this.rowFormats.length; ++i) {
            if (this.rowFormats[i] <= 3) continue;
            return true;
        }
        return false;
    }

    public void addLayoutComponent(Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(String name, Component comp) {
    }
}

