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

import com.opensymphony.user.provider.file.FileUser;
import com.opensymphony.user.provider.file.FileUsersCache;
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

class XMLUsersCache
extends FileUsersCache {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$XMLUsersCache == null ? (class$com$opensymphony$user$provider$file$XMLUsersCache = XMLUsersCache.class$("com.opensymphony.user.provider.file.XMLUsersCache")) : class$com$opensymphony$user$provider$file$XMLUsersCache));
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$XMLUsersCache;

    public XMLUsersCache(String storeFile, String storeFileType) {
        super(storeFile, storeFileType);
        this.load();
    }

    public boolean load() {
        try {
            this.users = new HashMap();
            SAXReader reader = new SAXReader();
            Document document = reader.read(this.getInputStreamFromStoreFile());
            Element root = document.getRootElement();
            if (log.isDebugEnabled()) {
                log.debug((Object)("loaded " + this.storeFile));
            }
            Iterator i = root.elementIterator();
            while (i.hasNext()) {
                Element groupElement = (Element)i.next();
                if (groupElement.getName().equals("user")) {
                    if (groupElement.attribute("id") == null) {
                        log.warn((Object)("attribute 'id' required for <user/> in " + this.storeFile));
                        continue;
                    }
                    String userName = groupElement.attribute("id").getValue();
                    String userPassword = null;
                    if (groupElement.attribute("password") != null) {
                        userPassword = groupElement.attribute("password").getValue();
                    }
                    FileUser user = new FileUser();
                    user.name = userName;
                    user.password = userPassword;
                    this.users.put(userName, user);
                    log.debug((Object)("added user credentials " + userName));
                    continue;
                }
                log.warn((Object)("expected 'user' element, found " + groupElement.getName() + " in " + this.storeFile));
            }
            return true;
        }
        catch (Exception e) {
            log.fatal((Object)("cannot load from file " + this.storeFile + "."), (Throwable)e);
            return false;
        }
    }

    public boolean store() {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("users");
            Iterator i = this.users.keySet().iterator();
            while (i.hasNext()) {
                String userKey = i.next().toString();
                FileUser user = (FileUser)this.users.get(userKey);
                Element userElement = root.addElement("user").addAttribute("id", user.name).addAttribute("password", user.password);
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

