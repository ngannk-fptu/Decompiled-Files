/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.svggen.SVGGraphics2D
 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.ToSVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.wmf.tosvg.WMFPainter;
import org.apache.batik.transcoder.wmf.tosvg.WMFRecordStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WMFTranscoder
extends ToSVGAbstractTranscoder {
    public static final String WMF_EXTENSION = ".wmf";
    public static final String SVG_EXTENSION = ".svg";

    @Override
    public void transcode(TranscoderInput input, TranscoderOutput output) throws TranscoderException {
        int vpH;
        int vpW;
        float wmfheight;
        float wmfwidth;
        DataInputStream is = this.getCompatibleInput(input);
        WMFRecordStore currentStore = new WMFRecordStore();
        try {
            currentStore.read(is);
        }
        catch (IOException e) {
            this.handler.fatalError(new TranscoderException(e));
            return;
        }
        float conv = 1.0f;
        if (this.hints.containsKey(KEY_INPUT_WIDTH)) {
            wmfwidth = ((Integer)this.hints.get(KEY_INPUT_WIDTH)).intValue();
            wmfheight = ((Integer)this.hints.get(KEY_INPUT_HEIGHT)).intValue();
        } else {
            wmfwidth = currentStore.getWidthPixels();
            wmfheight = currentStore.getHeightPixels();
        }
        float width = wmfwidth;
        float height = wmfheight;
        if (this.hints.containsKey(KEY_WIDTH)) {
            width = ((Float)this.hints.get(KEY_WIDTH)).floatValue();
            conv = width / wmfwidth;
            height = height * width / wmfwidth;
        }
        int xOffset = 0;
        int yOffset = 0;
        if (this.hints.containsKey(KEY_XOFFSET)) {
            xOffset = (Integer)this.hints.get(KEY_XOFFSET);
        }
        if (this.hints.containsKey(KEY_YOFFSET)) {
            yOffset = (Integer)this.hints.get(KEY_YOFFSET);
        }
        float sizeFactor = currentStore.getUnitsToPixels() * conv;
        int vpX = (int)(currentStore.getVpX() * sizeFactor);
        int vpY = (int)(currentStore.getVpY() * sizeFactor);
        if (this.hints.containsKey(KEY_INPUT_WIDTH)) {
            vpW = (int)((float)((Integer)this.hints.get(KEY_INPUT_WIDTH)).intValue() * conv);
            vpH = (int)((float)((Integer)this.hints.get(KEY_INPUT_HEIGHT)).intValue() * conv);
        } else {
            vpW = (int)((float)currentStore.getWidthUnits() * sizeFactor);
            vpH = (int)((float)currentStore.getHeightUnits() * sizeFactor);
        }
        WMFPainter painter = new WMFPainter(currentStore, xOffset, yOffset, conv);
        Document doc = this.createDocument(output);
        this.svgGenerator = new SVGGraphics2D(doc);
        this.svgGenerator.getGeneratorContext().setPrecision(4);
        painter.paint((Graphics)this.svgGenerator);
        this.svgGenerator.setSVGCanvasSize(new Dimension(vpW, vpH));
        Element svgRoot = this.svgGenerator.getRoot();
        svgRoot.setAttributeNS(null, "viewBox", String.valueOf(vpX) + ' ' + vpY + ' ' + vpW + ' ' + vpH);
        this.writeSVGToOutput(this.svgGenerator, svgRoot, output);
    }

    private DataInputStream getCompatibleInput(TranscoderInput input) throws TranscoderException {
        InputStream in;
        if (input == null) {
            this.handler.fatalError(new TranscoderException(String.valueOf(65280)));
        }
        if ((in = input.getInputStream()) != null) {
            return new DataInputStream(new BufferedInputStream(in));
        }
        String uri = input.getURI();
        if (uri != null) {
            try {
                URL url = new URL(uri);
                in = url.openStream();
                return new DataInputStream(new BufferedInputStream(in));
            }
            catch (MalformedURLException e) {
                this.handler.fatalError(new TranscoderException(e));
            }
            catch (IOException e) {
                this.handler.fatalError(new TranscoderException(e));
            }
        }
        this.handler.fatalError(new TranscoderException(String.valueOf(65281)));
        return null;
    }

    public static void main(String[] args) throws TranscoderException {
        if (args.length < 1) {
            System.out.println("Usage : WMFTranscoder.main <file 1> ... <file n>");
            System.exit(1);
        }
        WMFTranscoder transcoder = new WMFTranscoder();
        int nFiles = args.length;
        for (String fileName : args) {
            if (!fileName.toLowerCase().endsWith(WMF_EXTENSION)) {
                System.err.println(fileName + " does not have the " + WMF_EXTENSION + " extension. It is ignored");
                continue;
            }
            System.out.print("Processing : " + fileName + "...");
            String outputFileName = fileName.substring(0, fileName.toLowerCase().indexOf(WMF_EXTENSION)) + SVG_EXTENSION;
            File inputFile = new File(fileName);
            File outputFile = new File(outputFileName);
            try {
                TranscoderInput input = new TranscoderInput(inputFile.toURI().toURL().toString());
                TranscoderOutput output = new TranscoderOutput(new FileOutputStream(outputFile));
                transcoder.transcode(input, output);
            }
            catch (MalformedURLException e) {
                throw new TranscoderException(e);
            }
            catch (IOException e) {
                throw new TranscoderException(e);
            }
            System.out.println(".... Done");
        }
        System.exit(0);
    }
}

