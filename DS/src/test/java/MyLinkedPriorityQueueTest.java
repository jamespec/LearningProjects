import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MyLinkedPriorityQueueTest
{
    MyLinkedPriorityQueue<Integer> pq;
    int out;

    @BeforeEach
    void beforeEach() {
        pq = new MyLinkedPriorityQueue<>();
    }

    @Test
    void addCheck() {
        pq.enqueue(1, 1);
        pq.enqueue(2, 2);
        pq.enqueue(3, 3);

        out = pq.peek(0);
        assert( out == 1 );
        out = pq.peek(1);
        assert( out == 2 );
        out = pq.peek(2);
        assert( out == 3 );

        out = pq.dequeue();
        assert( out == 1 );
        out = pq.dequeue();
        assert( out == 2 );
        out = pq.dequeue();
        assert( out == 3 );

        // Priority Queue should be empty
        pq.enqueue(3, 3);
        pq.enqueue(30, 3);
        pq.enqueue(2, 2);
        pq.enqueue(20, 2);
        pq.enqueue(1, 1);
        pq.enqueue(10, 1);

        out = pq.peek(0);
        assert( out == 1 );
        out = pq.peek(1);
        assert( out == 10 );
        out = pq.peek(2);
        assert( out == 2 );
        out = pq.peek(3);
        assert( out == 20 );
        out = pq.peek(4);
        assert( out == 3 );
        out = pq.peek(5);
        assert( out == 30 );
    }

    @Test
    void enqueueDequeueSpeedTest() {
        pq = new MyLinkedPriorityQueue<>();
        long[] enqueueTimes = new long[1000];
        long[] dequeueTimes = new long[1000];
        long startTime, endTime;

        // Collect the times it takes to call enqueue.
        // The 0th time in the array is the time when there are zero items in the queue.
        // We start with an empty list and enqueue 1000 items.
        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            // The priority makes all the difference since it drives
            // where in the list a new item goes.
            // enqueuing in priority order makes enqueue O(1)
            // enqueuing in reverse priority order makes it O(n) (worst case)
            // enqueuing in random priority show a graph showing the tendency for O(n)
            // but unpredictable for any given operation.
            // pq.enqueue(i, (int)(Math.random() * 1000.0));
            // pq.enqueue(i, 1000-i);
            pq.enqueue(i, i);
            endTime = System.nanoTime();
            enqueueTimes[i] = (endTime-startTime);
        }

        // Since the queue is full (1000 items) we populate the times array backwards
        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            pq.dequeue();
            endTime = System.nanoTime();
            dequeueTimes[999-i] = (endTime-startTime);
        }

        while( fixOutliers(enqueueTimes) > 0 );
        while( fixOutliers(dequeueTimes) > 0 );

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("timings.txt"));
            for( int i=0; i<1000; i++) {
                out.write( enqueueTimes[i] + "\t" + dequeueTimes[i] + "\n");
            }
            out.close();
            System.out.println("File created successfully");
        }
        catch (IOException e) {
        }
    }

    // Replace an item with the average of the surrounding elements if the item
    // is 30% larger than average.  Return the number of changed elements.
    int fixOutliers( long[] values ) {
        // Trivial algorithm to replace values that are more than double the average
        // of the 4 surrounding points with the average. Gets rid of crazy java spikes.

        int changeCount=0;
        for(int i = 3; i < values.length-3; i++) {
            long avg = (values[i-3]+values[i-2]+values[i-1]+values[i+1]+values[i+2]+values[i+3])/6;

            // We should be using abs of the difference but because we know the anomalies never make
            // the code run faster we'll only concern ourselve with point that are higher than norm.
            if( values[i] - avg > avg*1.3 ) {
                values[i] = avg;
                changeCount++;
            }
        }
        return changeCount;
    }
}
