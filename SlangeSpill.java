import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SlangeSpill extends JPanel implements ActionListener, KeyListener {
    private class Rute {
        int x, y;

        Rute(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int bredde, hoyde;
    int ruteStorrelse = 25;

    //slange
    Rute slangeHode;
    ArrayList<Rute> slangeKropp;

    //mat
    Rute mat;
    Random random;

    //Spill logikken
    int hastighetX, hastighetY;
    Timer spillLooper;
    boolean gameOver = false;
    JButton provIgjen;

    SlangeSpill(int bredde, int hoyde) {
        this.bredde = bredde;
        this.hoyde = hoyde;
        setPreferredSize(new Dimension(this.bredde, this.hoyde));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

    }

}
