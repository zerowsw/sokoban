import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 同样是搜素问题，算法的基本框架不会改变，那么问题在于，state representation , transition model
 * 以及合适的 Herustic
 */
public class SoKoSearch {
    //可能不需要这么多，我需要一个只含路径的地图，来寻址。
    public static int WALL = 0;
    public static int GOAL = 1;
    public static int BOX  = 2;
    public static int PATH = 3;

    //坐标变换矩阵
    int[] directionX = {1, -1, 0, 0};
    int[] directionY = {0, 0, 1, -1};

    public static  void main(String[] args) {

        SoKoSearch soko = new SoKoSearch();
        ReadMapResult readMap = soko.readMap("src/mp1/sokoban/sokoban4.txt");

        long starTime=System.currentTimeMillis();
        soko.sokoSearch(readMap);
        long endTime=System.currentTimeMillis();
        System.out.println(endTime - starTime + "ms");
    }

    /**
     *
     * @param read outcome from the readmap function
     */
    private void sokoSearch(ReadMapResult read) {
        Point[][] maps = read.map;
        List<Point> goals = read.goals;
        List<BoxStatus> boxes = read.boxes;
        Point person = read.person;

        int expanded_nodes = 0;

        Queue<State> frontier = new PriorityQueue<>(new Comparator<State>() {
            @Override
            public int compare(State o1, State o2) {
                if ((o1.G + o1.H) > (o2.G + o2.H)) {
                    return 1;
                } else if ((o1.G + o1.H) < (o2.G + o2.H)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        Set<State> expanded = new HashSet<>();

        //initialize the state and put it into the queue
        State initialState = new State(boxes, maps, person);
        //calculate the G and H for the
        initialState.G = 0;
        initialState.H = herusticOfSoKoBan(initialState, maps);
        //initialState.H = 0;


        frontier.offer(initialState);


        //这里面就是用A* 的算法框架来处理，搜索各种state，找到最终的state。
        while (!frontier.isEmpty()) {
            State currentState = frontier.poll();

            //judge whether it is the final state
            //卧槽，我还在想怎么判断呢，，判读所有的boxesdou都在goal上不就好了吗
            List<BoxStatus> currentBoxes = currentState.boxes;
            boolean successful = true;
            for (BoxStatus box : currentBoxes) {
                if (maps[box.x][box.y ].type != GOAL) {
                    successful = false;
                    break;
                }
            }
            if (successful) {
                //print result
                printResult(currentState);

                System.out.println("Mission completed successfully !");
                System.out.println("Total path length : " + currentState.totalPathLength);
                System.out.println("Expanded nodes : " + expanded_nodes);
                return;
            }

            expanded_nodes++;
            expanded.add(currentState);

            //model transition, 对每个箱子在四个可能的方向上的移动进行判断
            for (BoxStatus box : currentBoxes) {
                for (int i = 0; i < 4; i++) {
                    if (box.moveable[i]) {
                        int iniX = box.x;
                        int iniY = box.y;
                        box.x += directionX[i];
                        box.y += directionY[i];
                        List<BoxStatus> newBoxStatus = new ArrayList<>();
                        for (BoxStatus box2: currentBoxes) {
                            BoxStatus newBox = new BoxStatus(box2.x,box2.y);
                            newBoxStatus.add(newBox);
                        }

                        //需要将坐标还原
                        box.x = iniX;
                        box.y = iniY;

                        //update the position of person here， 更新人的坐标，方便下个state里面moveable的判断
                        Point newPerson = new Point(box.x, box.y);
                        State newState = new State(newBoxStatus, maps, newPerson);

                        //get the toalPathLength;
                        //newState.totalPathLength = currentState.totalPathLength + box.path[i];

                        if (expanded.contains(newState)) {
                            continue;
                        }

                        //I need to judge the deadlock state，判断是不是死锁状态
                        if (newState.isDeadlock()) {
                            continue;
                        }

                        if (frontier.contains(newState)) {
//                            if (currentState.G + 1 <newState.G) {
//                                newState.G = currentState.G + 1;
//                                newState.parent = currentState;
//                            }
                            if (currentState.totalPathLength + box.path[i] < newState.totalPathLength) {
                                newState.G = currentState.totalPathLength + box.path[i];
                                //newState.G = 0;
                                //记录路径长度
                                newState.totalPathLength = currentState.totalPathLength + box.path[i];
                                newState.parent = currentState;
                            }
                        } else {
                            //newState.G = currentState.G + 1;
                            newState.G = currentState.totalPathLength + box.path[i];
                            //newState.G = 0;
                            newState.totalPathLength = currentState.totalPathLength + box.path[i];

                            newState.parent = currentState;
                            newState.H = herusticOfSoKoBan(newState, maps);
                            //newState.H = 0;

                            for (BoxStatus temp : newState.boxes) {
                                System.out.print("[" + temp.x + "," + temp.y + "]" + ",");
                            }
                            System.out.println();

                            frontier.offer(newState);
                        }
                    }
                }
            }
        }
    }


    private  int herusticOfSoKoBan(State state, Point[][] maps) {
        int herustic = 0;
        List<BoxStatus> boxes = state.boxes;
        for (BoxStatus box : boxes) {
            herustic += bfsForBox(box, maps);
        }
        return herustic;
    }

    /**
     * to caculate the distance from the box to the goal
     * @param box
     * @param map
     * @return
     */
    private int bfsForBox(BoxStatus box, Point[][] map) {
        int[] directionX = {1, -1, 0, 0};
        int[] directionY = {0, 0, 1, -1};

        Queue<Point> frontier = new LinkedList<>();
        Set<Point> extended = new HashSet<>();
        Point point = new Point(box.x, box.y);
        frontier.offer(point);

        int path = 0;

        while (!frontier.isEmpty()) {

            int size = frontier.size();

            for (int i = 0; i < size; i++) {
                Point current = frontier.poll();
                if (map[current.x][current.x].type == GOAL) {
                    return path;
                }
                extended.add(current);

                for (int j = 0; j < 4; j++) {
                    int newX = current.x + directionX[j];
                    int newY = current.y + directionY[j];
                    Point newP = new Point(newX, newY);

                    if (!isBound(newP, map) || map[newX][newY].type == WALL || extended.contains(newP)) {
                        continue;
                    }

                    if (frontier.contains(newP)) {
                        if (current.G + 1 < newP.G) {
                            newP.G = current.G + 1;
                            newP.parent = current;
                        }
                    } else {
                        newP.G = current.G + 1;
                        newP.parent = current;
                        frontier.offer(newP);
                    }
                }
            }
            path++;
        }
        return path;
    }

    private boolean isBound(Point p, Point[][] map) {
        return p.x > -1 && p.x < map.length && p.y > -1 && p.y < map[0].length;
    }


    private void printResult(State state) {
        while (state != null) {
            for (BoxStatus box : state.boxes) {
                System.out.print("[" + box.x + "," + box.y + "]" + ",");
            }
            System.out.println("<-");
            state = state.parent;
        }
    }
    /**
     * read map for txt
     * @param filepath
     * @return all nodes in the map and the list of final goals
     */
    private ReadMapResult readMap(String filepath) {
        File file = new File(filepath);
        BufferedReader reader = null;

        ArrayList<String> list = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                System.out.println(tempString + '\n');
                list.add(tempString);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ReadReturnType ro = new ReadOutput();
        Point[][] map = new Point[list.size()][list.get(0).length()];
        Point person = null;
        List<Point> goals = new ArrayList<>();
        List<BoxStatus> boxes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length(); j++) {
                char c = list.get(i).charAt(j);
                // Record the initial position
                if (c == 'P') {
                    Point node = new Point(i, j, PATH);
                    map[i][j] = node;
                    person = node;
                } else if (c == '.') {
                    Point node = new Point(i, j, GOAL);
                    map[i][j] = node;
                    goals.add(node);
                } else if (c == '%') {
                    map[i][j] = new Point(i, j, WALL);
                } else if (c == 'b') {
                    //we won't record the position of the boxes in the map
                    map[i][j] = new Point(i, j, PATH);
                    boxes.add(new BoxStatus(i, j));
                } else if (c == 'B') {
                    Point node = new Point(i, j, GOAL);
                    map[i][j] = node;
                    goals.add(node);
                    boxes.add(new BoxStatus(i, j));
                } else {
                    map[i][j] = new Point(i, j, PATH);
                }
            }
        }

        return new ReadMapResult(map, goals, boxes, person);
    }

}
