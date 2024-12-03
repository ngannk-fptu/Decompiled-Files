/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.BaseWatchable;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.PDFTextFormat;
import com.sun.pdfview.colorspace.PDFColorSpace;
import com.sun.pdfview.colorspace.PatternSpace;
import com.sun.pdfview.font.PDFFont;
import com.sun.pdfview.pattern.PDFShader;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class PDFParser
extends BaseWatchable {
    public static final String DEBUG_DCTDECODE_DATA = "debugdctdecode";
    private Stack<Object> stack;
    private Stack<ParserState> parserStates;
    private ParserState state;
    private GeneralPath path;
    private int clip;
    private int loc;
    private boolean resend = false;
    private Tok tok;
    private boolean catchexceptions;
    private WeakReference pageRef;
    private PDFPage cmds;
    byte[] stream;
    HashMap<String, PDFObject> resources;
    public static int debuglevel = 4000;
    boolean errorwritten = false;

    public static void debug(String msg, int level) {
        if (level > debuglevel) {
            System.out.println(PDFParser.escape(msg));
        }
    }

    public static String escape(String msg) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msg.length(); ++i) {
            int c = msg.charAt(i);
            if (c != 10 && (c < 32 || c >= 127)) {
                c = 63;
            }
            sb.append((char)c);
        }
        return sb.toString();
    }

    public static void setDebugLevel(int level) {
        debuglevel = level;
    }

    public PDFParser(PDFPage cmds, byte[] stream, HashMap<String, PDFObject> resources) {
        this.pageRef = new WeakReference<PDFPage>(cmds);
        this.resources = resources;
        if (resources == null) {
            this.resources = new HashMap();
        }
        this.stream = stream;
    }

    private void throwback() {
        this.resend = true;
    }

    private Tok nextToken() {
        if (this.resend) {
            this.resend = false;
            return this.tok;
        }
        this.tok = new Tok();
        while (this.loc < this.stream.length && PDFFile.isWhiteSpace(this.stream[this.loc])) {
            ++this.loc;
        }
        if (this.loc >= this.stream.length) {
            this.tok.type = -1;
            return this.tok;
        }
        byte c = this.stream[this.loc++];
        while (c == 37 || c == 28) {
            StringBuffer comment = new StringBuffer();
            while (this.loc < this.stream.length && c != 10) {
                comment.append((char)c);
                c = this.stream[this.loc++];
            }
            if (this.loc < this.stream.length && (c = this.stream[this.loc++]) == 13) {
                c = this.stream[this.loc++];
            }
            PDFParser.debug("Read comment: " + comment.toString(), -1);
        }
        if (c == 91) {
            this.tok.type = 9;
        } else if (c == 93) {
            this.tok.type = 8;
        } else if (c == 40) {
            this.tok.type = 7;
            this.tok.name = this.readString();
        } else if (c == 123) {
            this.tok.type = 5;
        } else if (c == 125) {
            this.tok.type = 4;
        } else if (c == 60 && this.stream[this.loc++] == 60) {
            this.tok.type = 11;
        } else if (c == 62 && this.stream[this.loc++] == 62) {
            this.tok.type = 10;
        } else if (c == 60) {
            --this.loc;
            this.tok.type = 7;
            this.tok.name = this.readByteArray();
        } else if (c == 47) {
            this.tok.type = 1;
            this.tok.name = this.readName();
        } else if (c == 46 || c == 45 || c >= 48 && c <= 57) {
            --this.loc;
            this.tok.type = 3;
            this.tok.value = this.readNum();
        } else if (c >= 97 && c <= 122 || c >= 65 && c <= 90 || c == 39 || c == 34) {
            --this.loc;
            this.tok.type = 2;
            this.tok.name = this.readName();
        } else {
            System.out.println("Encountered character: " + c + " (" + (char)c + ")");
            this.tok.type = 0;
        }
        PDFParser.debug("Read token: " + this.tok, -1);
        return this.tok;
    }

    private String readName() {
        int start = this.loc;
        while (this.loc < this.stream.length && PDFFile.isRegularCharacter(this.stream[this.loc])) {
            ++this.loc;
        }
        return new String(this.stream, start, this.loc - start);
    }

    private double readNum() {
        double value;
        boolean neg;
        block5: {
            byte c;
            neg = (c = this.stream[this.loc++]) == 45;
            boolean sawdot = c == 46;
            double dotmult = sawdot ? 0.1 : 1.0;
            double d = value = c >= 48 && c <= 57 ? (double)(c - 48) : 0.0;
            while (true) {
                if ((c = this.stream[this.loc++]) == 46) {
                    if (sawdot) {
                        --this.loc;
                        break block5;
                    }
                    sawdot = true;
                    dotmult = 0.1;
                    continue;
                }
                if (c < 48 || c > 57) break;
                int val = c - 48;
                if (sawdot) {
                    value += (double)val * dotmult;
                    dotmult *= 0.1;
                    continue;
                }
                value = value * 10.0 + (double)val;
            }
            --this.loc;
        }
        if (neg) {
            value = -value;
        }
        return value;
    }

    private String readString() {
        int parenLevel = 0;
        StringBuffer sb = new StringBuffer();
        while (this.loc < this.stream.length) {
            int c;
            if ((c = this.stream[this.loc++]) == 41) {
                if (parenLevel-- == 0) {
                    break;
                }
            } else if (c == 40) {
                ++parenLevel;
            } else if (c == 92) {
                if ((c = this.stream[this.loc++]) >= 48 && c < 56) {
                    int val = 0;
                    for (int count = 0; c >= 48 && c < 56 && count < 3; ++count) {
                        val = val * 8 + c - 48;
                        c = this.stream[this.loc++];
                    }
                    --this.loc;
                    c = val;
                } else if (c == 110) {
                    c = 10;
                } else if (c == 114) {
                    c = 13;
                } else if (c == 116) {
                    c = 9;
                } else if (c == 98) {
                    c = 8;
                } else if (c == 102) {
                    c = 12;
                }
            }
            sb.append((char)c);
        }
        return sb.toString();
    }

    private String readByteArray() {
        StringBuffer buf = new StringBuffer();
        int count = 0;
        char w = '\u0000';
        while (this.loc < this.stream.length && this.stream[this.loc] != 62) {
            char c = (char)this.stream[this.loc];
            byte b = 0;
            if (c >= '0' && c <= '9') {
                b = (byte)(c - 48);
            } else if (c >= 'a' && c <= 'f') {
                b = (byte)(10 + (c - 97));
            } else if (c >= 'A' && c <= 'F') {
                b = (byte)(10 + (c - 65));
            } else {
                ++this.loc;
                continue;
            }
            int offset = 1 - count % 2;
            w = (char)(w | (0xF & b) << offset * 4);
            if (offset == 0) {
                buf.append(w);
                w = '\u0000';
            }
            ++count;
            ++this.loc;
        }
        ++this.loc;
        return buf.toString();
    }

    @Override
    public void setup() {
        this.stack = new Stack();
        this.parserStates = new Stack();
        this.state = new ParserState();
        this.path = new GeneralPath();
        this.loc = 0;
        this.clip = 0;
        this.state.fillCS = PDFColorSpace.getColorSpace(0);
        this.state.strokeCS = PDFColorSpace.getColorSpace(0);
        this.state.textFormat = new PDFTextFormat();
    }

    @Override
    public int iterate() throws Exception {
        this.cmds = (PDFPage)this.pageRef.get();
        if (this.cmds == null) {
            System.out.println("Page gone.  Stopping");
            return 5;
        }
        Object obj = this.parseObject();
        if (obj == null) {
            return 6;
        }
        if (obj instanceof Tok) {
            String cmd = ((Tok)obj).name;
            PDFParser.debug("Command: " + cmd + " (stack size is " + this.stack.size() + ")", 0);
            if (cmd.equals("q")) {
                this.parserStates.push((ParserState)this.state.clone());
                this.cmds.addPush();
            } else if (cmd.equals("Q")) {
                this.processQCmd();
            } else if (cmd.equals("cm")) {
                float[] elts = this.popFloat(6);
                AffineTransform xform = new AffineTransform(elts);
                this.cmds.addXform(xform);
            } else if (cmd.equals("w")) {
                this.cmds.addStrokeWidth(this.popFloat());
            } else if (cmd.equals("J")) {
                this.cmds.addEndCap(this.popInt());
            } else if (cmd.equals("j")) {
                this.cmds.addLineJoin(this.popInt());
            } else if (cmd.equals("M")) {
                this.cmds.addMiterLimit(this.popInt());
            } else if (cmd.equals("d")) {
                float phase = this.popFloat();
                float[] dashary = this.popFloatArray();
                this.cmds.addDash(dashary, phase);
            } else if (!cmd.equals("ri")) {
                if (cmd.equals("i")) {
                    this.popFloat();
                } else if (cmd.equals("gs")) {
                    this.setGSState(this.popString());
                } else if (cmd.equals("m")) {
                    float y = this.popFloat();
                    float x = this.popFloat();
                    this.path.moveTo(x, y);
                } else if (cmd.equals("l")) {
                    float y = this.popFloat();
                    float x = this.popFloat();
                    this.path.lineTo(x, y);
                } else if (cmd.equals("c")) {
                    float[] a = this.popFloat(6);
                    this.path.curveTo(a[0], a[1], a[2], a[3], a[4], a[5]);
                } else if (cmd.equals("v")) {
                    float[] a = this.popFloat(4);
                    Point2D cp = this.path.getCurrentPoint();
                    this.path.curveTo((float)cp.getX(), (float)cp.getY(), a[0], a[1], a[2], a[3]);
                } else if (cmd.equals("y")) {
                    float[] a = this.popFloat(4);
                    this.path.curveTo(a[0], a[1], a[2], a[3], a[2], a[3]);
                } else if (cmd.equals("h")) {
                    this.path.closePath();
                } else if (cmd.equals("re")) {
                    float[] a = this.popFloat(4);
                    this.path.moveTo(a[0], a[1]);
                    this.path.lineTo(a[0] + a[2], a[1]);
                    this.path.lineTo(a[0] + a[2], a[1] + a[3]);
                    this.path.lineTo(a[0], a[1] + a[3]);
                    this.path.closePath();
                } else if (cmd.equals("S")) {
                    this.cmds.addPath(this.path, 1 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("s")) {
                    this.path.closePath();
                    this.cmds.addPath(this.path, 1 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("f") || cmd.equals("F")) {
                    this.cmds.addPath(this.path, 2 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("f*")) {
                    this.path.setWindingRule(0);
                    this.cmds.addPath(this.path, 2 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("B")) {
                    this.cmds.addPath(this.path, 3 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("B*")) {
                    this.path.setWindingRule(0);
                    this.cmds.addPath(this.path, 3 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("b")) {
                    this.path.closePath();
                    this.cmds.addPath(this.path, 3 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("b*")) {
                    this.path.closePath();
                    this.path.setWindingRule(0);
                    this.cmds.addPath(this.path, 3 | this.clip);
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("n")) {
                    if (this.clip != 0) {
                        this.cmds.addPath(this.path, this.clip);
                    }
                    this.clip = 0;
                    this.path = new GeneralPath();
                } else if (cmd.equals("W")) {
                    this.clip = 4;
                } else if (cmd.equals("W*")) {
                    this.path.setWindingRule(0);
                    this.clip = 4;
                } else if (cmd.equals("sh")) {
                    String gdictname = this.popString();
                    PDFObject shobj = this.findResource(gdictname, "Shading");
                    this.doShader(shobj);
                } else if (cmd.equals("CS")) {
                    this.state.strokeCS = this.parseColorSpace(new PDFObject(this.stack.pop()));
                } else if (cmd.equals("cs")) {
                    this.state.fillCS = this.parseColorSpace(new PDFObject(this.stack.pop()));
                } else if (cmd.equals("SC")) {
                    int n = this.state.strokeCS.getNumComponents();
                    this.cmds.addStrokePaint(this.state.strokeCS.getPaint(this.popFloat(n)));
                } else if (cmd.equals("SCN")) {
                    if (this.state.strokeCS instanceof PatternSpace) {
                        this.cmds.addFillPaint(this.doPattern((PatternSpace)this.state.strokeCS));
                    } else {
                        int n = this.state.strokeCS.getNumComponents();
                        this.cmds.addStrokePaint(this.state.strokeCS.getPaint(this.popFloat(n)));
                    }
                } else if (cmd.equals("sc")) {
                    int n = this.state.fillCS.getNumComponents();
                    this.cmds.addFillPaint(this.state.fillCS.getPaint(this.popFloat(n)));
                } else if (cmd.equals("scn")) {
                    if (this.state.fillCS instanceof PatternSpace) {
                        this.cmds.addFillPaint(this.doPattern((PatternSpace)this.state.fillCS));
                    } else {
                        int n = this.state.fillCS.getNumComponents();
                        this.cmds.addFillPaint(this.state.fillCS.getPaint(this.popFloat(n)));
                    }
                } else if (cmd.equals("G")) {
                    this.state.strokeCS = PDFColorSpace.getColorSpace(0);
                    this.cmds.addStrokePaint(this.state.strokeCS.getPaint(this.popFloat(1)));
                } else if (cmd.equals("g")) {
                    this.state.fillCS = PDFColorSpace.getColorSpace(0);
                    this.cmds.addFillPaint(this.state.fillCS.getPaint(this.popFloat(1)));
                } else if (cmd.equals("RG")) {
                    this.state.strokeCS = PDFColorSpace.getColorSpace(1);
                    this.cmds.addStrokePaint(this.state.strokeCS.getPaint(this.popFloat(3)));
                } else if (cmd.equals("rg")) {
                    this.state.fillCS = PDFColorSpace.getColorSpace(1);
                    this.cmds.addFillPaint(this.state.fillCS.getPaint(this.popFloat(3)));
                } else if (cmd.equals("K")) {
                    this.state.strokeCS = PDFColorSpace.getColorSpace(2);
                    this.cmds.addStrokePaint(this.state.strokeCS.getPaint(this.popFloat(4)));
                } else if (cmd.equals("k")) {
                    this.state.fillCS = PDFColorSpace.getColorSpace(2);
                    this.cmds.addFillPaint(this.state.fillCS.getPaint(this.popFloat(4)));
                } else if (cmd.equals("Do")) {
                    PDFObject xobj = this.findResource(this.popString(), "XObject");
                    this.doXObject(xobj);
                } else if (cmd.equals("BT")) {
                    this.processBTCmd();
                } else if (cmd.equals("ET")) {
                    this.state.textFormat.end();
                } else if (cmd.equals("Tc")) {
                    this.state.textFormat.setCharSpacing(this.popFloat());
                } else if (cmd.equals("Tw")) {
                    this.state.textFormat.setWordSpacing(this.popFloat());
                } else if (cmd.equals("Tz")) {
                    this.state.textFormat.setHorizontalScale(this.popFloat());
                } else if (cmd.equals("TL")) {
                    this.state.textFormat.setLeading(this.popFloat());
                } else if (cmd.equals("Tf")) {
                    float sz = this.popFloat();
                    String fontref = this.popString();
                    this.state.textFormat.setFont(this.getFontFrom(fontref), sz);
                } else if (cmd.equals("Tr")) {
                    this.state.textFormat.setMode(this.popInt());
                } else if (cmd.equals("Ts")) {
                    this.state.textFormat.setRise(this.popFloat());
                } else if (cmd.equals("Td")) {
                    float y = this.popFloat();
                    float x = this.popFloat();
                    this.state.textFormat.carriageReturn(x, y);
                } else if (cmd.equals("TD")) {
                    float y = this.popFloat();
                    float x = this.popFloat();
                    this.state.textFormat.setLeading(-y);
                    this.state.textFormat.carriageReturn(x, y);
                } else if (cmd.equals("Tm")) {
                    this.state.textFormat.setMatrix(this.popFloat(6));
                } else if (cmd.equals("T*")) {
                    this.state.textFormat.carriageReturn();
                } else if (cmd.equals("Tj")) {
                    this.state.textFormat.doText(this.cmds, this.popString());
                } else if (cmd.equals("'")) {
                    this.state.textFormat.carriageReturn();
                    this.state.textFormat.doText(this.cmds, this.popString());
                } else if (cmd.equals("\"")) {
                    String string = this.popString();
                    float ac = this.popFloat();
                    float aw = this.popFloat();
                    this.state.textFormat.setWordSpacing(aw);
                    this.state.textFormat.setCharSpacing(ac);
                    this.state.textFormat.doText(this.cmds, string);
                } else if (cmd.equals("TJ")) {
                    this.state.textFormat.doText(this.cmds, this.popArray());
                } else if (cmd.equals("BI")) {
                    this.parseInlineImage();
                } else if (cmd.equals("BX")) {
                    this.catchexceptions = true;
                } else if (cmd.equals("EX")) {
                    this.catchexceptions = false;
                } else if (cmd.equals("MP")) {
                    this.popString();
                } else if (cmd.equals("DP")) {
                    Object ref = this.stack.pop();
                    this.popString();
                } else if (cmd.equals("BMC")) {
                    this.popString();
                } else if (cmd.equals("BDC")) {
                    Object ref = this.stack.pop();
                    this.popString();
                } else if (!cmd.equals("EMC")) {
                    if (cmd.equals("d0")) {
                        this.popFloat(2);
                    } else if (cmd.equals("d1")) {
                        this.popFloat(6);
                    } else if (cmd.equals("QBT")) {
                        this.processQCmd();
                        this.processBTCmd();
                    } else if (this.catchexceptions) {
                        PDFParser.debug("**** WARNING: Unknown command: " + cmd + " **************************", 10);
                    } else {
                        throw new PDFParseException("Unknown command: " + cmd);
                    }
                }
            }
            if (this.stack.size() != 0) {
                PDFParser.debug("**** WARNING! Stack not zero! (cmd=" + cmd + ", size=" + this.stack.size() + ") *************************", 4);
                this.stack.setSize(0);
            }
        } else {
            this.stack.push(obj);
        }
        this.cmds = null;
        return 4;
    }

    private void processQCmd() {
        this.cmds.addPop();
        this.state = this.parserStates.pop();
    }

    private void processBTCmd() {
        this.state.textFormat.reset();
    }

    @Override
    public void cleanup() {
        this.state.textFormat.flush();
        this.cmds.finish();
        this.stack = null;
        this.parserStates = null;
        this.state = null;
        this.path = null;
        this.cmds = null;
    }

    public void dumpStreamToError() {
        if (this.errorwritten) {
            return;
        }
        this.errorwritten = true;
        try {
            File oops = File.createTempFile("PDFError", ".err");
            FileOutputStream fos = new FileOutputStream(oops);
            fos.write(this.stream);
            fos.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public String dumpStream() {
        return PDFParser.escape(new String(this.stream).replace('\r', '\n'));
    }

    public static void emitDataFile(byte[] ary, String name) {
        try {
            File file = File.createTempFile("DateFile", name);
            FileOutputStream ostr = new FileOutputStream(file);
            System.out.println("Write: " + file.getPath());
            ostr.write(ary);
            ostr.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private PDFObject findResource(String name, String inDict) throws IOException {
        if (inDict != null) {
            PDFObject in = this.resources.get(inDict);
            if (in == null || in.getType() != 6) {
                throw new PDFParseException("No dictionary called " + inDict + " found in the resources");
            }
            return in.getDictRef(name);
        }
        return this.resources.get(name);
    }

    private void doXObject(PDFObject obj) throws IOException {
        String type = obj.getDictRef("Subtype").getStringValue();
        if (type == null) {
            type = obj.getDictRef("S").getStringValue();
        }
        if (type.equals("Image")) {
            this.doImage(obj);
        } else if (type.equals("Form")) {
            this.doForm(obj);
        } else {
            throw new PDFParseException("Unknown XObject subtype: " + type);
        }
    }

    private void doImage(PDFObject obj) throws IOException {
        this.cmds.addImage(PDFImage.createImage(obj, this.resources, false));
    }

    private void doForm(PDFObject obj) throws IOException {
        PDFPage formCmds = (PDFPage)obj.getCache();
        if (formCmds == null) {
            AffineTransform at;
            PDFObject matrix = obj.getDictRef("Matrix");
            if (matrix == null) {
                at = new AffineTransform();
            } else {
                float[] elts = new float[6];
                for (int i = 0; i < elts.length; ++i) {
                    elts[i] = matrix.getAt(i).getFloatValue();
                }
                at = new AffineTransform(elts);
            }
            PDFObject bobj = obj.getDictRef("BBox");
            Rectangle2D.Float bbox = new Rectangle2D.Float(bobj.getAt(0).getFloatValue(), bobj.getAt(1).getFloatValue(), bobj.getAt(2).getFloatValue(), bobj.getAt(3).getFloatValue());
            formCmds = new PDFPage(bbox, 0);
            formCmds.addXform(at);
            HashMap<String, PDFObject> r = new HashMap<String, PDFObject>(this.resources);
            PDFObject rsrc = obj.getDictRef("Resources");
            if (rsrc != null) {
                r.putAll(rsrc.getDictionary());
            }
            PDFParser form = new PDFParser(formCmds, obj.getStream(), r);
            form.go(true);
            obj.setCache(formCmds);
        }
        this.cmds.addPush();
        this.cmds.addCommands(formCmds);
        this.cmds.addPop();
    }

    private PDFPaint doPattern(PatternSpace patternSpace) throws IOException {
        float[] components = null;
        String patternName = this.popString();
        PDFObject pattern = this.findResource(patternName, "Pattern");
        if (pattern == null) {
            throw new PDFParseException("Unknown pattern : " + patternName);
        }
        if (this.stack.size() > 0) {
            components = this.popFloat(this.stack.size());
        }
        return patternSpace.getPaint(pattern, components, this.resources);
    }

    private Object parseObject() throws PDFParseException {
        Tok t = this.nextToken();
        if (t.type == 3) {
            return new Double(this.tok.value);
        }
        if (t.type == 7) {
            return this.tok.name;
        }
        if (t.type == 1) {
            return this.tok.name;
        }
        if (t.type == 11) {
            Object obj;
            HashMap<String, PDFObject> hm = new HashMap<String, PDFObject>();
            String name = null;
            while ((obj = this.parseObject()) != null) {
                if (name == null) {
                    name = (String)obj;
                    continue;
                }
                hm.put(name, new PDFObject(obj));
                name = null;
            }
            if (this.tok.type != 10) {
                throw new PDFParseException("Inline dict should have ended with '>>'");
            }
            return hm;
        }
        if (t.type == 9) {
            Object obj;
            ArrayList<Object> ary = new ArrayList<Object>();
            while ((obj = this.parseObject()) != null) {
                ary.add(obj);
            }
            if (this.tok.type != 8) {
                throw new PDFParseException("Expected ']'");
            }
            return ary.toArray();
        }
        if (t.type == 2) {
            return t;
        }
        PDFParser.debug("**** WARNING! parseObject unknown token! (t.type=" + t.type + ") *************************", 4);
        return null;
    }

    private void parseInlineImage() throws IOException {
        PDFObject imObj;
        HashMap<String, PDFObject> hm = new HashMap<String, PDFObject>();
        while (true) {
            Tok t = this.nextToken();
            if (t.type == 2 && t.name.equals("ID")) break;
            String name = t.name;
            PDFParser.debug("ParseInlineImage, token: " + name, 1000);
            if (name.equals("BPC")) {
                name = "BitsPerComponent";
            } else if (name.equals("CS")) {
                name = "ColorSpace";
            } else if (name.equals("D")) {
                name = "Decode";
            } else if (name.equals("DP")) {
                name = "DecodeParms";
            } else if (name.equals("F")) {
                name = "Filter";
            } else if (name.equals("H")) {
                name = "Height";
            } else if (name.equals("IM")) {
                name = "ImageMask";
            } else if (name.equals("W")) {
                name = "Width";
            } else if (name.equals("I")) {
                name = "Interpolate";
            }
            Object vobj = this.parseObject();
            hm.put(name, new PDFObject(vobj));
        }
        if (this.stream[this.loc] == 13) {
            ++this.loc;
        }
        if (this.stream[this.loc] == 10 || this.stream[this.loc] == 32) {
            ++this.loc;
        }
        if ((imObj = (PDFObject)hm.get("ImageMask")) != null && imObj.getBooleanValue()) {
            Double[] decode = new Double[]{new Double(0.0), new Double(1.0)};
            PDFObject decodeObj = (PDFObject)hm.get("Decode");
            if (decodeObj != null) {
                decode[0] = new Double(decodeObj.getAt(0).getDoubleValue());
                decode[1] = new Double(decodeObj.getAt(1).getDoubleValue());
            }
            hm.put("Decode", new PDFObject(decode));
        }
        PDFObject obj = new PDFObject(null, 6, hm);
        int dstart = this.loc;
        while (!PDFFile.isWhiteSpace(this.stream[this.loc]) || this.stream[this.loc + 1] != 69 || this.stream[this.loc + 2] != 73) {
            ++this.loc;
        }
        byte[] data = new byte[this.loc - dstart];
        System.arraycopy(this.stream, dstart, data, 0, this.loc - dstart);
        obj.setStream(ByteBuffer.wrap(data));
        this.loc += 3;
        this.doImage(obj);
    }

    private void doShader(PDFObject shaderObj) throws IOException {
        PDFShader shader = PDFShader.getShader(shaderObj, this.resources);
        Rectangle2D bbox = shader.getBBox();
        if (bbox != null) {
            this.cmds.addFillPaint(shader.getPaint());
            this.cmds.addPath(new GeneralPath(bbox), 2);
        } else {
            this.cmds.addFillPaint(shader.getPaint());
            this.cmds.addPath(null, 2);
        }
    }

    private PDFFont getFontFrom(String fontref) throws IOException {
        PDFObject obj = this.findResource(fontref, "Font");
        return PDFFont.getFont(obj, this.resources);
    }

    private void setGSState(String name) throws IOException {
        PDFObject gsobj = this.findResource(name, "ExtGState");
        PDFObject d = gsobj.getDictRef("LW");
        if (d != null) {
            this.cmds.addStrokeWidth(d.getFloatValue());
        }
        if ((d = gsobj.getDictRef("LC")) != null) {
            this.cmds.addEndCap(d.getIntValue());
        }
        if ((d = gsobj.getDictRef("LJ")) != null) {
            this.cmds.addLineJoin(d.getIntValue());
        }
        if ((d = gsobj.getDictRef("Font")) != null) {
            this.state.textFormat.setFont(this.getFontFrom(d.getAt(0).getStringValue()), d.getAt(1).getFloatValue());
        }
        if ((d = gsobj.getDictRef("ML")) != null) {
            this.cmds.addMiterLimit(d.getFloatValue());
        }
        if ((d = gsobj.getDictRef("D")) != null) {
            PDFObject[] pdash = d.getAt(0).getArray();
            float[] dash = new float[pdash.length];
            for (int i = 0; i < pdash.length; ++i) {
                dash[i] = pdash[i].getFloatValue();
            }
            this.cmds.addDash(dash, d.getAt(1).getFloatValue());
        }
        if ((d = gsobj.getDictRef("CA")) != null) {
            this.cmds.addStrokeAlpha(d.getFloatValue());
        }
        if ((d = gsobj.getDictRef("ca")) != null) {
            this.cmds.addFillAlpha(d.getFloatValue());
        }
    }

    private PDFColorSpace parseColorSpace(PDFObject csobj) throws IOException {
        if (csobj == null) {
            return this.state.fillCS;
        }
        return PDFColorSpace.getColorSpace(csobj, this.resources);
    }

    private float popFloat() throws PDFParseException {
        Object obj = this.stack.pop();
        if (obj instanceof Double) {
            return ((Double)obj).floatValue();
        }
        throw new PDFParseException("Expected a number here.");
    }

    private float[] popFloat(int count) throws PDFParseException {
        float[] ary = new float[count];
        for (int i = count - 1; i >= 0; --i) {
            ary[i] = this.popFloat();
        }
        return ary;
    }

    private int popInt() throws PDFParseException {
        Object obj = this.stack.pop();
        if (obj instanceof Double) {
            return ((Double)obj).intValue();
        }
        throw new PDFParseException("Expected a number here.");
    }

    private float[] popFloatArray() throws PDFParseException {
        Object obj = this.stack.pop();
        if (!(obj instanceof Object[])) {
            throw new PDFParseException("Expected an [array] here.");
        }
        Object[] source = (Object[])obj;
        float[] ary = new float[source.length];
        for (int i = 0; i < ary.length; ++i) {
            if (!(source[i] instanceof Double)) {
                throw new PDFParseException("This array doesn't consist only of floats.");
            }
            ary[i] = ((Double)source[i]).floatValue();
        }
        return ary;
    }

    private String popString() throws PDFParseException {
        Object obj = this.stack.pop();
        if (!(obj instanceof String)) {
            throw new PDFParseException("Expected string here: " + obj.toString());
        }
        return (String)obj;
    }

    private PDFObject popObject() throws PDFParseException {
        Object obj = this.stack.pop();
        if (!(obj instanceof PDFObject)) {
            throw new PDFParseException("Expected a reference here: " + obj.toString());
        }
        return (PDFObject)obj;
    }

    private Object[] popArray() throws PDFParseException {
        Object obj = this.stack.pop();
        if (!(obj instanceof Object[])) {
            throw new PDFParseException("Expected an [array] here: " + obj.toString());
        }
        return (Object[])obj;
    }

    class ParserState
    implements Cloneable {
        PDFColorSpace fillCS;
        PDFColorSpace strokeCS;
        PDFTextFormat textFormat;

        ParserState() {
        }

        public Object clone() {
            ParserState newState = new ParserState();
            newState.fillCS = this.fillCS;
            newState.strokeCS = this.strokeCS;
            newState.textFormat = (PDFTextFormat)this.textFormat.clone();
            return newState;
        }
    }

    class Tok {
        public static final int BRKB = 11;
        public static final int BRKE = 10;
        public static final int ARYB = 9;
        public static final int ARYE = 8;
        public static final int STR = 7;
        public static final int BRCB = 5;
        public static final int BRCE = 4;
        public static final int NUM = 3;
        public static final int CMD = 2;
        public static final int NAME = 1;
        public static final int UNK = 0;
        public static final int EOF = -1;
        public String name;
        public double value;
        public int type;

        Tok() {
        }

        public String toString() {
            if (this.type == 3) {
                return "NUM: " + this.value;
            }
            if (this.type == 2) {
                return "CMD: " + this.name;
            }
            if (this.type == 0) {
                return "UNK";
            }
            if (this.type == -1) {
                return "EOF";
            }
            if (this.type == 1) {
                return "NAME: " + this.name;
            }
            if (this.type == 2) {
                return "CMD: " + this.name;
            }
            if (this.type == 7) {
                return "STR: (" + this.name;
            }
            if (this.type == 9) {
                return "ARY [";
            }
            if (this.type == 8) {
                return "ARY ]";
            }
            return "some kind of brace (" + this.type + ")";
        }
    }
}

