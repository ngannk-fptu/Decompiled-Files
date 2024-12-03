/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type != null && Path.class.isAssignableFrom(type);
    }

    public Path fromString(String str) {
        try {
            try {
                URI uri = new URI(str);
                if (uri.getScheme() == null || uri.getScheme().length() == 1) {
                    return Paths.get(File.separatorChar != '/' ? str.replace('/', File.separatorChar) : str, new String[0]);
                }
                return Paths.get(uri);
            }
            catch (URISyntaxException e) {
                return Paths.get(str, new String[0]);
            }
        }
        catch (InvalidPathException e) {
            throw new ConversionException(e);
        }
    }

    public String toString(Object obj) {
        Path path = (Path)obj;
        if (path.getFileSystem() == FileSystems.getDefault()) {
            String localPath = path.toString();
            if (File.separatorChar != '/') {
                return localPath.replace(File.separatorChar, '/');
            }
            return localPath;
        }
        return path.toUri().toString();
    }
}

