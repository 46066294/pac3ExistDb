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

//Part 1: MODE XQJ
public class Principal {
    public static String nomFitxer = "books.xml";
    public static String ipMaquina = "";

    private static String URI = "xmldb:exist://" + ipMaquina + ":8080/exist/xmlrpc";
    private static String driver = "org.exist.xmldb.DatabaseImpl";

    public static XQConnection conn = null;

    public static void main(String[]args) throws XQException {

        try {

            afegirFitxer();

        } catch (XMLDBException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        iniciarConexio();
        p1();
        p2();
        //p3();
        tancarConexio();

    }//main


    //::::::::::METODES::::::::::

    private static void afegirFitxer() throws XMLDBException, ClassNotFoundException, IllegalAccessException, InstantiationException{
        File f = new File("books.xml");

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");

        // crear el manejador
        DatabaseManager.registerDatabase(database);

        //crear la collecion
        Collection parent = DatabaseManager.getCollection(URI + "/db","admin","dionis");//padre de la coleccion
        CollectionManagementService c = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

        c.createCollection("cano");//nom de la coleccio

        Collection col = DatabaseManager.getCollection(URI + "/db/cano", "admin", "dionis");

        //afegir el recurs que farem servir
        Resource res = col.createResource("Cano.xml","XMLResource");
        res.setContent(f);
        col.storeResource(res);

    }

    private static void iniciarConexio() throws XQException {
        Scanner inputIp = new Scanner(System.in);
        System.out.println("Entra IP:");
        String ip = inputIp.next();

        /*
        verificacio IP
        */

        ipMaquina = ip;

        XQDataSource xqs = new ExistXQDataSource();
        xqs.setProperty("serverName", ipMaquina);
        xqs.setProperty("port", "8080");

        inputIp.close();

        System.out.println("...conexio OK per a IP: " + ip);

    }

    private static void tancarConexio() throws XQException {
        conn.close();
    }

    //query 1
    public static void p1 () throws XQException {

        String xpath = "doc('" + nomFitxer + "')/CATALOG/PLANT[AVAILABILITY = max(/CATALOG/PLANT/AVAILABILITY)]/COMMON/text()";

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

        System.out.println(resultado + " es la planta de la que tenim mes stoc");
    }



    //query 2
    public static void p2 () throws XQException {
        String xpath = "sum(doc(\"" + nomFitxer + "\")/CATALOG/PLANT/AVAILABILITY)";

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
            break;

        }

        System.out.println("Total " + resultado + " plantes en stoc.");
    }

}//Principal
