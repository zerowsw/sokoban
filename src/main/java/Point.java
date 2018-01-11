public class Point {
    int x;
    int y;
    int type;

    //for the purpose of calculate the path, A* search
    int G;
    int H;
    Point parent;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public boolean equals(Object obj) {
        Point point = (Point)obj;
        return this.x == point.x && this.y == point.y;
    }

    public int hashCode(){
        int result = 17;
        result = result * 31 + this.x;
        result = result * 31 + this.y;
        return result;
    }
}
