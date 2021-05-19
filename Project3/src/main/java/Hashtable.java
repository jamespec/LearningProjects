
public class Hashtable
{
    private int size;
    private Bucket[] data;

    public Hashtable(int s){
        size = s;
        data = new Bucket[s];
    }

    public boolean isEmpty(){
        for(int i = 0; i < data.length; i++){
            if(data[i] != null){
                return false;
            }
        }
        return true;
    }

    public boolean isFull(){
        for(int i = 0; i < data.length; i++){
            if(data[i] == null){
                return false;
            }
        }
        return true;
    }

    public int getSize(){
        return size;
    }

    public void expandCapacity(){
        int newSize = 2 * size;
        while(!isPrime(newSize)){
            newSize++;
        }
        Bucket[] data2 = new Bucket[newSize];
        System.arraycopy(data, 0, data2, 0, data.length);
        data = data2;
    }

    public boolean isPrime(int n){
        for(int i = 2; i < n; i++){
            if(n % i == 0){
                return false;
            }
        }
        return true;
    }

    public void insert(Player p){
        if(data[p.hashCode()] != null) {
            data[p.hashCode()].setNext(p);
        }
    }
}
