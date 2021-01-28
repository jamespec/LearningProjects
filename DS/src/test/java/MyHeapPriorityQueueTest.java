import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyHeapPriorityQueueTest
{
    MyHeapPriorityQueue q;
    int out;

    @BeforeEach
    void init() {
        q = new MyHeapPriorityQueue(5);
    }

    @Test
    void enqueueDequeueTest1() {
        q.enqueue(1, 1);
        out=q.dequeue();
        assert( out == 1 );
    }

    @Test
    void enqueueDequeueTest2() {
        q.enqueue(1, 1);
        q.enqueue(2, 2);
        q.enqueue(3, 3);
        out=q.dequeue();
        assert( out == 3 );
        out=q.dequeue();
        assert( out == 2 );
        out=q.dequeue();
        assert( out == 1 );
        assert( q.size() == 0 );
    }

    @Test
    void whenEnqueueTooManyThrowsBufferOverflowException() {
        q.enqueue(1, 5);
        q.enqueue(2, 4);
        q.enqueue(3, 2);
        q.enqueue(4, 1);
        q.enqueue(5, 3);

        Exception exception = assertThrows(BufferOverflowException.class, () -> {
            q.enqueue(6, 1);
        });
    }

    @Test
    void whenDequeueTooManyThrowsBufferUnderflowException() {
        q.enqueue(1, 2);
        q.enqueue(2, 1);
        q.dequeue();
        q.dequeue();

        Exception exception = assertThrows(BufferUnderflowException.class, () -> {
            q.dequeue();
        });
    }

    @Test
    void enqueueDequeueSpeedTest() {
        q = new MyHeapPriorityQueue(1000);

        // Hold on to the elapsed times until they can be written to a file at the end.
        double[] enqueueTimes = new double[1000];
        double[] dequeueTimes = new double[1000];
        long startTime, endTime;

        // Collect the times it takes to call enqueue.
        // The 0th time in the array is the time when there are zero items in the queue.
        // We start with an empty list and enqueue 1000 items.
        for( int i=0; i<1000; i++) {
            int priority = (int)(Math.random() * 10.0);
            startTime = System.nanoTime();
            q.enqueue(i, priority );  // enqueuing lowest priority first up to 1000  // );
            endTime = System.nanoTime();
            enqueueTimes[i] = (endTime-startTime)/1000.0;  // Store usecs
        }

        // Since the queue is full (1000 items) we populate the times array backwards
        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            q.dequeue();  // Ignore the output for now, just testing speed.
            endTime = System.nanoTime();
            dequeueTimes[999-i] = (endTime-startTime)/1000.0;  // 999 - i fills the array backwards with the first dequeue going at the end.
        }

        // See comment on the fixSpikes
        // Needs to be called over and over since there can still be bad points after smoothing.
        TestUtils.fixSpikes(enqueueTimes, 1.0);
        while( TestUtils.fixSpikes(enqueueTimes, 0.10) > 0 );  // No body of while! weird!
        TestUtils.fixSpikes(dequeueTimes, 1.0);
        while( TestUtils.fixSpikes(dequeueTimes, 0.10) > 0 );

        String[] colNames = {"Enqueue", "Dequeue"};
        TestUtils.writeDataPoints("timings.txt", colNames, enqueueTimes.length, enqueueTimes, dequeueTimes );
    }
}
