/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jcifs.Config
 *  jcifs.smb.NtlmPasswordAuthentication
 *  jcifs.smb.SmbNamedPipe
 */
package net.sourceforge.jtds.jdbc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import jcifs.Config;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbNamedPipe;
import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.SharedSocket;
import net.sourceforge.jtds.jdbc.Support;

public class SharedNamedPipe
extends SharedSocket {
    private SmbNamedPipe pipe;

    public SharedNamedPipe(JtdsConnection connection) throws IOException {
        super(connection.getBufferDir(), connection.getTdsVersion(), connection.getServerType());
        int timeout = connection.getSocketTimeout() * 1000;
        String val = String.valueOf(timeout > 0 ? timeout : Integer.MAX_VALUE);
        Config.setProperty((String)"jcifs.smb.client.responseTimeout", (String)val);
        Config.setProperty((String)"jcifs.smb.client.soTimeout", (String)val);
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(connection.getDomainName(), connection.getUser(), connection.getPassword());
        StringBuilder url = new StringBuilder(32);
        url.append("smb://");
        url.append(connection.getServerName());
        url.append("/IPC$");
        String instanceName = connection.getInstanceName();
        if (instanceName != null && instanceName.length() != 0) {
            url.append("/MSSQL$");
            url.append(instanceName);
        }
        String namedPipePath = DefaultProperties.getNamedPipePath(connection.getServerType());
        url.append(namedPipePath);
        this.setPipe(new SmbNamedPipe(url.toString(), 3, auth));
        this.setOut(new DataOutputStream(this.getPipe().getNamedPipeOutputStream()));
        int bufferSize = Support.calculateNamedPipeBufferSize(connection.getTdsVersion(), connection.getPacketSize());
        this.setIn(new DataInputStream(new BufferedInputStream(this.getPipe().getNamedPipeInputStream(), bufferSize)));
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
        return this.getPipe() != null;
    }

    @Override
    void close() throws IOException {
        super.close();
        this.getOut().close();
        this.getIn().close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void forceClose() {
        try {
            this.getOut().close();
        }
        catch (IOException e) {
        }
        finally {
            this.setOut(null);
        }
        try {
            this.getIn().close();
        }
        catch (IOException iOException) {
        }
        finally {
            this.setIn(null);
        }
        this.setPipe(null);
    }

    private SmbNamedPipe getPipe() {
        return this.pipe;
    }

    private void setPipe(SmbNamedPipe pipe) {
        this.pipe = pipe;
    }

    @Override
    protected void setTimeout(int timeout) {
    }
}

