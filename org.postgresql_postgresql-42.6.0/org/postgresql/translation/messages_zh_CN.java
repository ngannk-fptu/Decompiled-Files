/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_zh_CN
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
        t[1] = "Project-Id-Version: PostgreSQL JDBC Driver 8.3\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2008-01-31 14:34+0800\nLast-Translator: \u90ed\u671d\u76ca(ChaoYi, Kuo) <Kuo.ChaoYi@gmail.com>\nLanguage-Team: The PostgreSQL Development Team <Kuo.ChaoYi@gmail.com>\nLanguage: \nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Poedit-Language: Chinese\nX-Poedit-Country: CHINA\nX-Poedit-SourceCharset: utf-8\n";
        t[6] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[7] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u6570\u636e\u5217\u4e0a\u547c\u53eb cancelRowUpdates()\u3002";
        t[8] = "The server requested password-based authentication, but no password was provided.";
        t[9] = "\u670d\u52a1\u5668\u8981\u6c42\u4f7f\u7528\u5bc6\u7801\u9a8c\u8bc1\uff0c\u4f46\u662f\u5bc6\u7801\u5e76\u672a\u63d0\u4f9b\u3002";
        t[12] = "Detail: {0}";
        t[13] = "\u8be6\u7ec6\uff1a{0}";
        t[16] = "Can''t refresh the insert row.";
        t[17] = "\u65e0\u6cd5\u91cd\u8bfb\u65b0\u589e\u7684\u6570\u636e\u5217\u3002";
        t[18] = "Connection has been closed.";
        t[19] = "Connection \u5df2\u7ecf\u88ab\u5173\u95ed\u3002";
        t[24] = "Bad value for type {0} : {1}";
        t[25] = "\u4e0d\u826f\u7684\u7c7b\u578b\u503c {0} : {1}";
        t[36] = "Truncation of large objects is only implemented in 8.3 and later servers.";
        t[37] = "\u5927\u578b\u5bf9\u8c61\u7684\u622a\u65ad(Truncation)\u4ec5\u88ab\u5b9e\u4f5c\u6267\u884c\u5728 8.3 \u548c\u540e\u6765\u7684\u670d\u52a1\u5668\u3002";
        t[40] = "Cannot retrieve the name of an unnamed savepoint.";
        t[41] = "\u65e0\u6cd5\u53d6\u5f97\u672a\u547d\u540d\u50a8\u5b58\u70b9(Savepoint)\u7684\u540d\u79f0\u3002";
        t[46] = "An error occurred while setting up the SSL connection.";
        t[47] = "\u8fdb\u884c SSL \u8fde\u7ebf\u65f6\u53d1\u751f\u9519\u8bef\u3002";
        t[50] = "suspend/resume not implemented";
        t[51] = "\u6682\u505c(suspend)/\u518d\u7ee7\u7eed(resume)\u5c1a\u672a\u88ab\u5b9e\u4f5c\u3002";
        t[60] = "{0} function takes one and only one argument.";
        t[61] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e00\u4e2a\u4e14\u4ec5\u6709\u4e00\u4e2a\u5f15\u6570\u3002";
        t[62] = "Conversion to type {0} failed: {1}.";
        t[63] = "\u8f6c\u6362\u7c7b\u578b {0} \u5931\u8d25\uff1a{1}\u3002";
        t[66] = "Conversion of money failed.";
        t[67] = "money \u8f6c\u6362\u5931\u8d25\u3002";
        t[70] = "A result was returned when none was expected.";
        t[71] = "\u4f20\u56de\u9884\u671f\u4e4b\u5916\u7684\u7ed3\u679c\u3002";
        t[80] = "This PooledConnection has already been closed.";
        t[81] = "\u8fd9\u4e2a PooledConnection \u5df2\u7ecf\u88ab\u5173\u95ed\u3002";
        t[84] = "Multiple ResultSets were returned by the query.";
        t[85] = "\u67e5\u8be2\u4f20\u56de\u591a\u4e2a ResultSet\u3002";
        t[90] = "Not on the insert row.";
        t[91] = "\u4e0d\u5728\u65b0\u589e\u7684\u6570\u636e\u5217\u4e0a\u3002";
        t[94] = "An unexpected result was returned by a query.";
        t[95] = "\u4f20\u56de\u975e\u9884\u671f\u7684\u67e5\u8be2\u7ed3\u679c\u3002";
        t[102] = "Internal Query: {0}";
        t[103] = "\u5185\u90e8\u67e5\u8be2\uff1a{0}";
        t[106] = "The array index is out of range: {0}";
        t[107] = "\u9635\u5217\u7d22\u5f15\u8d85\u8fc7\u8bb8\u53ef\u8303\u56f4\uff1a{0}";
        t[112] = "Connection attempt timed out.";
        t[113] = "Connection \u5c1d\u8bd5\u903e\u65f6\u3002";
        t[114] = "Unable to find name datatype in the system catalogs.";
        t[115] = "\u5728\u7cfb\u7edf catalog \u4e2d\u627e\u4e0d\u5230\u540d\u79f0\u6570\u636e\u7c7b\u578b(datatype)\u3002";
        t[116] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[117] = "\u4e0d\u660e\u7684\u539f\u56e0\u5bfc\u81f4\u9a71\u52a8\u7a0b\u5e8f\u9020\u6210\u5931\u8d25\uff0c\u8bf7\u56de\u62a5\u8fd9\u4e2a\u4f8b\u5916\u3002";
        t[120] = "The array index is out of range: {0}, number of elements: {1}.";
        t[121] = "\u9635\u5217\u7d22\u5f15\u8d85\u8fc7\u8bb8\u53ef\u8303\u56f4\uff1a{0}\uff0c\u5143\u7d20\u6570\u91cf\uff1a{1}\u3002";
        t[138] = "Invalid flags {0}";
        t[139] = "\u65e0\u6548\u7684\u65d7\u6807 flags {0}";
        t[146] = "Unexpected error writing large object to database.";
        t[147] = "\u5c06\u5927\u578b\u5bf9\u8c61(large object)\u5199\u5165\u6570\u636e\u5e93\u65f6\u53d1\u751f\u4e0d\u660e\u9519\u8bef\u3002";
        t[162] = "Query timeout must be a value greater than or equals to 0.";
        t[163] = "\u67e5\u8be2\u903e\u65f6\u7b49\u5019\u65f6\u95f4\u5fc5\u987b\u5927\u4e8e\u6216\u7b49\u4e8e 0\u3002";
        t[170] = "Unknown type {0}.";
        t[171] = "\u4e0d\u660e\u7684\u7c7b\u578b {0}";
        t[174] = "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.";
        t[175] = "\u8fd9\u670d\u52a1\u5668\u7684 standard_conforming_strings \u53c2\u6570\u5df2\u56de\u62a5\u4e3a {0}\uff0cJDBC \u9a71\u52a8\u7a0b\u5e8f\u5df2\u9884\u671f\u5f00\u542f\u6216\u662f\u5173\u95ed\u3002";
        t[176] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[177] = "\u53d1\u73b0\u4e0d\u5408\u6cd5\u7684\u5b57\u5143\uff0c\u53ef\u80fd\u7684\u539f\u56e0\u662f\u6b32\u50a8\u5b58\u7684\u6570\u636e\u4e2d\u5305\u542b\u6570\u636e\u5e93\u7684\u5b57\u5143\u96c6\u4e0d\u652f\u63f4\u7684\u5b57\u7801\uff0c\u5176\u4e2d\u6700\u5e38\u89c1\u4f8b\u5b50\u7684\u5c31\u662f\u5c06 8 \u4f4d\u5143\u6570\u636e\u5b58\u5165\u4f7f\u7528 SQL_ASCII \u7f16\u7801\u7684\u6570\u636e\u5e93\u4e2d\u3002";
        t[178] = "The column index is out of range: {0}, number of columns: {1}.";
        t[179] = "\u680f\u4f4d\u7d22\u5f15\u8d85\u8fc7\u8bb8\u53ef\u8303\u56f4\uff1a{0}\uff0c\u680f\u4f4d\u6570\uff1a{1}\u3002";
        t[180] = "The connection attempt failed.";
        t[181] = "\u5c1d\u8bd5\u8fde\u7ebf\u5df2\u5931\u8d25\u3002";
        t[182] = "No value specified for parameter {0}.";
        t[183] = "\u672a\u8bbe\u5b9a\u53c2\u6570\u503c {0} \u7684\u5185\u5bb9\u3002";
        t[190] = "Provided Reader failed.";
        t[191] = "\u63d0\u4f9b\u7684 Reader \u5df2\u5931\u8d25\u3002";
        t[194] = "Unsupported value for stringtype parameter: {0}";
        t[195] = "\u5b57\u7b26\u7c7b\u578b\u53c2\u6570\u503c\u672a\u88ab\u652f\u6301\uff1a{0}";
        t[198] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[199] = "\u5df2\u7ecf\u5ba3\u544a CallableStatement \u51fd\u5f0f\uff0c\u4f46\u662f\u5c1a\u672a\u547c\u53eb registerOutParameter (1, <some_type>) \u3002";
        t[204] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[205] = "\u4e0d\u80fd\u5728 ResultSet \u7684\u7b2c\u4e00\u7b14\u6570\u636e\u4e4b\u524d\u547c\u53eb deleteRow()\u3002";
        t[214] = "The maximum field size must be a value greater than or equal to 0.";
        t[215] = "\u6700\u5927\u680f\u4f4d\u5bb9\u91cf\u5fc5\u987b\u5927\u4e8e\u6216\u7b49\u4e8e 0\u3002";
        t[216] = "Fetch size must be a value greater to or equal to 0.";
        t[217] = "\u6570\u636e\u8bfb\u53d6\u7b14\u6570(fetch size)\u5fc5\u987b\u5927\u4e8e\u6216\u7b49\u4e8e 0\u3002";
        t[220] = "PostgreSQL LOBs can only index to: {0}";
        t[221] = "PostgreSQL LOBs \u4ec5\u80fd\u7d22\u5f15\u5230\uff1a{0}";
        t[224] = "The JVM claims not to support the encoding: {0}";
        t[225] = "JVM \u58f0\u660e\u5e76\u4e0d\u652f\u63f4\u7f16\u7801\uff1a{0} \u3002";
        t[226] = "Interval {0} not yet implemented";
        t[227] = "\u9694\u7edd {0} \u5c1a\u672a\u88ab\u5b9e\u4f5c\u3002";
        t[238] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[239] = "Fastpath \u547c\u53eb {0} - \u6ca1\u6709\u4f20\u56de\u503c\uff0c\u4e14\u5e94\u8be5\u4f20\u56de\u4e00\u4e2a\u6574\u6570\u3002";
        t[246] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[247] = "ResultSets \u4e0e\u5e76\u53d1\u540c\u4f5c(Concurrency) CONCUR_READ_ONLY \u4e0d\u80fd\u88ab\u66f4\u65b0\u3002";
        t[250] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[251] = "\u8fd9\u4e2a statement \u672a\u5ba3\u544a OUT \u53c2\u6570\uff0c\u4f7f\u7528 '{' ?= call ... '}' \u5ba3\u544a\u4e00\u4e2a\u3002";
        t[256] = "Cannot reference a savepoint after it has been released.";
        t[257] = "\u65e0\u6cd5\u53c2\u7167\u5df2\u7ecf\u88ab\u91ca\u653e\u7684\u50a8\u5b58\u70b9\u3002";
        t[260] = "Unsupported Types value: {0}";
        t[261] = "\u672a\u88ab\u652f\u6301\u7684\u7c7b\u578b\u503c\uff1a{0}";
        t[266] = "Protocol error.  Session setup failed.";
        t[267] = "\u901a\u8baf\u534f\u5b9a\u9519\u8bef\uff0cSession \u521d\u59cb\u5316\u5931\u8d25\u3002";
        t[274] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[275] = "\u4e0d\u80fd\u5728 ResultSet \u7684\u6700\u540e\u4e00\u7b14\u6570\u636e\u4e4b\u540e\u547c\u53eb deleteRow()\u3002";
        t[278] = "Internal Position: {0}";
        t[279] = "\u5185\u90e8\u4f4d\u7f6e\uff1a{0}";
        t[280] = "Zero bytes may not occur in identifiers.";
        t[281] = "\u5728\u6807\u8bc6\u8bc6\u522b\u7b26\u4e2d\u4e0d\u5b58\u5728\u96f6\u4f4d\u5143\u7ec4\u3002";
        t[288] = "{0} function doesn''t take any argument.";
        t[289] = "{0} \u51fd\u5f0f\u65e0\u6cd5\u53d6\u5f97\u4efb\u4f55\u7684\u5f15\u6570\u3002";
        t[300] = "This statement has been closed.";
        t[301] = "\u8fd9\u4e2a statement \u5df2\u7ecf\u88ab\u5173\u95ed\u3002";
        t[318] = "Cannot establish a savepoint in auto-commit mode.";
        t[319] = "\u5728\u81ea\u52a8\u786e\u8ba4\u4e8b\u7269\u4ea4\u6613\u6a21\u5f0f\u65e0\u6cd5\u5efa\u7acb\u50a8\u5b58\u70b9(Savepoint)\u3002";
        t[320] = "Position: {0}";
        t[321] = "\u4f4d\u7f6e\uff1a{0}";
        t[322] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[323] = "\u4e0d\u53ef\u66f4\u65b0\u7684 ResultSet\u3002\u7528\u6765\u4ea7\u751f\u8fd9\u4e2a ResultSet \u7684 SQL \u547d\u4ee4\u53ea\u80fd\u64cd\u4f5c\u4e00\u4e2a\u6570\u636e\u8868\uff0c\u5e76\u4e14\u5fc5\u9700\u9009\u62e9\u6240\u6709\u4e3b\u952e\u680f\u4f4d\uff0c\u8be6\u7ec6\u8bf7\u53c2\u9605 JDBC 2.1 API \u89c4\u683c\u4e66 5.6 \u8282\u3002";
        t[330] = "This ResultSet is closed.";
        t[331] = "\u8fd9\u4e2a ResultSet \u5df2\u7ecf\u88ab\u5173\u95ed\u3002";
        t[338] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[339] = "\u5df2\u6ce8\u518c\u53c2\u6570\u7c7b\u578b {0}\uff0c\u4f46\u662f\u53c8\u547c\u53eb\u4e86get{1}(sqltype={2})\u3002";
        t[342] = "Transaction isolation level {0} not supported.";
        t[343] = "\u4e0d\u652f\u63f4\u4ea4\u6613\u9694\u7edd\u7b49\u7ea7 {0} \u3002";
        t[344] = "Statement has been closed.";
        t[345] = "Sstatement \u5df2\u7ecf\u88ab\u5173\u95ed\u3002";
        t[352] = "Server SQLState: {0}";
        t[353] = "\u670d\u52a1\u5668 SQLState\uff1a{0}";
        t[354] = "No primary key found for table {0}.";
        t[355] = "{0} \u6570\u636e\u8868\u4e2d\u672a\u627e\u5230\u4e3b\u952e(Primary key)\u3002";
        t[362] = "Cannot convert an instance of {0} to type {1}";
        t[363] = "\u65e0\u6cd5\u8f6c\u6362 {0} \u5230\u7c7b\u578b {1} \u7684\u5b9e\u4f8b";
        t[364] = "DataSource has been closed.";
        t[365] = "DataSource \u5df2\u7ecf\u88ab\u5173\u95ed\u3002";
        t[368] = "The column name {0} was not found in this ResultSet.";
        t[369] = "ResultSet \u4e2d\u627e\u4e0d\u5230\u680f\u4f4d\u540d\u79f0 {0}\u3002";
        t[372] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[373] = "\u67e5\u8be2\u7ed3\u679c\u6307\u6807\u4f4d\u7f6e\u4e0d\u6b63\u786e\uff0c\u60a8\u4e5f\u8bb8\u9700\u8981\u547c\u53eb ResultSet \u7684 next() \u65b9\u6cd5\u3002";
        t[378] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[379] = "\u65e0\u6cd5\u66f4\u65b0 ResultSet\uff0c\u53ef\u80fd\u5728\u7b2c\u4e00\u7b14\u6570\u636e\u4e4b\u524d\u6216\u6700\u672a\u7b14\u6570\u636e\u4e4b\u540e\u3002";
        t[380] = "Method {0} is not yet implemented.";
        t[381] = "\u8fd9\u4e2a {0} \u65b9\u6cd5\u5c1a\u672a\u88ab\u5b9e\u4f5c\u3002";
        t[382] = "{0} function takes two or three arguments.";
        t[383] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e8c\u4e2a\u6216\u4e09\u4e2a\u5f15\u6570\u3002";
        t[384] = "The JVM claims not to support the {0} encoding.";
        t[385] = "JVM \u58f0\u660e\u5e76\u4e0d\u652f\u63f4 {0} \u7f16\u7801\u3002";
        t[396] = "Unknown Response Type {0}.";
        t[397] = "\u4e0d\u660e\u7684\u56de\u5e94\u7c7b\u578b {0}\u3002";
        t[398] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[399] = "\u53c2\u6570\u7d22\u5f15\u8d85\u51fa\u8bb8\u53ef\u8303\u56f4\uff1a{0}\uff0c\u53c2\u6570\u603b\u6570\uff1a{1}\u3002";
        t[400] = "Where: {0}";
        t[401] = "\u5728\u4f4d\u7f6e\uff1a{0}";
        t[406] = "Cannot call deleteRow() when on the insert row.";
        t[407] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u6570\u636e\u4e0a\u547c\u53eb deleteRow()\u3002";
        t[414] = "{0} function takes four and only four argument.";
        t[415] = "{0} \u51fd\u5f0f\u53d6\u5f97\u56db\u4e2a\u4e14\u4ec5\u6709\u56db\u4e2a\u5f15\u6570\u3002";
        t[416] = "Unable to translate data into the desired encoding.";
        t[417] = "\u65e0\u6cd5\u5c06\u6570\u636e\u8f6c\u6210\u76ee\u6807\u7f16\u7801\u3002";
        t[424] = "Can''t use relative move methods while on the insert row.";
        t[425] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u6570\u636e\u5217\u4e0a\u4f7f\u7528\u76f8\u5bf9\u4f4d\u7f6e move \u65b9\u6cd5\u3002";
        t[434] = "Invalid stream length {0}.";
        t[435] = "\u65e0\u6548\u7684\u4e32\u6d41\u957f\u5ea6 {0}.";
        t[436] = "The driver currently does not support COPY operations.";
        t[437] = "\u9a71\u52a8\u7a0b\u5e8f\u76ee\u524d\u4e0d\u652f\u63f4 COPY \u64cd\u4f5c\u3002";
        t[440] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[441] = "\u6700\u5927\u6570\u636e\u8bfb\u53d6\u7b14\u6570\u5fc5\u987b\u5927\u4e8e\u6216\u7b49\u4e8e 0\u3002";
        t[446] = "Failed to create object for: {0}.";
        t[447] = "\u4e3a {0} \u5efa\u7acb\u5bf9\u8c61\u5931\u8d25\u3002";
        t[448] = "{0} function takes three and only three arguments.";
        t[449] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e09\u4e2a\u4e14\u4ec5\u6709\u4e09\u4e2a\u5f15\u6570\u3002";
        t[450] = "Conversion of interval failed";
        t[451] = "\u9694\u7edd(Interval)\u8f6c\u6362\u5931\u8d25\u3002";
        t[452] = "Cannot tell if path is open or closed: {0}.";
        t[453] = "\u65e0\u6cd5\u5f97\u77e5 path \u662f\u5f00\u542f\u6216\u5173\u95ed\uff1a{0}\u3002";
        t[460] = "Provided InputStream failed.";
        t[461] = "\u63d0\u4f9b\u7684 InputStream \u5df2\u5931\u8d25\u3002";
        t[462] = "Invalid fetch direction constant: {0}.";
        t[463] = "\u65e0\u6548\u7684 fetch \u65b9\u5411\u5e38\u6570\uff1a{0}\u3002";
        t[472] = "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}";
        t[473] = "\u4e8b\u7269\u4ea4\u6613\u9694\u7edd(Transaction interleaving)\u672a\u88ab\u5b9e\u4f5c\u3002xid={0}, currentXid={1}, state={2}, flags={3}";
        t[474] = "{0} function takes two and only two arguments.";
        t[475] = "{0} \u51fd\u5f0f\u53d6\u5f97\u4e8c\u4e2a\u4e14\u4ec5\u6709\u4e8c\u4e2a\u5f15\u6570\u3002";
        t[476] = "There are no rows in this ResultSet.";
        t[477] = "ResultSet \u4e2d\u627e\u4e0d\u5230\u6570\u636e\u5217\u3002";
        t[478] = "Zero bytes may not occur in string parameters.";
        t[479] = "\u5b57\u7b26\u53c2\u6570\u4e0d\u80fd\u6709 0 \u4e2a\u4f4d\u5143\u7ec4\u3002";
        t[480] = "Cannot call updateRow() when on the insert row.";
        t[481] = "\u4e0d\u80fd\u5728\u65b0\u589e\u7684\u6570\u636e\u5217\u4e0a\u547c\u53eb deleteRow()\u3002";
        t[482] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[483] = "Connection \u5df2\u81ea\u52a8\u7ed3\u675f\uff0c\u56e0\u4e3a\u4e00\u4e2a\u65b0\u7684  PooledConnection \u8fde\u7ebf\u88ab\u5f00\u542f\u6216\u8005\u6216 PooledConnection \u5df2\u88ab\u5173\u95ed\u3002";
        t[488] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[489] = "\u4e00\u4e2a CallableStatement \u6267\u884c\u51fd\u5f0f\u540e\u8f93\u51fa\u7684\u53c2\u6570\u7c7b\u578b\u4e3a {1} \u503c\u4e3a {0}\uff0c\u4f46\u662f\u5df2\u6ce8\u518c\u7684\u7c7b\u578b\u662f {2}\u3002";
        t[494] = "Cannot cast an instance of {0} to type {1}";
        t[495] = "\u4e0d\u80fd\u8f6c\u6362\u4e00\u4e2a {0} \u5b9e\u4f8b\u5230\u7c7b\u578b {1}";
        t[498] = "Cannot retrieve the id of a named savepoint.";
        t[499] = "\u65e0\u6cd5\u53d6\u5f97\u5df2\u547d\u540d\u50a8\u5b58\u70b9\u7684 id\u3002";
        t[500] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[501] = "\u4e0d\u80fd\u5728\u4e8b\u7269\u4ea4\u6613\u8fc7\u7a0b\u4e2d\u6539\u53d8\u4e8b\u7269\u4ea4\u6613\u552f\u8bfb\u5c5e\u6027\u3002";
        t[502] = "The server does not support SSL.";
        t[503] = "\u670d\u52a1\u5668\u4e0d\u652f\u63f4 SSL \u8fde\u7ebf\u3002";
        t[510] = "A connection could not be made using the requested protocol {0}.";
        t[511] = "\u65e0\u6cd5\u4ee5\u8981\u6c42\u7684\u901a\u8baf\u534f\u5b9a {0} \u5efa\u7acb\u8fde\u7ebf\u3002";
        t[512] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[513] = "\u4e0d\u652f\u63f4 {0} \u9a8c\u8bc1\u7c7b\u578b\u3002\u8bf7\u6838\u5bf9\u60a8\u5df2\u7ecf\u7ec4\u6001 pg_hba.conf \u6587\u4ef6\u5305\u542b\u5ba2\u6237\u7aef\u7684IP\u4f4d\u5740\u6216\u7f51\u8def\u533a\u6bb5\uff0c\u4ee5\u53ca\u9a71\u52a8\u7a0b\u5e8f\u6240\u652f\u63f4\u7684\u9a8c\u8bc1\u67b6\u6784\u6a21\u5f0f\u5df2\u88ab\u652f\u63f4\u3002";
        t[514] = "Malformed function or procedure escape syntax at offset {0}.";
        t[515] = "\u4e0d\u6b63\u786e\u7684\u51fd\u5f0f\u6216\u7a0b\u5e8f escape \u8bed\u6cd5\u4e8e {0}\u3002";
        t[516] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[517] = "\u8fd9\u670d\u52a1\u5668\u7684 DateStyle \u53c2\u6570\u88ab\u66f4\u6539\u6210 {0}\uff0cJDBC \u9a71\u52a8\u7a0b\u5e8f\u8bf7\u6c42\u9700\u8981 DateStyle \u4ee5 ISO \u5f00\u5934\u4ee5\u6b63\u786e\u5de5\u4f5c\u3002";
        t[518] = "No results were returned by the query.";
        t[519] = "\u67e5\u8be2\u6ca1\u6709\u4f20\u56de\u4efb\u4f55\u7ed3\u679c\u3002";
        t[520] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[521] = "\u4f4d\u7f6e\uff1a\u6587\u4ef6\uff1a{0}\uff0c\u5e38\u5f0f\uff1a{1}\uff0c\u884c\uff1a{2}";
        t[526] = "Hint: {0}";
        t[527] = "\u5efa\u8bae\uff1a{0}";
        t[528] = "A CallableStatement was executed with nothing returned.";
        t[529] = "\u4e00\u4e2a CallableStatement \u6267\u884c\u51fd\u5f0f\u540e\u6ca1\u6709\u4f20\u56de\u503c\u3002";
        t[530] = "Unknown ResultSet holdability setting: {0}.";
        t[531] = "\u672a\u77e5\u7684 ResultSet \u53ef\u9002\u7528\u7684\u8bbe\u7f6e\uff1a{0}\u3002";
        t[540] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[541] = "\u4e0d\u80fd\u5728\u4e8b\u52a1\u4ea4\u6613\u8fc7\u7a0b\u4e2d\u6539\u53d8\u4e8b\u7269\u4ea4\u6613\u9694\u7edd\u7b49\u7ea7\u3002";
        t[544] = "The fastpath function {0} is unknown.";
        t[545] = "\u4e0d\u660e\u7684 fastpath \u51fd\u5f0f {0}\u3002";
        t[546] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[547] = "\u5728 PreparedStatement \u4e0a\u4e0d\u80fd\u4f7f\u7528\u83b7\u53d6\u67e5\u8be2\u5b57\u7b26\u7684\u67e5\u8be2\u65b9\u6cd5\u3002";
        t[556] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[557] = "\u64cd\u4f5c\u8981\u6c42\u53ef\u5377\u52a8\u7684 ResultSet\uff0c\u4f46\u6b64 ResultSet \u662f FORWARD_ONLY\u3002";
        t[564] = "Unknown Types value.";
        t[565] = "\u4e0d\u660e\u7684\u7c7b\u578b\u503c\u3002";
        t[570] = "Large Objects may not be used in auto-commit mode.";
        t[571] = "\u5927\u578b\u5bf9\u8c61\u65e0\u6cd5\u88ab\u4f7f\u7528\u5728\u81ea\u52a8\u786e\u8ba4\u4e8b\u7269\u4ea4\u6613\u6a21\u5f0f\u3002";
        table = t;
    }
}

