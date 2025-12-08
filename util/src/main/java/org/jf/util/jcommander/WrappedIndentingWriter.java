package org.jf.util;

import java.io.IOException;
import java.io.Writer;

public class WrappedIndentingWriter extends Writer {
    private final Writer out;
    private int currentIndent = 0;
    private boolean atStartOfLine = true;

    public WrappedIndentingWriter(Writer out, int maxWidth, int indentWidth) {
        this.out = out;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            write(cbuf[off + i]);
        }
    }

    @Override
    public void write(int c) throws IOException {
        if (c == '\n') {
            out.write(c);
            atStartOfLine = true;
        } else {
            if (atStartOfLine) {
                for (int i = 0; i < currentIndent; i++) {
                    out.write(' ');
                }
                atStartOfLine = false;
            }
            out.write(c);
        }
    }

    public void indent(int spaces) {
        currentIndent += spaces;
    }

    public void deindent(int spaces) {
        currentIndent = Math.max(0, currentIndent - spaces);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}