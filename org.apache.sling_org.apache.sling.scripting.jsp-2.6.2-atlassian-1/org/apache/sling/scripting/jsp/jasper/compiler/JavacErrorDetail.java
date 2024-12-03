/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;

public class JavacErrorDetail {
    private String javaFileName;
    private int javaLineNum;
    private String jspFileName;
    private int jspBeginLineNum;
    private StringBuffer errMsg;
    private String jspExtract = null;

    public JavacErrorDetail(String javaFileName, int javaLineNum, StringBuffer errMsg) {
        this.javaFileName = javaFileName;
        this.javaLineNum = javaLineNum;
        this.errMsg = errMsg;
        this.jspBeginLineNum = -1;
    }

    public JavacErrorDetail(String javaFileName, int javaLineNum, String jspFileName, int jspBeginLineNum, StringBuffer errMsg) {
        this(javaFileName, javaLineNum, jspFileName, jspBeginLineNum, errMsg, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JavacErrorDetail(String javaFileName, int javaLineNum, String jspFileName, int jspBeginLineNum, StringBuffer errMsg, JspCompilationContext ctxt) {
        this(javaFileName, javaLineNum, errMsg);
        this.jspFileName = jspFileName;
        this.jspBeginLineNum = jspBeginLineNum;
        if (jspBeginLineNum > 0 && ctxt != null) {
            InputStream is = null;
            InputStream fis = null;
            try {
                is = ctxt.getResourceAsStream(jspFileName);
                String[] jspLines = this.readFile(is);
                fis = ctxt.getRuntimeContext().getIOProvider().getInputStream(ctxt.getServletJavaFileName());
                String[] javaLines = this.readFile(fis);
                if (jspLines[jspBeginLineNum - 1].lastIndexOf("<%") > jspLines[jspBeginLineNum - 1].lastIndexOf("%>")) {
                    String javaLine = javaLines[javaLineNum - 1].trim();
                    for (int i = jspBeginLineNum - 1; i < jspLines.length; ++i) {
                        if (jspLines[i].indexOf(javaLine) == -1) continue;
                        this.jspBeginLineNum = i + 1;
                        break;
                    }
                }
                StringBuffer fragment = new StringBuffer(1024);
                int startIndex = Math.max(0, this.jspBeginLineNum - 1 - 3);
                int endIndex = Math.min(jspLines.length - 1, this.jspBeginLineNum - 1 + 3);
                for (int i = startIndex; i <= endIndex; ++i) {
                    fragment.append(i + 1);
                    fragment.append(": ");
                    fragment.append(jspLines[i]);
                    fragment.append("\n");
                }
                this.jspExtract = fragment.toString();
            }
            catch (IOException iOException) {
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (IOException iOException) {}
                }
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
    }

    public String getJavaFileName() {
        return this.javaFileName;
    }

    public int getJavaLineNumber() {
        return this.javaLineNum;
    }

    public String getJspFileName() {
        return this.jspFileName;
    }

    public int getJspBeginLineNumber() {
        return this.jspBeginLineNum;
    }

    public String getErrorMessage() {
        return this.errMsg.toString();
    }

    public String getJspExtract() {
        return this.jspExtract;
    }

    private String[] readFile(InputStream s) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(s));
        ArrayList<String> lines = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines.toArray(new String[lines.size()]);
    }
}

