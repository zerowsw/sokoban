import java.util.*;

public class BoxStatus {
    //coordinate
    int x;
    int y;
    //reord movement situation in four directions
    boolean[] moveable;
    int[] path;

    /**
     * store the search result for A*
     */
    private class SearchResult {
        boolean arrival;
        int path;
        public SearchResult(boolean arrival, int path) {
            this.arrival = arrival;
            this.path = path;
        }
    }

    public BoxStatus(int x, int y) {
        this.x = x;
        this.y = y;
        path = new int[4];
        moveable = new boolean[4];
    }

    @Override
    public boolean equals(Object obj) {
        BoxStatus box = (BoxStatus) obj;

        //something wrong with equals
        boolean flag = (this.x == box.x) && (this.y == box.y);
        if (!flag) {
            return flag;
        }

        for (int i = 0; i < 4; i++) {
            if (this.moveable[i] != box.moveable[i]) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = result * 31 + this.x;
        result = result * 31 + this.y;
        return result;
    }

    /**
     * set the move condition for the box.
     * @param map
     * @param person
     */
    public  void setMoveable(Point[][] map, Point person) {

        int[] directionX = {1, -1, 0, 0};
        int[] directionY = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x + directionX[i];
            int newY = y + directionY[i];

            if (!isBound(map, newX, newY) || map[newX][newY].type == SoKoSearch.BOX
                    || map[newX][newY].type == SoKoSearch.WALL) {
                moveable[i] = false;
                continue;
            } else {
                //then we need to calculate the path from person to the position if ever exists
                SearchResult searchResult = A_Search(map, person, x - directionX[i], y - directionY[i]);
                moveable[i] = searchResult.arrival;
                path[i] = searchResult.path;
            }
        }


    }

    /**
     * A* search, find the shortest path from the person to the goal
     * @param map
     * @param person
     * @param x
     * @param y
     * @return whether the path exists and the length of path
     */
    private SearchResult A_Search(Point[][] map, Point person, int x, int y) {
        int[] directionX = {1, -1, 0, 0};
        int[] directionY = {0, 0, 1, -1};

        Queue<Point> frontier = new PriorityQueue<Point>(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                if ((o1.G + o1.H) > (o2.G + o2.H)) {
                    return 1;
                } else if ((o1.G + o1.H) < (o2.G + o2.H)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        Set<Point> extended = new HashSet<Point>();
        person.G = 0;
        person.H = herustic(person, x, y);
        frontier.offer(person);
        int path = 0;

        while(!frontier.isEmpty()) {

            Point current = frontier.poll();
            if (current.x == x && current.y == y) {
                while (current.parent != null) {
                    path++;
                    current = current.parent;
                }
                return new SearchResult(true, path + 1);
            }

            extended.add(current);

            for (int i = 0; i < 4; i++) {
                int newX = current.x + directionX[i];
                int newY = current.y + directionY[i];
                Point p = new Point(newX, newY);

                if (!isBound(map, newX, newY) || map[newX][newY].type == SoKoSearch.BOX
                        || map[newX][newY].type == SoKoSearch.WALL || extended.contains(p)) {
                    continue;
                }

                if (frontier.contains(p)) {
                    if (current.G + 1 < p.G) {
                        p.G = current.G + 1;
                        p.parent = current;
                    }
                } else {
                    p.G = current.G + 1;
                    p.H = herustic(p, x, y);
                    p.parent = current;
                    frontier.offer(p);
                }
            }
        }
        return new SearchResult(false, path);
    }

    /**
     * boundary detection
     * @param map
     * @param x
     * @param y
     * @return
     */
    private boolean isBound(Point[][] map, int x, int y) {
        int m = map.length;
        int n = map[0].length;
        return x > -1 && x < m && y > -1 && y < n;
    }

    private int herustic(Point point, int x, int y) {
        return Math.abs(point.x - x) + Math.abs(point.y - y);
    }
}
