/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.afm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.fontbox.afm.CharMetric;
import org.apache.fontbox.afm.Composite;
import org.apache.fontbox.afm.CompositePart;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.afm.KernPair;
import org.apache.fontbox.afm.Ligature;
import org.apache.fontbox.afm.TrackKern;
import org.apache.fontbox.util.BoundingBox;
import org.apache.fontbox.util.Charsets;

public class AFMParser {
    public static final String COMMENT = "Comment";
    public static final String START_FONT_METRICS = "StartFontMetrics";
    public static final String END_FONT_METRICS = "EndFontMetrics";
    public static final String FONT_NAME = "FontName";
    public static final String FULL_NAME = "FullName";
    public static final String FAMILY_NAME = "FamilyName";
    public static final String WEIGHT = "Weight";
    public static final String FONT_BBOX = "FontBBox";
    public static final String VERSION = "Version";
    public static final String NOTICE = "Notice";
    public static final String ENCODING_SCHEME = "EncodingScheme";
    public static final String MAPPING_SCHEME = "MappingScheme";
    public static final String ESC_CHAR = "EscChar";
    public static final String CHARACTER_SET = "CharacterSet";
    public static final String CHARACTERS = "Characters";
    public static final String IS_BASE_FONT = "IsBaseFont";
    public static final String V_VECTOR = "VVector";
    public static final String IS_FIXED_V = "IsFixedV";
    public static final String CAP_HEIGHT = "CapHeight";
    public static final String X_HEIGHT = "XHeight";
    public static final String ASCENDER = "Ascender";
    public static final String DESCENDER = "Descender";
    public static final String UNDERLINE_POSITION = "UnderlinePosition";
    public static final String UNDERLINE_THICKNESS = "UnderlineThickness";
    public static final String ITALIC_ANGLE = "ItalicAngle";
    public static final String CHAR_WIDTH = "CharWidth";
    public static final String IS_FIXED_PITCH = "IsFixedPitch";
    public static final String START_CHAR_METRICS = "StartCharMetrics";
    public static final String END_CHAR_METRICS = "EndCharMetrics";
    public static final String CHARMETRICS_C = "C";
    public static final String CHARMETRICS_CH = "CH";
    public static final String CHARMETRICS_WX = "WX";
    public static final String CHARMETRICS_W0X = "W0X";
    public static final String CHARMETRICS_W1X = "W1X";
    public static final String CHARMETRICS_WY = "WY";
    public static final String CHARMETRICS_W0Y = "W0Y";
    public static final String CHARMETRICS_W1Y = "W1Y";
    public static final String CHARMETRICS_W = "W";
    public static final String CHARMETRICS_W0 = "W0";
    public static final String CHARMETRICS_W1 = "W1";
    public static final String CHARMETRICS_VV = "VV";
    public static final String CHARMETRICS_N = "N";
    public static final String CHARMETRICS_B = "B";
    public static final String CHARMETRICS_L = "L";
    public static final String STD_HW = "StdHW";
    public static final String STD_VW = "StdVW";
    public static final String START_TRACK_KERN = "StartTrackKern";
    public static final String END_TRACK_KERN = "EndTrackKern";
    public static final String START_KERN_DATA = "StartKernData";
    public static final String END_KERN_DATA = "EndKernData";
    public static final String START_KERN_PAIRS = "StartKernPairs";
    public static final String END_KERN_PAIRS = "EndKernPairs";
    public static final String START_KERN_PAIRS0 = "StartKernPairs0";
    public static final String START_KERN_PAIRS1 = "StartKernPairs1";
    public static final String START_COMPOSITES = "StartComposites";
    public static final String END_COMPOSITES = "EndComposites";
    public static final String CC = "CC";
    public static final String PCC = "PCC";
    public static final String KERN_PAIR_KP = "KP";
    public static final String KERN_PAIR_KPH = "KPH";
    public static final String KERN_PAIR_KPX = "KPX";
    public static final String KERN_PAIR_KPY = "KPY";
    private static final int BITS_IN_HEX = 16;
    private final InputStream input;

    public AFMParser(InputStream in) {
        this.input = in;
    }

    public FontMetrics parse() throws IOException {
        return this.parseFontMetric(false);
    }

    public FontMetrics parse(boolean reducedDataset) throws IOException {
        return this.parseFontMetric(reducedDataset);
    }

    private FontMetrics parseFontMetric(boolean reducedDataset) throws IOException {
        String nextCommand;
        FontMetrics fontMetrics = new FontMetrics();
        String startFontMetrics = this.readString();
        if (!START_FONT_METRICS.equals(startFontMetrics)) {
            throw new IOException("Error: The AFM file should start with StartFontMetrics and not '" + startFontMetrics + "'");
        }
        fontMetrics.setAFMVersion(this.readFloat());
        boolean charMetricsRead = false;
        while (!END_FONT_METRICS.equals(nextCommand = this.readString())) {
            if (FONT_NAME.equals(nextCommand)) {
                fontMetrics.setFontName(this.readLine());
                continue;
            }
            if (FULL_NAME.equals(nextCommand)) {
                fontMetrics.setFullName(this.readLine());
                continue;
            }
            if (FAMILY_NAME.equals(nextCommand)) {
                fontMetrics.setFamilyName(this.readLine());
                continue;
            }
            if (WEIGHT.equals(nextCommand)) {
                fontMetrics.setWeight(this.readLine());
                continue;
            }
            if (FONT_BBOX.equals(nextCommand)) {
                BoundingBox bBox = new BoundingBox();
                bBox.setLowerLeftX(this.readFloat());
                bBox.setLowerLeftY(this.readFloat());
                bBox.setUpperRightX(this.readFloat());
                bBox.setUpperRightY(this.readFloat());
                fontMetrics.setFontBBox(bBox);
                continue;
            }
            if (VERSION.equals(nextCommand)) {
                fontMetrics.setFontVersion(this.readLine());
                continue;
            }
            if (NOTICE.equals(nextCommand)) {
                fontMetrics.setNotice(this.readLine());
                continue;
            }
            if (ENCODING_SCHEME.equals(nextCommand)) {
                fontMetrics.setEncodingScheme(this.readLine());
                continue;
            }
            if (MAPPING_SCHEME.equals(nextCommand)) {
                fontMetrics.setMappingScheme(this.readInt());
                continue;
            }
            if (ESC_CHAR.equals(nextCommand)) {
                fontMetrics.setEscChar(this.readInt());
                continue;
            }
            if (CHARACTER_SET.equals(nextCommand)) {
                fontMetrics.setCharacterSet(this.readLine());
                continue;
            }
            if (CHARACTERS.equals(nextCommand)) {
                fontMetrics.setCharacters(this.readInt());
                continue;
            }
            if (IS_BASE_FONT.equals(nextCommand)) {
                fontMetrics.setIsBaseFont(this.readBoolean());
                continue;
            }
            if (V_VECTOR.equals(nextCommand)) {
                float[] vector = new float[]{this.readFloat(), this.readFloat()};
                fontMetrics.setVVector(vector);
                continue;
            }
            if (IS_FIXED_V.equals(nextCommand)) {
                fontMetrics.setIsFixedV(this.readBoolean());
                continue;
            }
            if (CAP_HEIGHT.equals(nextCommand)) {
                fontMetrics.setCapHeight(this.readFloat());
                continue;
            }
            if (X_HEIGHT.equals(nextCommand)) {
                fontMetrics.setXHeight(this.readFloat());
                continue;
            }
            if (ASCENDER.equals(nextCommand)) {
                fontMetrics.setAscender(this.readFloat());
                continue;
            }
            if (DESCENDER.equals(nextCommand)) {
                fontMetrics.setDescender(this.readFloat());
                continue;
            }
            if (STD_HW.equals(nextCommand)) {
                fontMetrics.setStandardHorizontalWidth(this.readFloat());
                continue;
            }
            if (STD_VW.equals(nextCommand)) {
                fontMetrics.setStandardVerticalWidth(this.readFloat());
                continue;
            }
            if (COMMENT.equals(nextCommand)) {
                fontMetrics.addComment(this.readLine());
                continue;
            }
            if (UNDERLINE_POSITION.equals(nextCommand)) {
                fontMetrics.setUnderlinePosition(this.readFloat());
                continue;
            }
            if (UNDERLINE_THICKNESS.equals(nextCommand)) {
                fontMetrics.setUnderlineThickness(this.readFloat());
                continue;
            }
            if (ITALIC_ANGLE.equals(nextCommand)) {
                fontMetrics.setItalicAngle(this.readFloat());
                continue;
            }
            if (CHAR_WIDTH.equals(nextCommand)) {
                float[] widths = new float[]{this.readFloat(), this.readFloat()};
                fontMetrics.setCharWidth(widths);
                continue;
            }
            if (IS_FIXED_PITCH.equals(nextCommand)) {
                fontMetrics.setFixedPitch(this.readBoolean());
                continue;
            }
            if (START_CHAR_METRICS.equals(nextCommand)) {
                int count = this.readInt();
                ArrayList<CharMetric> charMetrics = new ArrayList<CharMetric>(count);
                for (int i = 0; i < count; ++i) {
                    CharMetric charMetric = this.parseCharMetric();
                    charMetrics.add(charMetric);
                }
                String end = this.readString();
                if (!end.equals(END_CHAR_METRICS)) {
                    throw new IOException("Error: Expected 'EndCharMetrics' actual '" + end + "'");
                }
                charMetricsRead = true;
                fontMetrics.setCharMetrics(charMetrics);
                continue;
            }
            if (!reducedDataset && START_COMPOSITES.equals(nextCommand)) {
                int count = this.readInt();
                for (int i = 0; i < count; ++i) {
                    Composite part = this.parseComposite();
                    fontMetrics.addComposite(part);
                }
                String end = this.readString();
                if (end.equals(END_COMPOSITES)) continue;
                throw new IOException("Error: Expected 'EndComposites' actual '" + end + "'");
            }
            if (!reducedDataset && START_KERN_DATA.equals(nextCommand)) {
                this.parseKernData(fontMetrics);
                continue;
            }
            if (reducedDataset && charMetricsRead) break;
            throw new IOException("Unknown AFM key '" + nextCommand + "'");
        }
        return fontMetrics;
    }

    private void parseKernData(FontMetrics fontMetrics) throws IOException {
        String nextCommand;
        while (!(nextCommand = this.readString()).equals(END_KERN_DATA)) {
            KernPair pair;
            String end;
            int count;
            if (START_TRACK_KERN.equals(nextCommand)) {
                count = this.readInt();
                for (int i = 0; i < count; ++i) {
                    TrackKern kern = new TrackKern();
                    kern.setDegree(this.readInt());
                    kern.setMinPointSize(this.readFloat());
                    kern.setMinKern(this.readFloat());
                    kern.setMaxPointSize(this.readFloat());
                    kern.setMaxKern(this.readFloat());
                    fontMetrics.addTrackKern(kern);
                }
                end = this.readString();
                if (end.equals(END_TRACK_KERN)) continue;
                throw new IOException("Error: Expected 'EndTrackKern' actual '" + end + "'");
            }
            if (START_KERN_PAIRS.equals(nextCommand)) {
                count = this.readInt();
                for (int i = 0; i < count; ++i) {
                    pair = this.parseKernPair();
                    fontMetrics.addKernPair(pair);
                }
                end = this.readString();
                if (end.equals(END_KERN_PAIRS)) continue;
                throw new IOException("Error: Expected 'EndKernPairs' actual '" + end + "'");
            }
            if (START_KERN_PAIRS0.equals(nextCommand)) {
                count = this.readInt();
                for (int i = 0; i < count; ++i) {
                    pair = this.parseKernPair();
                    fontMetrics.addKernPair0(pair);
                }
                end = this.readString();
                if (end.equals(END_KERN_PAIRS)) continue;
                throw new IOException("Error: Expected 'EndKernPairs' actual '" + end + "'");
            }
            if (START_KERN_PAIRS1.equals(nextCommand)) {
                count = this.readInt();
                for (int i = 0; i < count; ++i) {
                    pair = this.parseKernPair();
                    fontMetrics.addKernPair1(pair);
                }
                end = this.readString();
                if (end.equals(END_KERN_PAIRS)) continue;
                throw new IOException("Error: Expected 'EndKernPairs' actual '" + end + "'");
            }
            throw new IOException("Unknown kerning data type '" + nextCommand + "'");
        }
    }

    private KernPair parseKernPair() throws IOException {
        KernPair kernPair = new KernPair();
        String cmd = this.readString();
        if (KERN_PAIR_KP.equals(cmd)) {
            kernPair.setFirstKernCharacter(this.readString());
            kernPair.setSecondKernCharacter(this.readString());
            kernPair.setX(this.readFloat());
            kernPair.setY(this.readFloat());
        } else if (KERN_PAIR_KPH.equals(cmd)) {
            kernPair.setFirstKernCharacter(this.hexToString(this.readString()));
            kernPair.setSecondKernCharacter(this.hexToString(this.readString()));
            kernPair.setX(this.readFloat());
            kernPair.setY(this.readFloat());
        } else if (KERN_PAIR_KPX.equals(cmd)) {
            kernPair.setFirstKernCharacter(this.readString());
            kernPair.setSecondKernCharacter(this.readString());
            kernPair.setX(this.readFloat());
            kernPair.setY(0.0f);
        } else if (KERN_PAIR_KPY.equals(cmd)) {
            kernPair.setFirstKernCharacter(this.readString());
            kernPair.setSecondKernCharacter(this.readString());
            kernPair.setX(0.0f);
            kernPair.setY(this.readFloat());
        } else {
            throw new IOException("Error expected kern pair command actual='" + cmd + "'");
        }
        return kernPair;
    }

    private String hexToString(String hexString) throws IOException {
        if (hexString.length() < 2) {
            throw new IOException("Error: Expected hex string of length >= 2 not='" + hexString);
        }
        if (hexString.charAt(0) != '<' || hexString.charAt(hexString.length() - 1) != '>') {
            throw new IOException("String should be enclosed by angle brackets '" + hexString + "'");
        }
        hexString = hexString.substring(1, hexString.length() - 1);
        byte[] data = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            String hex = Character.toString(hexString.charAt(i)) + hexString.charAt(i + 1);
            try {
                data[i / 2] = (byte)Integer.parseInt(hex, 16);
                continue;
            }
            catch (NumberFormatException e) {
                throw new IOException("Error parsing AFM file:" + e);
            }
        }
        return new String(data, Charsets.ISO_8859_1);
    }

    private Composite parseComposite() throws IOException {
        int partCount;
        Composite composite = new Composite();
        String partData = this.readLine();
        StringTokenizer tokenizer = new StringTokenizer(partData, " ;");
        String cc = tokenizer.nextToken();
        if (!cc.equals(CC)) {
            throw new IOException("Expected 'CC' actual='" + cc + "'");
        }
        String name = tokenizer.nextToken();
        composite.setName(name);
        try {
            partCount = Integer.parseInt(tokenizer.nextToken());
        }
        catch (NumberFormatException e) {
            throw new IOException("Error parsing AFM document:" + e);
        }
        for (int i = 0; i < partCount; ++i) {
            CompositePart part = new CompositePart();
            String pcc = tokenizer.nextToken();
            if (!pcc.equals(PCC)) {
                throw new IOException("Expected 'PCC' actual='" + pcc + "'");
            }
            String partName = tokenizer.nextToken();
            try {
                int x = Integer.parseInt(tokenizer.nextToken());
                int y = Integer.parseInt(tokenizer.nextToken());
                part.setName(partName);
                part.setXDisplacement(x);
                part.setYDisplacement(y);
                composite.addPart(part);
                continue;
            }
            catch (NumberFormatException e) {
                throw new IOException("Error parsing AFM document:" + e);
            }
        }
        return composite;
    }

    private CharMetric parseCharMetric() throws IOException {
        CharMetric charMetric = new CharMetric();
        String metrics = this.readLine();
        StringTokenizer metricsTokenizer = new StringTokenizer(metrics);
        try {
            while (metricsTokenizer.hasMoreTokens()) {
                String charCode;
                String nextCommand = metricsTokenizer.nextToken();
                if (nextCommand.equals(CHARMETRICS_C)) {
                    charCode = metricsTokenizer.nextToken();
                    charMetric.setCharacterCode(Integer.parseInt(charCode));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_CH)) {
                    charCode = metricsTokenizer.nextToken();
                    charMetric.setCharacterCode(Integer.parseInt(charCode, 16));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_WX)) {
                    charMetric.setWx(Float.parseFloat(metricsTokenizer.nextToken()));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W0X)) {
                    charMetric.setW0x(Float.parseFloat(metricsTokenizer.nextToken()));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W1X)) {
                    charMetric.setW1x(Float.parseFloat(metricsTokenizer.nextToken()));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_WY)) {
                    charMetric.setWy(Float.parseFloat(metricsTokenizer.nextToken()));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W0Y)) {
                    charMetric.setW0y(Float.parseFloat(metricsTokenizer.nextToken()));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W1Y)) {
                    charMetric.setW1y(Float.parseFloat(metricsTokenizer.nextToken()));
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W)) {
                    float[] w = new float[]{Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setW(w);
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W0)) {
                    float[] w0 = new float[]{Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setW0(w0);
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_W1)) {
                    float[] w1 = new float[]{Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setW1(w1);
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_VV)) {
                    float[] vv = new float[]{Float.parseFloat(metricsTokenizer.nextToken()), Float.parseFloat(metricsTokenizer.nextToken())};
                    charMetric.setVv(vv);
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_N)) {
                    charMetric.setName(metricsTokenizer.nextToken());
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_B)) {
                    BoundingBox box = new BoundingBox();
                    box.setLowerLeftX(Float.parseFloat(metricsTokenizer.nextToken()));
                    box.setLowerLeftY(Float.parseFloat(metricsTokenizer.nextToken()));
                    box.setUpperRightX(Float.parseFloat(metricsTokenizer.nextToken()));
                    box.setUpperRightY(Float.parseFloat(metricsTokenizer.nextToken()));
                    charMetric.setBoundingBox(box);
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                if (nextCommand.equals(CHARMETRICS_L)) {
                    Ligature lig = new Ligature();
                    lig.setSuccessor(metricsTokenizer.nextToken());
                    lig.setLigature(metricsTokenizer.nextToken());
                    charMetric.addLigature(lig);
                    this.verifySemicolon(metricsTokenizer);
                    continue;
                }
                throw new IOException("Unknown CharMetrics command '" + nextCommand + "'");
            }
        }
        catch (NumberFormatException e) {
            throw new IOException("Error: Corrupt AFM document:" + e);
        }
        return charMetric;
    }

    private void verifySemicolon(StringTokenizer tokenizer) throws IOException {
        if (tokenizer.hasMoreTokens()) {
            String semicolon = tokenizer.nextToken();
            if (!";".equals(semicolon)) {
                throw new IOException("Error: Expected semicolon in stream actual='" + semicolon + "'");
            }
        } else {
            throw new IOException("CharMetrics is missing a semicolon after a command");
        }
    }

    private boolean readBoolean() throws IOException {
        String theBoolean = this.readString();
        return Boolean.parseBoolean(theBoolean);
    }

    private int readInt() throws IOException {
        String theInt = this.readString();
        try {
            return Integer.parseInt(theInt);
        }
        catch (NumberFormatException e) {
            throw new IOException("Error parsing AFM document:" + e);
        }
    }

    private float readFloat() throws IOException {
        String theFloat = this.readString();
        return Float.parseFloat(theFloat);
    }

    private String readLine() throws IOException {
        StringBuilder buf = new StringBuilder(60);
        int nextByte = this.input.read();
        while (this.isWhitespace(nextByte)) {
            nextByte = this.input.read();
        }
        buf.append((char)nextByte);
        nextByte = this.input.read();
        while (nextByte != -1 && !this.isEOL(nextByte)) {
            buf.append((char)nextByte);
            nextByte = this.input.read();
        }
        return buf.toString();
    }

    private String readString() throws IOException {
        StringBuilder buf = new StringBuilder(24);
        int nextByte = this.input.read();
        while (this.isWhitespace(nextByte)) {
            nextByte = this.input.read();
        }
        buf.append((char)nextByte);
        nextByte = this.input.read();
        while (nextByte != -1 && !this.isWhitespace(nextByte)) {
            buf.append((char)nextByte);
            nextByte = this.input.read();
        }
        return buf.toString();
    }

    private boolean isEOL(int character) {
        return character == 13 || character == 10;
    }

    private boolean isWhitespace(int character) {
        return character == 32 || character == 9 || character == 13 || character == 10;
    }
}

