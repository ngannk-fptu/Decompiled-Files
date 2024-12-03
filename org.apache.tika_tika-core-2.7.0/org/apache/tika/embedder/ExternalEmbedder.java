/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.tika.embedder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.tika.embedder.Embedder;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;

public class ExternalEmbedder
implements Embedder {
    public static final String METADATA_COMMAND_ARGUMENTS_TOKEN = "${METADATA}";
    public static final String METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN = "${METADATA_SERIALIZED}";
    private static final long serialVersionUID = -2828829275642475697L;
    private final TemporaryResources tmp = new TemporaryResources();
    private Set<MediaType> supportedEmbedTypes = Collections.emptySet();
    private Map<Property, String[]> metadataCommandArguments = null;
    private String[] command = new String[]{"sed", "-e", "$a\\\n${METADATA_SERIALIZED}", "${INPUT}"};
    private String commandAssignmentOperator = "=";
    private String commandAssignmentDelimeter = ", ";
    private String commandAppendOperator = "=";
    private boolean quoteAssignmentValues = false;

    protected static String serializeMetadata(List<String> metadataCommandArguments) {
        if (metadataCommandArguments != null) {
            return Arrays.toString(metadataCommandArguments.toArray());
        }
        return "";
    }

    public static boolean check(String checkCmd, int ... errorValue) {
        return ExternalEmbedder.check(new String[]{checkCmd}, errorValue);
    }

    public static boolean check(String[] checkCmd, int ... errorValue) {
        if (errorValue.length == 0) {
            errorValue = new int[]{127};
        }
        try {
            Process process = checkCmd.length == 1 ? Runtime.getRuntime().exec(checkCmd[0]) : Runtime.getRuntime().exec(checkCmd);
            int result = process.waitFor();
            for (int err : errorValue) {
                if (result != err) continue;
                return false;
            }
            return true;
        }
        catch (IOException | InterruptedException e) {
            return false;
        }
    }

    @Override
    public Set<MediaType> getSupportedEmbedTypes(ParseContext context) {
        return this.getSupportedEmbedTypes();
    }

    public Set<MediaType> getSupportedEmbedTypes() {
        return this.supportedEmbedTypes;
    }

    public void setSupportedEmbedTypes(Set<MediaType> supportedEmbedTypes) {
        this.supportedEmbedTypes = Collections.unmodifiableSet(new HashSet<MediaType>(supportedEmbedTypes));
    }

    public String[] getCommand() {
        return this.command;
    }

    public void setCommand(String ... command) {
        this.command = command;
    }

    public String getCommandAssignmentOperator() {
        return this.commandAssignmentOperator;
    }

    public void setCommandAssignmentOperator(String commandAssignmentOperator) {
        this.commandAssignmentOperator = commandAssignmentOperator;
    }

    public String getCommandAssignmentDelimeter() {
        return this.commandAssignmentDelimeter;
    }

    public void setCommandAssignmentDelimeter(String commandAssignmentDelimeter) {
        this.commandAssignmentDelimeter = commandAssignmentDelimeter;
    }

    public String getCommandAppendOperator() {
        return this.commandAppendOperator;
    }

    public void setCommandAppendOperator(String commandAppendOperator) {
        this.commandAppendOperator = commandAppendOperator;
    }

    public boolean isQuoteAssignmentValues() {
        return this.quoteAssignmentValues;
    }

    public void setQuoteAssignmentValues(boolean quoteAssignmentValues) {
        this.quoteAssignmentValues = quoteAssignmentValues;
    }

    public Map<Property, String[]> getMetadataCommandArguments() {
        return this.metadataCommandArguments;
    }

    public void setMetadataCommandArguments(Map<Property, String[]> arguments) {
        this.metadataCommandArguments = arguments;
    }

    protected List<String> getCommandMetadataSegments(Metadata metadata) {
        ArrayList<String> commandMetadataSegments = new ArrayList<String>();
        if (metadata == null || metadata.names() == null) {
            return commandMetadataSegments;
        }
        for (String metadataName : metadata.names()) {
            for (Property property : this.getMetadataCommandArguments().keySet()) {
                String[] metadataCommandArguments;
                if (!metadataName.equals(property.getName()) || (metadataCommandArguments = this.getMetadataCommandArguments().get(property)) == null) continue;
                for (String metadataCommandArgument : metadataCommandArguments) {
                    if (metadata.isMultiValued(metadataName)) {
                        String[] stringArray = metadata.getValues(metadataName);
                        int n = stringArray.length;
                        for (int i = 0; i < n; ++i) {
                            String metadataValue;
                            String assignmentValue = metadataValue = stringArray[i];
                            if (this.quoteAssignmentValues) {
                                assignmentValue = "'" + assignmentValue + "'";
                            }
                            commandMetadataSegments.add(metadataCommandArgument + this.commandAppendOperator + assignmentValue);
                        }
                        continue;
                    }
                    String assignmentValue = metadata.get(metadataName);
                    if (this.quoteAssignmentValues) {
                        assignmentValue = "'" + assignmentValue + "'";
                    }
                    commandMetadataSegments.add(metadataCommandArgument + this.commandAssignmentOperator + assignmentValue);
                }
            }
        }
        return commandMetadataSegments;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void embed(Metadata metadata, InputStream inputStream, OutputStream outputStream, ParseContext context) throws IOException, TikaException {
        block33: {
            boolean inputToStdIn = true;
            boolean outputFromStdOut = true;
            boolean hasMetadataCommandArguments = this.metadataCommandArguments != null && !this.metadataCommandArguments.isEmpty();
            boolean serializeMetadataCommandArgumentsToken = false;
            boolean replacedMetadataCommandArgumentsToken = false;
            TikaInputStream tikaInputStream = TikaInputStream.get(inputStream);
            File tempOutputFile = null;
            List<String> commandMetadataSegments = null;
            if (hasMetadataCommandArguments) {
                commandMetadataSegments = this.getCommandMetadataSegments(metadata);
            }
            String[] origCmd = this.command;
            ArrayList<String> cmd = new ArrayList<String>();
            for (String commandSegment : origCmd) {
                if (commandSegment.contains("${INPUT}")) {
                    commandSegment = commandSegment.replace("${INPUT}", tikaInputStream.getFile().toString());
                    inputToStdIn = false;
                }
                if (commandSegment.contains("${OUTPUT}")) {
                    tempOutputFile = this.tmp.createTemporaryFile();
                    commandSegment = commandSegment.replace("${OUTPUT}", tempOutputFile.toString());
                    outputFromStdOut = false;
                }
                if (commandSegment.contains(METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN)) {
                    serializeMetadataCommandArgumentsToken = true;
                }
                if (commandSegment.contains(METADATA_COMMAND_ARGUMENTS_TOKEN)) {
                    if (hasMetadataCommandArguments) {
                        cmd.addAll(commandMetadataSegments);
                    }
                    replacedMetadataCommandArgumentsToken = true;
                    continue;
                }
                cmd.add(commandSegment);
            }
            if (hasMetadataCommandArguments) {
                if (serializeMetadataCommandArgumentsToken) {
                    int i = 0;
                    for (String commandSegment : cmd) {
                        if (commandSegment.contains(METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN)) {
                            commandSegment = commandSegment.replace(METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN, ExternalEmbedder.serializeMetadata(commandMetadataSegments));
                            cmd.set(i, commandSegment);
                        }
                        ++i;
                    }
                } else if (!replacedMetadataCommandArgumentsToken && !serializeMetadataCommandArgumentsToken) {
                    cmd.addAll(commandMetadataSegments);
                }
            }
            Process process = cmd.toArray().length == 1 ? Runtime.getRuntime().exec(cmd.toArray(new String[0])[0]) : Runtime.getRuntime().exec(cmd.toArray(new String[0]));
            UnsynchronizedByteArrayOutputStream stdErrOutputStream = new UnsynchronizedByteArrayOutputStream();
            try {
                this.sendStdErrToOutputStream(process, (OutputStream)stdErrOutputStream);
                if (inputToStdIn) {
                    this.sendInputStreamToStdIn(inputStream, process);
                } else {
                    process.getOutputStream().close();
                }
                if (outputFromStdOut) {
                    this.sendStdOutToOutputStream(process, outputStream);
                    break block33;
                }
                this.tmp.dispose();
                try {
                    process.waitFor();
                }
                catch (InterruptedException commandSegment) {
                    // empty catch block
                }
                TikaInputStream tempOutputFileInputStream = TikaInputStream.get(tempOutputFile);
                IOUtils.copy((InputStream)((Object)tempOutputFileInputStream), (OutputStream)outputStream);
            }
            finally {
                if (outputFromStdOut) {
                    try {
                        process.waitFor();
                    }
                    catch (InterruptedException interruptedException) {}
                } else {
                    try {
                        tempOutputFile.delete();
                    }
                    catch (Exception exception) {}
                }
                if (!inputToStdIn) {
                    IOUtils.closeQuietly((InputStream)((Object)tikaInputStream));
                }
                IOUtils.closeQuietly((OutputStream)outputStream);
                IOUtils.closeQuietly((OutputStream)stdErrOutputStream);
                if (process.exitValue() != 0) {
                    throw new TikaException("There was an error executing the command line\nExecutable Command:\n\n" + cmd + "\nExecutable Error:\n\n" + stdErrOutputStream.toString(StandardCharsets.UTF_8.name()));
                }
            }
        }
    }

    private void multiThreadedStreamCopy(InputStream inputStream, OutputStream outputStream) {
        new Thread(() -> {
            try {
                IOUtils.copy((InputStream)inputStream, (OutputStream)outputStream);
            }
            catch (IOException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }).start();
    }

    private void sendInputStreamToStdIn(InputStream inputStream, Process process) {
        this.multiThreadedStreamCopy(inputStream, process.getOutputStream());
    }

    private void sendStdOutToOutputStream(Process process, OutputStream outputStream) {
        try {
            IOUtils.copy((InputStream)process.getInputStream(), (OutputStream)outputStream);
        }
        catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void sendStdErrToOutputStream(Process process, OutputStream outputStream) {
        this.multiThreadedStreamCopy(process.getErrorStream(), outputStream);
    }
}

