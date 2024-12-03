/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.resource;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.resource.BadResource;
import org.eclipse.jetty.util.resource.JarFileResource;
import org.eclipse.jetty.util.resource.JarResource;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.ResourceCollators;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.URLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Resource
implements ResourceFactory,
Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);
    public static boolean __defaultUseCaches = true;
    volatile Object _associate;

    public static void setDefaultUseCaches(boolean useCaches) {
        __defaultUseCaches = useCaches;
    }

    public static boolean getDefaultUseCaches() {
        return __defaultUseCaches;
    }

    public static Resource resolveAlias(Resource resource) {
        block4: {
            if (!resource.isAlias()) {
                return resource;
            }
            try {
                File file = resource.getFile();
                if (file != null) {
                    return Resource.newResource(file.toPath().toRealPath(new LinkOption[0]));
                }
            }
            catch (IOException e) {
                if (!LOG.isDebugEnabled()) break block4;
                LOG.debug("resolve alias failed", (Throwable)e);
            }
        }
        return resource;
    }

    public static Resource newResource(URI uri) throws MalformedURLException {
        return Resource.newResource(uri.toURL());
    }

    public static Resource newResource(URL url) {
        return Resource.newResource(url, __defaultUseCaches);
    }

    static Resource newResource(URL url, boolean useCaches) {
        if (url == null) {
            return null;
        }
        String urlString = url.toExternalForm();
        if (urlString.startsWith("file:")) {
            try {
                return new PathResource(url);
            }
            catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Bad PathResource: {}", (Object)url, (Object)e);
                } else {
                    LOG.warn("Bad PathResource: {} {}", (Object)url, (Object)e.toString());
                }
                return new BadResource(url, e.toString());
            }
        }
        if (urlString.startsWith("jar:file:")) {
            return new JarFileResource(url, useCaches);
        }
        if (urlString.startsWith("jar:")) {
            return new JarResource(url, useCaches);
        }
        return new URLResource(url, null, useCaches);
    }

    public static Resource newResource(String resource) throws IOException {
        return Resource.newResource(resource, __defaultUseCaches);
    }

    public static Resource newResource(String resource, boolean useCaches) throws IOException {
        URL url;
        try {
            url = new URL(resource);
        }
        catch (MalformedURLException e) {
            if (!(resource.startsWith("ftp:") || resource.startsWith("file:") || resource.startsWith("jar:"))) {
                return new PathResource(Paths.get(resource, new String[0]));
            }
            LOG.warn("Bad Resource: {}", (Object)resource);
            throw e;
        }
        return Resource.newResource(url, useCaches);
    }

    public static Resource newResource(File file) {
        return new PathResource(file.toPath());
    }

    public static Resource newResource(Path path) {
        return new PathResource(path);
    }

    public static Resource newSystemResource(String resource) throws IOException {
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            try {
                url = loader.getResource(resource);
                if (url == null && resource.startsWith("/")) {
                    url = loader.getResource(resource.substring(1));
                }
            }
            catch (IllegalArgumentException e) {
                LOG.trace("IGNORED", (Throwable)e);
                url = null;
            }
        }
        if (url == null && (loader = Resource.class.getClassLoader()) != null && (url = loader.getResource(resource)) == null && resource.startsWith("/")) {
            url = loader.getResource(resource.substring(1));
        }
        if (url == null && (url = ClassLoader.getSystemResource(resource)) == null && resource.startsWith("/")) {
            url = ClassLoader.getSystemResource(resource.substring(1));
        }
        if (url == null) {
            return null;
        }
        return Resource.newResource(url);
    }

    public static Resource newClassPathResource(String resource) {
        return Resource.newClassPathResource(resource, true, false);
    }

    public static Resource newClassPathResource(String name, boolean useCaches, boolean checkParents) {
        URL url = Resource.class.getResource(name);
        if (url == null) {
            url = Loader.getResource(name);
        }
        if (url == null) {
            return null;
        }
        return Resource.newResource(url, useCaches);
    }

    public static boolean isContainedIn(Resource r, Resource containingResource) throws MalformedURLException {
        return r.isContainedIn(containingResource);
    }

    public abstract boolean isContainedIn(Resource var1) throws MalformedURLException;

    public boolean isSame(Resource resource) {
        return this.equals(resource);
    }

    @Override
    public abstract void close();

    public abstract boolean exists();

    public abstract boolean isDirectory();

    public abstract long lastModified();

    public abstract long length();

    public abstract URI getURI();

    public abstract File getFile() throws IOException;

    public abstract String getName();

    public abstract InputStream getInputStream() throws IOException;

    public abstract ReadableByteChannel getReadableByteChannel() throws IOException;

    public abstract boolean delete() throws SecurityException;

    public abstract boolean renameTo(Resource var1) throws SecurityException;

    public abstract String[] list();

    public abstract Resource addPath(String var1) throws IOException, MalformedURLException;

    @Override
    public Resource getResource(String path) throws IOException {
        return this.addPath(path);
    }

    public Object getAssociate() {
        return this._associate;
    }

    public void setAssociate(Object o) {
        this._associate = o;
    }

    public boolean isAlias() {
        return this.getAlias() != null;
    }

    public URI getAlias() {
        return null;
    }

    public String getListHTML(String base, boolean parent, String query) throws IOException {
        if ((base = URIUtil.canonicalPath(base)) == null || !this.isDirectory()) {
            return null;
        }
        String[] rawListing = this.list();
        if (rawListing == null) {
            return null;
        }
        boolean sortOrderAscending = true;
        String sortColumn = "N";
        if (query != null) {
            MultiMap<String> params = new MultiMap<String>();
            UrlEncoded.decodeUtf8To(query, 0, query.length(), params);
            String paramO = params.getString("O");
            String paramC = params.getString("C");
            if (StringUtil.isNotBlank(paramO)) {
                if (paramO.equals("A")) {
                    sortOrderAscending = true;
                } else if (paramO.equals("D")) {
                    sortOrderAscending = false;
                }
            }
            if (StringUtil.isNotBlank(paramC) && (paramC.equals("N") || paramC.equals("M") || paramC.equals("S"))) {
                sortColumn = paramC;
            }
        }
        ArrayList<? super Resource> items = new ArrayList<Resource>();
        for (String l : rawListing) {
            Resource item = this.addPath(l);
            items.add(item);
        }
        if (sortColumn.equals("M")) {
            items.sort(ResourceCollators.byLastModified(sortOrderAscending));
        } else if (sortColumn.equals("S")) {
            items.sort(ResourceCollators.bySize(sortOrderAscending));
        } else {
            items.sort(ResourceCollators.byName(sortOrderAscending));
        }
        String decodedBase = URIUtil.decodePath(base);
        String title = "Directory: " + Resource.deTag(decodedBase);
        StringBuilder buf = new StringBuilder(4096);
        buf.append("<!DOCTYPE html>\n");
        buf.append("<html lang=\"en\">\n");
        buf.append("<head>\n");
        buf.append("<meta charset=\"utf-8\">\n");
        buf.append("<link href=\"jetty-dir.css\" rel=\"stylesheet\" />\n");
        buf.append("<title>");
        buf.append(title);
        buf.append("</title>\n");
        buf.append("</head>\n");
        buf.append("<body>\n");
        buf.append("<h1 class=\"title\">").append(title).append("</h1>\n");
        String ARROW_DOWN = "&nbsp; &#8681;";
        String ARROW_UP = "&nbsp; &#8679;";
        buf.append("<table class=\"listing\">\n");
        buf.append("<thead>\n");
        String arrow = "";
        String order = "A";
        if (sortColumn.equals("N")) {
            if (sortOrderAscending) {
                order = "D";
                arrow = "&nbsp; &#8679;";
            } else {
                order = "A";
                arrow = "&nbsp; &#8681;";
            }
        }
        buf.append("<tr><th class=\"name\"><a href=\"?C=N&O=").append(order).append("\">");
        buf.append("Name").append(arrow);
        buf.append("</a></th>");
        arrow = "";
        order = "A";
        if (sortColumn.equals("M")) {
            if (sortOrderAscending) {
                order = "D";
                arrow = "&nbsp; &#8679;";
            } else {
                order = "A";
                arrow = "&nbsp; &#8681;";
            }
        }
        buf.append("<th class=\"lastmodified\"><a href=\"?C=M&O=").append(order).append("\">");
        buf.append("Last Modified").append(arrow);
        buf.append("</a></th>");
        arrow = "";
        order = "A";
        if (sortColumn.equals("S")) {
            if (sortOrderAscending) {
                order = "D";
                arrow = "&nbsp; &#8679;";
            } else {
                order = "A";
                arrow = "&nbsp; &#8681;";
            }
        }
        buf.append("<th class=\"size\"><a href=\"?C=S&O=").append(order).append("\">");
        buf.append("Size").append(arrow);
        buf.append("</a></th></tr>\n");
        buf.append("</thead>\n");
        buf.append("<tbody>\n");
        String encodedBase = Resource.hrefEncodeURI(base);
        if (parent) {
            buf.append("<tr><td class=\"name\"><a href=\"");
            buf.append(URIUtil.addPaths(encodedBase, "../"));
            buf.append("\">Parent Directory</a></td>");
            buf.append("<td class=\"lastmodified\">-</td>");
            buf.append("<td>-</td>");
            buf.append("</tr>\n");
        }
        DateFormat dfmt = DateFormat.getDateTimeInstance(2, 2);
        for (Resource resource : items) {
            Object name = resource.getFileName();
            if (StringUtil.isBlank((String)name)) continue;
            if (resource.isDirectory() && !((String)name).endsWith("/")) {
                name = (String)name + "/";
            }
            buf.append("<tr><td class=\"name\"><a href=\"");
            String path = URIUtil.addEncodedPaths(encodedBase, URIUtil.encodePath((String)name));
            buf.append(path);
            buf.append("\">");
            buf.append(Resource.deTag((String)name));
            buf.append("&nbsp;");
            buf.append("</a></td>");
            buf.append("<td class=\"lastmodified\">");
            long lastModified = resource.lastModified();
            if (lastModified > 0L) {
                buf.append(dfmt.format(new Date(resource.lastModified())));
            }
            buf.append("&nbsp;</td>");
            buf.append("<td class=\"size\">");
            long length = resource.length();
            if (length >= 0L) {
                buf.append(String.format("%,d bytes", resource.length()));
            }
            buf.append("&nbsp;</td></tr>\n");
        }
        buf.append("</tbody>\n");
        buf.append("</table>\n");
        buf.append("</body></html>\n");
        return buf.toString();
    }

    private String getFileName() {
        try {
            File file = this.getFile();
            if (file != null) {
                return file.getName();
            }
        }
        catch (Throwable file) {
            // empty catch block
        }
        try {
            String rawName = this.getName();
            int idx = rawName.lastIndexOf(47);
            if (idx == rawName.length() - 1) {
                idx = rawName.lastIndexOf(47, idx - 1);
            }
            String encodedFileName = idx >= 0 ? rawName.substring(idx + 1) : rawName;
            return UrlEncoded.decodeString(encodedFileName, 0, encodedFileName.length(), StandardCharsets.UTF_8);
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private static String hrefEncodeURI(String raw) {
        char c;
        int i;
        StringBuffer buf = null;
        block9: for (i = 0; i < raw.length(); ++i) {
            c = raw.charAt(i);
            switch (c) {
                case '\"': 
                case '\'': 
                case '<': 
                case '>': {
                    buf = new StringBuffer(raw.length() << 1);
                    break block9;
                }
                default: {
                    continue block9;
                }
            }
        }
        if (buf == null) {
            return raw;
        }
        block10: for (i = 0; i < raw.length(); ++i) {
            c = raw.charAt(i);
            switch (c) {
                case '\"': {
                    buf.append("%22");
                    continue block10;
                }
                case '\'': {
                    buf.append("%27");
                    continue block10;
                }
                case '<': {
                    buf.append("%3C");
                    continue block10;
                }
                case '>': {
                    buf.append("%3E");
                    continue block10;
                }
                default: {
                    buf.append(c);
                }
            }
        }
        return buf.toString();
    }

    private static String deTag(String raw) {
        return StringUtil.sanitizeXmlString(raw);
    }

    public void copyTo(File destination) throws IOException {
        if (destination.exists()) {
            throw new IllegalArgumentException(destination + " exists");
        }
        File src = this.getFile();
        if (src != null) {
            Files.copy(src.toPath(), destination.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        try (InputStream in = this.getInputStream();
             FileOutputStream out = new FileOutputStream(destination);){
            IO.copy(in, out);
        }
    }

    public String getWeakETag() {
        return this.getWeakETag("");
    }

    public String getWeakETag(String suffix) {
        StringBuilder b = new StringBuilder(32);
        b.append("W/\"");
        String name = this.getName();
        int length = name.length();
        long lhash = 0L;
        for (int i = 0; i < length; ++i) {
            lhash = 31L * lhash + (long)name.charAt(i);
        }
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        b.append(encoder.encodeToString(Resource.longToBytes(this.lastModified() ^ lhash)));
        b.append(encoder.encodeToString(Resource.longToBytes(this.length() ^ lhash)));
        b.append(suffix);
        b.append('\"');
        return b.toString();
    }

    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; --i) {
            result[i] = (byte)(value & 0xFFL);
            value >>= 8;
        }
        return result;
    }

    public Collection<Resource> getAllResources() {
        try {
            ArrayList<Resource> deep = new ArrayList<Resource>();
            String[] list = this.list();
            if (list != null) {
                for (String i : list) {
                    Resource r = this.addPath(i);
                    if (r.isDirectory()) {
                        deep.addAll(r.getAllResources());
                        continue;
                    }
                    deep.add(r);
                }
            }
            return deep;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL toURL(File file) throws MalformedURLException {
        return file.toURI().toURL();
    }

    public static List<Resource> fromList(String resources, boolean globDirs) throws IOException {
        return Resource.fromList(resources, globDirs, Resource::newResource);
    }

    public static List<Resource> fromList(String resources, boolean globDirs, ResourceFactory resourceFactory) throws IOException {
        if (StringUtil.isBlank(resources)) {
            return Collections.emptyList();
        }
        ArrayList<Resource> returnedResources = new ArrayList<Resource>();
        StringTokenizer tokenizer = new StringTokenizer(resources, ",;");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.endsWith("/*") || token.endsWith("\\*")) {
                Object[] entries;
                String dir = token.substring(0, token.length() - 2);
                Resource dirResource = resourceFactory.getResource(dir);
                if (!dirResource.exists() || !dirResource.isDirectory() || (entries = dirResource.list()) == null) continue;
                Arrays.sort(entries);
                for (Object entry : entries) {
                    try {
                        Resource resource = dirResource.addPath((String)entry);
                        if (!resource.isDirectory()) {
                            returnedResources.add(resource);
                            continue;
                        }
                        if (!globDirs) continue;
                        returnedResources.add(resource);
                    }
                    catch (Exception ex) {
                        LOG.warn("Bad glob [{}] entry: {}", new Object[]{token, entry, ex});
                    }
                }
                continue;
            }
            returnedResources.add(resourceFactory.getResource(token));
        }
        return returnedResources;
    }
}

