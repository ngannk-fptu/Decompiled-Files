/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.cvslib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.taskdefs.cvslib.CvsTagEntry;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMUtils;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CvsTagDiff
extends AbstractCvsTask {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final DOMElementWriter DOM_WRITER = new DOMElementWriter();
    static final String FILE_STRING = "File ";
    static final int FILE_STRING_LENGTH = "File ".length();
    static final String TO_STRING = " to ";
    static final String FILE_IS_NEW = " is new;";
    static final String REVISION = "revision ";
    static final String FILE_HAS_CHANGED = " changed from revision ";
    static final String FILE_WAS_REMOVED = " is removed";
    private String mypackage;
    private String mystartTag;
    private String myendTag;
    private String mystartDate;
    private String myendDate;
    private File mydestfile;
    private boolean ignoreRemoved = false;
    private List<String> packageNames = new ArrayList<String>();
    private String[] packageNamePrefixes = null;
    private int[] packageNamePrefixLengths = null;

    @Override
    public void setPackage(String p) {
        this.mypackage = p;
    }

    public void setStartTag(String s) {
        this.mystartTag = s;
    }

    public void setStartDate(String s) {
        this.mystartDate = s;
    }

    public void setEndTag(String s) {
        this.myendTag = s;
    }

    public void setEndDate(String s) {
        this.myendDate = s;
    }

    public void setDestFile(File f) {
        this.mydestfile = f;
    }

    public void setIgnoreRemoved(boolean b) {
        this.ignoreRemoved = b;
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        this.addCommandArgument("rdiff");
        this.addCommandArgument("-s");
        if (this.mystartTag != null) {
            this.addCommandArgument("-r");
            this.addCommandArgument(this.mystartTag);
        } else {
            this.addCommandArgument("-D");
            this.addCommandArgument(this.mystartDate);
        }
        if (this.myendTag != null) {
            this.addCommandArgument("-r");
            this.addCommandArgument(this.myendTag);
        } else {
            this.addCommandArgument("-D");
            this.addCommandArgument(this.myendDate);
        }
        this.setCommand("");
        File tmpFile = null;
        try {
            this.handlePackageNames();
            tmpFile = FILE_UTILS.createTempFile(this.getProject(), "cvstagdiff", ".log", null, true, true);
            this.setOutput(tmpFile);
            super.execute();
            CvsTagEntry[] entries = this.parseRDiff(tmpFile);
            this.writeTagDiff(entries);
        }
        finally {
            this.packageNamePrefixes = null;
            this.packageNamePrefixLengths = null;
            this.packageNames.clear();
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }

    private CvsTagEntry[] parseRDiff(File tmpFile) throws BuildException {
        CvsTagEntry[] cvsTagEntryArray;
        BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
        try {
            ArrayList<CvsTagEntry> entries = new ArrayList<CvsTagEntry>();
            String line = reader.readLine();
            while (null != line) {
                if ((line = CvsTagDiff.removePackageName(line, this.packageNamePrefixes, this.packageNamePrefixLengths)) != null) {
                    boolean bl = this.doFileIsNew(entries, line) || this.doFileHasChanged(entries, line) || this.doFileWasRemoved(entries, line);
                }
                line = reader.readLine();
            }
            cvsTagEntryArray = entries.toArray(new CvsTagEntry[0]);
        }
        catch (Throwable throwable) {
            try {
                try {
                    reader.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new BuildException("Error in parsing", e);
            }
        }
        reader.close();
        return cvsTagEntryArray;
    }

    private boolean doFileIsNew(List<CvsTagEntry> entries, String line) {
        int index = line.indexOf(FILE_IS_NEW);
        if (index == -1) {
            return false;
        }
        String filename = line.substring(0, index);
        String rev = null;
        int indexrev = line.indexOf(REVISION, index);
        if (indexrev != -1) {
            rev = line.substring(indexrev + REVISION.length());
        }
        CvsTagEntry entry = new CvsTagEntry(filename, rev);
        entries.add(entry);
        this.log(entry.toString(), 3);
        return true;
    }

    private boolean doFileHasChanged(List<CvsTagEntry> entries, String line) {
        int index = line.indexOf(FILE_HAS_CHANGED);
        if (index == -1) {
            return false;
        }
        String filename = line.substring(0, index);
        int revSeparator = line.indexOf(TO_STRING, index);
        String prevRevision = line.substring(index + FILE_HAS_CHANGED.length(), revSeparator);
        String revision = line.substring(revSeparator + TO_STRING.length());
        CvsTagEntry entry = new CvsTagEntry(filename, revision, prevRevision);
        entries.add(entry);
        this.log(entry.toString(), 3);
        return true;
    }

    private boolean doFileWasRemoved(List<CvsTagEntry> entries, String line) {
        if (this.ignoreRemoved) {
            return false;
        }
        int index = line.indexOf(FILE_WAS_REMOVED);
        if (index == -1) {
            return false;
        }
        String filename = line.substring(0, index);
        String rev = null;
        int indexrev = line.indexOf(REVISION, index);
        if (indexrev != -1) {
            rev = line.substring(indexrev + REVISION.length());
        }
        CvsTagEntry entry = new CvsTagEntry(filename, null, rev);
        entries.add(entry);
        this.log(entry.toString(), 3);
        return true;
    }

    private void writeTagDiff(CvsTagEntry[] entries) throws BuildException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(this.mydestfile.toPath(), new OpenOption[0]), StandardCharsets.UTF_8));){
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            Document doc = DOMUtils.newDocument();
            Element root = doc.createElement("tagdiff");
            if (this.mystartTag != null) {
                root.setAttribute("startTag", this.mystartTag);
            } else {
                root.setAttribute("startDate", this.mystartDate);
            }
            if (this.myendTag != null) {
                root.setAttribute("endTag", this.myendTag);
            } else {
                root.setAttribute("endDate", this.myendDate);
            }
            root.setAttribute("cvsroot", this.getCvsRoot());
            root.setAttribute("package", String.join((CharSequence)",", this.packageNames));
            DOM_WRITER.openElement(root, writer, 0, "\t");
            writer.println();
            for (CvsTagEntry entry : entries) {
                this.writeTagEntry(doc, writer, entry);
            }
            DOM_WRITER.closeElement(root, writer, 0, "\t", true);
            writer.flush();
            if (writer.checkError()) {
                throw new IOException("Encountered an error writing tagdiff");
            }
        }
        catch (UnsupportedEncodingException uee) {
            this.log(uee.toString(), 0);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.toString(), ioe);
        }
    }

    private void writeTagEntry(Document doc, PrintWriter writer, CvsTagEntry entry) throws IOException {
        Element ent = doc.createElement("entry");
        Element f = DOMUtils.createChildElement(ent, "file");
        DOMUtils.appendCDATAElement(f, "name", entry.getFile());
        if (entry.getRevision() != null) {
            DOMUtils.appendTextElement(f, "revision", entry.getRevision());
        }
        if (entry.getPreviousRevision() != null) {
            DOMUtils.appendTextElement(f, "prevrevision", entry.getPreviousRevision());
        }
        DOM_WRITER.write(ent, writer, 1, "\t");
    }

    private void validate() throws BuildException {
        if (null == this.mypackage && this.getModules().isEmpty()) {
            throw new BuildException("Package/module must be set.");
        }
        if (null == this.mydestfile) {
            throw new BuildException("Destfile must be set.");
        }
        if (null == this.mystartTag && null == this.mystartDate) {
            throw new BuildException("Start tag or start date must be set.");
        }
        if (null != this.mystartTag && null != this.mystartDate) {
            throw new BuildException("Only one of start tag and start date must be set.");
        }
        if (null == this.myendTag && null == this.myendDate) {
            throw new BuildException("End tag or end date must be set.");
        }
        if (null != this.myendTag && null != this.myendDate) {
            throw new BuildException("Only one of end tag and end date must be set.");
        }
    }

    private void handlePackageNames() {
        if (this.mypackage != null) {
            StringTokenizer myTokenizer = new StringTokenizer(this.mypackage);
            while (myTokenizer.hasMoreTokens()) {
                String pack = myTokenizer.nextToken();
                this.packageNames.add(pack);
                this.addCommandArgument(pack);
            }
        }
        for (AbstractCvsTask.Module m : this.getModules()) {
            this.packageNames.add(m.getName());
        }
        this.packageNamePrefixes = new String[this.packageNames.size()];
        this.packageNamePrefixLengths = new int[this.packageNames.size()];
        for (int i = 0; i < this.packageNamePrefixes.length; ++i) {
            this.packageNamePrefixes[i] = FILE_STRING + this.packageNames.get(i) + "/";
            this.packageNamePrefixLengths[i] = this.packageNamePrefixes[i].length();
        }
    }

    private static String removePackageName(String line, String[] packagePrefixes, int[] prefixLengths) {
        if (line.length() < FILE_STRING_LENGTH) {
            return null;
        }
        for (int i = 0; i < packagePrefixes.length; ++i) {
            if (!line.startsWith(packagePrefixes[i])) continue;
            return line.substring(prefixLengths[i]);
        }
        return line.substring(FILE_STRING_LENGTH);
    }
}

