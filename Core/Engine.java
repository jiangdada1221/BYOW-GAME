package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdDraw;

import javax.swing.plaf.ButtonUI;
import java.awt.*;
import java.io.File;
import java.util.Random;
/* Author Yuepeng Jiang */
public class Engine {
    TERenderer ter = new TERenderer();
    /* free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    Engine() {
        StdDraw.setCanvasSize(40*16, 40 * 16);
        StdDraw.setXscale(0,40);
        StdDraw.setYscale(0,40);
        StdDraw.clear(Color.black);
        StdDraw.enableDoubleBuffering();
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        drawLogin("New Game(N)","Load(L)");
        InputSource input = new KeyboardInputSource();
        while (input.possibleNextInput()) {
                char c = input.getNextKey();
                if (c == 'n' ){
                    String seed = enterSeed();

                    newGame(seed,false,null);
                    }
                else if (c == 'l'){
                    load();break;}
                else if (c == 'q')
                    System.exit(0);
                else continue;

        }
    }

    private void drawLogin(String s1,String s2) {
        Font fontLarge = new Font("Monaco",Font.BOLD,40);
        Font fontSmall = new Font("Monaco",Font.BOLD,20);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(fontLarge);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(20,37,"The Game");
        StdDraw.setFont(fontSmall);
        StdDraw.text(20,23,s1);
        StdDraw.text(20,20,s2);
        StdDraw.text(20,17,"Quit(Q)");
        StdDraw.show();
    }

    private String enterSeed() {
        drawLogin("Enter seed :","(Start with 'n' and end with 's'");
        StringBuilder sb = new StringBuilder();
        InputSource input = new KeyboardInputSource();
        String seed ="";
        while (input.possibleNextInput()) {
            char c = input.getNextKey();
            sb.append(c);
            if (sb.length()==1 && sb.charAt(0)!='n'){
                drawLogin("Wrong!","Press ANY to input again!");
                while (input.possibleNextInput()) {
                    input.getNextKey();
                    enterSeed();
                }
            }
            drawLogin("Enter seed : " + sb.toString(),"");
            if (c == 's' ) {
                seed = sb.toString();
                break;
            }

        }
        return seed;
    }

    private void newGame(String seed,boolean load, int[] co) {
        ter.initialize(WIDTH,HEIGHT+3,0,1);
        TETile[][] world = interactWithInputString(seed);
        int[] coordinate = co;
        int[] coordinate2 = null;
        if (load) {
            world[co[0]][co[1]] = Tileset.AVATAR;
        }else {
        coordinate = Right(world);
        coordinate2 = Left(world);
        world[coordinate[0]][coordinate[1]] = Tileset.AVATAR;
        world[coordinate2[0]][coordinate2[1]] = Tileset.LOCKED_DOOR;
        }
        double time1 = System.currentTimeMillis();
        ter.renderFrame(world,false,null,true,Integer.toString(20-(int)((System.currentTimeMillis()-time1)/1000)));
        boolean isSucceed = false;
        while (true) {
            ter.renderFrame(world,false,null,true,Integer.toString(20-(int)((System.currentTimeMillis()-time1)/1000)));
            if (20-(int)((System.currentTimeMillis()-time1)/1000) <= 0) break;
            if (Math.abs(coordinate[0]-coordinate2[0] + coordinate[1] - coordinate2[1]) <= 1) {
                isSucceed = true;break;
            }
            if (StdDraw.isMousePressed()) {
                int x = (int)StdDraw.mouseX();
                int y = (int)StdDraw.mouseY();
                if (y<=30 && y>=1) {
                String description = world[x][y-1].description();
                ter.renderFrame(world,true,description,true,Integer.toString(20-(int)((System.currentTimeMillis()-time1)/1000)));
                }else {
                    ter.renderFrame(world,true,"nothing",true,Integer.toString(20-(int)((System.currentTimeMillis()-time1)/1000)));
                }
            }
            if (StdDraw.hasNextKeyTyped()) {
            char c = StdDraw.nextKeyTyped();
            switch (c) {
                case 'q':System.exit(0);
                case 'w':moveNorth(world,coordinate);break;
                case 'd':moveEast(world,coordinate);break;
                case 'a':moveWest(world,coordinate);break;
                case 's':moveSouth(world,coordinate);break;
                case ':':save(world,coordinate,seed);break;
                default:break;
            }
        }}
        if (isSucceed) {
            drawLogin("You win!","Press r to Continue");
            InputSource input = new KeyboardInputSource();
            while (input.possibleNextInput()) {
                char c = input.getNextKey();
                if (c == 'r') interactWithKeyboard();

            }
        }else {
            drawLogin("You fail!", "Press r to restart");
            InputSource input = new KeyboardInputSource();
            while (input.possibleNextInput()) {
                char c = input.getNextKey();
                if (c =='r') {
                    newGame(seed,false,null);
                }
            }
        }
    }

    private void save(TETile[][] world,int[] coordinate,String seed) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'q' || c == 'Q') {
                    File file = new File("saving.txt");
                    Out out = new Out("saving.txt");
                    out.println(seed);
                    out.println(coordinate[0]);
                    out.println(coordinate[1]);
                    break;
                }
                else return;
            }
        }
    }

    private void moveWest(TETile[][] world, int[] co) {
        if (world[co[0]-1][co[1]]==Tileset.FLOOR) {
            world[co[0]][co[1]] = Tileset.FLOOR;
            world[co[0]-1][co[1]] = Tileset.AVATAR;
            co[0] = co[0]-1;
        }
        ter.renderFrame(world,false,null,false,null);
    }

    private void moveEast(TETile[][] world, int[] co) {
        if (world[co[0]+1][co[1]]==Tileset.FLOOR) {
            world[co[0]][co[1]] = Tileset.FLOOR;
            world[co[0]+1][co[1]] = Tileset.AVATAR;
            co[0] = co[0]+1;
        }
        ter.renderFrame(world,false,null,false,null);
    }

    private void moveNorth(TETile[][] world, int[] co) {
        if (world[co[0]][co[1]+1]==Tileset.FLOOR) {
            world[co[0]][co[1]] = Tileset.FLOOR;
            world[co[0]][co[1]+1] = Tileset.AVATAR;
            co[1] = co[1]+1;
        }
        ter.renderFrame(world,false,null,false,null);
    }

    private void moveSouth(TETile[][] world, int[] co) {
        if (world[co[0]][co[1]-1]==Tileset.FLOOR) {
            world[co[0]][co[1]] = Tileset.FLOOR;
            world[co[0]][co[1]-1] = Tileset.AVATAR;
            co[1] = co[1]-1;
        }
        ter.renderFrame(world,false,null,false,null);
    }

    private int[] randomO(TETile[][] world, Random ran) {
        int[] result = new int[2];
        result[0] = ran.nextInt(WIDTH);
        result[1] = ran.nextInt(HEIGHT);
        while (world[result[0]][result[1]]!=Tileset.FLOOR){
            result[0] = ran.nextInt(WIDTH);
            result[1] = ran.nextInt(HEIGHT);
        }
        return result;
    }
    private int[] Right(TETile[][] world) {
        int[] result = new int[2];
        boolean isbreak = false;
        for (int col = world.length-1;col>=0;col--) {
            if(isbreak) break;
            for (int row = world[0].length-1;row>=0;row--) {
                if (world[col][row] == Tileset.FLOOR ){
                    result[0] = col;
                    result[1] = row;
                    isbreak = true;
                    break;
                }
            }
        }
        return result;
    }

    private int[] Left(TETile[][] word) {
        int[] result = new int[2];
        boolean isbreak = false;
        for (int col = 1;col<word[0].length-1;col++) {
            if (isbreak) break;
            for (int row = 1;row<word.length-1;row++) {
                if (word[row][col] == Tileset.WALL &&((word[row+1][col]==Tileset.WALL&&word[row-1][col]==Tileset.WALL)||
                        word[row][col+1]==Tileset.WALL&&word[row][col-1]==Tileset.WALL)){
                    result[0] = row;
                    result[1] = col;
                    isbreak = true;
                    break;
                }
            }
        }
        return result;
    }
    private void load() {
        try {
            In in = new In("saving.txt");
            String seed = in.readLine();
            int x = Integer.parseInt(in.readLine());
            int y = Integer.parseInt(in.readLine());
            int[] co = new int[]{x,y};
            newGame(seed,true,co);
        }catch (Exception e) {
            drawLogin("No saving!","Press N to start a new game");
            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    char c = StdDraw.nextKeyTyped();
                    if (c == 'n' || c == 'N') {
                        String seed = enterSeed();
                        newGame(seed,false,null);
                        break;
                    }
                    else if (c == 'q' || c =='Q')
                        System.exit(0);
                }
            }
        }

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        InputSource Input = new StringInputDevice(input);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        char start = Input.getNextKey();
        assert start=='n'||start=='N';   // the first N
        Long seed; StringBuilder sb = new StringBuilder();
        while (Input.possibleNextInput()) {
            char index = Input.getNextKey();
            if (index == 's' || index == 'S')
                break;
            sb.append(index);
        }
        seed = Long.parseLong(sb.substring(0));
        for (int i = 0;i<=WIDTH-1;i++) {
            for (int j = 0;j<=HEIGHT-1;j++)
                finalWorldFrame[i][j] = Tileset.NOTHING;
        }
        BuildWorld(new Random(seed),new Position(WIDTH/2,HEIGHT/2),finalWorldFrame);
        return finalWorldFrame;
    }

    /* direction: 0-North 1-South 2-West 3-East
    * start to build world
    * */
    private void BuildWorld(Random ran,Position p, TETile[][] world) {


            BuildRoom(3,3,world,p,ran);

//        else
//            BuildPath(ran.nextInt(5)+1,p,ran.nextInt(4),ran,world);

    }


    /*
    *
    * */
    private void BuildRoom(int width, int height, TETile[][] world, Position p, Random ran) {
        if (!ScanUD(p,0,width+1,height+2,0,world))
            return;
        for (int i = p.x;i<=p.x+width+1;i++) {
            world[i][p.y] = Tileset.WALL;
            world[i][p.y+height+1] = Tileset.WALL;
        }
            for (int j = p.y+1;j<=p.y+height;j++) {
                world[p.x][j] = Tileset.WALL;
                world[p.x+width+1][j] = Tileset.WALL;
                for (int i = p.x+1;i<=p.x+width;i++)
                    world[i][j] = Tileset.FLOOR;
            }
            if (p.x == 40 && p.y == 15)
                RandomPath(3,p,ran,world,width,height);
            else RandomPath(ran.nextInt(3)+1,p,ran,world,width,height);
    }
    /* num: howmany side have a path in a room */
    private void RandomPath(int num,Position p, Random ran, TETile[][] world, int width, int height) {
        int[] used = new int[]{-1,-1,-1};
        int index1,index2;
        for (int k = 1;k<=num;k++) {
            index1 = ran.nextInt(4);
            while (index1 == used[0]|| index1 == used[1]){
                index1 = ran.nextInt(4);
                used[k-1] = index1;}
            switch (index1) {
                case 0:
                    index2 = ran.nextInt(width);
                                                                    //! next p
                    BuildPath(ran.nextInt(4)+1,new Position(p.x+1+index2,p.y+height+2),0,ran,world);
                    if (InBound(p.x+1+index2,p.y+height+2,world)&& world[p.x+1+index2][p.y+height+2] == Tileset.FLOOR)
                    world[p.x+1+index2][p.y+height+1] = Tileset.FLOOR;
                    break;
                case 1:
                    index2 = ran.nextInt(width);
                    BuildPath(ran.nextInt(4)+1,new Position(p.x+1+index2,p.y-1),1,ran,world);
                    if (InBound(p.x+1+index2,p.y-1,world)&&world[p.x+1+index2][p.y-1]==Tileset.FLOOR)
                    world[p.x+1+index2][p.y] = Tileset.FLOOR;
                    break;
                case 2:
                    index2 = ran.nextInt(height);
                    BuildPath(ran.nextInt(6)+1,new Position(p.x-1,p.y+1+index2),2,ran,world);
                    if (InBound(p.x-1,p.y+1+index2,world)&& world[p.x-1][p.y+1+index2]==Tileset.FLOOR)
                    world[p.x][p.y+1+index2] = Tileset.FLOOR;
                    break;
                case 3:
                    index2 = ran.nextInt(height);
                    BuildPath(ran.nextInt(6)+1,new Position(p.x+width+2,p.y+1+index2),3,ran,world);
                    if (InBound(p.x+width+2,p.y+1+index2,world)&&world[p.x+width+2][p.y+1+index2]==Tileset.FLOOR)
                    world[p.x+width+1][p.y+1+index2] = Tileset.FLOOR;
                    break;
            }
        }
    }

//    private class Position {
//        int x;
//        int y;
//
//        Position(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//    }
    /* 0 1  means no subpath,  double path(like a cross
    * p is the next position */
    private void BuildPath(int length,Position p, int direction, Random ran,TETile[][] world) {
        if (direction ==0||direction == 1) {
            if (!ScanUD(p,1,1,length+2,direction,world))
                return;
        }
        else {
            if (!ScanWE(p,1,1,length+2,direction,world))
                return;
        }
//        int num = ran.nextInt(2);
        int num = 1;
        Position newp;
        switch (direction) {
            case 1:
                for (int j = p.y;j>=p.y-length;j--){  //!
                    if (j==p.y-length)
                        world[p.x][j] = Tileset.WALL;
                    else world[p.x][j] = Tileset.FLOOR;
                    world[p.x+1][j] = Tileset.WALL;
                    world[p.x-1][j] = Tileset.WALL;
                }
                newp = new Position(p.x,p.y-length);   //current position
                RandomPR(ran,world,newp,0,1);

                PathHelper(p,ran,world,1,1,length);
                break;
            case 0:
                for (int j = p.y;j<=p.y+length;j++) {
                    if (j==p.y+length)
                        world[p.x][j] = Tileset.WALL;
                    else
                        world[p.x][j] = Tileset.FLOOR;
                    world[p.x+1][j] = Tileset.WALL;
                    world[p.x-1][j] = Tileset.WALL;     // p.x, p.y+length
                }
                newp = new Position(p.x,p.y+length);
                RandomPR(ran,world,newp,0,0);

                PathHelper(p,ran,world,0,1,length);
                break;
            case 2:
                for (int i = p.x;i>=p.x-length;i--) {
                    if (i == p.x-length)
                        world[i][p.y] = Tileset.WALL;
                    else world[i][p.y] = Tileset.FLOOR;
                    world[i][p.y+1] = Tileset.WALL;
                    world[i][p.y-1] = Tileset.WALL;  // p.x-length, p.y
                }
                newp = new Position(p.x-length,p.y);
                RandomPR(ran,world,newp,0,2);

                PathHelper(p,ran,world,2,num,length);
                break;
            case 3:
                for (int i = p.x;i<=p.x+length;i++) {
                    if (i == p.x+length)
                        world[i][p.y] = Tileset.WALL;
                    else world[i][p.y] = Tileset.FLOOR;
                    world[i][p.y+1] = Tileset.WALL;    //p.x+length , p.y
                    world[i][p.y-1] = Tileset.WALL;
                }
                newp= new Position(p.x+length,p.y);
                RandomPR(ran,world,newp,0,3);

                PathHelper(p,ran,world,3,num,length);
                break;
        }
    }

    /* p is the next position (start of the path)*/
    private void PathHelper(Position p, Random ran, TETile[][] world,int direction,int num,int length) {
        if (num == 0 || length == 1)
            return;
        else  {
            int index1 = ran.nextInt(length-1);
            Position newp1,newp2;
            switch (direction) {
                case 3:
                    newp1 = new Position(p.x+index1+1,p.y+2);
                    newp2 = new Position(p.x+index1+1,p.y-2);
                    PathHelper1(newp1,newp2,world,ran);break;

                case 2:
                    newp1 = new Position(p.x-index1-1,p.y+2);     // the bug!!!
                    newp2 = new Position(p.x-index1-1,p.y-2);
                    PathHelper1(newp1,newp2,world,ran);break;
                case 0:
                    newp1 = new Position(p.x-2,p.y+1+index1);
                    newp2 = new Position(p.x+2,p.y+1+index1);
                    PathHelper2(newp1,newp2,world,ran);break;
                case 1:

                    newp1 = new Position(p.x-2,p.y-1-index1);
                    newp2 = new Position(p.x+2,p.y-1-index1);
                    PathHelper2(newp1,newp2,world,ran);break;
            }
        }
    }

    private void PathHelper1(Position newp1, Position newp2,TETile[][] world,Random ran) {
        BuildPath(ran.nextInt(4)+1,newp1,0,ran,world);
        BuildPath(ran.nextInt(4)+1,newp2,1,ran,world);
        if (InBound(newp1.x,newp1.y,world)&&world[newp1.x][newp1.y] == Tileset.FLOOR) world[newp1.x][newp1.y-1]=Tileset.FLOOR;
        if (InBound(newp2.x,newp2.y,world)&&world[newp2.x][newp2.y]==Tileset.FLOOR) world[newp2.x][newp2.y+1]=Tileset.FLOOR;

    }

    private void PathHelper2(Position newp1, Position newp2, TETile[][] world, Random ran) {

        BuildPath(ran.nextInt(7)+1,newp1,2,ran,world);
        BuildPath(ran.nextInt(7)+1,newp2,3,ran,world);
        if (InBound(newp1.x,newp1.y,world)&& world[newp1.x][newp1.y] == Tileset.FLOOR) world[newp1.x+1][newp1.y] = Tileset.FLOOR;
        if (InBound(newp2.x,newp2.y,world)&& world[newp2.x][newp2.y] == Tileset.FLOOR) world[newp2.x-1][newp2.y] = Tileset.FLOOR;
    }

    /* 0: build room */
    private void RandomPR(Random ran,TETile[][] world,Position p,int num,int direction) {
//        int decision = ran.nextInt(2);
//        if (decision == 0) {
//
//        }
        // p is the edge tile position
        BuildRoomhelper(direction,world,p,ran);
//        else switch (direction) {
//            case 0:BuildPath(ran.nextInt(7)+1,new Position(p.x,p.y+1),0,ran,world);break;
//            case 1:BuildPath(ran.nextInt(7) +1,new Position(p.x,p.y-1),1,ran,world);break;
//            case 2:BuildPath(ran.nextInt(7)+1,new Position(p.x-1,p.y),2,ran,world);break;
//            case 3:BuildPath(ran.nextI nt(7)+1,new Position(p.x+1,p.y),3,ran,world);break;
//        }

    }

    /* p is the current p */
    private void BuildRoomhelper(int direction,TETile[][] world, Position p, Random ran) {
        int index1 = ran.nextInt(3)+1;
        int index2 = ran.nextInt(3)+1;
        int width,height;
        switch (direction) {     //not open the wall
            case 0:
              BuildRoom(index1+index2-1,ran.nextInt(5)+1,world,new Position(p.x-index1,p.y+1),ran);
              if (InBound(p.x,p.y+1,world) && world[p.x][p.y+1] == Tileset.WALL) {
                  world[p.x][p.y] = Tileset.FLOOR;
                  world[p.x][p.y+1] = Tileset.FLOOR;
              }
              break;
            case 1:
                height = ran.nextInt(5)+1;
              BuildRoom(index1+index2-1,height,world,new Position(p.x-index1,p.y-2-height),ran);
            if (InBound(p.x,p.y-1,world) && world[p.x][p.y-1] == Tileset.WALL) {
                world[p.x][p.y] = Tileset.FLOOR;
                world[p.x][p.y-1] = Tileset.FLOOR;
            }break;
            case 2:
                width = ran.nextInt(6) +1;
                BuildRoom(width,index1+index2-1,world,new Position(p.x-2-width,p.y-index2),ran);
                if (InBound(p.x-1,p.y,world) &&world[p.x-1][p.y] == Tileset.WALL){
                    world[p.x][p.y] = Tileset.FLOOR;
                    world[p.x-1][p.y] = Tileset.FLOOR;
                }break;
            case 3:
                width = ran.nextInt(6)+1;
                BuildRoom(width,index1+index2-1,world,new Position(p.x+1,p.y-index2),ran);
                if (InBound(p.x+1,p.y,world) && world[p.x+1][p.y]==Tileset.WALL) {
                    world[p.x+1][p.y] = Tileset.FLOOR;
                    world[p.x][p.y] = Tileset.FLOOR;
                }break;
        }
    }


    /* direction is restricted to 0 and 1 */
    private boolean ScanUD(Position p, int left, int right, int length, int direction, TETile[][] world) {
        if (direction == 1) {
            for (int i = p.x - left; i <= p.x + right; i++) {
                for (int j = p.y; j >= p.y - length + 1; j--) {
                    if (!ValidTile(i, j, world))
                        return false;
                }
            }
            return true;
        }
        else {
            for (int i = p.x - left; i <= p.x + right; i++) {
                for (int j = p.y; j <= p.y + length - 1; j++) {
                    if (!ValidTile(i, j, world))
                        return false;
                }
            }
            return true;
        }

    }

    /* direction is restricted to 2 and 3*/
    private boolean ScanWE(Position p, int up, int down, int length, int direction, TETile[][] world) {
        if (direction == 2) {
            for (int i = p.x;i>=p.x-length+1;i--) {
                for (int j = p.y-down;j<=p.y+up;j++) {
                    if (!ValidTile(i,j,world))
                        return false;
                }
            }
            return true;
        }
        else {
            for (int i = p.x;i<=p.x+length-1;i++) {
                for (int j = p.y-down;j<=p.y+up;j++) {
                    if (!ValidTile(i,j,world))
                        return false;
                }
            }
            return true;
        }
    }

    /* in the bound and equals to nothing means valid*/
    private boolean ValidTile(int x, int y, TETile[][] world) {
        if (x < 0 || x > world.length - 1 || y > world[0].length - 1 || y < 0)
            return false;
        if (world[x][y] != Tileset.NOTHING)
            return false;
        return true;
    }

    private boolean InBound(int x,int y, TETile[][] world) {
        if (x < 0 || x > world.length - 1 || y > world[0].length - 1 || y < 0)
            return false;
        return true;
    }

    public static void main(String[] args) {
        Engine test = new Engine();
//        TETile[][] world = new TETile[80][30];
//        test.ter.initialize(80,30);
//        Random ran = new Random(12345);

//        Random ran = new Random(2312388);
//        for (int i = 0;i<=79;i++) {
//            for (int j = 0;j<=29;j++)
//                world[i][j] = Tileset.NOTHING;
//        }
//        test.BuildRoom(6,3,world,new Position(0,0),new Random(123));
//        test.BuildPath(5,new Position(40,15),1,new Random(1322328),world);
//        test.ter.renderFrame(world);
//        test.Begin(ran,new Position(40,15),world);
//        test.ter.renderFrame(world);
//        test.ter.renderFrame(test.interactWithInputString("n22s"));
        test.interactWithKeyboard();


    }
}
