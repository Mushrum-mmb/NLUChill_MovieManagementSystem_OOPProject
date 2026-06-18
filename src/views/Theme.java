package views;


import java.awt.Color;
import java.awt.Font;

public class Theme {
	public static final Color BG_DARK     = new Color(0, 0, 0);
    public static final Color BG_CARD     = new Color(62, 62, 62);
    public static final Color BG_SIDEBAR  = new Color(32, 32, 32);
    public static final Color BG_INPUT    = new Color(43, 43, 43);
    public static final Color BG_HOVER    = new Color(30, 38, 65);

    public static final Color ACCENT      = new Color(250, 250, 255);
    public static final Color ACCENT_DARK = new Color(244, 244, 244);
    public static final Color ACCENT_GLOW = new Color(255, 255, 255, 60);

    public static final Color TEXT_PRIMARY   = new Color(241, 245, 249);
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color TEXT_MUTED     = new Color(71, 85, 105);

    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color ERROR   = new Color(239, 68, 68);
    public static final Color WARNING = new Color(234, 179, 8);
    public static final Color VIP     = new Color(251, 191, 36);

    public static final Color BORDER = new Color(55, 55, 55);

    public static Font fontBold(int size)   {
    	return new Font("SansSerif", Font.BOLD,   size); 
    }
    public static Font fontPlain(int size)  {
    	return new Font("SansSerif", Font.PLAIN,  size); 
    }
    public static Font fontItalic(int size) {
    	return new Font("SansSerif", Font.ITALIC, size); 
    }
}

