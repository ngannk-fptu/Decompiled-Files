/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.hash;

import com.atlassian.analytics.client.hash.AnalyticsEmailHasher;
import com.atlassian.analytics.client.hash.EmailCleaner;
import com.atlassian.analytics.client.hash.reader.HashingInstructionsReader;
import com.atlassian.analytics.client.hash.reader.RemoteHashingInstructionsReader;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcryptAnalyticsEmailHasher
implements AnalyticsEmailHasher {
    private static final Logger LOG = LoggerFactory.getLogger(BcryptAnalyticsEmailHasher.class);
    private static final String SALT_ID = "uid.onewayhash";
    private final ResettableLazyReference<HashingInstructions> processedInstructions;
    private final AnalyticsS3Client analyticsS3Client;

    public BcryptAnalyticsEmailHasher(AnalyticsS3Client analyticsS3Client) {
        this.processedInstructions = new HashingInstructionsResettableLazyReference(new RemoteHashingInstructionsReader(analyticsS3Client));
        this.analyticsS3Client = Objects.requireNonNull(analyticsS3Client);
    }

    @VisibleForTesting
    BcryptAnalyticsEmailHasher(HashingInstructionsReader reader, AnalyticsS3Client analyticsS3Client) {
        this.processedInstructions = new HashingInstructionsResettableLazyReference(reader);
        this.analyticsS3Client = Objects.requireNonNull(analyticsS3Client);
    }

    @Override
    public String hash(String email) {
        return this.hashInternal(EmailCleaner.cleanEmail(email));
    }

    @VisibleForTesting
    String hashInternal(String text) {
        if (text == null) {
            return null;
        }
        HashingInstructions hashingInstructions = (HashingInstructions)this.processedInstructions.get();
        if (StringUtils.isBlank((CharSequence)hashingInstructions.getInstructionString()) || hashingInstructions.getProcessedInstructions().isEmpty()) {
            LOG.debug("No instructions for hashing could be found. Returning null.");
            return null;
        }
        String workingResult = text;
        StringBuilder encodingsUsed = new StringBuilder();
        String previousSalt = null;
        for (String instruction : hashingInstructions.getProcessedInstructions()) {
            String fullresult;
            int saltStart = instruction.lastIndexOf(36);
            if (saltStart < 0 || saltStart == instruction.length()) {
                LOG.warn("unable to find salt for instruction '{}'. returning empty string", (Object)instruction);
                return null;
            }
            String salt = instruction.substring(instruction.lastIndexOf(36) + 1);
            String algorithm = instruction.substring(0, instruction.lastIndexOf(36));
            LOG.debug("hash salt '{}'", (Object)salt);
            if (StringUtils.isBlank((CharSequence)salt) && !StringUtils.isBlank(previousSalt)) {
                LOG.info("Salt undefined for instruction '{}'. using previous salt '{}'", (Object)instruction, previousSalt);
                salt = previousSalt;
            } else {
                if (StringUtils.isBlank((CharSequence)salt)) {
                    LOG.warn("Salt is undefined for instruction '{}' and there is no previous salt. returning empty string", (Object)instruction);
                    return null;
                }
                if (salt.length() != 22) {
                    LOG.warn("Salt is wrong length short '{}'. returning empty string", (Object)salt);
                    return null;
                }
            }
            previousSalt = salt;
            if (StringUtils.isBlank((CharSequence)salt)) {
                LOG.warn("hash salt is empty for instruction '{}'. returning empty string", (Object)instruction);
                return null;
            }
            LOG.debug("algorithm '{}'", (Object)algorithm);
            String fixedalgorithm = algorithm.replace("b", "a");
            LOG.debug("fixedalgorithm '{}'", (Object)fixedalgorithm);
            String tempSalt = fixedalgorithm + "$" + salt;
            LOG.debug("Encoding '{}' with '{}'", (Object)workingResult, (Object)tempSalt);
            try {
                fullresult = BCrypt.hashpw(workingResult, tempSalt);
            }
            catch (Exception e) {
                LOG.warn("hash failed for instruction '{}' and source '{}'. returning empty string", (Object)instruction, (Object)workingResult);
                return null;
            }
            if (LOG.isDebugEnabled()) {
                encodingsUsed.append(algorithm);
                LOG.debug("Encodings so far '{}'", (Object)encodingsUsed);
            }
            workingResult = fullresult.substring(tempSalt.length());
            LOG.debug("Got new result '{}'", (Object)workingResult);
        }
        if (text.equals(workingResult)) {
            LOG.error("hash equals source. returning null");
            return null;
        }
        return hashingInstructions.getInstructionString() + workingResult;
    }

    @Override
    public void readRemoteInstructions() {
        this.processedInstructions.reset();
        this.processedInstructions.get();
    }

    private static class HashingInstructionsResettableLazyReference
    extends ResettableLazyReference<HashingInstructions> {
        private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("\\$2[ab]{1}\\$[0-9]+\\$[^$]{0,22}");
        private final HashingInstructionsReader instructionsReader;

        public HashingInstructionsResettableLazyReference(HashingInstructionsReader instructionsReader) {
            this.instructionsReader = instructionsReader;
        }

        protected HashingInstructions create() {
            Optional<String> remoteInstructions = this.instructionsReader.readInstructions(BcryptAnalyticsEmailHasher.SALT_ID);
            if (!remoteInstructions.isPresent()) {
                LOG.warn("No instructions for hashing could be found.");
                return new HashingInstructions(remoteInstructions.orElse(""), (List<String>)ImmutableList.of());
            }
            ArrayList instructionList = Lists.newArrayList();
            Matcher matcher = INSTRUCTION_PATTERN.matcher(remoteInstructions.get());
            while (matcher.find()) {
                instructionList.add(matcher.group());
            }
            if (instructionList.isEmpty()) {
                LOG.warn("unable to find instructions from input '{}'.", (Object)remoteInstructions.get());
            }
            Collections.reverse(instructionList);
            return new HashingInstructions(remoteInstructions.orElse(""), (List<String>)ImmutableList.copyOf((Collection)instructionList));
        }
    }

    static final class HashingInstructions {
        private final String instructionString;
        private final List<String> processedInstructions;

        public HashingInstructions(String instructionString, List<String> processedInstructions) {
            this.instructionString = instructionString;
            this.processedInstructions = processedInstructions;
        }

        public String getInstructionString() {
            return this.instructionString;
        }

        public List<String> getProcessedInstructions() {
            return this.processedInstructions;
        }
    }
}

