/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.api.WstxInputProperties
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.newexport.processor;

import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import com.ctc.wstx.api.WstxInputProperties;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserKeyXmlExtractor
implements RowProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserKeyXmlExtractor.class);
    private static final String BODY_COLUMN_NAME = "body";
    private static final String USER_TAG = "ri:user";
    private static final String USERKEY_ATTR = "ri:userkey";
    private final RowProcessor delegateProcessor;
    private final XMLInputFactory factory;
    private final Set<String> extractedUsers;

    public UserKeyXmlExtractor(RowProcessor processor, Set<String> users) {
        this.delegateProcessor = processor;
        this.extractedUsers = users;
        this.factory = XMLInputFactory.newInstance();
        this.factory.setProperty("javax.xml.stream.supportDTD", false);
        this.factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        this.factory.setProperty("javax.xml.stream.isNamespaceAware", false);
        this.factory.setProperty("javax.xml.stream.isReplacingEntityReferences", false);
        this.factory.setProperty("javax.xml.stream.isCoalescing", false);
        this.factory.setProperty("com.ctc.wstx.fragmentMode", WstxInputProperties.PARSING_MODE_FRAGMENT);
        if (this.doesObjectContainField(WstxInputProperties.class, "P_NORMALIZE_ATTR_VALUES")) {
            try {
                this.factory.setProperty(String.valueOf(WstxInputProperties.class.getField("P_NORMALIZE_ATTR_VALUES")), false);
            }
            catch (NoSuchFieldException e) {
                log.debug("P_NORMALIZE_ATTR_VALUES not found in WstxInputProperties");
            }
        }
    }

    public boolean doesObjectContainField(Object object, String fieldName) {
        return Arrays.stream(object.getClass().getFields()).anyMatch(f -> f.getName().equals(fieldName));
    }

    @Override
    public void initialise(ResultSet rs, Query query) {
        this.delegateProcessor.initialise(rs, query);
    }

    @Override
    public void process(ResultSet rs) {
        this.delegateProcessor.process(rs);
        try {
            String bodyContentStr = rs.getString(BODY_COLUMN_NAME);
            this.extractedUsers.addAll(this.extractUserKeys(bodyContentStr));
        }
        catch (SQLException e) {
            log.warn("Error while extracting user mentions from body content", (Throwable)e);
        }
    }

    private Collection<String> extractUserKeys(String bodyContent) {
        HashSet<String> userIds = new HashSet<String>();
        if (bodyContent != null) {
            try {
                XMLStreamReader streamReader = this.factory.createXMLStreamReader(new StringReader(bodyContent));
                while (streamReader.hasNext()) {
                    streamReader.next();
                    if (streamReader.getEventType() != 1 || !USER_TAG.equals(streamReader.getLocalName())) continue;
                    for (int i = 0; i < streamReader.getAttributeCount(); ++i) {
                        if (!USERKEY_ATTR.equals(streamReader.getAttributeName(i).getLocalPart())) continue;
                        userIds.add(streamReader.getAttributeValue(i));
                    }
                }
            }
            catch (XMLStreamException e) {
                log.debug("error while parsing: [{}(...)], {}", (Object)bodyContent.substring(0, Math.min(100, bodyContent.length())), (Object)e.getMessage());
            }
        }
        return userIds;
    }
}

