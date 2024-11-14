package com.sqs.util;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;


public class FontUtil
{
    private static Font font;
    private static Font fontBold;

    static
    {
        

        if (font == null)
        {
            font = loadFont("/fonts/PingFang-Regular.ttf");
        }
        if (fontBold == null)
        {
            fontBold = loadFont("/fonts/PingFang-Bold-2.ttf");
        }
    }

    public static Font loadFont(String path)
    {
        
        try (InputStream is = FontUtil.class.getResourceAsStream(path))
        {
            return  Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Font getDefaultFont()
    {
        return getDefaultFont(14, Font.PLAIN);
    }

    public static Font getDefaultFont(int size)
    {
        return getDefaultFont(size, Font.PLAIN);
    }

    public static Font getDefaultFont(int size, int style)
    {
        if (style == Font.BOLD)
        {
            return fontBold.deriveFont(Font.PLAIN, size);
        }
        return font.deriveFont(style, size);
    }
}
