import java.util.List;

public class ReadMapResult {
    Point[][] map;
    List<Point> goals;
    List<BoxStatus> boxes;
    Point person;
    public ReadMapResult(Point[][] map, List<Point> goals, List<BoxStatus> boxes, Point person) {
        this.map = map;
        this.goals = goals;
        this.boxes = boxes;
        this.person = person;
    }
}
