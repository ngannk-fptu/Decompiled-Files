/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.toJava.DuplicateFileException;
import org.apache.axis.wsdl.toJava.Emitter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class JavaWriter
implements Generator {
    protected static final int LINE_LENGTH = 65;
    protected Emitter emitter;
    protected String type;

    protected JavaWriter(Emitter emitter, String type) {
        this.emitter = emitter;
        this.type = type;
    }

    public void generate() throws IOException {
        String msg;
        String file = this.getFileName();
        if (this.isFileGenerated(file)) {
            throw new DuplicateFileException(Messages.getMessage("duplicateFile00", file), file);
        }
        this.registerFile(file);
        if (this.emitter.isVerbose() && (msg = this.verboseMessage(file)) != null) {
            System.out.println(msg);
        }
        PrintWriter pw = this.getPrintWriter(file);
        this.writeFileHeader(pw);
        this.writeFileBody(pw);
        this.writeFileFooter(pw);
        this.closePrintWriter(pw);
    }

    protected abstract String getFileName();

    protected boolean isFileGenerated(String file) {
        return this.emitter.getGeneratedFileNames().contains(file);
    }

    protected void registerFile(String file) {
        this.emitter.getGeneratedFileInfo().add(file, null, this.type);
    }

    protected String verboseMessage(String file) {
        return Messages.getMessage("generating", file);
    }

    protected PrintWriter getPrintWriter(String filename) throws IOException {
        File file = new File(filename);
        File parent = new File(file.getParent());
        parent.mkdirs();
        return new PrintWriter(new FileWriter(file));
    }

    protected void writeFileHeader(PrintWriter pw) throws IOException {
    }

    protected abstract void writeFileBody(PrintWriter var1) throws IOException;

    protected void writeFileFooter(PrintWriter pw) throws IOException {
    }

    protected void closePrintWriter(PrintWriter pw) {
        pw.close();
    }

    protected String getJavadocDescriptionPart(String documentation, boolean addTab) {
        StringBuffer newComments;
        if (documentation == null) {
            return "";
        }
        String doc = documentation.trim();
        if (documentation.trim().length() == 0) {
            return doc;
        }
        StringTokenizer st = new StringTokenizer(doc, "@");
        if (st.hasMoreTokens()) {
            String token = st.nextToken();
            boolean startLine = Character.isWhitespace(token.charAt(token.length() - 1)) && token.charAt(token.length() - 1) != '\n';
            newComments = new StringBuffer(token);
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                if (startLine) {
                    newComments.append('\n');
                }
                newComments.append('@');
                startLine = Character.isWhitespace(token.charAt(token.length() - 1)) & token.charAt(token.length() - 1) != '\n';
                newComments.append(token);
            }
        } else {
            newComments = new StringBuffer(doc);
        }
        newComments.insert(0, addTab ? "     * " : " * ");
        int pos = newComments.toString().indexOf("*/");
        while (pos >= 0) {
            newComments.insert(pos + 1, ' ');
            pos = newComments.toString().indexOf("*/");
        }
        int lineStart = 0;
        int newlinePos = 0;
        while (lineStart < newComments.length()) {
            newlinePos = newComments.toString().indexOf("\n", lineStart);
            if (newlinePos == -1) {
                newlinePos = newComments.length();
            }
            if (newlinePos - lineStart > 65) {
                lineStart += 65;
                while (lineStart < newComments.length() && !Character.isWhitespace(newComments.charAt(lineStart))) {
                    ++lineStart;
                }
                if (lineStart < newComments.length()) {
                    char next = newComments.charAt(lineStart);
                    if (next == '\r' || next == '\n') {
                        newComments.insert(lineStart + 1, addTab ? "     * " : " * ");
                        lineStart += addTab ? 8 : 4;
                    } else {
                        newComments.insert(lineStart, addTab ? "\n     * " : "\n * ");
                        lineStart += addTab ? 8 : 4;
                    }
                }
                while (lineStart < newComments.length() && newComments.charAt(lineStart) == ' ') {
                    newComments.delete(lineStart, lineStart + 1);
                }
                continue;
            }
            if (++newlinePos < newComments.length()) {
                newComments.insert(newlinePos, addTab ? "     * " : " * ");
            }
            lineStart = newlinePos;
            lineStart += addTab ? 7 : 3;
        }
        return newComments.toString();
    }

    protected void writeComment(PrintWriter pw, Element element) {
        this.writeComment(pw, element, true);
    }

    protected void writeComment(PrintWriter pw, Element element, boolean addTab) {
        if (element == null) {
            return;
        }
        Node child = element.getFirstChild();
        if (child == null) {
            return;
        }
        String comment = child.getNodeValue();
        if (comment != null) {
            boolean start = false;
            pw.println();
            pw.println(addTab ? "    /**" : "/**");
            pw.println(this.getJavadocDescriptionPart(comment, addTab));
            pw.println(addTab ? "     */" : " */");
        }
    }
}

