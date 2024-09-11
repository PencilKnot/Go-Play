import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

// Create start screen with game setup
public class StartPanel extends JFrame{
    // Variable Initialization
    private int size;
    private String theme;

    public StartPanel(){
        // Variable Initialization
        JButton start = new JButton("Start");
        JButton records = new JButton("Records");
        JButton help = new JButton("Help");
        JLabel title = new JLabel("<html><div style='text-align: center; font-size: 18pt;'>Welcome to GoPlay!</div></html>");
        JLabel description = new JLabel("<html><div style='text-align: center; font-size: 12pt;'>A Go Game Multiplayer</div></html>");
        JLabel sizeLabel = new JLabel("Sizes:");
        JLabel themeLabel = new JLabel("Themes:");
        String[] boardSize = {"9 x 9", "13 x 13", "19 x 19"};
        String[] themeType = {"Classic", "Noir", "Ocean"};
        JComboBox <String> sizes = new JComboBox<>(boardSize);
        JComboBox <String> themes = new JComboBox<>(themeType);

        // Set dimensions of drop-down tables
        sizes.setMaximumSize(new Dimension(100, 25));
        themes.setMaximumSize(new Dimension(100, 25));

        // When help clicked, open instructions page
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelpDialog(StartPanel.this);
            }
        });

        // When start clicked, get board size and theme for GameFrame
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                String selectedSize = (String) sizes.getSelectedItem();
                String[] section = selectedSize.split("x");
                size = Integer.parseInt(section[0].trim());
                theme = (String) themes.getSelectedItem();

                dispose();

                // start new game screen with given settings
                new GameFrame("GoPlay", size, 650, 700, theme);
            }
        });

        // Display previous game records
        records.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                recordsScreen(StartPanel.this);
            }
        });

        // Add all text and buttons to panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Setup panel (title, description, drop down)
        JPanel setup = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 60, 0, 0);
        setup.add(title, gbc);

        gbc.gridy = 1;
        setup.add(description, gbc);
        gbc.insets = new Insets(10, 0, 0, 0);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        setup.add(sizeLabel, gbc);

        gbc.gridx = 1;
        setup.add(sizes, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        setup.add(themeLabel, gbc);

        gbc.gridx = 1;
        setup.add(themes, gbc);
        
        // Buttons panel
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(start);
        buttons.add(help);
        buttons.add(records);

        // Add all panels to main panel
        panel.add(Box.createVerticalStrut(20));
        panel.add(setup);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttons);

        // Set up the JFrame
        this.setTitle("Start Screen");
        this.setSize(400, 250);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(panel, BorderLayout.NORTH);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
    }

    // Help/instructions page
    private void showHelpDialog(JFrame parent) {
        JDialog helpDialog = new JDialog(parent, "Game Instructions");
        JPanel helpPanel = new JPanel();
        JTextPane helpTextPane = new JTextPane();

        // HTML help message
        String helpMessage = "<html><u>Setup</u><br>" +
                "- Use drop-down tables to choose board size and board theme<br>" +
                "- Previous gameplay records can be viewed at <b>Records</b> button on start screen<br><br>" +
                "<u>In-Game</u><br>" +
                "- Click on any intersection to place black or white Go pieces, depending on the current player<br>" +
                "- Computer will automatically remove any captured stone on the board (edge cases like <i>ko</i> are not considered)<br>" +
                "- Timer found at top left, current player found at top right<br><br>" +
                "<u>Ending the Game</u><br>" +
                "- Resign results in instant win for other player<br>" +
                "- Two consecutive passes from a player ends game and opens scoring screen<br>" +
                "- Click on a dead stone on the board, the click <b>Remove Dead Stones</b> button of endgame screen to remove it<br>" +
                "- <b>Determine Winner</b> button calculates final winner of game and stores it</html>";

        helpTextPane.setContentType("text/html");
        helpTextPane.setText(helpMessage);
        helpTextPane.setEditable(false);
        helpTextPane.setBackground(parent.getBackground());

        helpPanel.add(helpTextPane);

        helpDialog.getContentPane().add(helpPanel);
        helpDialog.setSize(650, 320);
        helpDialog.setResizable(false);
        helpDialog.setLocationRelativeTo(parent);
        helpDialog.setModal(true);
        helpDialog.setVisible(true);
    }

    // Game records page
    private void recordsScreen(JFrame parent){
        FileReader.records.clear(); // clear data if screen was opened recently
        FileReader.readFile(); // read records

        // Variable Initialization
        JDialog dialog = new JDialog(parent, "Gameplay Records");
        JPanel dialogPanel = new JPanel();
        JLabel title = new JLabel("Each line is a game; shows winning colour and total game time");

        // Get list of records and put into JScrollPane
        String[] list = new String[FileReader.records.size()];
        for(int i = 0; i < FileReader.records.size(); i++)
            list[i] = FileReader.records.get(i);

        JList<String> records = new JList<String>(list);
        JScrollPane scrollPane = new JScrollPane(records);
        scrollPane.setBorder(null);
        scrollPane.setBackground(dialogPanel.getBackground());
        
        // Add text and scrollPane to main panel
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.add(Box.createVerticalStrut(10));
        dialogPanel.add(title);
        dialogPanel.add(Box.createVerticalStrut(20));
        dialogPanel.add(Box.createHorizontalGlue());
        dialogPanel.add(scrollPane);
        dialogPanel.add(Box.createHorizontalGlue());
        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(dialogPanel);
        dialog.setSize(400, 200);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.requestFocus(); // prevents multiple JDialogs from opening
    }
}
