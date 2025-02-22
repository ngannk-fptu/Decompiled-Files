/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase;
import ch.qos.logback.core.rolling.helper.ArchiveRemover;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.InvocationGate;
import ch.qos.logback.core.util.SimpleInvocationGate;
import java.io.File;
import java.time.Instant;

@NoAutoStart
public class SizeAndTimeBasedFNATP<E>
extends TimeBasedFileNamingAndTriggeringPolicyBase<E> {
    volatile int currentPeriodsCounter = 0;
    FileSize maxFileSize;
    Duration checkIncrement = null;
    static String MISSING_INT_TOKEN = "Missing integer token, that is %i, in FileNamePattern [";
    static String MISSING_DATE_TOKEN = "Missing date token, that is %d, in FileNamePattern [";
    private final Usage usage;
    InvocationGate invocationGate = new SimpleInvocationGate();

    public SizeAndTimeBasedFNATP() {
        this(Usage.DIRECT);
    }

    public SizeAndTimeBasedFNATP(Usage usage) {
        this.usage = usage;
    }

    @Override
    public void start() {
        super.start();
        if (this.usage == Usage.DIRECT) {
            this.addWarn("SizeAndTimeBasedFNATP is deprecated. Use SizeAndTimeBasedRollingPolicy instead");
            this.addWarn("For more information see http://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedRollingPolicy");
        }
        if (!super.isErrorFree()) {
            return;
        }
        if (this.maxFileSize == null) {
            this.addError("maxFileSize property is mandatory.");
            this.withErrors();
        }
        if (this.checkIncrement != null) {
            this.invocationGate = new SimpleInvocationGate(this.checkIncrement);
        }
        if (!this.validateDateAndIntegerTokens()) {
            this.withErrors();
            return;
        }
        this.archiveRemover = this.createArchiveRemover();
        this.archiveRemover.setContext(this.context);
        String regex = this.tbrp.fileNamePattern.toRegexForFixedDate(this.dateInCurrentPeriod);
        String stemRegex = FileFilterUtil.afterLastSlash(regex);
        this.computeCurrentPeriodsHighestCounterValue(stemRegex);
        if (this.isErrorFree()) {
            this.started = true;
        }
    }

    private boolean validateDateAndIntegerTokens() {
        boolean inError = false;
        if (this.tbrp.fileNamePattern.getIntegerTokenConverter() == null) {
            inError = true;
            this.addError(MISSING_INT_TOKEN + this.tbrp.fileNamePatternStr + "]");
            this.addError("See also http://logback.qos.ch/codes.html#sat_missing_integer_token");
        }
        if (this.tbrp.fileNamePattern.getPrimaryDateTokenConverter() == null) {
            inError = true;
            this.addError(MISSING_DATE_TOKEN + this.tbrp.fileNamePatternStr + "]");
        }
        return !inError;
    }

    protected ArchiveRemover createArchiveRemover() {
        return new SizeAndTimeBasedArchiveRemover(this.tbrp.fileNamePattern, this.rc);
    }

    void computeCurrentPeriodsHighestCounterValue(String stemRegex) {
        File file = new File(this.getCurrentPeriodsFileNameWithoutCompressionSuffix());
        File parentDir = file.getParentFile();
        File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
        if (matchingFileArray == null || matchingFileArray.length == 0) {
            this.currentPeriodsCounter = 0;
            return;
        }
        this.currentPeriodsCounter = FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
        if (this.tbrp.getParentsRawFileProperty() != null || this.tbrp.compressionMode != CompressionMode.NONE) {
            ++this.currentPeriodsCounter;
        }
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, E event) {
        long localNextCheck;
        long currentTime = this.getCurrentTime();
        if (currentTime >= (localNextCheck = this.atomicNextCheck.get())) {
            long nextCheckCandidate = this.computeNextCheck(currentTime);
            this.atomicNextCheck.set(nextCheckCandidate);
            Instant instantInElapsedPeriod = this.dateInCurrentPeriod;
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(instantInElapsedPeriod, this.currentPeriodsCounter);
            this.currentPeriodsCounter = 0;
            this.setDateInCurrentPeriod(currentTime);
            return true;
        }
        return this.checkSizeBasedTrigger(activeFile, currentTime);
    }

    private boolean checkSizeBasedTrigger(File activeFile, long currentTime) {
        if (this.invocationGate.isTooSoon(currentTime)) {
            return false;
        }
        if (activeFile == null) {
            this.addWarn("activeFile == null");
            return false;
        }
        if (this.maxFileSize == null) {
            this.addWarn("maxFileSize = null");
            return false;
        }
        if (activeFile.length() >= this.maxFileSize.getSize()) {
            this.elapsedPeriodsFileName = this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(this.dateInCurrentPeriod, this.currentPeriodsCounter);
            ++this.currentPeriodsCounter;
            return true;
        }
        return false;
    }

    public Duration getCheckIncrement() {
        return this.checkIncrement;
    }

    public void setCheckIncrement(Duration checkIncrement) {
        this.checkIncrement = checkIncrement;
    }

    @Override
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        return this.tbrp.fileNamePatternWithoutCompSuffix.convertMultipleArguments(this.dateInCurrentPeriod, this.currentPeriodsCounter);
    }

    public void setMaxFileSize(FileSize aMaxFileSize) {
        this.maxFileSize = aMaxFileSize;
    }

    static enum Usage {
        EMBEDDED,
        DIRECT;

    }
}

