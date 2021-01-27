import java.util.Scanner;

public class ArchitectureProject1
{
    static byte myFirstInitial = 'J';
    static byte myLastInitial = 'P';

    // ^ is the bitwise Exclusive OR (XOR) operator
    // for each bit, if one of the bits is a 1 but not both the output is a 1.
    // Said another way: from each bit, if the bits are different the output is a 1 else 0.
    // e.g  0b1010 1010 ^
    //      0b0110 1100 =
    //      ------------
    //      0b1100 0110
    // So, zero in each place means they match!
    // Counts the zero to know how many bits match.

    static int firstInitialMatches( int arg )
    {
        int xord = (arg ^ myFirstInitial);
        int zeroCount = countZeros(xord);

        System.out.println( "\"" + (char)(arg) + "\": " + formatBits(arg, 8)
                + " \"" + (char)(myFirstInitial) + "\": " + formatBits(myFirstInitial, 8)
                + " XOR: " + formatBits(xord, 8)
                + " zeroCount: " + zeroCount );

        return zeroCount;
    }

    static int lastInitialMatches( int arg )
    {
        int xord = (arg ^ myLastInitial);
        int zeroCount = countZeros(xord);

        System.out.println("\"" + (char)arg + "\": " + formatBits(arg, 8)
                + " \"" + (char)(myLastInitial) + "\": " + formatBits(myLastInitial, 8)
                + " XOR: " + formatBits(xord, 8)
                + " zeroCount: " + zeroCount );

        return zeroCount;
    }

    static int countZeros( int arg )
    {
        int mask = 0b00000001;
        int zeroCount = 0;

        for(int i=0; i<8; i++) {
            // Single & is the bitwise AND operator.
            // Each bit is ANDed and the output is a 1 only if both bits are a 1
            if ((arg & mask) == 0)
                zeroCount++;

            // This says to shift the bits of mask left by 1 place, mask = (mask << 1)
            // e.g. 0b00000001 becomes 0b00000010, called again: 0b00000100, then 0b00001000
            mask <<= 1;
        }

        return zeroCount;
    }

    static String formatBits(int arg, int size)
    {
        String value = Integer.toBinaryString(arg);
        char[] format = new char[ Math.max(size, value.length())+2];

        format[0] = '0';
        format[1] = 'b';
        for( int i=2; i<size+2; i++)
            format[i] = '0';

        for( int i=1; i<=value.length(); i++ )
            format[format.length-i] = value.charAt(value.length()-i);

        return new String( format );
    }

    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        while(true) {
            System.out.print("Enter two letters to compare: ");
            String entry = keyboard.nextLine();

            if( entry.toLowerCase().equals("quit") )
                break;

            if( entry.length() != 2 ) {
                System.out.println("Two letters only, please.");
                continue;
            }

            int matchesFirstLetterToFirstInitial = ArchitectureProject1.firstInitialMatches(entry.charAt(0));
            int matchesFirstLetterToLastInitial = ArchitectureProject1.lastInitialMatches(entry.charAt(0));
            int matchesSecondLetterToFirstInitial = ArchitectureProject1.firstInitialMatches(entry.charAt(1));
            int matchesSecondLetterToLastInitial = ArchitectureProject1.lastInitialMatches(entry.charAt(1));

            System.out.println("Matches, first letter to first initial: " + matchesFirstLetterToFirstInitial);
            System.out.println("Matches, first letter to Last initial: " + matchesFirstLetterToLastInitial);
            System.out.println("Matches, second letter to first initial: " + matchesSecondLetterToFirstInitial);
            System.out.println("Matches, second letter to last initial: " + matchesSecondLetterToLastInitial);

            if( matchesFirstLetterToFirstInitial == 8 && matchesSecondLetterToLastInitial == 8 ) {
                System.out.println("My initials!");
            }
        }
    }
}
