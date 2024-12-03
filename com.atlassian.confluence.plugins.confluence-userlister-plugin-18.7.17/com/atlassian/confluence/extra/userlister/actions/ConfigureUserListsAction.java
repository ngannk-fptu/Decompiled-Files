/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.google.common.base.Joiner
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.userlister.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.userlister.UserListManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.base.Joiner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigureUserListsAction
extends ConfluenceActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(ConfigureUserListsAction.class);
    private String blackListEntries;
    private UserListManager userListManager;
    private UserAccessor userAccessor;
    private String save;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<String> getBlackListEntriesCollection() throws IOException {
        TreeSet<String> treeSet;
        BufferedReader bufferedReader = null;
        try {
            String line;
            bufferedReader = new BufferedReader(new StringReader(StringUtils.defaultString((String)this.blackListEntries)));
            TreeSet<String> blackListEntries = new TreeSet<String>();
            while (null != (line = bufferedReader.readLine())) {
                blackListEntries.add(StringUtils.trim((String)line));
            }
            treeSet = blackListEntries;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(bufferedReader);
            throw throwable;
        }
        IOUtils.closeQuietly((Reader)bufferedReader);
        return treeSet;
    }

    public String getBlackListEntries() {
        return this.blackListEntries;
    }

    public void setBlackListEntries(String blackListEntries) {
        this.blackListEntries = blackListEntries;
    }

    public void setUserListManager(UserListManager userListManager) {
        this.userListManager = userListManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        super.setUserAccessor(userAccessor);
        this.userAccessor = userAccessor;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String doDefault() {
        this.setBlackListEntries(StringUtils.join(this.userListManager.getGroupBlackList(), (String)"\n"));
        return "success";
    }

    public String execute() throws Exception {
        if (StringUtils.equals((CharSequence)this.getText("add.name"), (CharSequence)this.save)) {
            this.userListManager.saveGroupBlackList(this.getBlackListEntriesCollection());
            this.addActionMessage(this.getText("userlister.configure.successful"));
            return "success";
        }
        return "cancel";
    }

    public void validate() {
        try {
            Collection encodedInvalidGroupNames = this.getBlackListEntriesCollection().stream().filter(name -> !StringUtils.equals((CharSequence)"*", (CharSequence)name) && null == this.userAccessor.getGroup(name)).map(GeneralUtil::htmlEncode).collect(Collectors.toSet());
            String joinedEncodedInvalidGroupNames = Joiner.on((String)", ").join((Iterable)encodedInvalidGroupNames);
            if (!encodedInvalidGroupNames.isEmpty()) {
                this.addActionError(this.getText("userlister.configure.invalid.group.names"), new Object[]{joinedEncodedInvalidGroupNames});
            }
        }
        catch (IOException ioe) {
            logger.error("Unable to perform action validation", (Throwable)ioe);
            this.addActionError(this.getText("userlister.configure.ioexception"));
        }
    }
}

