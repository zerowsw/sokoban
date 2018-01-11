import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EqualsTest {

    public static void main(String[] args) {
        BoxStatus box1 = new BoxStatus(3,5);
        BoxStatus box2 = new BoxStatus(5,3);
        BoxStatus box3 = new BoxStatus(4,3);
        BoxStatus box4 = new BoxStatus(5,5);
        BoxStatus box5 = new BoxStatus(3,5);
        BoxStatus box6 = new BoxStatus(5,3);
        BoxStatus box7 = new BoxStatus(4,3);
        BoxStatus box8 = new BoxStatus(5,5);

        boolean[] array = {true, true, true, true};
        box1.moveable = array;
        box2.moveable = array;
        box3.moveable = array;
        box4.moveable = array;

        boolean[] array2 = {true, true, true, true};
        box5.moveable = array2;
        box6.moveable = array2;
        box7.moveable = array2;
        box8.moveable = array2;




        Point[][] map = new Point[4][4];

        List<BoxStatus> boxes = new ArrayList<>();
        boxes.add(box1);
        boxes.add(box2);
        boxes.add(box3);
        boxes.add(box4);

        Point person  = new Point(1 , 2);

        State state1 = new State(boxes, map, person);

        List<BoxStatus> boxes2 = new ArrayList<>();
        boxes2.add(box5);
        boxes2.add(box6);
        boxes2.add(box7);
        boxes2.add(box8);

        Set<State> set = new HashSet<>();

        Point person2 = new Point(2, 3);

        State state2 = new State(boxes2, map, person);

        set.add(state1);
        System.out.println(set.contains(state2));

    }
}
