/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.mozilla.universalchardet.CharsetListener;
import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.prober.CharsetProber;
import org.mozilla.universalchardet.prober.EscCharsetProber;
import org.mozilla.universalchardet.prober.Latin1Prober;
import org.mozilla.universalchardet.prober.MBCSGroupProber;
import org.mozilla.universalchardet.prober.SBCSGroupProber;

public class UniversalDetector {
    public static final float SHORTCUT_THRESHOLD = 0.95f;
    public static final float MINIMUM_THRESHOLD = 0.2f;
    private InputState inputState;
    private boolean done;
    private boolean start;
    private boolean gotData;
    private byte lastChar;
    private String detectedCharset;
    private CharsetProber[] probers;
    private CharsetProber escCharsetProber;
    private CharsetListener listener;

    public UniversalDetector() {
        this(null);
    }

    public UniversalDetector(CharsetListener listener) {
        this.listener = listener;
        this.escCharsetProber = null;
        this.probers = new CharsetProber[3];
        this.reset();
    }

    public boolean isDone() {
        return this.done;
    }

    public String getDetectedCharset() {
        return this.detectedCharset;
    }

    public void setListener(CharsetListener listener) {
        this.listener = listener;
    }

    public CharsetListener getListener() {
        return this.listener;
    }

    public void handleData(byte[] buf) {
        this.handleData(buf, 0, buf.length);
    }

    public void handleData(byte[] buf, int offset, int length) {
        if (this.done) {
            return;
        }
        if (length > 0) {
            this.gotData = true;
        }
        if (this.start) {
            String detectedBOM;
            this.start = false;
            if (length > 3 && (detectedBOM = UniversalDetector.detectCharsetFromBOM(buf, offset)) != null) {
                this.detectedCharset = detectedBOM;
                this.done = true;
                return;
            }
        }
        int maxPos = offset + length;
        for (int i = offset; i < maxPos; ++i) {
            int c = buf[i] & 0xFF;
            if ((c & 0x80) != 0 && c != 160) {
                if (this.inputState == InputState.HIGHBYTE) continue;
                this.inputState = InputState.HIGHBYTE;
                if (this.escCharsetProber != null) {
                    this.escCharsetProber = null;
                }
                if (this.probers[0] == null) {
                    this.probers[0] = new MBCSGroupProber();
                }
                if (this.probers[1] == null) {
                    this.probers[1] = new SBCSGroupProber();
                }
                if (this.probers[2] != null) continue;
                this.probers[2] = new Latin1Prober();
                continue;
            }
            if (this.inputState == InputState.PURE_ASCII && (c == 27 || c == 123 && this.lastChar == 126)) {
                this.inputState = InputState.ESC_ASCII;
            }
            this.lastChar = buf[i];
        }
        if (this.inputState == InputState.ESC_ASCII) {
            CharsetProber.ProbingState st;
            if (this.escCharsetProber == null) {
                this.escCharsetProber = new EscCharsetProber();
            }
            if ((st = this.escCharsetProber.handleData(buf, offset, length)) == CharsetProber.ProbingState.FOUND_IT) {
                this.done = true;
                this.detectedCharset = this.escCharsetProber.getCharSetName();
            }
        } else if (this.inputState == InputState.HIGHBYTE) {
            for (int i = 0; i < this.probers.length; ++i) {
                CharsetProber.ProbingState st = this.probers[i].handleData(buf, offset, length);
                if (st != CharsetProber.ProbingState.FOUND_IT) continue;
                this.done = true;
                this.detectedCharset = this.probers[i].getCharSetName();
                return;
            }
        }
    }

    public static String detectCharsetFromBOM(byte[] buf) {
        return UniversalDetector.detectCharsetFromBOM(buf, 0);
    }

    private static String detectCharsetFromBOM(byte[] buf, int offset) {
        if (buf.length > offset + 3) {
            int b1 = buf[offset] & 0xFF;
            int b2 = buf[offset + 1] & 0xFF;
            int b3 = buf[offset + 2] & 0xFF;
            int b4 = buf[offset + 3] & 0xFF;
            switch (b1) {
                case 239: {
                    if (b2 != 187 || b3 != 191) break;
                    return Constants.CHARSET_UTF_8;
                }
                case 254: {
                    if (b2 == 255 && b3 == 0 && b4 == 0) {
                        return Constants.CHARSET_X_ISO_10646_UCS_4_3412;
                    }
                    if (b2 != 255) break;
                    return Constants.CHARSET_UTF_16BE;
                }
                case 0: {
                    if (b2 == 0 && b3 == 254 && b4 == 255) {
                        return Constants.CHARSET_UTF_32BE;
                    }
                    if (b2 != 0 || b3 != 255 || b4 != 254) break;
                    return Constants.CHARSET_X_ISO_10646_UCS_4_2143;
                }
                case 255: {
                    if (b2 == 254 && b3 == 0 && b4 == 0) {
                        return Constants.CHARSET_UTF_32LE;
                    }
                    if (b2 != 254) break;
                    return Constants.CHARSET_UTF_16LE;
                }
            }
        }
        return null;
    }

    public void dataEnd() {
        if (!this.gotData) {
            return;
        }
        if (this.detectedCharset != null) {
            this.done = true;
            if (this.listener != null) {
                this.listener.report(this.detectedCharset);
            }
            return;
        }
        if (this.inputState == InputState.HIGHBYTE) {
            float maxProberConfidence = 0.0f;
            int maxProber = 0;
            for (int i = 0; i < this.probers.length; ++i) {
                float proberConfidence = this.probers[i].getConfidence();
                if (!(proberConfidence > maxProberConfidence)) continue;
                maxProberConfidence = proberConfidence;
                maxProber = i;
            }
            if (maxProberConfidence > 0.2f) {
                this.detectedCharset = this.probers[maxProber].getCharSetName();
                if (this.listener != null) {
                    this.listener.report(this.detectedCharset);
                }
            }
        } else if (this.inputState == InputState.ESC_ASCII) {
            // empty if block
        }
    }

    public void reset() {
        this.done = false;
        this.start = true;
        this.detectedCharset = null;
        this.gotData = false;
        this.inputState = InputState.PURE_ASCII;
        this.lastChar = 0;
        if (this.escCharsetProber != null) {
            this.escCharsetProber.reset();
        }
        for (int i = 0; i < this.probers.length; ++i) {
            if (this.probers[i] == null) continue;
            this.probers[i].reset();
        }
    }

    public static String detectCharset(File file) throws IOException {
        return UniversalDetector.detectCharset(file.toPath());
    }

    public static String detectCharset(Path path) throws IOException {
        try (BufferedInputStream fis = new BufferedInputStream(Files.newInputStream(path, new OpenOption[0]));){
            String string = UniversalDetector.detectCharset(fis);
            return string;
        }
    }

    public static String detectCharset(InputStream inputStream) throws IOException {
        int nread;
        byte[] buf = new byte[4096];
        UniversalDetector detector = new UniversalDetector(null);
        while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        return encoding;
    }

    public static enum InputState {
        PURE_ASCII,
        ESC_ASCII,
        HIGHBYTE;

    }
}

