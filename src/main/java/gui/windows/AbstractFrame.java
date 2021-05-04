package gui.windows;

import javax.swing.*;
import java.util.Locale;

public abstract class AbstractFrame extends JInternalFrame {
    public AbstractFrame(String title, boolean resizable, boolean closable,
                         boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
    }
    public abstract void changeLocale(Locale locale);
}