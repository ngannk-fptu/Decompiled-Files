/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.multipart.FilePart
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.supercsv.cellprocessor.CellProcessorAdaptor
 *  org.supercsv.cellprocessor.Optional
 *  org.supercsv.cellprocessor.Trim
 *  org.supercsv.cellprocessor.constraint.StrMinMax
 *  org.supercsv.cellprocessor.ift.CellProcessor
 *  org.supercsv.cellprocessor.ift.StringCellProcessor
 *  org.supercsv.exception.SuperCsvCellProcessorException
 *  org.supercsv.exception.SuperCsvConstraintViolationException
 *  org.supercsv.io.CsvBeanReader
 *  org.supercsv.util.CsvContext
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import com.atlassian.migration.agent.model.CsvReadResult;
import com.atlassian.migration.agent.model.UserDomainRuleRecord;
import com.atlassian.migration.agent.service.email.CsvDomainUploadStatus;
import com.atlassian.migration.agent.service.impl.CsvReaderService;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.util.CsvContext;

public class TrustedDomainCsvReaderService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TrustedDomainCsvReaderService.class);
    private final UserDomainService userDomainService;
    private static long minCsvDomainLength = 1L;
    private static long maxCsvDomainLength = 255L;
    private static long minCsvRuleLength = 0L;
    private static long maxCsvRuleLength = 255L;
    private final CsvReaderService csvReaderService = new CsvReaderService();

    public TrustedDomainCsvReaderService(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    public CsvDomainUploadStatus processDomainsCsv(FilePart file) {
        BufferedInputStream inputStream;
        ArrayList<UserDomainRuleRecord> rules = new ArrayList<UserDomainRuleRecord>();
        ArrayList<String> errorRecords = new ArrayList<String>();
        if (!file.getContentType().equalsIgnoreCase("text/csv") || !file.getName().toLowerCase().endsWith(".csv")) {
            return CsvDomainUploadStatus.builder().result(CsvDomainUploadStatus.Result.RECORD_FAILED).errorMessage("Invalid file type").build();
        }
        try {
            inputStream = new BufferedInputStream(file.getInputStream());
        }
        catch (IOException e) {
            return CsvDomainUploadStatus.builder().result(CsvDomainUploadStatus.Result.RECORD_FAILED).errorMessage(e.getLocalizedMessage()).build();
        }
        try (CsvBeanReader reader = this.csvReaderService.getCsvBeanReaderForInputStream(inputStream);){
            CsvReadResult<UserDomainRuleRecord> result;
            String[] header = this.csvReaderService.getCsvHeader(reader);
            while ((result = this.csvReaderService.readCsvLine(reader, UserDomainRuleRecord.class, header, new CellProcessor[]{new NotBlank((CellProcessor)new Trim((StringCellProcessor)new StrMinMax(minCsvDomainLength, maxCsvDomainLength))), new Optional((CellProcessor)new Trim((StringCellProcessor)new StrMinMax(minCsvRuleLength, maxCsvRuleLength)))})) != null) {
                if (result.getErrorMessage() != null) {
                    errorRecords.add(result.getErrorMessage());
                    continue;
                }
                rules.add(result.getResult());
            }
        }
        catch (Exception e) {
            return CsvDomainUploadStatus.builder().result(CsvDomainUploadStatus.Result.RECORD_FAILED).records(rules.size()).errorMessage(e.getLocalizedMessage()).build();
        }
        CsvDomainUploadStatus status = this.validate(rules, errorRecords);
        return status.getResult() == CsvDomainUploadStatus.Result.SUCCESS ? this.recordTrustedDomainsData(rules) : status;
    }

    private CsvDomainUploadStatus validate(List<UserDomainRuleRecord> rules, List<String> errorRecords) {
        List<String> validDomains = this.getValidDomainNames();
        CsvDomainUploadStatus.CsvDomainUploadStatusBuilder uploadStatusBuilder = CsvDomainUploadStatus.builder();
        uploadStatusBuilder.records(rules.size());
        uploadStatusBuilder.result(CsvDomainUploadStatus.Result.RECORD_VALIDATION_FAILED);
        if (!errorRecords.isEmpty()) {
            String errorMessage = String.join((CharSequence)"\n", errorRecords);
            log.warn(errorMessage);
            return uploadStatusBuilder.errorMessage(errorMessage).result(CsvDomainUploadStatus.Result.RECORD_FAILED).records(errorRecords.size()).build();
        }
        if (!this.verifyFileIncludesCorrectNumberOfDomains(rules, validDomains)) {
            return uploadStatusBuilder.errorMessage("incorrect-domain-count").build();
        }
        if (!this.verifyFileIncludesOnlyValidDomains(rules, validDomains)) {
            return uploadStatusBuilder.errorMessage("invalid-domain-names").build();
        }
        if (!this.verifyFileIncludesOnlyValidRules(rules)) {
            return uploadStatusBuilder.errorMessage("invalid-rules").build();
        }
        return uploadStatusBuilder.result(CsvDomainUploadStatus.Result.SUCCESS).build();
    }

    private CsvDomainUploadStatus recordTrustedDomainsData(List<UserDomainRuleRecord> records) {
        CsvDomainUploadStatus.CsvDomainUploadStatusBuilder uploadStatusBuilder = CsvDomainUploadStatus.builder();
        uploadStatusBuilder.records(records.size());
        try {
            this.userDomainService.createAllDomainRules(records.stream().map(this::toUserDomainRuleDTO).filter(domainRule -> domainRule.getRule() != DomainRuleBehaviour.NO_DECISION_MADE).collect(Collectors.toList()));
            uploadStatusBuilder.result(CsvDomainUploadStatus.Result.SUCCESS);
        }
        catch (Exception e) {
            uploadStatusBuilder.result(CsvDomainUploadStatus.Result.RECORD_INSERT_FAILED).errorMessage(e.getLocalizedMessage());
            log.error("Failed to save trusted domains from CSV", (Throwable)e);
        }
        return uploadStatusBuilder.build();
    }

    private UserDomainRuleDto toUserDomainRuleDTO(UserDomainRuleRecord domainRuleRecord) {
        return new UserDomainRuleDto(domainRuleRecord.getDomain(), StringUtils.isBlank((CharSequence)domainRuleRecord.getDecision()) ? DomainRuleBehaviour.NO_DECISION_MADE : DomainRuleBehaviour.valueOf(domainRuleRecord.getDecision().toUpperCase()));
    }

    private List<String> getValidDomainNames() {
        List<String> storedBlockedDomains = this.userDomainService.getBlockedDomainsFromStore();
        return this.userDomainService.getUserDomainCounts().stream().map(domainCount -> domainCount.getDomainName().toLowerCase()).filter(domainName -> !storedBlockedDomains.contains(domainName)).distinct().collect(Collectors.toList());
    }

    private boolean verifyFileIncludesCorrectNumberOfDomains(List<UserDomainRuleRecord> rules, List<String> validDomains) {
        return rules.size() == validDomains.size();
    }

    private boolean verifyFileIncludesOnlyValidDomains(List<UserDomainRuleRecord> domainRuleRecords, List<String> validDomains) {
        return domainRuleRecords.stream().map(UserDomainRuleRecord::getDomain).allMatch(validDomains::contains);
    }

    private boolean verifyFileIncludesOnlyValidRules(List<UserDomainRuleRecord> domainRuleRecords) {
        List validRules = Arrays.stream(DomainRuleBehaviour.values()).map(Enum::name).filter(name -> !name.equals(DomainRuleBehaviour.BLOCKED.name())).collect(Collectors.toList());
        validRules.add("");
        return domainRuleRecords.stream().map(domainRecord -> domainRecord.getDecision().toUpperCase()).allMatch(validRules::contains);
    }

    private static class CsvDomainRecordResult {
        String errorMessage;
        UserDomainRuleRecord userDomainRule;

        private CsvDomainRecordResult() {
        }
    }

    private class NotBlank
    extends CellProcessorAdaptor {
        public NotBlank(CellProcessor next2) {
            super(next2);
        }

        public Object execute(Object value, CsvContext context) {
            if (value instanceof String && StringUtils.isBlank((CharSequence)((String)value))) {
                throw new SuperCsvConstraintViolationException("The Domain name should not be blank", context, (CellProcessor)this);
            }
            if (value instanceof String) {
                return this.next.execute((Object)((String)value).toLowerCase(), context);
            }
            throw new SuperCsvCellProcessorException(String.format("Could not parse '%s' as a domain name", value), context, (CellProcessor)this);
        }
    }
}

