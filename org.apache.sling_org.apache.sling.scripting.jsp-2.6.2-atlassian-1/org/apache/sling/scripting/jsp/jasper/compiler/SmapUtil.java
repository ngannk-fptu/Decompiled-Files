/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.SmapGenerator;
import org.apache.sling.scripting.jsp.jasper.compiler.SmapStratum;

public class SmapUtil {
    private Log log = LogFactory.getLog(SmapUtil.class);
    public static final String SMAP_ENCODING = "UTF-8";

    public static String[] generateSmap(JspCompilationContext ctxt, Node.Nodes pageNodes) throws IOException {
        PreScanVisitor psVisitor = new PreScanVisitor();
        try {
            pageNodes.visit(psVisitor);
        }
        catch (JasperException jasperException) {
            // empty catch block
        }
        HashMap map = psVisitor.getMap();
        SmapGenerator g = new SmapGenerator();
        SmapStratum s = new SmapStratum("JSP");
        g.setOutputFileName(SmapUtil.unqualify(ctxt.getServletJavaFileName()));
        SmapUtil.evaluateNodes(pageNodes, s, map, ctxt.getOptions().getMappedFile());
        s.optimizeLineSection();
        g.addStratum(s, true);
        SmapUtil.dumpSmap(g, ctxt.getClassFileName(), ctxt);
        String classFileName = ctxt.getClassFileName();
        int innerClassCount = map.size();
        String[] smapInfo = new String[2 + innerClassCount * 2];
        smapInfo[0] = classFileName;
        smapInfo[1] = g.getString();
        int count = 2;
        for (Map.Entry entry : map.entrySet()) {
            String innerClass = (String)entry.getKey();
            s = (SmapStratum)entry.getValue();
            s.optimizeLineSection();
            g = new SmapGenerator();
            g.setOutputFileName(SmapUtil.unqualify(ctxt.getServletJavaFileName()));
            g.addStratum(s, true);
            String innerClassFileName = classFileName.substring(0, classFileName.indexOf(".class")) + '$' + innerClass + ".class";
            SmapUtil.dumpSmap(g, innerClassFileName, ctxt);
            smapInfo[count] = innerClassFileName;
            smapInfo[count + 1] = g.getString();
            count += 2;
        }
        return smapInfo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void dumpSmap(SmapGenerator g, String smapFile, JspCompilationContext ctxt) throws IOException {
        if (ctxt.getOptions().isSmapDumped()) {
            OutputStream out = null;
            try {
                out = ctxt.getOutputStream(smapFile + ".smap");
                PrintWriter so = new PrintWriter(new OutputStreamWriter(out, SMAP_ENCODING));
                so.print(g.getString());
                so.close();
                out = null;
            }
            finally {
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
    }

    public static void installSmap(JspCompilationContext ctxt, String[] smap) throws IOException {
        if (smap == null) {
            return;
        }
        for (int i = 0; i < smap.length; i += 2) {
            String outServlet = smap[i];
            SDEInstaller.install(ctxt, outServlet, smap[i + 1].getBytes());
        }
    }

    private static String unqualify(String path) {
        path = path.replace('\\', '/');
        return path.substring(path.lastIndexOf(47) + 1);
    }

    private static String inputSmapPath(String path) {
        return path.substring(0, path.lastIndexOf(46) + 1) + "smap";
    }

    public static void evaluateNodes(Node.Nodes nodes, SmapStratum s, HashMap innerClassMap, boolean breakAtLF) {
        try {
            nodes.visit(new SmapGenVisitor(s, breakAtLF, innerClassMap));
        }
        catch (JasperException jasperException) {
            // empty catch block
        }
    }

    private static class PreScanVisitor
    extends Node.Visitor {
        HashMap map = new HashMap();

        private PreScanVisitor() {
        }

        @Override
        public void doVisit(Node n) {
            String inner = n.getInnerClassName();
            if (inner != null && !this.map.containsKey(inner)) {
                this.map.put(inner, new SmapStratum("JSP"));
            }
        }

        HashMap getMap() {
            return this.map;
        }
    }

    static class SmapGenVisitor
    extends Node.Visitor {
        private SmapStratum smap;
        private boolean breakAtLF;
        private HashMap innerClassMap;

        SmapGenVisitor(SmapStratum s, boolean breakAtLF, HashMap map) {
            this.smap = s;
            this.breakAtLF = breakAtLF;
            this.innerClassMap = map;
        }

        @Override
        public void visitBody(Node n) throws JasperException {
            SmapStratum smapSave = this.smap;
            String innerClass = n.getInnerClassName();
            if (innerClass != null) {
                this.smap = (SmapStratum)this.innerClassMap.get(innerClass);
            }
            super.visitBody(n);
            this.smap = smapSave;
        }

        @Override
        public void visit(Node.Declaration n) throws JasperException {
            this.doSmapText(n);
        }

        @Override
        public void visit(Node.Expression n) throws JasperException {
            this.doSmapText(n);
        }

        @Override
        public void visit(Node.Scriptlet n) throws JasperException {
            this.doSmapText(n);
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.GetProperty n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.UninterpretedTag n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspElement n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspText n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.NamedAttribute n) throws JasperException {
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspBody n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.InvokeAction n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.DoBodyAction n) throws JasperException {
            this.doSmap(n);
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ELExpression n) throws JasperException {
            this.doSmap(n);
        }

        @Override
        public void visit(Node.TemplateText n) throws JasperException {
            Mark mark = n.getStart();
            if (mark == null) {
                return;
            }
            String fileName = mark.getFile();
            this.smap.addFile(SmapUtil.unqualify(fileName), fileName);
            int iInputStartLine = mark.getLineNumber();
            int iOutputStartLine = n.getBeginJavaLine();
            int iOutputLineIncrement = this.breakAtLF ? 1 : 0;
            this.smap.addLineData(iInputStartLine, fileName, 1, iOutputStartLine, iOutputLineIncrement);
            ArrayList extraSmap = n.getExtraSmap();
            if (extraSmap != null) {
                for (int i = 0; i < extraSmap.size(); ++i) {
                    this.smap.addLineData(iInputStartLine + (Integer)extraSmap.get(i), fileName, 1, iOutputStartLine += iOutputLineIncrement, iOutputLineIncrement);
                }
            }
        }

        private void doSmap(Node n, int inLineCount, int outIncrement, int skippedLines) {
            Mark mark = n.getStart();
            if (mark == null) {
                return;
            }
            String unqualifiedName = SmapUtil.unqualify(mark.getFile());
            this.smap.addFile(unqualifiedName, mark.getFile());
            this.smap.addLineData(mark.getLineNumber() + skippedLines, mark.getFile(), inLineCount - skippedLines, n.getBeginJavaLine() + skippedLines, outIncrement);
        }

        private void doSmap(Node n) {
            this.doSmap(n, 1, n.getEndJavaLine() - n.getBeginJavaLine(), 0);
        }

        private void doSmapText(Node n) {
            String text = n.getText();
            int index = 0;
            int next = 0;
            int lineCount = 1;
            int skippedLines = 0;
            boolean slashStarSeen = false;
            boolean beginning = true;
            while ((next = text.indexOf(10, index)) > -1) {
                if (beginning) {
                    String line = text.substring(index, next).trim();
                    if (!slashStarSeen && line.startsWith("/*")) {
                        slashStarSeen = true;
                    }
                    if (slashStarSeen) {
                        ++skippedLines;
                        int endIndex = line.indexOf("*/");
                        if (endIndex >= 0) {
                            slashStarSeen = false;
                            if (endIndex < line.length() - 2) {
                                --skippedLines;
                                beginning = false;
                            }
                        }
                    } else if (line.length() == 0 || line.startsWith("//")) {
                        ++skippedLines;
                    } else {
                        beginning = false;
                    }
                }
                ++lineCount;
                index = next + 1;
            }
            this.doSmap(n, lineCount, 1, skippedLines);
        }
    }

    private static class SDEInstaller {
        private Log log = LogFactory.getLog(SDEInstaller.class);
        static final String nameSDE = "SourceDebugExtension";
        byte[] orig;
        byte[] sdeAttr;
        byte[] gen;
        int origPos = 0;
        int genPos = 0;
        int sdeIndex;

        static void install(JspCompilationContext ctxt, String classFile, byte[] smap) throws IOException {
            String tmpFile = classFile + "tmp";
            new SDEInstaller(ctxt, classFile, smap, tmpFile);
            if (!ctxt.delete(classFile)) {
                throw new IOException("classFile.delete() failed");
            }
            if (!ctxt.rename(tmpFile, classFile)) {
                throw new IOException("tmpFile.renameTo(classFile) failed (" + tmpFile + " -> " + classFile + ")");
            }
        }

        SDEInstaller(JspCompilationContext ctxt, String inClassFile, byte[] sdeAttr, String outClassFile) throws IOException {
            this.sdeAttr = sdeAttr;
            this.orig = SDEInstaller.readWhole(ctxt, inClassFile);
            this.gen = new byte[this.orig.length + sdeAttr.length + 100];
            this.addSDE();
            OutputStream outStream = ctxt.getOutputStream(outClassFile);
            outStream.write(this.gen, 0, this.genPos);
            outStream.close();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        static byte[] readWhole(JspCompilationContext ctxt, String input) throws IOException {
            InputStream inStream = ctxt.getInputStream(input);
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                long count = 0L;
                int n = 0;
                while (-1 != (n = inStream.read(buffer))) {
                    output.write(buffer, 0, n);
                    count += (long)n;
                }
                byte[] byArray = output.toByteArray();
                return byArray;
            }
            finally {
                try {
                    inStream.close();
                }
                catch (IOException iOException) {}
            }
        }

        void addSDE() throws UnsupportedEncodingException, IOException {
            this.copy(8);
            int constantPoolCountPos = this.genPos;
            int constantPoolCount = this.readU2();
            if (this.log.isDebugEnabled()) {
                this.log.debug("constant pool count: " + constantPoolCount);
            }
            this.writeU2(constantPoolCount);
            this.sdeIndex = this.copyConstantPool(constantPoolCount);
            if (this.sdeIndex < 0) {
                this.writeUtf8ForSDE();
                this.sdeIndex = constantPoolCount++;
                this.randomAccessWriteU2(constantPoolCountPos, constantPoolCount);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("SourceDebugExtension not found, installed at: " + this.sdeIndex);
                }
            } else if (this.log.isDebugEnabled()) {
                this.log.debug("SourceDebugExtension found at: " + this.sdeIndex);
            }
            this.copy(6);
            int interfaceCount = this.readU2();
            this.writeU2(interfaceCount);
            if (this.log.isDebugEnabled()) {
                this.log.debug("interfaceCount: " + interfaceCount);
            }
            this.copy(interfaceCount * 2);
            this.copyMembers();
            this.copyMembers();
            int attrCountPos = this.genPos;
            int attrCount = this.readU2();
            this.writeU2(attrCount);
            if (this.log.isDebugEnabled()) {
                this.log.debug("class attrCount: " + attrCount);
            }
            if (!this.copyAttrs(attrCount)) {
                this.randomAccessWriteU2(attrCountPos, ++attrCount);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("class attrCount incremented");
                }
            }
            this.writeAttrForSDE(this.sdeIndex);
        }

        void copyMembers() {
            int count = this.readU2();
            this.writeU2(count);
            if (this.log.isDebugEnabled()) {
                this.log.debug("members count: " + count);
            }
            for (int i = 0; i < count; ++i) {
                this.copy(6);
                int attrCount = this.readU2();
                this.writeU2(attrCount);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("member attr count: " + attrCount);
                }
                this.copyAttrs(attrCount);
            }
        }

        boolean copyAttrs(int attrCount) {
            boolean sdeFound = false;
            for (int i = 0; i < attrCount; ++i) {
                int nameIndex = this.readU2();
                if (nameIndex == this.sdeIndex) {
                    sdeFound = true;
                    if (!this.log.isDebugEnabled()) continue;
                    this.log.debug("SDE attr found");
                    continue;
                }
                this.writeU2(nameIndex);
                int len = this.readU4();
                this.writeU4(len);
                this.copy(len);
                if (!this.log.isDebugEnabled()) continue;
                this.log.debug("attr len: " + len);
            }
            return sdeFound;
        }

        void writeAttrForSDE(int index) {
            this.writeU2(index);
            this.writeU4(this.sdeAttr.length);
            for (int i = 0; i < this.sdeAttr.length; ++i) {
                this.writeU1(this.sdeAttr[i]);
            }
        }

        void randomAccessWriteU2(int pos, int val) {
            int savePos = this.genPos;
            this.genPos = pos;
            this.writeU2(val);
            this.genPos = savePos;
        }

        int readU1() {
            return this.orig[this.origPos++] & 0xFF;
        }

        int readU2() {
            int res = this.readU1();
            return (res << 8) + this.readU1();
        }

        int readU4() {
            int res = this.readU2();
            return (res << 16) + this.readU2();
        }

        void writeU1(int val) {
            this.gen[this.genPos++] = (byte)val;
        }

        void writeU2(int val) {
            this.writeU1(val >> 8);
            this.writeU1(val & 0xFF);
        }

        void writeU4(int val) {
            this.writeU2(val >> 16);
            this.writeU2(val & 0xFFFF);
        }

        void copy(int count) {
            for (int i = 0; i < count; ++i) {
                this.gen[this.genPos++] = this.orig[this.origPos++];
            }
        }

        byte[] readBytes(int count) {
            byte[] bytes = new byte[count];
            for (int i = 0; i < count; ++i) {
                bytes[i] = this.orig[this.origPos++];
            }
            return bytes;
        }

        void writeBytes(byte[] bytes) {
            for (int i = 0; i < bytes.length; ++i) {
                this.gen[this.genPos++] = bytes[i];
            }
        }

        int copyConstantPool(int constantPoolCount) throws UnsupportedEncodingException, IOException {
            int sdeIndex = -1;
            block7: for (int i = 1; i < constantPoolCount; ++i) {
                int tag = this.readU1();
                this.writeU1(tag);
                switch (tag) {
                    case 7: 
                    case 8: 
                    case 16: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(i + " copying 2 bytes");
                        }
                        this.copy(2);
                        continue block7;
                    }
                    case 15: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(i + " copying 3 bytes");
                        }
                        this.copy(3);
                        continue block7;
                    }
                    case 3: 
                    case 4: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 18: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(i + " copying 4 bytes");
                        }
                        this.copy(4);
                        continue block7;
                    }
                    case 5: 
                    case 6: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(i + " copying 8 bytes");
                        }
                        this.copy(8);
                        ++i;
                        continue block7;
                    }
                    case 1: {
                        int len = this.readU2();
                        this.writeU2(len);
                        byte[] utf8 = this.readBytes(len);
                        String str = new String(utf8, SmapUtil.SMAP_ENCODING);
                        if (this.log.isDebugEnabled()) {
                            this.log.debug(i + " read class attr -- '" + str + "'");
                        }
                        if (str.equals(nameSDE)) {
                            sdeIndex = i;
                        }
                        this.writeBytes(utf8);
                        continue block7;
                    }
                    default: {
                        throw new IOException("unexpected tag: " + tag);
                    }
                }
            }
            return sdeIndex;
        }

        void writeUtf8ForSDE() {
            int len = nameSDE.length();
            this.writeU1(1);
            this.writeU2(len);
            for (int i = 0; i < len; ++i) {
                this.writeU1(nameSDE.charAt(i));
            }
        }
    }
}

