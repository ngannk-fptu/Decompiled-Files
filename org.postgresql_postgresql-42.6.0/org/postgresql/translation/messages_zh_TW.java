/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_zh_TW
extends ResourceBundle {
    private static final String[] table;

    @Override
    public Object handleGetObject(String msgid) throws MissingResourceException {
        String found;
        int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 289 << 1;
        String found2 = table[idx];
        if (found2 == null) {
            return null;
        }
        if (msgid.equals(found2)) {
            return table[idx + 1];
        }
        int incr = hash_val % 287 + 1 << 1;
        do {
            if ((idx += incr) >= 578) {
                idx -= 578;
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
                while (this.idx < 578 && table[this.idx] == null) {
                    this.idx += 2;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.idx < 578;
            }

            public Object nextElement() {
                String key = table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 578 && table[this.idx] == null);
                return key;
            }
        };
    }

    public ResourceBundle getParent() {
        return this.parent;
    }

    static {
        String[] t = new String[578];
        t[0] = "";
        t[1] = "Project-Id-Version: PostgreSQL JDBC Driver 8.3\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2008-01-21 16:50+0800\nLast-Translator: \u90ed\u671d\u76ca(ChaoYi, Kuo) <Kuo.ChaoYi@gmail.com>\nLanguage-Team: The PostgreSQL Development Team <Kuo.ChaoYi@gmail.com>\nLanguage: \nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Poedit-Language: Chinese\nX-Poedit-Country: TAIWAN\nX-Poedit-SourceCharset: utf-8\n";
        t[6] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[7] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u8cc7\u6599\u5217\u4e0a\u547c\u53eb cancelRowUpdates()\u3002";
        t[8] = "The server requested password-based authentication, but no password was provided.";
        t[9] = "\u4f3a\u670d\u5668\u8981\u6c42\u4f7f\u7528\u5bc6\u78bc\u9a57\u8b49\uff0c\u4f46\u662f\u5bc6\u78bc\u4e26\u672a\u63d0\u4f9b\u3002";
        t[12] = "Detail: {0}";
        t[13] = "\u8a73\u7d30\uff1a{0}";
        t[16] = "Can''t refresh the insert row.";
        t[17] = "\u7121\u6cd5\u91cd\u8b80\u65b0\u589e\u7684\u8cc7\u6599\u5217\u3002";
        t[18] = "Connection has been closed.";
        t[19] = "Connection \u5df2\u7d93\u88ab\u95dc\u9589\u3002";
        t[24] = "Bad value for type {0} : {1}";
        t[25] = "\u4e0d\u826f\u7684\u578b\u5225\u503c {0} : {1}";
        t[36] = "Truncation of large objects is only implemented in 8.3 and later servers.";
        t[37] = "\u5927\u578b\u7269\u4ef6\u7684\u622a\u65b7(Truncation)\u50c5\u88ab\u5be6\u4f5c\u57f7\u884c\u5728 8.3 \u548c\u5f8c\u4f86\u7684\u4f3a\u670d\u5668\u3002";
        t[40] = "Cannot retrieve the name of an unnamed savepoint.";
        t[41] = "\u7121\u6cd5\u53d6\u5f97\u672a\u547d\u540d\u5132\u5b58\u9ede(Savepoint)\u7684\u540d\u7a31\u3002";
        t[46] = "An error occurred while setting up the SSL connection.";
        t[47] = "\u9032\u884c SSL \u9023\u7dda\u6642\u767c\u751f\u932f\u8aa4\u3002";
        t[50] = "suspend/resume not implemented";
        t[51] = "\u66ab\u505c(suspend)/\u518d\u7e7c\u7e8c(resume)\u5c1a\u672a\u88ab\u5be6\u4f5c\u3002";
        t[60] = "{0} function takes one and only one argument.";
        t[61] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e00\u500b\u4e14\u50c5\u6709\u4e00\u500b\u5f15\u6578\u3002";
        t[62] = "Conversion to type {0} failed: {1}.";
        t[63] = "\u8f49\u63db\u578b\u5225 {0} \u5931\u6557\uff1a{1}\u3002";
        t[66] = "Conversion of money failed.";
        t[67] = "money \u8f49\u63db\u5931\u6557\u3002";
        t[70] = "A result was returned when none was expected.";
        t[71] = "\u50b3\u56de\u9810\u671f\u4e4b\u5916\u7684\u7d50\u679c\u3002";
        t[80] = "This PooledConnection has already been closed.";
        t[81] = "\u9019\u500b PooledConnection \u5df2\u7d93\u88ab\u95dc\u9589\u3002";
        t[84] = "Multiple ResultSets were returned by the query.";
        t[85] = "\u67e5\u8a62\u50b3\u56de\u591a\u500b ResultSet\u3002";
        t[90] = "Not on the insert row.";
        t[91] = "\u4e0d\u5728\u65b0\u589e\u7684\u8cc7\u6599\u5217\u4e0a\u3002";
        t[94] = "An unexpected result was returned by a query.";
        t[95] = "\u50b3\u56de\u975e\u9810\u671f\u7684\u67e5\u8a62\u7d50\u679c\u3002";
        t[102] = "Internal Query: {0}";
        t[103] = "\u5167\u90e8\u67e5\u8a62\uff1a{0}";
        t[106] = "The array index is out of range: {0}";
        t[107] = "\u9663\u5217\u7d22\u5f15\u8d85\u904e\u8a31\u53ef\u7bc4\u570d\uff1a{0}";
        t[112] = "Connection attempt timed out.";
        t[113] = "Connection \u5617\u8a66\u903e\u6642\u3002";
        t[114] = "Unable to find name datatype in the system catalogs.";
        t[115] = "\u5728\u7cfb\u7d71 catalog \u4e2d\u627e\u4e0d\u5230\u540d\u7a31\u8cc7\u6599\u985e\u578b(datatype)\u3002";
        t[116] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[117] = "\u4e0d\u660e\u7684\u539f\u56e0\u5c0e\u81f4\u9a45\u52d5\u7a0b\u5f0f\u9020\u6210\u5931\u6557\uff0c\u8acb\u56de\u5831\u9019\u500b\u4f8b\u5916\u3002";
        t[120] = "The array index is out of range: {0}, number of elements: {1}.";
        t[121] = "\u9663\u5217\u7d22\u5f15\u8d85\u904e\u8a31\u53ef\u7bc4\u570d\uff1a{0}\uff0c\u5143\u7d20\u6578\u91cf\uff1a{1}\u3002";
        t[138] = "Invalid flags {0}";
        t[139] = "\u7121\u6548\u7684\u65d7\u6a19 {0}";
        t[146] = "Unexpected error writing large object to database.";
        t[147] = "\u5c07\u5927\u578b\u7269\u4ef6(large object)\u5beb\u5165\u8cc7\u6599\u5eab\u6642\u767c\u751f\u4e0d\u660e\u932f\u8aa4\u3002";
        t[162] = "Query timeout must be a value greater than or equals to 0.";
        t[163] = "\u67e5\u8a62\u903e\u6642\u7b49\u5019\u6642\u9593\u5fc5\u9808\u5927\u65bc\u6216\u7b49\u65bc 0\u3002";
        t[170] = "Unknown type {0}.";
        t[171] = "\u4e0d\u660e\u7684\u578b\u5225 {0}";
        t[174] = "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.";
        t[175] = "\u9019\u4f3a\u670d\u5668\u7684 standard_conforming_strings \u53c3\u6578\u5df2\u56de\u5831\u70ba {0}\uff0cJDBC \u9a45\u52d5\u7a0b\u5f0f\u5df2\u9810\u671f\u958b\u555f\u6216\u662f\u95dc\u9589\u3002";
        t[176] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[177] = "\u767c\u73fe\u4e0d\u5408\u6cd5\u7684\u5b57\u5143\uff0c\u53ef\u80fd\u7684\u539f\u56e0\u662f\u6b32\u5132\u5b58\u7684\u8cc7\u6599\u4e2d\u5305\u542b\u8cc7\u6599\u5eab\u7684\u5b57\u5143\u96c6\u4e0d\u652f\u63f4\u7684\u5b57\u78bc\uff0c\u5176\u4e2d\u6700\u5e38\u898b\u4f8b\u5b50\u7684\u5c31\u662f\u5c07 8 \u4f4d\u5143\u8cc7\u6599\u5b58\u5165\u4f7f\u7528 SQL_ASCII \u7de8\u78bc\u7684\u8cc7\u6599\u5eab\u4e2d\u3002";
        t[178] = "The column index is out of range: {0}, number of columns: {1}.";
        t[179] = "\u6b04\u4f4d\u7d22\u5f15\u8d85\u904e\u8a31\u53ef\u7bc4\u570d\uff1a{0}\uff0c\u6b04\u4f4d\u6578\uff1a{1}\u3002";
        t[180] = "The connection attempt failed.";
        t[181] = "\u5617\u8a66\u9023\u7dda\u5df2\u5931\u6557\u3002";
        t[182] = "No value specified for parameter {0}.";
        t[183] = "\u672a\u8a2d\u5b9a\u53c3\u6578\u503c {0} \u7684\u5167\u5bb9\u3002";
        t[190] = "Provided Reader failed.";
        t[191] = "\u63d0\u4f9b\u7684 Reader \u5df2\u5931\u6557\u3002";
        t[194] = "Unsupported value for stringtype parameter: {0}";
        t[195] = "\u5b57\u4e32\u578b\u5225\u53c3\u6578\u503c\u672a\u88ab\u652f\u6301\uff1a{0}";
        t[198] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[199] = "\u5df2\u7d93\u5ba3\u544a CallableStatement \u51fd\u5f0f\uff0c\u4f46\u662f\u5c1a\u672a\u547c\u53eb registerOutParameter (1, <some_type>) \u3002";
        t[204] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[205] = "\u4e0d\u80fd\u5728 ResultSet \u7684\u7b2c\u4e00\u7b46\u8cc7\u6599\u4e4b\u524d\u547c\u53eb deleteRow()\u3002";
        t[214] = "The maximum field size must be a value greater than or equal to 0.";
        t[215] = "\u6700\u5927\u6b04\u4f4d\u5bb9\u91cf\u5fc5\u9808\u5927\u65bc\u6216\u7b49\u65bc 0\u3002";
        t[216] = "Fetch size must be a value greater to or equal to 0.";
        t[217] = "\u8cc7\u6599\u8b80\u53d6\u7b46\u6578(fetch size)\u5fc5\u9808\u5927\u65bc\u6216\u7b49\u65bc 0\u3002";
        t[220] = "PostgreSQL LOBs can only index to: {0}";
        t[221] = "PostgreSQL LOBs \u50c5\u80fd\u7d22\u5f15\u5230\uff1a{0}";
        t[224] = "The JVM claims not to support the encoding: {0}";
        t[225] = "JVM \u8072\u660e\u4e26\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0} \u3002";
        t[226] = "Interval {0} not yet implemented";
        t[227] = "\u9694\u7d55 {0} \u5c1a\u672a\u88ab\u5be6\u4f5c\u3002";
        t[238] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[239] = "Fastpath \u547c\u53eb {0} - \u6c92\u6709\u50b3\u56de\u503c\uff0c\u4e14\u61c9\u8a72\u50b3\u56de\u4e00\u500b\u6574\u6578\u3002";
        t[246] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[247] = "ResultSets \u8207\u4e26\u767c\u540c\u4f5c(Concurrency) CONCUR_READ_ONLY \u4e0d\u80fd\u88ab\u66f4\u65b0\u3002";
        t[250] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[251] = "\u9019\u500b statement \u672a\u5ba3\u544a OUT \u53c3\u6578\uff0c\u4f7f\u7528 '{' ?= call ... '}' \u5ba3\u544a\u4e00\u500b\u3002";
        t[256] = "Cannot reference a savepoint after it has been released.";
        t[257] = "\u7121\u6cd5\u53c3\u7167\u5df2\u7d93\u88ab\u91cb\u653e\u7684\u5132\u5b58\u9ede\u3002";
        t[260] = "Unsupported Types value: {0}";
        t[261] = "\u672a\u88ab\u652f\u6301\u7684\u578b\u5225\u503c\uff1a{0}";
        t[266] = "Protocol error.  Session setup failed.";
        t[267] = "\u901a\u8a0a\u5354\u5b9a\u932f\u8aa4\uff0cSession \u521d\u59cb\u5316\u5931\u6557\u3002";
        t[274] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[275] = "\u4e0d\u80fd\u5728 ResultSet \u7684\u6700\u5f8c\u4e00\u7b46\u8cc7\u6599\u4e4b\u5f8c\u547c\u53eb deleteRow()\u3002";
        t[278] = "Internal Position: {0}";
        t[279] = "\u5167\u90e8\u4f4d\u7f6e\uff1a{0}";
        t[280] = "Zero bytes may not occur in identifiers.";
        t[281] = "\u5728\u6a19\u8b58\u8b58\u5225\u7b26\u4e2d\u4e0d\u5b58\u5728\u96f6\u4f4d\u5143\u7d44\u3002";
        t[288] = "{0} function doesn''t take any argument.";
        t[289] = "{0} \u51fd\u5f0f\u7121\u6cd5\u53d6\u5f97\u4efb\u4f55\u7684\u5f15\u6578\u3002";
        t[300] = "This statement has been closed.";
        t[301] = "\u9019\u500b statement \u5df2\u7d93\u88ab\u95dc\u9589\u3002";
        t[318] = "Cannot establish a savepoint in auto-commit mode.";
        t[319] = "\u5728\u81ea\u52d5\u78ba\u8a8d\u4e8b\u7269\u4ea4\u6613\u6a21\u5f0f\u7121\u6cd5\u5efa\u7acb\u5132\u5b58\u9ede(Savepoint)\u3002";
        t[320] = "Position: {0}";
        t[321] = "\u4f4d\u7f6e\uff1a{0}";
        t[322] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[323] = "\u4e0d\u53ef\u66f4\u65b0\u7684 ResultSet\u3002\u7528\u4f86\u7522\u751f\u9019\u500b ResultSet \u7684 SQL \u547d\u4ee4\u53ea\u80fd\u64cd\u4f5c\u4e00\u500b\u8cc7\u6599\u8868\uff0c\u4e26\u4e14\u5fc5\u9700\u9078\u64c7\u6240\u6709\u4e3b\u9375\u6b04\u4f4d\uff0c\u8a73\u7d30\u8acb\u53c3\u95b1 JDBC 2.1 API \u898f\u683c\u66f8 5.6 \u7bc0\u3002";
        t[330] = "This ResultSet is closed.";
        t[331] = "\u9019\u500b ResultSet \u5df2\u7d93\u88ab\u95dc\u9589\u3002";
        t[338] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[339] = "\u5df2\u8a3b\u518a\u53c3\u6578\u578b\u5225 {0}\uff0c\u4f46\u662f\u53c8\u547c\u53eb\u4e86get{1}(sqltype={2})\u3002";
        t[342] = "Transaction isolation level {0} not supported.";
        t[343] = "\u4e0d\u652f\u63f4\u4ea4\u6613\u9694\u7d55\u7b49\u7d1a {0} \u3002";
        t[344] = "Statement has been closed.";
        t[345] = "Sstatement \u5df2\u7d93\u88ab\u95dc\u9589\u3002";
        t[352] = "Server SQLState: {0}";
        t[353] = "\u4f3a\u670d\u5668 SQLState\uff1a{0}";
        t[354] = "No primary key found for table {0}.";
        t[355] = "{0} \u8cc7\u6599\u8868\u4e2d\u672a\u627e\u5230\u4e3b\u9375(Primary key)\u3002";
        t[362] = "Cannot convert an instance of {0} to type {1}";
        t[363] = "\u7121\u6cd5\u8f49\u63db {0} \u5230\u985e\u578b {1} \u7684\u5be6\u4f8b";
        t[364] = "DataSource has been closed.";
        t[365] = "DataSource \u5df2\u7d93\u88ab\u95dc\u9589\u3002";
        t[368] = "The column name {0} was not found in this ResultSet.";
        t[369] = "ResultSet \u4e2d\u627e\u4e0d\u5230\u6b04\u4f4d\u540d\u7a31 {0}\u3002";
        t[372] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[373] = "\u67e5\u8a62\u7d50\u679c\u6307\u6a19\u4f4d\u7f6e\u4e0d\u6b63\u78ba\uff0c\u60a8\u4e5f\u8a31\u9700\u8981\u547c\u53eb ResultSet \u7684 next() \u65b9\u6cd5\u3002";
        t[378] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[379] = "\u7121\u6cd5\u66f4\u65b0 ResultSet\uff0c\u53ef\u80fd\u5728\u7b2c\u4e00\u7b46\u8cc7\u6599\u4e4b\u524d\u6216\u6700\u672a\u7b46\u8cc7\u6599\u4e4b\u5f8c\u3002";
        t[380] = "Method {0} is not yet implemented.";
        t[381] = "\u9019\u500b {0} \u65b9\u6cd5\u5c1a\u672a\u88ab\u5be6\u4f5c\u3002";
        t[382] = "{0} function takes two or three arguments.";
        t[383] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e8c\u500b\u6216\u4e09\u500b\u5f15\u6578\u3002";
        t[384] = "The JVM claims not to support the {0} encoding.";
        t[385] = "JVM \u8072\u660e\u4e26\u4e0d\u652f\u63f4 {0} \u7de8\u78bc\u3002";
        t[396] = "Unknown Response Type {0}.";
        t[397] = "\u4e0d\u660e\u7684\u56de\u61c9\u985e\u578b {0}\u3002";
        t[398] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[399] = "\u53c3\u6578\u7d22\u5f15\u8d85\u51fa\u8a31\u53ef\u7bc4\u570d\uff1a{0}\uff0c\u53c3\u6578\u7e3d\u6578\uff1a{1}\u3002";
        t[400] = "Where: {0}";
        t[401] = "\u5728\u4f4d\u7f6e\uff1a{0}";
        t[406] = "Cannot call deleteRow() when on the insert row.";
        t[407] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u8cc7\u6599\u4e0a\u547c\u53eb deleteRow()\u3002";
        t[414] = "{0} function takes four and only four argument.";
        t[415] = "{0} \u51fd\u5f0f\u53d6\u5f97\u56db\u500b\u4e14\u50c5\u6709\u56db\u500b\u5f15\u6578\u3002";
        t[416] = "Unable to translate data into the desired encoding.";
        t[417] = "\u7121\u6cd5\u5c07\u8cc7\u6599\u8f49\u6210\u76ee\u6a19\u7de8\u78bc\u3002";
        t[424] = "Can''t use relative move methods while on the insert row.";
        t[425] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u8cc7\u6599\u5217\u4e0a\u4f7f\u7528\u76f8\u5c0d\u4f4d\u7f6e move \u65b9\u6cd5\u3002";
        t[434] = "Invalid stream length {0}.";
        t[435] = "\u7121\u6548\u7684\u4e32\u6d41\u9577\u5ea6 {0}.";
        t[436] = "The driver currently does not support COPY operations.";
        t[437] = "\u9a45\u52d5\u7a0b\u5f0f\u76ee\u524d\u4e0d\u652f\u63f4 COPY \u64cd\u4f5c\u3002";
        t[440] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[441] = "\u6700\u5927\u8cc7\u6599\u8b80\u53d6\u7b46\u6578\u5fc5\u9808\u5927\u65bc\u6216\u7b49\u65bc 0\u3002";
        t[446] = "Failed to create object for: {0}.";
        t[447] = "\u70ba {0} \u5efa\u7acb\u7269\u4ef6\u5931\u6557\u3002";
        t[448] = "{0} function takes three and only three arguments.";
        t[449] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e09\u500b\u4e14\u50c5\u6709\u4e09\u500b\u5f15\u6578\u3002";
        t[450] = "Conversion of interval failed";
        t[451] = "\u9694\u7d55(Interval)\u8f49\u63db\u5931\u6557\u3002";
        t[452] = "Cannot tell if path is open or closed: {0}.";
        t[453] = "\u7121\u6cd5\u5f97\u77e5 path \u662f\u958b\u555f\u6216\u95dc\u9589\uff1a{0}\u3002";
        t[460] = "Provided InputStream failed.";
        t[461] = "\u63d0\u4f9b\u7684 InputStream \u5df2\u5931\u6557\u3002";
        t[462] = "Invalid fetch direction constant: {0}.";
        t[463] = "\u7121\u6548\u7684 fetch \u65b9\u5411\u5e38\u6578\uff1a{0}\u3002";
        t[472] = "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[473] = "\u4e8b\u7269\u4ea4\u6613\u9694\u7d55(Transaction interleaving)\u672a\u88ab\u5be6\u4f5c\u3002xid={0}, currentXid={1}, state={2}, flags={3}";
        t[474] = "{0} function takes two and only two arguments.";
        t[475] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e8c\u500b\u4e14\u50c5\u6709\u4e8c\u500b\u5f15\u6578\u3002";
        t[476] = "There are no rows in this ResultSet.";
        t[477] = "ResultSet \u4e2d\u627e\u4e0d\u5230\u8cc7\u6599\u5217\u3002";
        t[478] = "Zero bytes may not occur in string parameters.";
        t[479] = "\u5b57\u4e32\u53c3\u6578\u4e0d\u80fd\u6709 0 \u500b\u4f4d\u5143\u7d44\u3002";
        t[480] = "Cannot call updateRow() when on the insert row.";
        t[481] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u8cc7\u6599\u5217\u4e0a\u547c\u53eb deleteRow()\u3002";
        t[482] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[483] = "Connection \u5df2\u81ea\u52d5\u7d50\u675f\uff0c\u56e0\u70ba\u4e00\u500b\u65b0\u7684  PooledConnection \u9023\u7dda\u88ab\u958b\u555f\u6216\u8005\u6216 PooledConnection \u5df2\u88ab\u95dc\u9589\u3002";
        t[488] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[489] = "\u4e00\u500b CallableStatement \u57f7\u884c\u51fd\u5f0f\u5f8c\u8f38\u51fa\u7684\u53c3\u6578\u578b\u5225\u70ba {1} \u503c\u70ba {0}\uff0c\u4f46\u662f\u5df2\u8a3b\u518a\u7684\u578b\u5225\u662f {2}\u3002";
        t[494] = "Cannot cast an instance of {0} to type {1}";
        t[495] = "\u4e0d\u80fd\u8f49\u63db\u4e00\u500b {0} \u5be6\u4f8b\u5230\u578b\u5225 {1}";
        t[498] = "Cannot retrieve the id of a named savepoint.";
        t[499] = "\u7121\u6cd5\u53d6\u5f97\u5df2\u547d\u540d\u5132\u5b58\u9ede\u7684 id\u3002";
        t[500] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[501] = "\u4e0d\u80fd\u5728\u4e8b\u7269\u4ea4\u6613\u904e\u7a0b\u4e2d\u6539\u8b8a\u4e8b\u7269\u4ea4\u6613\u552f\u8b80\u5c6c\u6027\u3002";
        t[502] = "The server does not support SSL.";
        t[503] = "\u4f3a\u670d\u5668\u4e0d\u652f\u63f4 SSL \u9023\u7dda\u3002";
        t[510] = "A connection could not be made using the requested protocol {0}.";
        t[511] = "\u7121\u6cd5\u4ee5\u8981\u6c42\u7684\u901a\u8a0a\u5354\u5b9a {0} \u5efa\u7acb\u9023\u7dda\u3002";
        t[512] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[513] = "\u4e0d\u652f\u63f4 {0} \u9a57\u8b49\u578b\u5225\u3002\u8acb\u6838\u5c0d\u60a8\u5df2\u7d93\u7d44\u614b pg_hba.conf \u6a94\u6848\u5305\u542b\u5ba2\u6236\u7aef\u7684IP\u4f4d\u5740\u6216\u7db2\u8def\u5340\u6bb5\uff0c\u4ee5\u53ca\u9a45\u52d5\u7a0b\u5f0f\u6240\u652f\u63f4\u7684\u9a57\u8b49\u67b6\u69cb\u6a21\u5f0f\u5df2\u88ab\u652f\u63f4\u3002";
        t[514] = "Malformed function or procedure escape syntax at offset {0}.";
        t[515] = "\u4e0d\u6b63\u78ba\u7684\u51fd\u5f0f\u6216\u7a0b\u5e8f escape \u8a9e\u6cd5\u65bc {0}\u3002";
        t[516] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[517] = "\u9019\u4f3a\u670d\u5668\u7684 DateStyle \u53c3\u6578\u88ab\u66f4\u6539\u6210 {0}\uff0cJDBC \u9a45\u52d5\u7a0b\u5f0f\u8acb\u6c42\u9700\u8981 DateStyle \u4ee5 ISO \u958b\u982d\u4ee5\u6b63\u78ba\u5de5\u4f5c\u3002";
        t[518] = "No results were returned by the query.";
        t[519] = "\u67e5\u8a62\u6c92\u6709\u50b3\u56de\u4efb\u4f55\u7d50\u679c\u3002";
        t[520] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[521] = "\u4f4d\u7f6e\uff1a\u6a94\u6848\uff1a{0}\uff0c\u5e38\u5f0f\uff1a{1}\uff0c\u884c\uff1a{2}";
        t[526] = "Hint: {0}";
        t[527] = "\u5efa\u8b70\uff1a{0}";
        t[528] = "A CallableStatement was executed with nothing returned.";
        t[529] = "\u4e00\u500b CallableStatement \u57f7\u884c\u51fd\u5f0f\u5f8c\u6c92\u6709\u50b3\u56de\u503c\u3002";
        t[530] = "Unknown ResultSet holdability setting: {0}.";
        t[531] = "\u672a\u77e5\u7684 ResultSet \u53ef\u9069\u7528\u7684\u8a2d\u7f6e\uff1a{0}\u3002";
        t[540] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[541] = "\u4e0d\u80fd\u5728\u4e8b\u52d9\u4ea4\u6613\u904e\u7a0b\u4e2d\u6539\u8b8a\u4e8b\u7269\u4ea4\u6613\u9694\u7d55\u7b49\u7d1a\u3002";
        t[544] = "The fastpath function {0} is unknown.";
        t[545] = "\u4e0d\u660e\u7684 fastpath \u51fd\u5f0f {0}\u3002";
        t[546] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[547] = "\u5728 PreparedStatement \u4e0a\u4e0d\u80fd\u4f7f\u7528\u7372\u53d6\u67e5\u8a62\u5b57\u4e32\u7684\u67e5\u8a62\u65b9\u6cd5\u3002";
        t[556] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[557] = "\u64cd\u4f5c\u8981\u6c42\u53ef\u6372\u52d5\u7684 ResultSet\uff0c\u4f46\u6b64 ResultSet \u662f FORWARD_ONLY\u3002";
        t[564] = "Unknown Types value.";
        t[565] = "\u4e0d\u660e\u7684\u578b\u5225\u503c\u3002";
        t[570] = "Large Objects may not be used in auto-commit mode.";
        t[571] = "\u5927\u578b\u7269\u4ef6\u7121\u6cd5\u88ab\u4f7f\u7528\u5728\u81ea\u52d5\u78ba\u8a8d\u4e8b\u7269\u4ea4\u6613\u6a21\u5f0f\u3002";
        table = t;
    }
}

