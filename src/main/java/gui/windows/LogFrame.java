package gui.windows;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;

public class LogFrame extends JInternalFrame implements LogChangeListener {
    private static final long serialVersionUID = 3L;
    public final transient TextArea m_logContent;
    private final transient LogWindowSource m_logSource;

    public LogFrame(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
            content.append(entry.getMessage()).append("\n");

        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    public void exit() {
        m_logSource.unregisterListener(this);
    }
}
