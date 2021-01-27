import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestUtils {
    // fixPikes - Smooths out positive spikes in an array.  Compares each point with the average of its neighbors
    //     and replaces items that are not within the maxPercentage of the averages.
    //
    // Example:
    // [ 10, 30, 20, 15, 50, 4124, 35, 42, 47, 60 ]
    //
    // When looking at the 6th element, we calculate the avg as ( 20+15+50 + 42+47+60)/6 = 39
    // We expect the 6th element to be less than 39 + (39*maxPercent), if not replace it with the average.
    //
    // Returns the number of elements that were "fixed".
    public static int fixSpikes( double[] values, double percentMax ) {
        int changeCount=0;

        // Got tired of having the first three items be crazy high. (JIT compile running)
        // Just always replace them with the average of 4, 5, and 6.
        // Same for last three items
        double avg = (values[4]+values[5]+values[6])/3.0;
        values[0] = values[1] = values[2] = avg;
        avg = (values[values.length-4]+values[values.length-5]+values[values.length-6])/3;
        values[values.length-1] = values[values.length-2] = values[values.length-3] = avg;

        for(int i = 3; i < values.length-3; i++) {
            avg = (values[i-3]+values[i-2]+values[i-1]+values[i+1]+values[i+2]+values[i+3])/6;

            // In theory the values could spike high and low,
            // we will ignore low spikes since that won't really happen when looking at speed
            // and can make the data look worse as the big spikes raise the value of its neighers.
            double max = avg + (long)(avg*percentMax);
            if( Math.abs(values[i] - avg) > max ) {
                values[i] = avg;
                changeCount++;
            }
        }
        return changeCount;
    }

    public static void writeDataPoints( String filename, String[] colNames, int numPoints, double[] ...pointArrays  )
    {
        try {
            if( pointArrays.length != colNames.length )
                throw new RuntimeException("Number of pts arrays not equal to number of column headers");

            BufferedWriter out = new BufferedWriter(new FileWriter(filename));

            String titleRow = String.join("\t", colNames);
            out.write(titleRow);
            out.write("\n");

            for (int i = 0; i < numPoints; i++) {
                for( int j=0; j<pointArrays.length-1; j++)
                    out.write(pointArrays[j][i] + "\t" );

                out.write(pointArrays[pointArrays.length-1][i] + "\n" );
            }
            out.close();
            System.out.println("File created successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String[] colNames = {"First", "Second"};
        int numPoints = 3;
        double[] first = {1,2,3};
        double[] second = {10,20,30};
        double[] third = {100,200,300};

        writeDataPoints("test.txt", colNames, numPoints, first, second );
    }
}
