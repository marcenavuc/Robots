package serialization;

import java.io.Serializable;

public class Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public int xPosition;
    public int yPosition;

    public int width;
    public int height;

    public boolean isMinimized;
    public boolean isMaximized;

    public String locale;

    public Info() {}
}
