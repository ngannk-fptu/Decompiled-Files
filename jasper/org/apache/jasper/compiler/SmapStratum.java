/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.util.ArrayList;
import java.util.List;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.SmapInput;

public class SmapStratum {
    private final List<String> fileNameList = new ArrayList<String>();
    private final List<String> filePathList = new ArrayList<String>();
    private final List<LineInfo> lineData = new ArrayList<LineInfo>();
    private int lastFileID;
    private String outputFileName;
    private String classFileName;

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
            li = this.lineData.get(i);
            liNext = this.lineData.get(i + 1);
            if (!liNext.lineFileIDSet && liNext.inputStartLine == li.inputStartLine && liNext.inputLineCount == 1 && li.inputLineCount == 1 && liNext.outputStartLine == li.outputStartLine + li.inputLineCount * li.outputLineIncrement) {
                li.setOutputLineIncrement(liNext.outputStartLine - li.outputStartLine + liNext.outputLineIncrement);
                this.lineData.remove(i + 1);
                continue;
            }
            ++i;
        }
        i = 0;
        while (i < this.lineData.size() - 1) {
            li = this.lineData.get(i);
            liNext = this.lineData.get(i + 1);
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

    public void addLineInfo(LineInfo li) {
        this.lineData.add(li);
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void setClassFileName(String classFileName) {
        this.classFileName = classFileName;
    }

    public String getClassFileName() {
        return this.classFileName;
    }

    public String toString() {
        return this.getSmapStringInternal();
    }

    public String getSmapString() {
        if (this.outputFileName == null) {
            throw new IllegalStateException();
        }
        return this.getSmapStringInternal();
    }

    private String getSmapStringInternal() {
        int i;
        StringBuilder out = new StringBuilder();
        out.append("SMAP\n");
        out.append(this.outputFileName + '\n');
        out.append("JSP\n");
        out.append("*S JSP\n");
        out.append("*F\n");
        int bound = this.fileNameList.size();
        for (i = 0; i < bound; ++i) {
            if (this.filePathList.get(i) != null) {
                out.append("+ " + i + " " + this.fileNameList.get(i) + "\n");
                String filePath = this.filePathList.get(i);
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
            LineInfo li = this.lineData.get(i);
            out.append(li.getString());
        }
        out.append("*E\n");
        return out.toString();
    }

    public SmapInput getInputLineNumber(int outputLineNumber) {
        int inputLineNumber = -1;
        int fileId = 0;
        for (LineInfo lineInfo : this.lineData) {
            if (lineInfo.lineFileIDSet) {
                fileId = lineInfo.lineFileID;
            }
            if (lineInfo.outputStartLine > outputLineNumber) break;
            if (lineInfo.getMaxOutputLineNumber() < outputLineNumber) continue;
            int inputOffset = (outputLineNumber - lineInfo.outputStartLine) / lineInfo.outputLineIncrement;
            inputLineNumber = lineInfo.inputStartLine + inputOffset;
        }
        return new SmapInput(this.filePathList.get(fileId), inputLineNumber);
    }

    static class LineInfo {
        private int inputStartLine = -1;
        private int outputStartLine = -1;
        private int lineFileID = 0;
        private int inputLineCount = 1;
        private int outputLineIncrement = 1;
        private boolean lineFileIDSet = false;

        LineInfo() {
        }

        public void setInputStartLine(int inputStartLine) {
            if (inputStartLine < 0) {
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.negativeParameter", inputStartLine));
            }
            this.inputStartLine = inputStartLine;
        }

        public void setOutputStartLine(int outputStartLine) {
            if (outputStartLine < 0) {
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.negativeParameter", outputStartLine));
            }
            this.outputStartLine = outputStartLine;
        }

        public void setLineFileID(int lineFileID) {
            if (lineFileID < 0) {
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.negativeParameter", lineFileID));
            }
            this.lineFileID = lineFileID;
            this.lineFileIDSet = true;
        }

        public void setInputLineCount(int inputLineCount) {
            if (inputLineCount < 0) {
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.negativeParameter", inputLineCount));
            }
            this.inputLineCount = inputLineCount;
        }

        public void setOutputLineIncrement(int outputLineIncrement) {
            if (outputLineIncrement < 0) {
                throw new IllegalArgumentException(Localizer.getMessage("jsp.error.negativeParameter", outputLineIncrement));
            }
            this.outputLineIncrement = outputLineIncrement;
        }

        public int getMaxOutputLineNumber() {
            return this.outputStartLine + this.inputLineCount * this.outputLineIncrement;
        }

        public String getString() {
            if (this.inputStartLine == -1 || this.outputStartLine == -1) {
                throw new IllegalStateException();
            }
            StringBuilder out = new StringBuilder();
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

