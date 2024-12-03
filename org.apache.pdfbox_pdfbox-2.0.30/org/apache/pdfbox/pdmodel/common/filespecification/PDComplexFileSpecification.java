/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.filespecification;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

public class PDComplexFileSpecification
extends PDFileSpecification {
    private final COSDictionary fs;
    private COSDictionary efDictionary;

    public PDComplexFileSpecification() {
        this.fs = new COSDictionary();
        this.fs.setItem(COSName.TYPE, (COSBase)COSName.FILESPEC);
    }

    public PDComplexFileSpecification(COSDictionary dict) {
        if (dict == null) {
            this.fs = new COSDictionary();
            this.fs.setItem(COSName.TYPE, (COSBase)COSName.FILESPEC);
        } else {
            this.fs = dict;
        }
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.fs;
    }

    private COSDictionary getEFDictionary() {
        if (this.efDictionary == null && this.fs != null) {
            this.efDictionary = this.fs.getCOSDictionary(COSName.EF);
        }
        return this.efDictionary;
    }

    private COSBase getObjectFromEFDictionary(COSName key) {
        COSDictionary ef = this.getEFDictionary();
        if (ef != null) {
            return ef.getDictionaryObject(key);
        }
        return null;
    }

    public String getFilename() {
        String filename = this.getFileUnicode();
        if (filename == null) {
            filename = this.getFileDos();
        }
        if (filename == null) {
            filename = this.getFileMac();
        }
        if (filename == null) {
            filename = this.getFileUnix();
        }
        if (filename == null) {
            filename = this.getFile();
        }
        return filename;
    }

    public String getFileUnicode() {
        return this.fs.getString(COSName.UF);
    }

    public void setFileUnicode(String file) {
        this.fs.setString(COSName.UF, file);
    }

    @Override
    public String getFile() {
        return this.fs.getString(COSName.F);
    }

    @Override
    public void setFile(String file) {
        this.fs.setString(COSName.F, file);
    }

    public String getFileDos() {
        return this.fs.getString(COSName.DOS);
    }

    @Deprecated
    public void setFileDos(String file) {
        this.fs.setString(COSName.DOS, file);
    }

    public String getFileMac() {
        return this.fs.getString(COSName.MAC);
    }

    @Deprecated
    public void setFileMac(String file) {
        this.fs.setString(COSName.MAC, file);
    }

    public String getFileUnix() {
        return this.fs.getString(COSName.UNIX);
    }

    @Deprecated
    public void setFileUnix(String file) {
        this.fs.setString(COSName.UNIX, file);
    }

    public void setVolatile(boolean fileIsVolatile) {
        this.fs.setBoolean(COSName.V, fileIsVolatile);
    }

    public boolean isVolatile() {
        return this.fs.getBoolean(COSName.V, false);
    }

    public PDEmbeddedFile getEmbeddedFile() {
        COSBase base = this.getObjectFromEFDictionary(COSName.F);
        if (base instanceof COSStream) {
            return new PDEmbeddedFile((COSStream)base);
        }
        return null;
    }

    public void setEmbeddedFile(PDEmbeddedFile file) {
        COSDictionary ef = this.getEFDictionary();
        if (ef == null && file != null) {
            ef = new COSDictionary();
            this.fs.setItem(COSName.EF, (COSBase)ef);
        }
        if (ef != null) {
            ef.setItem(COSName.F, (COSObjectable)file);
        }
    }

    public PDEmbeddedFile getEmbeddedFileDos() {
        COSBase base = this.getObjectFromEFDictionary(COSName.DOS);
        if (base instanceof COSStream) {
            return new PDEmbeddedFile((COSStream)base);
        }
        return null;
    }

    @Deprecated
    public void setEmbeddedFileDos(PDEmbeddedFile file) {
        COSDictionary ef = this.getEFDictionary();
        if (ef == null && file != null) {
            ef = new COSDictionary();
            this.fs.setItem(COSName.EF, (COSBase)ef);
        }
        if (ef != null) {
            ef.setItem(COSName.DOS, (COSObjectable)file);
        }
    }

    public PDEmbeddedFile getEmbeddedFileMac() {
        COSBase base = this.getObjectFromEFDictionary(COSName.MAC);
        if (base instanceof COSStream) {
            return new PDEmbeddedFile((COSStream)base);
        }
        return null;
    }

    @Deprecated
    public void setEmbeddedFileMac(PDEmbeddedFile file) {
        COSDictionary ef = this.getEFDictionary();
        if (ef == null && file != null) {
            ef = new COSDictionary();
            this.fs.setItem(COSName.EF, (COSBase)ef);
        }
        if (ef != null) {
            ef.setItem(COSName.MAC, (COSObjectable)file);
        }
    }

    public PDEmbeddedFile getEmbeddedFileUnix() {
        COSBase base = this.getObjectFromEFDictionary(COSName.UNIX);
        if (base instanceof COSStream) {
            return new PDEmbeddedFile((COSStream)base);
        }
        return null;
    }

    @Deprecated
    public void setEmbeddedFileUnix(PDEmbeddedFile file) {
        COSDictionary ef = this.getEFDictionary();
        if (ef == null && file != null) {
            ef = new COSDictionary();
            this.fs.setItem(COSName.EF, (COSBase)ef);
        }
        if (ef != null) {
            ef.setItem(COSName.UNIX, (COSObjectable)file);
        }
    }

    public PDEmbeddedFile getEmbeddedFileUnicode() {
        COSBase base = this.getObjectFromEFDictionary(COSName.UF);
        if (base instanceof COSStream) {
            return new PDEmbeddedFile((COSStream)base);
        }
        return null;
    }

    public void setEmbeddedFileUnicode(PDEmbeddedFile file) {
        COSDictionary ef = this.getEFDictionary();
        if (ef == null && file != null) {
            ef = new COSDictionary();
            this.fs.setItem(COSName.EF, (COSBase)ef);
        }
        if (ef != null) {
            ef.setItem(COSName.UF, (COSObjectable)file);
        }
    }

    public void setFileDescription(String description) {
        this.fs.setString(COSName.DESC, description);
    }

    public String getFileDescription() {
        return this.fs.getString(COSName.DESC);
    }
}

