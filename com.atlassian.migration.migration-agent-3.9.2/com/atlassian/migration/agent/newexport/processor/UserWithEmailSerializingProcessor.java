/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 *  org.supercsv.io.CsvListWriter
 */
package com.atlassian.migration.agent.newexport.processor;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.processor.CsvSerializingProcessor;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import com.atlassian.migration.agent.service.UserMappingsManager;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.supercsv.io.CsvListWriter;

public class UserWithEmailSerializingProcessor
implements RowProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserWithEmailSerializingProcessor.class);
    private final UserMappingsManager userMappingsManager;
    private final CsvListWriter csvListWriter;
    public static final String USER_KEY = "user_key";
    public static final String USERNAME = "username";
    public static final String LOWER_USERNAME = "lower_username";
    public static final String EMAIL = "email";
    public static final String AAID = "aaid";
    private final CsvSerializingProcessor delegateProcessor;

    public UserWithEmailSerializingProcessor(RowProcessor processor, UserMappingsManager userMappingsManager) {
        this.userMappingsManager = userMappingsManager;
        this.delegateProcessor = (CsvSerializingProcessor)processor;
        this.csvListWriter = this.delegateProcessor.getWriter();
    }

    @Override
    public void initialise(ResultSet rs, Query query) {
        try {
            this.csvListWriter.write(Arrays.asList(USER_KEY, USERNAME, LOWER_USERNAME, EMAIL, AAID));
            this.csvListWriter.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(ResultSet rs) {
        try {
            String userKey = rs.getString(USER_KEY);
            String userName = rs.getString(USERNAME);
            String lower = rs.getString(LOWER_USERNAME);
            String email = IdentityAcceptedEmailValidator.cleanse((String)rs.getString(EMAIL));
            if (StringUtils.isEmpty((Object)email)) {
                email = userKey;
            }
            String aaId = this.userMappingsManager.getAaid(userKey, email, null);
            List<String> columValues = Arrays.asList(userKey, userName, lower, email, aaId);
            this.csvListWriter.write(columValues);
            this.delegateProcessor.addRowCount();
            this.delegateProcessor.addContentCharacters(columValues);
            this.csvListWriter.flush();
        }
        catch (IOException | SQLException e) {
            log.error("Error while serializing data: ", (Throwable)e);
        }
    }
}

