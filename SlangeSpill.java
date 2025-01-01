import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
    JButton restartKnapp;
    private int hoyestScore = 0;

    SlangeSpill(int bredde, int hoyde) {
        this.bredde = bredde;
        this.hoyde = hoyde;
        setPreferredSize(new Dimension(this.bredde, this.hoyde));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        slangeHode = new Rute(5,5);
        slangeKropp = new ArrayList<>();

        mat = new Rute(15,15);
        random = new Random();
        utplasseringMat();

        hastighetX = 1;
        hastighetY = 0;

        spillLooper = new Timer(100, this);
        spillLooper.start();

        int knappBredde = 150;
        int knappHoyde = 50;
        int knappX = bredde / 2 - knappBredde / 2;
        int knappY = hoyde / 2 - knappHoyde / 2;

        restartKnapp = new JButton("Restart");
        restartKnapp.setFont(new Font("Arial", Font.BOLD, 20));
        restartKnapp.setBackground(Color.GREEN);
        restartKnapp.setForeground(Color.BLACK);
        restartKnapp.setFocusable(false); 
        restartKnapp.setVisible(false); //Setter fokus på knappen etter gameover, altså knappen vises ikke før spillet er over
        
        restartKnapp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartSpill();
            }
        });

        this.setLayout(null);
        restartKnapp.setBounds(knappX, knappY, knappBredde, knappHoyde);
        this.add(restartKnapp);
    }

    public void lesHoyestScore() {
        try {
            File fil = new File("hoyestscore.txt");
            if (fil.exists()) {
                Scanner sc = new Scanner(fil);
                if (sc.hasNextInt()) {
                    hoyestScore = sc.nextInt();
                }
                sc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lagre høyest score til fil
    public void lagreHoyestScore() {
        try {
            FileWriter writer = new FileWriter("hoyestScore.txt");
            writer.write(String.valueOf(hoyestScore));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		farge(g);
	}

    public void tegnRute(Graphics g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.fill3DRect(x * size, y * size, size, size, true);
    }    

    public void farge(Graphics g) {
        for (int i = 0; i < bredde/ruteStorrelse; i++) {
            g.drawLine(i*ruteStorrelse, 0, i*ruteStorrelse, hoyde);
            g.drawLine(0, i*ruteStorrelse, bredde, i*ruteStorrelse);
        }

        //Tegn mat
        tegnRute(g, mat.x, mat.y, ruteStorrelse, Color.red);

        //Tegn slangehode
        tegnRute(g, slangeHode.x, slangeHode.y, ruteStorrelse, Color.green);

        //Tegn slange kropp
        for (int i = 0; i < slangeKropp.size(); i++) {
            Rute slangeDel = slangeKropp.get(i);
            tegnRute(g, slangeDel.x, slangeDel.y, ruteStorrelse, Color.green); // Kan bruke samme farge eller endre farge etter behov
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
        }
        else {
            g.drawString("Score: " + String.valueOf(slangeKropp.size()), ruteStorrelse - 16, ruteStorrelse);
        }

        g.drawString("Score: " + slangeKropp.size(), ruteStorrelse - 16, ruteStorrelse);
        g.drawString("Hoyest score: " + hoyestScore, ruteStorrelse - 16, ruteStorrelse + 20);  
    }

    public void utplasseringMat() {
        mat.x = random.nextInt(bredde/ruteStorrelse);
        mat.y = random.nextInt(hoyde/ruteStorrelse);
    }

    public void bevege() {
        if (kollisjon(slangeHode, mat)) {
            slangeKropp.add(new Rute(mat.x, mat.y));
            utplasseringMat();
        }

        for (int i = slangeKropp.size()-1; i >= 0; i--) {
            Rute del = slangeKropp.get(i);
            if (i == 0) {
                del.x = slangeHode.x;
                del.y = slangeHode.y;
            } else {
                Rute forrigeDel = slangeKropp.get(i-1);
                del.x = forrigeDel.x;
                del.y = forrigeDel.y;
            }
        }

        slangeHode.x += hastighetX;
        slangeHode.y += hastighetY;
        
        //Tilfeller hvor spillet er over
        for (int i = 0; i < slangeKropp.size(); i++) {
            Rute del = slangeKropp.get(i);

            //Kolliderer med slangehode
            if (kollisjon(slangeHode, del)) {
                gameOver = true;
            }
        }

        if (slangeHode.x*ruteStorrelse < 0 || slangeHode.x*ruteStorrelse > bredde || slangeHode.y*ruteStorrelse < 0 || slangeHode.y*ruteStorrelse > hoyde) {
            gameOver = true;
        }

        if (gameOver) {
            restartKnapp.setVisible(true);
            spillLooper.stop();
        }

        if (slangeKropp.size() > hoyestScore) {
            hoyestScore = slangeKropp.size();
            lagreHoyestScore();
        }
    }

    public boolean kollisjon(Rute rute1, Rute rute2) {
        return rute1.x == rute2.x && rute1.y == rute2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bevege();
        repaint();
        if (gameOver) {
            spillLooper.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && hastighetY != 1) {
            hastighetX = 0;
            hastighetY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && hastighetY != -1) {
            hastighetX = 0;
            hastighetY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && hastighetX != -1) {
            hastighetX = 1;
            hastighetY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && hastighetX != 1) {
            hastighetX = -1;
            hastighetY = 0;
        }
    }

    public void restartSpill() {
        slangeHode = new Rute(5,5);
        slangeKropp.clear();
        utplasseringMat();
        hastighetX = 1;
        hastighetY = 0;
        gameOver = false;
        restartKnapp.setVisible(false);
        spillLooper.start();
        repaint();
    }

}
