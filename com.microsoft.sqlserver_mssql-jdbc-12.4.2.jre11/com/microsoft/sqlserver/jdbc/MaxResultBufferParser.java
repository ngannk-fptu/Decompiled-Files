/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.StringUtils;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaxResultBufferParser {
    private static final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.MaxResultBufferParser");
    private static final String[] PERCENT_PHRASES = new String[]{"percent", "pct", "p"};
    private static final String ERROR_MESSAGE = "MaxResultBuffer property is badly formatted: {0}.";

    private MaxResultBufferParser() {
    }

    public static long validateMaxResultBuffer(String input) throws SQLServerException {
        long number = -1L;
        if (StringUtils.isEmpty(input) || input.equals("-1")) {
            return number;
        }
        if (!StringUtils.isEmpty(input) && input.matches("-?\\d+(\\.\\d+)?")) {
            try {
                number = Long.parseLong(input);
            }
            catch (NumberFormatException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, ERROR_MESSAGE, new Object[]{input});
                }
                MaxResultBufferParser.throwNewInvalidMaxResultBufferParameterException(e, input);
            }
            return MaxResultBufferParser.adjustMemory(number, 1L);
        }
        for (String percentPhrase : PERCENT_PHRASES) {
            if (!input.endsWith(percentPhrase)) continue;
            String numberString = input.substring(0, input.length() - percentPhrase.length());
            try {
                number = Long.parseLong(numberString);
            }
            catch (NumberFormatException e) {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, ERROR_MESSAGE, new Object[]{input});
                }
                MaxResultBufferParser.throwNewInvalidMaxResultBufferParameterException(e, numberString);
            }
            return MaxResultBufferParser.adjustMemoryPercentage(number);
        }
        long multiplier = MaxResultBufferParser.getMultiplier(input);
        String numberString = input.substring(0, input.length() - 1);
        try {
            number = Long.parseLong(numberString);
        }
        catch (NumberFormatException e) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, ERROR_MESSAGE, new Object[]{input});
            }
            MaxResultBufferParser.throwNewInvalidMaxResultBufferParameterException(e, numberString);
        }
        return MaxResultBufferParser.adjustMemory(number, multiplier);
    }

    private static void checkForNegativeValue(long value) throws SQLServerException {
        if (value <= 0L) {
            Object[] objectToThrow = new Object[]{value};
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_maxResultBufferNegativeParameterValue"));
            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, SQLServerException.getErrString("R_maxResultBufferNegativeParameterValue"), objectToThrow);
            }
            throw new SQLServerException(form.format(objectToThrow), new Throwable());
        }
    }

    private static long getMultiplier(String input) throws SQLServerException {
        long multiplier = 1L;
        switch (Character.toUpperCase(input.charAt(input.length() - 1))) {
            case 'K': {
                multiplier = 1000L;
                break;
            }
            case 'M': {
                multiplier = 1000000L;
                break;
            }
            case 'G': {
                multiplier = 1000000000L;
                break;
            }
            case 'T': {
                multiplier = 1000000000000L;
                break;
            }
            default: {
                if (logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE, ERROR_MESSAGE, new Object[]{input});
                }
                MaxResultBufferParser.throwNewInvalidMaxResultBufferParameterException(null, input);
            }
        }
        return multiplier;
    }

    private static long adjustMemoryPercentage(long percentage) throws SQLServerException {
        MaxResultBufferParser.checkForNegativeValue(percentage);
        if (percentage > 90L) {
            return (long)(0.9 * (double)MaxResultBufferParser.getMaxMemory());
        }
        return (long)((double)percentage / 100.0 * (double)MaxResultBufferParser.getMaxMemory());
    }

    private static long adjustMemory(long size, long multiplier) throws SQLServerException {
        MaxResultBufferParser.checkForNegativeValue(size);
        if ((double)(size * multiplier) > 0.9 * (double)MaxResultBufferParser.getMaxMemory()) {
            return (long)(0.9 * (double)MaxResultBufferParser.getMaxMemory());
        }
        return size * multiplier;
    }

    private static long getMaxMemory() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
    }

    private static void throwNewInvalidMaxResultBufferParameterException(Throwable cause, Object ... arguments) throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_maxResultBufferInvalidSyntax"));
        throw new SQLServerException(form.format(arguments), cause);
    }
}

