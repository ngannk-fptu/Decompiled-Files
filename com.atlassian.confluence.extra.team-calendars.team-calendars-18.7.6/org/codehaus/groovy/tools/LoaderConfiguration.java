/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.groovy.util.SystemUtil;

public class LoaderConfiguration {
    private static final String MAIN_PREFIX = "main is";
    private static final String LOAD_PREFIX = "load";
    private static final String GRAB_PREFIX = "grab";
    private static final String PROP_PREFIX = "property";
    private List<URL> classPath = new ArrayList<URL>();
    private String main;
    private boolean requireMain = true;
    private static final char WILDCARD = '*';
    private static final String ALL_WILDCARD = "**";
    private static final String MATCH_FILE_NAME = "\\\\E[^/]+?\\\\Q";
    private static final String MATCH_ALL = "\\\\E.+?\\\\Q";
    private final List<String> grabList = new ArrayList<String>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void configure(InputStream is) throws IOException {
        block10: {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                String line;
                int lineNumber = 0;
                while (true) {
                    if ((line = reader.readLine()) == null) {
                        break block10;
                    }
                    line = line.trim();
                    ++lineNumber;
                    if (line.startsWith("#") || line.length() == 0) continue;
                    if (line.startsWith(LOAD_PREFIX)) {
                        String loadPath = line.substring(LOAD_PREFIX.length()).trim();
                        loadPath = LoaderConfiguration.assignProperties(loadPath);
                        this.loadFilteredPath(loadPath);
                        continue;
                    }
                    if (line.startsWith(GRAB_PREFIX)) {
                        String grabParams = line.substring(GRAB_PREFIX.length()).trim();
                        this.grabList.add(LoaderConfiguration.assignProperties(grabParams));
                        continue;
                    }
                    if (line.startsWith(MAIN_PREFIX)) {
                        if (this.main != null) {
                            throw new IOException("duplicate definition of main in line " + lineNumber + " : " + line);
                        }
                        this.main = line.substring(MAIN_PREFIX.length()).trim();
                        continue;
                    }
                    if (!line.startsWith(PROP_PREFIX)) break;
                    String params = line.substring(PROP_PREFIX.length()).trim();
                    String key = SystemUtil.setSystemPropertyFrom(params);
                    System.setProperty(key, LoaderConfiguration.assignProperties(System.getProperty(key)));
                }
                throw new IOException("unexpected line in " + lineNumber + " : " + line);
            }
            finally {
                reader.close();
            }
        }
        if (this.requireMain && this.main == null) {
            throw new IOException("missing main class definition in config file");
        }
    }

    private static String assignProperties(String str) {
        int propertyIndexStart = 0;
        int propertyIndexEnd = 0;
        StringBuilder result = new StringBuilder();
        while (propertyIndexStart < str.length()) {
            boolean requireProperty;
            int i1 = str.indexOf("${", propertyIndexStart);
            int i2 = str.indexOf("!{", propertyIndexStart);
            propertyIndexStart = i1 == -1 ? i2 : (i2 == -1 ? i1 : Math.min(i1, i2));
            boolean bl = requireProperty = propertyIndexStart == i2;
            if (propertyIndexStart == -1) break;
            result.append(str.substring(propertyIndexEnd, propertyIndexStart));
            propertyIndexEnd = str.indexOf("}", propertyIndexStart);
            if (propertyIndexEnd == -1) break;
            String propertyKey = str.substring(propertyIndexStart + 2, propertyIndexEnd);
            String propertyValue = System.getProperty(propertyKey);
            if (propertyValue == null) {
                if (requireProperty) {
                    throw new IllegalArgumentException("Variable " + propertyKey + " in groovy-starter.conf references a non-existent System property! Try passing the property to the VM using -D" + propertyKey + "=myValue in JAVA_OPTS");
                }
                return null;
            }
            propertyValue = LoaderConfiguration.getSlashyPath(propertyValue);
            propertyValue = LoaderConfiguration.correctDoubleSlash(propertyValue, propertyIndexEnd, str);
            result.append(propertyValue);
            propertyIndexStart = ++propertyIndexEnd;
        }
        if (propertyIndexStart == -1 || propertyIndexStart >= str.length()) {
            result.append(str.substring(propertyIndexEnd));
        } else if (propertyIndexEnd == -1) {
            result.append(str.substring(propertyIndexStart));
        }
        return result.toString();
    }

    private static String correctDoubleSlash(String propertyValue, int propertyIndexEnd, String str) {
        int index = propertyIndexEnd + 1;
        if (index < str.length() && str.charAt(index) == '/' && propertyValue.endsWith("/") && propertyValue.length() > 0) {
            propertyValue = propertyValue.substring(0, propertyValue.length() - 1);
        }
        return propertyValue;
    }

    private void loadFilteredPath(String filter) {
        if (filter == null) {
            return;
        }
        int starIndex = (filter = LoaderConfiguration.getSlashyPath(filter)).indexOf(42);
        if (starIndex == -1) {
            this.addFile(new File(filter));
            return;
        }
        boolean recursive = filter.contains(ALL_WILDCARD);
        if (filter.lastIndexOf(47) < starIndex) {
            starIndex = filter.lastIndexOf(47) + 1;
        }
        String startDir = filter.substring(0, starIndex - 1);
        File root = new File(startDir);
        filter = Pattern.quote(filter);
        filter = filter.replaceAll("\\*\\*", MATCH_ALL);
        filter = filter.replaceAll("\\*", MATCH_FILE_NAME);
        Pattern pattern = Pattern.compile(filter);
        File[] files = root.listFiles();
        if (files != null) {
            this.findMatchingFiles(files, pattern, recursive);
        }
    }

    private void findMatchingFiles(File[] files, Pattern pattern, boolean recursive) {
        for (File file : files) {
            File[] dirFiles;
            String fileString = LoaderConfiguration.getSlashyPath(file.getPath());
            Matcher m = pattern.matcher(fileString);
            if (m.matches() && file.isFile()) {
                this.addFile(file);
            }
            if (!file.isDirectory() || !recursive || (dirFiles = file.listFiles()) == null) continue;
            this.findMatchingFiles(dirFiles, pattern, true);
        }
    }

    private static String getSlashyPath(String path) {
        String changedPath = path;
        if (File.separatorChar != '/') {
            changedPath = changedPath.replace(File.separatorChar, '/');
        }
        return changedPath;
    }

    public void addFile(File file) {
        if (file != null && file.exists()) {
            try {
                this.classPath.add(file.toURI().toURL());
            }
            catch (MalformedURLException e) {
                throw new AssertionError((Object)"converting an existing file to an url should have never thrown an exception!");
            }
        }
    }

    public void addFile(String filename) {
        if (filename != null) {
            this.addFile(new File(filename));
        }
    }

    public void addClassPath(String path) {
        String[] paths;
        for (String cpPath : paths = path.split(File.pathSeparator)) {
            if (cpPath.endsWith("*")) {
                File dir = new File(cpPath.substring(0, cpPath.length() - 1));
                File[] files = dir.listFiles();
                if (files == null) continue;
                for (File file : files) {
                    if (!file.isFile() || !file.getName().endsWith(".jar")) continue;
                    this.addFile(file);
                }
                continue;
            }
            this.addFile(new File(cpPath));
        }
    }

    public URL[] getClassPathUrls() {
        return this.classPath.toArray(new URL[this.classPath.size()]);
    }

    public List<String> getGrabUrls() {
        return this.grabList;
    }

    public String getMainClass() {
        return this.main;
    }

    public void setMainClass(String classname) {
        this.main = classname;
        this.requireMain = false;
    }

    public void setRequireMain(boolean requireMain) {
        this.requireMain = requireMain;
    }
}

