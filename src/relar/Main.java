package relar;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {

    public static void main(String[] args) throws AWTException {

      //Se obtiene nombre del usuario
      String nombreUsuario = System.getProperty("user.name");

      // obtenemos el tamaño del rectangulo
      Rectangle rectangleTam = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

      Robot robot = new Robot();

      // tomamos una captura de pantalla( screenshot )
      BufferedImage bufferedImage = robot.createScreenCapture(rectangleTam);

      //Se obtiene la unidad donde se va a realizar la copia
      String unidad = JOptionPane.showInputDialog("Unidad Donde se hará la copia:");

      //Se pasa a mayusculas y se añade :\
      unidad = unidad.toUpperCase() + ":\\";

      //Se crea la ruta destino
      String destino = unidad + nombreUsuario;

      //Se crea destino carpeta firefox
      String desFire = destino + "\\Firefox";

      System.out.println(destino);

      //Se crea la carpeta del usuario pasandole la ruta
      File carpeta = new File(destino);

      //Se crea la carpeta de firefox
      File carpetaFirefox = new File(desFire);

      if (carpeta.mkdir()) //aqui se ejecuta el comando de creacion de la carpeta
          System.out.println("Creado");

      if (carpetaFirefox.mkdir()) //aqui se ejecuta el comando de creacion de la carpeta
        System.out.println("Creado");

      /////////////////////////  CAPTURA DE PANTALLA DEL ESCRITORIO //////////////////////////////////////////////////

      try {

            //String nombreFichero=System.getProperty("user.home")+File.separatorChar+"caputura.png";
            String nombreFichero = carpeta + "\\Escritorio Anterior.png";
            System.out.println("Generando el fichero: "+nombreFichero );
            FileOutputStream out = new FileOutputStream(nombreFichero);

            // esbribe la imagen a fichero
            ImageIO.write(bufferedImage, "png", out);

        } catch (IOException e) {
            e.printStackTrace();
        }


        ////////////////////////////// SE OBTIENE LA IMPRESORA PREDETERMINADA //////////////////////////////////////////

        //Sacar impresora predeterminada
        PrintService impresora = PrintServiceLookup.lookupDefaultPrintService();

        try {
            File myObj = new File(carpeta + "\\Impresora predeterminada.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter(carpeta + "\\Impresora predeterminada.txt");
            myWriter.write("Impresora Predeterminada: " + impresora);
            myWriter.close();
            System.out.println("Escritura correcta");
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
            e.printStackTrace();
        }

        System.out.println(impresora);

        /////////////////////////////////// Se obtiene la lista de programas instalados ////////////////////////////////

        Process p = null;
        try {
            p = Runtime.getRuntime().exec("powershell -command \"Get-ItemProperty HKLM:\\Software\\Wow6432Node" +
              "\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName, DisplayVersion " +
              "| Format-Table –AutoSize | Out-File -FilePath " + destino + "\\Programas.txt\"");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(p);

        /////////////////////////////////////// Se lanza Teracopy para copiar datos ////////////////////////////////////

        //Se obtiene la unidad donde se va a realizar la copia
        String uniTerCop = JOptionPane.showInputDialog("Unidad Donde está Teracopy:");

        //Se pasa a mayusculas y se añade :\
        uniTerCop = uniTerCop.toUpperCase() + ":\\";

        File postmigracion = new File("C:\\Postmigracion");

        File correo = new File("C:\\correo");

        File scaner = new File("C:\\escaner");

        File datos = new File("C:\\datos");

        String rutaFavChrome = "C:\\Users\\"+nombreUsuario+"\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks";
        File favChrome = new File(rutaFavChrome);

        String rutaSaplogon = "C:\\Windows\\Saplogon.ini";
        File saplogon = new File(rutaSaplogon);

        //rutas
        String programa = uniTerCop + "TeraCopy\\TeraCopy.exe";
        String origen = "C:\\Users\\"+ nombreUsuario;

        File correoP = new File(origen + "\\correo");

        File barra = new File(origen + "\\AppData\\Roaming\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar");

        String oriFireIni = origen + "\\AppData\\Roaming\\Mozilla\\Firefox";
        String origenFirefox = oriFireIni + "\\Profiles";

        String[] esc = {programa, "Copy", origen + "\\Desktop", destino};
        String[] des = {programa, "Copy", origen + "\\Downloads", destino};
        String[] doc = {programa, "Copy", origen +"\\Documents", destino};
        String[] fav = {programa, "Copy", origen +"\\Favorites", destino};
        String[] img = {programa, "Copy", origen +"\\Pictures", destino};
        String[] mus = {programa, "Copy", origen +"\\Music", destino};
        String[] vid = {programa, "Copy", origen +"\\Videos", destino};

        String[] fir = {programa, "Copy", origenFirefox, desFire};
        String[] ini = {programa, "Copy", oriFireIni + "\\profiles.ini", desFire};

//        String [] tera = {uniTerCop + "TeraCopy\\TeraCopy.exe"};

      try {

//          Runtime.getRuntime().exec(tera); No se lanza

          if (correoP.exists()){
            String[] cor = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", origen + "\\correo", destino};
            Runtime.getRuntime().exec(cor);
          }

          if (barra.exists()){
            String[] bar = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", origen + "\\AppData\\Roaming\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar", destino};
            Runtime.getRuntime().exec(bar);
          }

          if (postmigracion.exists()){
            String[] pos = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", "C:\\Postmigracion", destino};
            Runtime.getRuntime().exec(pos);
          }

          if (correo.exists()){
            String[] cor = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", "C:\\correo", destino};
            Runtime.getRuntime().exec(cor);
          }

          if (scaner.exists()){
            String[] escaner = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", "C:\\escaner", destino};
            Runtime.getRuntime().exec(escaner);
          }

          if (datos.exists()){
            String[] dat = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", "C:\\datos", destino};
            Runtime.getRuntime().exec(dat);
          }

          if (favChrome.exists()){
            String[] fChrome = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", rutaFavChrome, destino};
            Runtime.getRuntime().exec(fChrome);
          }

          if (saplogon.exists()){
            String[] sap = {uniTerCop + "TeraCopy\\TeraCopy.exe", "Copy", rutaSaplogon, destino};
            Runtime.getRuntime().exec(sap);
          }

          Runtime.getRuntime().exec(esc);
          Runtime.getRuntime().exec(des);
          Runtime.getRuntime().exec(doc);
          Runtime.getRuntime().exec(fav);
          Runtime.getRuntime().exec(img);
          Runtime.getRuntime().exec(mus);
          Runtime.getRuntime().exec(vid);

          Runtime.getRuntime().exec(fir);
          Runtime.getRuntime().exec(ini);

        System.out.println("comando realizado con exito");

      } catch (IOException e) {
        e.printStackTrace();
      }

    }
}