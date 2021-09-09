package relar;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    Ventana ventana = new Ventana();

    ventana.setBounds(200, 200, 410, 250);
    ventana.setVisible(true);
    ventana.texto.append("Buscando disco duro \n"); //✔
    ventana.setState(Frame.ICONIFIED);
    ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


    String unidadDestino = null;
    String uniTerCop = null;

    ////////////////////////  MODO AUTOMATICO/////////////////////////////////////////////////////////

    // Listado de todas las unidades de almacenamiento del sistema
    File[] unidades = File.listRoots();

    // Se recorre toda la lista en busca de las particiones del disco externo
    for (File u : unidades) {
      ventana.texto.append("Analizando unidad: " + u + "\n"); //✔
      String nombreUnidad = FileSystemView.getFileSystemView().getSystemDisplayName(u);
      String letra = nombreUnidad.charAt(nombreUnidad.length() - 3) + ":\\";

      if (nombreUnidad.indexOf("Copia") == 0)
        unidadDestino = letra;

      if (nombreUnidad.indexOf("Santi") == 0)
        uniTerCop = letra;

    }

    ////////////////////////  MODO MANUAL /////////////////////////////////////////////////////////
    if (unidadDestino == null) {
      // Se obtiene la unidad donde se va a realizar la copia manualmente
      unidadDestino = JOptionPane.showInputDialog("Unidad Donde se hará la copia no encontrada introducir:");

      // Se pasa a mayusculas y se añade :\
      unidadDestino = unidadDestino.toUpperCase() + ":\\";
    } else
      ventana.texto.append("Unidad Copia Encontrada en: " + unidadDestino + "\n"); //✔

    if (uniTerCop == null) {
      // Se obtiene la unidad donde se encuentra el teracopy manualmente
      uniTerCop = JOptionPane.showInputDialog("Unidad Donde está Teracopy:");

      // Se pasa a mayusculas y se añade :\
      uniTerCop = uniTerCop.toUpperCase() + ":\\";
    } else
      ventana.texto.append("Unidad Teracopy Encontrada en: " + uniTerCop + "\n"); //✔

    /////////////////////////////////////////////////////////////////////////////////////////////////

    // Se obtiene nombre del usuario
    String nombreUsuario = System.getProperty("user.name");

    ventana.texto.append("Usuario: " + nombreUsuario + "\n"); //✔

    // Se crea la ruta destino
    String destino = unidadDestino + nombreUsuario;

    ventana.texto.append("Ruta destino obtenida: " + destino + "\n"); //✔

    //Creacion de carpetas//
    // Se crea la carpeta del usuario pasandole la ruta
    File carpetaDestino = new File(destino);


    if (carpetaDestino.mkdir())
      ventana.texto.append("Carpeta " + nombreUsuario + " Creada" + "\n"); //✔


    // CAPTURA DE PANTALLA DEL ESCRITORIO
    ventana.texto.append(captura(destino) + "\n"); //✔

    // IMPRESORA PREDETERMINADA
    ventana.texto.append(impresora(destino) + "\n"); //✔

    // LISTADO PROGRAMAS INSTALADOS
    ventana.texto.append(programas(destino) + "\n"); //✔

    // POSICION DE LOS ICONOS DEL ESCRITORIO .REG
    ventana.texto.append(posicionIconos(destino) + "\n"); //✔

    //SE GENERA LA LISTA DE LAS RUTAS A COMPROBAR
    List<String> rutas = new ArrayList<>();

    //SE GENERA LA LISTA PARA COPIAR
    List<String> lista = new ArrayList<>();


    String perfUsu = "C:\\Users\\" + nombreUsuario;

    lista.add(perfUsu + "\\Desktop");
    lista.add(perfUsu + "\\Downloads");
    lista.add(perfUsu + "\\Documents");
    lista.add(perfUsu + "\\Favorites");
    lista.add(perfUsu + "\\Pictures");
    lista.add(perfUsu + "\\Music");
    lista.add(perfUsu + "\\Videos");

    // ESTAS DE AQUI SE TIENEN QUE COMPROBAR ANTES DE INTENTAR COPIARLAS A LA LISTA //
    rutas.add(perfUsu + "\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks");
    rutas.add(perfUsu + "\\AppData\\Roaming\\Microsoft\\Firmas");
    rutas.add(perfUsu + "\\correo");
    rutas.add(perfUsu + "\\AppData\\Roaming\\Mozilla\\Firefox\\profiles.ini");
    rutas.add(perfUsu + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles");
    rutas.add("C:\\Postmigracion");
    rutas.add("C:\\correo");
    rutas.add("C:\\escaner");
    rutas.add("C:\\datos");
    rutas.add("C:\\Windows\\Saplogon.ini");
    rutas.add(perfUsu + "\\AppData\\Roaming\\Microsoft\\Internet Explorer\\Quick Launch\\User Pinned\\TaskBar");

    // AQUI SE MANDAN COMPROBAR SI EXISTEN
    for (String ruta : rutas)
      if (new File(ruta).exists()) lista.add(ruta);

    // SE CREA EL FICHERO CON LA LISTA PARA COPIAR
    try {
      File listado = new File(destino + "\\lista.txt");

      if (listado.createNewFile())
        ventana.texto.append(listado.getName() + " Creado" + "\n"); //✔


    } catch (IOException e) {
      ventana.texto.append("Ha ocurrido un error al crear el archivo txt." + e + "\n"); //✔
    }

    try {
      FileWriter myWriter = new FileWriter(destino + "\\lista.txt");

      for (String r : lista)
        myWriter.write(r + "\n");

      myWriter.close();
      ventana.texto.append("Contenido de la lista agregado ✔"); //✔❌

    } catch (IOException e) {
      ventana.texto.append("Ha ocurrido un error al escribir en el fichero txt ❌" + e); //✔❌
    }

    // SE LANZA LA SENTENCIA DE COPIADO
    copia(uniTerCop, destino, "*" + destino + "\\lista.txt");

  }

  /**
   * Comando cmd para exportar una clave del registro del sistema de windows con la posicion de
   * los iconos del escritorio
   *
   * @param destino carpeta donde se aloja la copia que se genera
   */
  public static String posicionIconos(String destino) {

    ProcessBuilder pb = new ProcessBuilder(
        "REG",
        "EXPORT",
        "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\Shell\\Bags\\1\\Desktop",
        destino + "\\posicionIconos.reg");

    pb.redirectErrorStream(true);

    try {
      pb.start();
    } catch (IOException e) {
      System.out.println("Error al ejecutar copiado: " + e);
    }


    return "posicionIconos.reg creado";
  }

  /**
   * @param uniTerCop unidad donde se encuentra el ejecutable de teracopy
   * @param destino   carpeta donde se aloja la copia que se genera
   * @param ruta      ruta de lo que se desea copiar
   */
  public static void copia(String uniTerCop, String destino, String ruta) {

    ProcessBuilder pb = new ProcessBuilder(
        uniTerCop + "\\TeraCopy\\TeraCopy.exe",
        "Copy",
        ruta,
        destino,
        "/NoClose");

    //pb.directory(new File(uniTerCop + "\\TeraCopy\\TeraCopy.exe"));
    pb.redirectErrorStream(true);

    Process p = null;
    try {
      p = pb.start();
    } catch (IOException e) {
      System.out.println("Error al ejecutar copiado: " + e);
    }

    try {
      assert p != null;
      int exitValue = p.waitFor();
      System.out.println("\nCódigo de salida: " + exitValue);

    } catch (InterruptedException e) {
      e.printStackTrace(System.err);
    }

    System.out.println(p);
  }

  /**
   * Comando de powershell para obtener y guardar la lista de programas instalados
   *
   * @param destino Para localizar la carpeta donde se genera la lista
   */
  public static String programas(String destino) {
    Process p;
    try {
      p = Runtime.getRuntime()
          .exec("powershell -command \"Get-ItemProperty HKLM:\\Software\\Wow6432Node"
              + "\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName, DisplayVersion "
              + "| Format-Table –AutoSize | Out-File -FilePath " + destino + "\\Programas.txt\"");
    } catch (IOException e) {
      return "Error al obtener o generar la lista de programas" + e;
      //e.printStackTrace();
    }

    return "Listado de programas generado " + p;
  }

  /**
   * PrintServiceLookup permite ver las impresoras
   *
   * @param carpeta Para localizar la carpeta donde se coloca la imagen
   */
  public static String impresora(String carpeta) {
    String log = "";

    PrintService imp = PrintServiceLookup.lookupDefaultPrintService();

    try {
      File archivoTxt = new File(carpeta + "\\Impresora predeterminada.txt");

      if (archivoTxt.createNewFile())
        log += archivoTxt.getName() + " Creado" + "\n";

    } catch (IOException e) {
      log += "Ha ocurrido un error al crear el archivo txt." + e + "\n";
      //e.printStackTrace();
    }

    try {
      FileWriter myWriter = new FileWriter(carpeta + "\\Impresora predeterminada.txt");
      myWriter.write("Impresora Predeterminada: " + imp);
      myWriter.close();
      log += "Guardada impresora: " + imp;
    } catch (IOException e) {
      log += "Ha ocurrido un error al escribir en el fichero txt" + e;
      //e.printStackTrace();
    }
    return log;
  }

  /**
   * Delimita el tamaño de la pantalla en un rectangulo y con la clase robot
   * captura la pantalla con write.io se genera la imagen
   *
   * @param carpeta Para localizar la carpeta donde se coloca la imagen
   */
  public static String captura(String carpeta) {
    try {
      // obtenemos el tamaño del rectangulo de la pantalla
      Rectangle rectangleTam = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

      Robot robot = new Robot();

      // tomamos una captura de pantalla( screenshot )
      BufferedImage bufferedImage = robot.createScreenCapture(rectangleTam);

      // nombreFichero=System.getProperty("user.home")+File.separatorChar+"caputura.png";
      FileOutputStream out = new FileOutputStream(carpeta + "\\Escritorio Anterior.png");

      // esbribe la imagen a fichero
      ImageIO.write(bufferedImage, "png", out);

      return "Captura de pantalla guardada";

    } catch (IOException | AWTException e) {
      //e.printStackTrace();
      return "Fallo al capturar la pantalla: " + e;
    }
  }
}