import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        pq.add(1, 1);
        pq.add(2, 2);
        pq.add(3, 3);

        out = pq.peek(0);
        assert( out == 1 );
        out = pq.peek(1);
        assert( out == 2 );
        out = pq.peek(2);
        assert( out == 3 );

        out = pq.pull();
        assert( out == 1 );
        out = pq.pull();
        assert( out == 2 );
        out = pq.pull();
        assert( out == 3 );

        // Priority Queue should be empty
        pq.add(3, 3);
        pq.add(30, 3);
        pq.add(2, 2);
        pq.add(20, 2);
        pq.add(1, 1);
        pq.add(10, 1);

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
}
