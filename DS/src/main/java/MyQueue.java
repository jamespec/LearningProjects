import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class MyQueue<T>
{
    T[] data;
    int size;
    int head;
    int numEnqueued;

    MyQueue(int size ) {
        this.size = size;
        this.data = (T[]) new Object[size];
        this.head = 0;
        this.numEnqueued = 0;
    }

    void enqueue( T v ) {
        if( numEnqueued < size ) {
            data[(head + numEnqueued) % size] = v;
            numEnqueued++;
        }
        else
            throw new BufferOverflowException();
    }

    T dequeue() {
        if( numEnqueued > 0 ) {
            T result = data[head++];

            numEnqueued--;
            if( head == size )
                head = 0;

            return result;
        }
        else
            throw new BufferUnderflowException();
    }
}
