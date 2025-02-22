/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_fr
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
        t[1] = "Project-Id-Version: head-fr\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2007-07-27 12:27+0200\nLast-Translator: \nLanguage-Team:  <en@li.org>\nLanguage: \nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Generator: KBabel 1.11.4\nPlural-Forms:  nplurals=2; plural=(n > 1);\n";
        t[4] = "DataSource has been closed.";
        t[5] = "DataSource a \u00e9t\u00e9 ferm\u00e9e.";
        t[18] = "Where: {0}";
        t[19] = "O\u00f9\u00a0: {0}";
        t[26] = "The connection attempt failed.";
        t[27] = "La tentative de connexion a \u00e9chou\u00e9.";
        t[28] = "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.";
        t[29] = "Actuellement positionn\u00e9 apr\u00e8s la fin du ResultSet. Vous ne pouvez pas appeler deleteRow() ici.";
        t[32] = "Can''t use query methods that take a query string on a PreparedStatement.";
        t[33] = "Impossible d''utiliser les fonctions de requ\u00eate qui utilisent une cha\u00eene de caract\u00e8res sur un PreparedStatement.";
        t[36] = "Multiple ResultSets were returned by the query.";
        t[37] = "Plusieurs ResultSets ont \u00e9t\u00e9 retourn\u00e9s par la requ\u00eate.";
        t[50] = "Too many update results were returned.";
        t[51] = "Trop de r\u00e9sultats de mise \u00e0 jour ont \u00e9t\u00e9 retourn\u00e9s.";
        t[58] = "Illegal UTF-8 sequence: initial byte is {0}: {1}";
        t[59] = "S\u00e9quence UTF-8 ill\u00e9gale: le premier octet est {0}: {1}";
        t[66] = "The column name {0} was not found in this ResultSet.";
        t[67] = "Le nom de colonne {0} n''a pas \u00e9t\u00e9 trouv\u00e9 dans ce ResultSet.";
        t[70] = "Fastpath call {0} - No result was returned and we expected an integer.";
        t[71] = "Appel Fastpath {0} - Aucun r\u00e9sultat n''a \u00e9t\u00e9 retourn\u00e9 et nous attendions un entier.";
        t[74] = "Protocol error.  Session setup failed.";
        t[75] = "Erreur de protocole. Ouverture de la session en \u00e9chec.";
        t[76] = "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.";
        t[77] = "Un CallableStatement a \u00e9t\u00e9 d\u00e9clar\u00e9, mais aucun appel \u00e0 registerOutParameter(1, <un type>) n''a \u00e9t\u00e9 fait.";
        t[78] = "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.";
        t[79] = "Les ResultSets avec la concurrence CONCUR_READ_ONLY ne peuvent \u00eatre mis \u00e0 jour.";
        t[90] = "LOB positioning offsets start at 1.";
        t[91] = "Les d\u00e9calages de position des LOB commencent \u00e0 1.";
        t[92] = "Internal Position: {0}";
        t[93] = "Position interne\u00a0: {0}";
        t[96] = "free() was called on this LOB previously";
        t[97] = "free() a \u00e9t\u00e9 appel\u00e9e auparavant sur ce LOB";
        t[100] = "Cannot change transaction read-only property in the middle of a transaction.";
        t[101] = "Impossible de changer la propri\u00e9t\u00e9 read-only d''une transaction au milieu d''une transaction.";
        t[102] = "The JVM claims not to support the {0} encoding.";
        t[103] = "La JVM pr\u00e9tend ne pas supporter l''encodage {0}.";
        t[108] = "{0} function doesn''t take any argument.";
        t[109] = "La fonction {0} n''accepte aucun argument.";
        t[112] = "xid must not be null";
        t[113] = "xid ne doit pas \u00eatre nul";
        t[114] = "Connection has been closed.";
        t[115] = "La connexion a \u00e9t\u00e9 ferm\u00e9e.";
        t[122] = "The server does not support SSL.";
        t[123] = "Le serveur ne supporte pas SSL.";
        t[140] = "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}";
        t[141] = "S\u00e9quence UTF-8 ill\u00e9gale: l''octet {0} de la s\u00e9quence d''octet {1} n''est pas 10xxxxxx: {2}";
        t[148] = "Hint: {0}";
        t[149] = "Indice\u00a0: {0}";
        t[152] = "Unable to find name datatype in the system catalogs.";
        t[153] = "Incapable de trouver le type de donn\u00e9e name dans les catalogues syst\u00e8mes.";
        t[156] = "Unsupported Types value: {0}";
        t[157] = "Valeur de type non support\u00e9e\u00a0: {0}";
        t[158] = "Unknown type {0}.";
        t[159] = "Type inconnu\u00a0: {0}.";
        t[166] = "{0} function takes two and only two arguments.";
        t[167] = "La fonction {0} n''accepte que deux et seulement deux arguments.";
        t[170] = "Finalizing a Connection that was never closed:";
        t[171] = "Destruction d''une connection qui n''a jamais \u00e9t\u00e9 ferm\u00e9e:";
        t[180] = "The maximum field size must be a value greater than or equal to 0.";
        t[181] = "La taille maximum des champs doit \u00eatre une valeur sup\u00e9rieure ou \u00e9gale \u00e0 0.";
        t[186] = "PostgreSQL LOBs can only index to: {0}";
        t[187] = "Les LOB PostgreSQL peuvent seulement s''indicer \u00e0: {0}";
        t[194] = "Method {0} is not yet implemented.";
        t[195] = "La fonction {0} n''est pas encore impl\u00e9ment\u00e9e.";
        t[198] = "Error loading default settings from driverconfig.properties";
        t[199] = "Erreur de chargement des valeurs par d\u00e9faut depuis driverconfig.properties";
        t[200] = "Results cannot be retrieved from a CallableStatement before it is executed.";
        t[201] = "Les r\u00e9sultats ne peuvent \u00eatre r\u00e9cup\u00e9r\u00e9s \u00e0 partir d''un CallableStatement avant qu''il ne soit ex\u00e9cut\u00e9.";
        t[202] = "Large Objects may not be used in auto-commit mode.";
        t[203] = "Les Large Objects ne devraient pas \u00eatre utilis\u00e9s en mode auto-commit.";
        t[208] = "Expected command status BEGIN, got {0}.";
        t[209] = "Attendait le statut de commande BEGIN, obtenu {0}.";
        t[218] = "Invalid fetch direction constant: {0}.";
        t[219] = "Constante de direction pour la r\u00e9cup\u00e9ration invalide\u00a0: {0}.";
        t[222] = "{0} function takes three and only three arguments.";
        t[223] = "La fonction {0} n''accepte que trois et seulement trois arguments.";
        t[226] = "Error during recover";
        t[227] = "Erreur durant la restauration";
        t[228] = "Cannot update the ResultSet because it is either before the start or after the end of the results.";
        t[229] = "Impossible de mettre \u00e0 jour le ResultSet car c''est soit avant le d\u00e9but ou apr\u00e8s la fin des r\u00e9sultats.";
        t[232] = "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.";
        t[233] = "Un param\u00e8tre de type {0} a \u00e9t\u00e9 enregistr\u00e9, mais un appel \u00e0 get{1} (sqltype={2}) a \u00e9t\u00e9 fait.";
        t[240] = "Cannot establish a savepoint in auto-commit mode.";
        t[241] = "Impossible d''\u00e9tablir un savepoint en mode auto-commit.";
        t[242] = "Cannot retrieve the id of a named savepoint.";
        t[243] = "Impossible de retrouver l''identifiant d''un savepoint nomm\u00e9.";
        t[244] = "The column index is out of range: {0}, number of columns: {1}.";
        t[245] = "L''indice de la colonne est hors limite\u00a0: {0}, nombre de colonnes\u00a0: {1}.";
        t[250] = "Something unusual has occurred to cause the driver to fail. Please report this exception.";
        t[251] = "Quelque chose d''inhabituel a provoqu\u00e9 l''\u00e9chec du pilote. Veuillez faire un rapport sur cette erreur.";
        t[260] = "Cannot cast an instance of {0} to type {1}";
        t[261] = "Impossible de convertir une instance de {0} vers le type {1}";
        t[264] = "Unknown Types value.";
        t[265] = "Valeur de Types inconnue.";
        t[266] = "Invalid stream length {0}.";
        t[267] = "Longueur de flux invalide {0}.";
        t[272] = "Cannot retrieve the name of an unnamed savepoint.";
        t[273] = "Impossible de retrouver le nom d''un savepoint sans nom.";
        t[274] = "Unable to translate data into the desired encoding.";
        t[275] = "Impossible de traduire les donn\u00e9es dans l''encodage d\u00e9sir\u00e9.";
        t[276] = "Expected an EOF from server, got: {0}";
        t[277] = "Attendait une fin de fichier du serveur, re\u00e7u: {0}";
        t[278] = "Bad value for type {0} : {1}";
        t[279] = "Mauvaise valeur pour le type {0}\u00a0: {1}";
        t[280] = "The server requested password-based authentication, but no password was provided.";
        t[281] = "Le serveur a demand\u00e9 une authentification par mots de passe, mais aucun mot de passe n''a \u00e9t\u00e9 fourni.";
        t[296] = "Truncation of large objects is only implemented in 8.3 and later servers.";
        t[297] = "Le troncage des large objects n''est impl\u00e9ment\u00e9 que dans les serveurs 8.3 et sup\u00e9rieurs.";
        t[298] = "This PooledConnection has already been closed.";
        t[299] = "Cette PooledConnection a d\u00e9j\u00e0 \u00e9t\u00e9 ferm\u00e9e.";
        t[306] = "Fetch size must be a value greater to or equal to 0.";
        t[307] = "Fetch size doit \u00eatre une valeur sup\u00e9rieur ou \u00e9gal \u00e0 0.";
        t[312] = "A connection could not be made using the requested protocol {0}.";
        t[313] = "Aucune connexion n''a pu \u00eatre \u00e9tablie en utilisant le protocole demand\u00e9 {0}. ";
        t[322] = "There are no rows in this ResultSet.";
        t[323] = "Il n''y pas pas de lignes dans ce ResultSet.";
        t[324] = "Unexpected command status: {0}.";
        t[325] = "Statut de commande inattendu\u00a0: {0}.";
        t[334] = "Not on the insert row.";
        t[335] = "Pas sur la ligne en insertion.";
        t[344] = "Server SQLState: {0}";
        t[345] = "SQLState serveur\u00a0: {0}";
        t[348] = "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.";
        t[349] = "Le param\u00e8tre serveur standard_conforming_strings a pour valeur {0}. Le driver JDBC attend on ou off.";
        t[360] = "The driver currently does not support COPY operations.";
        t[361] = "Le pilote ne supporte pas actuellement les op\u00e9rations COPY.";
        t[364] = "The array index is out of range: {0}, number of elements: {1}.";
        t[365] = "L''indice du tableau est hors limites\u00a0: {0}, nombre d''\u00e9l\u00e9ments\u00a0: {1}.";
        t[374] = "suspend/resume not implemented";
        t[375] = "suspend/resume pas impl\u00e9ment\u00e9";
        t[378] = "Not implemented: one-phase commit must be issued using the same connection that was used to start it";
        t[379] = "Pas impl\u00e9ment\u00e9: le commit \u00e0 une phase doit avoir lieu en utilisant la m\u00eame connection que celle o\u00f9 il a commenc\u00e9";
        t[398] = "Cannot call cancelRowUpdates() when on the insert row.";
        t[399] = "Impossible d''appeler cancelRowUpdates() pendant l''insertion d''une ligne.";
        t[400] = "Cannot reference a savepoint after it has been released.";
        t[401] = "Impossible de r\u00e9f\u00e9rencer un savepoint apr\u00e8s qu''il ait \u00e9t\u00e9 lib\u00e9r\u00e9.";
        t[402] = "You must specify at least one column value to insert a row.";
        t[403] = "Vous devez sp\u00e9cifier au moins une valeur de colonne pour ins\u00e9rer une ligne.";
        t[404] = "Unable to determine a value for MaxIndexKeys due to missing system catalog data.";
        t[405] = "Incapable de d\u00e9terminer la valeur de MaxIndexKeys en raison de donn\u00e9es manquante dans lecatalogue syst\u00e8me.";
        t[412] = "The JVM claims not to support the encoding: {0}";
        t[413] = "La JVM pr\u00e9tend ne pas supporter l''encodage: {0}";
        t[414] = "{0} function takes two or three arguments.";
        t[415] = "La fonction {0} n''accepte que deux ou trois arguments.";
        t[440] = "Unexpected error writing large object to database.";
        t[441] = "Erreur inattendue pendant l''\u00e9criture de large object dans la base.";
        t[442] = "Zero bytes may not occur in string parameters.";
        t[443] = "Z\u00e9ro octets ne devrait pas se produire dans les param\u00e8tres de type cha\u00eene de caract\u00e8res.";
        t[444] = "A result was returned when none was expected.";
        t[445] = "Un r\u00e9sultat a \u00e9t\u00e9 retourn\u00e9 alors qu''aucun n''\u00e9tait attendu.";
        t[450] = "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.";
        t[451] = "Le ResultSet n''est pas modifiable. La requ\u00eate qui a g\u00e9n\u00e9r\u00e9 ce r\u00e9sultat doit s\u00e9lectionner seulement une table, et doit s\u00e9lectionner toutes les cl\u00e9s primaires de cette table. Voir la sp\u00e9cification de l''API JDBC 2.1, section 5.6 pour plus de d\u00e9tails.";
        t[454] = "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.";
        t[455] = "La longueur du message de liaison {0} est trop grande. Cela peut \u00eatre caus\u00e9 par des sp\u00e9cification de longueur tr\u00e8s grandes ou incorrectes pour les param\u00e8tres de type InputStream.";
        t[460] = "Statement has been closed.";
        t[461] = "Statement a \u00e9t\u00e9 ferm\u00e9.";
        t[462] = "No value specified for parameter {0}.";
        t[463] = "Pas de valeur sp\u00e9cifi\u00e9e pour le param\u00e8tre {0}.";
        t[468] = "The array index is out of range: {0}";
        t[469] = "L''indice du tableau est hors limites\u00a0: {0}";
        t[474] = "Unable to bind parameter values for statement.";
        t[475] = "Incapable de lier les valeurs des param\u00e8tres pour la commande.";
        t[476] = "Can''t refresh the insert row.";
        t[477] = "Impossible de rafra\u00eechir la ligne ins\u00e9r\u00e9e.";
        t[480] = "No primary key found for table {0}.";
        t[481] = "Pas de cl\u00e9 primaire trouv\u00e9e pour la table {0}.";
        t[482] = "Cannot change transaction isolation level in the middle of a transaction.";
        t[483] = "Impossible de changer le niveau d''isolation des transactions au milieu d''une transaction.";
        t[498] = "Provided InputStream failed.";
        t[499] = "L''InputStream fourni a \u00e9chou\u00e9.";
        t[500] = "The parameter index is out of range: {0}, number of parameters: {1}.";
        t[501] = "L''indice du param\u00e8tre est hors limites\u00a0: {0}, nombre de param\u00e8tres\u00a0: {1}.";
        t[502] = "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.";
        t[503] = "Le param\u00e8tre DateStyle du serveur a \u00e9t\u00e9 chang\u00e9 pour {0}. Le pilote JDBC n\u00e9cessite que DateStyle commence par ISO pour un fonctionnement correct.";
        t[508] = "Connection attempt timed out.";
        t[509] = "La tentative de connexion a \u00e9chou\u00e9 dans le d\u00e9lai imparti.";
        t[512] = "Internal Query: {0}";
        t[513] = "Requ\u00eate interne: {0}";
        t[518] = "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.";
        t[519] = "Le type d''authentification {0} n''est pas support\u00e9. V\u00e9rifiez que vous avez configur\u00e9 le fichier pg_hba.conf pour inclure l''adresse IP du client ou le sous-r\u00e9seau et qu''il utilise un sch\u00e9ma d''authentification support\u00e9 par le pilote.";
        t[526] = "Interval {0} not yet implemented";
        t[527] = "L''interval {0} n''est pas encore impl\u00e9ment\u00e9";
        t[532] = "Conversion of interval failed";
        t[533] = "La conversion de l''intervalle a \u00e9chou\u00e9";
        t[540] = "Query timeout must be a value greater than or equals to 0.";
        t[541] = "Query timeout doit \u00eatre une valeur sup\u00e9rieure ou \u00e9gale \u00e0 0.";
        t[542] = "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.";
        t[543] = "La connexion a \u00e9t\u00e9 ferm\u00e9e automatiquement car une nouvelle connexion a \u00e9t\u00e9 ouverte pour la m\u00eame PooledConnection ou la PooledConnection a \u00e9t\u00e9 ferm\u00e9e.";
        t[544] = "ResultSet not positioned properly, perhaps you need to call next.";
        t[545] = "Le ResultSet n''est pas positionn\u00e9 correctement, vous devez peut-\u00eatre appeler next().";
        t[550] = "This statement has been closed.";
        t[551] = "Ce statement a \u00e9t\u00e9 ferm\u00e9.";
        t[552] = "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.";
        t[553] = "Impossible de d\u00e9duire le type SQL \u00e0 utiliser pour une instance de {0}. Utilisez setObject() avec une valeur de type explicite pour sp\u00e9cifier le type \u00e0 utiliser.";
        t[554] = "Cannot call updateRow() when on the insert row.";
        t[555] = "Impossible d''appeler updateRow() tant que l''on est sur la ligne ins\u00e9r\u00e9e.";
        t[562] = "Detail: {0}";
        t[563] = "D\u00e9tail\u00a0: {0}";
        t[566] = "Cannot call deleteRow() when on the insert row.";
        t[567] = "Impossible d''appeler deleteRow() pendant l''insertion d''une ligne.";
        t[568] = "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.";
        t[569] = "Actuellement positionn\u00e9 avant le d\u00e9but du ResultSet. Vous ne pouvez pas appeler deleteRow() ici.";
        t[576] = "Illegal UTF-8 sequence: final value is a surrogate value: {0}";
        t[577] = "S\u00e9quence UTF-8 ill\u00e9gale: la valeur finale est une valeur de remplacement: {0}";
        t[578] = "Unknown Response Type {0}.";
        t[579] = "Type de r\u00e9ponse inconnu {0}.";
        t[582] = "Unsupported value for stringtype parameter: {0}";
        t[583] = "Valeur non support\u00e9e pour les param\u00e8tre de type cha\u00eene de caract\u00e8res\u00a0: {0}";
        t[584] = "Conversion to type {0} failed: {1}.";
        t[585] = "La conversion vers le type {0} a \u00e9chou\u00e9\u00a0: {1}.";
        t[586] = "Conversion of money failed.";
        t[587] = "La conversion de money a \u00e9chou\u00e9.";
        t[600] = "Unable to load the class {0} responsible for the datatype {1}";
        t[601] = "Incapable de charger la classe {0} responsable du type de donn\u00e9es {1}";
        t[604] = "The fastpath function {0} is unknown.";
        t[605] = "La fonction fastpath {0} est inconnue.";
        t[608] = "Malformed function or procedure escape syntax at offset {0}.";
        t[609] = "Syntaxe de fonction ou d''\u00e9chappement de proc\u00e9dure malform\u00e9e \u00e0 l''indice {0}.";
        t[612] = "Provided Reader failed.";
        t[613] = "Le Reader fourni a \u00e9chou\u00e9.";
        t[614] = "Maximum number of rows must be a value grater than or equal to 0.";
        t[615] = "Le nombre maximum de lignes doit \u00eatre une valeur sup\u00e9rieure ou \u00e9gale \u00e0 0.";
        t[616] = "Failed to create object for: {0}.";
        t[617] = "\u00c9chec \u00e0 la cr\u00e9ation de l''objet pour\u00a0: {0}.";
        t[622] = "Premature end of input stream, expected {0} bytes, but only read {1}.";
        t[623] = "Fin pr\u00e9matur\u00e9e du flux en entr\u00e9e, {0} octets attendus, mais seulement {1} lus.";
        t[626] = "An unexpected result was returned by a query.";
        t[627] = "Un r\u00e9sultat inattendu a \u00e9t\u00e9 retourn\u00e9 par une requ\u00eate.";
        t[646] = "An error occurred while setting up the SSL connection.";
        t[647] = "Une erreur s''est produite pendant l''\u00e9tablissement de la connexion SSL.";
        t[654] = "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}";
        t[655] = "S\u00e9quence UTF-8 ill\u00e9gale: {0} octets utilis\u00e9 pour encoder une valeur \u00e0 {1} octets: {2}";
        t[658] = "The SSLSocketFactory class provided {0} could not be instantiated.";
        t[659] = "La classe SSLSocketFactory fournie {0} n''a pas pu \u00eatre instanci\u00e9e.";
        t[670] = "Position: {0}";
        t[671] = "Position\u00a0: {0}";
        t[676] = "Location: File: {0}, Routine: {1}, Line: {2}";
        t[677] = "Localisation\u00a0: Fichier\u00a0: {0}, Routine\u00a0: {1}, Ligne\u00a0: {2}";
        t[684] = "Cannot tell if path is open or closed: {0}.";
        t[685] = "Impossible de dire si path est ferm\u00e9 ou ouvert\u00a0: {0}.";
        t[700] = "Cannot convert an instance of {0} to type {1}";
        t[701] = "Impossible de convertir une instance de type {0} vers le type {1}";
        t[710] = "{0} function takes four and only four argument.";
        t[711] = "La fonction {0} n''accepte que quatre et seulement quatre arguments.";
        t[718] = "Interrupted while attempting to connect.";
        t[719] = "Interrompu pendant l''\u00e9tablissement de la connexion.";
        t[722] = "Illegal UTF-8 sequence: final value is out of range: {0}";
        t[723] = "S\u00e9quence UTF-8 ill\u00e9gale: la valeur finale est en dehors des limites: {0}";
        t[734] = "No function outputs were registered.";
        t[735] = "Aucune fonction outputs n''a \u00e9t\u00e9 enregistr\u00e9e.";
        t[736] = "{0} function takes one and only one argument.";
        t[737] = "La fonction {0} n''accepte qu''un et un seul argument.";
        t[744] = "This ResultSet is closed.";
        t[745] = "Ce ResultSet est ferm\u00e9.";
        t[746] = "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.";
        t[747] = "Des donn\u00e9es de caract\u00e8res invalides ont \u00e9t\u00e9 trouv\u00e9es. C''est probablement caus\u00e9 par le stockage de caract\u00e8res invalides pour le jeu de caract\u00e8res de cr\u00e9ation de la base. L''exemple le plus courant est le stockage de donn\u00e9es 8bit dans une base SQL_ASCII.";
        t[750] = "An I/O error occurred while sending to the backend.";
        t[751] = "Une erreur d''entr\u00e9e/sortie a eu lieu lors d''envoi vers le serveur.";
        t[752] = "Error disabling autocommit";
        t[753] = "Erreur en d\u00e9sactivant autocommit";
        t[754] = "Ran out of memory retrieving query results.";
        t[755] = "Ai manqu\u00e9 de m\u00e9moire en r\u00e9cup\u00e9rant les r\u00e9sultats de la requ\u00eate.";
        t[756] = "Returning autogenerated keys is not supported.";
        t[757] = "Le renvoi des cl\u00e9s automatiquement g\u00e9n\u00e9r\u00e9es n''est pas support\u00e9.";
        t[760] = "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.";
        t[761] = "L''op\u00e9ration n\u00e9cessite un scrollable ResultSet, mais ce ResultSet est FORWARD_ONLY.";
        t[762] = "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.";
        t[763] = "Une fonction CallableStatement a \u00e9t\u00e9 ex\u00e9cut\u00e9e et le param\u00e8tre en sortie {0} \u00e9tait du type {1} alors que le type {2} \u00e9tait pr\u00e9vu.";
        t[768] = "Unknown ResultSet holdability setting: {0}.";
        t[769] = "Param\u00e8tre holdability du ResultSet inconnu\u00a0: {0}.";
        t[772] = "Transaction isolation level {0} not supported.";
        t[773] = "Le niveau d''isolation de transaction {0} n''est pas support\u00e9.";
        t[774] = "Zero bytes may not occur in identifiers.";
        t[775] = "Des octects \u00e0 0 ne devraient pas appara\u00eetre dans les identifiants.";
        t[776] = "No results were returned by the query.";
        t[777] = "Aucun r\u00e9sultat retourn\u00e9 par la requ\u00eate.";
        t[778] = "A CallableStatement was executed with nothing returned.";
        t[779] = "Un CallableStatement a \u00e9t\u00e9 ex\u00e9cut\u00e9 mais n''a rien retourn\u00e9.";
        t[780] = "wasNull cannot be call before fetching a result.";
        t[781] = "wasNull ne peut pas \u00eatre appel\u00e9 avant la r\u00e9cup\u00e9ration d''un r\u00e9sultat.";
        t[786] = "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.";
        t[787] = "Cette requ\u00eate ne d\u00e9clare pas de param\u00e8tre OUT. Utilisez '{' ?= call ... '}' pour en d\u00e9clarer un.";
        t[788] = "Can''t use relative move methods while on the insert row.";
        t[789] = "Impossible d''utiliser les fonctions de d\u00e9placement relatif pendant l''insertion d''une ligne.";
        t[792] = "Connection is busy with another transaction";
        t[793] = "La connection est occup\u00e9e avec une autre transaction";
        table = t;
    }
}

