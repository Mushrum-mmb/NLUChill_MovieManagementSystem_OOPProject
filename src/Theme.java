

import java.awt.Color;
import java.awt.Font;

public class Theme {
	public static final Color BG_DARK     = new Color(10, 12, 20);
    public static final Color BG_CARD     = new Color(18, 22, 36);
    public static final Color BG_SIDEBAR  = new Color(13, 16, 28);
    public static final Color BG_INPUT    = new Color(24, 29, 48);
    public static final Color BG_HOVER    = new Color(30, 38, 65);

    public static final Color ACCENT      = new Color(99, 102, 241);
    public static final Color ACCENT_DARK = new Color(67, 56, 202);
    public static final Color ACCENT_GLOW = new Color(99, 102, 241, 60);

    public static final Color TEXT_PRIMARY   = new Color(241, 245, 249);
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    public static final Color TEXT_MUTED     = new Color(71, 85, 105);

    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color ERROR   = new Color(239, 68, 68);
    public static final Color WARNING = new Color(234, 179, 8);
    public static final Color VIP     = new Color(251, 191, 36);

    public static final Color BORDER = new Color(30, 41, 59);

    public static Font fontBold(int size)   { return new Font("SansSerif", Font.BOLD,   size); }
    public static Font fontPlain(int size)  { return new Font("SansSerif", Font.PLAIN,  size); }
    public static Font fontItalic(int size) { return new Font("SansSerif", Font.ITALIC, size); }
}

