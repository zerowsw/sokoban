import java.util.List;

/**
 * this class for state representation
 *
 */
public class State implements Cloneable{

    int[] directionX = {1, -1, 0, 0};
    int[] directionY = {0, 0, 1, -1};

    public static int WALL = 0;
    public static int GOAL = 1;
    public static int BOX  = 2;
    public static int PATH = 3;

    public int totalPathLength = 0;



    List<BoxStatus> boxes;
    Point[][] map;
    Point person; //record the person here just in order to calculate the true state
    State parent;
    boolean deadlock;

    int G;
    int H;

    public State(List<BoxStatus> boxes, Point[][] map, Point person) {
        this.boxes = boxes;
        this.map = map;
        this.person = person;
        initialize(boxes, map);
    }

    /**
     * initialize the state, include the position of the boxes and the move
     * situation of the boxes which should consider the space in the direction
     * of the boxes and whether human could move to there.
     */
    private  void initialize(List<BoxStatus> boxes, Point[][] map) {

        //set the box positions into the map
        int m = map.length;
        int n = map[0].length;

        Point[][] newMap = new Point[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
               // System.out.println(i + "  " + j);
                Point point = map[i][j];
                newMap[i][j] = new Point(point.x, point.y, point.type);
            }
        }

        for (BoxStatus box : boxes) {
           // System.out.println("setup box for newmap, box :" + box.x + box.y);
            newMap[box.x][box.y].type = SoKoSearch.BOX;
        }

        for (BoxStatus box : boxes) {
            //set the moveable for every box
            box.setMoveable(newMap, person);
        }


        //I need to judege the basic deadlock state here.
        for (BoxStatus box : boxes) {
            //when a box get into the corner which is not the goal.
            if (!(box.moveable[0] || box.moveable[1] || box.moveable[2] || box.moveable[3])) {
                boolean flag1 = true;
                int count = 0;

                if (box.x == 2 && box.y == 1) {
                    int tmd = 0;
                }

                for (int i = 0; i < 4; i++) {
                    int x = box.x + directionX[i];
                    int y = box.y + directionY[i];
                    if (map[x][y].type == WALL) {
                        count++;
                    }
                }

                if (count < 2) {
                    flag1 = false;
                }

                if ((map[box.x][box.y].type != GOAL) && flag1) {
                    deadlock = true;
                    break;
                } else {
                    deadlock = false;
                }
            }
        }
    }

    /**
     * return the deadlock judgement
     * @return
     */
    public boolean isDeadlock(){
        return deadlock;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;

        State state = (State) o;

        boolean flag = true;
        for (BoxStatus box : state.boxes) {
            if(!this.boxes.contains(box)) {
                flag = false;
                break;
            }
        }
        //&& state.person.x == this.person.x && state.person.y == this.person.y
        return flag ;
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = result * 31;
        return result;
    }
}
