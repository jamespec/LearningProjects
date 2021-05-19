public class Bucket {
    private Player current;
    private Player next;

    public Bucket(Player p){
        current = p;
        next = null;
    }

    public Player getCurrent(){
        return current;
    }

    public void setCurrent(Player p){
        current = p;
    }

    public void setNext(Player p){
        next = p;
    }
}
