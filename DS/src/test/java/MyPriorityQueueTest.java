import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.LongSummaryStatistics;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyPriorityQueueTest
{
    MyPriorityQueue<Integer> q;
    int out;

    @BeforeEach
    void init() {
        q = new MyPriorityQueue<>(5);
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
        q = new MyPriorityQueue<>(1000);
        long[] enqueueTimes = new long[1000];
        long[] dequeueTimes = new long[1000];
        long startTime, endTime;

        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            q.enqueue(i, i);
            endTime = System.nanoTime();
            enqueueTimes[i] = (endTime-startTime);
        }

        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            q.dequeue();
            endTime = System.nanoTime();
            dequeueTimes[i] = (endTime-startTime);
        }

        fixOutliers(enqueueTimes);
        fixOutliers(dequeueTimes);

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

    void fixOutliers( long[] values ) {
        // Trivial algorithm to replace values that are more than double the average
        // of the 4 surrounding points with the average. Gets rid of crazy java spikes.

        for(int i = 2; i < values.length-2; i++) {
            long avg = (values[i-2]+values[i-1]+values[i+1]+values[i+2])/4;
            if( Math.abs(values[i] - avg) > avg*1.2 ) {
                values[i] = avg;
            }
        }
    }
}
