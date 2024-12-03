/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.SharedSocket;
import net.sourceforge.jtds.jdbc.Support;

public class SharedLocalNamedPipe
extends SharedSocket {
    RandomAccessFile pipe;

    public SharedLocalNamedPipe(JtdsConnection connection) throws IOException {
        super(connection.getBufferDir(), connection.getTdsVersion(), connection.getServerType());
        String serverName = connection.getServerName();
        String instanceName = connection.getInstanceName();
        StringBuilder pipeName = new StringBuilder(64);
        pipeName.append("\\\\");
        if (serverName == null || serverName.length() == 0) {
            pipeName.append('.');
        } else {
            pipeName.append(serverName);
        }
        pipeName.append("\\pipe");
        if (instanceName != null && instanceName.length() != 0) {
            pipeName.append("\\MSSQL$").append(instanceName);
        }
        String namedPipePath = DefaultProperties.getNamedPipePath(connection.getServerType());
        pipeName.append(namedPipePath.replace('/', '\\'));
        this.pipe = new RandomAccessFile(pipeName.toString(), "rw");
        int bufferSize = Support.calculateNamedPipeBufferSize(connection.getTdsVersion(), connection.getPacketSize());
        this.setOut(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.pipe.getFD()), bufferSize)));
        this.setIn(new DataInputStream(new BufferedInputStream(new FileInputStream(this.pipe.getFD()), bufferSize)));
    }

    @Override
    String getMAC() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                try {
                    byte[] address;
                    if (nic.isLoopback() || nic.isVirtual() || (address = nic.getHardwareAddress()) == null) continue;
                    String mac = "";
                    for (int k = 0; k < address.length; ++k) {
                        String macValue = String.format("%02X", address[k]);
                        mac = mac + macValue;
                    }
                    return mac;
                }
                catch (SocketException socketException) {
                }
            }
        }
        catch (SocketException socketException) {
            // empty catch block
        }
        return null;
    }

    @Override
    boolean isConnected() {
        return this.pipe != null;
    }

    @Override
    byte[] sendNetPacket(SharedSocket.VirtualSocket vsock, byte[] buffer) throws IOException {
        byte[] ret = super.sendNetPacket(vsock, buffer);
        this.getOut().flush();
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void close() throws IOException {
        try {
            super.close();
            this.getOut().close();
            this.setOut(null);
            this.getIn().close();
            this.setIn(null);
            if (this.pipe != null) {
                this.pipe.close();
            }
        }
        finally {
            this.pipe = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void forceClose() {
        try {
            this.getOut().close();
        }
        catch (Exception e) {
        }
        finally {
            this.setOut(null);
        }
        try {
            this.getIn().close();
        }
        catch (Exception e) {
        }
        finally {
            this.setIn(null);
        }
        try {
            if (this.pipe != null) {
                this.pipe.close();
            }
        }
        catch (IOException iOException) {
        }
        finally {
            this.pipe = null;
        }
    }

    @Override
    protected void setTimeout(int timeout) {
    }
}

