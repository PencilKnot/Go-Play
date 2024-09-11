import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
 * Need to consider "ko" case in Go
 */

public class GamePanel extends JPanel {
    private GameFrame frame;
    public Square[][] board; // create game board made of Square objects
    public boolean player; // boolean to indicate who will play
    private int size;
    private Theme currentTheme;
    
    private ArrayList<Square> liberties = new ArrayList<>(); // store position of liberties
    private ArrayList<Square> group = new ArrayList<>(); // store position of all stones in a group
    
    public AudioPlayer audioPlayer = new AudioPlayer();

    // Setup GamePanel
    public GamePanel(GameFrame frame, int d, String theme) {
        audioPlayer.play("GoPlay\\src\\zen_background.wav");
        frame.startTime = System.currentTimeMillis(); // set current time (time game started)
        this.frame = frame;
        size = d;
        setTheme(theme);

        board = new Square[d][d];
        player = true; // BLACK starts the game
        boardSetup(d);
    }

    // Set current board theme from user choice
    public void setTheme(String t){
        if(t.equals("Classic")) currentTheme = Theme.CLASSIC;
        else if(t.equals("Noir")) currentTheme = Theme.NOIR;
        else currentTheme = Theme.OCEAN;
    }

    // Set up board
    private void boardSetup(int d) {
        super.setLayout(new GridLayout(d, d)); // use grid layout to align go pieces
        for(int r = 0; r < d; r++) {
            for(int c = 0; c < d; c++) {
                board[r][c] = new Square(r, c, false, false, currentTheme);
                super.add(board[r][c]); // add each square to the overall JFrame in grid layout
            }
        }
        repaint();
    }

    // Returns current player
    public boolean getPlayer(){
        return player;
    }

    // Given Square, check for it's piece's group and liberties
    public void check(int row, int col, Color c){
        Square s = board[row][col];
        Stone piece = s.stone; // get game piece on given square
        
        // if there is a stone placed on the square, it is the color we are looking for and the piece has not been marked yet
        if(piece != Stone.NONE && c == piece.colour && !s.marked){
            group.add(s); // arraylist with all stone coordinates
            s.marked = true; // mark stone as checked

            /*
             * find neighbouring stones recursively (find group)
             * need to check if stone is not on edge and check square in all 4 directions
             */
            // if square at row 0, no stones to search
            if(row > 0) check(row - 1, col, piece.colour); // check square above
            if(row < size - 1) check(row + 1, col, piece.colour); // check square below
            if(col > 0) check(row, col - 1, piece.colour); // check square to the left
            if(col < size - 1) check(row, col + 1, piece.colour); // check square to the right
        }
        // if no stone on square, mark it as liberty
        else if(piece == Stone.NONE){
            s.liberty = true;
            liberties.add(s);
        }
    }

    // Clear list and stones
    public void clearLists(){
        liberties.clear();
        group.clear();

        // clear marked status for all stones
        for(int r = 0; r < size; r++)
            for(int c = 0; c < size; c++)
                board[r][c].marked = false;
    }

    // Format Arraylist of Squares as list of coordinates
    public String ArrayListtoString(ArrayList<Square> arr){
        String str = "";
        Square s;
        for(int i = 0; i < arr.size(); i++){
            s = arr.get(i);
            str += "(" + s.row + ", " + s.col + "), ";
        }
        return str;
    }

    // returns Group
    public String getGroup(){
        return ArrayListtoString(group);
    }

    // returns Liberties
    public String getLiberties(){
        return ArrayListtoString(liberties);
    }

    // Returns stone given coordinates
    public Stone getStone(int r, int c){
        return board[r][c].stone;
    }

    // remove a stone given coordinates
    public void remove(int r, int c){
        board[r][c].stone = Stone.NONE;
        repaint();
    }

    // remove captured stones
    public void removeCaptured(int r, int c){
        if(group.size() == 0) remove(r, c);
        else{
            for(int i = 0; i < group.size(); i++){
                remove(group.get(i).row, group.get(i).col);
            }
        }
    }

    // Count number of stones given an example (its colour)
    public int countStones(Stone s){
        int counter = 0;
        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                if(board[row][col].stone == s) counter++;
            }
        }
        return counter;
    }

    // Check the screen for any captured pieces
    public void checkScreen(){
        int empty = 0;
        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                Stone s = board[row][col].stone;
                // if there is a stone at that square
                if(s != Stone.NONE){
                    check(row, col, s.colour);
                    if(liberties.size() == 0) removeCaptured(row, col);
                    else clearLists();
                    repaint();
                }
                else empty++; // if no stone increment counter
            }
        }
        // 
        if(empty == 1) frame.scoringScreen(frame);
    }

    // Draw square (center is where stone is placed)
    // Cannot seperate into other class for some reason because player, board
    public class Square extends JPanel {
        private Stone stone;
        private int row, col;
        private boolean marked, liberty; // to determine if there is liberty
        private Theme theme;

        Square(int r, int c, boolean m, boolean l, Theme t){
            stone = Stone.NONE;
            row = r;
            col = c;
            marked = m;
            liberty = l;
            theme = t;

            super.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e){
                    // If scoring screen is open, prevent new pieces from being played
                    if(frame.scoring){
                        // user cannot remove empty square for dead stone
                        if(stone != Stone.NONE){
                            frame.count++;
                            frame.getSquare(row, col);
                        }
                    }
                    else{
                        frame.getSquare(row, col); // send square to GameFrame
                        if(stone != Stone.NONE) return; //if there is already a stone at that intersection
                        if(player) stone = Stone.BLACK;
                        else stone = Stone.WHITE;
                        checkScreen(); // check if added piece has caused capture of others

                        player = !player; // change current player
                        frame.updatePlayer(); // update the current player
                    }
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // called original paintComponent method
            int w = super.getWidth(), h = super.getHeight(); // get width/height of square
            
            // Set background and line colour based on theme
            if(currentTheme == Theme.CLASSIC){
                g.setColor(new Color(219,179,91));
                g.fillRect(0, 0, w, h);
                g.setColor(Color.BLACK);
            }
            else if(currentTheme == Theme.NOIR){
                g.setColor(new Color(49, 48, 46));
                g.fillRect(0, 0, w, h);
                g.setColor(Color.WHITE);
            }
            else{
                g.setColor(new Color(160, 201, 241));
                g.fillRect(0, 0, w, h);
                g.setColor(Color.BLUE);
            }

            // check if square is on the edges of game board
            // let center of each square (where piece is placed) be intersection (draw line in the center)
            if(row == 0 || row == board.length-1 || col == 0 || col == board.length-1) {
                // left edge of board
                if(col == 0) {
                    g.drawLine(w/2, h/2, w, h/2);
                    // top left corner of board
                    if(row == 0) g.drawLine(w/2, h/2, w/2, h);
                    // bottom left corner of board
                    else if(row == size - 1) g.drawLine(w/2, h/2, w/2, 0);
                    // anything in between
                    else g.drawLine(w/2, 0, w/2, h);
                }
                // right edge of board
                else if(col == size - 1) { // how to modify this for different board sizes
                    g.drawLine(0, h/2, w/2, h/2);
                    if(row == 0) g.drawLine(w/2, h/2, w/2, h);
                    else if(row == size - 1) g.drawLine(w/2, h/2, w/2, 0);
                    else g.drawLine(w/2, 0, w/2, h);
                }
                // top edge of board
                else if(row == 0) {
                    g.drawLine(0, h/2, w, h/2);
                    g.drawLine(w/2, h/2, w/2, h);
                }
                // bottom edge of board
                else {
                    g.drawLine(0, h/2, w, h/2);
                    g.drawLine(w/2, h/2, w/2, 0);
                }
            } else {
                g.drawLine(0, h/2, w, h/2);
                g.drawLine(w/2, 0, w/2, h);
            }
            stone.paint(g, w, theme);
        }
    }
}
