/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.util;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOWriteProgressListener;

public abstract class ProgressListenerBase
implements IIOReadProgressListener,
IIOWriteProgressListener {
    protected ProgressListenerBase() {
    }

    @Override
    public void imageComplete(ImageReader imageReader) {
    }

    @Override
    public void imageProgress(ImageReader imageReader, float f) {
    }

    @Override
    public void imageStarted(ImageReader imageReader, int n) {
    }

    @Override
    public void readAborted(ImageReader imageReader) {
    }

    @Override
    public void sequenceComplete(ImageReader imageReader) {
    }

    @Override
    public void sequenceStarted(ImageReader imageReader, int n) {
    }

    @Override
    public void thumbnailComplete(ImageReader imageReader) {
    }

    @Override
    public void thumbnailProgress(ImageReader imageReader, float f) {
    }

    @Override
    public void thumbnailStarted(ImageReader imageReader, int n, int n2) {
    }

    @Override
    public void imageComplete(ImageWriter imageWriter) {
    }

    @Override
    public void imageProgress(ImageWriter imageWriter, float f) {
    }

    @Override
    public void imageStarted(ImageWriter imageWriter, int n) {
    }

    @Override
    public void thumbnailComplete(ImageWriter imageWriter) {
    }

    @Override
    public void thumbnailProgress(ImageWriter imageWriter, float f) {
    }

    @Override
    public void thumbnailStarted(ImageWriter imageWriter, int n, int n2) {
    }

    @Override
    public void writeAborted(ImageWriter imageWriter) {
    }
}

