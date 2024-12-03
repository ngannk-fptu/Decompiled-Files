/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.dom4j.Document
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.dom4j.io.OutputFormat
 *  org.dom4j.io.SAXReader
 *  org.dom4j.io.XMLWriter
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileGroup;
import com.opensymphony.user.provider.file.FileGroupsCache;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

class XMLGroupsCache
extends FileGroupsCache {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$XMLGroupsCache == null ? (class$com$opensymphony$user$provider$file$XMLGroupsCache = XMLGroupsCache.class$("com.opensymphony.user.provider.file.XMLGroupsCache")) : class$com$opensymphony$user$provider$file$XMLGroupsCache));
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$XMLGroupsCache;

    public XMLGroupsCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
        this.load();
    }

    public boolean load() {
        try {
            this.groups = new HashMap();
            SAXReader reader = new SAXReader();
            Document document = null;
            document = reader.read(this.getInputStreamFromStoreFile());
            Element root = document.getRootElement();
            if (log.isDebugEnabled()) {
                log.debug((Object)("loaded " + this.storeFile));
            }
            Iterator i = root.elementIterator();
            while (i.hasNext()) {
                Element groupElement = (Element)i.next();
                if (groupElement.getName().equals("group")) {
                    String groupName = groupElement.attribute("id").getValue();
                    FileGroup group = new FileGroup();
                    group.name = groupName;
                    Iterator j = groupElement.elementIterator();
                    while (j.hasNext()) {
                        Element userElement = (Element)j.next();
                        if (userElement.getName().equals("user")) {
                            String userName = userElement.attribute("id").getValue();
                            group.users.add(userName);
                            log.debug((Object)("added user " + userName + " to group " + groupName));
                            continue;
                        }
                        log.warn((Object)("expected 'user' element, found " + userElement.getName() + " in " + this.storeFile));
                    }
                    this.groups.put(groupName, group);
                    log.debug((Object)("added group " + groupName));
                    continue;
                }
                log.warn((Object)("expected 'group' element, found " + groupElement.getName() + " in " + this.storeFile));
            }
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)("cannot load from " + this.storeFile + "."), (Throwable)e);
            return false;
        }
    }

    public boolean store() {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("groups");
            Iterator i = this.groups.keySet().iterator();
            while (i.hasNext()) {
                String groupKey = i.next().toString();
                FileGroup group = (FileGroup)this.groups.get(groupKey);
                Element groupElement = root.addElement("group").addAttribute("id", group.name);
                Iterator j = group.users.iterator();
                while (j.hasNext()) {
                    groupElement.addElement("user").addAttribute("id", j.next().toString());
                }
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter((Writer)new FileWriter(this.storeFile));
            writer.write(document);
            writer.close();
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)("cannot store in file " + this.storeFile + "."), (Throwable)e);
            return false;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

