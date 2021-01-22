
public class MyLinkedList<T>
{
    private static class Node<T> {
        T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    Node<T> head;
    int numInList;

    MyLinkedList() {
        head = null;
        numInList = 0;
    }

    // Insert the new item at the specified location,
    // 0 is the front, if pos is larger than the list the item is appended to the end
    void insertAtPos( T v, int pos ) {
        if( pos == 0 || head == null ) {
            // Insert at position 0 or there is no head: the new item is the new head.
            head = new Node<>(v, head);
        }
        else {
            Node<T> prior = head;
            for (int i = 1; i < pos && prior.next != null; i++)
                prior = prior.next;

            // Prior can't be null, we would have gone into the 'if' above.
            // Prior.next might be null but that is okay:
            // We add our new item and the next on our new item is null, the new end of the list.
            Node<T> newNode = new Node<>(v, prior.next);
            prior.next = newNode;
        }
        numInList++;
    }

    // Append to end of the list
    void append( T v ) {
        insertAtPos( v, numInList );
    }

    // Get the value at index
    T peek( int index ) {
        Node<T> current=head;
        for( int i=0; i<index && current != null; i++ )
            current = current.next;

        return (current != null ? current.value : null);
    }

    int size() {
        return numInList;
    }

    // Remove the next item and return it, will be null if list is empty.
    T pop() {
        T retValue = null;

        if( head != null ) {
            retValue = head.value;
            head = head.next;
            numInList--;

            // Note: the old item that head was pointing to is automatically garbage collected in Java.
            // In 'C', we would have called 'free' to give back the memory for the object.
        }

        return retValue;
    }
}
