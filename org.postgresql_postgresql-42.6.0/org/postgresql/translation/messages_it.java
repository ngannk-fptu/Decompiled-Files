/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_it
extends ResourceBundle {
    private static final String[] table;

    @Override
    public Object handleGetObject(String msgid) throws MissingResourceException {
        String found;
        int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 397 << 1;
        String found2 = table[idx];
        if (found2 == null) {
            return null;
        }
        if (msgid.equals(found2)) {
            return table[idx + 1];
        }
        int incr = hash_val % 395 + 1 << 1;
        do {
            if ((idx += incr) >= 794) {
                idx -= 794;
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
                while (this.idx < 794 && table[this.idx] == null) {
                    this.idx += 2;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return this.idx < 794;
            }

            public Object nextElement() {
                String key = table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 794 && table[this.idx] == null);
                return key;
            }
        };
    }

    public ResourceBundle getParent() {
        return this.parent;
    }

    static {
        String[] t = new String[794];
        t[0] = "";
        t[1] = "Project-Id-Version: PostgreSQL JDBC Driver 8.2\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2006-06-23 17:25+0200\nLast-Translator: Giuseppe Sacco <eppesuig@debian.org>\nLanguage-Team: Italian <tp@lists.linux.it>\nLanguage: it\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\n";
        t[4] = "DataSource has been closed.";
        t[5] = "Questo \u00abDataSource\u00bb \u00e8 stato chiuso.";
        t[18] = "Where: {0}";
        t[19] = "Dove: {0}";
        t[26] = "The connection attempt failed.";
        t[27] = "Il tentativo di connessione \u00e8 fallito.";
        t[28] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[29] = "La posizione attuale \u00e8 successiva alla fine del ResultSet. Non \u00e8 possibile invocare \u00abdeleteRow()\u00bb qui.";
        t[32] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[33] = "Non si possono utilizzare i metodi \"query\" che hanno come argomento una stringa nel caso di \u00abPreparedStatement\u00bb.";
        t[36] = "Multiple ResultSets were returned by the query.";
        t[37] = "La query ha restituito \u00abResultSet\u00bb multipli.";
        t[50] = "Too many update results were returned.";
        t[51] = "Sono stati restituiti troppi aggiornamenti.";
        t[58] = "Illegal UTF-8 sequence: initial byte is {0}: {1}";
        t[59] = "Sequenza UTF-8 illegale: il byte iniziale \u00e8 {0}: {1}";
        t[66] = "The column name {0} was not found in this ResultSet.";
        t[67] = "Colonna denominata \u00ab{0}\u00bb non \u00e8 presente in questo \u00abResultSet\u00bb.";
        t[70] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[71] = "Chiamata Fastpath \u00ab{0}\u00bb: Nessun risultato restituito mentre ci si aspettava un intero.";
        t[74] = "Protocol error.  Session setup failed.";
        t[75] = "Errore di protocollo. Impostazione della sessione fallita.";
        t[76] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[77] = "\u00c8 stato definito un \u00abCallableStatement\u00bb ma non \u00e8 stato invocato il metodo \u00abregisterOutParameter(1, <tipo>)\u00bb.";
        t[78] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[79] = "I \u00abResultSet\u00bb in modalit\u00e0 CONCUR_READ_ONLY non possono essere aggiornati.";
        t[90] = "LOB positioning offsets start at 1.";
        t[91] = "L''offset per la posizione dei LOB comincia da 1.";
        t[92] = "Internal Position: {0}";
        t[93] = "Posizione interna: {0}";
        t[100] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[101] = "Non \u00e8 possibile modificare la propriet\u00e0 \u00abread-only\u00bb delle transazioni nel mezzo di una transazione.";
        t[102] = "The JVM claims not to support the {0} encoding.";
        t[103] = "La JVM sostiene di non supportare la codifica {0}.";
        t[108] = "{0} function doesn''t take any argument.";
        t[109] = "Il metodo \u00ab{0}\u00bb non accetta argomenti.";
        t[112] = "xid must not be null";
        t[113] = "xid non pu\u00f2 essere NULL";
        t[114] = "Connection has been closed.";
        t[115] = "Questo \u00abConnection\u00bb \u00e8 stato chiuso.";
        t[122] = "The server does not support SSL.";
        t[123] = "Il server non supporta SSL.";
        t[140] = "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}";
        t[141] = "Sequenza UTF-8 illegale: il byte {0} di una sequenza di {1} byte non \u00e8 10xxxxxx: {2}";
        t[148] = "Hint: {0}";
        t[149] = "Suggerimento: {0}";
        t[152] = "Unable to find name datatype in the system catalogs.";
        t[153] = "Non \u00e8 possibile trovare il datatype \u00abname\u00bb nel catalogo di sistema.";
        t[156] = "Unsupported Types value: {0}";
        t[157] = "Valore di tipo \u00ab{0}\u00bb non supportato.";
        t[158] = "Unknown type {0}.";
        t[159] = "Tipo sconosciuto {0}.";
        t[166] = "{0} function takes two and only two arguments.";
        t[167] = "Il metodo \u00ab{0}\u00bb accetta due e solo due argomenti.";
        t[170] = "Finalizing a Connection that was never closed:";
        t[171] = "Finalizzazione di una \u00abConnection\u00bb che non \u00e8 stata chiusa.";
        t[186] = "PostgreSQL LOBs can only index to: {0}";
        t[187] = "Il massimo valore per l''indice dei LOB di PostgreSQL \u00e8 {0}. ";
        t[194] = "Method {0} is not yet implemented.";
        t[195] = "Il metodo \u00ab{0}\u00bb non \u00e8 stato ancora implementato.";
        t[198] = "Error loading default settings from driverconfig.properties";
        t[199] = "Si \u00e8 verificato un errore caricando le impostazioni predefinite da \u00abdriverconfig.properties\u00bb.";
        t[202] = "Large Objects may not be used in auto-commit mode.";
        t[203] = "Non \u00e8 possibile impostare i \u00abLarge Object\u00bb in modalit\u00e0 \u00abauto-commit\u00bb.";
        t[208] = "Expected command status BEGIN, got {0}.";
        t[209] = "Lo stato del comando avrebbe dovuto essere BEGIN, mentre invece \u00e8 {0}.";
        t[218] = "Invalid fetch direction constant: {0}.";
        t[219] = "Costante per la direzione dell''estrazione non valida: {0}.";
        t[222] = "{0} function takes three and only three arguments.";
        t[223] = "Il metodo \u00ab{0}\u00bb accetta tre e solo tre argomenti.";
        t[226] = "Error during recover";
        t[227] = "Errore durante il ripristino";
        t[228] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[229] = "Non \u00e8 possibile aggiornare il \u00abResultSet\u00bb perch\u00e9 la posizione attuale \u00e8 precedente all''inizio o successiva alla file dei risultati.";
        t[232] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[233] = "\u00c8 stato definito il parametro di tipo \u00ab{0}\u00bb, ma poi \u00e8 stato invocato il metodo \u00abget{1}()\u00bb (sqltype={2}).";
        t[240] = "Cannot establish a savepoint in auto-commit mode.";
        t[241] = "Non \u00e8 possibile impostare i punti di ripristino in modalit\u00e0 \u00abauto-commit\u00bb.";
        t[242] = "Cannot retrieve the id of a named savepoint.";
        t[243] = "Non \u00e8 possibile trovare l''id del punto di ripristino indicato.";
        t[244] = "The column index is out of range: {0}, number of columns: {1}.";
        t[245] = "Indice di colonna, {0}, \u00e8 maggiore del numero di colonne {1}.";
        t[250] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[251] = "Qualcosa di insolito si \u00e8 verificato causando il fallimento del driver. Per favore riferire all''autore del driver questa eccezione.";
        t[260] = "Cannot cast an instance of {0} to type {1}";
        t[261] = "Non \u00e8 possibile fare il cast di una istanza di \u00ab{0}\u00bb al tipo \u00ab{1}\u00bb.";
        t[264] = "Unknown Types value.";
        t[265] = "Valore di tipo sconosciuto.";
        t[266] = "Invalid stream length {0}.";
        t[267] = "La dimensione specificata, {0}, per lo \u00abstream\u00bb non \u00e8 valida.";
        t[272] = "Cannot retrieve the name of an unnamed savepoint.";
        t[273] = "Non \u00e8 possibile trovare il nome di un punto di ripristino anonimo.";
        t[274] = "Unable to translate data into the desired encoding.";
        t[275] = "Impossibile tradurre i dati nella codifica richiesta.";
        t[276] = "Expected an EOF from server, got: {0}";
        t[277] = "Ricevuto dal server \u00ab{0}\u00bb mentre era atteso un EOF";
        t[278] = "Bad value for type {0} : {1}";
        t[279] = "Il valore \u00ab{1}\u00bb non \u00e8 adeguato al tipo \u00ab{0}\u00bb.";
        t[280] = "The server requested password-based authentication, but no password was provided.";
        t[281] = "Il server ha richiesto l''autenticazione con password, ma tale password non \u00e8 stata fornita.";
        t[298] = "This PooledConnection has already been closed.";
        t[299] = "Questo \u00abPooledConnection\u00bb \u00e8 stato chiuso.";
        t[306] = "Fetch size must be a value greater to or equal to 0.";
        t[307] = "La dimensione dell''area di \u00abfetch\u00bb deve essere maggiore o eguale a 0.";
        t[312] = "A connection could not be made using the requested protocol {0}.";
        t[313] = "Non \u00e8 stato possibile attivare la connessione utilizzando il protocollo richiesto {0}.";
        t[322] = "There are no rows in this ResultSet.";
        t[323] = "Non ci sono righe in questo \u00abResultSet\u00bb.";
        t[324] = "Unexpected command status: {0}.";
        t[325] = "Stato del comando non previsto: {0}.";
        t[334] = "Not on the insert row.";
        t[335] = "Non si \u00e8 in una nuova riga.";
        t[344] = "Server SQLState: {0}";
        t[345] = "SQLState del server: {0}";
        t[360] = "The driver currently does not support COPY operations.";
        t[361] = "Il driver non supporta al momento l''operazione \u00abCOPY\u00bb.";
        t[364] = "The array index is out of range: {0}, number of elements: {1}.";
        t[365] = "L''indice dell''array \u00e8 fuori intervallo: {0}, numero di elementi: {1}.";
        t[374] = "suspend/resume not implemented";
        t[375] = "\u00absuspend\u00bb/\u00abresume\u00bb non implementato";
        t[378] = "Not implemented: one-phase commit must be issued using the same connection that was used to start it";
        t[379] = "Non implementato: il commit \"one-phase\" deve essere invocato sulla stessa connessione che ha iniziato la transazione.";
        t[398] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[399] = "Non \u00e8 possibile invocare \u00abcancelRowUpdates()\u00bb durante l''inserimento di una riga.";
        t[400] = "Cannot reference a savepoint after it has been released.";
        t[401] = "Non \u00e8 possibile utilizzare un punto di ripristino successivamente al suo rilascio.";
        t[402] = "You must specify at least one column value to insert a row.";
        t[403] = "Per inserire un record si deve specificare almeno il valore di una colonna.";
        t[404] = "Unable to determine a value for MaxIndexKeys due to missing system catalog data.";
        t[405] = "Non \u00e8 possibile trovare il valore di \u00abMaxIndexKeys\u00bb nel catalogo si sistema.";
        t[412] = "The JVM claims not to support the encoding: {0}";
        t[413] = "La JVM sostiene di non supportare la codifica: {0}.";
        t[414] = "{0} function takes two or three arguments.";
        t[415] = "Il metodo \u00ab{0}\u00bb accetta due o tre argomenti.";
        t[440] = "Unexpected error writing large object to database.";
        t[441] = "Errore inatteso inviando un \u00ablarge object\u00bb al database.";
        t[442] = "Zero bytes may not occur in string parameters.";
        t[443] = "Byte con valore zero non possono essere contenuti nei parametri stringa.";
        t[444] = "A result was returned when none was expected.";
        t[445] = "\u00c8 stato restituito un valore nonostante non ne fosse atteso nessuno.";
        t[450] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[451] = "Il \u00abResultSet\u00bb non \u00e8 aggiornabile. La query che lo genera deve selezionare una sola tabella e deve selezionarne tutti i campi che ne compongono la chiave primaria. Si vedano le specifiche dell''API JDBC 2.1, sezione 5.6, per ulteriori dettagli.";
        t[454] = "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.";
        t[455] = "Il messaggio di \u00abbind\u00bb \u00e8 troppo lungo ({0}). Questo pu\u00f2 essere causato da una dimensione eccessiva o non corretta dei parametri dell''\u00abInputStream\u00bb.";
        t[460] = "Statement has been closed.";
        t[461] = "Questo \u00abStatement\u00bb \u00e8 stato chiuso.";
        t[462] = "No value specified for parameter {0}.";
        t[463] = "Nessun valore specificato come parametro {0}.";
        t[468] = "The array index is out of range: {0}";
        t[469] = "Indice di colonna fuori dall''intervallo ammissibile: {0}";
        t[474] = "Unable to bind parameter values for statement.";
        t[475] = "Impossibile fare il \u00abbind\u00bb dei valori passati come parametri per lo statement.";
        t[476] = "Can''t refresh the insert row.";
        t[477] = "Non \u00e8 possibile aggiornare la riga in inserimento.";
        t[480] = "No primary key found for table {0}.";
        t[481] = "Non \u00e8 stata trovata la chiave primaria della tabella \u00ab{0}\u00bb.";
        t[482] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[483] = "Non \u00e8 possibile cambiare il livello di isolamento delle transazioni nel mezzo di una transazione.";
        t[498] = "Provided InputStream failed.";
        t[499] = "L''\u00abInputStream\u00bb fornito \u00e8 fallito.";
        t[500] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[501] = "Il parametro indice \u00e8 fuori intervallo: {0}, numero di elementi: {1}.";
        t[502] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[503] = "Il parametro del server \u00abDateStyle\u00bb \u00e8 stato cambiato in {0}. Il driver JDBC richiede che \u00abDateStyle\u00bb cominci con \u00abISO\u00bb per un corretto funzionamento.";
        t[508] = "Connection attempt timed out.";
        t[509] = "Il tentativo di connessione \u00e8 scaduto.";
        t[512] = "Internal Query: {0}";
        t[513] = "Query interna: {0}";
        t[518] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[519] = "L''autenticazione di tipo {0} non \u00e8 supportata. Verificare che nel file di configurazione pg_hba.conf sia presente l''indirizzo IP o la sottorete del client, e che lo schema di autenticazione utilizzato sia supportato dal driver.";
        t[526] = "Interval {0} not yet implemented";
        t[527] = "L''intervallo \u00ab{0}\u00bb non \u00e8 stato ancora implementato.";
        t[532] = "Conversion of interval failed";
        t[533] = "Fallita la conversione di un \u00abinterval\u00bb.";
        t[540] = "Query timeout must be a value greater than or equals to 0.";
        t[541] = "Il timeout relativo alle query deve essere maggiore o eguale a 0.";
        t[542] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[543] = "La \u00abConnection\u00bb \u00e8 stata chiusa automaticamente perch\u00e9 una nuova l''ha sostituita nello stesso \u00abPooledConnection\u00bb, oppure il \u00abPooledConnection\u00bb \u00e8 stato chiuso.";
        t[544] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[545] = "Il \u00abResultSet\u00bb non \u00e8 correttamente posizionato; forse \u00e8 necessario invocare \u00abnext()\u00bb.";
        t[550] = "This statement has been closed.";
        t[551] = "Questo statement \u00e8 stato chiuso.";
        t[552] = "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.";
        t[553] = "Non \u00e8 possibile identificare il tipo SQL da usare per l''istanza di tipo \u00ab{0}\u00bb. Usare \u00absetObject()\u00bb specificando esplicitamente il tipo da usare per questo valore.";
        t[554] = "Cannot call updateRow() when on the insert row.";
        t[555] = "Non \u00e8 possibile invocare \u00abupdateRow()\u00bb durante l''inserimento di una riga.";
        t[562] = "Detail: {0}";
        t[563] = "Dettaglio: {0}";
        t[566] = "Cannot call deleteRow() when on the insert row.";
        t[567] = "Non \u00e8 possibile invocare \u00abdeleteRow()\u00bb durante l''inserimento di una riga.";
        t[568] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[569] = "La posizione attuale \u00e8 precedente all''inizio del ResultSet. Non \u00e8 possibile invocare \u00abdeleteRow()\u00bb qui.";
        t[576] = "Illegal UTF-8 sequence: final value is a surrogate value: {0}";
        t[577] = "Sequenza UTF-8 illegale: il valore \u00e8 finale \u00e8 un surrogato: {0}";
        t[578] = "Unknown Response Type {0}.";
        t[579] = "Risposta di tipo sconosciuto {0}.";
        t[582] = "Unsupported value for stringtype parameter: {0}";
        t[583] = "Il valore per il parametro di tipo string \u00ab{0}\u00bb non \u00e8 supportato.";
        t[584] = "Conversion to type {0} failed: {1}.";
        t[585] = "Conversione al tipo {0} fallita: {1}.";
        t[586] = "Conversion of money failed.";
        t[587] = "Fallita la conversione di un \u00abmoney\u00bb.";
        t[600] = "Unable to load the class {0} responsible for the datatype {1}";
        t[601] = "Non \u00e8 possibile caricare la class \u00ab{0}\u00bb per gestire il tipo \u00ab{1}\u00bb.";
        t[604] = "The fastpath function {0} is unknown.";
        t[605] = "La funzione fastpath \u00ab{0}\u00bb \u00e8 sconosciuta.";
        t[608] = "Malformed function or procedure escape syntax at offset {0}.";
        t[609] = "Sequenza di escape definita erroneamente nella funzione o procedura all''offset {0}.";
        t[612] = "Provided Reader failed.";
        t[613] = "Il \u00abReader\u00bb fornito \u00e8 fallito.";
        t[614] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[615] = "Il numero massimo di righe deve essere maggiore o eguale a 0.";
        t[616] = "Failed to create object for: {0}.";
        t[617] = "Fallita la creazione dell''oggetto per: {0}.";
        t[622] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[623] = "Il flusso di input \u00e8 stato interrotto, sono arrivati {1} byte al posto dei {0} attesi.";
        t[626] = "An unexpected result was returned by a query.";
        t[627] = "Un risultato inaspettato \u00e8 stato ricevuto dalla query.";
        t[646] = "An error occurred while setting up the SSL connection.";
        t[647] = "Si \u00e8 verificato un errore impostando la connessione SSL.";
        t[654] = "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}";
        t[655] = "Sequenza UTF-8 illegale: {0} byte utilizzati per codificare un valore di {1} byte: {2}";
        t[658] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[659] = "La classe \u00abSSLSocketFactory\u00bb specificata, \u00ab{0}\u00bb, non pu\u00f2 essere istanziata.";
        t[670] = "Position: {0}";
        t[671] = "Posizione: {0}";
        t[676] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[677] = "Individuazione: file: \"{0}\", routine: {1}, linea: {2}";
        t[684] = "Cannot tell if path is open or closed: {0}.";
        t[685] = "Impossibile stabilire se il percorso \u00e8 aperto o chiuso: {0}.";
        t[700] = "Cannot convert an instance of {0} to type {1}";
        t[701] = "Non \u00e8 possibile convertire una istanza di \u00ab{0}\u00bb nel tipo \u00ab{1}\u00bb";
        t[710] = "{0} function takes four and only four argument.";
        t[711] = "Il metodo \u00ab{0}\u00bb accetta quattro e solo quattro argomenti.";
        t[718] = "Interrupted while attempting to connect.";
        t[719] = "Si \u00e8 verificata una interruzione durante il tentativo di connessione.";
        t[722] = "Illegal UTF-8 sequence: final value is out of range: {0}";
        t[723] = "Sequenza UTF-8 illegale: il valore finale \u00e8 fuori dall''intervallo permesso: {0}";
        t[736] = "{0} function takes one and only one argument.";
        t[737] = "Il metodo \u00ab{0}\u00bb accetta un ed un solo argomento.";
        t[744] = "This ResultSet is closed.";
        t[745] = "Questo \u00abResultSet\u00bb \u00e8 chiuso.";
        t[746] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[747] = "Sono stati trovati caratteri non validi tra i dati. Molto probabilmente sono stati memorizzati dei caratteri che non sono validi per la codifica dei caratteri impostata alla creazione del database. Il caso pi\u00f9 diffuso \u00e8 quello nel quale si memorizzano caratteri a 8bit in un database con codifica SQL_ASCII.";
        t[750] = "An I/O error occurred while sending to the backend.";
        t[751] = "Si \u00e8 verificato un errore di I/O nella spedizione di dati al server.";
        t[754] = "Ran out of memory retrieving query results.";
        t[755] = "Fine memoria scaricando i risultati della query.";
        t[756] = "Returning autogenerated keys is not supported.";
        t[757] = "La restituzione di chiavi autogenerate non \u00e8 supportata.";
        t[760] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[761] = "L''operazione richiete un \u00abResultSet\u00bb scorribile mentre questo \u00e8 \u00abFORWARD_ONLY\u00bb.";
        t[762] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[763] = "\u00c8 stato eseguito un \u00abCallableStatement\u00bb ma il parametro in uscita \u00ab{0}\u00bb era di tipo \u00ab{1}\u00bb al posto di \u00ab{2}\u00bb, che era stato dichiarato.";
        t[768] = "Unknown ResultSet holdability setting: {0}.";
        t[769] = "Il parametro \u00abholdability\u00bb per il \u00abResultSet\u00bb \u00e8 sconosciuto: {0}.";
        t[772] = "Transaction isolation level {0} not supported.";
        t[773] = "Il livello di isolamento delle transazioni \u00ab{0}\u00bb non \u00e8 supportato.";
        t[776] = "No results were returned by the query.";
        t[777] = "Nessun risultato \u00e8 stato restituito dalla query.";
        t[778] = "A CallableStatement was executed with nothing returned.";
        t[779] = "Un \u00abCallableStatement\u00bb \u00e8 stato eseguito senza produrre alcun risultato. ";
        t[780] = "The maximum field size must be a value greater than or equal to 0.";
        t[781] = "La dimensione massima del campo deve essere maggiore o eguale a 0.";
        t[786] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[787] = "Questo statement non dichiara il parametro in uscita. Usare \u00ab{ ?= call ... }\u00bb per farlo.";
        t[788] = "Can''t use relative move methods while on the insert row.";
        t[789] = "Non \u00e8 possibile utilizzare gli spostamenti relativi durante l''inserimento di una riga.";
        t[792] = "Connection is busy with another transaction";
        t[793] = "La connessione \u00e8 utilizzata da un''altra transazione";
        table = t;
    }
}

