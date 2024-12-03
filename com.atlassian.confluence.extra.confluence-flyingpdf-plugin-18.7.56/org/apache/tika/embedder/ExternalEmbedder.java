/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.embedder;

import java.io.ByteArrayOutputStream;
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
import org.apache.tika.embedder.Embedder;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;

public class ExternalEmbedder
implements Embedder {
    private static final long serialVersionUID = -2828829275642475697L;
    public static final String METADATA_COMMAND_ARGUMENTS_TOKEN = "${METADATA}";
    public static final String METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN = "${METADATA_SERIALIZED}";
    private Set<MediaType> supportedEmbedTypes = Collections.emptySet();
    private Map<Property, String[]> metadataCommandArguments = null;
    private String[] command = new String[]{"sed", "-e", "$a\\\n${METADATA_SERIALIZED}", "${INPUT}"};
    private String commandAssignmentOperator = "=";
    private String commandAssignmentDelimeter = ", ";
    private String commandAppendOperator = "=";
    private boolean quoteAssignmentValues = false;
    private TemporaryResources tmp = new TemporaryResources();

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

    protected static String serializeMetadata(List<String> metadataCommandArguments) {
        if (metadataCommandArguments != null) {
            return Arrays.toString(metadataCommandArguments.toArray());
        }
        return "";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    @Override
    public void embed(Metadata metadata, InputStream inputStream, OutputStream outputStream, ParseContext context) throws IOException, TikaException {
        block34: {
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
            List<String> origCmd = Arrays.asList(this.command);
            ArrayList<Object> cmd = new ArrayList<Object>();
            for (String string : origCmd) {
                void var16_17;
                void var16_20;
                if (string.indexOf("${INPUT}") != -1) {
                    String string2 = string.replace("${INPUT}", tikaInputStream.getFile().toString());
                    inputToStdIn = false;
                }
                if (var16_20.indexOf("${OUTPUT}") != -1) {
                    tempOutputFile = this.tmp.createTemporaryFile();
                    String string3 = var16_20.replace("${OUTPUT}", tempOutputFile.toString());
                    outputFromStdOut = false;
                }
                if (var16_17.indexOf(METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN) != -1) {
                    serializeMetadataCommandArgumentsToken = true;
                }
                if (var16_17.indexOf(METADATA_COMMAND_ARGUMENTS_TOKEN) != -1) {
                    if (hasMetadataCommandArguments) {
                        for (String commandMetadataSegment : commandMetadataSegments) {
                            cmd.add(commandMetadataSegment);
                        }
                    }
                    replacedMetadataCommandArgumentsToken = true;
                    continue;
                }
                cmd.add(var16_17);
            }
            if (hasMetadataCommandArguments) {
                if (serializeMetadataCommandArgumentsToken) {
                    int i = 0;
                    for (String string : cmd) {
                        if (string.indexOf(METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN) != -1) {
                            String string4 = string.replace(METADATA_COMMAND_ARGUMENTS_SERIALIZED_TOKEN, ExternalEmbedder.serializeMetadata(commandMetadataSegments));
                            cmd.set(i, string4);
                        }
                        ++i;
                    }
                } else if (!replacedMetadataCommandArgumentsToken && !serializeMetadataCommandArgumentsToken) {
                    cmd.addAll(commandMetadataSegments);
                }
            }
            Process process = cmd.toArray().length == 1 ? Runtime.getRuntime().exec(cmd.toArray(new String[0])[0]) : Runtime.getRuntime().exec(cmd.toArray(new String[0]));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                this.sendStdErrToOutputStream(process, byteArrayOutputStream);
                if (inputToStdIn) {
                    this.sendInputStreamToStdIn(inputStream, process);
                } else {
                    process.getOutputStream().close();
                }
                if (outputFromStdOut) {
                    this.sendStdOutToOutputStream(process, outputStream);
                    break block34;
                }
                this.tmp.dispose();
                try {
                    process.waitFor();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                TikaInputStream tikaInputStream2 = TikaInputStream.get(tempOutputFile);
                IOUtils.copy((InputStream)tikaInputStream2, outputStream);
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
                    IOUtils.closeQuietly(tikaInputStream);
                }
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(byteArrayOutputStream);
                if (process.exitValue() != 0) {
                    throw new TikaException("There was an error executing the command line\nExecutable Command:\n\n" + cmd + "\nExecutable Error:\n\n" + byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()));
                }
            }
        }
    }

    private void multiThreadedStreamCopy(final InputStream inputStream, final OutputStream outputStream) {
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    IOUtils.copy(inputStream, outputStream);
                }
                catch (IOException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }).start();
    }

    private void sendInputStreamToStdIn(InputStream inputStream, Process process) {
        this.multiThreadedStreamCopy(inputStream, process.getOutputStream());
    }

    private void sendStdOutToOutputStream(Process process, OutputStream outputStream) {
        try {
            IOUtils.copy(process.getInputStream(), outputStream);
        }
        catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void sendStdErrToOutputStream(Process process, OutputStream outputStream) {
        this.multiThreadedStreamCopy(process.getErrorStream(), outputStream);
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
        catch (IOException e) {
            return false;
        }
        catch (InterruptedException ie) {
            return false;
        }
    }
}

