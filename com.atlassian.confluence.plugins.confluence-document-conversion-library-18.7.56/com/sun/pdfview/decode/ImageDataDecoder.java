/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class ImageDataDecoder {
    static byte[] decodeImageData(BufferedImage bimg) {
        byte[] output = null;
        int type = bimg.getType();
        if (type == 1) {
            DataBufferInt db = (DataBufferInt)bimg.getData().getDataBuffer();
            int[] data = db.getData();
            output = new byte[data.length * 3];
            int i = 0;
            int offset = 0;
            while (i < data.length) {
                output[offset] = (byte)(data[i] >> 16);
                output[offset + 1] = (byte)(data[i] >> 8);
                output[offset + 2] = (byte)data[i];
                ++i;
                offset += 3;
            }
        } else if (type == 10) {
            DataBufferByte db = (DataBufferByte)bimg.getData().getDataBuffer();
            output = db.getData();
        } else if (type == 2) {
            DataBufferInt db = (DataBufferInt)bimg.getData().getDataBuffer();
            int[] data = db.getData();
            output = new byte[data.length * 4];
            int i = 0;
            int offset = 0;
            while (i < data.length) {
                output[offset] = (byte)(data[i] >> 24);
                output[offset + 1] = (byte)(data[i] >> 16);
                output[offset + 2] = (byte)(data[i] >> 8);
                output[offset + 3] = (byte)data[i];
                ++i;
                offset += 4;
            }
        } else {
            BufferedImage tmp = new BufferedImage(bimg.getWidth(), bimg.getHeight(), 1);
            Graphics2D g = tmp.createGraphics();
            g.drawImage((Image)bimg, 0, 0, null);
            g.dispose();
            DataBufferInt db = (DataBufferInt)tmp.getData().getDataBuffer();
            int[] data = db.getData();
            output = new byte[data.length * 3];
            int i = 0;
            int offset = 0;
            while (i < data.length) {
                output[offset] = (byte)(data[i] >> 16);
                output[offset + 1] = (byte)(data[i] >> 8);
                output[offset + 2] = (byte)data[i];
                ++i;
                offset += 3;
            }
            tmp.flush();
        }
        return output;
    }
}

