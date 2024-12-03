/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.CommandInfo
 *  javax.activation.CommandMap
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package org.apache.axiom.util.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axiom.util.activation.EmptyDataSource;

public class DataHandlerWrapper
extends DataHandler {
    private final DataHandler parent;

    public DataHandlerWrapper(DataHandler parent) {
        super((DataSource)EmptyDataSource.INSTANCE);
        this.parent = parent;
    }

    public CommandInfo[] getAllCommands() {
        return this.parent.getAllCommands();
    }

    public Object getBean(CommandInfo cmdinfo) {
        return this.parent.getBean(cmdinfo);
    }

    public CommandInfo getCommand(String cmdName) {
        return this.parent.getCommand(cmdName);
    }

    public Object getContent() throws IOException {
        return this.parent.getContent();
    }

    public String getContentType() {
        return this.parent.getContentType();
    }

    public DataSource getDataSource() {
        return this.parent.getDataSource();
    }

    public InputStream getInputStream() throws IOException {
        return this.parent.getInputStream();
    }

    public String getName() {
        return this.parent.getName();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.parent.getOutputStream();
    }

    public CommandInfo[] getPreferredCommands() {
        return this.parent.getPreferredCommands();
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this.parent.getTransferData(flavor);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return this.parent.getTransferDataFlavors();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return this.parent.isDataFlavorSupported(flavor);
    }

    public void setCommandMap(CommandMap commandMap) {
        this.parent.setCommandMap(commandMap);
    }

    public void writeTo(OutputStream os) throws IOException {
        this.parent.writeTo(os);
    }
}

