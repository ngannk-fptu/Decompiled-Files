/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.jasper.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class SmapUtil {
    private static final Charset SMAP_ENCODING = StandardCharsets.UTF_8;

    public static Map<String, SmapStratum> generateSmap(JspCompilationContext ctxt, Node.Nodes pageNodes) throws IOException {
        HashMap<String, SmapStratum> smapInfo = new HashMap<String, SmapStratum>();
        PreScanVisitor psVisitor = new PreScanVisitor();
        try {
            pageNodes.visit(psVisitor);
        }
        catch (JasperException jasperException) {
            // empty catch block
        }
        HashMap<String, SmapStratum> map = psVisitor.getMap();
        SmapStratum s = new SmapStratum();
        SmapUtil.evaluateNodes(pageNodes, s, map, ctxt.getOptions().getMappedFile());
        s.optimizeLineSection();
        s.setOutputFileName(SmapUtil.unqualify(ctxt.getServletJavaFileName()));
        String classFileName = ctxt.getClassFileName();
        s.setClassFileName(classFileName);
        smapInfo.put(ctxt.getFQCN(), s);
        if (ctxt.getOptions().isSmapDumped()) {
            File outSmap = new File(classFileName + ".smap");
            PrintWriter so = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(outSmap), SMAP_ENCODING));
            so.print(s.getSmapString());
            so.close();
        }
        for (Map.Entry<String, SmapStratum> entry : map.entrySet()) {
            String innerClass = entry.getKey();
            s = entry.getValue();
            s.optimizeLineSection();
            s.setOutputFileName(SmapUtil.unqualify(ctxt.getServletJavaFileName()));
            String innerClassFileName = classFileName.substring(0, classFileName.indexOf(".class")) + '$' + innerClass + ".class";
            s.setClassFileName(innerClassFileName);
            smapInfo.put(ctxt.getFQCN() + "." + innerClass, s);
            if (!ctxt.getOptions().isSmapDumped()) continue;
            File outSmap = new File(innerClassFileName + ".smap");
            PrintWriter so = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(outSmap), SMAP_ENCODING));
            so.print(s.getSmapString());
            so.close();
        }
        return smapInfo;
    }

    public static void installSmap(Map<String, SmapStratum> smapInfo) throws IOException {
        if (smapInfo == null) {
            return;
        }
        for (Map.Entry<String, SmapStratum> entry : smapInfo.entrySet()) {
            File outServlet = new File(entry.getValue().getClassFileName());
            SDEInstaller.install(outServlet, entry.getValue().getSmapString().getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    private static String unqualify(String path) {
        path = path.replace('\\', '/');
        return path.substring(path.lastIndexOf(47) + 1);
    }

    public static void evaluateNodes(Node.Nodes nodes, SmapStratum s, HashMap<String, SmapStratum> innerClassMap, boolean breakAtLF) {
        try {
            nodes.visit(new SmapGenVisitor(s, breakAtLF, innerClassMap));
        }
        catch (JasperException jasperException) {
            // empty catch block
        }
    }

    public static SmapStratum loadSmap(String className, ClassLoader cl) {
        String smap = SmapUtil.getSmap(className, cl);
        if (smap == null) {
            return null;
        }
        SmapStratum smapStratum = new SmapStratum();
        String[] lines = smap.split("\n");
        int lineIndex = 0;
        smapStratum.setOutputFileName(lines[lineIndex]);
        lineIndex = 4;
        while (!lines[lineIndex].equals("*L")) {
            int i = lines[lineIndex].lastIndexOf(32);
            String fileName = lines[lineIndex].substring(i + 1);
            smapStratum.addFile(fileName, lines[++lineIndex]);
            ++lineIndex;
        }
        ++lineIndex;
        while (!lines[lineIndex].equals("*E")) {
            String[] start;
            SmapStratum.LineInfo li = new SmapStratum.LineInfo();
            String[] inOut = lines[lineIndex].split(":");
            String[] in = inOut[0].split(",");
            if (in.length == 2) {
                li.setInputLineCount(Integer.parseInt(in[1]));
            }
            if ((start = in[0].split("#")).length == 2) {
                li.setLineFileID(Integer.parseInt(start[1]));
            }
            li.setInputStartLine(Integer.parseInt(start[0]));
            String[] out = inOut[1].split(",");
            if (out.length == 2) {
                li.setOutputLineIncrement(Integer.parseInt(out[1]));
            }
            li.setOutputStartLine(Integer.parseInt(out[0]));
            smapStratum.addLineInfo(li);
            ++lineIndex;
        }
        return smapStratum;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static String getSmap(String className, ClassLoader cl) {
        Charset encoding = StandardCharsets.ISO_8859_1;
        boolean found = false;
        String smap = null;
        InputStream is = null;
        is = cl.getResourceAsStream(className.replace(".", "/") + ".smap");
        if (is != null) {
            encoding = SMAP_ENCODING;
            found = true;
        } else {
            is = cl.getResourceAsStream(className.replace(".", "/") + ".class");
            int b = is.read();
            while (b != -1) {
                if (b == 83) {
                    b = is.read();
                    if (b != 77 || (b = is.read()) != 65 || (b = is.read()) != 80 || (b = is.read()) != 10) continue;
                    found = true;
                    break;
                }
                b = is.read();
            }
        }
        if (found) {
            int numRead;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            byte[] buf = new byte[1024];
            while ((numRead = is.read(buf)) >= 0) {
                baos.write(buf, 0, numRead);
            }
            smap = new String(baos.toByteArray(), encoding);
        }
        if (is == null) return smap;
        try {
            is.close();
            return smap;
        }
        catch (IOException ioe) {
            Log log = LogFactory.getLog(SmapUtil.class);
            log.warn((Object)Localizer.getMessage("jsp.warning.loadSmap", className), (Throwable)ioe);
        }
        return smap;
        catch (IOException ioe) {
            Log log;
            try {
                log = LogFactory.getLog(SmapUtil.class);
                log.warn((Object)Localizer.getMessage("jsp.warning.loadSmap", className), (Throwable)ioe);
                if (is == null) return smap;
            }
            catch (Throwable throwable) {
                if (is == null) throw throwable;
                try {
                    is.close();
                    throw throwable;
                }
                catch (IOException ioe2) {
                    Log log2 = LogFactory.getLog(SmapUtil.class);
                    log2.warn((Object)Localizer.getMessage("jsp.warning.loadSmap", className), (Throwable)ioe2);
                }
                throw throwable;
            }
            try {
                is.close();
                return smap;
            }
            catch (IOException ioe3) {
                log = LogFactory.getLog(SmapUtil.class);
                log.warn((Object)Localizer.getMessage("jsp.warning.loadSmap", className), (Throwable)ioe3);
            }
            return smap;
        }
    }

    private static class PreScanVisitor
    extends Node.Visitor {
        HashMap<String, SmapStratum> map = new HashMap();

        private PreScanVisitor() {
        }

        @Override
        public void doVisit(Node n) {
            String inner = n.getInnerClassName();
            if (inner != null && !this.map.containsKey(inner)) {
                this.map.put(inner, new SmapStratum());
            }
        }

        HashMap<String, SmapStratum> getMap() {
            return this.map;
        }
    }

    private static class SDEInstaller {
        private final Log log = LogFactory.getLog(SDEInstaller.class);
        static final String nameSDE = "SourceDebugExtension";
        byte[] orig;
        byte[] sdeAttr;
        byte[] gen;
        int origPos = 0;
        int genPos = 0;
        int sdeIndex;

        static void install(File classFile, byte[] smap) throws IOException {
            File tmpFile = new File(classFile.getPath() + "tmp");
            SDEInstaller installer = new SDEInstaller(classFile, smap);
            installer.install(tmpFile);
            if (!classFile.delete()) {
                throw new IOException(Localizer.getMessage("jsp.error.unable.deleteClassFile", classFile.getAbsolutePath()));
            }
            if (!tmpFile.renameTo(classFile)) {
                throw new IOException(Localizer.getMessage("jsp.error.unable.renameClassFile", tmpFile.getAbsolutePath(), classFile.getAbsolutePath()));
            }
        }

        SDEInstaller(File inClassFile, byte[] sdeAttr) throws IOException {
            if (!inClassFile.exists()) {
                throw new FileNotFoundException(Localizer.getMessage("jsp.error.noFile", inClassFile));
            }
            this.sdeAttr = sdeAttr;
            this.orig = SDEInstaller.readWhole(inClassFile);
            this.gen = new byte[this.orig.length + sdeAttr.length + 100];
        }

        void install(File outClassFile) throws IOException {
            this.addSDE();
            try (FileOutputStream outStream = new FileOutputStream(outClassFile);){
                outStream.write(this.gen, 0, this.genPos);
            }
        }

        static byte[] readWhole(File input) throws IOException {
            int len = (int)input.length();
            byte[] bytes = new byte[len];
            try (FileInputStream inStream = new FileInputStream(input);){
                if (inStream.read(bytes, 0, len) != len) {
                    throw new IOException(Localizer.getMessage("jsp.error.readContent", len));
                }
            }
            return bytes;
        }

        void addSDE() throws UnsupportedEncodingException, IOException {
            this.copy(8);
            int constantPoolCountPos = this.genPos;
            int constantPoolCount = this.readU2();
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("constant pool count: " + constantPoolCount));
            }
            this.writeU2(constantPoolCount);
            this.sdeIndex = this.copyConstantPool(constantPoolCount);
            if (this.sdeIndex < 0) {
                this.writeUtf8ForSDE();
                this.sdeIndex = constantPoolCount++;
                this.randomAccessWriteU2(constantPoolCountPos, constantPoolCount);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("SourceDebugExtension not found, installed at: " + this.sdeIndex));
                }
            } else if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("SourceDebugExtension found at: " + this.sdeIndex));
            }
            this.copy(6);
            int interfaceCount = this.readU2();
            this.writeU2(interfaceCount);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("interfaceCount: " + interfaceCount));
            }
            this.copy(interfaceCount * 2);
            this.copyMembers();
            this.copyMembers();
            int attrCountPos = this.genPos;
            int attrCount = this.readU2();
            this.writeU2(attrCount);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("class attrCount: " + attrCount));
            }
            if (!this.copyAttrs(attrCount)) {
                this.randomAccessWriteU2(attrCountPos, ++attrCount);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"class attrCount incremented");
                }
            }
            this.writeAttrForSDE(this.sdeIndex);
        }

        void copyMembers() {
            int count = this.readU2();
            this.writeU2(count);
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("members count: " + count));
            }
            for (int i = 0; i < count; ++i) {
                this.copy(6);
                int attrCount = this.readU2();
                this.writeU2(attrCount);
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("member attr count: " + attrCount));
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
                    this.log.debug((Object)"SDE attr found");
                    continue;
                }
                this.writeU2(nameIndex);
                int len = this.readU4();
                this.writeU4(len);
                this.copy(len);
                if (!this.log.isDebugEnabled()) continue;
                this.log.debug((Object)("attr len: " + len));
            }
            return sdeFound;
        }

        void writeAttrForSDE(int index) {
            this.writeU2(index);
            this.writeU4(this.sdeAttr.length);
            for (byte b : this.sdeAttr) {
                this.writeU1(b);
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
            for (byte aByte : bytes) {
                this.gen[this.genPos++] = aByte;
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
                            this.log.debug((Object)(i + " copying 2 bytes"));
                        }
                        this.copy(2);
                        continue block7;
                    }
                    case 15: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)(i + " copying 3 bytes"));
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
                            this.log.debug((Object)(i + " copying 4 bytes"));
                        }
                        this.copy(4);
                        continue block7;
                    }
                    case 5: 
                    case 6: {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)(i + " copying 8 bytes"));
                        }
                        this.copy(8);
                        ++i;
                        continue block7;
                    }
                    case 1: {
                        int len = this.readU2();
                        this.writeU2(len);
                        byte[] utf8 = this.readBytes(len);
                        String str = new String(utf8, "UTF-8");
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)(i + " read class attr -- '" + str + "'"));
                        }
                        if (str.equals(nameSDE)) {
                            sdeIndex = i;
                        }
                        this.writeBytes(utf8);
                        continue block7;
                    }
                    default: {
                        throw new IOException(Localizer.getMessage("jsp.error.unexpectedTag", tag));
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

    private static class SmapGenVisitor
    extends Node.Visitor {
        private SmapStratum smap;
        private final boolean breakAtLF;
        private final HashMap<String, SmapStratum> innerClassMap;

        SmapGenVisitor(SmapStratum s, boolean breakAtLF, HashMap<String, SmapStratum> map) {
            this.smap = s;
            this.breakAtLF = breakAtLF;
            this.innerClassMap = map;
        }

        @Override
        public void visitBody(Node n) throws JasperException {
            SmapStratum smapSave = this.smap;
            String innerClass = n.getInnerClassName();
            if (innerClass != null) {
                this.smap = this.innerClassMap.get(innerClass);
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
            ArrayList<Integer> extraSmap = n.getExtraSmap();
            if (extraSmap != null) {
                for (Integer integer : extraSmap) {
                    this.smap.addLineData(iInputStartLine + integer, fileName, 1, iOutputStartLine += iOutputLineIncrement, iOutputLineIncrement);
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
}

