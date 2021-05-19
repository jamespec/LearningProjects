import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Project3 {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("players.txt"));
            String data = reader.readLine();
            Hashtable myTable = new Hashtable(11);
            while(data != null){
                String[] params = data.split(" ");
                if(params.length == 2) {
                    String f = params[0];
                    String l = params[1];
                    Player newPlayer = new Player(f, l);
                    int i = newPlayer.hashCode();
                    int j = newPlayer.hashCode2();
                    System.out.println(i + " " + j);
                    myTable.insert(newPlayer);
                }
                data = reader.readLine();
            }
        }
        catch(IOException exception){
            exception.printStackTrace();
        }
    }
}
