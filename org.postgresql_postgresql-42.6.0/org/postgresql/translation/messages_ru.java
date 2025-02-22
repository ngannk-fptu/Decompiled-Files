/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_ru
extends ResourceBundle {
    private static final String[] table;

    @Override
    public Object handleGetObject(String msgid) throws MissingResourceException {
        String found;
        int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 269 << 1;
        String found2 = table[idx];
        if (found2 == null) {
            return null;
        }
        if (msgid.equals(found2)) {
            return table[idx + 1];
        }
        int incr = hash_val % 267 + 1 << 1;
        do {
            if ((idx += incr) >= 538) {
                idx -= 538;
            }
            if ((found = table[idx]) != null) continue;
            return null;
        } while (!msgid.equals(found));
        return table[idx + 1];
    }

    public Enumeration getKeys() {
        return new Enumeration(){
            private int idx = 0;
            {
                while (this.idx < 538 && table[this.idx] == null) {
                    this.idx += 2;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.idx < 538;
            }

            public Object nextElement() {
                String key = table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 538 && table[this.idx] == null);
                return key;
            }
        };
    }

    public ResourceBundle getParent() {
        return this.parent;
    }

    static {
        String[] t = new String[538];
        t[0] = "";
        t[1] = "Project-Id-Version: JDBC Driver for PostgreSQL 8.x.x\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2016-01-07 15:09+0300\nLast-Translator: Vladimir Sitnikov <sitnikov.vladimir@gmail.com>\nLanguage-Team: pgsql-rus <pgsql-rus@yahoogroups.com>\nLanguage: ru_RU\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Generator: Poedit 1.5.7\n";
        t[4] = "Server SQLState: {0}";
        t[5] = "SQLState \u0441\u0435\u0440\u0432\u0435\u0440\u0430: {0}";
        t[14] = "suspend/resume not implemented";
        t[15] = "\u041e\u043f\u0435\u0440\u0430\u0446\u0438\u0438 XA suspend/resume \u043d\u0435 \u0440\u0435\u0430\u043b\u0438\u0437\u043e\u0432\u0430\u043d\u044b";
        t[18] = "The array index is out of range: {0}";
        t[19] = "\u0418\u043d\u0434\u0435\u043a\u0441 \u043c\u0430\u0441\u0441\u0438\u0432\u0430 \u0432\u043d\u0435 \u0434\u0438\u0430\u043f\u0430\u0437\u043e\u043d\u0430: {0}";
        t[28] = "This PooledConnection has already been closed.";
        t[29] = "\u042d\u0442\u043e \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u0443\u0436\u0435 \u0431\u044b\u043b\u043e \u0437\u0430\u043a\u0440\u044b\u0442\u043e";
        t[30] = "Malformed function or procedure escape syntax at offset {0}.";
        t[31] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0440\u0430\u0437\u043e\u0431\u0440\u0430\u0442\u044c SQL \u043a\u043e\u043c\u0430\u043d\u0434\u0443. \u041e\u0448\u0438\u0431\u043a\u0430 \u043d\u0430 \u043f\u043e\u0437\u0438\u0446\u0438\u0438 {0}";
        t[32] = "The column index is out of range: {0}, number of columns: {1}.";
        t[33] = "\u0418\u043d\u0434\u0435\u043a\u0441 \u043a\u043e\u043b\u043e\u043d\u043a\u0438 \u0432\u043d\u0435 \u0434\u0438\u0430\u043f\u0430\u0437\u043e\u043d\u0430: {0}. \u0414\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u044f: 1..{1}";
        t[34] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[35] = "\u0420\u0430\u043d\u043d\u0435\u0435 \u0437\u0430\u0432\u0435\u0440\u0448\u0435\u043d\u0438\u0435 \u0432\u0445\u043e\u0434\u043d\u043e\u0433\u043e \u043f\u043e\u0442\u043e\u043a\u0430, \u043e\u0436\u0438\u0434\u0430\u043b\u043e\u0441\u044c \u0431\u0430\u0439\u0442: {0}, \u043d\u043e \u0441\u0447\u0438\u0442\u0430\u043d\u043e \u0442\u043e\u043b\u044c\u043a\u043e {1}";
        t[44] = "An I/O error occurred while sending to the backend.";
        t[45] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0432\u043e\u0434\u0430/\u0432\u044b\u0432\u043e\u0434\u0430 \u043f\u0440\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0435 \u0431\u044d\u043a\u0435\u043d\u0434\u0443";
        t[46] = "Prepare called before end. prepare xid={0}, state={1}";
        t[47] = "\u0412\u044b\u0437\u043e\u0432 prepare \u0434\u043e\u043b\u0436\u0435\u043d \u043f\u0440\u043e\u0438\u0441\u0445\u043e\u0434\u0438\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u043e\u0441\u043b\u0435 \u0432\u044b\u0437\u043e\u0432\u0430 end. prepare xid={0}, state={1}";
        t[48] = "Transaction isolation level {0} not supported.";
        t[49] = "\u0423\u0440\u043e\u0432\u0435\u043d\u044c \u0438\u0437\u043e\u043b\u044f\u0446\u0438\u0438 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0439 {0} \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f.";
        t[50] = "Could not find a server with specified targetServerType: {0}";
        t[51] = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043d\u0430\u0439\u0442\u0438 \u0441\u0435\u0440\u0432\u0435\u0440 \u0441 \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u044b\u043c \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435\u043c targetServerType: {0}";
        t[52] = "Conversion of interval failed";
        t[53] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0430\u0442\u044c PGInterval: {0}";
        t[54] = "The array index is out of range: {0}, number of elements: {1}.";
        t[55] = "\u0418\u043d\u0434\u0435\u043a\u0441 \u043c\u0430\u0441\u0441\u0438\u0432\u0430 \u0432\u043d\u0435 \u0434\u0438\u0430\u043f\u0430\u0437\u043e\u043d\u0430: {0}. \u0414\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u044f: 1..{1}";
        t[62] = "Unsupported value for stringtype parameter: {0}";
        t[63] = "\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0434\u043b\u044f \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u0430 stringtype: {0}";
        t[72] = "Invalid stream length {0}.";
        t[73] = "\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u0434\u043b\u0438\u043d\u0430 \u043f\u043e\u0442\u043e\u043a\u0430 {0}.";
        t[80] = "Error rolling back prepared transaction. rollback xid={0}, preparedXid={1}, currentXid={2}";
        t[81] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0442\u043a\u0430\u0442\u0435 \u043f\u043e\u0434\u0433\u043e\u0442\u043e\u0432\u043b\u0435\u043d\u043d\u043e\u0439 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0438. rollback xid={0}, preparedXid={1}, currentXid={2}";
        t[84] = "The driver currently does not support COPY operations.";
        t[85] = "\u0414\u0440\u0430\u0439\u0432\u0435\u0440 \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0442\u0435 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0438 COPY.";
        t[94] = "DataSource has been closed.";
        t[95] = "DataSource \u0437\u0430\u043a\u0440\u044b\u0442.";
        t[96] = "Cannot write to copy a byte of value {0}";
        t[97] = "\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 byte \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0432 \u0434\u0438\u0430\u043f\u0430\u0437\u043e\u043d\u0435 0..255, \u043f\u0435\u0440\u0435\u0434\u0430\u043d\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435: {0}";
        t[98] = "Fastpath call {0} - No result was returned and we expected a long.";
        t[99] = "\u0412\u044b\u0437\u043e\u0432 fastpath {0} \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u0432\u0435\u0440\u043d\u0443\u043b, \u0430 \u043e\u0436\u0438\u0434\u0430\u043b\u043e\u0441\u044c long";
        t[100] = "Connection attempt timed out.";
        t[101] = "\u0417\u0430\u043a\u043e\u043d\u0447\u0438\u043b\u043e\u0441\u044c \u0432\u0440\u0435\u043c\u044f \u043e\u0436\u0438\u0434\u0430\u043d\u0438\u044f";
        t[102] = "Detail: {0}";
        t[103] = "\u041f\u043e\u0434\u0440\u043e\u0431\u043d\u043e\u0441\u0442\u0438: {0}";
        t[104] = "Connection to {0} refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections.";
        t[105] = "\u041f\u043e\u0434\u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u043f\u043e \u0430\u0434\u0440\u0435\u0441\u0443 {0} \u043e\u0442\u043a\u043b\u043e\u043d\u0435\u043d\u043e. \u041f\u0440\u043e\u0432\u0435\u0440\u044c\u0442\u0435 \u0447\u0442\u043e \u0445\u043e\u0441\u0442 \u0438 \u043f\u043e\u0440\u0442 \u0443\u043a\u0430\u0437\u0430\u043d\u044b \u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e \u0438 \u0447\u0442\u043e postmaster \u043f\u0440\u0438\u043d\u0438\u043c\u0430\u0435\u0442 TCP/IP-\u043f\u043e\u0434\u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f.";
        t[108] = "This statement has been closed.";
        t[109] = "\u042d\u0442\u043e\u0442 statement \u0431\u044b\u043b \u0437\u0430\u043a\u0440\u044b\u0442.";
        t[110] = "Error committing prepared transaction. commit xid={0}, preparedXid={1}, currentXid={2}";
        t[111] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0444\u0438\u043a\u0441\u0430\u0446\u0438\u0438 \u043f\u043e\u0434\u0433\u043e\u0442\u043e\u0432\u043b\u0435\u043d\u043d\u043e\u0439 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0438. commit xid={0}, preparedXid={1}, currentXid={2}";
        t[114] = "Position: {0}";
        t[115] = "\u041f\u043e\u0437\u0438\u0446\u0438\u044f: {0}";
        t[116] = "Not implemented: Prepare must be issued using the same connection that started the transaction. currentXid={0}, prepare xid={1}";
        t[117] = "\u0412 \u043a\u0430\u043a\u043e\u043c \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0438 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u044e \u043d\u0430\u0447\u0438\u043d\u0430\u043b\u0438, \u0432 \u0442\u0430\u043a\u043e\u043c \u0438 \u0432\u044b\u0437\u044b\u0432\u0430\u0439\u0442\u0435 prepare. \u041f\u043e-\u0434\u0440\u0443\u0433\u043e\u043c\u0443 \u043d\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442. currentXid={0}, prepare xid={1}";
        t[118] = "The connection attempt failed.";
        t[119] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u043f\u044b\u0442\u043a\u0435 \u043f\u043e\u0434\u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f.";
        t[120] = "Unexpected copydata from server for {0}";
        t[121] = "\u041d\u0435\u043e\u0436\u0438\u0434\u0430\u043d\u043d\u044b\u0439 \u0441\u0442\u0430\u0442\u0443\u0441 \u043a\u043e\u043c\u0430\u043d\u0434\u044b COPY: {0}";
        t[124] = "Illegal UTF-8 sequence: initial byte is {0}: {1}";
        t[125] = "\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u043f\u043e\u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c UTF-8: \u043d\u0430\u0447\u0430\u043b\u044c\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u0438\u0435 {0}: {1}";
        t[128] = "This ResultSet is closed.";
        t[129] = "ResultSet \u0437\u0430\u043a\u0440\u044b\u0442.";
        t[142] = "Not implemented: 2nd phase commit must be issued using an idle connection. commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[143] = "\u0414\u0443\u0445\u0444\u0430\u0437\u043d\u0430\u044f \u0444\u0438\u043a\u0441\u0430\u0446\u0438\u044f \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e, \u0435\u0441\u043b\u0438 \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u043d\u0435\u0430\u043a\u0442\u0438\u0432\u043d\u043e (state=idle \u0438 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0446\u0438\u044f \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442). commit xid={0}, currentXid={1}, state={2}, transactionState={3}";
        t[146] = "Too many update results were returned.";
        t[147] = "\u0412\u043e\u0437\u0432\u0440\u0430\u0449\u0435\u043d\u043e \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u043e\u0432 \u043e\u0431\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u044f.";
        t[148] = "An error occurred reading the certificate";
        t[149] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0447\u0442\u0435\u043d\u0438\u0438 \u0441\u0435\u0440\u0442\u0438\u0444\u0438\u043a\u0430\u0442\u0430";
        t[160] = "Unknown type {0}.";
        t[161] = "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0442\u0438\u043f {0}.";
        t[172] = "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}";
        t[173] = "\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u043f\u043e\u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c UTF-8: {0} bytes used to encode a {1} byte value: {2}";
        t[182] = "Protocol error.  Session setup failed.";
        t[183] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u043e\u0442\u043e\u043a\u043e\u043b\u0430.  \u0423\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u0435\u0441\u0441\u0438\u0438 \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c.";
        t[184] = "Connection has been closed.";
        t[185] = "\u042d\u0442\u043e \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u0443\u0436\u0435 \u0431\u044b\u043b\u043e \u0437\u0430\u043a\u0440\u044b\u0442\u043e";
        t[188] = "This copy stream is closed.";
        t[189] = "\u041f\u043e\u0442\u043e\u043a \u0443\u0436\u0435 \u0431\u044b\u043b \u0437\u0430\u043a\u0440\u044b\u0442";
        t[196] = "Statement has been closed.";
        t[197] = "Statement \u0437\u0430\u043a\u0440\u044b\u0442.";
        t[200] = "Failed to set ClientInfo property: {0}";
        t[201] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u0432\u043e\u0439\u0441\u0442\u0432\u043e ClientInfo: {0}";
        t[204] = "Where: {0}";
        t[205] = "\u0413\u0434\u0435: {0}";
        t[212] = "Expected command status BEGIN, got {0}.";
        t[213] = "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u0441\u0442\u0430\u0442\u0443\u0441 \u043a\u043e\u043c\u0430\u043d\u0434\u044b BEGIN, \u043d\u043e \u043f\u043e\u043b\u0443\u0447\u0435\u043d {0}";
        t[216] = "The HostnameVerifier class provided {0} could not be instantiated.";
        t[217] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c HostnameVerifier \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u043b\u0430\u0441\u0441\u0430 {0}";
        t[220] = "Unsupported properties: {0}";
        t[221] = "\u0423\u043a\u0430\u0437\u0430\u043d\u043d\u044b\u0435 \u0441\u0432\u043e\u0439\u0441\u0442\u0432\u0430 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u044e\u0442\u0441\u044f: {0}";
        t[222] = "Failed to create object for: {0}.";
        t[223] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u0438 \u043e\u0431\u044a\u0435\u043a\u0442 \u0434\u043b\u044f: {0}.";
        t[230] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[231] = "\u0421\u043b\u0443\u0447\u0438\u043b\u043e\u0441\u044c \u0447\u0442\u043e-\u0442\u043e \u043d\u0435\u043e\u0431\u044b\u0447\u043d\u043e\u0435, \u0447\u0442\u043e \u0437\u0430\u0441\u0442\u0430\u0432\u0438\u043b\u043e \u0434\u0440\u0430\u0439\u0432\u0435\u0440 \u043f\u0440\u043e\u0438\u0437\u0432\u0435\u0441\u0442\u0438 \u043e\u0448\u0438\u0431\u043a\u0443. \u041f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430 \u0441\u043e\u043e\u0431\u0449\u0438\u0442\u0435 \u044d\u0442\u043e \u0438\u0441\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435.";
        t[236] = "Finalizing a Connection that was never closed:";
        t[237] = "\u0421\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u00ab\u0443\u0442\u0435\u043a\u043b\u043e\u00bb. \u041f\u0440\u043e\u0432\u0435\u0440\u044c\u0442\u0435, \u0447\u0442\u043e \u0432 \u043a\u043e\u0434\u0435 \u043f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u044f \u0432\u044b\u0437\u044b\u0432\u0430\u0435\u0442\u0441\u044f connection.close(). \u0414\u0430\u043b\u0435\u0435 \u0441\u043b\u0435\u0434\u0443\u0435\u0442 \u0441\u0442\u0435\u043a\u0442\u0440\u0435\u0439\u0441 \u0442\u043e\u0433\u043e \u043c\u0435\u0441\u0442\u0430, \u0433\u0434\u0435 \u0441\u043e\u0437\u0434\u0430\u0432\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0431\u043b\u0435\u043c\u043d\u043e\u0435 \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435";
        t[238] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[239] = "\u041d\u0430\u0439\u0434\u0435\u043d\u044b \u043d\u0435\u0432\u0435\u0440\u043d\u044b\u0435 \u0441\u0438\u043c\u0432\u043e\u043b\u044c\u043d\u044b\u0435 \u0434\u0430\u043d\u043d\u044b\u0435.  \u041f\u0440\u0438\u0447\u0438\u043d\u043e\u0439 \u044d\u0442\u043e\u0433\u043e \u0441\u043a\u043e\u0440\u0435\u0435 \u0432\u0441\u0435\u0433\u043e \u044f\u0432\u043b\u044f\u044e\u0442\u0441\u044f \u0445\u0440\u0430\u043d\u0438\u043c\u044b\u0435 \u0434\u0430\u043d\u043d\u044b\u0435 \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u0449\u0438\u0435 \u0441\u0438\u043c\u0432\u043e\u043b\u044b \u043d\u0435 \u0441\u043e\u043e\u0442\u0432\u0435\u0442\u0441\u0442\u0432\u0443\u044e\u0449\u0438\u0435 \u043d\u0430\u0431\u043e\u0440\u0443 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432 \u0431\u0430\u0437\u044b.  \u0422\u0438\u043f\u0438\u0447\u043d\u044b\u043c \u043f\u0440\u0438\u043c\u0435\u0440\u043e\u043c \u044d\u0442\u043e\u0433\u043e \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 8-\u0431\u0438\u0442\u043d\u044b\u0445 \u0434\u0430\u043d\u043d\u044b\u0445 \u0432 \u0431\u0430\u0437\u0435 SQL_ASCII.";
        t[252] = "Unable to create SAXResult for SQLXML.";
        t[253] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c SAXResult \u0434\u043b\u044f SQLXML";
        t[260] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[261] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c SSLSocketFactory \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u043b\u0430\u0441\u0441\u0430 {0}";
        t[266] = "No IOException expected from StringBuffer or StringBuilder";
        t[267] = "\u0427\u0442\u043e-\u0442\u043e \u043f\u043e\u0448\u043b\u043e \u043d\u0435 \u0442\u0430\u043a: \u0438\u0437 \u043a\u043b\u0430\u0441\u0441\u043e\u0432 StringBuffer \u0438 StringBuilder \u0438\u0441\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0439 \u043d\u0435 \u043e\u0436\u0438\u0434\u0430\u043b\u043e\u0441\u044c";
        t[280] = "Interrupted while waiting to obtain lock on database connection";
        t[281] = "\u041e\u0436\u0438\u0434\u0430\u043d\u0438\u0435 COPY \u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0438 \u043f\u0440\u0435\u0440\u0432\u0430\u043d\u043e \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u0435\u043c interrupt";
        t[284] = "Zero bytes may not occur in identifiers.";
        t[285] = "\u0421\u0438\u043c\u0432\u043e\u043b \u0441 \u043a\u043e\u0434\u043e\u043c 0 \u0432 \u0438\u0434\u0435\u043d\u0442\u0438\u0444\u0438\u043a\u0430\u0442\u043e\u0440\u0430\u0445 \u043d\u0435 \u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c";
        t[286] = "There are no rows in this ResultSet.";
        t[287] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0442\u0440\u043e\u043a\u0443, \u0442.\u043a. \u0432 \u0442\u0435\u043a\u0443\u0449\u0435\u043c ResultSet\u2019\u0435 \u0441\u0442\u0440\u043e\u043a \u0432\u043e\u043e\u0431\u0449\u0435 \u043d\u0435\u0442";
        t[288] = "Expected an EOF from server, got: {0}";
        t[289] = "\u041d\u0435\u043e\u0436\u0438\u0434\u0430\u043d\u043d\u044b\u0439 \u043e\u0442\u0432\u0435\u0442 \u043e\u0442 \u0441\u0435\u0440\u0432\u0435\u0440\u0430. \u041e\u0436\u0438\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0435 \u043f\u043e\u0442\u043e\u043a\u0430, \u043f\u043e\u043b\u0443\u0447\u0435\u043d \u0431\u0430\u0439\u0442 {0}";
        t[304] = "No results were returned by the query.";
        t[305] = "\u0417\u0430\u043f\u0440\u043e\u0441 \u043d\u0435 \u0432\u0435\u0440\u043d\u0443\u043b \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u043e\u0432.";
        t[306] = "Invalid targetServerType value: {0}";
        t[307] = "\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 targetServerType: {0}";
        t[310] = "Requested CopyOut but got {0}";
        t[311] = "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u043e\u0442\u0432\u0435\u0442 CopyOut, \u0430 \u043f\u043e\u043b\u0443\u0447\u0435\u043d {0}";
        t[318] = "Invalid flags {0}";
        t[319] = "\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0435 \u0444\u043b\u0430\u0433\u0438 {0}";
        t[324] = "Unsupported Types value: {0}";
        t[325] = "\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u044b\u0439 java.sql.Types \u0442\u0438\u043f: {0}";
        t[326] = "Invalid timeout ({0}<0).";
        t[327] = "\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0442\u0430\u0439\u043c\u0430\u0443\u0442\u0430 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u043d\u0435\u043e\u0442\u0440\u0438\u0446\u0430\u0442\u0435\u043b\u044c\u043d\u044b\u043c: {0}";
        t[328] = "tried to call end without corresponding start call. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[329] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0437\u0430\u0432\u0435\u0440\u0448\u0438\u0442\u044c \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u044e, \u0442.\u043a. \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u044f \u043d\u0435 \u0431\u044b\u043b\u0430 \u043d\u0430\u0447\u0430\u0442\u0430. state={0}, start xid={1}, currentXid={2}, preparedXid={3}";
        t[350] = "A result was returned when none was expected.";
        t[351] = "\u0420\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0451\u043d \u043a\u043e\u0433\u0434\u0430 \u0435\u0433\u043e \u043d\u0435 \u043e\u0436\u0438\u0434\u0430\u043b\u043e\u0441\u044c.";
        t[352] = "Unsupported binary encoding of {0}.";
        t[353] = "\u0411\u0438\u043d\u0430\u0440\u043d\u0430\u044f \u043f\u0435\u0440\u0435\u0434\u0430\u0447\u0430 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0434\u043b\u044f \u0442\u0438\u043f\u0430  {0}";
        t[354] = "Zero bytes may not occur in string parameters.";
        t[355] = "\u0411\u0430\u0439\u0442 \u0441 \u043a\u043e\u0434\u043e\u043c 0 \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0432\u0442\u0440\u0435\u0447\u0430\u0442\u044c\u0441\u044f \u0432 \u0441\u0442\u0440\u043e\u043a\u043e\u0432\u044b\u0445 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u0430\u0445";
        t[360] = "Requested CopyIn but got {0}";
        t[361] = "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u043e\u0442\u0432\u0435\u0442 CopyIn, \u0430 \u043f\u043e\u043b\u0443\u0447\u0435\u043d {0}";
        t[364] = "Error during one-phase commit. commit xid={0}";
        t[365] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0434\u043d\u043e\u0444\u0430\u0437\u043d\u043e\u0439 \u0444\u0438\u043a\u0441\u0430\u0446\u0438\u0438 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0438. commit xid={0}";
        t[372] = "Unable to bind parameter values for statement.";
        t[373] = "\u041d\u0435 \u0432 \u0441\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0438 \u0430\u0441\u0441\u043e\u0446\u0438\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u044f \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u043e\u0432 \u0434\u043b\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b (PGBindException)";
        t[374] = "Interrupted while attempting to connect.";
        t[375] = "\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043f\u0440\u0435\u0440\u0432\u0430\u043d\u043e \u043f\u043e\u043b\u0443\u0447\u0430\u0435\u043d\u0438\u0435\u043c interrupt";
        t[380] = "An unexpected result was returned by a query.";
        t[381] = "\u0417\u0430\u043f\u0440\u043e\u0441 \u0432\u0435\u0440\u043d\u0443\u043b \u043d\u0435\u043e\u0436\u0438\u0434\u0430\u043d\u043d\u044b\u0439 \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442.";
        t[384] = "Method {0} is not yet implemented.";
        t[385] = "\u041c\u0435\u0442\u043e\u0434 {0} \u0435\u0449\u0451 \u043d\u0435 \u0440\u0435\u0430\u043b\u0438\u0437\u043e\u0432\u0430\u043d";
        t[386] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[387] = "\u041c\u0435\u0441\u0442\u043e\u043d\u0430\u0445\u043e\u0436\u0434\u0435\u043d\u0438\u0435: \u0424\u0430\u0439\u043b {0}, \u041f\u0440\u043e\u0446\u0435\u0434\u0443\u0440\u0430: {1}, \u0421\u0442\u0440\u043e\u043a\u0430: {2}";
        t[388] = "The server does not support SSL.";
        t[389] = "\u0421\u0435\u0440\u0432\u0435\u0440 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442 SSL.";
        t[392] = "The password callback class provided {0} could not be instantiated.";
        t[393] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c password callback \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u043b\u0430\u0441\u0441\u0430 {0}";
        t[396] = "Unknown Types value.";
        t[397] = "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 Types.";
        t[400] = "Unknown Response Type {0}.";
        t[401] = "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0442\u0438\u043f \u043e\u0442\u0432\u0435\u0442\u0430 {0}.";
        t[406] = "commit called before end. commit xid={0}, state={1}";
        t[407] = "\u041e\u043f\u0435\u0440\u0430\u0446\u0438\u044f commit \u0434\u043e\u043b\u0436\u043d\u0430 \u0432\u044b\u0437\u044b\u0432\u0430\u0442\u044c\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u043e\u0441\u043b\u0435 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0438 end. commit xid={0}, state={1}";
        t[420] = "An error occurred while setting up the SSL connection.";
        t[421] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0435 SSL-\u043f\u043e\u0434\u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f.";
        t[424] = "Invalid sslmode value: {0}";
        t[425] = "\u041d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 sslmode: {0}";
        t[436] = "Copying from database failed: {0}";
        t[437] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u043a\u0435 \u043e\u0442\u0432\u0435\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u044b COPY: {0}";
        t[438] = "Illegal UTF-8 sequence: final value is out of range: {0}";
        t[439] = "\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u043f\u043e\u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c UTF-8: \u0444\u0438\u043d\u0430\u043b\u044c\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0432\u043d\u0435 \u043e\u0431\u043b\u0430\u0441\u0442\u0438 \u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0445: {0}";
        t[442] = "Error preparing transaction. prepare xid={0}";
        t[443] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u0438 prepare \u0434\u043b\u044f \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0438 {0}";
        t[450] = "A connection could not be made using the requested protocol {0}.";
        t[451] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u043f\u0440\u043e\u0442\u043e\u043a\u043e\u043b\u0430 {0}";
        t[460] = "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[461] = "\u0427\u0435\u0440\u0435\u0434\u043e\u0432\u0430\u043d\u0438\u0435 \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u0439 \u0432 \u043e\u0434\u043d\u043e\u043c \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0438 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f. \u041f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0443\u044e \u0442\u0440\u0430\u043d\u0437\u0430\u043a\u0446\u0438\u044e \u043d\u0443\u0436\u043d\u043e \u0437\u0430\u0432\u0435\u0440\u0448\u0438\u0442\u044c xid={0}, currentXid={1}, state={2}, flags={3}";
        t[462] = "Illegal UTF-8 sequence: final value is a surrogate value: {0}";
        t[463] = "\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u043f\u043e\u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c UTF-8: \u0444\u0438\u043d\u0430\u043b\u044c\u043d\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f surrogate \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435\u043c: {0}";
        t[466] = "The column name {0} was not found in this ResultSet.";
        t[467] = "\u041a\u043e\u043b\u043e\u043d\u043a\u0438 {0} \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e \u0432 \u044d\u0442\u043e\u043c ResultSet\u2019\u2019\u0435.";
        t[468] = "oid type {0} not known and not a number";
        t[469] = "Oid {0} \u043d\u0435 \u0438\u0437\u0432\u0435\u0441\u0442\u0435\u043d \u0438\u043b\u0438 \u043d\u0435 \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0447\u0438\u0441\u043b\u043e\u043c";
        t[476] = "Hint: {0}";
        t[477] = "\u041f\u043e\u0434\u0441\u043a\u0430\u0437\u043a\u0430: {0}";
        t[478] = "Unsupported property name: {0}";
        t[479] = "\u0421\u0432\u043e\u0439\u0441\u0442\u0432\u043e {0} \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f";
        t[480] = "Ran out of memory retrieving query results.";
        t[481] = "\u041d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u0430\u043c\u044f\u0442\u0438 \u0434\u043b\u044f \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u043a\u0438 \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u043e\u0432 \u0437\u0430\u043f\u0440\u043e\u0441\u0430. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u0443\u0432\u0435\u043b\u0438\u0447\u0438\u0442\u044c -Xmx \u0438\u043b\u0438 \u043f\u0440\u043e\u0432\u0435\u0440\u044c\u0442\u0435 \u0440\u0430\u0437\u043c\u0435\u0440\u044b \u043e\u0431\u0440\u0430\u0431\u0430\u0442\u044b\u0432\u0430\u0435\u043c\u044b\u0445 \u0434\u0430\u043d\u043d\u044b\u0445";
        t[484] = "Interval {0} not yet implemented";
        t[485] = "\u0418\u043d\u0442\u0435\u0432\u0440\u0432\u0430\u043b {0} \u0435\u0449\u0451 \u043d\u0435 \u0440\u0435\u0430\u043b\u0438\u0437\u043e\u0432\u0430\u043d";
        t[486] = "This connection has been closed.";
        t[487] = "\u0421\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u0435 \u0443\u0436\u0435 \u0431\u044b\u043b\u043e \u0437\u0430\u043a\u0440\u044b\u0442\u043e";
        t[488] = "The SocketFactory class provided {0} could not be instantiated.";
        t[489] = "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c SSLSocketFactory \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u043e\u0433\u043e \u043a\u043b\u0430\u0441\u0441\u0430 {0}";
        t[490] = "This SQLXML object has already been freed.";
        t[491] = "\u042d\u0442\u043e\u0442 \u043e\u0431\u044a\u0435\u043a\u0442 SQLXML \u0443\u0436\u0435 \u0431\u044b\u043b \u0437\u0430\u043a\u0440\u044b\u0442";
        t[494] = "Unexpected command status: {0}.";
        t[495] = "\u041d\u0435\u043e\u0436\u0438\u0434\u0430\u043d\u043d\u044b\u0439 \u0441\u0442\u0430\u0442\u0443\u0441 \u043a\u043e\u043c\u0430\u043d\u0434\u044b: {0}.";
        t[502] = "Large Objects may not be used in auto-commit mode.";
        t[503] = "\u0411\u043e\u043b\u044c\u0448\u0438\u0435 \u043e\u0431\u044a\u0435\u043a\u0442\u044b \u043d\u0435 \u043c\u043e\u0433\u0443\u0442 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u0430\u0432\u0442\u043e-\u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f (auto-commit).";
        t[504] = "Conversion of money failed.";
        t[505] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u043d\u0438\u0438 \u0442\u0438\u043f\u0430 money.";
        t[512] = "No value specified for parameter {0}.";
        t[513] = "\u041d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d\u043e \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u0434\u043b\u044f \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u0430 {0}.";
        t[514] = "The server requested password-based authentication, but no password was provided.";
        t[515] = "\u0421\u0435\u0440\u0432\u0435\u0440 \u0437\u0430\u043f\u0440\u043e\u0441\u0438\u043b \u043f\u0430\u0440\u043e\u043b\u044c\u043d\u0443\u044e \u0430\u0443\u0442\u0435\u043d\u0442\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u044e, \u043d\u043e \u043f\u0430\u0440\u043e\u043b\u044c \u043d\u0435 \u0431\u044b\u043b \u0443\u043a\u0430\u0437\u0430\u043d.";
        t[518] = "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}";
        t[519] = "\u041d\u0435\u0432\u0435\u0440\u043d\u0430\u044f \u043f\u043e\u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c UTF-8: \u0431\u0430\u0439\u0442 {0} \u0438\u0437 {1} \u043d\u0435 \u043f\u043e\u0434\u0445\u043e\u0434\u0438\u0442 \u043a \u043c\u0430\u0441\u043a\u0435 10xxxxxx: {2}";
        t[522] = "Conversion to type {0} failed: {1}.";
        t[523] = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u043d\u0438\u0438 \u043a \u0442\u0438\u043f\u0443 {0}: {1}";
        t[528] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[529] = "\u0422\u0438\u043f \u0430\u0443\u0442\u0435\u043d\u0442\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u0438 {0} \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f. \u041f\u0440\u043e\u0432\u0435\u0440\u044c\u0442\u0435 \u0435\u0441\u043b\u0438 \u0432\u044b \u0441\u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0438\u0440\u043e\u0432\u0430\u043b\u0438 \u0444\u0430\u0439\u043b pg_hba.conf \u0447\u0442\u043e\u0431\u044b \u0432\u043a\u043b\u044e\u0447\u0438\u0442\u044c IP-\u0430\u0434\u0440\u0435\u0441\u0430 \u043a\u043b\u0438\u0435\u043d\u0442\u043e\u0432 \u0438\u043b\u0438 \u043f\u043e\u0434\u0441\u0435\u0442\u044c. \u0422\u0430\u043a\u0436\u0435 \u0443\u0434\u043e\u0441\u0442\u043e\u0432\u0435\u0440\u0442\u0435\u0441\u044c \u0447\u0442\u043e \u043e\u043d \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0435\u0442 \u0441\u0445\u0435\u043c\u0443 \u0430\u0443\u0442\u0435\u043d\u0442\u0438\u0444\u0438\u043a\u0430\u0446\u0438\u0438 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u0443\u044e \u0434\u0440\u0430\u0439\u0432\u0435\u0440\u043e\u043c.";
        t[534] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[535] = "\u0418\u043d\u0434\u0435\u043a\u0441 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u0430 \u0432\u043d\u0435 \u0434\u0438\u0430\u043f\u0430\u0437\u043e\u043d\u0430: {0}. \u0414\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u044f: 1..{1}";
        table = t;
    }
}

