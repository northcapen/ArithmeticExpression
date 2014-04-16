package av.expr;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 4/14/14.
 */
public class REPLConsole {

    private JFrame frame;
    private ExpResolver expResolver = new ExpResolver();
    private Map<Main.Var, Main.Exp> context = new HashMap<>();

    public static final String SIMPLIFY = "Simplify";
    public static final String EVALUATE = "Evaluate";
    private SingleEditCommand singleEditCommand;


    public static final String GREETING = System.lineSeparator() + ">";

    private void init() {
        frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setLayout(new BorderLayout());


        final JComboBox<String> optionPane = new JComboBox<>();
        optionPane.addItem(SIMPLIFY);
        optionPane.addItem(EVALUATE);
        frame.add(optionPane, "North");

        JEditorPane textArea = new JEditorPane();
        AbstractDocument document = (AbstractDocument) textArea.getDocument();
        document.setDocumentFilter(new Filter());
        textArea.setText("Welcome to REPL Console! " + System.lineSeparator() + ">");
        textArea.setEditable(true);
        frame.add(textArea, "Center");

        textArea.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke("ENTER"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JEditorPane source = (JEditorPane) e.getSource();
                Document document = source.getDocument();
                try {
                    String text = document.getText(0, document.getLength());
                    String userInput = text.substring(lastLineIndex(document) + GREETING.length());

                    String resolvedExpression;
                    try {
                        if(simplifyMode()) {
                            resolvedExpression = expResolver.simplifyStatement(userInput, context);
                        } else {
                            resolvedExpression = expResolver.evaluateStatement(userInput, context);
                        }
                    } catch (RuntimeException e1) {
                        resolvedExpression = e1.getMessage();
                    }

                    document.insertString(endOffset(document), System.lineSeparator() + resolvedExpression, null);
                    document.insertString(endOffset(document), GREETING, null);
                    source.setCaretPosition(endOffset(document));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            private int endOffset(Document document) {
                return document.getEndPosition().getOffset() - 1;
            }

            public boolean simplifyMode() {
                return SIMPLIFY.equals(optionPane.getSelectedItem());
            }
        });


        textArea.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    singleEditCommand.unexecute();
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });
            frame.setVisible(true);
            frame.setSize(500,300);
        }


    public static void main(String[] args) {
        REPLConsole replConsole = new REPLConsole();
        replConsole.init();
    }


    private class Filter extends DocumentFilter {


        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (cursorOnLastLine(offset, fb)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
            if (cursorOnLastLine(offset, fb)) {
                super.remove(fb, offset, length);
            }
        }

        public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)
                throws BadLocationException {
            if (cursorOnLastLine(offset, fb)) {
                singleEditCommand = new SingleEditCommand(fb.getDocument(), text, offset);
                singleEditCommand.execute();
            }
        }

    }

    private static boolean cursorOnLastLine(int offset, DocumentFilter.FilterBypass fb) {
        return cursorOnLastLine(offset, fb.getDocument());
    }

    private static boolean cursorOnLastLine(int offset, Document document) {
        int lastLineIndex = 0;
        try {
            lastLineIndex = lastLineIndex(document);
        } catch (BadLocationException e) {
            return false;
        }
        return offset > lastLineIndex;
    }

    private static int lastLineIndex(Document document) throws BadLocationException {
        return document.getText(0, document.getLength()).lastIndexOf(System.lineSeparator());
    }

    interface Command {
        public void execute() throws BadLocationException;
        public void unexecute() throws BadLocationException;

    }

    static class SingleEditCommand implements Command {
      private Document document;
      private String text;
      private int offset;
      boolean wasundo;

        SingleEditCommand(Document document, String text, int offset) {
            this.document = document;
            this.text = text;
            this.offset = offset;
        }

        @Override
        public void execute() throws BadLocationException {
            document.insertString(offset, text, null);
        }

        public void unexecute() throws BadLocationException {
            if(!wasundo) {
                document.remove(offset, text.length());
                wasundo = true;
            }
        }
    }

}
