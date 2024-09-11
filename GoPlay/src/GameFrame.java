import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame{
    // Variable Initialization
    private GamePanel panel;
    private JLabel player, time;
    private JButton pass, resign;
    private int Bpasses = 0, Wpasses = 0, row, col, whiteDead = 0, blackDead = 0, whiteTotal = 0, blackTotal = 0;
    private boolean tie;
    public boolean scoring;
    private Timer timer;
    long startTime, elapsedTime;
    public int count = 0;

    public GameFrame(String title, int boardSize, int w, int h, String t){
        this.setTitle(title); // set title of game frame
        this.setLayout(new BorderLayout()); // indicate type of layout
        
        player = new JLabel("<html><div style='text-align: center; font-size: 12pt;'>Current Player: </div><div style='text-align: center; font-size: 14pt;'>BLACK</div></html>");
        time = new JLabel("<html><div style='text-align: center; font-size: 12pt;'>Time: </div><div style='text-align: center; font-size: 14pt;'>" + millisecondsFormat(elapsedTime) + "</div></html>");
        pass = new JButton("Pass");
        resign = new JButton("Resign");

        // infoBar contains top bar with time and current player
        JPanel infoBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoBar.add(time);
        infoBar.add(Box.createHorizontalStrut(200));
        infoBar.add(player);

        // Add all buttons in FlowLayout
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(pass);
        buttons.add(resign);

        panel = new GamePanel(this, boardSize, t); // create a new GamePanel for game, used to access all variables

        this.add(infoBar, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER); // place game to center of frame
        this.add(buttons, BorderLayout.SOUTH);

        // 2 consecutive passes for either player
        pass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                // Check to see which player is passing
                if(panel.player) Bpasses++;
                else Wpasses++;

                // Update current player
                panel.player = !panel.player;
                updatePlayer();

                if(Bpasses == 2 || Wpasses == 2){
                    timer.stop();
                    scoringScreen(GameFrame.this); // open final scoring screen
                }
            }
        });
        
        // Resigning is instant win for the other player
        resign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                panel.audioPlayer.stop();
                //elapsedTime = System.currentTimeMillis() - startTime; // calculate final time
                timer.stop();
                endScreen(GameFrame.this);
            }
        });
        
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e){
                updateTimer();
            }
        });

        this.setResizable(false); // set frame as not resizable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(w, h); // set size of game frame
        this.setVisible(true);

        timer.start(); // starts timer
    }

    // update timer text
    private void updateTimer(){
        elapsedTime = System.currentTimeMillis() - startTime;
        time.setText("<html><div style='text-align: center; font-size: 12pt;'>Time: </div><div style='text-align: center; font-size: 14pt;'>" + millisecondsFormat(elapsedTime) + "</div></html>");
    }

    // Given row and column, get square
    public void getSquare(int r, int c){
        row = r;
        col = c;
    }

    // Update current player
    public void updatePlayer(){
        if(panel.getPlayer()) player.setText("<html><div style='text-align: center; font-size: 12pt;'>Current Player: </div><div style='text-align: center; font-size: 14pt;'>BLACK</div></html>");
        else player.setText("<html><div style='text-align: center; font-size: 12pt;'>Current Player: </div><div style='text-align: center; font-size: 14pt;'>WHITE</div></html>");
    }

    // Calculate winner of game
    public String getWinner(){
        if(panel.getPlayer()) return "WHITE";
        else return "BLACK";
    }

    // Convert milliseconds to minutes and seconds
    public String millisecondsFormat(long num){
        long seconds = elapsedTime / 1000;
        long secondsDisplay = seconds % 60;
        long minutes = seconds / 60;

        return minutes + " mins " + secondsDisplay + "s";
    }

    // Scoring Screen
    public void scoringScreen(JFrame parent){
        scoring = true; // prevent new stones from being added

        // Set previous click to invalid, lock buttons
        row = 0;
        col = 0;
        pass.setEnabled(false);
        resign.setEnabled(false);

        // Variable Initialization
        JDialog dialog = new JDialog(parent, "Scoring Screen");
        JPanel dialogPanel = new JPanel();
        JButton remove = new JButton("Remove Dead Stone");
        JLabel instructions = new JLabel("Click on a stone on the board to remove it.");
        JLabel piece = new JLabel("Piece removed at: PIECE NOT CHOSEN");
        JButton calculate = new JButton("Calculate Winner");
        JLabel whiteLabel = new JLabel("Number of white dead stones: 0");
        JLabel blackLabel = new JLabel("Number of black dead stones: 0");

        // Add text and buttons to JDialog
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.add(Box.createHorizontalStrut(10));
        dialogPanel.add(instructions);
        dialogPanel.add(Box.createHorizontalStrut(10));
        dialogPanel.add(piece);
        dialogPanel.add(Box.createHorizontalStrut(10));
        dialogPanel.add(remove);
        dialogPanel.add(Box.createHorizontalStrut(10));
        dialogPanel.add(whiteLabel);
        dialogPanel.add(Box.createHorizontalStrut(10));
        dialogPanel.add(blackLabel);
        dialogPanel.add(Box.createHorizontalStrut(10));
        dialogPanel.add(calculate);

        // Remove current stone (considered dead stone) and track how many white and black dead stones have been removed
        // User then determines the winner
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                // if a piece has been clicked since scoring board opened
                if(count >= 1){
                    piece.setText("Piece removed at: (" + row + ", " + col + ")");
                    if(panel.getStone(row, col) == Stone.WHITE) whiteDead++;
                    else if(panel.getStone(row, col) == Stone.BLACK) blackDead++;

                    panel.remove(row, col); // remove current clicked piece
                    whiteLabel.setText("Number of white dead stones: " + whiteDead);
                    blackLabel.setText("Number of black dead stones: " + blackDead);  
                }     
                else{ // if no piece selected
                    piece.setText("No piece selected to be removed");
                }         
            }
        });

        calculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                // Calculate which player has more stones on the board
                // In accordance to: https://jpolitz.github.io/notes/2012/12/25/go-termination.html
                whiteTotal = panel.countStones(Stone.WHITE);
                blackTotal = panel.countStones(Stone.BLACK);

                if(whiteTotal + whiteDead > blackTotal + blackDead) panel.player = true;
                else if(whiteTotal + whiteDead < blackTotal + blackDead) panel.player = false;
                else tie = true;
                dispose();
                endScreen(parent);               
            }
        });

        dialog.getContentPane().add(dialogPanel);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        dialog.requestFocus(); // prevents multiple JDialogs from opening
        dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    // End Screen
    public void endScreen(JFrame parent) {
        // Variable Initialization
        JDialog dialog = new JDialog(parent, "End Screen");
        JPanel dialogPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel winner = new JLabel();
        JLabel time = new JLabel("Game Time: " + millisecondsFormat(elapsedTime));
    
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        
        // Set text based on winner of game
        if(tie)
            winner.setText("There is a TIE!");
        else if(panel.getPlayer())
            winner.setText("WHITE has won!");
        else
            winner.setText("BLACK has won!");
        
        winner.setAlignmentX(Component.CENTER_ALIGNMENT);
        time.setAlignmentX(Component.CENTER_ALIGNMENT);

        dialogPanel.add(Box.createVerticalGlue());
        dialogPanel.add(winner);
        dialogPanel.add(Box.createVerticalStrut(10));
        dialogPanel.add(time);
        dialogPanel.add(Box.createVerticalGlue());
    
        // Add results of the game to the file
        FileReader.writeFile(getWinner() + "   " + millisecondsFormat(elapsedTime));

        dialog.getContentPane().add(dialogPanel);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.setModal(true); // freezes the main game screen
        dialog.setVisible(true);
        dialog.requestFocus(); // prevents multiple JDialogs from opening
    
        setVisible(false); // close the game frame
        dispose();
        System.exit(0); // end the program
    }
}
