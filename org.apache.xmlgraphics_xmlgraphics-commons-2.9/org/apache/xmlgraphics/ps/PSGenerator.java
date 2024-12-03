/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.ps;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.java2d.color.ColorUtil;
import org.apache.xmlgraphics.java2d.color.ColorWithAlternatives;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.PSCommandMap;
import org.apache.xmlgraphics.ps.PSProcSets;
import org.apache.xmlgraphics.ps.PSResource;
import org.apache.xmlgraphics.ps.PSState;
import org.apache.xmlgraphics.ps.dsc.ResourceTracker;
import org.apache.xmlgraphics.util.DoubleFormatUtil;

public class PSGenerator
implements PSCommandMap {
    public static final int DEFAULT_LANGUAGE_LEVEL = 3;
    @Deprecated
    public static final Object ATEND = DSCConstants.ATEND;
    public static final char LF = '\n';
    private static final String IDENTITY_H = "Identity-H";
    private Log log = LogFactory.getLog(this.getClass());
    private OutputStream out;
    private int psLevel = 3;
    private boolean acrobatDownsample;
    private boolean commentsEnabled = true;
    private boolean compactMode = true;
    private PSCommandMap commandMap = PSProcSets.STD_COMMAND_MAP;
    private Stack<PSState> graphicsStateStack = new Stack();
    private PSState currentState;
    private StringBuffer doubleBuffer = new StringBuffer(16);
    private StringBuffer tempBuffer = new StringBuffer(256);
    private boolean identityHEmbedded;
    private PSResource procsetCIDInitResource;
    private PSResource identityHCMapResource;
    private ResourceTracker resTracker = new ResourceTracker();

    public PSGenerator(OutputStream out) {
        this.out = out;
        this.resetGraphicsState();
    }

    public boolean isCompactMode() {
        return this.compactMode;
    }

    public void setCompactMode(boolean value) {
        this.compactMode = value;
    }

    public boolean isCommentsEnabled() {
        return this.commentsEnabled;
    }

    public void setCommentsEnabled(boolean value) {
        this.commentsEnabled = value;
    }

    private void resetGraphicsState() {
        if (!this.graphicsStateStack.isEmpty()) {
            throw new IllegalStateException("Graphics state stack should be empty at this point");
        }
        this.currentState = new PSState();
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    public int getPSLevel() {
        return this.psLevel;
    }

    public void setPSLevel(int level) {
        this.psLevel = level;
    }

    public boolean isAcrobatDownsample() {
        return this.acrobatDownsample;
    }

    public void setAcrobatDownsample(boolean b) {
        this.acrobatDownsample = b;
    }

    public Source resolveURI(String uri) {
        return new StreamSource(uri);
    }

    public final void newLine() throws IOException {
        this.out.write(10);
    }

    public String formatDouble(double value) {
        this.doubleBuffer.setLength(0);
        DoubleFormatUtil.formatDouble(value, 3, 3, this.doubleBuffer);
        return this.doubleBuffer.toString();
    }

    public String formatDouble5(double value) {
        this.doubleBuffer.setLength(0);
        DoubleFormatUtil.formatDouble(value, 5, 5, this.doubleBuffer);
        return this.doubleBuffer.toString();
    }

    public void write(String cmd) throws IOException {
        this.out.write(cmd.getBytes("US-ASCII"));
    }

    public void write(int n) throws IOException {
        this.write(Integer.toString(n));
    }

    public void writeln(String cmd) throws IOException {
        this.write(cmd);
        this.newLine();
    }

    public void commentln(String comment) throws IOException {
        if (this.isCommentsEnabled()) {
            this.writeln(comment);
        }
    }

    @Override
    public String mapCommand(String command) {
        if (this.isCompactMode()) {
            return this.commandMap.mapCommand(command);
        }
        return command;
    }

    public void writeByteArr(byte[] cmd) throws IOException {
        this.out.write(cmd);
        this.newLine();
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public static final void escapeChar(char c, StringBuffer target) {
        switch (c) {
            case '\n': {
                target.append("\\n");
                break;
            }
            case '\r': {
                target.append("\\r");
                break;
            }
            case '\t': {
                target.append("\\t");
                break;
            }
            case '\b': {
                target.append("\\b");
                break;
            }
            case '\f': {
                target.append("\\f");
                break;
            }
            case '\\': {
                target.append("\\\\");
                break;
            }
            case '(': {
                target.append("\\(");
                break;
            }
            case ')': {
                target.append("\\)");
                break;
            }
            default: {
                if (c > '\u00ff') {
                    target.append('?');
                    break;
                }
                if (c < ' ' || c > '\u007f') {
                    target.append('\\');
                    target.append((char)(48 + (c >> 6)));
                    target.append((char)(48 + (c >> 3) % 8));
                    target.append((char)(48 + c % 8));
                    break;
                }
                target.append(c);
            }
        }
    }

    public static final String convertStringToDSC(String text) {
        return PSGenerator.convertStringToDSC(text, false);
    }

    public static final String convertRealToDSC(float value) {
        return Float.toString(value);
    }

    public static final String convertStringToDSC(String text, boolean forceParentheses) {
        if (text == null || text.length() == 0) {
            return "()";
        }
        int initialSize = text.length();
        initialSize += initialSize / 2;
        StringBuffer sb = new StringBuffer(initialSize);
        if (text.indexOf(32) >= 0 || forceParentheses) {
            sb.append('(');
            for (int i = 0; i < text.length(); ++i) {
                char c = text.charAt(i);
                PSGenerator.escapeChar(c, sb);
            }
            sb.append(')');
            return sb.toString();
        }
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            PSGenerator.escapeChar(c, sb);
        }
        return sb.toString();
    }

    public void writeDSCComment(String name) throws IOException {
        this.writeln("%%" + name);
    }

    public void writeDSCComment(String name, Object param) throws IOException {
        this.writeDSCComment(name, new Object[]{param});
    }

    public void writeDSCComment(String name, Object[] params) throws IOException {
        this.tempBuffer.setLength(0);
        this.tempBuffer.append("%%");
        this.tempBuffer.append(name);
        if (params != null && params.length > 0) {
            this.tempBuffer.append(": ");
            for (int i = 0; i < params.length; ++i) {
                if (i > 0) {
                    this.tempBuffer.append(" ");
                }
                if (params[i] instanceof String) {
                    this.tempBuffer.append(PSGenerator.convertStringToDSC((String)params[i]));
                    continue;
                }
                if (params[i] == DSCConstants.ATEND) {
                    this.tempBuffer.append(DSCConstants.ATEND);
                    continue;
                }
                if (params[i] instanceof Double) {
                    this.tempBuffer.append(this.formatDouble((Double)params[i]));
                    continue;
                }
                if (params[i] instanceof Number) {
                    this.tempBuffer.append(params[i].toString());
                    continue;
                }
                if (params[i] instanceof Date) {
                    SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    this.tempBuffer.append(PSGenerator.convertStringToDSC(datef.format((Date)params[i])));
                    continue;
                }
                if (params[i] instanceof PSResource) {
                    this.tempBuffer.append(((PSResource)params[i]).getResourceSpecification());
                    continue;
                }
                throw new IllegalArgumentException("Unsupported parameter type: " + params[i].getClass().getName());
            }
        }
        this.writeln(this.tempBuffer.toString());
    }

    public void saveGraphicsState() throws IOException {
        this.writeln(this.mapCommand("gsave"));
        PSState state = new PSState(this.currentState, false);
        this.graphicsStateStack.push(this.currentState);
        this.currentState = state;
    }

    public boolean restoreGraphicsState() throws IOException {
        if (this.graphicsStateStack.size() > 0) {
            this.writeln(this.mapCommand("grestore"));
            this.currentState = this.graphicsStateStack.pop();
            return true;
        }
        return false;
    }

    public PSState getCurrentState() {
        return this.currentState;
    }

    public void showPage() throws IOException {
        this.writeln("showpage");
        this.resetGraphicsState();
    }

    public void concatMatrix(double a, double b, double c, double d, double e, double f) throws IOException {
        AffineTransform at = new AffineTransform(a, b, c, d, e, f);
        this.concatMatrix(at);
    }

    public void concatMatrix(double[] matrix) throws IOException {
        this.concatMatrix(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
    }

    public String formatMatrix(AffineTransform at) {
        double[] matrix = new double[6];
        at.getMatrix(matrix);
        return "[" + this.formatDouble5(matrix[0]) + " " + this.formatDouble5(matrix[1]) + " " + this.formatDouble5(matrix[2]) + " " + this.formatDouble5(matrix[3]) + " " + this.formatDouble5(matrix[4]) + " " + this.formatDouble5(matrix[5]) + "]";
    }

    public void concatMatrix(AffineTransform at) throws IOException {
        this.getCurrentState().concatMatrix(at);
        this.writeln(this.formatMatrix(at) + " " + this.mapCommand("concat"));
    }

    public String formatRectangleToArray(Rectangle2D rect) {
        return "[" + this.formatDouble(rect.getX()) + " " + this.formatDouble(rect.getY()) + " " + this.formatDouble(rect.getWidth()) + " " + this.formatDouble(rect.getHeight()) + "]";
    }

    public void defineRect(double x, double y, double w, double h) throws IOException {
        this.writeln(this.formatDouble(x) + " " + this.formatDouble(y) + " " + this.formatDouble(w) + " " + this.formatDouble(h) + " re");
    }

    public void useLineCap(int linecap) throws IOException {
        if (this.getCurrentState().useLineCap(linecap)) {
            this.writeln(linecap + " " + this.mapCommand("setlinecap"));
        }
    }

    public void useLineJoin(int linejoin) throws IOException {
        if (this.getCurrentState().useLineJoin(linejoin)) {
            this.writeln(linejoin + " " + this.mapCommand("setlinejoin"));
        }
    }

    public void useMiterLimit(float miterlimit) throws IOException {
        if (this.getCurrentState().useMiterLimit(miterlimit)) {
            this.writeln(miterlimit + " " + this.mapCommand("setmiterlimit"));
        }
    }

    public void useLineWidth(double width) throws IOException {
        if (this.getCurrentState().useLineWidth(width)) {
            this.writeln(this.formatDouble(width) + " " + this.mapCommand("setlinewidth"));
        }
    }

    public void useDash(String pattern) throws IOException {
        if (pattern == null) {
            pattern = "[] 0";
        }
        if (this.getCurrentState().useDash(pattern)) {
            this.writeln(pattern + " " + this.mapCommand("setdash"));
        }
    }

    @Deprecated
    public void useRGBColor(Color col) throws IOException {
        this.useColor(col);
    }

    public void useColor(Color col) throws IOException {
        if (this.getCurrentState().useColor(col)) {
            this.writeln(this.convertColorToPS(col));
        }
    }

    private String convertColorToPS(Color color) {
        StringBuffer codeBuffer = new StringBuffer();
        boolean established = false;
        if (color instanceof ColorWithAlternatives) {
            Color col;
            Color[] alt;
            ColorWithAlternatives colExt = (ColorWithAlternatives)color;
            Color[] colorArray = alt = colExt.getAlternativeColors();
            int n = colorArray.length;
            for (int i = 0; i < n && !(established = this.establishColorFromColor(codeBuffer, col = colorArray[i])); ++i) {
            }
            if (this.log.isDebugEnabled() && alt.length > 0) {
                this.log.debug((Object)("None of the alternative colors are supported. Using fallback: " + color));
            }
        }
        if (!established) {
            established = this.establishColorFromColor(codeBuffer, color);
        }
        if (!established) {
            this.establishFallbackRGB(codeBuffer, color);
        }
        return codeBuffer.toString();
    }

    private boolean establishColorFromColor(StringBuffer codeBuffer, Color color) {
        float[] comps = color.getColorComponents(null);
        if (color.getColorSpace().getType() == 9) {
            this.writeSetColor(codeBuffer, comps, "setcmykcolor");
            return true;
        }
        return false;
    }

    private void writeSetColor(StringBuffer codeBuffer, float[] comps, String command) {
        int c = comps.length;
        for (int i = 0; i < c; ++i) {
            if (i > 0) {
                codeBuffer.append(" ");
            }
            codeBuffer.append(this.formatDouble(comps[i]));
        }
        codeBuffer.append(" ").append(this.mapCommand(command));
    }

    private void establishFallbackRGB(StringBuffer codeBuffer, Color color) {
        float[] comps;
        if (color.getColorSpace().isCS_sRGB()) {
            comps = color.getColorComponents(null);
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Converting color to sRGB as a fallback: " + color));
            }
            ColorSpace sRGB = ColorSpace.getInstance(1000);
            comps = color.getColorComponents(sRGB, null);
        }
        assert (comps.length == 3);
        boolean gray = ColorUtil.isGray(color);
        if (gray) {
            comps = new float[]{comps[0]};
        }
        this.writeSetColor(codeBuffer, comps, gray ? "setgray" : "setrgbcolor");
    }

    public void useFont(String name, float size) throws IOException {
        if (this.getCurrentState().useFont(name, size)) {
            this.writeln(name + " " + this.formatDouble(size) + " F");
        }
    }

    public ResourceTracker getResourceTracker() {
        return this.resTracker;
    }

    public void setResourceTracker(ResourceTracker resTracker) {
        this.resTracker = resTracker;
    }

    @Deprecated
    public void notifyStartNewPage() {
        this.getResourceTracker().notifyStartNewPage();
    }

    @Deprecated
    public void notifyResourceUsage(PSResource res, boolean needed) {
        this.getResourceTracker().notifyResourceUsageOnPage(res);
    }

    @Deprecated
    public void writeResources(boolean pageLevel) throws IOException {
        this.getResourceTracker().writeResources(pageLevel, this);
    }

    @Deprecated
    public boolean isResourceSupplied(PSResource res) {
        return this.getResourceTracker().isResourceSupplied(res);
    }

    public boolean embedIdentityH() throws IOException {
        if (this.identityHEmbedded) {
            return false;
        }
        this.resTracker.registerNeededResource(this.getProcsetCIDInitResource());
        this.writeDSCComment("BeginDocument", IDENTITY_H);
        InputStream cmap = PSGenerator.class.getResourceAsStream(IDENTITY_H);
        try {
            IOUtils.copyLarge((InputStream)cmap, (OutputStream)this.out);
        }
        finally {
            IOUtils.closeQuietly((InputStream)cmap);
        }
        this.writeDSCComment("EndDocument");
        this.resTracker.registerSuppliedResource(this.getIdentityHCMapResource());
        this.identityHEmbedded = true;
        return true;
    }

    public PSResource getIdentityHCMapResource() {
        if (this.identityHCMapResource == null) {
            this.identityHCMapResource = new PSResource("cmap", IDENTITY_H);
        }
        return this.identityHCMapResource;
    }

    public PSResource getProcsetCIDInitResource() {
        if (this.procsetCIDInitResource == null) {
            this.procsetCIDInitResource = new PSResource("procset", "CIDInit");
        }
        return this.procsetCIDInitResource;
    }

    public void includeProcsetCIDInitResource() throws IOException {
        this.writeDSCComment("IncludeResource", this.getProcsetCIDInitResource());
    }
}

