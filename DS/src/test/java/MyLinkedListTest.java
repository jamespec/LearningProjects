import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyLinkedListTest
{
    MyLinkedList<Integer> ll;
    Integer out;

    @BeforeEach
    void beforeEach() {
        ll = new MyLinkedList<>();
    }

    @Test
    void insertAtPosAppendSizeAndPeekCheck() {
        // This creates the list: [ 10, 15, 20, 25 30, 40 ]
        ll.append(10);
        ll.append(20);
        ll.append(30);
        ll.insertAtPos(15, 1);
        ll.insertAtPos(25, 3);
        ll.append(40);

        out = ll.peek(0);
        assert( out == 10 );
        out = ll.peek(1);
        assert( out == 15 );
        out = ll.peek(2);
        assert( out == 20 );
        out = ll.peek(3);
        assert( out == 25 );
        out = ll.peek(4);
        assert( out == 30 );
        out = ll.peek(5);
        assert( out == 40 );

        out = ll.size();
        assert( out == 6 );
    }

    @Test
    void popCheck() {
        // This creates the list: [ 10, 15, 20, 25, 30, 40 ]
        ll.append(10);
        ll.append(20);
        ll.append(30);
        ll.insertAtPos(15, 1);
        ll.insertAtPos(25, 3);
        ll.append(40);

        assert( ll.size() == 6 );

        out = ll.pop();
        assert( out == 10 );
        assert( ll.size() == 5 );

        out = ll.pop();
        assert( out == 15 );
        assert( ll.size() == 4 );

        out = ll.pop();
        assert( out == 20 );
        assert( ll.size() == 3 );

        out = ll.pop();
        assert( out == 25 );
        assert( ll.size() == 2 );

        out = ll.pop();
        assert( out == 30 );
        assert( ll.size() == 1 );

        out = ll.pop();
        assert( out == 40 );
        assert( ll.size() == 0 );

        out = ll.pop();
        assert( out == null );
        assert( ll.size() == 0 );
    }

    @Test
    void appendSpeedCheck()
    {
        int[] counts = { 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000 };

        // For each count
        for( int j=0; j<counts.length; j++) {
            int count = counts[j];

            // Run the test 5 times and save results
            long[] appendResults = new long[5];
            long[] popResults = new long[5];
            for( int k=0; k<5; k++) {
                MyLinkedList<Integer> mll = new MyLinkedList<>();
                long startTime = System.nanoTime();
                for (int i = 0; i < count; i++) {
                    mll.append(i * 10);
                }
                long endTime = System.nanoTime();
                appendResults[k] = (endTime-startTime);

                long startTime2 = System.nanoTime();
                for (int i = 0; i < count; i++) {
                    mll.pop();
                }
                long endTime2 = System.nanoTime();
                popResults[k] = (endTime2-startTime2);
            }

            long appendAverage = calcAverage(appendResults);
            long popAverage = calcAverage(popResults);
            System.out.println("Average time to insert " +
                    count + ": " + appendAverage/1000000 + " msecs, "
                    + (appendAverage/count)/1000 + " usecs each");

            System.out.println("Average time to pop " +
                    count + ": " + popAverage/1000 + " usecs, "
                    + (popAverage/count) + " nsecs each");
        }
    }

    long calcAverage( long[] values )
    {
        // Throw away the largest and smallest result
        long largest = values[0];
        long smallest = values[0];
        long total=0;
        for( int k=0; k<values.length; k++) {
            if( values[k] < smallest )
                smallest = values[k];
            if( values[k] > largest )
                largest = values[k];

            total += values[k];
        }

        return (total - smallest - largest)/(values.length-2);
    }
}
