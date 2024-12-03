/*
 * Decompiled with CFR 0.152.
 */
package javax.activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;

public class FileDataSource
implements DataSource {
    private File _file = null;
    private FileTypeMap typeMap = null;

    public FileDataSource(File file) {
        this._file = file;
    }

    public FileDataSource(String name) {
        this(new File(name));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this._file);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this._file);
    }

    @Override
    public String getContentType() {
        if (this.typeMap == null) {
            return FileTypeMap.getDefaultFileTypeMap().getContentType(this._file);
        }
        return this.typeMap.getContentType(this._file);
    }

    @Override
    public String getName() {
        return this._file.getName();
    }

    public File getFile() {
        return this._file;
    }

    public void setFileTypeMap(FileTypeMap map) {
        this.typeMap = map;
    }
}

