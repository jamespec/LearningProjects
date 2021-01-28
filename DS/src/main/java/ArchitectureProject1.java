import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.Scanner;

public class ArchitectureProject1
{
    static byte myFirstInitial = 'J';
    static byte myLastInitial = 'P';

    // Compare the lowest order 8 bits between arg1 and arg2
    // return the number of matches.
    static int countMatches( int arg1, int arg2 )
    {
        int mask = 0b00000001;
        int matchCount=0;
        for(int i=0; i<8; i++) {
            if( (arg1 & mask) == (arg2 & mask) )
                matchCount++;

            mask <<= 1;
        }
        return matchCount;
    }

    static String formatBits(int arg, int size)
    {
        if( size > 32 )
            throw new ValueException("Max of 32 bit width");

        String zeros = "00000000000000000000000000000000";
        String value = Integer.toBinaryString(arg);

        return "0b" + zeros.substring(0, size - value.length()) + value;
    }

    static int firstInitialMatches( int arg )
    {
        int matchCount = countMatches( arg, myFirstInitial);

        System.out.println( "\"" + (char)(arg) + "\": " + formatBits(arg, 8)
                + " \"" + (char)(myFirstInitial) + "\": " + formatBits(myFirstInitial, 8)
                + " matchCount: " + matchCount );

        return matchCount;
    }

    static int lastInitialMatches( int arg )
    {
        int matchCount = countMatches( arg, myLastInitial);

        System.out.println( "\"" + (char)(arg) + "\": " + formatBits(arg, 8)
                + " \"" + (char)(myLastInitial) + "\": " + formatBits(myLastInitial, 8)
                + " matchCount: " + matchCount );

        return matchCount;
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

            System.out.println("Matches, first input to first initial: " + matchesFirstLetterToFirstInitial);
            System.out.println("Matches, first input to Last initial: " + matchesFirstLetterToLastInitial);
            System.out.println("Matches, second input to first initial: " + matchesSecondLetterToFirstInitial);
            System.out.println("Matches, second input to last initial: " + matchesSecondLetterToLastInitial);

            if( matchesFirstLetterToFirstInitial == 8 && matchesSecondLetterToLastInitial == 8 ) {
                System.out.println("My initials!");
            }
        }
    }
}
