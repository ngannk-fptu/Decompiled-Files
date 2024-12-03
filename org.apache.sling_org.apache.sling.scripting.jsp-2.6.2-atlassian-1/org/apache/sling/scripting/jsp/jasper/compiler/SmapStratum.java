/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.util.ArrayList;
import java.util.List;

public class SmapStratum {
    private String stratumName;
    private List fileNameList;
    private List filePathList;
    private List lineData;
    private int lastFileID;

    public SmapStratum(String stratumName) {
        this.stratumName = stratumName;
        this.fileNameList = new ArrayList();
        this.filePathList = new ArrayList();
        this.lineData = new ArrayList();
        this.lastFileID = 0;
    }

    public void addFile(String filename) {
        this.addFile(filename, filename);
    }

    public void addFile(String filename, String filePath) {
        int pathIndex = this.filePathList.indexOf(filePath);
        if (pathIndex == -1) {
            this.fileNameList.add(filename);
            this.filePathList.add(filePath);
        }
    }

    public void optimizeLineSection() {
        LineInfo liNext;
        LineInfo li;
        int i = 0;
        while (i < this.lineData.size() - 1) {
            li = (LineInfo)this.lineData.get(i);
            liNext = (LineInfo)this.lineData.get(i + 1);
            if (!liNext.lineFileIDSet && liNext.inputStartLine == li.inputStartLine && liNext.inputLineCount == 1 && li.inputLineCount == 1 && liNext.outputStartLine == li.outputStartLine + li.inputLineCount * li.outputLineIncrement) {
                li.setOutputLineIncrement(liNext.outputStartLine - li.outputStartLine + liNext.outputLineIncrement);
                this.lineData.remove(i + 1);
                continue;
            }
            ++i;
        }
        i = 0;
        while (i < this.lineData.size() - 1) {
            li = (LineInfo)this.lineData.get(i);
            liNext = (LineInfo)this.lineData.get(i + 1);
            if (!liNext.lineFileIDSet && liNext.inputStartLine == li.inputStartLine + li.inputLineCount && liNext.outputLineIncrement == li.outputLineIncrement && liNext.outputStartLine == li.outputStartLine + li.inputLineCount * li.outputLineIncrement) {
                li.setInputLineCount(li.inputLineCount + liNext.inputLineCount);
                this.lineData.remove(i + 1);
                continue;
            }
            ++i;
        }
    }

    public void addLineData(int inputStartLine, String inputFileName, int inputLineCount, int outputStartLine, int outputLineIncrement) {
        int fileIndex = this.filePathList.indexOf(inputFileName);
        if (fileIndex == -1) {
            throw new IllegalArgumentException("inputFileName: " + inputFileName);
        }
        if (outputStartLine == 0) {
            return;
        }
        LineInfo li = new LineInfo();
        li.setInputStartLine(inputStartLine);
        li.setInputLineCount(inputLineCount);
        li.setOutputStartLine(outputStartLine);
        li.setOutputLineIncrement(outputLineIncrement);
        if (fileIndex != this.lastFileID) {
            li.setLineFileID(fileIndex);
        }
        this.lastFileID = fileIndex;
        this.lineData.add(li);
    }

    public String getStratumName() {
        return this.stratumName;
    }

    public String getString() {
        int i;
        if (this.fileNameList.size() == 0 || this.lineData.size() == 0) {
            return null;
        }
        StringBuffer out = new StringBuffer();
        out.append("*S " + this.stratumName + "\n");
        out.append("*F\n");
        int bound = this.fileNameList.size();
        for (i = 0; i < bound; ++i) {
            if (this.filePathList.get(i) != null) {
                out.append("+ " + i + " " + this.fileNameList.get(i) + "\n");
                String filePath = (String)this.filePathList.get(i);
                if (filePath.startsWith("/")) {
                    filePath = filePath.substring(1);
                }
                out.append(filePath + "\n");
                continue;
            }
            out.append(i + " " + this.fileNameList.get(i) + "\n");
        }
        out.append("*L\n");
        bound = this.lineData.size();
        for (i = 0; i < bound; ++i) {
            LineInfo li = (LineInfo)this.lineData.get(i);
            out.append(li.getString());
        }
        return out.toString();
    }

    public String toString() {
        return this.getString();
    }

    public static class LineInfo {
        private int inputStartLine = -1;
        private int outputStartLine = -1;
        private int lineFileID = 0;
        private int inputLineCount = 1;
        private int outputLineIncrement = 1;
        private boolean lineFileIDSet = false;

        public void setInputStartLine(int inputStartLine) {
            if (inputStartLine < 0) {
                throw new IllegalArgumentException("" + inputStartLine);
            }
            this.inputStartLine = inputStartLine;
        }

        public void setOutputStartLine(int outputStartLine) {
            if (outputStartLine < 0) {
                throw new IllegalArgumentException("" + outputStartLine);
            }
            this.outputStartLine = outputStartLine;
        }

        public void setLineFileID(int lineFileID) {
            if (lineFileID < 0) {
                throw new IllegalArgumentException("" + lineFileID);
            }
            this.lineFileID = lineFileID;
            this.lineFileIDSet = true;
        }

        public void setInputLineCount(int inputLineCount) {
            if (inputLineCount < 0) {
                throw new IllegalArgumentException("" + inputLineCount);
            }
            this.inputLineCount = inputLineCount;
        }

        public void setOutputLineIncrement(int outputLineIncrement) {
            if (outputLineIncrement < 0) {
                throw new IllegalArgumentException("" + outputLineIncrement);
            }
            this.outputLineIncrement = outputLineIncrement;
        }

        public String getString() {
            if (this.inputStartLine == -1 || this.outputStartLine == -1) {
                throw new IllegalStateException();
            }
            StringBuffer out = new StringBuffer();
            out.append(this.inputStartLine);
            if (this.lineFileIDSet) {
                out.append("#" + this.lineFileID);
            }
            if (this.inputLineCount != 1) {
                out.append("," + this.inputLineCount);
            }
            out.append(":" + this.outputStartLine);
            if (this.outputLineIncrement != 1) {
                out.append("," + this.outputLineIncrement);
            }
            out.append('\n');
            return out.toString();
        }

        public String toString() {
            return this.getString();
        }
    }
}

