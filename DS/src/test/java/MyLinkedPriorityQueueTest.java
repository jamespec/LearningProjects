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
        double[] enqueueTimes = new double[1000];
        double[] dequeueTimes = new double[1000];
        long startTime, endTime;

        // Collect the times it takes to call enqueue.
        // The 0th time in the array is the time when there are zero items in the queue.
        // We start with an empty list and enqueue 1000 items.
        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            // The priority makes all the difference since it drives
            // where in the list a new item goes.
            // enqueuing in priority order makes enqueue O(1)
            // enqueuing in random priority show a graph showing the tendency for O(n)
            // enqueuing in reverse priority order makes it O(n) (worst case)
            // Therefore, by empirical evidence we call this O(n)
            // pq.enqueue(i, (int)(Math.random() * 1000.0));
            // pq.enqueue(i, (int)(Math.random() * 2.0));
            // pq.enqueue(i, 1000-i);
            pq.enqueue(i, i);
            endTime = System.nanoTime();
            enqueueTimes[i] = (endTime-startTime)/1000.0;
        }

        // Since the queue is full (1000 items) we populate the times array backwards
        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            pq.dequeue();
            endTime = System.nanoTime();
            dequeueTimes[999-i] = (endTime-startTime)/1000.0;
        }

        // See comment on the fixSpikes
        // Needs to be called over and over since there can still be bad points after smoothing.
        TestUtils.fixSpikes(enqueueTimes, 2.0);
        while( TestUtils.fixSpikes(enqueueTimes, 0.20) > 0 );  // No body of while! weird!
        TestUtils.fixSpikes(dequeueTimes, 2.0);
        while( TestUtils.fixSpikes(dequeueTimes, 0.20) > 0 );

        String[] colNames = {"Enqueue", "Dequeue"};
        TestUtils.writeDataPoints("timings.txt", colNames, enqueueTimes.length, enqueueTimes, dequeueTimes );
    }
}
