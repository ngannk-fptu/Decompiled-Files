/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context.support;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.context.support.ServletContextResourceLoader;

public class ServletContextResourcePatternResolver
extends PathMatchingResourcePatternResolver {
    private static final Log logger = LogFactory.getLog(ServletContextResourcePatternResolver.class);

    public ServletContextResourcePatternResolver(ServletContext servletContext) {
        super(new ServletContextResourceLoader(servletContext));
    }

    public ServletContextResourcePatternResolver(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException {
        if (rootDirResource instanceof ServletContextResource) {
            ServletContextResource scResource = (ServletContextResource)rootDirResource;
            ServletContext sc = scResource.getServletContext();
            String fullPattern = scResource.getPath() + subPattern;
            LinkedHashSet<Resource> result = new LinkedHashSet<Resource>(8);
            this.doRetrieveMatchingServletContextResources(sc, fullPattern, scResource.getPath(), result);
            return result;
        }
        return super.doFindPathMatchingFileResources(rootDirResource, subPattern);
    }

    protected void doRetrieveMatchingServletContextResources(ServletContext servletContext, String fullPattern, String dir, Set<Resource> result) throws IOException {
        Set candidates = servletContext.getResourcePaths(dir);
        if (candidates != null) {
            boolean dirDepthNotFixed = fullPattern.contains("**");
            int jarFileSep = fullPattern.indexOf("!/");
            String jarFilePath = null;
            String pathInJarFile = null;
            if (jarFileSep > 0 && jarFileSep + "!/".length() < fullPattern.length()) {
                jarFilePath = fullPattern.substring(0, jarFileSep);
                pathInJarFile = fullPattern.substring(jarFileSep + "!/".length());
            }
            for (String currPath : candidates) {
                String absoluteJarPath;
                int dirIndex;
                if (!currPath.startsWith(dir) && (dirIndex = currPath.indexOf(dir)) != -1) {
                    currPath = currPath.substring(dirIndex);
                }
                if (currPath.endsWith("/") && (dirDepthNotFixed || StringUtils.countOccurrencesOf(currPath, "/") <= StringUtils.countOccurrencesOf(fullPattern, "/"))) {
                    this.doRetrieveMatchingServletContextResources(servletContext, fullPattern, currPath, result);
                }
                if (jarFilePath != null && this.getPathMatcher().match(jarFilePath, currPath) && (absoluteJarPath = servletContext.getRealPath(currPath)) != null) {
                    this.doRetrieveMatchingJarEntries(absoluteJarPath, pathInJarFile, result);
                }
                if (!this.getPathMatcher().match(fullPattern, currPath)) continue;
                result.add(new ServletContextResource(servletContext, currPath));
            }
        }
    }

    private void doRetrieveMatchingJarEntries(String jarFilePath, String entryPattern, Set<Resource> result) {
        block16: {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Searching jar file [" + jarFilePath + "] for entries matching [" + entryPattern + "]"));
            }
            try (JarFile jarFile = new JarFile(jarFilePath);){
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryPath = entry.getName();
                    if (!this.getPathMatcher().match(entryPattern, entryPath)) continue;
                    result.add(new UrlResource("jar", "file:" + jarFilePath + "!/" + entryPath));
                }
            }
            catch (IOException ex) {
                if (!logger.isWarnEnabled()) break block16;
                logger.warn((Object)("Cannot search for matching resources in jar file [" + jarFilePath + "] because the jar cannot be opened through the file system"), (Throwable)ex);
            }
        }
    }
}

