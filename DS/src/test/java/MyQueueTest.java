import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyQueueTest
{
    MyQueue<Integer> q;
    int out;

    @BeforeEach
    void init() {
        q = new MyQueue<>(5);
    }

    @Test
    void enqueueDequeueTest1() {
        q.enqueue(1);
        out=q.dequeue();
        assert( out == 1 );
    }

    @Test
    void enqueueDequeueTest2() {
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        out=q.dequeue();
        assert( out == 1 );
        out=q.dequeue();
        assert( out == 2 );
        out=q.dequeue();
        assert( out == 3 );
    }

    @Test
    void whenEnqueueTooManyThrowsBufferOverflowException() {
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);

        Exception exception = assertThrows(BufferOverflowException.class, () -> {
            q.enqueue(6);
        });
    }
}
