import java.io.*;
import java.util.*;

public class FileReader {
    public static final String GAME_RECORDS = "GoPlay\\src\\game_records.txt";
    public static ArrayList<String> records = new ArrayList<>();

    // Read records in text file
    public static void readFile(){
        try{
            File GAME_RECORDS = new File("GoPlay\\src\\game_records.txt");
            Scanner scan = new Scanner(GAME_RECORDS);

            while(scan.hasNextLine()){
                String line = scan.nextLine();
                records.add(line);
            }
            scan.close();
        }
        catch(FileNotFoundException e){
            System.out.println("File not found!");
        }
    }

    // Add new game records to the text file
    public static void writeFile(String info){
        try {
            FileWriter writer = new FileWriter("GoPlay\\src\\game_records.txt", true);
            writer.write(info + "\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occured.");
        }
    }

    // Count the number of records found in the text file
    public int counter(){
        int count = 0;
        try{
            File GAME_RECORDS = new File("GoPlay\\src\\game_records.txt");
            Scanner scan = new Scanner(GAME_RECORDS);

            while(scan.hasNextLine()){
                count++;
            }
            scan.close();
        }
        catch(FileNotFoundException e){
            System.out.println("File not found!");
        }
        return count;
    }
}
