import java.awt.*;

// enum = class that represents a group of constants that are unchangeable
enum Stone {
    BLACK(Color.BLACK), WHITE(Color.WHITE), NONE(null);

    final Color colour;

    private Stone(Color c){
        colour = c;
    }

    public void paint(Graphics g, int d, Theme t){
        if(this == NONE) return; // no stone = no drawing needed
        g.setColor(colour); // set colour of stone to type of stone
        
        // Modify stone size based on board size
        if(d == 70){
            g.fillOval(3, 0, d-10, d-10);
            if(t == Theme.NOIR) g.setColor(Color.WHITE);
            else g.setColor(Color.BLACK);
            g.drawOval(3, 0, d-10, d-10);
        }
        else if(d == 48){
            g.fillOval(2, 0, d-6, d-6);
            if(t == Theme.NOIR) g.setColor(Color.WHITE);
            else g.setColor(Color.BLACK);
            g.drawOval(2, 0, d-6, d-6);
        }
        else{
            g.fillOval(1, 0, d-5, d-5);
            if(t == Theme.NOIR) g.setColor(Color.WHITE);
            else g.setColor(Color.BLACK);
            g.drawOval(1, 0, d-5, d-5);
        }
    }
}
