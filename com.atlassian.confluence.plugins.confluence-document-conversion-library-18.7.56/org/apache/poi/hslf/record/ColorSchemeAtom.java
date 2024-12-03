/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.util.LittleEndian;

public final class ColorSchemeAtom
extends RecordAtom {
    private static final long _type = 2032L;
    private final byte[] _header;
    private int backgroundColourRGB;
    private int textAndLinesColourRGB;
    private int shadowsColourRGB;
    private int titleTextColourRGB;
    private int fillsColourRGB;
    private int accentColourRGB;
    private int accentAndHyperlinkColourRGB;
    private int accentAndFollowingHyperlinkColourRGB;

    public int getBackgroundColourRGB() {
        return this.backgroundColourRGB;
    }

    public void setBackgroundColourRGB(int rgb) {
        this.backgroundColourRGB = rgb;
    }

    public int getTextAndLinesColourRGB() {
        return this.textAndLinesColourRGB;
    }

    public void setTextAndLinesColourRGB(int rgb) {
        this.textAndLinesColourRGB = rgb;
    }

    public int getShadowsColourRGB() {
        return this.shadowsColourRGB;
    }

    public void setShadowsColourRGB(int rgb) {
        this.shadowsColourRGB = rgb;
    }

    public int getTitleTextColourRGB() {
        return this.titleTextColourRGB;
    }

    public void setTitleTextColourRGB(int rgb) {
        this.titleTextColourRGB = rgb;
    }

    public int getFillsColourRGB() {
        return this.fillsColourRGB;
    }

    public void setFillsColourRGB(int rgb) {
        this.fillsColourRGB = rgb;
    }

    public int getAccentColourRGB() {
        return this.accentColourRGB;
    }

    public void setAccentColourRGB(int rgb) {
        this.accentColourRGB = rgb;
    }

    public int getAccentAndHyperlinkColourRGB() {
        return this.accentAndHyperlinkColourRGB;
    }

    public void setAccentAndHyperlinkColourRGB(int rgb) {
        this.accentAndHyperlinkColourRGB = rgb;
    }

    public int getAccentAndFollowingHyperlinkColourRGB() {
        return this.accentAndFollowingHyperlinkColourRGB;
    }

    public void setAccentAndFollowingHyperlinkColourRGB(int rgb) {
        this.accentAndFollowingHyperlinkColourRGB = rgb;
    }

    protected ColorSchemeAtom(byte[] source, int start, int len) {
        if (len < 40 && source.length - start < 40) {
            throw new HSLFException("Not enough data to form a ColorSchemeAtom (always 40 bytes long) - found " + (source.length - start));
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.backgroundColourRGB = LittleEndian.getInt(source, start + 8);
        this.textAndLinesColourRGB = LittleEndian.getInt(source, start + 8 + 4);
        this.shadowsColourRGB = LittleEndian.getInt(source, start + 8 + 8);
        this.titleTextColourRGB = LittleEndian.getInt(source, start + 8 + 12);
        this.fillsColourRGB = LittleEndian.getInt(source, start + 8 + 16);
        this.accentColourRGB = LittleEndian.getInt(source, start + 8 + 20);
        this.accentAndHyperlinkColourRGB = LittleEndian.getInt(source, start + 8 + 24);
        this.accentAndFollowingHyperlinkColourRGB = LittleEndian.getInt(source, start + 8 + 28);
    }

    public ColorSchemeAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 16);
        LittleEndian.putUShort(this._header, 2, 2032);
        LittleEndian.putInt(this._header, 4, 32);
        this.backgroundColourRGB = 0xFFFFFF;
        this.textAndLinesColourRGB = 0;
        this.shadowsColourRGB = 0x808080;
        this.titleTextColourRGB = 0;
        this.fillsColourRGB = 0x99CC00;
        this.accentColourRGB = 0xCC3333;
        this.accentAndHyperlinkColourRGB = 0xFFCCCC;
        this.accentAndFollowingHyperlinkColourRGB = 0xB2B2B2;
    }

    @Override
    public long getRecordType() {
        return 2032L;
    }

    public static byte[] splitRGB(int rgb) {
        byte[] ret = new byte[3];
        UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();
        try {
            ColorSchemeAtom.writeLittleEndian(rgb, (OutputStream)baos);
        }
        catch (IOException ie) {
            throw new HSLFException(ie);
        }
        byte[] b = baos.toByteArray();
        System.arraycopy(b, 0, ret, 0, 3);
        return ret;
    }

    public static int joinRGB(byte r, byte g, byte b) {
        return ColorSchemeAtom.joinRGB(new byte[]{r, g, b});
    }

    public static int joinRGB(byte[] rgb) {
        if (rgb.length != 3) {
            throw new HSLFException("joinRGB accepts a byte array of 3 values, but got one of " + rgb.length + " values!");
        }
        byte[] with_zero = new byte[4];
        System.arraycopy(rgb, 0, with_zero, 0, 3);
        with_zero[3] = 0;
        return LittleEndian.getInt(with_zero, 0);
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        ColorSchemeAtom.writeLittleEndian(this.backgroundColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.textAndLinesColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.shadowsColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.titleTextColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.fillsColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.accentColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.accentAndHyperlinkColourRGB, out);
        ColorSchemeAtom.writeLittleEndian(this.accentAndFollowingHyperlinkColourRGB, out);
    }

    public int getColor(int idx) {
        int[] clr = new int[]{this.backgroundColourRGB, this.textAndLinesColourRGB, this.shadowsColourRGB, this.titleTextColourRGB, this.fillsColourRGB, this.accentColourRGB, this.accentAndHyperlinkColourRGB, this.accentAndFollowingHyperlinkColourRGB};
        return clr[idx];
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("backgroundColourRGB", this::getBackgroundColourRGB);
        m.put("textAndLinesColourRGB", this::getTextAndLinesColourRGB);
        m.put("shadowsColourRGB", this::getShadowsColourRGB);
        m.put("titleTextColourRGB", this::getTitleTextColourRGB);
        m.put("fillsColourRGB", this::getFillsColourRGB);
        m.put("accentColourRGB", this::getAccentColourRGB);
        m.put("accentAndHyperlinkColourRGB", this::getAccentAndHyperlinkColourRGB);
        m.put("accentAndFollowingHyperlinkColourRGB", this::getAccentAndFollowingHyperlinkColourRGB);
        return Collections.unmodifiableMap(m);
    }
}

