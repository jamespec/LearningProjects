public class Player {
    private String first;
    private String last;

    public Player(String f, String l) {
        first = f;
        last = l;
    }

    public String getFirst(){
        return first;
    }

    public String getLast(){
        return last;
    }

    public String toString(){
        return first + " " + last;
    }

    public int hashCode(){
        int sum = 0;
        int length = getFirst().length() + getLast().length() + 1;
        for(int i = 0; i < length; i++){
            sum += this.toString().charAt(i);
        }
        return sum % 11;
    }

    public int hashCode2(){
        int length = getFirst().length() + getLast().length() + 1;
        int lengthCube = (int) Math.pow(length, 3);
        return lengthCube % 11;
    }

}
