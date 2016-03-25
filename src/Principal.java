import net.xqj.exist.ExistXQDataSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import javax.xml.xquery.*;
import java.io.File;
import java.util.Scanner;


public class Principal {
    /*
    S'estableixen les variables i constants globals
    */
    public static String nomFitxer = "books.xml";
    public static String ipMaquina = "";
    public static String URI = "xmldb:exist://" + ipMaquina + ":8080/exist/xmlrpc";
    public static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    public static XQConnection conn = null;

    public static void main(String[]args) throws XQException {

        try {

            iniciarConexio();
            afegirFitxer();

            System.out.println("\nCONSULTES:");

            contarLlibres();
            preuMesCar();
            retornaRecurs();

        } catch (XMLDBException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }//main


    //::::::::::METODES::::::::::

    //S'estableix la conexio amb existDB.
    //ipMaquina hauria de ser "localhost" o la nostra ip
    private static void iniciarConexio() throws XQException {
        Scanner inputIp = new Scanner(System.in);
        System.out.println("Entra IP:");
        String ip = inputIp.next();

        ipMaquina = ip;
        URI = "xmldb:exist://" + ipMaquina + ":8080/exist/xmlrpc";

        XQDataSource xqs = new ExistXQDataSource();
        xqs.setProperty("serverName", ipMaquina);
        xqs.setProperty("port", "8080");//s'estableix el port 8080

        inputIp.close();

        System.out.println("...iniciant conexio");

    }

    //Afegeixi un recurs a una col·lecció
    private static void afegirFitxer() throws XMLDBException, ClassNotFoundException, IllegalAccessException, InstantiationException{
        System.out.println("\n...creant nova col·leccio");

        File f = new File("books.xml");

        // initialize database DRIVER
        Class cl = Class.forName(DRIVER);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");

        // crear el manejador
        DatabaseManager.registerDatabase(database);

        //crear la collecion
        Collection parent = DatabaseManager.getCollection(URI + "/db","admin","marc");//padre de la coleccion
        CollectionManagementService c = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

        c.createCollection("books_cano");//nom de la coleccio

        Collection col = DatabaseManager.getCollection(URI + "/db/books_cano", "admin", "marc");

        //afegir el recurs que farem servir
        Resource res = col.createResource("books.xml","XMLResource");
        res.setContent(f);
        col.storeResource(res);

        System.out.println("DADES:");
        System.out.println("\texistDB URI: " + URI);
        System.out.println("\tlocal IP: " + ipMaquina);
        System.out.println("\tnom de la col·leccio: " + col.getName());
        System.out.println("\tnom del recurs: " + res.getId());

    }




    //query 1
    public static void contarLlibres () throws XQException {

        String xpath = "doc('" + nomFitxer + "')/catalog/count(/catalog/book)";

        String resultado = "";
        String linea = "";
        XQDataSource xqs = new ExistXQDataSource();
        xqs.setProperty("serverName", ipMaquina);
        xqs.setProperty("port", "8080");

        XQConnection conn = xqs.getConnection();

        XQPreparedExpression xqpe = conn.prepareExpression(xpath);

        XQResultSequence rs = xqpe.executeQuery();

        while (rs.next()){
            linea = rs.getItemAsString(null);
            resultado += linea;

        }

        System.out.println("1) Hi han " + resultado + " llibres");
    }



    //query 2
    public static void preuMesCar () throws XQException {

        String xpath = "doc('" + nomFitxer + "')/catalog/max(/catalog/book/price)";

        String resultado = "";
        System.out.println();
        String linea = "";
        XQDataSource xqs = new ExistXQDataSource();
        xqs.setProperty("serverName", ipMaquina);
        xqs.setProperty("port", "8080");

        XQConnection conn = xqs.getConnection();

        XQPreparedExpression xqpe = conn.prepareExpression(xpath);

        XQResultSequence rs = xqpe.executeQuery();

        while (rs.next()){
            linea = rs.getItemAsString(null);
            resultado += linea + "\n";
            break;

        }

        System.out.println("2) Preu del llibre mes car: " + resultado);
    }

    //imprimeix per pantalla el recurs books.xml
    public static void retornaRecurs () throws XQException {

        String xpath = "collection(\"/db/books_cano\")//catalog";


        String resultado = "";
        String linea = "";
        XQDataSource xqs = new ExistXQDataSource();
        xqs.setProperty("serverName", ipMaquina);
        xqs.setProperty("port", "8080");

        XQConnection conn = xqs.getConnection();

        XQPreparedExpression xqpe = conn.prepareExpression(xpath);

        XQResultSequence rs = xqpe.executeQuery();

        while (rs.next()){
            linea = rs.getItemAsString(null);
            resultado += linea + "\n";
        }

        System.out.println("3) Recurs complet:\n" + resultado);
    }

}//Principal
