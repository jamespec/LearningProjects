import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class MyPriorityQueue<T>
{
    private static class Item {
        Object value;
        int priority;

        Item( Object value, int priority ) {
            this.value = value;
            this.priority = priority;
        }
    }
    Item[] data;
    int size;
    int head;
    int numEnqueued;

    MyPriorityQueue(int size ) {
        this.size = size;
        this.data = new Item[size];
        this.head = 0;
        this.numEnqueued = 0;
    }

    void enqueue( T v, int priority ) {
        if( numEnqueued < size ) {
            data[(head + numEnqueued) % size] = new Item( v, priority );
            numEnqueued++;
        }
        else
            throw new BufferOverflowException();
    }

    T dequeue()
    {
        if( numEnqueued == 0 )
            throw new BufferUnderflowException();

        if( numEnqueued == 1 ) {
            numEnqueued--;
            return (T) data[head].value;
        }

        int highestIndex=head;
        int highestPriority = data[head].priority;
        for(int i=head; i<head+numEnqueued; i++) {
            int index = i % size;
            if( data[index].priority > highestPriority ) {
                highestPriority = data[index].priority;
                highestIndex    = index;
            }
        }

        T result = (T) data[highestIndex].value;
        data[highestIndex] = data[head+numEnqueued-1];
        numEnqueued--;

        return result;
    }

    int size() {
        return numEnqueued;
    }

    T peek() {
        if( numEnqueued > 0 )
            return (T)data[head].value;
        else
            throw new BufferUnderflowException();
    }
}
