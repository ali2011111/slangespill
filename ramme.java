import javax.swing.*;

class ramme {
    public static void main(String[] args) throws Exception{
        int bredde = 600;
        int hoyde = bredde;

        JFrame rammen = new JFrame("Slange_spill");
        rammen.setVisible(true);
        rammen.setSize(bredde, hoyde);
        rammen.setLocationRelativeTo(null);
        rammen.setResizable(false);
        rammen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SlangeSpill spill = new SlangeSpill(bredde, hoyde);
        rammen.add(spill);
        rammen.pack();
        spill.requestFocus();
    }
}
