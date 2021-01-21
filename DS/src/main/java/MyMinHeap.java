import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

public class MyMinHeap
{
    // A class for Min Heap
    int[] heap; // pointer to array of elements in heap
    int capacity; // maximum possible size of min heap
    int heap_size; // Current number of elements in min heap

    MyMinHeap(int cap)
    {
        heap_size = 0;
        capacity = cap;
        heap = new int[cap];
    }

    int parent(int i) { return (i-1)/2; }

    // to get index of left child of node at index i
    int left(int i) { return (2*i + 1); }

    // to get index of right child of node at index i
    int right(int i) { return (2*i + 2); }

    // Returns the minimum key (key at root) from min heap
    int getMin() { return heap[0]; }

    // Inserts a new key 'k'
    void insertKey(int k)
    {
        if (heap_size == capacity)
            throw new BufferOverflowException();

        // First insert the new key at the end
        heap_size++;
        int i = heap_size - 1;
        heap[i] = k;

        // Fix the min heap property if it is violated
        while (i != 0 && heap[parent(i)] > heap[i])
        {
            int tmp = heap[i];
            heap[i] = heap[parent(i)];
            heap[parent(i)] = tmp;
            i = parent(i);
        }
    }

    // Decreases value of key at index 'i' to new_val. It is assumed that
    // new_val is smaller than harr[i].
    void decreaseKey(int i, int new_val)
    {
        heap[i] = new_val;
        while (i != 0 && heap[parent(i)] > heap[i])
        {
            int tmp = heap[i];
            heap[i] = heap[parent(i)];
            heap[parent(i)] = tmp;
            i = parent(i);
        }
    }

    // Method to remove minimum element (or root) from min heap
    int extractMin()
    {
        if (heap_size <= 0)
            throw new BufferUnderflowException();

        if (heap_size == 1)
        {
            heap_size--;
            return heap[0];
        }

        // Store the minimum value, and remove it from heap
        int root = heap[0];
        heap[0] = heap[heap_size-1];
        heap_size--;
        MinHeapify(0);

        return root;
    }

    // This function deletes key at index i. It first reduced value to minus
    // infinite, then calls extractMin()
    void deleteKey(int i)
    {
        decreaseKey(i, Integer.MIN_VALUE);
        extractMin();
    }

    // A recursive method to heapify a subtree with the root at given index
    // This method assumes that the subtrees are already heapified
    void MinHeapify(int i)
    {
        int l = left(i);
        int r = right(i);
        int smallest = i;
        if (l < heap_size && heap[l] < heap[i])
            smallest = l;
        if (r < heap_size && heap[r] < heap[smallest])
            smallest = r;
        if (smallest != i)
        {
            int tmp = heap[i];
            heap[i] = heap[smallest];
            heap[smallest] = tmp;
            MinHeapify(smallest);
        }
    }
}
