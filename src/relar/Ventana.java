package relar;

import javax.swing.*;

public class Ventana extends JFrame {

  JTextArea texto = new JTextArea();

  public Ventana(){

    texto.setEditable(false);
    add(texto);
    JScrollPane scrol = new JScrollPane(texto);
    add(scrol);

  }

}

