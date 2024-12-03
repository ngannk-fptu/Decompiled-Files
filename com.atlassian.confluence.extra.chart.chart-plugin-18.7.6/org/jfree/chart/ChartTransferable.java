/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.jfree.chart.JFreeChart;

public class ChartTransferable
implements Transferable {
    final DataFlavor imageFlavor = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");
    private JFreeChart chart;
    private int width;
    private int height;

    public ChartTransferable(JFreeChart chart, int width, int height) {
        this(chart, width, height, true);
    }

    public ChartTransferable(JFreeChart chart, int width, int height, boolean cloneData) {
        try {
            this.chart = (JFreeChart)chart.clone();
        }
        catch (CloneNotSupportedException e) {
            this.chart = chart;
        }
        this.width = width;
        this.height = height;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{this.imageFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return this.imageFlavor.equals(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (this.imageFlavor.equals(flavor)) {
            return this.chart.createBufferedImage(this.width, this.height);
        }
        throw new UnsupportedFlavorException(flavor);
    }
}

