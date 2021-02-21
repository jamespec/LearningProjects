import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generate
{
    public static void main(String[] args)
    {
        if( args.length != 3 ) {
            System.out.println("Usage: java Generate <num of data rows as power of 2> <type: d, i, s> <range of values>");
            System.exit(1);
        }

        int powerOfTwo = Integer.parseInt(args[0]);
        String type = args[1];
        int range = Integer.parseInt(args[2]);
        int rowsToOutput = (int)Math.pow(2.0, powerOfTwo);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("data.txt"));

            out.write(type + " " + range + "\n");

            for (int i = 0; i < rowsToOutput; i++) {
                switch ( type.toLowerCase() ) {
                    case "d":  // Double
                        out.write( Math.random() * range + "\n" );
                        break;
                    case "i": // Integer
                        out.write( (int)(Math.random() * range) + "\n" );
                        break;
                    case "s":
                        out.write( randomString(range) + "\n" );
                        break;
                }
            }
            out.close();
            System.out.println("File created successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String randomString(int length)
    {
        StringBuilder builder = new StringBuilder();

        for(int i=0; i<length; i++) {
            builder.append( (char)((int)(Math.random() * 26) + 'A') );
        }

        return builder.toString();
    }
}
