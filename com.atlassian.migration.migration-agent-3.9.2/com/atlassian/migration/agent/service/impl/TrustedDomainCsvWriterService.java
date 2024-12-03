/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.dto.UserDomainCountDto;
import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import com.atlassian.migration.agent.service.impl.BlockedDomainService;
import com.atlassian.migration.agent.service.impl.CsvWriterService;
import com.atlassian.migration.agent.service.impl.DefaultExportDirManager;
import com.atlassian.migration.agent.service.impl.TrustedDomainsCsvZipException;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import com.atlassian.migration.agent.service.util.ZipService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrustedDomainCsvWriterService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TrustedDomainCsvWriterService.class);
    private final UserDomainService userDomainService;
    private final DefaultExportDirManager defaultExportDirManager;
    private final BlockedDomainService blockedDomainService;
    private final CsvWriterService csvWriterService;
    public static final String CSV_FILENAME = "email-domains-";

    public TrustedDomainCsvWriterService(UserDomainService userDomainService, DefaultExportDirManager defaultExportDirManager, BlockedDomainService blockedDomainService, CsvWriterService csvWriterService) {
        this.userDomainService = userDomainService;
        this.defaultExportDirManager = defaultExportDirManager;
        this.blockedDomainService = blockedDomainService;
        this.csvWriterService = csvWriterService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeDomainsCsvZip(OutputStream outputStream, String cloudId, long filenameTimestamp) throws TrustedDomainsCsvZipException {
        File csvFile = null;
        try {
            csvFile = this.createEmptyDomainsCsv(filenameTimestamp);
            List<UserDomainRuleDto> domainEntries = this.getDomainNameCsvEntries(cloudId);
            Collections.sort(domainEntries);
            this.writeDomainsCsv(csvFile, domainEntries);
            this.writeDomainsZip(csvFile, outputStream);
        }
        finally {
            this.csvWriterService.deleteCsvFile(csvFile);
        }
    }

    private File createEmptyDomainsCsv(long filenameTimestamp) throws TrustedDomainsCsvZipException {
        try {
            return this.csvWriterService.createEmptyCsvFile(this.defaultExportDirManager.getMigrationDomainsDirectory(), CSV_FILENAME + filenameTimestamp);
        }
        catch (Exception e) {
            log.error("Error creating CSV file: " + e.getMessage(), (Throwable)e);
            throw new TrustedDomainsCsvZipException(TrustedDomainsCsvZipException.Type.COULD_NOT_CREATE_CSV, (Throwable)e);
        }
    }

    private List<UserDomainRuleDto> getDomainNameCsvEntries(String cloudId) {
        Map<String, DomainRuleBehaviour> domainsToDecisionMap = this.userDomainService.getDomainRules().getDomains().stream().collect(Collectors.toMap(UserDomainRuleDto::getDomainName, UserDomainRuleDto::getRule));
        List<UserDomainRuleDto> allDomainRules = this.userDomainService.getUserDomainCounts().stream().map(UserDomainCountDto::getDomainName).map(domain -> new UserDomainRuleDto((String)domain, domainsToDecisionMap.getOrDefault(domain, DomainRuleBehaviour.NO_DECISION_MADE))).collect(Collectors.toList());
        return this.excludeBlockedDomains(allDomainRules, cloudId);
    }

    private List<UserDomainRuleDto> excludeBlockedDomains(List<UserDomainRuleDto> list, String cloudId) {
        List<String> blockedDomains = this.blockedDomainService.getBlockedDomains(cloudId);
        return list.stream().filter(domain -> !blockedDomains.contains(domain.getDomainName())).collect(Collectors.toList());
    }

    /*
     * Exception decompiling
     */
    private void writeDomainsCsv(File csvFile, List<UserDomainRuleDto> domainEntries) throws TrustedDomainsCsvZipException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.UnsupportedOperationException
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.NewAnonymousArray.getDimSize(NewAnonymousArray.java:142)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.isNewArrayLambda(LambdaRewriter.java:455)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:409)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:167)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:105)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.rewriteExpressions(StructuredExpressionStatement.java:70)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private void writeDomainsZip(File csvFile, OutputStream outputStream) throws TrustedDomainsCsvZipException {
        String readmePath = "/domains-readme.txt";
        ZipService zipService = this.getZipService(csvFile);
        try (ZipOutputStream zipOutputStream = zipService.createZip(outputStream);
             InputStream readmeInputStream = TrustedDomainCsvWriterService.class.getResourceAsStream("/domains-readme.txt");){
            zipService.addFileToZip(zipOutputStream, csvFile.getName(), csvFile);
            zipService.addToZip(zipOutputStream, "/domains-readme.txt", readmeInputStream);
            log.debug("Successfully written readme and domains csv to zip file.");
        }
        catch (Exception e) {
            log.error("Error writing to zip file: " + e.getMessage(), (Throwable)e);
            throw new TrustedDomainsCsvZipException(TrustedDomainsCsvZipException.Type.COULD_NOT_WRITE_ZIP, (Throwable)e);
        }
    }

    protected ZipService getZipService(File file) {
        try {
            return new ZipService(new FileInputStream(file));
        }
        catch (Exception e) {
            log.error("Error creating zip file: " + e.getMessage(), (Throwable)e);
            throw new TrustedDomainsCsvZipException(TrustedDomainsCsvZipException.Type.COULD_NOT_WRITE_ZIP, (Throwable)e);
        }
    }
}

