package byow.InputDemo;


import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInputSource implements InputSource {


    public char getNextKey() {
        while (true) {

            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toLowerCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}
