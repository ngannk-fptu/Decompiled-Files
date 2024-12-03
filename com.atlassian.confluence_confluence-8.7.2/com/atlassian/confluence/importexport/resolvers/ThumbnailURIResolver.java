/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.resolvers;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.filestore.FileStoreFactory;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class ThumbnailURIResolver
implements URIResolver {
    private final FilesystemPath confluenceHome;

    @Deprecated
    public ThumbnailURIResolver() {
        this(new FileStoreFactory((BootstrapManager)BootstrapUtils.getBootstrapManager()).getConfluenceHome());
    }

    public ThumbnailURIResolver(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        File brokenThumbnailFile;
        File fixedThumbnailFile;
        if (StringUtils.defaultString((String)href).indexOf("thumbnails/") != -1 && (fixedThumbnailFile = this.getFixedThumbnailFile(brokenThumbnailFile = new File(this.getHomeDir(), href))).isFile()) {
            String systemId = this.getSystemId(fixedThumbnailFile);
            try {
                if (StringUtils.isEmpty((CharSequence)systemId)) {
                    return new StreamSource(new FileInputStream(fixedThumbnailFile));
                }
                return new StreamSource(new FileInputStream(fixedThumbnailFile), systemId);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return null;
    }

    File getFixedThumbnailFile(File brokenThumbnailFile) {
        String imageFilename = brokenThumbnailFile.getName();
        return new File(brokenThumbnailFile.getParentFile(), "thumb_" + imageFilename);
    }

    private String getSystemId(File resource) {
        String result = null;
        try {
            result = resource.toURL().toExternalForm();
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        return result;
    }

    private File getHomeDir() {
        return this.confluenceHome.asJavaFile();
    }
}

