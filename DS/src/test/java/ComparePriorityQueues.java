
import org.junit.jupiter.api.Test;

public class ComparePriorityQueues
{
    @Test
    void enqueueDequeueSpeedTest() {
        MyPriorityQueue<Integer> lpq = new MyPriorityQueue<>( 1000 );
        MyHeapPriorityQueue hpq = new MyHeapPriorityQueue(1000);

        // Hold on to the elapsed times until they can be written to a file at the end.
        double[] enqueueTimesHeap = new double[1000];
        double[] dequeueTimesHeap = new double[1000];
        double[] enqueueTimesLinked = new double[1000];
        double[] dequeueTimesLinked = new double[1000];
        long startTime, endTime;

        // Collect the times it takes to call enqueue.
        // The 0th time in the array is the time when there are zero items in the queue.
        // We start with an empty list and enqueue 1000 items.
        for( int i=0; i<1000; i++) {
            int priority = (int)(Math.random() * 10.0);
            startTime = System.nanoTime();
            hpq.enqueue(i, priority );  // enqueuing lowest priority first up to 1000  // );
            endTime = System.nanoTime();
            enqueueTimesHeap[i] = (endTime-startTime)/1000.0;  // Store usecs
            startTime = System.nanoTime();
            lpq.enqueue(i, priority );  // enqueuing lowest priority first up to 1000  // );
            endTime = System.nanoTime();
            enqueueTimesLinked[i] = (endTime-startTime)/1000.0;  // Store usecs
        }

        // Since the queue is full (1000 items) we populate the times array backwards
        for( int i=0; i<1000; i++) {
            startTime = System.nanoTime();
            hpq.dequeue();  // Ignore the output for now, just testing speed.
            endTime = System.nanoTime();
            dequeueTimesHeap[999-i] = (endTime-startTime)/1000.0;  // 999 - i fills the array backwards with the first dequeue going at the end.
            startTime = System.nanoTime();
            lpq.dequeue();  // Ignore the output for now, just testing speed.
            endTime = System.nanoTime();
            dequeueTimesLinked[999-i] = (endTime-startTime)/1000.0;  // 999 - i fills the array backwards with the first dequeue going at the end.
        }

        // See comment on the fixSpikes
        // Needs to be called over and over since there can still be bad points after smoothing.
        TestUtils.fixSpikes(enqueueTimesHeap, 1.0);
        while( TestUtils.fixSpikes(enqueueTimesHeap, 0.10) > 0 );  // No body of while! weird!
        TestUtils.fixSpikes(dequeueTimesHeap, 1.0);
        while( TestUtils.fixSpikes(dequeueTimesHeap, 0.10) > 0 );
        TestUtils.fixSpikes(enqueueTimesLinked, 1.0);
        while( TestUtils.fixSpikes(enqueueTimesLinked, 0.10) > 0 );  // No body of while! weird!
        TestUtils.fixSpikes(dequeueTimesLinked, 1.0);
        while( TestUtils.fixSpikes(dequeueTimesLinked, 0.10) > 0 );

        String[] colNames = {"HeapEnqueue", "HeapDequeue", "ArrayEnqueue", "ArrayDequeue"};
        TestUtils.writeDataPoints("timings.txt", colNames, enqueueTimesHeap.length, enqueueTimesHeap, dequeueTimesHeap, enqueueTimesLinked, dequeueTimesLinked );
    }
}
