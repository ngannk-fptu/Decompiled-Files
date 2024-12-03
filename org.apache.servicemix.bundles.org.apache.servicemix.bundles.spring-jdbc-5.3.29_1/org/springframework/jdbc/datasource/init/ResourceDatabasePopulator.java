/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.EncodedResource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ResourceDatabasePopulator
implements DatabasePopulator {
    List<Resource> scripts = new ArrayList<Resource>();
    @Nullable
    private String sqlScriptEncoding;
    private String separator = ";";
    private String[] commentPrefixes = ScriptUtils.DEFAULT_COMMENT_PREFIXES;
    private String blockCommentStartDelimiter = "/*";
    private String blockCommentEndDelimiter = "*/";
    private boolean continueOnError = false;
    private boolean ignoreFailedDrops = false;

    public ResourceDatabasePopulator() {
    }

    public ResourceDatabasePopulator(Resource ... scripts) {
        this.setScripts(scripts);
    }

    public ResourceDatabasePopulator(boolean continueOnError, boolean ignoreFailedDrops, @Nullable String sqlScriptEncoding, Resource ... scripts) {
        this.continueOnError = continueOnError;
        this.ignoreFailedDrops = ignoreFailedDrops;
        this.setSqlScriptEncoding(sqlScriptEncoding);
        this.setScripts(scripts);
    }

    public void addScript(Resource script) {
        Assert.notNull((Object)script, (String)"'script' must not be null");
        this.scripts.add(script);
    }

    public void addScripts(Resource ... scripts) {
        this.assertContentsOfScriptArray(scripts);
        this.scripts.addAll(Arrays.asList(scripts));
    }

    public void setScripts(Resource ... scripts) {
        this.assertContentsOfScriptArray(scripts);
        this.scripts = new ArrayList<Resource>(Arrays.asList(scripts));
    }

    private void assertContentsOfScriptArray(Resource ... scripts) {
        Assert.notNull((Object)scripts, (String)"'scripts' must not be null");
        Assert.noNullElements((Object[])scripts, (String)"'scripts' must not contain null elements");
    }

    public void setSqlScriptEncoding(@Nullable String sqlScriptEncoding) {
        this.sqlScriptEncoding = StringUtils.hasText((String)sqlScriptEncoding) ? sqlScriptEncoding : null;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setCommentPrefix(String commentPrefix) {
        Assert.hasText((String)commentPrefix, (String)"'commentPrefix' must not be null or empty");
        this.commentPrefixes = new String[]{commentPrefix};
    }

    public void setCommentPrefixes(String ... commentPrefixes) {
        Assert.notEmpty((Object[])commentPrefixes, (String)"'commentPrefixes' must not be null or empty");
        Assert.noNullElements((Object[])commentPrefixes, (String)"'commentPrefixes' must not contain null elements");
        this.commentPrefixes = commentPrefixes;
    }

    public void setBlockCommentStartDelimiter(String blockCommentStartDelimiter) {
        Assert.hasText((String)blockCommentStartDelimiter, (String)"'blockCommentStartDelimiter' must not be null or empty");
        this.blockCommentStartDelimiter = blockCommentStartDelimiter;
    }

    public void setBlockCommentEndDelimiter(String blockCommentEndDelimiter) {
        Assert.hasText((String)blockCommentEndDelimiter, (String)"'blockCommentEndDelimiter' must not be null or empty");
        this.blockCommentEndDelimiter = blockCommentEndDelimiter;
    }

    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    public void setIgnoreFailedDrops(boolean ignoreFailedDrops) {
        this.ignoreFailedDrops = ignoreFailedDrops;
    }

    @Override
    public void populate(Connection connection) throws ScriptException {
        Assert.notNull((Object)connection, (String)"'connection' must not be null");
        for (Resource script : this.scripts) {
            EncodedResource encodedScript = new EncodedResource(script, this.sqlScriptEncoding);
            ScriptUtils.executeSqlScript(connection, encodedScript, this.continueOnError, this.ignoreFailedDrops, this.commentPrefixes, this.separator, this.blockCommentStartDelimiter, this.blockCommentEndDelimiter);
        }
    }

    public void execute(DataSource dataSource) throws ScriptException {
        DatabasePopulatorUtils.execute(this, dataSource);
    }
}

