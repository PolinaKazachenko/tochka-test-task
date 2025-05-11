import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;


public class run2 {
    // Константы для символов ключей и дверей
    private static final char[] KEYS_CHAR = new char[26];
    private static final char[] DOORS_CHAR = new char[26];


    static {
        for (int i = 0; i < 26; i++) {
            KEYS_CHAR[i] = (char)('a' + i);
            DOORS_CHAR[i] = (char)('A' + i);
        }
    }


    // Чтение данных из стандартного ввода
    private static char[][] getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;


        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }


        char[][] maze = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            maze[i] = lines.get(i).toCharArray();
        }


        return maze;
    }


    static class State implements Comparable<State> {
        String[] robots;
        int keys;
        int dist;

        State(String[] robots, int keys, int dist) {
            this.robots = robots;
            this.keys = keys;
            this.dist = dist;
        }

        public int compareTo(State o) {
            return Integer.compare(this.dist, o.dist);
        }
    }

    private static Map<String, Integer> bfsFrom(String src, Map<String, int[]> location, char[][] maze, int rows, int cols) {
        int[] start = location.get(src);
        int row = start[0];
        int col = start[1];

        boolean[][] visited = new boolean[rows][cols];
        visited[row][col] = true;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{row, col, 0});

        Map<String, Integer> res = new HashMap<>();

        while (!queue.isEmpty()) {
            int[] currentState = queue.poll();
            int currentRow = currentState[0];
            int currentCol = currentState[1];
            int dist = currentState[2];

            char val = maze[currentRow][currentCol];
            String strVal = String.valueOf(val);
            if (!strVal.equals(src) && val != '.' && val != '@') {
                res.put(strVal, dist);
                continue;
            }

            int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
            for (int i = 0; i < 4; i++) {
                int newRow = currentRow + directions[i][0];
                int newCol = currentCol + directions[i][1];
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && maze[newRow][newCol] != '#' && !visited[newRow][newCol]) {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol, dist + 1});
                }
            }
        }

        return res;
    }

    private static String getStateKey(String[] pos, int mask) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pos.length; i++) {
            stringBuilder.append(pos[i]).append(",");
        }
        stringBuilder.append("#").append(mask);
        return stringBuilder.toString();
    }


    private static int solve(char[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        Map<String, int[]> location = new HashMap<>();
        int robotCount = 0;
        int targetState = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char symbol = data[r][c];
                if (symbol != '.' && symbol != '#') {
                    if (symbol == '@') {
                        String robotName = String.valueOf(robotCount);
                        data[r][c] = robotName.charAt(0);
                        location.put(robotName, new int[]{r, c});
                        robotCount++;
                    } else {
                        location.put(String.valueOf(symbol), new int[]{r, c});
                        if (symbol >= 'a' && symbol <= 'z') {
                            targetState |= (1 << (symbol - 'a'));
                        }
                    }
                }
            }
        }

        String[] robots = new String[robotCount];
        for (int i = 0; i < robotCount; i++) {
            robots[i] = String.valueOf(i);
        }

        Map<String, Map<String, Integer>> keyGraph = new HashMap<>();
        for (String src : location.keySet()) {
            keyGraph.put(src, bfsFrom(src, location, data, rows, cols));
        }



        PriorityQueue<State> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(new State(robots, 0, 0));

        Map<String, Integer> minDist = new HashMap<>();
        String startKey = getStateKey(robots, 0);
        minDist.put(startKey, 0);

        while (!priorityQueue.isEmpty()) {
            State currentState = priorityQueue.poll();
            int dist = currentState.dist;
            String[] pos = currentState.robots;
            int keyMask = currentState.keys;

            String key = getStateKey(pos, keyMask);
            if (minDist.getOrDefault(key, Integer.MAX_VALUE) < dist) {
                continue;
            }
            if (keyMask == targetState) {
                return dist;
            }

            for (int i = 0; i < pos.length; i++) {
                String from = pos[i];
                Map<String, Integer> moves = keyGraph.get(from);
                if (moves == null) {
                    continue;
                }

                for (Map.Entry<String, Integer> move : moves.entrySet()) {
                    String to = move.getKey();
                    int step = move.getValue();

                    char symbol = to.charAt(0);
                    if (symbol >= 'A' && symbol <= 'Z') {
                        if ((keyMask & (1 << (symbol - 'A'))) == 0) {
                            continue;
                        }
                    }

                    int newMask = keyMask;
                    if (symbol >= 'a' && symbol <= 'z') {
                        newMask |= (1 << (symbol - 'a'));
                    }

                    String[] newPos = new String[pos.length];
                    for (int j = 0; j < pos.length; j++) {
                        newPos[j] = pos[j];
                    }
                    newPos[i] = to;

                    String newKey = getStateKey(newPos, newMask);
                    int newDist = dist + step;

                    if (newDist < minDist.getOrDefault(newKey, Integer.MAX_VALUE)) {
                        minDist.put(newKey, newDist);
                        priorityQueue.offer(new State(newPos, newMask, newDist));
                    }
                }
            }
        }

        return Integer.MAX_VALUE;
    }


    public static void main(String[] args) throws IOException {
        char[][] data = getInput();
        int result = solve(data);

        if (result == Integer.MAX_VALUE) {
            System.out.println("No solution found");
        } else {
            System.out.println(result);
        }
    }
}