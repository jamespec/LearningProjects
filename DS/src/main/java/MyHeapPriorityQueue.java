import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class MyHeapPriorityQueue
{
    static class Item {
        int value;
        int priority;

        Item( int value, int priority ) {
            this.value = value;
            this.priority = priority;
        }
    }

    // A class for Max Heap
    Item[] heap; // pointer to array of elements in heap
    int capacity; // maximum possible size of min heap
    int heap_size; // Current number of elements in min heap

    MyHeapPriorityQueue(int cap)
    {
        heap_size = 0;
        capacity = cap;
        heap = new Item[cap];
    }

    private int parent(int i) { return (i-1)/2; }

    // to get index of left child of node at index i
    private int left(int i) { return (2*i + 1); }

    // to get index of right child of node at index i
    private int right(int i) { return (2*i + 2); }

    // Returns without dequeuing the top priority item.
    int peek() {
        if( heap_size == 0 )
            throw new BufferUnderflowException();

        return heap[0].value;
    }

    public int size() {
        return heap_size;
    }

    // Inserts a new value 'k' with priority 'priority'
    public void enqueue(int k, int priority)
    {
        if (heap_size == capacity)
            throw new BufferOverflowException();

        int i = heap_size++;
        heap[i] = new Item(k, priority);

        // Fix the max heap property if it is violated
        while (i != 0 && heap[parent(i)].priority < heap[i].priority)
        {
            Item tmp = heap[i];
            heap[i] = heap[parent(i)];
            heap[parent(i)] = tmp;
            i = parent(i);
        }
    }

    // Method to remove max priority element (or root) from PriorityQueue
    public int dequeue()
    {
        if (heap_size <= 0)
            throw new BufferUnderflowException();

        if (heap_size == 1)
        {
            heap_size--;
            return heap[0].value;
        }

        // Store the minimum value, and remove it from heap
        int value = heap[0].value;
        heap[0] = heap[heap_size-1];
        heap_size--;
        heapify(0);

        return value;
    }

    // This function deletes Item at index i. It first sets priority to max then
    // then calls dequeue to remove and throw away the Item.
    private void deleteKey(int i)
    {
        changePriority(i, Integer.MAX_VALUE);
        dequeue();  // dequeue that MAX value and throw away.
    }

    void changePriority(int i, int new_priority)
    {
        if( i < heap_size ) {
            heap[i].priority = new_priority;
            while (i != 0 && heap[parent(i)].priority < heap[i].priority) {
                Item tmp = heap[i];
                heap[i] = heap[parent(i)];
                heap[parent(i)] = tmp;
                i = parent(i);
            }
        }
        else
            throw new ValueException("i index is larger than queue size");
    }

    // A recursive method to heapify a subtree with the root at given index
    // This method assumes that the subtrees are already heapified
    private void heapify(int i)
    {
        int l = left(i);
        int r = right(i);
        int iOfMax = i;
        if (l < heap_size && heap[l].priority > heap[i].priority)
            iOfMax = l;
        if (r < heap_size && heap[r].priority > heap[iOfMax].priority)
            iOfMax = r;
        if (iOfMax != i)
        {
            Item tmp = heap[i];
            heap[i] = heap[iOfMax];
            heap[iOfMax] = tmp;
            heapify(iOfMax);
        }
    }
}
