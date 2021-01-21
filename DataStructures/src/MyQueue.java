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

    public static void main( String[] argv ) {
        MyQueue<Integer> s = new MyQueue<>(10 );
        s.enqueue(1);
        s.enqueue(2);
        s.enqueue(3);
        s.enqueue(4);
        s.enqueue(5);
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        s.enqueue(6);
        s.enqueue(7);
        s.enqueue(8);
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        s.enqueue(9);
        s.enqueue(10);
        s.enqueue(11);
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
        System.out.println( "Dequeue: " + s.dequeue() );
    }
}
