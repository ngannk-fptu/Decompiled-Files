/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.FileTypeMap
 *  javax.activation.MimetypesFileTypeMap
 */
package org.springframework.mail.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class ConfigurableMimeFileTypeMap
extends FileTypeMap
implements InitializingBean {
    private Resource mappingLocation = new ClassPathResource("mime.types", this.getClass());
    @Nullable
    private String[] mappings;
    @Nullable
    private FileTypeMap fileTypeMap;

    public void setMappingLocation(Resource mappingLocation) {
        this.mappingLocation = mappingLocation;
    }

    public void setMappings(String ... mappings) {
        this.mappings = mappings;
    }

    @Override
    public void afterPropertiesSet() {
        this.getFileTypeMap();
    }

    protected final FileTypeMap getFileTypeMap() {
        if (this.fileTypeMap == null) {
            try {
                this.fileTypeMap = this.createFileTypeMap(this.mappingLocation, this.mappings);
            }
            catch (IOException ex) {
                throw new IllegalStateException("Could not load specified MIME type mapping file: " + this.mappingLocation, ex);
            }
        }
        return this.fileTypeMap;
    }

    protected FileTypeMap createFileTypeMap(@Nullable Resource mappingLocation, @Nullable String[] mappings) throws IOException {
        MimetypesFileTypeMap fileTypeMap = null;
        if (mappingLocation != null) {
            try (InputStream is = mappingLocation.getInputStream();){
                fileTypeMap = new MimetypesFileTypeMap(is);
            }
        } else {
            fileTypeMap = new MimetypesFileTypeMap();
        }
        if (mappings != null) {
            for (String mapping : mappings) {
                fileTypeMap.addMimeTypes(mapping);
            }
        }
        return fileTypeMap;
    }

    public String getContentType(File file) {
        return this.getFileTypeMap().getContentType(file);
    }

    public String getContentType(String fileName) {
        return this.getFileTypeMap().getContentType(fileName);
    }
}

