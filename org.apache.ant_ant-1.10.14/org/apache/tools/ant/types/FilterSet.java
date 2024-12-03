/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiConsumer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.VectorSet;

public class FilterSet
extends DataType
implements Cloneable {
    public static final String DEFAULT_TOKEN_START = "@";
    public static final String DEFAULT_TOKEN_END = "@";
    private String startOfToken = "@";
    private String endOfToken = "@";
    private Vector<String> passedTokens;
    private boolean duplicateToken = false;
    private boolean recurse = true;
    private Hashtable<String, String> filterHash = null;
    private Vector<File> filtersFiles = new Vector();
    private OnMissing onMissingFiltersFile = OnMissing.FAIL;
    private boolean readingFiles = false;
    private int recurseDepth = 0;
    private Vector<Filter> filters = new Vector();

    public FilterSet() {
    }

    protected FilterSet(FilterSet filterset) {
        Vector clone;
        this.filters = clone = (Vector)filterset.getFilters().clone();
    }

    protected synchronized Vector<Filter> getFilters() {
        if (this.isReference()) {
            return this.getRef().getFilters();
        }
        this.dieOnCircularReference();
        if (!this.readingFiles) {
            this.readingFiles = true;
            for (File filtersFile : this.filtersFiles) {
                this.readFiltersFromFile(filtersFile);
            }
            this.filtersFiles.clear();
            this.readingFiles = false;
        }
        return this.filters;
    }

    protected FilterSet getRef() {
        return this.getCheckedRef(FilterSet.class);
    }

    public synchronized Hashtable<String, String> getFilterHash() {
        if (this.isReference()) {
            return this.getRef().getFilterHash();
        }
        this.dieOnCircularReference();
        if (this.filterHash == null) {
            this.filterHash = new Hashtable(this.getFilters().size());
            this.getFilters().forEach(filter -> this.filterHash.put(filter.getToken(), filter.getValue()));
        }
        return this.filterHash;
    }

    public void setFiltersfile(File filtersFile) throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.filtersFiles.add(filtersFile);
    }

    public void setBeginToken(String startOfToken) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (startOfToken == null || startOfToken.isEmpty()) {
            throw new BuildException("beginToken must not be empty");
        }
        this.startOfToken = startOfToken;
    }

    public String getBeginToken() {
        if (this.isReference()) {
            return this.getRef().getBeginToken();
        }
        return this.startOfToken;
    }

    public void setEndToken(String endOfToken) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (endOfToken == null || endOfToken.isEmpty()) {
            throw new BuildException("endToken must not be empty");
        }
        this.endOfToken = endOfToken;
    }

    public String getEndToken() {
        if (this.isReference()) {
            return this.getRef().getEndToken();
        }
        return this.endOfToken;
    }

    public void setRecurse(boolean recurse) {
        this.recurse = recurse;
    }

    public boolean isRecurse() {
        return this.recurse;
    }

    public synchronized void readFiltersFromFile(File filtersFile) throws BuildException {
        block11: {
            if (this.isReference()) {
                throw this.tooManyAttributes();
            }
            if (!filtersFile.exists()) {
                this.handleMissingFile("Could not read filters from file " + filtersFile + " as it doesn't exist.");
            }
            if (filtersFile.isFile()) {
                this.log("Reading filters from " + filtersFile, 3);
                try (InputStream in = Files.newInputStream(filtersFile.toPath(), new OpenOption[0]);){
                    Properties props = new Properties();
                    props.load(in);
                    props.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(k, v) -> this.addFilter(new Filter((String)k, (String)v))));
                    break block11;
                }
                catch (Exception ex) {
                    throw new BuildException("Could not read filters from file: " + filtersFile, ex);
                }
            }
            this.handleMissingFile("Must specify a file rather than a directory in the filtersfile attribute:" + filtersFile);
        }
        this.filterHash = null;
    }

    public synchronized String replaceTokens(String line) {
        return this.iReplaceTokens(line);
    }

    public synchronized void addFilter(Filter filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.filters.addElement(filter);
        this.filterHash = null;
    }

    public FiltersFile createFiltersfile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        return new FiltersFile();
    }

    public synchronized void addFilter(String token, String value) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.addFilter(new Filter(token, value));
    }

    public synchronized void addConfiguredFilterSet(FilterSet filterSet) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        for (Filter filter : filterSet.getFilters()) {
            this.addFilter(filter);
        }
    }

    public synchronized void addConfiguredPropertySet(PropertySet propertySet) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        Properties p = propertySet.getProperties();
        Set<Map.Entry<Object, Object>> entries = p.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            this.addFilter(new Filter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
        }
    }

    public synchronized boolean hasFilters() {
        return !this.getFilters().isEmpty();
    }

    @Override
    public synchronized Object clone() throws BuildException {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        try {
            Vector clonedFilters;
            FilterSet fs = (FilterSet)super.clone();
            fs.filters = clonedFilters = (Vector)this.getFilters().clone();
            fs.setProject(this.getProject());
            return fs;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void setOnMissingFiltersFile(OnMissing onMissingFiltersFile) {
        this.onMissingFiltersFile = onMissingFiltersFile;
    }

    public OnMissing getOnMissingFiltersFile() {
        return this.onMissingFiltersFile;
    }

    private synchronized String iReplaceTokens(String line) {
        String beginToken = this.getBeginToken();
        String endToken = this.getEndToken();
        int index = line.indexOf(beginToken);
        if (index > -1) {
            Hashtable<String, String> tokens = this.getFilterHash();
            try {
                int endIndex;
                StringBuilder b = new StringBuilder();
                int i = 0;
                while (index > -1 && (endIndex = line.indexOf(endToken, index + beginToken.length() + 1)) != -1) {
                    String token = line.substring(index + beginToken.length(), endIndex);
                    b.append(line, i, index);
                    if (tokens.containsKey(token)) {
                        String value = tokens.get(token);
                        if (this.recurse && !value.equals(token)) {
                            value = this.replaceTokens(value, token);
                        }
                        this.log("Replacing: " + beginToken + token + endToken + " -> " + value, 3);
                        b.append(value);
                        i = index + beginToken.length() + token.length() + endToken.length();
                    } else {
                        b.append(beginToken.charAt(0));
                        i = index + 1;
                    }
                    index = line.indexOf(beginToken, i);
                }
                b.append(line.substring(i));
                return b.toString();
            }
            catch (StringIndexOutOfBoundsException e) {
                return line;
            }
        }
        return line;
    }

    private synchronized String replaceTokens(String line, String parent) throws BuildException {
        String beginToken = this.getBeginToken();
        String endToken = this.getEndToken();
        if (this.recurseDepth == 0) {
            this.passedTokens = new VectorSet<String>();
        }
        ++this.recurseDepth;
        if (this.passedTokens.contains(parent) && !this.duplicateToken) {
            this.duplicateToken = true;
            System.out.println("Infinite loop in tokens. Currently known tokens : " + this.passedTokens.toString() + "\nProblem token : " + beginToken + parent + endToken + " called from " + beginToken + this.passedTokens.lastElement() + endToken);
            --this.recurseDepth;
            return parent;
        }
        this.passedTokens.addElement(parent);
        String value = this.iReplaceTokens(line);
        if (!value.contains(beginToken) && !this.duplicateToken && this.recurseDepth == 1) {
            this.passedTokens = null;
        } else if (this.duplicateToken) {
            if (!this.passedTokens.isEmpty()) {
                value = this.passedTokens.remove(this.passedTokens.size() - 1);
                if (this.passedTokens.isEmpty()) {
                    value = beginToken + value + endToken;
                    this.duplicateToken = false;
                }
            }
        } else if (!this.passedTokens.isEmpty()) {
            this.passedTokens.remove(this.passedTokens.size() - 1);
        }
        --this.recurseDepth;
        return value;
    }

    private void handleMissingFile(String message) {
        switch (this.onMissingFiltersFile.getIndex()) {
            case 2: {
                return;
            }
            case 0: {
                throw new BuildException(message);
            }
            case 1: {
                this.log(message, 1);
                return;
            }
        }
        throw new BuildException("Invalid value for onMissingFiltersFile");
    }

    public static class OnMissing
    extends EnumeratedAttribute {
        private static final String[] VALUES = new String[]{"fail", "warn", "ignore"};
        public static final OnMissing FAIL = new OnMissing("fail");
        public static final OnMissing WARN = new OnMissing("warn");
        public static final OnMissing IGNORE = new OnMissing("ignore");
        private static final int FAIL_INDEX = 0;
        private static final int WARN_INDEX = 1;
        private static final int IGNORE_INDEX = 2;

        public OnMissing() {
        }

        public OnMissing(String value) {
            this.setValue(value);
        }

        @Override
        public String[] getValues() {
            return VALUES;
        }
    }

    public class FiltersFile {
        public void setFile(File file) {
            FilterSet.this.filtersFiles.add(file);
        }
    }

    public static class Filter {
        String token;
        String value;

        public Filter(String token, String value) {
            this.setToken(token);
            this.setValue(value);
        }

        public Filter() {
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getToken() {
            return this.token;
        }

        public String getValue() {
            return this.value;
        }
    }
}

